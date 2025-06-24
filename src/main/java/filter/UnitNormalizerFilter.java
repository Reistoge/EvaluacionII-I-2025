package filter;

import model.RawData;

public class UnitNormalizerFilter implements RawDataFilter {
    @Override
    public RawData apply(RawData data) {
        if ("temperature".equals(data.getType())) {

            if ("F".equals(data.getUnit())) {  // conversion a celsius cuando la unidad es "fahrenheit"
                double celsius = (data.getMeasuredValue() - 32) / 1.8;
                data.setMeasuredValue(celsius);
                data.setUnit("C"); // se actualiza la unidad a celsius
            }
        } else if ("mp".equals(data.getType())) { // conversion de mg/m3 a ug/m3

            if ("mg/m3".equals(data.getUnit())) {
                data.setMeasuredValue(data.getMeasuredValue() * 1000);
                data.setUnit("ug/m3");  // se actualiza la unidad a ug/m3
            }
        }
        return data;
    }
}