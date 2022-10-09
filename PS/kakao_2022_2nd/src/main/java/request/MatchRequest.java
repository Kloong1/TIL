package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import parser.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.BASE_URL;
import static connection.HttpConst.*;

public class MatchRequest {

    private final URL url;
    private final ObjectMapper objectMapper;

    public MatchRequest() throws IOException {
        this.url = new URL(BASE_URL + "/match");
        this.objectMapper = new ObjectMapper();
    }

    public int request(Map<Integer, Integer> waitingUserList, Map<Integer, Integer> userGradeMap, String authKey) throws IOException {
        List<List<Integer>> pairList = generatePairList(waitingUserList, userGradeMap);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        String body = objectMapper.writeValueAsString(Map.of("pairs", pairList));

        System.out.println(body);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(body);
        bw.flush();
        bw.close();

        JsonParser<Map<String, Object>> jsonParser = new JsonParser<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Object> jsonMap = jsonParser.parse(br);

        conn.disconnect();

        return (int) jsonMap.get("time");
    }

    private List<List<Integer>> generatePairList(Map<Integer, Integer> waitingUserList, Map<Integer, Integer> userGradeMap) {
        List<List<Integer>> pairList = new ArrayList<>();

        if (waitingUserList == null || waitingUserList.isEmpty() || waitingUserList.size() == 1) {
            pairList.add(new ArrayList<>());
            return pairList;
        }

        int i = 1;
        ArrayList<Integer> pair = new ArrayList<>();
        for (Integer userId : waitingUserList.keySet()) {
            pair.add(userId);
            if (i % 2 == 0) {
                pairList.add(pair);
                pair = new ArrayList<>();
            }
            i++;
        }

        return pairList;
    }
}
