import java.util.*;
class PacketPair
{
	int pid;
	Packet pair1 = new Packet();
	Packet pair2 = new Packet();

	public PacketPair()
	{
		
	}
	public PacketPair(int pid, Packet p1, Packet p2)
	{
		this.pid = pid;
		pair1 = p1;
		pair2 = p2;
	}
}