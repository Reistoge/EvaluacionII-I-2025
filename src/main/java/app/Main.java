package app;

import filter.ExtremeValueFilter;
import filter.RawDataFilter;
import filter.UnitNormalizerFilter;
import filter.ValidatorFilter;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import repository.CleanDataRepository;
import repository.RawDataRepository;
import service.RawDataProcessingService;

import java.util.List;

/**
 * Entry point of the application that coordinates the data processing pipeline.
 *
 * <p>This class initializes the persistence context, constructs the repositories
 * for raw and clean data, defines the filter chain, and invokes the data
 * processing service to execute the Pipe-and-Filter workflow.</p>
 *
 * <p>Students are expected to:</p>
 * <ul>
 *   <li>Define and instantiate one or more {@link RawDataFilter} implementations,</li>
 *   <li>Populate the filter chain in the {@code filters} list,</li>
 *   <li>Observe the output logs for saved and discarded data points.</li>
 * </ul>
 *
 * <p>Logging is handled via SLF4J. Ensure a compatible backend (e.g., Logback)
 * is included in the project dependencies.</p>
 *
 * <p><strong>Note:</strong> The persistence unit name must match the one defined
 * in {@code persistence.xml}, typically {@code "environment"}.</p>
 *
 * @author Daniel San Martín
 */
public class Main {

    /** Logger instance for logging the application lifecycle. */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Main entry point of the program. Initializes all required components,
     * runs the processing pipeline, and closes resources.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        log.info("Iniciando procesamiento...");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("environment");
        RawDataRepository rawRepo = new RawDataRepository(emf);
        CleanDataRepository cleanRepo = new CleanDataRepository(emf);

        // TODO: Instantiate filters and replace `null` entries
        List<RawDataFilter> filters = List.of(
                new ValidatorFilter(),
                new UnitNormalizerFilter(),
                new ExtremeValueFilter()
        );

        // Create the processing service
        RawDataProcessingService service = new RawDataProcessingService(rawRepo, cleanRepo, filters);

        // Execute the pipeline
        try {
            service.processAll();
        } finally {
            emf.close(); // Always close the factory
        }

        log.info("✅ Procesamiento finalizado.");
    }
}
