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
		for(ArrayList<Integer> sflist_outer : clusterList.leaves)
		{
			int j=0;
			for(ArrayList<Integer> sflist_inner : clusterList.leaves)
			{
				proximity[i][j] = euclideanDistance(sflist_outer, sflist_inner);
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

	void clustering(SubFlow sf, int flowSize)
	{
		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
		// function to convert the subflows sf to clusters 
		for(ArrayList<Integer> i : sf.subflows)
		{
			clusterList.add(new Cluster(i));
		}

		float[][] proximity = proximityMatrix(clusterList, flowSize);
		int k = flowSize;

		while(k > 1) // until the number of clusters becomes 1.
		{
			// get i and j of subflows having least proximity
			// take centroid of those subflows - associated those subflows with their centroid
			// new proximity matrix is calculated using this centroid
			// for each centroid formed create a cluster, update its centroid and subflows within 
			// the cluster

			// get (i,j) having minimum proximity
			// create cluster object for k for (i,j)
			// to current cluster list add all clusters except i and j, finally add k
			// take centroid of each cluster as next point and calculate new proximity matrix 

			ArrayList<Cluster> currentClusterList = new ArrayList<Cluster>();

			float[][] proximity = proximityMatrix(,k);

			--k;

		}

	}


	// method 3

	// method 4

}