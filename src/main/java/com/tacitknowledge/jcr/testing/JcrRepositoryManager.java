package com.tacitknowledge.jcr.testing;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.IOException;

/**
 * Common interface for classes that manage a JCR Repository
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 *
 */
public interface JcrRepositoryManager {

    /**
     * Starts the managed repository for the current instance
     * @return JCR Repository
     * @throws java.io.IOException If repository cannot be started
     */
    Repository startRepository() throws IOException;

    /**
     * Returns the active session for the current managed repository
     * @return JCR Session
     * @throws RepositoryException - If a repository error happens
     */
    Session getSession() throws RepositoryException;

    /**
     * Deletes the managed repository
     */
    void shutdownRepository();

    /**
     * Returns a valid NodeTypeManager of the current managed repository
     * @return NodeTypeManager
     * @throws RepositoryException If a repository error happens
     */
    NodeTypeManager getNodeTypeManager() throws RepositoryException;
}
