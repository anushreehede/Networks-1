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
	
	// int label;

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
		// label = -1;

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
		// String lbl;
		// switch(this.label)
		// {
		// 	case 0: lbl = "Successful DA"; break;
		// 	case 1: lbl = "Unsuccessful DA"; break;
		// 	case 2: lbl = "SSH Connection"; break;
		// 	default: lbl = "NA"; break;
		// }

		System.out.print("\nFlow: "+id+"\n");
		for(Packet i:this.features)
			i.printPacket();
		// System.out.println("Label -> "+lbl);
	}
}