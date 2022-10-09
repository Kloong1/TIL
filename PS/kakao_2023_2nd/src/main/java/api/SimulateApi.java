package api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ReplyReservationDto;
import dto.RoomAssignDto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.*;
import static connection.ConnectionConst.CONTENT_TYPE_JSON;

public class SimulateApi {

    private final URL url;
    private final ObjectMapper objectMapper;
    private final String authKey;

    public SimulateApi(String authKey) throws MalformedURLException {
        this.url = new URL(BASE_URL + "/simulate");
        this.objectMapper = new ObjectMapper();
        this.authKey = authKey;
    }

    public int assignRoom(List<RoomAssignDto> roomAssignList) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        objectMapper.writeValue(bw, Map.of("room_assign", roomAssignList));

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Integer> jsonMap = objectMapper.readValue(br, new TypeReference<>() {});
        br.close();

        int failCount = jsonMap.get("fail_count");
        System.out.println("failCount = " + failCount);

        return jsonMap.get("day");
    }
}
