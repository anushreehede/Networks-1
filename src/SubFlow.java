import java.util.*;
class SubFlow
{
	int flowid; // Flow to which subflow belongs
	int ppid; // Packet pair around which the subflow is built
	ArrayList<Integer> subflow; // Subflow features
	String subprotocol; // Subprotocol value of the subflow

	public SubFlow()
	{
		
	}
	public SubFlow(int fid)
	{
		flowid = fid;
		subflow = new ArrayList<Integer>();
		subprotocol = "none";
	}

}