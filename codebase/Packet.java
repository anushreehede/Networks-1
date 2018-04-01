// import java.sql.Timestamp;
// import java.text.SimpleDateFormat;
// import java.util.Date;

class Packet
{
	int id;
	int frame;
	int incoming;
	String sourceIP;
	String destIP;
	int sourcePort;
	int destPort;
	String timestamp;
	int size;

	Packet(int id, int frame, int incoming, String sourceIP, String destIP, int sourcePort,int destPort,String timestamp,int size)
	{
		this.id = id;
		this.frame = frame;
		this.incoming=incoming;
		this.sourceIP = sourceIP;
		this.destIP = destIP;
		this.sourcePort = sourcePort;
		this.destPort = destPort;
		this.timestamp = timestamp;
		this.size=size;
		
	}

	void printPacket()
	{
		System.out.println("Packet ID: "+id+ ", Frame: "+frame+", Incoming: "+incoming+"\nSource IP: "+sourceIP+", Destination IP: "+destIP+"\nSource port no: "+sourcePort+", Destination port: "+destPort+"\nTimestamp: "+timestamp+", Size: "+size+"\n");
	}
}