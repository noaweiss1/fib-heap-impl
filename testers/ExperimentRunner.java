public class ExperimentRunner {
    private static final int M = 464646;
    private static final int RUNS = 20;
    private static final int REMAINING = 46;

    public static void main(String[] args) {
        int[] cValues = {2, 3, 4, 10, 20, 100, 1000, 5000};
        for (int c : cValues) {
            runExperiments(c);
        }
    }

    private static void runExperiments(int c) {
        long totalTime1 = 0;
        long totalLinks1 = 0;
        long totalCuts1 = 0;
        int trees1 = 0;
        int size1 = 0;

        long totalTime2 = 0;
        long totalLinks2 = 0;
        long totalCuts2 = 0;
        int trees2 = 0;
        int size2 = 0;

        for (int i = 0; i < RUNS; i++) {
            Result r1 = experiment1(c);
            totalTime1 += r1.timeMs;
            totalLinks1 += r1.links;
            totalCuts1 += r1.cuts;
            trees1 += r1.trees;
            size1 += r1.size;

            Result r2 = experiment2(c);
            totalTime2 += r2.timeMs;
            totalLinks2 += r2.links;
            totalCuts2 += r2.cuts;
            trees2 += r2.trees;
            size2 += r2.size;
        }

        System.out.println("\n=== c=" + c + " ===");
        System.out.println("Experiment 1 averages over " + RUNS + " runs:");
        System.out.println("time(ms)=" + (totalTime1 / RUNS) +
                           ", links=" + (totalLinks1 / RUNS) +
                           ", cuts=" + (totalCuts1 / RUNS) +
                           ", trees=" + (trees1 / RUNS) +
                           ", size=" + (size1 / RUNS));

        System.out.println("Experiment 2 averages over " + RUNS + " runs:");
        System.out.println("time(ms)=" + (totalTime2 / RUNS) +
                           ", links=" + (totalLinks2 / RUNS) +
                           ", cuts=" + (totalCuts2 / RUNS) +
                           ", trees=" + (trees2 / RUNS) +
                           ", size=" + (size2 / RUNS));
    }

    private static Result experiment1(int c) {
        FibonacciHeap heap = new FibonacciHeap(c);
        FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[M + 1];
        int[] order = randomPermutation(M);

        long start = System.currentTimeMillis();
        for (int key : order) {
            nodes[key] = heap.insert(key, null);
        }

        heap.deleteMin();
        nodes[1] = null;
        int max = M;
        while (heap.size() > REMAINING) {
            while (max > 0 && nodes[max] == null) {
                max--;
            }
            FibonacciHeap.HeapNode node = nodes[max];
            if (node != null) {
                heap.delete(node);
                nodes[max] = null;
            }
        }

        long end = System.currentTimeMillis();
        return new Result(end - start, heap.totalLinks(), heap.totalCuts(), heap.numTrees(), heap.size());
    }

    private static Result experiment2(int c) {
        FibonacciHeap heap = new FibonacciHeap(c);
        FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[M + 1];
        int[] order = randomPermutation(M);

        long start = System.currentTimeMillis();
        for (int key : order) {
            nodes[key] = heap.insert(key, null);
        }

        heap.deleteMin();
        nodes[1] = null;
        int positive = heap.size();
        int max = M;
        while (positive > REMAINING) {
            while (max > 0 && (nodes[max] == null || nodes[max].key == 0)) {
                max--;
            }
            FibonacciHeap.HeapNode node = nodes[max];
            if (node != null && node.key > 0) {
                heap.decreaseKey(node, node.key);
                positive--;
            }
        }
        heap.deleteMin();
        long end = System.currentTimeMillis();
        return new Result(end - start, heap.totalLinks(), heap.totalCuts(), heap.numTrees(), heap.size());
    }

    private static int[] randomPermutation(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i + 1;
        }
        java.util.Random rand = new java.util.Random();
        for (int i = n - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        return arr;
    }

    private static class Result {
        long timeMs;
        long links;
        long cuts;
        int trees;
        int size;

        Result(long t, long l, long c, int tr, int s) {
            this.timeMs = t;
            this.links = l;
            this.cuts = c;
            this.trees = tr;
            this.size = s;
        }
    }
}
