package com.tacitknowledge.jcr.testing.utils;

import com.tacitknowledge.jcr.mocking.JcrMockService;
import com.tacitknowledge.jcr.mocking.impl.JsonMockService;
import com.tacitknowledge.jcr.testing.NodeFactory;
import com.tacitknowledge.jcr.testing.impl.MockNodeFactory;
import org.apache.commons.io.IOUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for JCR mocking
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrMockingUtils
{

    /**
     * Creates mock nodes based on JSON
     * @param jsonNodeDefinition JSON string
     * @return Node hierarchy
     * @throws RepositoryException If a repository error happens
     */
    public static Node createNodesFromJsonString(NodeTypeManager nodeTypeManager, String jsonNodeDefinition)
        throws RepositoryException
    {
        NodeFactory nodeFactory = new MockNodeFactory(nodeTypeManager, new NodeTypeResolver());
        JcrMockService mockService = new JsonMockService(nodeFactory);
        return mockService.fromString(jsonNodeDefinition);
    }


    public static Node createNodesFromJsonFile(NodeTypeManager nodeTypeManager, InputStream assetsJsonFile) throws IOException, RepositoryException
    {
        String jsonFormattedString = IOUtils.toString(assetsJsonFile);
        NodeFactory nodeFactory = new MockNodeFactory(nodeTypeManager, new NodeTypeResolver());
        JcrMockService mockService = new JsonMockService(nodeFactory);
        return mockService.fromString(jsonFormattedString);
    }
}
