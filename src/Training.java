import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*; 
import java.io.File;
public class Training 
{
	public static void main(String args[]) throws Exception
	{
		long sTime = System.currentTimeMillis();

		// creates flow objects from the files of directory present in args[0]
		ArrayList<Flow> flows = createFlows(args[0]);
		// System.out.println("Number of flows in pcap files: "+flows.size());

		// u and v to decide subflow size
		int u=1, v=1;

		// make subflows using flows and packet pair info
		createSubflows(flows, u, v);
		
		// label the subflows with the subprotocols 
		labelSubprotocols(flows);

		long eTime = System.currentTimeMillis();

		long timeTaken = eTime-sTime;
		System.out.println("Time taken for training: "+timeTaken+" seconds");

		// create a CSV file dataset using th subflows
		createDataset(flows);
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

							// int id=0; // Packet ID 
							
							/* Converting each pcap file line into a transaction */
							while(scan.hasNextLine())
							{
								String mline = scan.nextLine();
								String[] W = mline.split(",");

								// ++id;
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
		// ArrayList<SubFlow> subflows = new ArrayList<SubFlow>();
		for(Flow f : flows)
		{
			System.out.println(f.id);
			// for each packet pair belonging to the flow f 
			for(PacketPair p : f.packetPairs)
			{
				
				// make a valid subflow range using values u and v 

				int i = f.features.indexOf(p.pair1) - u;
				int j = f.features.indexOf(p.pair2) + v;

				System.out.println(p.pid+": "+i+" "+j);
				
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

	static void printSubFlows(ArrayList<SubFlow> subflows)
	{
		int x = 0, y= 0;
		for(SubFlow sf : subflows)
		{
			// System.out.println("Flow: "+sf.flowid+"\n*******");
			// for(ArrayList<Integer> sflist : sf.subflows)
			// {
			// 	System.out.println(sflist);
			// }
			if(sf.subprotocol == "transport" || sf.subprotocol == "transition point 1")
				++x;
			if(sf.subprotocol == "user auth" || sf.subprotocol == "transition point 2")
				++y;
			// System.out.println("flow: "+sf.flowid+" packet pair: "+sf.ppid+" subprotocol: "+sf.subprotocol);
		}
		System.out.println("x_alpha print: "+x+" x_beta print: "+y);
	}

	// Find out the labels of the subprotocols for the subflows
	static void labelSubprotocols(ArrayList<Flow> flows)
	{
		// List of subflows for X alpha and X beta
		ArrayList<SubFlow> x_alpha = new ArrayList<SubFlow>();
		ArrayList<SubFlow> x_beta = new ArrayList<SubFlow>();

		// Packet pairs denoting transition point 1 and 2
		PacketPair trans_point1 = new PacketPair();
		PacketPair trans_point2 = new PacketPair();

		// Iterate through the flows
		for(Flow f : flows)
		{
			System.out.println("\n** Flow id: "+f.id);
			int l=0;// count the number of login attempts

			int check_tp2 = 0; // flag for checking presence of transition point 2

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
										System.out.println("ID of transition point 1: "+f.packetPairs.get(j).pair1.id);
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
			System.out.println("Number of login attempts total: "+logins);

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
						// break;
						for(PacketPair pp : f.packetPairs)
						{
							// System.out.println(pp.pair1.id+" > ");
							if(pp.pair1.id >= p.id)
							{
								System.out.println("ID of transition point 2: "+pp.pair1.id);
								trans_point2 = pp;
								break;
							}
						}

						break;

					}
				}
				
			}
			System.out.println("No. of packet pairs: "+f.packetPairs.size());
			
			// Label the subflow of that packet pair as transition point 1
			for(PacketPair pp : f.packetPairs)
			{
				if(trans_point1.pid == pp.pid && pp.sf.subprotocol == "")
				{
					pp.sf.subprotocol = "transition point 1";
					x_alpha.add(pp.sf);
					// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
				}

				if(pp.pid < trans_point1.pid && pp.sf.subprotocol == "")
				{
					pp.sf.subprotocol = "transport";
					x_alpha.add(pp.sf);
					// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
				}

				if(pp.pid > trans_point1.pid && pp.sf.subprotocol.equals(""))
				{
					if(check_tp2 == 1 && trans_point2.pid == pp.pid)
					{
						pp.sf.subprotocol = "transition point 2";
						x_beta.add(pp.sf);
						// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
					}
					else
					{
						pp.sf.subprotocol = "user auth";
						x_beta.add(pp.sf);
						// System.out.println("packet pair: "+pp.pid+" "+pp.sf.subprotocol);
					}
				}

				// System.out.println(sf.ppid+" "+sf.subprotocol);

			} 

		}

		System.out.println("Alpha: "+x_alpha.size()+" Beta: "+x_beta.size());

	}

	// Create a CSV file having the subflow information
	static void createDataset(ArrayList<Flow> flows)
	{
		// name of the dataset file
		String filePath = "../subflow_dataset.csv";
		Writer bufferedWriter = null;
		try {
			
			//Creating a file
			Writer fileWriter = new FileWriter(filePath);
			bufferedWriter = new BufferedWriter(fileWriter);

			for(Flow f: flows)
			{
				for(PacketPair p: f.packetPairs)
				{
					if(p.sf.subflow.size() == 0)
						continue;
					bufferedWriter.write(f.id+","+p.pid+",");
					for(int i : p.sf.subflow)
					{
						bufferedWriter.write(i+",");
					}
					bufferedWriter.write(p.sf.subprotocol);
					bufferedWriter.write(System.getProperty("line.separator"));
				}
			}
			
		} catch (IOException e) {
			System.out.println("Problem occurs when creating file " + filePath);
			e.printStackTrace();
		} finally {
			
			//Closing the file
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					System.out.println("Problem occurs when closing file !");
					e.printStackTrace();
				}
			}
		}

	}

}


