package request;

import parser.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.BASE_URL;
import static connection.HttpConst.*;

public class UserInfoRequest {

    private final URL url;

    public UserInfoRequest() throws IOException {
        this.url = new URL(BASE_URL + "/user_info");
    }

    public Map<Integer, Integer> request(String authKey) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        JsonParser<Map<String, List<Map<String, Integer>>>> jsonParser = new JsonParser<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, List<Map<String, Integer>>>  userInfoMap = jsonParser.parse(br);
        br.close();
        conn.disconnect();

        List<Map<String, Integer>> userGradeMapList = userInfoMap.get("user_info");

        return convertUserGradeMapList(userGradeMapList);
    }

    private Map<Integer, Integer> convertUserGradeMapList(List<Map<String, Integer>> userGradeMapList) {
        Map<Integer, Integer> userGradeMap = new HashMap<>();

        for (Map<String, Integer> map : userGradeMapList) {
            userGradeMap.put(map.get("id"), map.get("grade"));
        }

        return userGradeMap;
    }

}
