import java.util.*;
class Cluster
{
	ArrayList<Cluster> children;
    ArrayList<ArrayList<Integer>> leaves; // subject to change

    Distance distance = new Distance();

    public Cluster(ArrayList<ArrayList<Integer>> sf)
    {
    	for(ArrayList<Integer> i : sf)
        	leaves.add(i);
    }
}