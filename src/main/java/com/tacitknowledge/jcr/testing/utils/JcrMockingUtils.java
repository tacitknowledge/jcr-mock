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

    public static Node createNodesFromJsonString(String jsonNodeDefinition) throws RepositoryException, IOException
    {
        NodeFactory nodeFactory = new MockNodeFactory();
        JcrMockService mockService = new JsonMockService(nodeFactory);
        return mockService.fromString(jsonNodeDefinition);
    }

    public static Node createNodesFromJsonFile(InputStream assetsJsonFile) throws IOException, RepositoryException
    {
        String jsonFormattedString = IOUtils.toString(assetsJsonFile);
        NodeFactory nodeFactory = new MockNodeFactory();
        JcrMockService mockService = new JsonMockService(nodeFactory);
        return mockService.fromString(jsonFormattedString);
    }

}
