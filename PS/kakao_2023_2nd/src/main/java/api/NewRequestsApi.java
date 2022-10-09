package api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ReservationDto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.*;

public class NewRequestsApi {

    private final URL url;
    private final ObjectMapper objectMapper;
    private final String authKey;

    public NewRequestsApi(String authKey) throws MalformedURLException {
        this.url = new URL(BASE_URL + "/new_requests");
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        this.authKey = authKey;
    }

    public List<ReservationDto> getReservationList() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, List<Map<String, Integer>>> jsonMap = objectMapper.readValue(br, new TypeReference<>() {});
        br.close();

        List<Map<String, Integer>> reservationMapList = jsonMap.get("reservations_info");

        conn.disconnect();

        return convertMapListToReservationList(reservationMapList);
    }

    private List<ReservationDto> convertMapListToReservationList(List<Map<String, Integer>> reservationMapList) {
        List<ReservationDto> reservationList = new ArrayList<>(reservationMapList.size());

        for (Map<String, Integer> reservationMap : reservationMapList) {
            ReservationDto reservation = new ReservationDto(
                    reservationMap.get("id"),
                    reservationMap.get("amount"),
                    reservationMap.get("check_in_date"),
                    reservationMap.get("check_out_date")
            );
            reservationList.add(reservation);
        }

        return reservationList;
    }
}
