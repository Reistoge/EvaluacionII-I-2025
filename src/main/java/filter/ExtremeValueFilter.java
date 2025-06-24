package filter;

import model.RawData;

public class ExtremeValueFilter implements RawDataFilter {
    @Override
    public RawData apply(RawData data) {

        if ("temperature".equals(data.getType())) { // valores extremos cuando es temperatura
            if (data.getMeasuredValue() < -50 || data.getMeasuredValue() > 70) {
                throw new IllegalArgumentException("Temperature out of range");
            }
        } else if ("mp".equals(data.getType())) { // valores extremos cuando es mp
            if (data.getMeasuredValue() < 0 || data.getMeasuredValue() > 1000) {
                throw new IllegalArgumentException("MP out of range");
            }
        }
        return data;
    }
}