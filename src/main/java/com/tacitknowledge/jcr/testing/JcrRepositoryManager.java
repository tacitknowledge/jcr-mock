package com.tacitknowledge.jcr.testing;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.IOException;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public interface JcrRepositoryManager {
    Repository startTransientRepository() throws IOException;

    Session getSession() throws RepositoryException;

    void shutdownRepository();

    NodeTypeManager getNodeTypeManager() throws RepositoryException;
}
