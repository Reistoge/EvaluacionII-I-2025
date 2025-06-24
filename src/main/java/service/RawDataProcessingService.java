package service;

import model.CleanData;
import repository.CleanDataRepository;
import filter.RawDataFilter;
import model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RawDataRepository;

import java.util.List;

/**
 * Service class responsible for orchestrating the processing of raw sensor data
 * through a configurable pipeline of {@link RawDataFilter}s and persisting valid
 * results into the clean data repository.
 *
 * <p>This class implements the core logic of a Pipe-and-Filter architecture:
 * it retrieves all entries from the {@link RawDataRepository}, applies a sequence
 * of transformations/validations via the filter chain, and if successful,
 * stores the result in the {@link CleanDataRepository}.</p>
 *
 * <p>Data that fails any filter in the chain is discarded, and an explanatory
 * message is printed to standard output.</p>
 *
 * <p>This design allows for flexible addition or removal of filters, enabling
 * the processing logic to be easily extended or reused.</p>
 *
 * <pre>{@code
 * RawDataProcessingService service = new RawDataProcessingService(rawRepo, cleanRepo, filters);
 * service.processAll();
 * }</pre>
 *
 * @author Daniel San Martín
 */
public class RawDataProcessingService {

    private final RawDataRepository rawRepo;
    private final CleanDataRepository cleanRepo;
    private final List<RawDataFilter> filters;

    private static final Logger log = LoggerFactory.getLogger(RawDataProcessingService.class);


    /**
     * Constructs a new {@code RawDataProcessingService} with the required repositories and filter chain.
     *
     * @param rawRepo   the repository from which raw data will be retrieved
     * @param cleanRepo the repository into which valid processed data will be persisted
     * @param filters   the list of filters to apply in sequence to each raw record
     */
    public RawDataProcessingService(
            RawDataRepository rawRepo,
            CleanDataRepository cleanRepo,
            List<RawDataFilter> filters
    ) {
        this.rawRepo = rawRepo;
        this.cleanRepo = cleanRepo;
        this.filters = filters;
    }

    /**
     * Processes all raw records in the {@link RawDataRepository}:
     * <ul>
     *     <li>Applies each configured filter in order</li>
     *     <li>Persists valid results as {@link CleanData}</li>
     *     <li>Prints success or discard messages for each record</li>
     * </ul>
     *
     * <p>If any filter throws an exception, the current raw record is discarded.</p>
     */
    public void processAll() {
        List<RawData> allRaw = rawRepo.findAll();

        for (RawData raw : allRaw) {
            try {
                RawData filtered = applyFilters(raw);
                CleanData clean = new CleanData(
                        filtered.getType(),
                        filtered.getTimestamp(),
                        filtered.getValue()
                );
                cleanRepo.save(clean);
                log.info("Dato guardado: " + clean);
            } catch (Exception e) {
                log.info("Dato descartado (ID=" + raw.getId() + "): " + e.getMessage());
            }
        }
    }

    /**
     * Applies the entire filter chain to the given {@link RawData} input.
     *
     * @param input the raw data record to process
     * @return the filtered and potentially transformed raw data
     * @throws Exception if any filter in the chain rejects the data
     */
    private RawData applyFilters(RawData input) throws Exception {
        RawData result = input;
        for (RawDataFilter filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }
}
