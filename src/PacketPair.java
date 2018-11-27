import java.util.*;
class PacketPair
{
	int pid;
	Packet pair1;
	Packet pair2;
	SubFlow sf;

	public PacketPair()
	{
		pair1 = new Packet();
		pair2 = new Packet();
		sf = new SubFlow();
	}
	public PacketPair(int pid, Packet p1, Packet p2)
	{
		this.pid = pid;
		pair1 = p1;
		pair2 = p2;
		sf = new SubFlow();
	}
}