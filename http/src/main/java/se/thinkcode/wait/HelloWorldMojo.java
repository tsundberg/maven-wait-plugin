package se.thinkcode.wait;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;

@Mojo(name = "wait", threadSafe = true, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class HelloWorldMojo extends AbstractMojo {

    @Parameter(defaultValue = "Hello, world!")
    private String greeting;

    @Parameter
    private Map<String, String> headers;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println(greeting);

        for (String key : headers.keySet()) {
            String value = headers.get(key);
            System.out.println(key + " : " + value);
        }
    }
}
