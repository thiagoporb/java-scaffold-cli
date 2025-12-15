package %1$s.gatling.simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;

/**
 * Simulação Gatling básica para validar o endpoint {@code /api/v1/exemplos}.
 */
public class BasicSimulation extends Simulation {

    private static final HttpProtocolBuilder HTTP_PROTOCOL = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling Simulation");

    private static final ScenarioBuilder SCENARIO =
            scenario("Listagem inicial")
                    .exec(
                            http("Listar Exemplos")
                                    .get("/api/v1/exemplos")
                                    .check(status().is(200)));

    {
        setUp(
                        SCENARIO.injectOpen(
                                atOnceUsers(5),
                                rampUsers(20).during(Duration.ofSeconds(15))))
                .protocols(HTTP_PROTOCOL);
    }
}

