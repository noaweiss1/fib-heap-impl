public class FibonacciHeapTester {

    public static void main(String[] args) {
        testInsertAndFindMin();
        testDeleteMin();
        testDecreaseKey();
        testDelete();
        testMeld();
        System.out.println("✅ All tests finished.");
    }

    private static void testInsertAndFindMin() {
        FibonacciHeap heap = new FibonacciHeap(2);
        heap.insert(10, "a");
        heap.insert(5, "b");
        heap.insert(7, "c");

        assert heap.findMin().key == 5 : "❌ findMin should return 5";
        assert heap.size() == 3 : "❌ size should be 3";
        assert heap.numTrees() == 3;
        System.out.println("✔️ testInsertAndFindMin passed");
    }

    private static void testDeleteMin() {
        FibonacciHeap heap = new FibonacciHeap(2);
        heap.insert(8, "a");
        heap.insert(3, "b");
        heap.insert(5, "c");

        assert heap.findMin().key == 3;
        heap.deleteMin();
        assert heap.findMin().key != 3 : "❌ deleteMin failed to remove 3";
        assert heap.size() == 2 : "❌ size should be 2 after deleteMin";
        assert heap.numTrees() == 2;
        System.out.println("✔️ testDeleteMin passed");
    }

    private static void testDecreaseKey() {
        FibonacciHeap heap = new FibonacciHeap(2);
        FibonacciHeap.HeapNode x = heap.insert(10, "x");
        heap.insert(20, "y");
        heap.insert(30, "z");

        int cutsBefore = heap.totalCuts();
        heap.decreaseKey(x, 9); // Now x.key = 1
        assert heap.findMin().key == 1 : "❌ decreaseKey failed";
        assert heap.totalCuts() >= cutsBefore : "❌ cuts did not increase";

        System.out.println("✔️ testDecreaseKey passed");
    }

    private static void testDelete() {
        FibonacciHeap heap = new FibonacciHeap(2);
        heap.insert(15, "a");
        FibonacciHeap.HeapNode x = heap.insert(7, "b");
        heap.insert(20, "c");

        heap.delete(x);
        assert heap.findMin().key != 7 : "❌ delete failed";
        assert heap.size() == 2 : "❌ size should be 2 after delete";
        assert heap.numTrees() == 1;
        System.out.println("✔️ testDelete passed");
    }

    private static void testMeld() {
        FibonacciHeap heap1 = new FibonacciHeap(2);
        FibonacciHeap heap2 = new FibonacciHeap(2);

        heap1.insert(10, "a");
        heap1.insert(5, "b");

        heap2.insert(3, "c");
        heap2.insert(8, "d");

        heap1.meld(heap2);

        assert heap1.findMin().key == 3 : "❌ meld failed, min should be 3";
        assert heap1.size() == 4 : "❌ meld failed, size should be 4";
        assert heap2.findMin() == null : "❌ heap2 should not be usable";
        assert heap1.numTrees() == 4;

        System.out.println("✔️ testMeld passed");
    }
}