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
		if(node.key < this.findMin().key){ //might need to update min
			this.min = node;
		}
		this.heapSize++;
		return node;
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
		//remove the min from tree list
		HeapNode prevMin = this.min;
		int index = treeList.indexOf(prevMin);
		treeList.remove(index);

		//iterate over all children and add them to tree list
		if(!(prevMin.child.equals(null))){ //if min had children
			HeapNode currChild = prevMin.child; 
			treeList.add(currChild);

			while(!(currChild.next.equals(null))){
				currChild = currChild.next;
				treeList.add(currChild);
			}
		}

		//successive linking
		int logn = (int)Math.floor(Math.log(this.size())) +1;
		HeapNode[] baskets = new HeapNode[logn]; //helper array to 
		int linksDone = 0;
		for(int i=0 ; i<this.numTrees() ; i++){ //iterate over all roots
			HeapNode node = treeList.get(i);
			int cellIdx = node.rank;
			while(!(baskets[cellIdx].equals(null))){ //if there already is a tree in this cell (same rank), link them and check next
				HeapNode other = baskets[cellIdx];
				baskets[cellIdx] = null;
				node = _link(node,other); //link 2 nodes of same rank
				linksDone++;
				cellIdx++;
			}
			baskets[cellIdx] = node;
		}

		//update tree list
		this.treeList = new ArrayList<>();
		for(int i=0; i<logn ; i++){ // iterate over the array and add all trees to tree list
			if (!(baskets[i].equals(null))){
				this.treeList.add(baskets[i]);
			}
		}

		this.linksCount += linksDone;
		return linksDone; 
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
		int cuts = 0;
		x.key -= diff;
		// if has no dad, was a root
		if(x.parent.equals(null)){ 
			if(x.key<this.findMin().key){
				this.min = x;
			}
			return cuts;
		}
		// if the new key doesn't break heap rule, no cuts needs to be done
		if(x.key >= x.parent.key){ 
			return cuts;
		}

		//else: cut x, and maybe need cascading cuts
		HeapNode currDad = x.parent;
		_cut(x); //cut the node and create a new tree, update all relevent fields
		cuts++;
		//while the dad has a dad (so it can be cut) AND lost c children (so it needs to be cut)
		while(!(currDad.parent.equals(null)) && currDad.childrenLost == this.c){ 
			HeapNode node = currDad;
			currDad = node.parent;
			_cut(node); //cut the node and create a new tree, update all relevent fields 
			cuts++;

		}

		this.cutsCount += cuts;
		return cuts; 
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
		int cuts = this.decreaseKey(x, Integer.MAX_VALUE); //ensure x will be the new min
		int links = this.deleteMin();
		this.linksCount += links; //update total links count 
		this.cutsCount += cuts; //update total cuts count 

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

	//helper funcs:

	/**
	 * 
	 * Return a list of all trees in the heap
	 * 
	 */
	public List<HeapNode> _getTreeList(){
		return this.treeList;
	}

	/**
	 * 
	 * link 2 trees to a new tree (based on heap rule)
	 * 
	 */

	 public HeapNode _link(HeapNode node1, HeapNode node2){
		//if node1 has smaller key than node2, it will be the root and node2 will become its child
		if(node1.key < node2.key){
			HeapNode prevChild = node1.child;
			node1.child = node2;
			node2.parent = node1;
			node2.next = prevChild;
			prevChild.prev = node2;
			node1.rank++;
			return node1;
		}
		else{//node2's key <= node1's key
			HeapNode prevChild = node2.child;
			node2.child = node1;
			node1.parent = node2;
			node1.next = prevChild;
			prevChild.prev = node1;
			node2.rank++;
			return node2;
		}
		
	 }


	 /**
	  * 
	  * cut the node: remove it from its parnt children list and insert it to tree list
	  * update all relevat fields of: the node, its parent and its siblings
	  *
	  */
	 public void _cut(HeapNode node){
		//keep pointers:
		HeapNode prevPrev = node.prev;
		HeapNode prevDad = node.parent;
		HeapNode prevNext = node.next;
		//the node we cut now has no parent and no siblings:
		node.parent = null;
		node.next = null;
		node.prev = null;
		//node is now a new tree:
		this.treeList.add(node);
		//bypass node (remove it from "children list")
		if(!(prevPrev.equals(null))){ // if node had prev
			if(!(prevNext.equals(null))){ // if node had next as well, it will be prev's new next (bypass node)
				prevPrev.next = prevNext;
			}
			else{ //node didnt have next
				prevPrev.next = null; // prev is now the last child
			}
		}
		if(!(prevNext.equals(null))){ // if node had next
			if(!(prevPrev.equals(null))){ // if node had prev as well, it will be next's new prev (bypass node)
				prevNext.prev = prevPrev;
			}
			else{ //node didnt have prev
				prevNext.prev = null; //prev is now the first child
				prevDad.child = prevNext; //prev is the new first child so it needs to dad's child
			}
		}
		//if node was only child:
		if(prevDad.child.equals(node) && prevNext.equals(null)){
			prevDad.child = null;
		}

		//handle dad: update its rank and childrenLost:
		prevDad.rank--;
		prevDad.childrenLost++;

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
