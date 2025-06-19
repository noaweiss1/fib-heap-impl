public class CValueTester {
    public static void main(String[] args) {
        int[] cValues;
        if (args.length > 0) {
            cValues = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                cValues[i] = Integer.parseInt(args[i]);
            }
        } else {
            cValues = new int[] {2, 3, 4};
        }

        for (int c : cValues) {
            System.out.println("\n>>> Testing FibonacciHeap with c=" + c);
            runTests(c);
        }
        System.out.println("\nAll c-values tested successfully!");
    }

    private static void runTests(int c) {
        // 1) insert & findMin
        FibonacciHeap h1 = new FibonacciHeap(c);
        h1.insert(10, "ten");
        h1.insert(5, "five");
        h1.insert(7, "seven");
        if (h1.findMin().key != 5) {
            throw new RuntimeException("insert/findMin failed for c=" + c);
        }
        System.out.println("insert/findMin OK");

        // 2) size & numTrees
        if (h1.size() != 3 || h1.numTrees() != 3) {
            throw new RuntimeException("size/numTrees failed for c=" + c);
        }
        System.out.println("size/numTrees OK");

        // 3) deleteMin
        int links = h1.deleteMin();
        if (h1.size() != 2 || h1.findMin().key != 7) {
            throw new RuntimeException("deleteMin failed for c=" + c);
        }
        System.out.println("deleteMin OK (links=" + links + ")");

        // 4) decreaseKey & cuts
        FibonacciHeap h2 = new FibonacciHeap(c);
        FibonacciHeap.HeapNode na = h2.insert(1, "one");
        FibonacciHeap.HeapNode nb = h2.insert(2, "two");
        FibonacciHeap.HeapNode nc = h2.insert(3, "three");
        h2.deleteMin();                  // links 2 & 3 into one tree
        int cuts = h2.decreaseKey(nc, 2); // decrease 3->1, should cut once
        if (cuts != 1 || h2.findMin().key != 1) {
            throw new RuntimeException("decreaseKey failed for c=" + c);
        }
        System.out.println("decreaseKey OK (cuts=" + cuts + ")");

        // 5) delete(x)
        FibonacciHeap hDel = new FibonacciHeap(c);
        FibonacciHeap.HeapNode dx1 = hDel.insert(4, "four");
        FibonacciHeap.HeapNode dx2 = hDel.insert(6, "six");
        int linksDel = hDel.delete(dx2);
        if (hDel.size() != 1 || hDel.findMin().key != 4) {
            throw new RuntimeException("delete(x) failed for c=" + c);
        }
        System.out.println("delete(x) OK (links=" + linksDel + ")");

        // 6) totalLinks/totalCuts initial
        FibonacciHeap h0 = new FibonacciHeap(c);
        if (h0.totalLinks() != 0 || h0.totalCuts() != 0) {
            throw new RuntimeException("totalLinks/totalCuts initial failed for c=" + c);
        }
        System.out.println("totalLinks/totalCuts initial OK");

        // 7) meld
        FibonacciHeap ha = new FibonacciHeap(c);
        FibonacciHeap hb = new FibonacciHeap(c);
        ha.insert(100, "100");
        hb.insert(50,  "50");
        hb.insert(150, "150");
        ha.meld(hb);
        if (hb.size() != 0 || hb.findMin() != null) {
            throw new RuntimeException("meld hb clear failed for c=" + c);
        }
        System.out.println("meld hb clear OK");
        if (ha.size() != 3 || ha.findMin().key != 50) {
            throw new RuntimeException("meld failed for c=" + c);
        }
        System.out.println("meld OK");

        System.out.println("c=" + c + " tests passed!");
    }
}
