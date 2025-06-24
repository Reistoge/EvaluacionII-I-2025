package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a raw, unprocessed sensor reading retrieved directly from the data source,
 * typically stored before any normalization, validation, or filtering is applied.
 *
 * <p>This entity corresponds to the {@code raw_readings} table in the database.</p>
 *
 * <p>Each reading includes:</p>
 * <ul>
 *     <li>an auto-generated {@code id},</li>
 *     <li>a {@code type} (e.g., "temperature", "mp"),</li>
 *     <li>a {@code timestamp} indicating when the data was captured,</li>
 *     <li>a {@code measuredValue}, which may require unit normalization,</li>
 *     <li>and a {@code unit} string such as "C", "F", "mg/m3", or "ug/m3".</li>
 * </ul>
 *
 * <p>After applying filters, valid and normalized readings are persisted as
 * {@link CleanData} instances.</p>
 *
 * @author Daniel San Martín
 */
@Entity
@Table(name = "raw_readings")
public class RawData {

    /**
     * Primary key of the {@code raw_readings} table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Type of the sensor reading (e.g., "temperature", "mp").
     */
    private String type;

    /**
     * Timestamp representing when the reading was taken.
     */
    private LocalDateTime timestamp;

    /**
     * Raw value measured by the sensor.
     */
    @Column(name = "measured_value")
    private double measuredValue;

    /**
     * Unit of measurement (e.g., "C", "F", "ug/m3", "mg/m3").
     */
    private String unit;

    /**
     * Default constructor required by JPA.
     */
    public RawData() {}

    /**
     * Constructs a new {@code RawData} record with the specified parameters.
     *
     * @param type     the type of measurement
     * @param timestamp the date and time of the reading
     * @param value    the raw measured value
     * @param unit     the unit of the measurement
     */
    public RawData(String type, LocalDateTime timestamp, double value, String unit) {
        this.type = type;
        this.timestamp = timestamp;
        this.measuredValue = value;
        this.unit = unit;
    }

    /** @return the record ID */
    public Integer getId() { return id; }

    /** @return the sensor type */
    public String getType() { return type; }

    /** @return the timestamp of the reading */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @return the raw measured value */
    public double getValue() { return measuredValue; }

    /** @return the measurement unit */
    public String getUnit() { return unit; }

    /** @param id the ID to set (used internally by JPA) */
    public void setId(Integer id) { this.id = id; }

    /** @param type the type of reading to set */
    public void setType(String type) { this.type = type; }

    /** @param timestamp the timestamp to set */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /** @param value the raw value to set */
    public void setValue(double value) { this.measuredValue = value; }


    /** @param unit the unit of measurement to set */
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        return "RawData{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + measuredValue +
                ", unit='" + unit + '\'' +
                '}';
    }

    public double getMeasuredValue() {
        return this.measuredValue;
    }

    public void setMeasuredValue(double celsius) {
        this.measuredValue = celsius;
    }
}
