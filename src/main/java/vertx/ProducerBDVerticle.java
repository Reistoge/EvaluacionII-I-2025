package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Verticle responsible for initiating a request to retrieve raw data (e.g., from a database or memory)
 * and publishing each data item onto the EventBus channel {@code "raw.data.incoming"}.
 *
 * <p>This component acts as a data producer within a Pub/Sub architecture implemented in Vert.x.
 * Upon deployment, it sends a message (without parameters) to the address {@code "db.read"} and expects
 * a {@link JsonArray} in the response. Each element of the array is then published individually to the
 * pipeline for further processing.</p>
 *
 * <p>This is typically the second verticle in the chain, following a reader or mock data provider.</p>
 *
 * <p><strong>Note:</strong> Error handling is minimal and assumes the consumer of {@code "db.read"}
 * responds with a valid JSON array.</p>
 *
 * @author Daniel San Martín
 */
public class ProducerBDVerticle extends AbstractVerticle {

    /**
     * Called when this verticle is deployed. Sends a request to the address {@code "db.read"}
     * and publishes each entry in the resulting {@link JsonArray} to {@code "raw.data.incoming"}.
     */
    @Override
    public void start() {

        JsonObject request = new JsonObject();  // can be used to add filters or params if needed

        vertx.eventBus().request("db.read", request, ar -> {
            if (ar.succeeded()) {
                JsonArray readings = (JsonArray) ar.result().body();
                for (int i = 0; i < readings.size(); i++) {
                    JsonObject reading = readings.getJsonObject(i);
                    vertx.eventBus().publish("raw.data.incoming", reading);
                }
            } else {
                System.err.println("Failed to read from database: " + ar.cause().getMessage());
            }
        });
    }
}
