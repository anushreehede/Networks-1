/* Old clustering algorithm */

// // method 2
	// float[][] proximityMatrix(ArrayList<Cluster> clusterList, int flowSize)
	// {
	// 	float[][] proximity = new float[flowSize][flowSize];
	// 	int i=0;
	// 	for(Cluster c_outer : clusterList)
	// 	{
	// 		int j=0;
	// 		for(Cluster c_inner : clusterList)
	// 		{
	// 			proximity[i][j] = euclideanDistance(c_outer.leaves, c_inner.leaves);
	// 			++j;
	// 		}
	// 		++i;
	// 	}

	// 	for(i=0; i<flowSize; ++i)
	// 	{
	// 		for(int j=0; j<flowSize; ++j)
	// 		{
	// 			System.out.print(proximity[i][j]+" \t ");
	// 		}
	// 		System.out.print("\n");
	// 	}

	// 	return proximity;

	// 	// using proximity matrix begin clustering. write pseudocode. 
	// }

	// float euclideanDistance(ArrayList<Integer> u, ArrayList<Integer> v)
	// {	
	// 	int sum = 0;
	// 	for(int i=0; i<u.size(); ++i)
	// 	{
	// 		sum += ((u.get(i) - v.get(i)) * (u.get(i) - v.get(i)));
	// 	}
	// 	float dist = (float)Math.sqrt(sum);
	// 	return dist;
	// }

	// void clustering(SubFlow sf)
	// {
	// 	ArrayList<ArrayList<Cluster>> clusterList = new ArrayList<ArrayList<Cluster>>();
	// 	ArrayList<Cluster> currentClusterList = new ArrayList<Cluster>();
	// 	// function to convert the subflows sf to clusters 
	// 	for(ArrayList<Integer> i : sf.subflows)
	// 	{
	// 		ArrayList<Cluster> temp = new ArrayList<Cluster>();
	// 		temp.add(new Cluster(i));
	// 		clusterList.add(temp);
	// 		currentClusterList.add(new Cluster(i));
	// 	}

	// 	int k = currentClusterList.size();

	// 	while(k > 1) // until the number of clusters becomes 1.
	// 	{
	// 		System.out.print("\n");
	// 		for(Cluster c : currentClusterList)
	// 			c.printCluster();

	// 		float[][] proximity = proximityMatrix(currentClusterList,k);

	// 		ArrayList<Cluster> clusterPair = minimumProximity(proximity, currentClusterList);

	// 		clusterPair.get(0).printCluster();
	// 		clusterPair.get(1).printCluster();

	// 		// find a mean point for clusterPair
	// 		ArrayList<Integer> centroid = new ArrayList<Integer>();
	// 		for(int j = 0; j<clusterPair.get(0).leaves.size(); ++j)
	// 		{
	// 			centroid.add((clusterPair.get(0).leaves.get(j) + clusterPair.get(1).leaves.get(j))/2);
	// 		}
	// 		Cluster mid = new Cluster(centroid);
	// 		mid.children.add(clusterPair.get(0));
	// 		mid.children.add(clusterPair.get(1));
	// 		System.out.println(mid.leaves);
	// 		// mid.children.get(0).printCluster();
	// 		// mid.children.get(1).printCluster();

	// 		// remove clusterPair from the currentClusterList and add the mean point to it
	// 		currentClusterList.remove(clusterPair.get(0));
	// 		currentClusterList.remove(clusterPair.get(1));
	// 		currentClusterList.add(mid);

	// 		// for(Cluster c : currentClusterList)
	// 		// 	c.printCluster();
			
	// 		// // add clusterPair to clusterList
	// 		// addCluster(clusterPair, clusterList);

	// 		// update cluster count
	// 		k = currentClusterList.size();
	// 		System.out.println(k);
	// 		System.out.println("--------");

	// 	}
	// }

	// ArrayList<Cluster> minimumProximity(float[][] proximity, ArrayList<Cluster> currentClusterList)
	// {
	// 	int i=0,j=1;
	// 	float min = proximity[0][1];
	// 	int m = currentClusterList.size();
	// 	int n = currentClusterList.size();

	//    	for(int c = 0; c<m; c++)
	//    	{
	//     	for(int d = 0; d<n; d++)
	//       	{
	//       		if(c == d)
	//       			continue;
	//         	if (proximity[c][d] < min)
	//         	{
	//             	min = proximity[c][d];
	//             	i=c;
	//             	j=d;
	//         	}
	//       	}
	//    	}

	//    	ArrayList<Cluster> clusterPair = new ArrayList<Cluster>();
	//    	clusterPair.add(currentClusterList.get(i));
	//    	clusterPair.add(currentClusterList.get(j));

	// 	return clusterPair;
	// }

	// // TODO
	// // very imp - final representation 
	// void addCluster(ArrayList<Cluster> clusterPair, ArrayList<ArrayList<Cluster>> clusterList)
	// {
	// 	if(clusterPair.get(0).children.isEmpty() && clusterPair.get(1).children.isEmpty())
	// 		clusterList.add(clusterPair);
	// 	else
	// 	{
	// 		if(!clusterPair.get(0).children.isEmpty())
	// 			ArrayList<Cluster> lchild = clusterPair.get(0).children;


	// 	}
	// 	else if(!clusterPair.get(0).children.isEmpty())
	// 	{
	// 		clusterPair = clusterPair.get(1).children;
	// 		addCluster(clusterPair, clusterList);
	// 	}
	// 	else if(!clusterPair.get(0).children.isEmpty() && clusterPair.get(1).children.isEmpty())
	// 	{
	// 		clusterPair = clusterPair.get(0).children;
	// 		addCluster(clusterPair, clusterList);
	// 	}

	// }

	// method 3

	// method 4

/* Old function to create new flow objects */

	// static ArrayList<Flow> readInputCSV(String fileName) throws Exception 
	// {
	// 	ArrayList<Flow> flows = new ArrayList<Flow>();
	// 	String directoryPath = fileName;
	// 	File dir = new File(directoryPath);
 //  		File[] directoryListing = dir.listFiles();
 //  		int i;
 //  		if (directoryListing != null) 
 //  		{
 //    		for (File child : directoryListing) 
 //    		{
 //    			//System.out.println("**"+child.getName().charAt(4)+"**");
 //    			i = Character.getNumericValue((child.getName().charAt(4)));
 //      			Scanner sc = new Scanner(child);
	// 			ArrayList<Packet> data  = new ArrayList<Packet>();

	// 			int id=0; // P acket ID 
	// 			/* Converting each line into a transaction */
	// 			while(sc.hasNextLine())
	// 			{
	// 				String line = sc.nextLine();
	// 				String[] values = line.split(",");

	// 				++id;
	// 				int frame = Integer.parseInt(values[0]);
	// 				int incoming;
	// 				if(values[1].equals("True"))
	// 					incoming = 1;
	// 				else
	// 					incoming = 0;

	// 				String sourceIP = values[2];
	// 				String destIP = values[3];
	// 				String sourcePort = values[4];
	// 				String destPort = values[5];
	// 				String timestamp = values[6];
	// 				int size = Integer.parseInt(values[7]);

	// 				Packet temp = new Packet(id, frame, incoming, sourceIP,destIP,sourcePort,destPort,timestamp,size);

	// 				data.add(temp);
	// 			}

	// 			flows.add(new Flow(i, data));
	// 			i+=1;
	// 		}

 //    	}
    	
 //    	return flows;
 //    }

/* Old function to create packet pair objects */


	// method 1 - flow feature calculation
	// ArrayList<PacketPair> createPacketPairs(ArrayList<Flow> flows)
	// {
	// 	// go through each flow and make packet pairs within each
	// 	ArrayList<PacketPair> packetPairs = new ArrayList<PacketPair>();
	// 	for(Flow f : flows)
	// 	{
	// 		int t = 1; // packet pair ID
	// 		for(int i=0; i<f.features.size()-1; ++i)
	// 		{
	// 			if((f.features.get(i).incoming != f.features.get(i+1).incoming)) // && (f.features.get(i).frame+1 == f.features.get(i+1).frame))
	// 			{
	// 				PacketPair pp = new PacketPair(f.id, t, f.features.get(i), f.features.get(i+1));
	// 				packetPairs.add(pp);
	// 				++t;
	// 			}
	// 		}
	// 	}

	// 	return packetPairs;
	// }

/* Old function to print packet pairs */
// void printPacketPairs(ArrayList<PacketPair> flows)
	// {
	// 	for(PacketPair f : flows)
	// 	{
	// 		System.out.println("Flow: "+f.flowid+" PacketPairID: "+f.pid+"\n");
			
	// 		f.pair1.printPacket();
	// 		f.pair2.printPacket();
	// 		System.out.println("--x--x--x--");
	// 	}
	// }

/* Old function to label subprotocols */
	// // Find out the labels of the subprotocols for the subflows
	// void labelSubprotocols(ArrayList<Flow> flows, ArrayList<SubFlow> subflows)
	// {
	// 	// List of subflows for X alpha and X beta
	// 	ArrayList<SubFlow> x_alpha = new ArrayList<SubFlow>();
	// 	ArrayList<SubFlow> x_beta = new ArrayList<SubFlow>();

	// 	// Packet pairs denoting transition point 1 and 2
	// 	PacketPair trans_point1 = new PacketPair();
	// 	PacketPair trans_point2 = new PacketPair();

	// 	// Iterate through the flows
	// 	for(Flow f : flows)
	// 	{
	// 		// Go through each line in the logs of the flow
	// 		for(String line : f.logs)
	// 		{
	// 			// Locate the first transition point 
	// 			if(line.contains("starting service ssh-userauth"))
	// 			{
	// 				// Get the timestamp of first transition point
	// 				int x = line.indexOf("+");
	// 				String timestamp = line.substring(0, x);
	// 				// System.out.println(timestamp+"-----");
	// 				try 
	// 				{
	// 				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// 				    Date parsedDate = dateFormat.parse(timestamp);
	// 				    Timestamp ts = new java.sql.Timestamp(parsedDate.getTime() + (330*60*1000));
	// 				    // System.out.println(ts+"\n-------------");

	// 				} catch(Exception e) { //this generic but you can control another types of exception
	// 				    // look the origin of excption 
	// 				    System.out.println(e);
	// 				}

	// 				// Find the packet pair belonging to the flow
	// 				// having timestamp most similar to timestamp obtained above
	// 				float max_sim = 0;				
	// 				for(PacketPair pp : f.packetPairs)
	// 				{
	// 					// System.out.println(similarity(pp.pair1.timestamp, timestamp));
	// 					// System.out.println(pp.pair1.timestamp+"	"+ pp.pair2.timestamp);

	// 					if(similarity(pp.pair1.timestamp, timestamp) >= max_sim || similarity(pp.pair2.timestamp, timestamp) >= max_sim)
	// 					{
	// 						trans_point1 = pp;
	// 					}
	// 				}
					
	// 				// Label the subflow of that packet pair as transition point 1
	// 				for(SubFlow sf : subflows)
	// 				{
	// 					if(f.id == sf.flowid && trans_point1.pid == sf.ppid && sf.subprotocol == "none")
	// 					{
	// 						sf.subprotocol = "transition point 1";
	// 						x_alpha.add(sf);
	// 						break;
	// 						// System.out.println("flow: "+sf.flowid+" packet pair: "+sf.ppid);
	// 					}
	// 					if(f.id == sf.flowid && sf.ppid < trans_point1.pid)
	// 					{
	// 						sf.subprotocol = "transport";
	// 						x_alpha.add(sf);
	// 					}
	// 				} 
	// 				// System.out.println();
	// 			}

	// 			// Locate the second transition point
	// 			else if(line.contains("starting service ssh-connection"))
	// 			{
	// 				int x = line.indexOf("+");
	// 				String timestamp = line.substring(0, x);
	// 				// System.out.println(timestamp+"-----");
	// 				try 
	// 				{
	// 					// Get the timestamp of first transition point

	// 				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// 				    Date parsedDate = dateFormat.parse(timestamp);
	// 				    Timestamp ts = new java.sql.Timestamp(parsedDate.getTime() + (330*60*1000));
	// 				    // System.out.println(ts+"\n-------------");

	// 				} catch(Exception e) { //this generic but you can control another types of exception
	// 				    // look the origin of excption 
	// 				    System.out.println(e);
	// 				}

	// 				// Find the packet pair belonging to the flow
	// 				// having timestamp most similar to timestamp obtained above
	// 				float max_sim = 0;
	// 				for(PacketPair pp : f.packetPairs)
	// 				{
	// 					// System.out.println(similarity(pp.pair1.timestamp, timestamp));
	// 					// System.out.println(pp.pair1.timestamp+"	"+ pp.pair2.timestamp);

	// 					if(similarity(pp.pair1.timestamp, timestamp) >= max_sim || similarity(pp.pair2.timestamp, timestamp) >= max_sim)
	// 					{
	// 						trans_point2 = pp;
	// 					}
	// 				}
					
	// 				// Label the subflow of that packet pair as transition point 2
	// 				// System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");

	// 				for(SubFlow sf : subflows)
	// 				{
	// 					if(f.id == sf.flowid && trans_point2.pid == sf.ppid && sf.subprotocol == "none")
	// 					{
	// 						// System.out.println("sp: "+sf.subprotocol);
	// 						// System.out.println("beta");
	// 						sf.subprotocol = "transition point 2";
	// 						x_beta.add(sf);
	// 						// break;
	// 						// System.out.println("flow: "+sf.flowid+" packet pair: "+sf.ppid);
	// 					}
	// 					if(f.id == sf.flowid && sf.ppid > trans_point1.pid && sf.subprotocol == "none")
	// 					{
	// 						// System.out.println("sp: "+sf.subprotocol);
	// 						// System.out.println("betaaaa");
	// 						sf.subprotocol = "user auth";
	// 						x_beta.add(sf);
	// 					}
	// 				}
	// 			}
	// 			else
	// 				continue;
	// 		}
	// 	}

	// 	for(SubFlow s: x_alpha)
	// 	{
	// 		if(s.subprotocol == "user auth" || s.subprotocol == "transition point 2")
	// 			System.out.println("wrong 1 "+s.subprotocol);
	// 	}
	// 	for(SubFlow s: x_beta)
	// 	{
	// 		if(s.subprotocol == "transport" || s.subprotocol == "transition point 1")
	// 			System.out.println("wrong 2 "+s.subprotocol);
	// 	}

	// 	System.out.println("X alpha: "+x_alpha.size()+" X beta: "+x_beta.size());
	// }

/* Old helper functions */
// Simple edit distance algorithm to calculate similarity between two strings
	// int editDistance(String s1, String s2) 
	// {
	//     s1 = s1.toLowerCase();
	//     s2 = s2.toLowerCase();

	//     int[] costs = new int[s2.length() + 1];
	//     for (int i = 0; i <= s1.length(); i++) 
	//     {
	//       	int lastValue = i;
	//       	for (int j = 0; j <= s2.length(); j++) 
	//       	{
	// 	        if (i == 0)
	// 	          	costs[j] = j;
	// 	        else 
	// 	        {
	// 	          	if (j > 0) 
	// 	          	{
	// 		            int newValue = costs[j - 1];
	// 		            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	// 		              newValue = Math.min(Math.min(newValue, lastValue),
	// 		                  costs[j]) + 1;
	// 		            costs[j - 1] = lastValue;
	// 		            lastValue = newValue;
	// 	          	}
	// 	        }
	//       	}
	//       	if (i > 0)
	//         	costs[s2.length()] = lastValue;
	//     }
	//     return costs[s2.length()];
 //  	}

 //  	// Calculate the similarity measure
	// double similarity(String s1, String s2) 
	// {
	//   String longer = s1, shorter = s2;
	//   if (s1.length() < s2.length()) { // longer should always have greater length
	//     longer = s2; shorter = s1;
	//   }
	//   int longerLength = longer.length();
	//   if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	//   return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	// }


	/* Old IP swapping method */
	// swap check
	// if(SIP.equals("111.93.5.203") && SP.equals("22"))
	// {
	// 	SIP.replace(0, SIP.length(), DIP);
	// 	SP.replace(0, SP.length(), DP);
		
	// 	// System.out.println("i'm here.");
	// 	// System.out.println(SIP+" "+SP);
			// // q=1;
	// }

	// System.out.println(SIP+" "+SP);