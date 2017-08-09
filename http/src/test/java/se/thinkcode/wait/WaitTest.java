package se.thinkcode.wait;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class WaitTest {
    private String host = "localhost";
    private static int port = 8080;
    private String resource = "/geo/rest/v1/gata?gatunamn=drott";
    private Map<String, String> headers = new HashMap<>();

    private HttpWaitMojo waitMojo;

    @BeforeClass
    public static void startTestServer() {
        TestServer.start(port);
    }

    @Before
    public void setUp() {
        waitMojo = new HttpWaitMojo();
    }

    @Test
    public void see_started_application() throws Exception {
        waitMojo.url = "http://" + host + ":" + port + resource;

        headers.put("applicationId", "Acceptance test");
        waitMojo.headers = headers;

        waitMojo.execute();
    }

    @Test
    public void missing_header() throws Exception {
        waitMojo.url = "http://" + host + ":" + port + resource;
        waitMojo.headers = headers;

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(
                () -> waitMojo.execute()
        ).withMessage("Connection to http://localhost:8080/geo/rest/v1/gata?gatunamn=drott failed with response code 401");
    }

    @Test
    public void protocol_exception() throws Exception {
        waitMojo.url = "ht://" + host + ":" + port + resource;

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(
                () -> waitMojo.execute()
        ).withMessage("Connection to ht://localhost:8080/geo/rest/v1/gata?gatunamn=drott failed with the message: unknown protocol: ht");
    }

    @Test
    public void connection_timeout() throws Exception {
        int wrongPort = 9090;
        waitMojo.url = "http://" + host + ":" + wrongPort + resource;

        waitMojo.timeout = 10;

        assertThatExceptionOfType(TimeoutException.class).isThrownBy(
                () -> waitMojo.execute()
        ).withMessage("Connection to http://localhost:9090/geo/rest/v1/gata?gatunamn=drott timed out");
    }

}
