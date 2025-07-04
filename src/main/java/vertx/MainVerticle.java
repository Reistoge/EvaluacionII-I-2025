package vertx;

import io.vertx.core.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Main verticle that coordinates the deployment of all components in the
 * Vert.x-based Pub/Sub monitoring pipeline.
 *
 * <p>This verticle uses chained deployments to sequentially launch the
 * pipeline components such as:
 * <ul>
 *   <li>{@code ReaderBDVerticle}: reads raw data (e.g., from database or memory)</li>
 *   <li>{@code ProducerBDVerticle}: publishes readings to the EventBus</li>
 *   <li>{@code FileStorageVerticle}: stores clean results</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This class demonstrates the use of deployment chaining
 * via {@code Future.compose()} and includes logging of the success/failure status.</p>
 *
 * <p>To run the system, the {@code main} method instantiates Vert.x and deploys this verticle.</p>
 *
 * <pre>{@code
 * $ java -cp target/classes vertx.MainVerticle
 * }</pre>
 *
 * @author Daniel San Martín
 */
public class MainVerticle extends AbstractVerticle {

    /** SLF4J logger for lifecycle and error reporting. */
    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    /**
     * Called automatically by Vert.x when this verticle is deployed.
     *
     * <p>This method orchestrates the deployment of dependent verticles and completes
     * the startup process only if all deployments succeed.</p>
     *
     * @param startPromise a promise used to signal successful or failed startup
     */
    @Override
    public void start(Promise<Void> startPromise) {

        DeploymentOptions workerOptions = new DeploymentOptions().setWorker(true);

        // Chain deployments of verticles in order
        vertx.deployVerticle(new ReaderBDVerticle(), workerOptions)
                .compose(id -> vertx.deployVerticle(new ProducerBDVerticle()))
                .compose(id -> vertx.deployVerticle(new ValidatorFilterVerticle()))
                .compose(id -> vertx.deployVerticle(new UnitNormalizerFilterVerticle()))
                .compose(id -> vertx.deployVerticle(new ExtremeValueFilterVerticle()))
                .compose(id -> vertx.deployVerticle(new FileStorageVerticle()))
                .onSuccess(id -> {
                    log.info("✅ Sistema de monitoreo iniciado.");
                    startPromise.complete();
                    // se testea enviando datos de ejemplo a traves del eventBus.
                    // testWithSampleData();
                })
                .onFailure(err -> {
                    log.error("Error al iniciar el sistema: " + err.getMessage());
                    startPromise.fail(err);
                });
    }
    /**
     * Launches the Vert.x application from the command line.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new MainVerticle(), res -> {
            if (res.failed()) {
                log.error("Error al desplegar MainVerticle: " + res.cause().getMessage());
            }
        });
    }

    private void testWithSampleData() {
        log.info("Sending test data through the pipeline...");

        // ejemplo con Fahrenheit
        JsonObject tempF = new JsonObject()
                .put("variableType", "temperature")
                .put("value", 86.0)
                .put("unit", "F")
                .put("timestamp", LocalDateTime.now().toString());
        vertx.eventBus().publish("raw.data.incoming", tempF);

        // ejemplo con with mg/m3
        JsonObject mp = new JsonObject()
                .put("variableType", "mp")
                .put("value", 0.5)
                .put("unit", "mg/m3")
                .put("timestamp", LocalDateTime.now().toString());
        vertx.eventBus().publish("raw.data.incoming", mp);

        // ejemplo con un valor invalido
        JsonObject invalid = new JsonObject()
                .put("variableType", "unknown")
                .put("value", 100.0)
                .put("unit", "C")
                .put("timestamp", LocalDateTime.now().toString());
        vertx.eventBus().publish("raw.data.incoming", invalid);
    }

}
