package trainingset;

import fileIO.FolderMethods;
import tl_library.PortScanner;
import tl_library.SegregateTCPFlows;

/***
 * Take all SSH TCP flow pcap files. <br>
 * Segregate it based on, <br>
 * 1) All TCP flows that are port scanners. <br>
 * 2) All TCP flows that have both connection establishment and connection
 * termination. <br>
 * 3) All TCP flows that have only connection establishment. <br>
 * 
 * @author gokul
 *
 */
public class SampleCreation1 {
	/***
	 * Copy the SSH pcap files to /home/gokul/Downloads/ssh_flows/ directory. <br>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Move files containing no payloads to different directory
		FolderMethods.createDir(Filenames.PORT_SCAN_DIR);
		PortScanner.filterNoPayloadPcaps(Filenames.pcapFiles, Filenames.PORT_SCAN_DIR);
		PortScanner.filterNoPayloadFromAttacker(Filenames.pcapFiles, Filenames.PORT_SCAN_DIR);

		// Move all completed sessions to a different directory
		FolderMethods.createDir(Filenames.COMPLETED_SESSIONS_DIR);
		SegregateTCPFlows.moveCompletedFlows(Filenames.pcapFiles, Filenames.COMPLETED_SESSIONS_DIR);

		// Move all incomplete sessions to a different directory
		FolderMethods.createDir(Filenames.INCOMPLETED_SESSIONS_DIR);
		FolderMethods.moveFiles(Filenames.pcapFiles, Filenames.INCOMPLETED_SESSIONS_DIR);
	}

}
