package com.tacitknowledge.jcr.testing.impl;

import com.tacitknowledge.jcr.testing.JcrRepositoryManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrRepositoryRunnerTest {

    private static String repositoryConfigPath = "/jackrabbit/jackrabbit-transient.xml";
    private static String repositoryDirectoryPath = "/jackrabbit/repository";
    private static String userName = "admin";
    private static String password = "admin";

    @Test
    public void testRepositoryCreation() throws IOException, RepositoryException {
        JcrRepositoryManager manager = new TransientRepositoryManager(repositoryConfigPath, repositoryDirectoryPath, userName, password);
        Repository repository = manager.startTransientRepository();
        Session session = manager.getSession();
        NodeTypeManager nodeTypeManager = manager.getNodeTypeManager();

        assertNotNull("Expected repository to be not null", repository);
        assertNotNull("Expected session to be not null", session);
        assertNotNull("Expected nodeTypeManager to be not null", nodeTypeManager);

        manager.shutdownRepository();
    }

    @Test
    public void tesCreateNodeTypeManagerAfterShuttingDownRepository() throws RepositoryException, IOException {
        NodeTypeManager nodeTypeManager = TransientRepositoryManager.createNodeTypeManager(repositoryConfigPath, repositoryDirectoryPath, userName, password);
        assertNotNull(nodeTypeManager);

        NodeType ntFileNodeType = nodeTypeManager.getNodeType("nt:file");
        assertNotNull(ntFileNodeType);

        NodeDefinition[] nodeDefinitions = ntFileNodeType.getChildNodeDefinitions();
        for(NodeDefinition nodeDefinition : nodeDefinitions){
            String nodeName = nodeDefinition.getName();
            assertFalse("Node name should not be empty", StringUtils.isEmpty(nodeName));
        }

        PropertyDefinition[] propertyDefinitions = ntFileNodeType.getPropertyDefinitions();
        for(PropertyDefinition propertyDefinition: propertyDefinitions){
            String propName = propertyDefinition.getName();
            assertFalse("PropertyName should not be empty", StringUtils.isEmpty(propName));
        }
    }
}
