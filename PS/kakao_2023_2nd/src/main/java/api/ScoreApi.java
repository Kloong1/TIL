package api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ReservationDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.*;
import static connection.ConnectionConst.CONTENT_TYPE_JSON;

public class ScoreApi {

    private final URL url;
    private final ObjectMapper objectMapper;
    private final String authKey;

    public ScoreApi(String authKey) throws MalformedURLException {
        this.url = new URL(BASE_URL + "/score");
        this.objectMapper = new ObjectMapper();
        this.authKey = authKey;
    }

    public void printScore() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Double> scoreMap = objectMapper.readValue(br, new TypeReference<>() {});
        br.close();

        System.out.println("\n------ Score ------");
        for (String score : scoreMap.keySet()) {
            System.out.println(score + ": " + scoreMap.get(score));
        }
        System.out.println("------------------\n");

        conn.disconnect();
    }
}
