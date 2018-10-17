import java.util.*;
class Flow
{
	int id; // Flow ID

	// List of all the packets in the flow
	ArrayList<Packet> features = new ArrayList<Packet>();
	// Lines in the logs of the flow
	ArrayList<String> logs = new ArrayList<String>();
	// List of the packet pairs in the flow
	ArrayList<PacketPair> packetPairs = new ArrayList<PacketPair>();

	// Flow constructor
	public Flow(int i,ArrayList<Packet> p, ArrayList<String> l)
	{
		id = i;
		for(Packet pk : p)
		{
			features.add(pk);
		}
		for(String s : l)
		{
			logs.add(s);
		} 

		int t = 1; // packet pair ID
		for(int j=0; j<features.size()-1; ++j)
		{
			if((features.get(j).incoming != features.get(j+1).incoming)) // && (f.features.get(i).frame+1 == f.features.get(i+1).frame))
			{
				PacketPair pp = new PacketPair(t, features.get(j), features.get(j+1));
				packetPairs.add(pp);
				++t;
			}
		}
	}

	// Printing flow
	void printFlow()
	{
		System.out.print("\nFlow: "+id+"\n");
		for(Packet i:this.features)
			i.printPacket();
	}
}