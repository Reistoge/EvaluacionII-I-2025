package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Verticle responsible for receiving validated data via the EventBus
 * and persisting it to a local text file.
 *
 * <p>It listens on the {@code "validated.data"} channel and stores incoming
 * {@link JsonObject} messages both in memory and by appending them to a text file.
 * Each message is expected to be a properly formatted and validated data record.</p>
 *
 * <p>This verticle demonstrates non-blocking file writing using {@code executeBlocking()}
 * to prevent event loop blocking during I/O operations.</p>
 *
 * <p>The file is named {@code data.txt} and will be created or appended to
 * in the working directory of the application.</p>
 *
 * <p><strong>Note:</strong> This class assumes that messages on {@code "validated.data"}
 * are safe and do not require further schema validation at this stage.</p>
 *
 * @author Daniel San Martín
 */
public class FileStorageVerticle extends AbstractVerticle {

    /** Internal buffer to keep valid data in memory (optional, not persisted across restarts). */
    private final List<JsonObject> validDataList = new ArrayList<>();

    /** Name of the file where validated entries will be stored. */
    private static final String FILE_NAME = "data.txt";

    /**
     * Registers an EventBus consumer on the {@code "validated.data"} channel.
     * When a message is received, it is stored in memory and appended to a file.
     */
    @Override
    public void start() {
        vertx.eventBus().consumer("validated.data", message -> {
            JsonObject data = (JsonObject) message.body();
            validDataList.add(data);
            appendToFile(data.encodePrettily());
        });
    }

    /**
     * Appends a JSON string to the output file asynchronously.
     * Uses {@code executeBlocking()} to ensure the file write does not block the event loop.
     *
     * @param content the string to write to file (with newline appended)
     */
    private void appendToFile(String content) {
        vertx.executeBlocking(promise -> {
            try {
                Files.write(Paths.get(FILE_NAME),
                        (content + System.lineSeparator()).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                promise.complete();
            } catch (IOException e) {
                promise.fail(e);
            }
        }, res -> {
            if (res.failed()) {
                System.err.println("Error writing to file: " + res.cause().getMessage());
            }
        });
    }
}
