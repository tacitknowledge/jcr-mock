package com.tacitknowledge.jcr.testing;

import com.tacitknowledge.jcr.testing.impl.TransientRepositoryManager;
import org.junit.After;
import org.junit.Before;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;


/**
 * Base abstract class for all JCR related unit tests.
 * Creates an in-memory repository before each test and destroys it after each test.

 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public abstract class AbstractJcrTest {
    private Repository repository;
    private static String USER = "admin";
    private static String PASSWORD = "admin";
    private Session session;
    private JcrRepositoryManager manager = new TransientRepositoryManager(REPOSITORY_CONFIG_PATH, REPOSITORY_DIRECTORY_PATH, USER, PASSWORD);

    private static final String REPOSITORY_DIRECTORY_PATH = "/jackrabbit/repository";
    private static final String REPOSITORY_CONFIG_PATH = "/jackrabbit/jackrabbit-transient.xml";

    @Before
    public void setupJcrTransientRepository() throws RepositoryException, IOException {
        repository = manager.startTransientRepository();
        session = manager.getSession();
    }

    @After
    public void shutdownTransientRepository(){
        manager.shutdownRepository();
    }

    /**
     * Retrieve the JCR session.
     * @return the current JCR session.
     */
    protected Session getSession(){
        return session;
    }

    protected Repository getRepository(){
        return repository;
    }
}
