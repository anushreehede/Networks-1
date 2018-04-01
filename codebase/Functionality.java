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

	// method 3

	// method 4

}