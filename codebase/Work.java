import java.io.File;
import java.util.*;
class Work{
	public static void main(String args[]) throws Exception
	{
		// contain details of all packets in each flow
		ArrayList<Flow> trainingExamples = readInputCSV(args[0]);
		for (Flow p : trainingExamples)
		{
			p.printFlow();
		}

		// u and v to decide subflow size
		int u=1, v=1;

		// object to call methods 
		Functionality f = new Functionality();

		// make packet pairs within each flow and print them 
		ArrayList<PacketPair> packetPairs = f.createPacketPairs(trainingExamples);
		f.printPacketPairs(packetPairs);

		// make subflows using flows and packet pair info
		ArrayList<SubFlow> subflows = f.createSubflows(trainingExamples, packetPairs, u, v);
		f.printSubFlows(subflows);

		// clustering the subflows within each flow
		for(SubFlow sfs : subflows)
		{
			System.out.println("Flow: "+sfs.flowid);
			f.clustering(sfs);
		}

		// assign labels to each flow
		//assignLabels(trainingExamples);
	}

	static ArrayList<Flow> readInputCSV(String fileName) throws Exception 
	{
		ArrayList<Flow> flows = new ArrayList<Flow>();
		String directoryPath = fileName;
		File dir = new File(directoryPath);
  		File[] directoryListing = dir.listFiles();
  		int i;
  		if (directoryListing != null) 
  		{
    		for (File child : directoryListing) 
    		{
    			//System.out.println("**"+child.getName().charAt(4)+"**");
    			i = Character.getNumericValue((child.getName().charAt(4)));
      			Scanner sc = new Scanner(child);
				ArrayList<Packet> data  = new ArrayList<Packet>();

				int id=0;
				/* Converting each line into a transaction */
				while(sc.hasNextLine())
				{
					String line = sc.nextLine();
					String[] values = line.split(",");

					++id;
					int frame = Integer.parseInt(values[0]);
					int incoming;
					if(values[1].equals("True"))
						incoming = 1;
					else
						incoming = 0;

					String sourceIP = values[2];
					String destIP = values[3];
					int sourcePort = Integer.parseInt(values[4]);
					int destPort = Integer.parseInt(values[5]);
					String timestamp = values[6];
					int size = Integer.parseInt(values[7]);

					Packet temp = new Packet(id, frame, incoming, sourceIP,destIP,sourcePort,destPort,timestamp,size);

					data.add(temp);
				}

				flows.add(new Flow(i, data));
				i+=1;
			}

    	}
    	
    	return flows;
    }

    static void assignLabels(ArrayList<Flow> trainingExamples)
    {
    	// assigning labels to flows from log files
    }

}