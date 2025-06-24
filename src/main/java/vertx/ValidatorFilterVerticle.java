package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class ValidatorFilterVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("raw.data.incoming", message -> {
            JsonObject data = (JsonObject) message.body();
            System.out.println("ValidatorFilter - Before: " + data.encode());

            try {
                String type = data.getString("variableType");
                String timestamp = data.getString("timestamp");
                Double value = data.getDouble("value");
                if (type == null || timestamp == null || value == null) throw new IllegalArgumentException("Incomplete data");
                if (!type.equals("temperature") && !type.equals("mp")) throw new IllegalArgumentException("Invalid type");

                System.out.println("ValidatorFilter - Valid data: " + data.encode());
                vertx.eventBus().publish("filter.validated", data);
            } catch (Exception e) {
                System.err.println("ValidatorFilter - Error: " + e.getMessage());
                // Optionally log or discard
            }
        });
    }
}