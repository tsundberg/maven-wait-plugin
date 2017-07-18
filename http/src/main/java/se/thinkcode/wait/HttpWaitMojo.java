package se.thinkcode.wait;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@Mojo(name = "wait", threadSafe = true, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class HttpWaitMojo extends AbstractMojo {

    @Parameter(defaultValue = "http://localhost")
    String url;

    @Parameter(defaultValue = "3000")
    int timeout;

    @Parameter
    Map<String, String> headers = Collections.emptyMap();

    public void execute() throws MojoExecutionException, MojoFailureException {
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
