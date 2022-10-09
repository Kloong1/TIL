import grade.UserGradeSetter;
import request.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Simulator implements Runnable {

    private final StartRequest startRequest;
    private final UserInfoRequest userInfoRequest;
    private final WaitingLineRequest waitingLineRequest;
    private final GameResultRequest gameResultRequest;
    private final UserGradeSetter userGradeSetter;
    private final MatchRequest matchRequest;
    private final ChangeGradeRequest changeGradeRequest;
    private final ScoreRequest scoreRequest;

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
        this.changeGradeRequest = new ChangeGradeRequest();
        this.scoreRequest = new ScoreRequest();
    }

    public void simulate() throws IOException {
        System.out.println("init");
        initSimulate();

        System.out.println("match");
        while (turn <= 555) {
            System.out.println("turn = " + turn);
            
            waitingUserList = waitingLineRequest.request(authKey);
//            System.out.println(waitingUserList);

            gameResultList = gameResultRequest.request(authKey);

            userGradeSetter.setGrade(userGradeMap, gameResultList);

            turn = matchRequest.request(waitingUserList, userGradeMap, authKey);
        }

        while (turn < 595) {
            System.out.println("turn = " + turn);
            turn = matchRequest.request(null, userGradeMap, authKey);
            gameResultList = gameResultRequest.request(authKey);
            userGradeSetter.setGrade(userGradeMap, gameResultList);
        }

        System.out.println("turn = " + turn);
        gameResultList = gameResultRequest.request(authKey);
        userGradeSetter.setGrade(userGradeMap, gameResultList);

        changeGradeRequest.request(userGradeMap, authKey);

        scoreRequest.request(authKey);
    }

    private void initSimulate() throws IOException {
        authKey = startRequest.request(1);
        System.out.println("authKey = " + authKey);

        userGradeMap = userInfoRequest.request(authKey);
        for (int userId : userGradeMap.keySet()) {
            userGradeMap.put(userId, 40_000); //평균
        }

        turn = matchRequest.request(null, userGradeMap, authKey);
    }

    @Override
    public void run() {
        try {
            simulate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
