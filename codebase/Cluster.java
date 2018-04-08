import java.util.*;
// Change all ints to floats!
class Cluster
{
	ArrayList<Cluster> children;
    ArrayList<Integer> leaves; // subflows

    public Cluster(ArrayList<Integer> sf)
    {
    	leaves = new ArrayList<Integer>();
    	for(int i : sf)
        	leaves.add(i);

        children = new ArrayList<Cluster>();
    }

    void printCluster()
    {
    	for(int i: this.leaves)
    		System.out.print(i+" ");
    	System.out.print("\n");
    }
}