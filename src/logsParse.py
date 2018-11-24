import os 
import sys

def joinLogs(source, dest):
	folder = source
	i = 33
	file = "kippo.log."

	f = open(dest, "a")

	while i>=0:
		filepath = os.path.join(folder, file+str(i))
		with open(filepath) as fp:
			contents = fp.read()
			f.write(contents)
		i-=1


def main():
	source = sys.argv[1]
	dest = sys.argv[2]
	joinLogs(source, dest)

if __name__ == '__main__':
	main()
