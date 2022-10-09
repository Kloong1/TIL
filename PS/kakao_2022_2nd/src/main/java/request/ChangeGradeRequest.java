package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import parser.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static connection.ConnectionConst.BASE_URL;
import static connection.HttpConst.*;

public class ChangeGradeRequest {

    private final URL url;
    private final ObjectMapper objectMapper;

    public ChangeGradeRequest() throws IOException {
        this.url = new URL(BASE_URL + "/change_grade");
        this.objectMapper = new ObjectMapper();
    }

    public void request(Map<Integer, Integer> userGradeMap, String authKey) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        List<Map<String, Integer>> userGradeMapList = convertUserGradeMap(userGradeMap);

        String body = objectMapper.writeValueAsString(Map.of("commands", userGradeMapList));

        System.out.println(body);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(body);
        bw.flush();
        bw.close();

        JsonParser<Map<String, Object>> jsonParser = new JsonParser<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Object> jsonMap = jsonParser.parse(br);

        conn.disconnect();
    }

    private List<Map<String, Integer>> convertUserGradeMap(Map<Integer, Integer> userGradeMap) {
        ArrayList<Map<String, Integer>> userGradeMapList = new ArrayList<>();

        for (int userId : userGradeMap.keySet()) {
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("id", userId);
            map.put("grade", 1900);
            userGradeMapList.add(map);
            break;
        }

        return userGradeMapList;
    }
}