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

import static org.junit.Assert.*;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrRepositoryRunnerTest
{

    private static final String repositoryConfigPath = "/jackrabbit/jackrabbit-transient.xml";

    private static final String repositoryDirectoryPath = "/jackrabbit/repository";

    private static final String userName = "admin";

    private static final String password = "admin";

    @Test
    public void shouldCreateRepository() throws IOException, RepositoryException
    {
        JcrRepositoryManager manager = new TransientRepositoryManager(repositoryConfigPath, repositoryDirectoryPath,
                userName, password);
        Repository repository = manager.startRepository();
        Session session = manager.getSession();
        NodeTypeManager nodeTypeManager = manager.getNodeTypeManager();

        assertNotNull("Repository should not be null", repository);
        assertNotNull("Session should not be null", session);
        assertNotNull("NodeTypeManager should not be null", nodeTypeManager);
        assertEquals("We should get the same repository instance every time", repository, manager.startRepository());
        assertSame("Node type manager should be the same", nodeTypeManager, manager.getNodeTypeManager());

        manager.shutdownRepository();
    }

    @Test
    public void shouldGetConsistentJcrSession() throws Exception
    {
         JcrRepositoryManager manager = new TransientRepositoryManager(repositoryConfigPath, repositoryDirectoryPath,
                userName, password);
        manager.startRepository();
        Session session = manager.getSession();
        assertSame(session, manager.getSession());
        session.logout();
        assertNotSame(session, manager.getSession());
        manager.shutdownRepository();
    }

    @Test
    public void shouldCreateNodeTypeManagerAfterShuttingDownRepository() throws RepositoryException, IOException
    {
        NodeTypeManager nodeTypeManager = TransientRepositoryManager.createNodeTypeManager(repositoryConfigPath,
                repositoryDirectoryPath, userName, password);
        assertNotNull(nodeTypeManager);

        NodeType ntFileNodeType = nodeTypeManager.getNodeType("nt:file");
        assertNotNull(ntFileNodeType);

        NodeDefinition[] nodeDefinitions = ntFileNodeType.getChildNodeDefinitions();
        for (NodeDefinition nodeDefinition : nodeDefinitions)
        {
            String nodeName = nodeDefinition.getName();
            assertFalse("Node name should not be empty", StringUtils.isEmpty(nodeName));
        }

        PropertyDefinition[] propertyDefinitions = ntFileNodeType.getPropertyDefinitions();
        for (PropertyDefinition propertyDefinition : propertyDefinitions)
        {
            String propName = propertyDefinition.getName();
            assertFalse("PropertyName should not be empty", StringUtils.isEmpty(propName));
        }
    }
}
