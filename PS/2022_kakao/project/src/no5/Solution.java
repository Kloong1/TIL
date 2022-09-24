package no5;

import java.util.ArrayList;
import java.util.Arrays;

public class Solution {

    Node[] nodes = new Node[50 * 50 + 1];

    public static void main(String[] args) {
        String[] commands = {"UPDATE 1 1 menu", "UPDATE 1 2 category", "UPDATE 2 1 bibimbap", "UPDATE 2 2 korean", "UPDATE 2 3 rice", "UPDATE 3 1 ramyeon", "UPDATE 3 2 korean", "UPDATE 3 3 noodle", "UPDATE 3 4 instant", "UPDATE 4 1 pasta", "UPDATE 4 2 italian", "UPDATE 4 3 noodle", "MERGE 1 2 1 3", "MERGE 1 3 1 4", "UPDATE korean hansik", "UPDATE 1 3 group", "UNMERGE 1 4", "PRINT 1 3", "PRINT 1 4"};
        Solution s = new Solution();
        String[] result = s.solution(commands);
        System.out.println(Arrays.toString(result));
    }

    public String[] solution(String[] commands) {
        String[] answer = {};

        for (int i = 1; i <= 50 * 50; i++) {
            nodes[i] = new Node(i);
        }

        ArrayList<String> arrayList = new ArrayList<>();

        for (String command : commands) {
            String[] strings = command.split(" ");
            String cmd = strings[0];
            String[] params = Arrays.copyOfRange(strings, 1, strings.length);

            switch (cmd) {
                case "UPDATE":
                    if (params.length == 3) {
                        update(Integer.parseInt(params[0]), Integer.parseInt(params[1]), params[2]);
                    } else {
                        update(params[0], params[1]);
                    }
                    break;
                case "MERGE":
                    merge(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
                    break;
                case "UNMERGE":
                    unmerge(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
                    break;
                default:
                    String result = print(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
                    arrayList.add(result);
                    break;
            }
        }

        return arrayList.toArray(new String[0]);
    }

    int find(int x) {
        if (nodes[x].parent == x) {
            return x;
        } else {
            return nodes[x].parent = find(nodes[x].parent);
        }
    }

    void update(int r, int c, String val) {
        nodes[find((r - 1) * 50 + c)].val = val;
    }

    void update(String from, String to) {
        for (int i = 1; i < nodes.length; i++) {
            Node root = nodes[find(i)];
            if (root.val != null && root.val.equals(from)) {
                root.val = to;
            }
        }
    }

    String print(int r, int c) {
        if (nodes[find((r - 1) * 50 + c)].val == null) {
            return "EMPTY";
        }
        return nodes[find((r - 1) * 50 + c)].val;
    }

    //union
    void merge(int r1, int c1, int r2, int c2) {
        int x = (r1 - 1) * 50 + c1;
        int y = (r2 - 1) * 50 + c2;

        int rootX = find(x);
        int rootY = find(y);

        if (nodes[rootY].val != null && nodes[rootX].val == null) {
            nodes[rootX].parent = rootY;
        } else {
            nodes[rootY].parent = rootX;
        }
    }

    void unmerge(int r, int c) {
        int x = (r - 1) * 50 + c;
        int root = find(x);
        String val = nodes[root].val;

        ArrayList<Integer> unmergeList = new ArrayList<>(nodes.length);

        for (int i = 1; i < nodes.length; i++) {
            if (nodes[find(i)].parent == root) {
                unmergeList.add(i);
            }
        }

        for (int i : unmergeList) {
            nodes[i].parent = i;
            nodes[i].val = null;
        }

        nodes[x].val = val;
    }
}

class Node {
    int parent;
    String val = null;

    public Node(int parent) {
        this.parent = parent;
    }
}