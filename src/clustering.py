#### Script to carry out the clustering ####

from scipy.cluster.hierarchy import linkage, fcluster
import csv

# File containing the dataset
filename = "../subflow_dataset.csv"

# Thresholds for the classes alpha and beta
th_alpha = 100
th_beta = 50

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
				X_beta.append(line[2:-1])
	return X_alpha, X_beta

# Get the centroids of the formed clusters 
def get_centroids(T):
	
	# Sum the vectors in each cluster
	lens = {}      # will contain the lengths for each cluster
	centroids = {} # will contain the centroids of each cluster
	
	for idx,clno in enumerate(T):
	    centroids.setdefault(clno,np.zeros(D)) 
	    centroids[clno] += features[idx,:]
	    lens.setdefault(clno,0)
	    lens[clno] += 1
	
	# Divide by number of observations in each cluster to get the centroid
	for clno in centroids:
	    centroids[clno] /= float(lens[clno])

	return centroids

def main():

	# Get the two sets to cluster
	X_alpha, X_beta = read_file(filename)
	print(len(X_alpha), len(X_beta))

	# Perform hierarchical Ward clustering using Euclidean distance 
	Y_alpha = fcluster(linkage(X_alpha, method='ward', metric='euclidean'), th_alpha)
	Y_beta = fcluster(linkage(X_beta, method='ward', metric='euclidean'), th_beta)

	print(len(Y_alpha), len(Y_beta))

	print(Yalpha[:20])
	
	# Final step: how to make Z_alpha and Z_beta?

	# code to store the cluster models in a file 


if __name__ == "__main__":
	main()