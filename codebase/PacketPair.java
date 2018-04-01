import java.util.*;
class PacketPair
{
	int flowid;
	int pid;
	Packet pair1;
	Packet pair2;
	String subprotocol;

	public PacketPair(int fid, int pid, Packet p1, Packet p2)
	{
		flowid = fid;
		this.pid = pid;
		pair1 = p1;
		pair2 = p2;
		subprotocol = "none";
	}
}