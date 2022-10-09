package grade;

import java.util.List;
import java.util.Map;

public class UserGradeSetter {
    public void setGrade(Map<Integer, Integer> userGradeMap, List<Map<String, Integer>> gameResultList) {
        for (Map<String, Integer> gameResult : gameResultList) {
            int winnerId = gameResult.get("win");
            int loserId = gameResult.get("lose");
            int takenTime = gameResult.get("taken");

            int winnerGrade = userGradeMap.get(winnerId);
            if (winnerGrade < 100_000) {
                userGradeMap.put(winnerId, winnerGrade + 1);
            }
            int loserGrade = userGradeMap.get(loserId);
            if (loserGrade > 1_000) {
                userGradeMap.put(loserId, loserGrade - 1);
            }
        }
    }
}
