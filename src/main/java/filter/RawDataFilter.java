package filter;

import model.RawData;

/**
 * Defines a strategy interface for filtering or transforming {@link RawData} objects.
 *
 * <p>Each implementation of this interface applies a specific transformation,
 * validation, or normalization step to a raw data reading, typically as part of
 * a Pipe-and-Filter architectural pattern.</p>
 *
 * <p>Filters may include operations such as:</p>
 * <ul>
 *   <li>Converting temperature from Fahrenheit to Celsius</li>
 *   <li>Normalizing units (e.g., from mg/m³ to μg/m³)</li>
 *   <li>Discarding invalid or out-of-range measurements</li>
 * </ul>
 *
 * <p>Filters are intended to be chained, and each one may modify or return a
 * new {@code RawData} instance, or throw an exception if the data is invalid.</p>
 *
 * @author Daniel San Martín
 */
public interface RawDataFilter {

    /**
     * Applies the filter logic to a single {@link RawData} instance.
     *
     * @param data the raw sensor data to process
     * @return a processed {@link RawData} instance, possibly modified
     * @throws Exception if the data is invalid or cannot be processed
     */
    RawData apply(RawData data) throws Exception;
}
