import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.io.File;

class Testing 
{
	
	// threshold values used by clustering model
	static int th_alpha = 100;
	static int th_beta = 50;

	public static void main(String args[]) throws Exception
	{
		long sTime = System.currentTimeMillis();

		// creates flow objects from the files of directory present in args[0]
		ArrayList<Flow> test_flows = createFlows(args[0]);

		// u and v to decide subflow size
		int u=1, v=1;

		// make subflows using flows and packet pair info
		createSubflows(test_flows, u, v);
		
		// file containing the results of the clustering
		String model_file = "../model.txt";

		// identify the transition points for the test flows
		identifySubprotocols(model_file, test_flows);

		// true labels for transition points
		labelSubprotocols(test_flows);

		// evaluate the accuracy of the transition point indentification
		// evaluateSubprotocolAccuracy(test_flows);

		// label the test flows with the actual ground truth
		labelFlows(test_flows);

		float th_time = 1.5f; // threshold time for typing password
		
		// prediction of the label for the flow using criteria
		detectionFunction(test_flows, th_time);

		long eTime = System.currentTimeMillis();
		long timeTaken = eTime-sTime;
		System.out.println("\nTime taken for testing: "+timeTaken+" seconds");

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

		// System.out.println(log_file.size());
		
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
							// System.out.println(child.getName());

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

							/* Converting each pcap file line into a transaction */
							while(scan.hasNextLine())
							{
								String mline = scan.nextLine();
								String[] W = mline.split(",");

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

								Packet temp = new Packet(frame, incoming, sourceIP2,destIP2,sourcePort2,destPort2,timestamp,size,trans_point, login);

								data.add(temp);
							}

							flows.add(new Flow(w, data, loglines));

						}
		    		}

		    	}
	
			}
			
		}

		System.out.println("\n\nNo. of flows in log files: "+w);
		return flows;
			
	}

	// Method to create subflows from the flows
	static void createSubflows(ArrayList<Flow> flows, int u, int v)
	{
		// create subflows for packet pairs in each flow and create their flow features
		for(Flow f : flows)
		{
			// for each packet pair belonging to the flow f 
			for(PacketPair p : f.packetPairs)
			{
				// make a valid subflow range using values u and v 

				int i = f.features.indexOf(p.pair1) - u;
				int j = f.features.indexOf(p.pair2) + v;

				// System.out.println(p.pid+": "+i+" "+j);
				
				if(i<=0 || j>f.features.size()) continue;

				// create the subflows themselves 
				for(int k=i; k<=j; ++k)
				{
					int n = f.features.get(k-1).size;
					if(f.features.get(k-1).incoming == 0)
						n = 0-n;
					p.sf.subflow.add(n);
				}

			}
		}
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
	static void identifySubprotocols(String model_file, ArrayList<Flow> test_flows) throws Exception
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
			PacketPair pp1 = new PacketPair();
			// sf2 for transition point 2
			PacketPair pp2 = new PacketPair(); 

			// identify transition point 1
			for(PacketPair pp : f.packetPairs)
			{
				if(pp.sf.subflow.size() != 0)
				{
					float min_distance = 9999;

					// iterater through each cluster centroids in alpha
					for(ArrayList<Float> cluster : alpha_centroids)
					{
						// find the distance between the current cluster centroid and current test subflow
						float d = distance(cluster, pp.sf.subflow);

						// if the distance is minimum, store that subflow
						if(d < min_distance)
						{
							min_distance = d;
							pp1 = pp;
						}
					}

					// if the minimum distance is lesser than the alpha threshold, subflow contains transition point 1
					if(min_distance < th_alpha)
					{
						pp1.sf.label = "transition point 1";
						break;
					}
				}
			}

			// identify transition point 2
			for(PacketPair pp : f.packetPairs)
			{
				if(pp.sf.subflow.size() != 0)
				{
					float min_distance = 9999;

					// iterater through each cluster centroids in beta
					for(ArrayList<Float> cluster : beta_centroids)
					{
						// find the distance between the current cluster centroid and current test subflow
						float d = distance(cluster, pp.sf.subflow);
						
						// if the distance is minimum, store that subflow
						if(d < min_distance)
						{
							min_distance = d;
							pp2 = pp;
						}
					}

					// if the minimum distance is lesser than the beta threshold, subflow contains transition point 2
					if(min_distance < th_beta)
					{
						pp2.sf.label = "transition point 2";
						// System.out.println("found t p 2 :)))))");
						break;
					}
				}
			}
			// System.out.println("For flow "+f.id+" of size "+f.features.size()+": trans pt 1 is "+pp1.pid+" and trans pt 2 is "+pp2.pid);
		}

	}

	// Find out the labels of the subprotocols for the subflows
	static void labelSubprotocols(ArrayList<Flow> flows)
	{
		// Packet pairs denoting transition point 1 and 2
		PacketPair trans_point1 = new PacketPair();
		PacketPair trans_point2 = new PacketPair();

		// Iterate through the flows
		for(Flow f : flows)
		{
			// System.out.println("\n** Flow id: "+f.id);
			int l=0;// count the number of login attempts
			
			int check_tp2 = 0;
			// Go through each line in the logs of the flow
			for(String line : f.logs)
			{
				// if there is a login attempt, only then check for trans point 1
				if(line.contains("login attempt"))
				{
					if(l==0)
					{
						for(int i=0; i<f.features.size(); ++i)
						{
							if(f.features.get(i).trans_point == 1)
							{
								for(int j=0; j<f.packetPairs.size(); ++j)
								{
									if(f.packetPairs.get(j).pair1.id > f.features.get(i).id)
									{
										// System.out.println("ID of transition point 1: "+f.packetPairs.get(j).pair1.id);
										trans_point1 = f.packetPairs.get(j);
										break;
									}
								}

								break;
							}
						}
					}
					
					++l;
				}
				if(line.contains("root authenticated with password"))
					check_tp2=1;
			}

			// Find transition point 2 
			// the encrypted packet right after the last login attempt
			int logins = l;
			int j = 0;
			// System.out.println("Number of login attempts total: "+logins);

			if(check_tp2==1)
			{
				for(Packet p : f.features)
				{
					if(p.login == 1)
						logins--;

					if(logins == 0)
					{
						j = 1;
						continue;
					}

					if(j == 1 && p.login == 1)
					{
						p.trans_point = 2;
						for(PacketPair pp : f.packetPairs)
						{
							
							if(pp.pair1.id >= p.id)
							{
								// System.out.println("ID of transition point 2: "+pp.pair1.id);
								trans_point2 = pp;
								break;
							}
						}

						break;

					}
				}
				
			}
			// System.out.println("No. of packet pairs: "+f.packetPairs.size());
			
			// Label the subflow of that packet pair as transition point 1
			for(PacketPair pp : f.packetPairs)
			{
				
				if(trans_point1.pid == pp.pid && pp.sf.subprotocol == "")
				{
					pp.sf.subprotocol = "transition point 1";
					
					// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
				}

				if(pp.pid < trans_point1.pid && pp.sf.subprotocol == "")
				{
					pp.sf.subprotocol = "transport";
					
					// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
				}

				if(pp.pid > trans_point1.pid && pp.sf.subprotocol.equals(""))
				{
					if(check_tp2 == 1 && trans_point2.pid == pp.pid)
					{
						pp.sf.subprotocol = "transition point 2";
						
						// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
					}
					else
					{
						pp.sf.subprotocol = "user auth";
						
						// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
					}
				}

				// System.out.println(sf.ppid+" "+sf.subprotocol);

			} 
			
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
		int s = 0, u = 0;
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
				++u;
				f.actual = "Unsuccessful SSH Attack";
			}
			else
			{
				++s;
				f.actual = "Successful SSH Attack";
			}

		}
		System.out.println("Success: "+s+" unsuccess: "+u);
	}

	// Use the crietria to do the detection 
	static void detectionFunction(ArrayList<Flow> test_flows, float th_time)
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
				for(PacketPair pp: f.packetPairs)
				{

					if(pp.sf.label.equals("transition point 2"))
					{
						ppid = pp.pid;
						break;
					}

				}

				if(ppid == -1)
				{
					// System.out.println("****** problem!");
					f.label = "Unsuccessful SSH Attack";
				}

			
				for(PacketPair pp : f.packetPairs)
				{
					if(ppid == pp.pid)
					{
						if(f.features.size() > f.features.indexOf(pp.pair2))
						{
							f.label = "Successful SSH Attack";
						}
						else
						{
							// System.out.println("&&& anothe problem");
							f.label = "Unsuccessful SSH Attack";
						}
					}
				}
			// }
		}
	}

	// // Evaluate the accuracy of the subprotocol identification function
	// static void evaluateSubprotocolAccuracy(ArrayList<Flow> flows)
	// {
	// 	int correct_1 = 0, correct_2=0;
	// 	for(Flow f: flows)
	// 	{
	// 		for(PacketPair pp: f.packetPairs)
	// 		{
	// 			if(pp.sf.subprotocol.equals(pp.sf.label))
	// 			{
	// 				System.out.println("Flow: "+f.id+" id: "+pp.pid+" Actual: "+pp.sf.subprotocol+" Predicted: "+pp.sf.label);
	// 				if(pp.sf.subprotocol.equals("transition point 1"))
	// 					++correct_1;
	// 				else
	// 					++correct_2;
	// 			}
	// 		}
	// 	}
		
	// 	System.out.println(correct_1+" "+correct_2);
	// }

	// Evaluate the accuracy of the flow labelling function
	static void evaluateDetectionAccuracy(ArrayList<Flow> flows)
	{
		int correct = 0; // total number of correct labels
		int tp = 0, tn = 0, fp = 0, fn = 0;
		for(Flow f: flows)
		{
			// System.out.println("Flow: "+f.id+" Actual: "+f.actual+" Predicted: "+f.label);
			if(f.label.equals(f.actual))
				++correct;

			if(f.actual.equals("Successful SSH Attack") && f.label.equals("Successful SSH Attack"))
    			++tp;
    		if(f.actual.equals("Unsuccessful SSH Attack") && f.label.equals("Unsuccessful SSH Attack"))
    			++tn;
    		if(f.actual.equals("Successful SSH Attack") && f.label.equals("Unsuccessful SSH Attack"))
    			++fn;
    		if(f.actual.equals("Unsuccessful SSH Attack") && f.label.equals("Successful SSH Attack"))
    			++fp;
		}

		float accuracy = (float)correct/flows.size()*100;
		System.out.println("\nAccuracy of attack detection: "+accuracy+"%");

		// confusion matrix
    	System.out.println("\nTrue Positive: "+tp+" , False Negative: "+fn);
    	System.out.println("False Positive: "+fp+" , True Negative: "+tn);
	}
}
