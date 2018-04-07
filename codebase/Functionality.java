import java.util.*;
class Functionality
{
	// method 1 - flow feature calculation
	ArrayList<PacketPair> createPacketPairs(ArrayList<Flow> flows)
	{
		// go through each flow and make packet pairs within each
		ArrayList<PacketPair> packetPairs = new ArrayList<PacketPair>();
		for(Flow f : flows)
		{
			int t = 1; // packet pair ID
			for(int i=0; i<f.features.size()-1; ++i)
			{
				if((f.features.get(i).incoming != f.features.get(i+1).incoming)) // && (f.features.get(i).frame+1 == f.features.get(i+1).frame))
				{
					PacketPair pp = new PacketPair(f.id, t, f.features.get(i), f.features.get(i+1));
					packetPairs.add(pp);
					++t;
				}
			}
		}

		return packetPairs;
	}

	ArrayList<SubFlow> createSubflows(ArrayList<Flow> flows, ArrayList<PacketPair> packetPairs, int u, int v)
	{
		// create subflows for packet pairs in each flow and create their flow features
		ArrayList<SubFlow> subflows = new ArrayList<SubFlow>();
		for(Flow f : flows)
		{
			// each subflow associated with a flow - use the flow id
			SubFlow sf = new SubFlow(f.id);

			// for each packet pair belonging to the flow f 
			for(PacketPair p : packetPairs)
			{
				if(f.id == p.flowid)
				{
					// make a valid subflow range using values u and v 
					int i = p.pair1.id - u;
					int j = p.pair2.id + v;
					
					if(i<=0 || j>f.features.size()) continue;

					// create the subflows themselves 
					ArrayList<Integer> subflow = new ArrayList<Integer>();
					for(int k=i; k<=j; ++k)
					{
						int n = f.features.get(k-1).size;
						if(f.features.get(k-1).incoming == 0)
							n = 0-n;
						subflow.add(n);
					}
					sf.subflows.add(subflow);
				}
			}

			subflows.add(sf);
		}

		return subflows;
	}

	void printPacketPairs(ArrayList<PacketPair> flows)
	{
		for(PacketPair f : flows)
		{
			System.out.println("Flow: "+f.flowid+" PacketPairID: "+f.pid+"\n");
			
			f.pair1.printPacket();
			f.pair2.printPacket();
			System.out.println("Subprotocol: "+f.subprotocol+"\n--x--x--x--");
		}
	}

	void printSubFlows(ArrayList<SubFlow> subflows)
	{
		for(SubFlow sf : subflows)
		{
			System.out.println("Flow: "+sf.flowid+"\n*******");
			for(ArrayList<Integer> sflist : sf.subflows)
			{
				System.out.println(sflist);
			}
		}
	}

	// method 2
	float[][] proximityMatrix(ArrayList<Cluster> clusterList, int flowSize)
	{
		float[][] proximity = new float[flowSize][flowSize];
		int i=0;
		for(Cluster c_outer : clusterList)
		{
			int j=0;
			for(Cluster c_inner : clusterList)
			{
				proximity[i][j] = euclideanDistance(c_outer.leaves, c_inner.leaves);
				++j;
			}
			++i;
		}

		for(i=0; i<flowSize; ++i)
		{
			for(int j=0; j<flowSize; ++j)
			{
				System.out.print(proximity[i][j]+" \t ");
			}
			System.out.print("\n");
		}

		return proximity;

		// using proximity matrix begin clustering. write pseudocode. 
	}

	float euclideanDistance(ArrayList<Integer> u, ArrayList<Integer> v)
	{	
		int sum = 0;
		for(int i=0; i<u.size(); ++i)
		{
			sum += ((u.get(i) - v.get(i)) * (u.get(i) - v.get(i)));
		}
		float dist = (float)Math.sqrt(sum);
		return dist;
	}

	void clustering(SubFlow sf)
	{
		ArrayList<ArrayList<Cluster>> clusterList = new ArrayList<ArrayList<Cluster>>();
		ArrayList<Cluster> currentClusterList = new ArrayList<Cluster>();
		// function to convert the subflows sf to clusters 
		for(ArrayList<Integer> i : sf.subflows)
		{
			ArrayList<Cluster> temp = new ArrayList<Cluster>();
			temp.add(new Cluster(i));
			clusterList.add(temp);
			currentClusterList.add(new Cluster(i));
		}

		int k = currentClusterList.size();

		while(k > 2) // until the number of clusters becomes 1.
		{
			for(Cluster c : currentClusterList)
				c.printCluster();

			float[][] proximity = proximityMatrix(currentClusterList,k);

			ArrayList<Cluster> clusterPair = minimumProximity(proximity, currentClusterList);

			clusterPair.get(0).printCluster();
			clusterPair.get(1).printCluster();

			// find a mean point for clusterPair
			ArrayList<Integer> centroid = new ArrayList<Integer>();
			for(int j = 0; j<clusterPair.get(0).leaves.size(); ++j)
			{
				centroid.add((clusterPair.get(0).leaves.get(j) + clusterPair.get(1).leaves.get(j))/2);
			}
			Cluster mid = new Cluster(centroid);
			mid.children.add(clusterPair.get(0));
			mid.children.add(clusterPair.get(1));
			System.out.println(mid.leaves);
			// mid.children.get(0).printCluster();
			// mid.children.get(1).printCluster();

			// remove clusterPair from the currentClusterList and add the mean point to it
			currentClusterList.remove(clusterPair.get(0));
			currentClusterList.remove(clusterPair.get(1));
			currentClusterList.add(mid);

			// for(Cluster c : currentClusterList)
			// 	c.printCluster();
			
			// // add clusterPair to clusterList
			// addCluster(clusterPair, clusterList);

			// update cluster count
			k = currentClusterList.size();
			System.out.println(k);

		}
	}

	ArrayList<Cluster> minimumProximity(float[][] proximity, ArrayList<Cluster> currentClusterList)
	{
		int i=0,j=1;
		float min = proximity[0][1];
		int m = currentClusterList.size();
		int n = currentClusterList.size();

	   	for(int c = 0; c<m; c++)
	   	{
	    	for(int d = 0; d<n; d++)
	      	{
	      		if(c == d)
	      			continue;
	        	if (proximity[c][d] < min)
	        	{
	            	min = proximity[c][d];
	            	i=c;
	            	j=d;
	        	}
	      	}
	   	}

	   	ArrayList<Cluster> clusterPair = new ArrayList<Cluster>();
	   	clusterPair.add(currentClusterList.get(i));
	   	clusterPair.add(currentClusterList.get(j));

		return clusterPair;
	}

	// TODO
	// very imp - final representation 
	void addCluster(ArrayList<Cluster> clusterPair, ArrayList<ArrayList<Cluster>> clusterList)
	{
		if(clusterPair.get(0).children.isEmpty() && clusterPair.get(1).children.isEmpty())
			clusterList.add(clusterPair);
		else
		{
			if(!clusterPair.get(0).children.isEmpty())
				ArrayList<Cluster> lchild = clusterPair.get(0).children;


		}
		else if(!clusterPair.get(0).children.isEmpty())
		{
			clusterPair = clusterPair.get(1).children;
			addCluster(clusterPair, clusterList);
		}
		else if(!clusterPair.get(0).children.isEmpty() && clusterPair.get(1).children.isEmpty())
		{
			clusterPair = clusterPair.get(0).children;
			addCluster(clusterPair, clusterList);
		}

	}

	// method 3

	// method 4

}