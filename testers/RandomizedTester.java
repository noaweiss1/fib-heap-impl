import java.util.*;

public class RandomizedTester {

    private static Random rand = new Random();

    public static void main(String[] args) {
        runTest(100);
        System.out.println("done");
    }

    public static String runTest(int k) {
        FibonacciHeap heap = new FibonacciHeap(2);
        ArrayList<FibonacciHeap.HeapNode> liveNodes = new ArrayList<>();
        Map<FibonacciHeap.HeapNode, String> nodeNames = new HashMap<>();
        StringBuilder log = new StringBuilder();

        int nodeCounter = 0;

        try {
            for (int i = 0; i < k; i++) {
                double op = rand.nextDouble();

                if (op < 0.5 || liveNodes.isEmpty() || heap.size() == 0) {
                    // Insert
                    int key = rand.nextInt(1_000_000) + 1;
                    String varName = "node_" + nodeCounter++;
                    FibonacciHeap.HeapNode node = heap.insert(key, null);
                    liveNodes.add(node);
                    nodeNames.put(node, varName);
                    log.append("FibonacciHeap.HeapNode ").append(varName)
                       .append(" = heap.insert(").append(key).append(", null);\n");
                } else {
                    double subOp = rand.nextDouble();
                    if (subOp < 0.33 && heap.size() > 0) {
                        FibonacciHeap.HeapNode min = heap.findMin();
                        log.append("heap.deleteMin();\n");
                        heap.deleteMin();
                        liveNodes.remove(min);
                        nodeNames.remove(min);
                    } else if (subOp < 0.66 && !liveNodes.isEmpty()) {
                        int index = rand.nextInt(liveNodes.size());
                        FibonacciHeap.HeapNode node = liveNodes.remove(index);
                        log.append("heap.delete(").append(nodeNames.get(node)).append(");\n");
                        heap.delete(node);
                        nodeNames.remove(node);
                    } else if (!liveNodes.isEmpty()) {
                        int index = rand.nextInt(liveNodes.size());
                        FibonacciHeap.HeapNode node = liveNodes.get(index);
                        if (node.key > 1) {
                            int dec = rand.nextInt(node.key - 1) + 1;
                            log.append("heap.decreaseKey(").append(nodeNames.get(node))
                               .append(", ").append(dec).append(");\n");
                            heap.decreaseKey(node, dec);
                        }
                    }
                }

                if (heap.size() != liveNodes.size()) {
                    throw new RuntimeException("Heap size mismatch: heap.size() = " + heap.size() +
                            ", liveNodes = " + liveNodes.size());
                }
            }

            // Final logs
            log.append("\nSystem.out.println(\"✅ Test complete\");\n");
            log.append("System.out.println(\"Final size: \" + heap.size());\n");
            log.append("System.out.println(\"Min: \" + (heap.findMin() != null ? heap.findMin().key : \"null\"));\n");
            log.append("System.out.println(\"Links: \" + heap.totalLinks());\n");
            log.append("System.out.println(\"Cuts: \" + heap.totalCuts());\n");
            log.append("System.out.println(\"Tree count: \" + heap.numTrees());\n");

            // Tree count check
            int rootCount = countRootList(heap);
            log.append("System.out.println(\"Actual trees in root list: \" + ").append(rootCount).append(");\n");
            if (rootCount != heap.numTrees()) {
                throw new RuntimeException("❌ Tree count mismatch: heap.numTrees() = " +
                        heap.numTrees() + ", counted = " + rootCount);
            }

            // Rank check
            validateRanks(heap.findMin(), new HashSet<>());

            System.out.println(log);
            return log.toString();

        } catch (Exception | AssertionError e) {
            System.err.println("❌ Error during testing!");
            e.printStackTrace();
            System.err.println("\n📜 Reproducible Java commands:\n" + log);
            return log.toString();
        }
    }

    private static int countRootList(FibonacciHeap heap) {
        int count = heap._getTreeList().size();
        return count;
    }

    private static int validateRanks(FibonacciHeap.HeapNode node, Set<FibonacciHeap.HeapNode> visited) {
        if (node == null || visited.contains(node)) return 0;
        visited.add(node);
        int childCount = 0;
        FibonacciHeap.HeapNode child = node.child;
        if (child != null) {
            childCount = 1;
            FibonacciHeap.HeapNode curr = child.next;
            while (curr != null) {
                childCount++;
                curr = curr.next;
            }

            if (childCount != node.rank) {
                throw new RuntimeException("❌ Rank mismatch at node with key " + node.key +
                        ": rank = " + node.rank + ", actual children = " + childCount);
            }

            // Recurse on children
            curr = child;
            do {
                validateRanks(curr, visited);
                curr = curr.next;
            } while (curr != null);
        } else if (node.rank != 0) {
            throw new RuntimeException("❌ Rank mismatch at node with key " + node.key +
                    ": rank = " + node.rank + ", but has no children");
        }

        return childCount;
    }
}
