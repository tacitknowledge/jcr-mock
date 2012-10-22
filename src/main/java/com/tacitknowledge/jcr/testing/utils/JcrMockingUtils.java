package com.tacitknowledge.jcr.testing.utils;

import com.tacitknowledge.jcr.mocking.JcrMockService;
import com.tacitknowledge.jcr.mocking.impl.JsonMockService;
import com.tacitknowledge.jcr.testing.NodeFactory;
import com.tacitknowledge.jcr.testing.impl.MockNodeFactory;
import com.tacitknowledge.jcr.testing.impl.TransientRepositoryManager;
import org.apache.commons.io.IOUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrMockingUtils {

    private static NodeTypeManager nodeTypeManager;

    static {
        try {
            nodeTypeManager = TransientRepositoryManager.createNodeTypeManager();
        } catch (Exception e) {
            throw new RuntimeException("Problem ocurred initializing transient repsitory", e);
        }
    }

    public static Node createNodesFromJsonString(String jsonNodeDefinition){
        try {
            NodeFactory nodeFactory = new MockNodeFactory(nodeTypeManager, new NodeTypeResolver());
            JcrMockService mockService = new JsonMockService(nodeFactory);
            return mockService.fromString(jsonNodeDefinition);

        } catch (RepositoryException e){
            throw new RuntimeException("Problem converting json string to node hierarchy", e);
        }
    }

    public static Node createNodesFromJsonFile(InputStream assetsJsonFile) {
        try {
            String jsonFormattedString = IOUtils.toString(assetsJsonFile);
            NodeFactory nodeFactory = new MockNodeFactory(nodeTypeManager, new NodeTypeResolver());
            JcrMockService mockService = new JsonMockService(nodeFactory);
            try {
                return mockService.fromString(jsonFormattedString);
            } catch (RepositoryException e) {
                throw new RuntimeException("Problem converting json string to node hierarchy: ", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem reading input stream", e);
        }
    }
}
