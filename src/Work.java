import java.io.File;
import java.util.*;
public class Work{
	public static void main(String args[]) throws Exception
	{
		// object to call methods 
		Functionality f = new Functionality();

		// contain details of all packets in each flow
		ArrayList<Flow> flows = f.createFlows(args[0]);

		// for (Flow p : flows)
		// {
		// 	int c=0;
		// 	for(String line: p.logs)
		// 	{
		// 		if(line.contains("New connection: "))
		// 			++c;
		// 	}
		// 	System.out.println("Flow: "+p.id+" number of new connections: "+c);
		// }

		System.out.println("Number of flows in pcap files: "+flows.size());

		// u and v to decide subflow size
		int u=1, v=1;

		// make subflows using flows and packet pair info
		ArrayList<SubFlow> subflows = f.createSubflows(flows, u, v);
		// f.printSubFlows(subflows);
		
		System.out.println("Number of sub flows: "+subflows.size());

		// label the subflows with the subprotocols 
		f.labelSubprotocols(flows, subflows);
		// f.printSubFlows(subflows);

		// create a CSV file dataset using th subflows
		f.createDataset(subflows);

	}

}