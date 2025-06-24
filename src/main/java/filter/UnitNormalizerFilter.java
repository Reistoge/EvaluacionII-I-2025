package filter;

import model.RawData;

public class UnitNormalizerFilter implements RawDataFilter {
    @Override
    public RawData apply(RawData data) {
        if ("temperature".equals(data.getType())) {
            if (data.getMeasuredValue() > 70) {
                double celsius = (data.getMeasuredValue() - 32) / 1.8;
                data.setMeasuredValue(celsius);
            }
        } else if ("mp".equals(data.getType())) {
            if (data.getMeasuredValue() < 10) {
                data.setMeasuredValue(data.getMeasuredValue() * 1000);
            }
        }
        return data;
    }
}