import java.util.*;
class Flow
{
	int id;
	ArrayList<Packet> features = new ArrayList<Packet>();
	int label;

	public Flow(int i,ArrayList<Packet> p)
	{
		id = i;
		for(Packet pk : p)
		{
			features.add(pk);
		}
		label = -1;
	}

	void printFlow()
	{
		String lbl;
		switch(this.label)
		{
			case 0: lbl = "Successful DA"; break;
			case 1: lbl = "Unsuccessful DA"; break;
			case 2: lbl = "SSH Connection"; break;
			default: lbl = "NA"; break;
		}

		System.out.print("\nFlow: "+id+"\n");
		for(Packet i:this.features)
			i.printPacket();
		System.out.println("Label -> "+lbl);
	}
}