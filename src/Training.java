import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*; 
import java.io.File;
public class Training 
{
	public static void main(String args[]) throws Exception
	{
		// creates flow objects from the files of directory present in args[0]
		ArrayList<Flow> flows = createFlows(args[0]);
		System.out.println("Number of flows in pcap files: "+flows.size());

		// u and v to decide subflow size
		int u=1, v=1;

		// make subflows using flows and packet pair info
		ArrayList<SubFlow> subflows = createSubflows(flows, u, v);
		// printSubFlows(subflows);
		System.out.println("Number of sub flows: "+subflows.size());

		// label the subflows with the subprotocols 
		labelSubprotocols(flows, subflows);
		// printSubFlows(subflows);

		// create a CSV file dataset using th subflows
		createDataset(subflows);
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
	static void labelSubprotocols(ArrayList<Flow> flows, ArrayList<SubFlow> subflows)
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
			int l=0;// count the number of login attempts

			// Go through each line in the logs of the flow
			for(String line : f.logs)
			{
				// if there is a login attempt, only then check for trans point 1
				if(line.contains("login attempt"))
				{
					if(l==0)
					{
						for(int i=0; i<f.packetPairs.size(); ++i)
						{
							if(f.packetPairs.get(i).pair1.trans_point == 1)
							{
								trans_point1 = f.packetPairs.get(i+1);
								break;
							}
						}
					}
					
					++l;
				}
			}

			// Find transition point 2 
			// the encrypted packet right after the last login attempt
			int logins = l;
			int j = 0;
			for(Packet p : f.features)
			{
				if(p.login == 1)
					logins--;

				if(logins == 0)
				{
					j = 1;
				}

				if(j == 1 && p.incoming == 1)
				{
					p.trans_point = 2;
					break;
				}
			}
			for(PacketPair pp : f.packetPairs)
			{
				if(pp.pair1.trans_point == 2 || pp.pair2.trans_point == 2)
				{
					trans_point2 = pp;
					break;
				}
			}

			// Label the subflow of that packet pair as transition point 1
			for(SubFlow sf : subflows)
			{
				if(f.id == sf.flowid && trans_point1.pid == sf.ppid && sf.subprotocol == "none")
				{
					sf.subprotocol = "transition point 1";
					x_alpha.add(sf);
					// System.out.println("flow: "+sf.flowid+" packet pair: "+sf.ppid);
				}
				if(f.id == sf.flowid && sf.ppid < trans_point1.pid)
				{
					sf.subprotocol = "transport";
					x_alpha.add(sf);
				}

				if(f.id == sf.flowid && trans_point2.pid == sf.ppid && sf.subprotocol == "none")
				{
					// System.out.println("sp: "+sf.subprotocol);
					// System.out.println("beta");
					sf.subprotocol = "transition point 2";
					x_beta.add(sf);
					// break;
					// System.out.println("flow: "+sf.flowid+" packet pair: "+sf.ppid);
				}

				if(f.id == sf.flowid && sf.ppid > trans_point1.pid && sf.subprotocol != "transition point 2")
				{
					// System.out.println("sp: "+sf.subprotocol);
					// System.out.println("betaaaa");
					sf.subprotocol = "user auth";
					x_beta.add(sf);
				}

			} 
		}

		System.out.println("Alpha: "+x_alpha.size()+" Beta: "+x_beta.size());

	}

	// Create a CSV file having the subflow information
	static void createDataset(ArrayList<SubFlow> subflows)
	{
		// name of the dataset file
		String filePath = "../subflow_dataset.csv";
		Writer bufferedWriter = null;
		try {
			
			//Creating a file
			Writer fileWriter = new FileWriter(filePath);
			bufferedWriter = new BufferedWriter(fileWriter);

			for(SubFlow sf : subflows)
			{
				bufferedWriter.write(sf.flowid+","+sf.ppid+",");
				for(int i : sf.subflow)
				{
					bufferedWriter.write(i+",");
				}
				bufferedWriter.write(sf.subprotocol);
				bufferedWriter.write(System.getProperty("line.separator"));
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

