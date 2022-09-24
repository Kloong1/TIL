package no6;

public class Solution {
    public static void main(String[] args) {
        Solution s = new Solution();
        String result = s.solution(3, 4, 2, 3, 3, 1, 5);
        System.out.println("result = " + result);
        result = s.solution(2, 2, 1, 1, 2, 2, 2);
        System.out.println("result = " + result);
    }

    public String solution(int n, int m, int x, int y, int r, int c, int k) {
        int dist = getManDist(x, y, r, c);

        if ((dist + k) % 2 == 1 || dist > k) {
            return "impossible";
        }

        StringBuilder stringBuilder = new StringBuilder();

        while (k > dist) {
            if (x < n) {
                x++;
                stringBuilder.append("d");
            } else if (y > 1) {
                y--;
                stringBuilder.append("l");
            } else if (y < m) {
                y++;
                stringBuilder.append("r");
            } else {
                x--;
                stringBuilder.append("u");
            }
            k--;
            dist = getManDist(x, y, r, c);
        }

        int rowDist = r - x;
        int colDist = c - y;

        if (rowDist > 0) {
            for (int i = 0; i < rowDist; i++) {
                stringBuilder.append("d");
            }
        }

        if (colDist < 0) {
            for (int i = 0; i < -colDist; i++) {
                stringBuilder.append("l");
            }
        }

        if (colDist > 0) {
            for (int i = 0; i < colDist; i++) {
                stringBuilder.append("r");
            }
        }

        if (rowDist < 0) {
            for (int i = 0; i < -rowDist; i++) {
                stringBuilder.append("u");
            }
        }

        return stringBuilder.toString();
    }

    private int getManDist(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

}
