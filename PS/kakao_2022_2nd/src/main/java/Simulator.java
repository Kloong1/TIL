import grade.UserGradeSetter;
import request.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulator {

    private final StartRequest startRequest;
    private final UserInfoRequest userInfoRequest;
    private final WaitingLineRequest waitingLineRequest;
    private final GameResultRequest gameResultRequest;
    private final UserGradeSetter userGradeSetter;
    private final MatchRequest matchRequest;

    private String authKey;

    private Map<Integer, Integer> userGradeMap;
    private Map<Integer, Integer> waitingUserList;
    private List<Map<String, Integer>> gameResultList;

    private int turn = 0;

    public Simulator() throws IOException {
        this.startRequest = new StartRequest();
        this.userInfoRequest = new UserInfoRequest();
        this.waitingLineRequest = new WaitingLineRequest();
        this.gameResultRequest = new GameResultRequest();
        this.userGradeSetter = new UserGradeSetter();
        this.matchRequest = new MatchRequest();
    }

    public void simulate() throws IOException {
        System.out.println("init");
        initSimulate();

        System.out.println("match");
        while (turn <= 555) {
            System.out.println("turn = " + turn);

            waitingUserList = waitingLineRequest.request(authKey);
            System.out.println(waitingUserList);

            gameResultList = gameResultRequest.request(authKey);

            for (Map<String, Integer> gameResult : gameResultList) {
                System.out.println(gameResult);
            }

            userGradeSetter.setGrade(userGradeMap, gameResultList);

            turn = matchRequest.request(waitingUserList, userGradeMap, authKey);
        }
    }

    private void initSimulate() throws IOException {
        authKey = startRequest.request(1);
        userGradeMap = userInfoRequest.request(authKey);
        turn = matchRequest.request(null, userGradeMap, authKey);
    }
}
