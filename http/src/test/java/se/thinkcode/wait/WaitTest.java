package se.thinkcode.wait;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class WaitTest {
    private String host = "localhost";
    private String port = "8080";
    private String resource = "/geo/rest/v1/gata?gatunamn=drott";
    private Map<String, String> headers = new HashMap<>();
    private int timeout = 100;

    @Test
    public void see_started_application() throws Exception {
        String url = "http://" + host + ":" + port + resource;

        headers.put("applicationId", "Acceptance test");

        connect(url, timeout, headers);
    }

    @Test
    public void missing_header() throws Exception {
        String url = "http://" + host + ":" + port + resource;

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(
                () -> connect(url, timeout, headers)
        ).withMessage("Connection to http://localhost:8080/geo/rest/v1/gata?gatunamn=drott failed with response code 401");
    }

    @Test
    public void protocol_exception() throws Exception {
        String url = "ht://" + host + ":" + port + resource;

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(
                () -> connect(url, timeout, headers)
        ).withMessage("Connection to ht://localhost:8080/geo/rest/v1/gata?gatunamn=drott failed with the message: unknown protocol: ht");
    }

    @Test
    public void connection_timeout() throws Exception {
        String url = "http://" + host + ":" + port + resource;
        int timeout = 1;

        assertThatExceptionOfType(TimeoutException.class).isThrownBy(
                () -> connect(url, timeout, headers)
        ).withMessage("Connection to http://localhost:8080/geo/rest/v1/gata?gatunamn=drott timed out");
    }

    private void connect(String url, int timeout, Map<String, String> headers) {
        HttpURLConnection con;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            throw new ConnectionException("Connection to " + url + " failed with the message: " + e.getMessage());
        }

        con.setConnectTimeout(timeout);

        for (String key : headers.keySet()) {
            String value = headers.get(key);
            con.setRequestProperty(key, value);
        }

        int responseCode;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            throw new TimeoutException("Connection to " + url + " timed out");
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return;
        }

        throw new ConnectionException("Connection to " + url + " failed with response code " + responseCode);
    }
}
