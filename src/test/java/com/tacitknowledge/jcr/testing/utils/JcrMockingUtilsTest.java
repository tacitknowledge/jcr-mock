package com.tacitknowledge.jcr.testing.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrMockingUtilsTest {

    private InputStream assetsJsonFile;

    private String jsonNodeHierarchy =
            "{" +
                    "    parentNode: {" +
                    "        childNode: {" +
                    "            myFile: {" +
                    "                'jcr:primaryType' : 'nt:file', " +
                    "                'jcr:content' : {" +
                    "                        'jcr:data' : 'type:Binary, value:/files/air_jordan.jpg'" +
                    "                }" +
                    "            }" +
                    "        }" +
                    "    }" +
                    "}";


    @BeforeClass
    public static void setup() throws Exception {
    }

    @Test
    public void shouldCreateNodesFromString() throws Exception {
        Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeHierarchy);
        assertNodeHierarchy(rootNode);
    }

    @Test
    public void shouldCreateNodesFromStringWithoutNodeTypeManager() throws Exception {
        Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeHierarchy);
        assertNodeHierarchy(rootNode);
    }

    @Test
    public void shouldCreateNodeStructureFromJsonFile() throws RepositoryException, IOException {
        assetsJsonFile = getClass().getResourceAsStream("/assets.json");
        Node rootNode = JcrMockingUtils.createNodesFromJsonFile(assetsJsonFile);
        assertFileNodeHierarchy(rootNode);
    }

    @Test
    public void shouldCreateNodeStructureFromJsonFileWithoutNodeTypeManager() throws RepositoryException, IOException {
        assetsJsonFile = getClass().getResourceAsStream("/assets.json");
        Node rootNode = JcrMockingUtils.createNodesFromJsonFile(assetsJsonFile);
        assertFileNodeHierarchy(rootNode);
    }


    private void assertFileNodeHierarchy(Node rootNode) throws RepositoryException {
        assertNotNull(rootNode);

        Node assetsNode = rootNode.getNode("digitalAsset");
        assertEquals("Expected digitalAsset node", "digitalAsset", assetsNode.getName());

        Property mimeType = assetsNode.getProperty("mimeType");
        assertEquals("Expected jpg", "jpg", mimeType.getString());

        Node binaryNode = assetsNode.getNode("binary");
        assertNotNull("Expected a non null binary", binaryNode.getProperty("jcr:content").getBinary());
    }

    private void assertNodeHierarchy(Node rootNode) throws RepositoryException {
        Node parentNode = rootNode.getNode("parentNode");

        assertNotNull("Expected parentNode not to be null", parentNode);

        Node fileNode = parentNode.getNode("childNode/myFile");
        assertEquals("Expected nt:file", "nt:file", fileNode.getPrimaryNodeType().getName());

        Binary binary = fileNode.getNode("jcr:content").getProperty("jcr:data").getBinary();
        assertNotNull("Expected binary not to be null", binary);
    }
}
