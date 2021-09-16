package se.thinkcode.wait;

import java.util.Set;

import static spark.Spark.*;

public class WaitServer {

    public static void main(String[] args) {
        port(4040);

        get("/geo/rest/v1/gata", (req, res) -> {
            Set<String> headers = req.headers();
            if (!headers.contains("applicationId")) {
                res.status(401);
            }

            return "Hello, World!";
        });

        awaitInitialization();
    }

}
