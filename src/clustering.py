#### Script to carry out the clustering ####

from scipy.cluster.hierarchy import linkage, fcluster
import numpy as np
import csv

# File containing the dataset
filename = "../subflow_dataset.csv"

# Thresholds for the classes alpha and beta
th_alpha = 100
th_beta = 50
u = 1
v = 1

# Function to read the dataset and compute the sets X_alpha and X_beta
def read_file(filename):
	# Empty lists
	X_alpha = []
	X_beta = []

	# Open file
	with open(filename) as f:
		# Read in CSV format
		reader = csv.reader(f)

		# For every line 
		for line in reader:

			# Determine if example is of X_alpha or X_beta
			if line[-1] == "transport" or line[-1] == "transition point 1":
				X_alpha.append(line[2:-1])
			else:
				# print(line)
				X_beta.append(line[2:-1])
	return X_alpha, X_beta

# Calculate centroid
def centroid(a, b):
	c = []
	i = 0
	D = u+v+2
	for i in range(D):
		x = (float(a[i]) + float(b[i]))/2
		c.append(x)
	return c

# Get the centroids of the formed clusters 
def get_centroids(X, c):
	i = 0
	centroids = {}
	c = c.astype(np.int64)
	for cluster in c:
		if cluster[0] < len(X) and cluster[1] < len(X):
			val = centroid(X[cluster[0]], X[cluster[1]])

		elif cluster[0] < len(X) and cluster[1] >= len(X): 
			val = centroid(X[cluster[0]], centroids[cluster[1]-len(X)])

		elif cluster[0] > len(X) and cluster[1] < len(X): 
			val = centroid(centroids[cluster[0]-len(X)], X[cluster[1]])

		else:
			val = centroid(centroids[cluster[0]-len(X)], centroids[cluster[1]-len(X)])

		centroids[i] = val
		i += 1
		# print(i)

	return centroids 

def each_type_cluster(X):
	Y = linkage(X, method='ward', metric='euclidean')
	print(len(Y))
	centroids = get_centroids(X,Y)
	return centroids

def main():

	# Get the two sets to cluster
	X_alpha, X_beta = read_file(filename)
	print(len(X_alpha), len(X_beta))

	model_file = "../model.txt"
	with open(model_file, 'w') as f:
		writer = csv.writer(f)
		centroids_alpha = each_type_cluster(X_alpha)
		for c in centroids_alpha.keys():
			writer.writerow(centroids_alpha[c])
		f.write("---\n")
		centroids_beta = each_type_cluster(X_beta)
		for c in centroids_beta.keys():
			writer.writerow(centroids_beta[c])

	# code to store the cluster models in a file 


if __name__ == "__main__":
	main()