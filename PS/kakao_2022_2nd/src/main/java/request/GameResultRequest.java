package request;

import parser.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.BASE_URL;
import static connection.HttpConst.*;

public class GameResultRequest {

    private final URL url;

    public GameResultRequest() throws IOException {
        this.url = new URL(BASE_URL + "/game_result");
    }

    public List<Map<String, Integer>> request(String authKey) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        JsonParser<Map<String, List<Map<String, Integer>>>> jsonParser = new JsonParser<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, List<Map<String, Integer>>> gameResultListMap = jsonParser.parseAfterPrint(br);
        br.close();
        conn.disconnect();

        List<Map<String, Integer>> gameResultList = gameResultListMap.get("game_result");

        return gameResultList;
    }
}
