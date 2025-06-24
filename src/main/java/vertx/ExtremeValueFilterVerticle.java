package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class ExtremeValueFilterVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("filter.normalized", message -> {
            JsonObject data = (JsonObject) message.body();
            System.out.println("ExtremeValueFilter - Before: " + data.encode());

            try {
                String type = data.getString("variableType");
                double value = data.getDouble("value");
                boolean valid = true;
                if ("temperature".equals(type)) {
                    valid = value >= -50 && value <= 70;
                } else if ("mp".equals(type)) {
                    valid = value >= 0 && value <= 1000;
                }

                if (valid) {
                    System.out.println("ExtremeValueFilter - Valid data: " + data.encode());
                    vertx.eventBus().publish("validated.data", data);
                } else {
                    System.out.println("ExtremeValueFilter - Discarded extreme value: " + data.encode());
                }
            } catch (Exception e) {
                System.err.println("ExtremeValueFilter - Error: " + e.getMessage());
            }
        });
    }
}