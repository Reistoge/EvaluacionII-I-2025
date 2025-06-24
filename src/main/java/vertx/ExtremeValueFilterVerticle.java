package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class ExtremeValueFilterVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("filter.normalized", message -> {
            JsonObject data = (JsonObject) message.body();
            String type = data.getString("type");
            double value = data.getDouble("measuredValue");
            boolean valid = true;
            if ("temperature".equals(type)) {
                valid = value >= -50 && value <= 70;
            } else if ("mp".equals(type)) {
                valid = value >= 0 && value <= 1000;
            }
            if (valid) {
                vertx.eventBus().publish("validated.data", data);
            }
            // else: discard or log
        });
    }
}