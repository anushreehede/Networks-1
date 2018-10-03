package trainingset;

public class Sample {
	public String filename;
	FlagCount flgCount;
	public PacketCount pktCount;
	public PacketCount pktCountEP;	// count only encrypted packets
	public ByteCount byteCount;
	public ByteCount byteCountEP;	// count only encrypted packets
	public IAT iat; // Inter-arrival time between packets
	public IAT iatEP;	// Inter-arrival time between encrypted packets
	public Mean mean;
	String label;
	
	// IAT is in milli-seconds
	public class IAT {
		public float mean;
		float variance;
	}

	class FlagCount {
		int sumOfURG;
		int sumOfACK;
		int sumOfPSH;
		int sumOfRST;
		int sumOfSYN;
		int sumOfFIN;
	}

	class PacketCount {
		int total;
		int recvTotal;
		int sentTotal;

		int totalPayloads;
		int recvTotalPayloads;
		int sentTotalPayloads;
	}

	public class ByteCount {
		int sumBytes;
		int sumRecvBytes;
		int sumSentBytes;

		int sumPayloadBytes;
		public int sumPayloadRecvBytes;
		int sumPayloadSentBytes;

		int sumIPBytes;
		int sumIPRecvBytes;
		int sumIPSentBytes;

		int sumTcpBytes;
		int sumTcpRecvBytes;
		int sumTcpSentBytes;
	}

	/**
	 * Mean length of packets in a session pcap file
	 *
	 * @author gokul
	 *
	 */
	class Mean {
		double meanPktLength;
		double meanRecvPktLength;
		double meanSentPktLength;

		double meanPayloadLength;
		double meanRecvPayloadLength;
		double meanSentPayloadLength;

		double meanIPLength;
		double meanRecvIPLength;
		double meanSentIPLength;

		double meanTCPLength;
		double meanRecvTCPLength;
		double meanSentTCPLength;
	}

	/**
	 * Variance of packet length in a session pcap file
	 *
	 * @author gokul
	 *
	 */
	class Variance {
		double varPktLength;
		double varRecvPktLength;
		double varSentPktLength;

		double varPayloadLength;
		double varRecvPayloadLength;
		double varSentPayloadLength;

		double varIPLength;
		double varRecvIPLength;
		double varSentIPLength;

		double varTCPLength;
		double varRecvTCPLength;
		double varSentTCPLength;
	}

	/***
	 * Experiment 1 feature set
	 *
	 * @return Concatenation of all features delimited by a comma
	 */
	private String getString1() {
		StringBuilder builder = new StringBuilder();

		// Total number of packets in a session file
		builder.append(this.pktCount.total + ",");

		// Total number of packets received in a session file
		builder.append(this.pktCount.recvTotal + ",");

		// Total number of packets sent in a session file
		builder.append(this.pktCount.sentTotal + ",");

		// The sum of all packets in a session file
		builder.append(this.byteCount.sumBytes + ",");

		// The sum of all received packets in a session file
		builder.append(this.byteCount.sumRecvBytes + ",");

		// The sum of all sent packets in a session file
		builder.append(this.byteCount.sumSentBytes + ",");

		// The sum of all payloads in a session file
		builder.append(this.byteCount.sumPayloadBytes + ",");

		// The sum of all received payloads in a session file
		builder.append(this.byteCount.sumPayloadRecvBytes + ",");

		// The sum of all sent payloads in a session file
		builder.append(this.byteCount.sumPayloadSentBytes + ",");

		// The mean inter-arrival time between received packets
		builder.append(this.iat.mean + ",");

		// The variance of inter-arrival time between received packets
		builder.append(this.iat.variance + ",");

		// The total number of packets with ACK flag set in a session file
		builder.append(this.flgCount.sumOfACK + ",");

		// The total number of packets with PSH flag set in a session file
		builder.append(this.flgCount.sumOfPSH + ",");

		// The total number of packets with RST flag set in a session file
		builder.append(this.flgCount.sumOfRST + ",");

		// Label
		builder.append(this.label + ",");

		// Filename
		builder.append(this.filename);

		return (builder.toString());
	}

	/***
	 * Experiment 2 feature set <br>
	 * Considers the average payload lengths. <br>
	 *
	 * @return Concatenation of all features delimited by a comma
	 */
	@SuppressWarnings("unused")
	private String getString2() {
		StringBuilder builder = new StringBuilder();

		// The sum of all payloads in a session file
		builder.append(this.byteCount.sumPayloadBytes + ",");

		// The sum of all received payloads in a session file
		builder.append(this.byteCount.sumPayloadRecvBytes + ",");

		// The sum of all sent payloads in a session file
		builder.append(this.byteCount.sumPayloadSentBytes + ",");

		// The mean inter-arrival time between received packets
		builder.append(this.iat.mean + ",");

		// The variance of inter-arrival time between received packets
		builder.append(this.iat.variance + ",");

		// The total number of packets with RST flag set in a session file
		builder.append(this.flgCount.sumOfRST + ",");

		// Average length of all the payloads in a session file
		builder.append(this.mean.meanPayloadLength + ",");

		// Average length of all the received payloads in a session file
		builder.append(this.mean.meanRecvPayloadLength + ",");

		// Average length of all the sent payloads in a session file
		builder.append(this.mean.meanSentPayloadLength + ",");

		// Label
		builder.append(this.label + ",");

		// Filename
		builder.append(this.filename);

		return (builder.toString());
	}

	@Override
	public String toString() {
		return this.getString1();
	}
}
