package vertx;

import model.RawData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

/**
 * Verticle that queries the database for {@link RawData} records and returns
 * the result to requesting verticles via the EventBus.
 *
 * <p>It listens on the address {@code "db.read"} and performs the query inside
 * {@code executeBlocking} to avoid blocking the Vert.x event loop. Results are
 * returned as a {@link JsonArray} containing one JSON object per row.</p>
 *
 * <p>Lifecycle:</p>
 * <ol>
 *   <li>{@code start()} – initialises a JPA {@link EntityManagerFactory} and
 *       registers the EventBus consumer.</li>
 *   <li>On each request, retrieves all rows from {@code RawData} and converts
 *       them to JSON.</li>
 *   <li>{@code stop()} – closes the factory when the verticle is undeployed.</li>
 * </ol>
 *
 * <p><strong>Note for students:</strong> Adjust the persistence-unit name
 * ({@code "myPersistenceUnit"}) to match your <code>persistence.xml</code>.
 * You may also extend the JPQL query to add ordering, filtering or pagination
 * if required.</p>
 *
 * @author Daniel San Martín
 */
public class ReaderBDVerticle extends AbstractVerticle {

    /** Factory used to create {@link EntityManager} instances per request. */
    private EntityManagerFactory entityManagerFactory;

    /**
     * Sets up the JPA factory and registers the {@code "db.read"} consumer.
     * All database access is wrapped in {@code executeBlocking} to keep the
     * event-loop thread unblocked.
     */
    @Override
    public void start() {
        entityManagerFactory = Persistence.createEntityManagerFactory("environment");

        vertx.eventBus().consumer("db.read", message -> {
            vertx.executeBlocking(promise -> {
                EntityManager entityManager = null;
                try {
                    entityManager = entityManagerFactory.createEntityManager();

                    List<RawData> readings = entityManager
                            .createQuery("SELECT r FROM RawData r", RawData.class)
                            .getResultList();

                    JsonArray resultArray = new JsonArray();
                    for (RawData reading : readings) {
                        resultArray.add(new JsonObject()
                                .put("variableType", reading.getType())
                                .put("timestamp", reading.getTimestamp().toString())
                                .put("value", reading.getValue()));
                    }

                    promise.complete(resultArray);

                } catch (Exception e) {
                    promise.fail(e);
                } finally {
                    if (entityManager != null && entityManager.isOpen()) {
                        entityManager.close();
                    }
                }
            }, asyncResult -> {
                if (asyncResult.succeeded()) {
                    message.reply(asyncResult.result());
                } else {
                    message.fail(500, asyncResult.cause().getMessage());
                }
            });
        });
    }

    /**
     * Closes the {@link EntityManagerFactory} when the verticle is undeployed,
     * releasing all underlying resources.
     */
    @Override
    public void stop() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
