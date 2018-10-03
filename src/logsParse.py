import os 

def joinLogs():
	folder = "../Project_Dataset/D1/kippo-logs"
	i = 33
	file = "kippo.log."

	f = open("../joint_logs", "a")

	while i>=0:
		filepath = os.path.join(folder, file+str(i))
		with open(filepath) as fp:
			contents = fp.read()
			f.write(contents)
		i-=1


def main():
	joinLogs()

if __name__ == '__main__':
	main()
