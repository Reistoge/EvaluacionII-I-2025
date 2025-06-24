package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class ValidatorFilterVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("raw.data.incoming", message -> {
            JsonObject data = (JsonObject) message.body();
            try {
                String type = data.getString("type");
                String timestamp = data.getString("timestamp");
                Double value = data.getDouble("measuredValue");
                if (type == null || timestamp == null || value == null) throw new IllegalArgumentException("Incomplete data");
                if (!type.equals("temperature") && !type.equals("mp")) throw new IllegalArgumentException("Invalid type");
                vertx.eventBus().publish("filter.validated", data);
            } catch (Exception e) {
                // Optionally log or discard
            }
        });
    }
}