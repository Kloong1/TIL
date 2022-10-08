package no2;

class Solution {
    public static void main(String[] args) {
        Solution s = new Solution();
        s.solution(4, 5, new int[]{1, 0, 3, 1, 2}, new int[]{0, 3, 0, 4, 0});
    }
    public long solution(int cap, int n, int[] deliveries, int[] pickups) {
        long answer = 0;

        int farthestHouse = initFarthestHouse(deliveries, pickups, n);

        int round = 1;
        while (farthestHouse > 0) {
            long delCap = 0;
            long pickCap = 0;
            int tempFarthestHouse = farthestHouse;

            System.out.println("round = " + round);
            System.out.println("farthestHouse = " + farthestHouse);

            for (int i = farthestHouse - 1; i >= 0; i--) {
                if (delCap + deliveries[i] <= cap) {
                    delCap += deliveries[i];
                    deliveries[i] = 0;
                } else {
                    deliveries[i] -= (cap - delCap);
                    delCap = cap;
                }

                if (pickCap + pickups[i] <= cap) {
                    pickCap += pickups[i];
                    pickups[i] = 0;
                } else {
                    pickups[i] -= (cap - pickCap);
                    pickCap = cap;
                }

                if (deliveries[i] == 0 && pickups[i] == 0) {
                    tempFarthestHouse--;
                }

                if (delCap == cap && pickCap == cap || tempFarthestHouse == 0) {
                    break;
                }
            }

            answer += farthestHouse * 2L;
            System.out.println("answer = " + answer);

            farthestHouse = tempFarthestHouse;
            farthestHouse = initFarthestHouse(deliveries, pickups, farthestHouse);
            round++;
        }

        return answer;
    }

    private int initFarthestHouse(int[] deliveries, int[] pickups, int len) {
        int farthestHouse = len;

        for (int i = len - 1; i >= 0; i--) {
            if (deliveries[i] == 0 && pickups[i] == 0) {
                farthestHouse--;
            }
            else
                break;
        }

        return farthestHouse;
    }
}
