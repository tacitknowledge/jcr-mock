package com.tacitknowledge.jcr.mocking;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author Daniel Valencia (Daniel.Valencia@nike.com)
 */
public interface JcrMockService {

    Node fromString(Node parentNode, String nodeDefinition) throws RepositoryException;

    Node fromString(String jsonNodeStructure) throws RepositoryException;
}
