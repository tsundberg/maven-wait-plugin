package se.thinkcode.wait;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@Mojo(name = "wait", threadSafe = true, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class HttpWaitMojo extends AbstractMojo {

    @Parameter(defaultValue = "http://localhost")
    String url;

    @Parameter(defaultValue = "10000")
    int timeout;

    @Parameter
    Map<String, String> headers = Collections.emptyMap();

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Waiting for " + url);
        long startTime = System.currentTimeMillis();

        int responseCode = HttpURLConnection.HTTP_NOT_FOUND;
        try {
            long endTime = System.currentTimeMillis() + timeout;
            while (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                try {
                    HttpURLConnection con;

                    URL obj = new URL(url);
                    con = (HttpURLConnection) obj.openConnection();
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

                Thread.sleep(100);
            }
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
