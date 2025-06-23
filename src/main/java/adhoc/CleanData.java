package adhoc;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a cleaned and validated sensor reading, ready for storage
 * after passing through the pipe-and-filter processing pipeline.
 *
 * <p>This entity is persisted in the {@code clean_readings} table.</p>
 *
 * <p>Each record includes:</p>
 * <ul>
 *     <li>an auto-generated {@code id},</li>
 *     <li>a {@code type} indicating the sensor type (e.g., "temperature", "mp"),</li>
 *     <li>a {@code timestamp} of when the data was recorded,</li>
 *     <li>and a {@code measuredValue} in standardized units.</li>
 * </ul>
 *
 * @author Daniel San Martín
 */
@Entity
@Table(name = "clean_readings")
public class CleanData {

    /**
     * Primary key for the clean_readings table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The type of measurement, such as "temperature" or "mp" (particulate matter).
     */
    private String type;

    /**
     * Timestamp indicating when the sensor reading was taken.
     */
    private LocalDateTime timestamp;

    /**
     * The standardized and validated value of the reading, stored as a double.
     */
    @Column(name = "measured_value")
    private double measuredValue;

    /**
     * Default constructor required by JPA.
     */
    public CleanData() {}

    /**
     * Constructs a new {@code CleanData} instance with the given values.
     *
     * @param type the type of the reading (e.g., "temperature")
     * @param timestamp the timestamp of the reading
     * @param value the standardized and validated value
     */
    public CleanData(String type, LocalDateTime timestamp, double value) {
        this.type = type;
        this.timestamp = timestamp;
        this.measuredValue = value;
    }

    /** @return the database ID */
    public Integer getId() { return id; }

    /** @return the type of the measurement */
    public String getType() { return type; }

    /** @return the timestamp of the reading */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @return the cleaned and standardized measurement value */
    public double getValue() { return measuredValue; }

    /** @param id sets the ID (used internally by JPA) */
    public void setId(Integer id) { this.id = id; }

    /** @param type the type of measurement to set */
    public void setType(String type) { this.type = type; }

    /** @param timestamp the timestamp to set */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /** @param value the cleaned measurement value to set */
    public void setValue(double value) { this.measuredValue = value; }

    @Override
    public String toString() {
        return "CleanData{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + measuredValue +
                '}';
    }
}
