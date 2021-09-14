package se.thinkcode.wait;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class WaitTest {
    private final String host = "localhost";
    private static final int port = 8080;
    private final String resource = "/geo/rest/v1/gata?gatunamn=drott";
    private final Map<String, String> headers = new HashMap<>();

    private HttpWaitMojo waitMojo;

    @BeforeClass
    public static void startTestServer() {
        TestServer.start(port);
    }

    @Before
    public void setUp() {
        waitMojo = new HttpWaitMojo();
        waitMojo.waitableStatuses = Collections.singletonList(404);
    }

    @Test
    public void see_started_application() throws Exception {
        waitMojo.url = "http://" + host + ":" + port + resource;

        waitMojo.timeout = 1000;
        headers.put("applicationId", "Acceptance test");
        waitMojo.headers = headers;

        waitMojo.execute();
    }

    @Test
    public void missing_header() {
        waitMojo.url = "http://" + host + ":" + port + resource;
        waitMojo.timeout = 1000;
        waitMojo.headers = headers;

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(
                () -> waitMojo.execute()
        ).withMessage("Connection to http://localhost:8080/geo/rest/v1/gata?gatunamn=drott failed with response code 401");
    }

    @Test
    public void protocol_exception() {
        waitMojo.url = "ht://" + host + ":" + port + resource;

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(
                () -> waitMojo.execute()
        ).withMessage("Connection to ht://localhost:8080/geo/rest/v1/gata?gatunamn=drott failed with the message: unknown protocol: ht");
    }

    @Test
    public void connection_timeout() {
        int wrongPort = 9090;
        waitMojo.url = "http://" + host + ":" + wrongPort + resource;

        waitMojo.timeout = 10;

        assertThatExceptionOfType(TimeoutException.class).isThrownBy(
                () -> waitMojo.execute()
        ).withMessage("Connection to http://localhost:9090/geo/rest/v1/gata?gatunamn=drott timed out");
    }

    @Test
    public void skip_wait() throws Exception {
        waitMojo.url = "http://localhost";
        waitMojo.skip = true;

        waitMojo.execute();
    }
}
