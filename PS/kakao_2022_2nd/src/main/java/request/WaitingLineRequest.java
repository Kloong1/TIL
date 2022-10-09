package request;

import parser.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.*;
import static connection.HttpConst.*;

public class WaitingLineRequest {

    private final URL url;

    public WaitingLineRequest() throws IOException {
        this.url = new URL(BASE_URL + "/waiting_line");
    }

    public Map<Integer, Integer> request(String authKey) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        JsonParser<Map<String, List<Map<String, Integer>>>> jsonParser = new JsonParser<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, List<Map<String, Integer>>> waitingUserListMap = jsonParser.parse(br);
        br.close();
        conn.disconnect();

        List<Map<String, Integer>> waitingUserMapList = waitingUserListMap.get("waiting_line");

        return convertWaitingUserMapList(waitingUserMapList);
    }

    private Map<Integer, Integer> convertWaitingUserMapList(List<Map<String, Integer>> waitingUserMapList) {
        Map<Integer, Integer> waitingUserFromMap = new HashMap<>();

        for (Map<String, Integer> map : waitingUserMapList) {
            waitingUserFromMap.put(map.get("id"), map.get("from"));
        }

        return waitingUserFromMap;
    }
}
