package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import model.CleanData;

/**
 * Repository class responsible for persisting instances of {@link CleanData}
 * into the database using JPA.
 *
 * <p>This class uses a provided {@link EntityManagerFactory} to create
 * {@link EntityManager} instances as needed. It performs transactional
 * operations to persist validated sensor data.</p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * CleanDataRepository repo = new CleanDataRepository(emf);
 * repo.save(cleanData);
 * }</pre>
 *
 * <p>It is assumed that the EntityManagerFactory is properly configured
 * and managed outside this class (e.g., in a main application class).</p>
 *
 * @author Daniel San Martín
 */
public class CleanDataRepository {

    /**
     * The factory used to create EntityManager instances.
     */
    private final EntityManagerFactory emf;

    /**
     * Constructs a {@code CleanDataRepository} with the given EntityManagerFactory.
     *
     * @param emf the EntityManagerFactory to use for persistence
     */
    public CleanDataRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a {@link CleanData} instance into the database within a transaction.
     *
     * @param cleanData the validated and standardized data to persist
     * @throws RuntimeException if an error occurs during the persistence operation,
     *                          including rollback on failure
     */
    public void save(CleanData cleanData) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cleanData);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving CleanData: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
