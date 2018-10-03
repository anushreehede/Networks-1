package trainingset;

import java.util.ArrayList;
import fileIO.ASCIIFileIO;
import fileIO.FolderMethods;
import kippo_java.TCPFlow;
import utilities.CustomLogger;
import kippo_java.ConnDetails;

/***
 * Store each TCP flow pcap file in a different class directory. <br>
 * 1)Segregate all flows where packets have no AL payloads. <br>
 * 2)Segregate all flows with AL payload only from the HP. <br>
 * 3)
 * 
 * @author gokul
 */
public class SampleCreation2 {
	/***
	 * Run this after executing SampleCreation1.java. <br>
	 * Copy the SSH pcap files to /home/gokul/Downloads/ssh_flows/ directory. <br>
	 * Set the location where kippo log files are stored (). <br>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FolderMethods.createDir(Filenames.SEVERE_DIR);
		FolderMethods.createDir(Filenames.SUCCESS_NP_DIR);
		FolderMethods.createDir(Filenames.BF_DIR);

		// Analyze kippo log files and move the pcap files.
		ArrayList<TCPFlow> sessions = ConnDetails.getAllSessions(constants.Filenames.INPUT_DIRECTORY2_2,
				constants.Filenames.OS_INFO_FILENAME2, constants.Constants.DST_IP2);

		ArrayList<String> noKippomatch = new ArrayList<String>();

		for (TCPFlow s : sessions) {
			// ArrayList<String> matches = FolderMethods.listFolder(COMPLETED_SESSIONS_DIR,
			// s.pcapFilename);
			// long min, diff;
			// if (matches.size() == 0) {
			// noKippomatch.add(s.pcapFilename + " " + Long.toString(s.startTime));
			// } else if (matches.size() >= 1) {
			// min = s.startTime;
			// for (String file : matches) {
			// String absSrcFilename = COMPLETED_SESSIONS_DIR + "/" + file;
			// if (FolderMethods.isFileExists(COMPLETED_SESSIONS_DIR, file)) {
			// PcapPacket packet = PcapUtilities.getFirstPacket(absSrcFilename);
			// diff = Math.abs((PacketDate.getTimeStamp(packet)) / 1000 - s.startTime);
			// if (diff < min) {
			// min = diff;
			// s.pcapFilename = file;
			// }
			// }
			// }
			// }

			if (s.successAttempts > 0) {
				if (s.ttyLog != null) {
					if (s.cmds.size() > 0) {
						FolderMethods.moveFile(Filenames.COMPLETED_SESSIONS_DIR + "/" + s.pcapFilename,
								Filenames.SEVERE_DIR + "/" + s.pcapFilename);
					} else {
						FolderMethods.moveFile(Filenames.COMPLETED_SESSIONS_DIR + "/" + s.pcapFilename,
								Filenames.SUCCESS_NP_DIR + "/" + s.pcapFilename);
					}
				} else {
					FolderMethods.moveFile(Filenames.COMPLETED_SESSIONS_DIR + "/" + s.pcapFilename,
							Filenames.SUCCESS_NP_DIR + "/" + s.pcapFilename);
				}
			} else {
				if (s.failedAttempts > 0) {
					FolderMethods.moveFile(Filenames.COMPLETED_SESSIONS_DIR + "/" + s.pcapFilename,
							Filenames.BF_DIR + "/" + s.pcapFilename);
				} else {
					FolderMethods.moveFile(Filenames.COMPLETED_SESSIONS_DIR + "/" + s.pcapFilename,
							Filenames.PORT_SCAN_DIR + "/" + s.pcapFilename);
				}
			}
		}

		ASCIIFileIO.write(Filenames.kipponomatch, noKippomatch);
	}
}
