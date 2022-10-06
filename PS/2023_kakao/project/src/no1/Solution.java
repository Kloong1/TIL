package no1;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class Solution {
    public static void main(String[] args) throws ParseException {
        Solution solution = new Solution();
        solution.solution("2001.01.08", new String[0], new String[0]);
    }

    public int[] solution(String todayStr, String[] terms, String[] privacies) throws ParseException {
        ArrayList<Integer> arrayList = new ArrayList<>();

        HashMap<String, Integer> termsMap = new HashMap<>();
        convertTerms(termsMap, terms);

        int todayDayCnt = convertDateStrToInt(todayStr);

        int num = 1;
        for (String privacy : privacies) {
            String[] dateTerm = privacy.split(" ");
            int privacyDayCnt = convertDateStrToInt(dateTerm[0]);

            privacyDayCnt += termsMap.get(dateTerm[1]) * 28;
            System.out.println(todayDayCnt);
            System.out.println(privacyDayCnt);
            System.out.println();
            if (privacyDayCnt >= todayDayCnt) {
                arrayList.add(num);
            }
            num++;
        }

        Collections.sort(arrayList);

        int[] answer = new int[arrayList.size()];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = arrayList.get(i);
        }

        return answer;
    }

    private int convertDateStrToInt(String dateStr) {
        String[] yearMonthDay = dateStr.split("\\.");


        int year = Integer.parseInt(yearMonthDay[0]);
        int month = Integer.parseInt(yearMonthDay[1]);
        int day = Integer.parseInt(yearMonthDay[2]);

        year -= 2000;
        month -= 1;

        return day + (month + (year * 12)) * 28;
    }

    private void convertTerms(HashMap<String, Integer> termsMap, String[] terms) {
        for (String term : terms) {
            String[] termAndExpire = term.split(" ");
            termsMap.put(termAndExpire[0], Integer.parseInt(termAndExpire[1]));
        }
    }
}