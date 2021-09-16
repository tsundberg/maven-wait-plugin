package se.thinkcode.wait;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mojo(name = "wait", threadSafe = true, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class HttpWaitMojo extends AbstractMojo {

    @Parameter(defaultValue = "http://localhost")
    String url;

    @Parameter(defaultValue = "10000")
    int timeout;

    @Parameter
    Map<String, String> headers = Collections.emptyMap();

    @Parameter(property = "http.wait.skip", defaultValue = "false")
    boolean skip;

    /**
     * Defines a list of statuses on which the plugin will continue waiting.
     */
    @Parameter(property = "http.wait.statuses", defaultValue = "404")
    List<Integer> waitableStatuses;

    public void execute() {
        if (skip) {
            getLog().debug("Mojo has been configured to be skipped; no waiting will occur");
            return;
        }

        getLog().info(String.format("Waiting for " + url + " while statuses %s are seen.", waitableStatuses));
        long startTime = System.currentTimeMillis();

        int responseCode = HttpURLConnection.HTTP_NOT_FOUND;
        try {
            long endTime = System.currentTimeMillis() + timeout;
            do {
                if (System.currentTimeMillis() > endTime) {
                    throw new TimeoutException("Connection to " + url + " timed out");
                }

                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(timeout);

                    for (String key : headers.keySet()) {
                        String value = headers.get(key);
                        con.setRequestProperty(key, value);
                    }

                    responseCode = con.getResponseCode();
                    con.disconnect();
                } catch (MalformedURLException e) {
                    throw new ConnectionException("Connection to " + url + " failed with the message: " + e.getMessage());
                } catch (IOException e) {
                    if (System.currentTimeMillis() > endTime) {
                        throw new TimeoutException("Connection to " + url + " timed out");
                    }
                }

                //noinspection BusyWait
                Thread.sleep(100);
            } while (waitableStatuses.contains(responseCode));
        } catch (InterruptedException e) {
            getLog().error(e);
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            long waitingTime = System.currentTimeMillis() - startTime;
            getLog().info("Waited for " + waitingTime + "ms for " + url);
            return;
        }

        throw new ConnectionException("Connection to " + url + " failed with response code " + responseCode);
    }
}
