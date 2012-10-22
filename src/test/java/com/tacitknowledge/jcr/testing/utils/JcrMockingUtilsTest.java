package com.tacitknowledge.jcr.testing.utils;

import org.junit.Test;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrMockingUtilsTest {

    @Test
    public void testCreateNodesFromString() throws Exception {
        String jsonNodeHierarchy = "{parentNode: " +
                                        "{childNode: " +
                                            "{" +
                                                "myFile: " +
                                                    "{" +
                                                        "nodeType: 'nt:file'," +
                                                        "'jcr:content' : " +
                                                            "{" +
                                                                "'jcr:data' : 'type:Binary, value:/files/air_jordan.jpg'" +
                                                            "}" +
                                                    "}" +
                                            "}" +
                                        "}" +
                "}";

        Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeHierarchy);
        Node parentNode = rootNode.getNode("parentNode");

        assertNotNull("Expected parentNode not to be null");

        Node fileNode = parentNode.getNode("childNode/myFile");
        assertEquals("Expected nt:file", "nt:file", fileNode.getPrimaryNodeType().getName());

        Binary binary = fileNode.getNode("jcr:content").getProperty("jcr:data").getBinary();
        assertNotNull("Expected binary not to be null", binary);
    }

    @Test
    public void shouldCreateNodeStructureFromJsonFile() throws RepositoryException {
        InputStream assetsJsonFile = getClass().getResourceAsStream("/assets.json");

        Node rootNode = JcrMockingUtils.createNodesFromJsonFile(assetsJsonFile);

        assertNotNull(rootNode);

        Node assetsNode = rootNode.getNode("digitalAsset");
        assertEquals("Expected digitalAsset node", "digitalAsset", assetsNode.getName());

        Property mimeType = assetsNode.getProperty("mimeType");
        assertEquals("Expected jpg", "jpg", mimeType.getString());

        Node binaryNode = assetsNode.getNode("binary");
        assertNotNull("Expected a non null binary", binaryNode.getProperty("jcr:content").getBinary());

    }
}
