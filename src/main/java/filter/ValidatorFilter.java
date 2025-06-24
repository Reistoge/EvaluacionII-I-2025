package filter;

import model.RawData;

public class ValidatorFilter implements RawDataFilter {

    //
    @Override
    public RawData apply(RawData data) {
        if (data == null || data.getType() == null || data.getTimestamp() == null) {
            throw new IllegalArgumentException("Incomplete data");
        }
        if (!data.getType().equals("temperature") && !data.getType().equals("mp")) {
            throw new IllegalArgumentException("Invalid type");
        }
        return data;
    }
}