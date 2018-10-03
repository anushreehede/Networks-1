package trainingset;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.jnetpcap.packet.PcapPacket;

import fileIO.ASCIIFileIO;
import fileIO.FolderMethods;
import net_utilities.PcapUtilities;

/***
 * Program to create the dataset.
 * @author gokul
 */
public class CreateTS {
	private final static String sessionDir = "/home/gokul/workspace/SSH_ML/non_severe";
	private final static String label = "0";

	public static void main(String[] args) {
		ArrayList<Sample> samples = CreateTS.getAllSamples(CreateTS.sessionDir);

		PrintWriter file = ASCIIFileIO.newFile("temp.csv");

		for (Sample f : samples) {
			file.println(f);
		}

		file.close();
	}

	/***
	 * Get a list of training samples
	 *
	 * @param dirName
	 * @return a list of samples
	 */
	public static ArrayList<Sample> getAllSamples(String dirName) {
		ArrayList<Sample> sessions = null;
		Sample session = null;
		ArrayList<PcapPacket> packets = null;
		ArrayList<String> files = null;

		files = FolderMethods.getAbsFilenames(dirName);
		sessions = new ArrayList<Sample>();

		for (String filename : files) {
			packets = PcapUtilities.getAllPackets(filename);

			if (packets.size() > 0) {
				session = new Sample();

				session.pktCount = ExtractFeature.countOfPkts(packets);
				session.byteCount = ExtractFeature.sumOfBytes(packets);
				session.flgCount = ExtractFeature.sumOfTCPFlags(packets);
				session.iat = ExtractFeature.computeIATMeasures(packets);
				session.mean = ExtractFeature.meanOfPktLength(packets);

				// Scaling for ACK, PSH, RST
				session.flgCount.sumOfACK *= 100;
				session.flgCount.sumOfPSH *= 100;
				session.flgCount.sumOfRST *= 100;
				session.label = CreateTS.label;

				session.filename = filename;

				sessions.add(session);
			}

			System.out.println("Finished processing " + filename);
			session = null;
		}

		return sessions;
	}

	/***
	 * Display the sample for one pcap file
	 * 
	 * @param filename
	 */
	public static void testOneFile(String filename) {
		ArrayList<PcapPacket> packets = null;
		Sample session = null;

		packets = PcapUtilities.getAllPackets(filename);

		if (packets.size() > 0) {
			session = new Sample();

			session.pktCount = ExtractFeature.countOfPkts(packets);
			session.byteCount = ExtractFeature.sumOfBytes(packets);
			session.flgCount = ExtractFeature.sumOfTCPFlags(packets);
			session.iat = ExtractFeature.computeIATMeasures(packets);
			session.mean = ExtractFeature.meanOfPktLength(packets);

			System.out.println(session);

			System.out.println(session.byteCount.sumPayloadRecvBytes);
			System.out.println(session.byteCount.sumPayloadSentBytes);
		}
	}

}
