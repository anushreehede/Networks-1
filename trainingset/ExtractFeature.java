package trainingset;

import java.util.ArrayList;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import trainingset.Sample.ByteCount;
import trainingset.Sample.FlagCount;
import trainingset.Sample.IAT;
import trainingset.Sample.Mean;
import trainingset.Sample.PacketCount;
import trainingset.Sample.Variance;

import utilities.CustomMath;
import constants.Constants;
import header.IPHeaderInfo;
import net_utilities.DatePcapUtilities;

public class ExtractFeature {
	public static void main(String[] args) {

	}

	/**
	 * Compute the inter-arrival time (IAT) between packets
	 *
	 * @param packets
	 * @return inter-arrival time measures (mean, variance)
	 */
	public static IAT computeIATMeasures(ArrayList<PcapPacket> packets) {
		IAT iat = null;
		ArrayList<Long> iats = null;
		double[] d = null;

		iat = (new Sample()).new IAT();

		iats = DatePcapUtilities.getIATPackets(packets);
		d = new double[iats.size()];

		for (int i = 0; i < iats.size(); i++) {
			d[i] = iats.get(i).doubleValue();
		}

		iat.mean = (float) CustomMath.getMean(d);
		iat.variance = (float) CustomMath.getVariance(d);

		return iat;
	}

	/**
	 * Compute the mean of packet's length
	 *
	 * @param packets
	 * @return the mean of packet's length
	 */
	public static Mean meanOfPktLength(ArrayList<PcapPacket> packets) {
		Mean mean = null;
		PacketCount cnt = null;
		double[][] length = null;
		PcapPacket packet = null;
		Payload payload = null;
		Tcp tcp = null;
		Ip4 ip = null;

		cnt = countOfPkts(packets);

		mean = (new Sample()).new Mean();
		length = new double[12][cnt.total];
		payload = new Payload();
		tcp = new Tcp();
		ip = new Ip4();

		for (int i = 0; i < cnt.total; i++) {
			packet = packets.get(i);
			length[0][i] = packet.size();

			if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress))
				length[1][i] = packet.size();
			else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false)
				length[2][i] = packet.size();

			// Payload
			if (packet.hasHeader(payload)) {
				length[3][i] += payload.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					length[4][i] += payload.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					length[5][i] += payload.size();
				}
			}

			// IPv4
			if (packet.hasHeader(ip)) {
				length[6][i] += ip.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					length[7][i] += ip.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					length[8][i] += ip.size();
				}
			}

			// Tcp
			if (packet.hasHeader(tcp)) {
				length[9][i] += tcp.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					length[10][i] += tcp.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					length[11][i] += tcp.size();
				}
			}
		}

		mean.meanPktLength = CustomMath.getMean(length[0]);
		mean.meanRecvPktLength = CustomMath.getMean(length[1]);
		mean.meanSentPktLength = CustomMath.getMean(length[2]);

		mean.meanPayloadLength = CustomMath.getMean(length[3]);
		mean.meanRecvPayloadLength = CustomMath.getMean(length[4]);
		mean.meanSentPayloadLength = CustomMath.getMean(length[5]);

		mean.meanIPLength = CustomMath.getMean(length[6]);
		mean.meanRecvIPLength = CustomMath.getMean(length[7]);
		mean.meanSentIPLength = CustomMath.getMean(length[8]);

		mean.meanTCPLength = CustomMath.getMean(length[9]);
		mean.meanRecvTCPLength = CustomMath.getMean(length[10]);
		mean.meanSentTCPLength = CustomMath.getMean(length[11]);

		return mean;
	}

	/**
	 * Compute the population variance of packet's length
	 *
	 * @param packets
	 * @return the variance of packet's length
	 */
	public static Variance varOfPktLength(ArrayList<PcapPacket> packets) {
		Variance var = null;
		PacketCount cnt = null;
		double[][] length = null;
		PcapPacket packet = null;
		Payload payload = null;
		Tcp tcp = null;
		Ip4 ip = null;

		cnt = countOfPkts(packets);

		var = (new Sample()).new Variance();
		length = new double[12][cnt.total];
		payload = new Payload();
		tcp = new Tcp();
		ip = new Ip4();

		for (int i = 0; i < cnt.total; i++) {
			packet = packets.get(i);
			length[0][i] = packet.size();

			if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress))
				length[1][i] = packet.size();
			else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false)
				length[2][i] = packet.size();

			// Payload
			if (packet.hasHeader(payload)) {
				length[3][i] += payload.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					length[4][i] += payload.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					length[5][i] += payload.size();
				}
			}

			// IPv4
			if (packet.hasHeader(ip)) {
				length[6][i] += ip.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					length[7][i] += ip.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					length[8][i] += ip.size();
				}
			}

			// Tcp
			if (packet.hasHeader(tcp)) {
				length[9][i] += tcp.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					length[10][i] += tcp.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					length[11][i] += tcp.size();
				}
			}
		}

		var.varPktLength = CustomMath.getVariance(length[0]);
		var.varRecvPktLength = CustomMath.getVariance(length[1]);
		var.varSentPktLength = CustomMath.getVariance(length[2]);

		var.varPayloadLength = CustomMath.getVariance(length[3]);
		var.varRecvPayloadLength = CustomMath.getVariance(length[4]);
		var.varSentPayloadLength = CustomMath.getVariance(length[5]);

		var.varIPLength = CustomMath.getVariance(length[6]);
		var.varRecvIPLength = CustomMath.getVariance(length[7]);
		var.varSentIPLength = CustomMath.getVariance(length[8]);

		var.varTCPLength = CustomMath.getVariance(length[9]);
		var.varRecvTCPLength = CustomMath.getVariance(length[10]);
		var.varSentTCPLength = CustomMath.getVariance(length[11]);

		return var;
	}

	/***
	 * Get the sum of TCP flags
	 *
	 * @param packets
	 * @return the sum of each and every TCP flag
	 */
	public static FlagCount sumOfTCPFlags(ArrayList<PcapPacket> packets) {
		FlagCount flagCnt = null;
		Tcp tcp = null;

		flagCnt = (new Sample()).new FlagCount();

		for (PcapPacket packet : packets) {
			tcp = packet.getHeader(new Tcp());
			if (tcp != null) {
				if (tcp.flags_URG())
					flagCnt.sumOfURG++;
				if (tcp.flags_ACK())
					flagCnt.sumOfACK++;
				if (tcp.flags_PSH())
					flagCnt.sumOfPSH++;
				if (tcp.flags_RST())
					flagCnt.sumOfRST++;
				if (tcp.flags_SYN())
					flagCnt.sumOfSYN++;
				if (tcp.flags_FIN())
					flagCnt.sumOfFIN++;
			}
		}

		return flagCnt;
	}

	/***
	 * Get the number of packets w.r.t various criteria
	 *
	 * @param packets
	 * @return the number of packets
	 */
	public static PacketCount countOfPkts(ArrayList<PcapPacket> packets) {
		PacketCount pktCnt = null;

		pktCnt = (new Sample()).new PacketCount();

		for (PcapPacket packet : packets) {
			pktCnt.total++;

			if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress))
				pktCnt.recvTotal++;
			else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false)
				pktCnt.sentTotal++;
		}

		return pktCnt;
	}

	/***
	 * Get the total number of bytes w.r.t different criteria (jnetpcap 1.3)
	 *
	 * @param packets
	 * @return the number of bytes
	 */
	public static ByteCount sumOfBytes(ArrayList<PcapPacket> packets) {
		ByteCount cnt = null;
		Payload payload = null;
		Tcp tcp = null;
		Ip4 ip = null;

		cnt = (new Sample()).new ByteCount();
		payload = new Payload();
		tcp = new Tcp();
		ip = new Ip4();

		for (PcapPacket packet : packets) {
			// Packet
			cnt.sumBytes += packet.size();

			if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
				cnt.sumRecvBytes += packet.size();
			} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
				cnt.sumSentBytes += packet.size();
			}

			// IPv4
			if (packet.hasHeader(ip)) {
				cnt.sumIPBytes += ip.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					cnt.sumIPRecvBytes += ip.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					cnt.sumIPSentBytes += ip.size();
				}
			}

			// Tcp
			if (packet.hasHeader(tcp)) {
				cnt.sumTcpBytes += tcp.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					cnt.sumTcpRecvBytes += tcp.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					cnt.sumTcpSentBytes += tcp.size();
				}
			}

			// Payload
			if (packet.hasHeader(payload)) {
				cnt.sumPayloadBytes += payload.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					cnt.sumPayloadRecvBytes += payload.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					cnt.sumPayloadSentBytes += payload.size();
				}
			}
		}

		return cnt;
	}

	/***
	 * Get the total number of bytes w.r.t different criteria (jnetpcap 1.4)
	 *
	 * @param packets
	 * @return the number of bytes
	 */
	public static ByteCount sumOfBytes1(ArrayList<PcapPacket> packets) {
		ByteCount cnt = null;
		JBuffer buffer = null;
		JBuffer payload = null;
		Tcp tcp = null;
		Ip4 ip = null;

		cnt = (new Sample()).new ByteCount();
		buffer = new JBuffer(JMemory.Type.POINTER);
		tcp = new Tcp();
		ip = new Ip4();

		if (packets == null)
			return null;

		for (PcapPacket packet : packets) {
			// Packet
			cnt.sumBytes += packet.size();

			if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
				cnt.sumRecvBytes += packet.size();
			} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
				cnt.sumSentBytes += packet.size();
			}

			// IPv4
			if (packet.hasHeader(ip)) {
				cnt.sumIPBytes += ip.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					cnt.sumIPRecvBytes += ip.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					cnt.sumIPSentBytes += ip.size();
				}
			}

			// Tcp
			if (packet.hasHeader(tcp)) {
				cnt.sumTcpBytes += tcp.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					cnt.sumTcpRecvBytes += tcp.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					cnt.sumTcpSentBytes += tcp.size();
				}
			}

			// Payload
			payload = tcp.peerPayloadTo(buffer);
			if (payload.size() > 0) {
				cnt.sumPayloadBytes += payload.size();

				if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress)) {
					cnt.sumPayloadRecvBytes += payload.size();
				} else if (IPHeaderInfo.isDestination(packet, Constants.destIPaddress) == false) {
					cnt.sumPayloadSentBytes += payload.size();
				}
			}
		}

		return cnt;
	}
}
