import socket
import dpkt
import sys
import os
import datetime

directory = sys.argv[1]
for filename in os.listdir(directory):

	filepath = os.path.join(directory, filename)
	pcapReader = dpkt.pcap.Reader(file(filepath, "rb"))
	newfile = "datatxt/"+filename+".txt"
	packets = open(newfile, "w")

	i=0

	for ts, data in pcapReader:
		i+=1
		ether = dpkt.ethernet.Ethernet(data)
		if ether.type != dpkt.ethernet.ETH_TYPE_IP: raise
		ip = ether.data
		src = socket.inet_ntoa(ip.src)
		dst = socket.inet_ntoa(ip.dst)
		tcp = ip.data
		s_port = tcp.sport
		d_port = tcp.dport
		if d_port == 22 and len(tcp.data) > 0:
			incoming=True
			print "Packet: %d, Incoming: %r\nSource IP: %s, Destination IP: %s\nSource port no: %d, Destination port: %d\nTimestamp: %s, Size: %d\n" % (i,incoming, src, dst, s_port, d_port, str(datetime.datetime.utcfromtimestamp(ts)), len(data))
			packets.write("%d,%r,%s,%s,%d,%d,%s,%d\n" % (i,incoming, src, dst, s_port, d_port, str(datetime.datetime.utcfromtimestamp(ts)), len(data)))
		elif s_port == 22 and len(tcp.data) > 0:
			incoming=False
			print "Packet: %d, Incoming: %r\nSource IP: %s, Destination IP: %s\nSource port no: %d, Destination port: %d\nTimestamp: %s, Size: %d\n" % (i,incoming, src, dst, s_port, d_port, str(datetime.datetime.utcfromtimestamp(ts)), len(data))
			packets.write("%d,%r,%s,%s,%d,%d,%s,%d\n" % (i,incoming, src, dst, s_port, d_port, str(datetime.datetime.utcfromtimestamp(ts)), len(data)))
	packets.close()


