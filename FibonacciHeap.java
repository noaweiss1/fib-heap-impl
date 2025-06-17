import java.util.ArrayList;
import java.util.List;

/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
	public HeapNode min;
	private int c;
	private List<HeapNode> treeList = new ArrayList<>();
	private int linksCount; //counter for links
	private int cutsCount; //counter of cuts
	private int heapSize;
	
	/**
	 *
	 * Constructor to initialize an empty heap.
	 * pre: c >= 2.
	 *
	 */
	public FibonacciHeap(int c)
	{
		this.c = c;
		this.linksCount = 0;
		this.cutsCount = 0;
		this.heapSize = 0;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) 
	{    
		HeapNode node = new HeapNode(key, info);
		this.treeList.add(node);
		if(node.key < this.findMin().key){
			this.min = node;
		}
		return node; // should be replaced by student code
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin()
	{
		if(treeList.size() == 0){
			return null; //if heap is empty
		}
		return min; 
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin()
	{
		return 46; // should be replaced by student code

	}

	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap.
	 * Return the number of cuts.
	 * 
	 */
	public int decreaseKey(HeapNode x, int diff) 
	{    
		return 46; // should be replaced by student code
	}

	/**
	 * 
	 * Delete the x from the heap.
	 * Return the number of links.
	 *
	 */
	public int delete(HeapNode x) 
	{    
		if(x.key > 0){ //to handle edge case of a very big key for x
			this.decreaseKey(x, x.key);
		}
		this.decreaseKey(x, Integer.MAX_VALUE); //ensure x will be the new min
		int links = this.deleteMin();

		return links;
	}


	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks()
	{
		return this.linksCount; 
	}


	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts()
	{
		return this.cutsCount;
	}


	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2)
	{
		this.treeList.addAll(heap2._getTreeList());
		if(heap2.findMin().key < this.min.key){
			this.min = heap2.findMin();
		}   		
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return this.heapSize;
	}


	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		return this.treeList.size(); 
	}


	/**
	 * 
	 * 
	 * Return a list of all trees in the heap
	 * 
	 */
	public List<HeapNode> _getTreeList(){
		return this.treeList;
	}


	/**
	 * Class implementing a node in a Fibonacci Heap.
	 *  
	 */
	public static class HeapNode{
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public int childrenLost;

		public HeapNode(int key, String info){
			this.key = key;
			this.info = info;
			this.rank = 0; 
			this.childrenLost = 0;
		}
	}
}
