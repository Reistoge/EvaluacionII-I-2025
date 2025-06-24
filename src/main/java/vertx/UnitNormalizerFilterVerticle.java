package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class UnitNormalizerFilterVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("filter.validated", message -> {
            JsonObject data = (JsonObject) message.body();
            String type = data.getString("type");
            double value = data.getDouble("measuredValue");
            String unit = data.getString("unit");
            if ("temperature".equals(type) && "F".equals(unit)) {
                double celsius = (value - 32) / 1.8;
                data.put("measuredValue", celsius);
                data.put("unit", "C");
            } else if ("mp".equals(type) && "mg/m3".equals(unit)) {
                data.put("measuredValue", value * 1000);
                data.put("unit", "ug/m3");
            }
            vertx.eventBus().publish("filter.normalized", data);
        });
    }
}