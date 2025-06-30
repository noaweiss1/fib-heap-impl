/**
 * Submitters:
 * Noa Weiss - 324065275 - noaweiss3
 * Dan Ben David - 322578469 - danbendavid1
 */
/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
	public HeapNode min; // pointer to the minimum root
	private int c; // threshold for cascading cuts
	private int rootCount; // number of trees in the root list
	private int linksCount; // total number of performed links
	private int cutsCount; // total number of performed cuts
	private int heapSize; // current number of nodes
	
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
		this.rootCount = 0;
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
		_insertRoot(node);
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
		return this.min;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin()
	{
		java.util.ArrayList<Object> buckets = new java.util.ArrayList<>();

		if (this.min == null) {
			return 0;
		}

		HeapNode oldMin = this.min;

		// Phase 1: removing the minimum node from the root list
		if (oldMin.next == oldMin) {
			this.min = null;
		} else {
			oldMin.prev.next = oldMin.next;
			oldMin.next.prev = oldMin.prev;
			this.min = oldMin.next;
		}
		this.rootCount--;
		this.heapSize--;

		// Phase 2: promote all of its children to the list of roots
		HeapNode child = oldMin.child;
		while (child != null) {
			HeapNode nextChild = child.next;
			child.parent = null;
			child.prev = null;
			child.next = null;
			_insertRoot(child);
			child = nextChild;
		}

		int linksDone = 0;// counts how many link operations were performed

		// Phase 3: successive linking of trees using the buckets array
		if (this.min != null) {
			HeapNode start = this.min;
			this.min = null;
			this.rootCount = 0;
			HeapNode curr = start;
			do {
				HeapNode next = curr.next;
				curr.prev = null;
				curr.next = null;
				int cellIdx = curr.rank;
				while (cellIdx >= buckets.size()) {
					buckets.add(null);
				}
				while (buckets.get(cellIdx) != null) {
					HeapNode other = (HeapNode) buckets.get(cellIdx);
					buckets.set(cellIdx, null);
					curr = _link(curr, other);
					linksDone++;// linksDone accumulates one for each link
					cellIdx = curr.rank;
					while (cellIdx >= buckets.size()) {
						buckets.add(null);
					}
				}
				buckets.set(cellIdx, curr);
				curr = next;
			} while (curr != start);
		}

		// Phase 4: reinsert the consolidated trees back into the heap
		for (Object obj : buckets) {
			HeapNode node = (HeapNode) obj;
			if (node != null) {
				_insertRoot(node);
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

		// if x is a root, its parent is null and we can return immediately
		if(x.parent == null){ 
			if(x.key<this.findMin().key){
				this.min = x;
			}
			return cuts; // nothing to cut
		}

		// if the new key is still not smaller than its parent, heap order is preserved
		if(x.key >= x.parent.key){ 
			return cuts; // no cut required
		}

		// else: cut x from its parent and possibly perform cascading cuts
		HeapNode currDad = x.parent;
		_cut(x); // detach x and create a new root
		if(x.key < this.min.key){
			this.min = x; // update heap minimum after the cut
		}
		cuts++;
		// continue cutting ancestors while the parent lost at least c children
		while(currDad.parent != null && currDad.childrenLost >= this.c){ 
			HeapNode node = currDad;
			currDad = node.parent;
			_cut(node); // promote the parent to the root list
			if(node.key < this.min.key){
				this.min = node;
			}
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
		if (heap2.min == null) {
			return;
		}

		if (this.min == null) {
			this.min = heap2.min;
			this.rootCount = heap2.rootCount;
		} else {
			// connect root lists
			HeapNode thisNext = this.min.next;
			HeapNode otherPrev = heap2.min.prev;

			this.min.next = heap2.min;
			heap2.min.prev = this.min;

			otherPrev.next = thisNext;
			thisNext.prev = otherPrev;

			if (heap2.min.key < this.min.key) {
				this.min = heap2.min;
			}
			this.rootCount += heap2.rootCount;
		}

		this.heapSize += heap2.heapSize;
		this.linksCount += heap2.linksCount;
		this.cutsCount += heap2.cutsCount;

		heap2.min = null;
		heap2.rootCount = 0;
		heap2.heapSize = 0;
		heap2.linksCount = 0;
		heap2.cutsCount = 0;
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
		return this.rootCount;
	}


	//helper funcs:

	/**
	 * Insert a node into the root list.
	 */
	private void _insertRoot(HeapNode node)
	{
		if (this.min == null) {
			node.next = node;
			node.prev = node;
			this.min = node;
		} else {
			node.next = this.min.next;
			node.prev = this.min;
			this.min.next.prev = node;
			this.min.next = node;
			if (node.key < this.min.key) {
				this.min = node;
			}
		}
		this.rootCount++;
	}


	/**
	 * Merge two trees according to the heap order and return the new root.
	 */

	public HeapNode _link(HeapNode node1, HeapNode node2){
		// node1 becomes parent and node2 is inserted as its first child
		if(node1.key < node2.key){
			HeapNode prevChild = node1.child;
			node1.child = node2;
			node2.parent = node1;
			node2.prev = null;
			node2.childrenLost = 0;
			node2.next = prevChild;
			if(prevChild != null){
				prevChild.prev = node2;
			}
			node1.rank++;
			return node1;
		}
		else{// node2 becomes parent and node1 becomes its first child
			HeapNode prevChild = node2.child;
			node2.child = node1;
			node1.parent = node2;
			node1.prev = null;
			node1.childrenLost = 0;
			node1.next = prevChild;
			if(prevChild != null){
				prevChild.prev = node1;
			}
			node2.rank++;
			return node2;
		}

	}


	 /**
	 * Detach the node from its parent's child list, insert it into the root
	 * list, and decrease the parent's rank while increasing its
	 * childrenLost counter.
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
		_insertRoot(node);
		node.childrenLost = 0;
		//if node was only child:
		if(prevDad.child.equals(node) && prevNext == null){
			prevDad.child = null;
		}
		else{
			//bypass node (remove it from "children list")
			if(prevPrev != null){ // if node had prev
				if(prevNext != null){ // if node had next as well, it will be prev's new next (bypass node)
					prevPrev.next = prevNext;
				}
				else{ //node didnt have next
					prevPrev.next = null; // prev is now the last child
				}
			}
			if(prevNext != null){ // if node had next
				if(prevPrev != null){ // if node had prev as well, it will be next's new prev (bypass node)
					prevNext.prev = prevPrev;
				}
				else{ //node didnt have prev
					prevNext.prev = null; //prev is now the first child
					prevDad.child = prevNext; //prev is the new first child so it needs to dad's child
				}
			}
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
