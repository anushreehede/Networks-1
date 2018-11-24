import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.io.File;

class Testing 
{
	// 1. Get the value of the centroid for each cluster, store in a list
	// 2. Take new set of pcap files, convert them to text and flow objects
	// 3. Using the corresponding log files, label the flows
	// 4. For each flow, create its subflows
	// 5. Find the subflow/packet pair corresponding to each transition point
	// 6. Predict labels for each flow based on criteria
	// 7. Using the actual labelling and precitions, find accuracy of the model 
	
	// threshold values used by clustering model
	static int th_alpha = 100;
	static int th_beta = 50;

	public static void main(String args[]) throws Exception
	{
		// creates flow objects from the files of directory present in args[0]
		ArrayList<Flow> test_flows = createFlows(args[0]);

		// u and v to decide subflow size
		int u=1, v=1;

		// make subflows using flows and packet pair info
		ArrayList<SubFlow> test_subflows = createSubflows(test_flows, u, v);
		System.out.println("Num of test subflows: "+test_subflows.size());
		
		// file containing the results of the clustering
		String model_file = "../model.txt";

		// identify the transition points for the test flows
		identifySubprotocols(model_file, test_flows, test_subflows);

		// label the test flows with the actual ground truth
		labelFlows(test_flows);

		float th_time = 1.5f; // threshold time for typing password
		
		// prediction of the label for the flow using criteria
		detectionFunction(test_flows, test_subflows, th_time);

		// Printing actuals and labels
		// int suc = 0, unsuc = 0;
		for(Flow p : test_flows)
		{
			System.out.println("Actual: "+p.actual+" Label: "+p.label);
			// if(p.label == "Successful SSH Attack")
			// 	++suc;
			// else if(p.label == "Unsuccessful SSH Attack")
			// 	++unsuc;
			// else
			// 	continue;
		}

		// System.out.println("Successful: "+suc+" Unsuccessful: "+unsuc);

		// evaluate the accuracy of the transition point indentification
		// evaluateSubprotocolAccuracy(test_flows, test_subflows);

		// evaluate accuracy of the labelling of flows
		evaluateDetectionAccuracy(test_flows);
	}

	// Method to get flow information from the logs
	static ArrayList<Flow> createFlows(String dirName) throws Exception
	{
		// ArrayList to hold the flows
		ArrayList<Flow> flows = new ArrayList<Flow>();
		int session_id = -1; // counts the number of flows present in the logs files
		int w = 0; // counts the number of log file flows mapped to the pcap files
		
		// Object to store all the lines in the log file for easier iteration
		ArrayList<String> log_file = new ArrayList<String>();

		// File containing all the kippo logs information
		String directoryPath = "../all_logs";
		File logs = new File(directoryPath);
		Scanner sc = new Scanner(logs);

		// store all the lines in an arraylist object
		while(sc.hasNextLine())
		{
			log_file.add(sc.nextLine());
		}

		System.out.println(log_file.size());
		
		// iterate over each line of the log file 
		for(int k=0; k< log_file.size(); ++k)
		{
			String line = log_file.get(k);
			// System.out.println(line);

			// To check for the start of a new flow
			String checkStr = "New connection: ";
			if(line.contains(checkStr))
			{
				++session_id; // increment the number of flows in log files

				// Get the source and dest IP, source and dest port

				// System.out.println(session_id+":-");	
				int i = line.indexOf(checkStr);
				i += checkStr.length();
				int j = line.indexOf(')');
				String connection = line.substring(i, j+1);
				// System.out.println(connection);

				i = connection.indexOf(" (");
				String s = connection.substring(0, i);
				String d = connection.substring(i+2, connection.length()-1);
				// System.out.println(s+" , "+d);

				j = s.indexOf(":");
				String sourceIP = s.substring(0, j);
				String sourcePort = s.substring(j+1);
				j = d.indexOf(":");
				String destIP = d.substring(0, j);
				String destPort = d.substring(j+1);

				// System.out.println("*.*.*.*.*.*.*\n"+sourceIP+" : "+sourcePort+" , "+destIP+" : "+destPort);

				// Begin iterating through pcap files
				File dir = new File(dirName);
		  		File[] directoryListing = dir.listFiles();
		  		if (directoryListing != null) 
		  		{
		    		for (File child : directoryListing) 
		    		{
		    			// Get the source and dest IP, source and dest port of pcap file
		    			int x = child.getName().indexOf("TCP_");
		    			int y = child.getName().indexOf(".pcap.txt");

		    			String values = child.getName().substring(x+4, y);
		    			String[] V = values.split("_");
		    			V[0] = V[0].replace("-", ".");
		    			V[2] = V[2].replace("-", ".");
		    			// StringBuffer SIP = new StringBuffer(V[0]);
		    			// StringBuffer SP = new StringBuffer(V[1]);
		    			String SIP = V[0];
		    			String SP = V[1];
		    			String DIP = V[2];
		    			String DP = V[3];

		    			// If there is a match between flow found in log file and pcap file
		    			if((SIP.equals(sourceIP) && SP.equals(sourcePort)) || (DIP.equals(sourceIP) && DP.equals(sourcePort)))
						{
							++w; // increment the number of matches of log file and 
							System.out.print(w+" ");
							// System.out.println("x-x-x-x-x-x-x-x-x\n"+sourceIP+" : "+sourcePort+" , "+destIP+" : "+destPort);
							
							// Store all the log file lines of the flow
							ArrayList<String> loglines = new ArrayList<String>();
							loglines.add(line); // add the current line
							int m = k+1; // go to the next line in the file
							String check2 = "connection lost";
							while(m<log_file.size())
							{
								String nline = log_file.get(m);
								// System.out.println(nline);

								// if the current line belongs to the current flow (using the session_id)
								if(nline.contains("HoneyPotTransport,"+Integer.toString(session_id)))
								{
									// if the current line is not the end of the flow
									if(!log_file.get(m).contains(check2))
										loglines.add(nline);
									else
									{
										loglines.add(nline);
										break;
									}
								}

								++m;
							}

							// Create a new flow to store flow info and log info
							Scanner scan = new Scanner(child);
							ArrayList<Packet> data  = new ArrayList<Packet>();

							int id=0; // Packet ID 
							
							/* Converting each pcap file line into a transaction */
							while(scan.hasNextLine())
							{
								String mline = scan.nextLine();
								String[] W = mline.split(",");

								++id;
								int frame = Integer.parseInt(W[0]);
								int incoming;
								if(W[1].equals("True"))
									incoming = 1;
								else
									incoming = 0;

								String sourceIP2 = W[2];
								String destIP2 = W[3];
								String sourcePort2 = W[4];
								String destPort2 = W[5];
								String timestamp = W[6];
								int size = Integer.parseInt(W[7]);
								int trans_point = Integer.parseInt(W[8]);
								int login = Integer.parseInt(W[9]);

								Packet temp = new Packet(id, frame, incoming, sourceIP2,destIP2,sourcePort2,destPort2,timestamp,size,trans_point, login);

								data.add(temp);
							}

							flows.add(new Flow(w, data, loglines));

						}
		    		}
		    	}
	
			}
			
		}

		System.out.println("\nNo. of flows in log files: "+w);
		return flows;
			
	}

	// Method to create subflows from the flows
	static ArrayList<SubFlow> createSubflows(ArrayList<Flow> flows, int u, int v)
	{
		// create subflows for packet pairs in each flow and create their flow features
		ArrayList<SubFlow> subflows = new ArrayList<SubFlow>();
		for(Flow f : flows)
		{
			// for each packet pair belonging to the flow f 
			for(PacketPair p : f.packetPairs)
			{
				// each subflow associated with a flow - use the flow id
				SubFlow sf = new SubFlow(f.id);

				// this subflow is associated with a particular packet pair
				sf.ppid = p.pid;

				// make a valid subflow range using values u and v 
				int i = p.pair1.id - u;
				int j = p.pair2.id + v;
				
				if(i<=0 || j>f.features.size()) continue;

				// create the subflows themselves 
				for(int k=i; k<=j; ++k)
				{
					int n = f.features.get(k-1).size;
					if(f.features.get(k-1).incoming == 0)
						n = 0-n;
					sf.subflow.add(n);
				}
				
				subflows.add(sf);
			}
		}

		return subflows;
	}

	// Method to calculate Euclidean distance between two subflows
	static float distance(ArrayList<Float> u, ArrayList<Integer> v)
	{	
		int sum = 0;
		for(int i=0; i<u.size(); ++i)
		{
			sum += ((u.get(i) - v.get(i)) * (u.get(i) - v.get(i)));
		}
		float dist = (float)Math.sqrt(sum);
		return dist;
	}
	
	// Use the clustering results model to identify the test flow subprotocols
	static void identifySubprotocols(String model_file, ArrayList<Flow> test_flows, ArrayList<SubFlow> test_subflows) throws Exception
	{
		// lists to store alpha and beta subflows from the model
		ArrayList<ArrayList<Float>> alpha_centroids = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Float>> beta_centroids = new ArrayList<ArrayList<Float>>();
		
		// read the model file and store the subflows accordingly 
		File model = new File(model_file);
		Scanner sc = new Scanner(model);
		int fl = 0;	// flag to store new cluster in the lists	
		
		while(sc.hasNextLine())
		{	
			String line = sc.nextLine();
			if(line.contains("---"))
			{
				fl=1;
				continue;
			}

			ArrayList<Float> values = new ArrayList<Float>();
			String[] V = line.split(",");
			for(String v : V)
			{
				values.add(Float.parseFloat(v));
			}

			if(fl == 0)
				alpha_centroids.add(values);
			else
				beta_centroids.add(values);
		}

		// identify the subprotocol for the test flows using the alpha and beta clusters  
		for(Flow f : test_flows)
		{
			// sf1 for transition point 1
			SubFlow sf1 = new SubFlow();
			// sf2 for transition point 2
			SubFlow sf2 = new SubFlow(); 

			// identify transition point 1
			for(SubFlow sf : test_subflows)
			{
				if(f.id == sf.flowid)
				{
					float min_distance = 9999;

					// iterater through each cluster centroids in alpha
					for(ArrayList<Float> cluster : alpha_centroids)
					{
						// find the distance between the current cluster centroid and current test subflow
						float d = distance(cluster, sf.subflow);

						// if the distance is minimum, store that subflow
						if(d < min_distance)
						{
							min_distance = d;
							sf1 = sf;
						}
					}

					// if the minimum distance is lesser than the alpha threshold, subflow contains transition point 1
					if(min_distance < th_alpha)
					{
						sf1.subprotocol = "trans point 1";
						break;
					}
				}
			}

			// identify transition point 2
			for(SubFlow sf : test_subflows)
			{
				if(f.id == sf.flowid)
				{
					float min_distance = 9999;

					// iterater through each cluster centroids in beta
					for(ArrayList<Float> cluster : beta_centroids)
					{
						// find the distance between the current cluster centroid and current test subflow
						float d = distance(cluster, sf.subflow);
						
						// if the distance is minimum, store that subflow
						if(d < min_distance)
						{
							min_distance = d;
							sf2 = sf;
						}
					}

					// if the minimum distance is lesser than the beta threshold, subflow contains transition point 2
					if(min_distance < th_beta)
					{
						sf2.subprotocol = "trans point 2";
						// System.out.println("found t p 2 :)))))");
						break;
					}
				}
			}
			// System.out.print("For subflow "+f.id+": trans pt 1 is "+sf1.ppid+" and trans pt 2 is "+sf2.ppid);
		}

	}

	/* Omitting this function because it is out of scope of our dataset */

	// Calculate the inter-arrival time
	// float inter_arrival_time(Flow f)
	// {
	// 	long t = 0;
	// 	long c = 0;
	// 	int n = 0;
	// 	for(int i=0; i<f.features.size(); ++i)
	// 	{
	// 		if(f.features.get(i).login == 1)
	// 		{
	// 			long milli = 0;
	// 			try 
	// 			{
	// 			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// 			    Date parsedDate = dateFormat.parse(f.features.get(i).timestamp);
	// 			    milli = parsedDate.getTime();
	// 			    // System.out.println(ts+"\n-------------");

	// 			} catch(Exception e) { //this generic but you can control another types of exception
	// 			    // look the origin of excption 
	// 			    System.out.println(e);
	// 			}

	// 			++n;
	// 			c += (milli-t);
	// 			t = milli;
	// 		}
	// 	}

	// 	float avg = (float)c/n;
	// 	return avg;
	// }

	// Label the flows with the actual ground truth
	static void labelFlows(ArrayList<Flow> flows)
	{
		// iterate through the flows
		for(Flow f : flows)
		{
			int x = 0; // flag
			for(String line: f.logs)
			{
				// check if authentication is successful
				if(line.contains("root authenticated with password"))
				{
					// because we know that dataset contains only malicious attacks
					x = 1;
					// System.out.println("#### found success!");
					
					/* Otherwise check for user logins */

					// int logins = 0;
					// for(Packet p : f.features)
					// {
					// 	if(p.login == 1)
					// 		++logins;
					// }

					// // float t = inter_arrival_time(f);
					// System.out.println(logins);
					break;
				}
			}

			// assign labels according to flag value
			if(x == 0)
			{
				f.actual = "Unsuccessful SSH Attack";
			}
			else
			{
				f.actual = "Successful SSH Attack";
			}

		}
	}

	// Use the crietria to do the detection 
	static void detectionFunction(ArrayList<Flow> test_flows, ArrayList<SubFlow> test_subflows, float th_time)
	{
		for(Flow f : test_flows)
		{
			/* For SSH connection detection */

			// if(inter_arrival_time(f) >= th_time)
			// 	f.label = "SSH connection";

			// else
			// {

			// check for existence of transition point 2
				int ppid = -1;
				for(SubFlow sf : test_subflows)
				{
					if(f.id == sf.flowid)
					{
						if(sf.subprotocol.equals("trans point 2"))
						{
							ppid = sf.ppid;
						}
					}
				}

				if(ppid == -1)
				{
					// System.out.println("****** problem!");
					f.label = "Unsuccessful SSH Attack";
				}

				// if packets exist beyond transition point 2, successful else unsuccessful
				for(PacketPair pp : f.packetPairs)
				{
					if(ppid == pp.pid)
					{
						if(f.features.size() > pp.pair2.id)
						{
							f.label = "Successful SSH Attack";
						}
						else
						{
							f.label = "Unsuccessful SSH Attack";
						}
					}
				}
			// }
		}
	}

	// Evaluate the accuracy of the subprotocol identification function
	static void evaluateSubprotocolAccuracy(ArrayList<Flow> flows, ArrayList<SubFlow> subflows)
	{
		int sum = 0;
		for(Flow f : flows)
		{
			int a = 0;
			int b = 0;

			// if(f.)
		}
	}

	// Evaluate the accuracy of the flow labelling function
	static void evaluateDetectionAccuracy(ArrayList<Flow> flows)
	{
		int correct = 0; // total number of correct labels
		for(Flow f: flows)
		{
			if(f.label.equals(f.actual))
				++correct;
		}

		float accuracy = (float)correct/flows.size()*100;
		System.out.println("Accuracy of attack detection: "+accuracy+"%");
	}
}
