package com.tacitknowledge.jcr.mocking;

import com.tacitknowledge.jcr.mocking.impl.JsonMockService;
import com.tacitknowledge.jcr.testing.NodeFactory;
import com.tacitknowledge.jcr.testing.impl.MockNodeFactory;
import com.tacitknowledge.jcr.testing.utils.JcrTestingUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jcr.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JcrMockServiceTest {

    private static JcrMockService mockService;
    public static final String JSON_NODE_DEFINITION_WITH_NODE_TYPES =
                    "{" +
                    "    ac2d111: {" +
                    "        trustEntity: 'type:String'," +
                    "        view: 'type:String'," +
                    "        binary: {" +
                    "            nodeType: 'nt:file'," +
                    "            'jcr:content': {" +
                    "                'jcr:data': 'type:Binary, value:/files/air_jordan.jpg'" +
                    "            }" +
                    "        }" +
                    "    }" +
                    "}";

    public static final String JSON_NODE_DEFINITION_WITH_TWO_NODES =
                    "{" +
                    "    ac2d111: {" +
                    "        trustEntity: 'type:String'," +
                    "        view: 'type:String'," +
                    "        binary: {" +
                    "            nodeType: 'nt:file'" +
                    "        }," +
                    "        anotherNode: {" +
                    "            attri: 'valueyes'" +
                    "        }" +
                    "    }" +
                    "} ";

    public static final String JSON_NODE_DEFINITION_WITH_PRIMARY_TYPE =
                    "{" +
                    "    ac2d111: {" +
                    "        trustEntity: 'type:String'," +
                    "        view: 'type:String'," +
                    "        binary: {" +
                    "            jcr:primaryType: 'nt:file'" +
                    "        }," +
                    "        anotherNode: {" +
                    "            attri: 'valueyes'" +
                    "        }" +
                    "    }" +
                    "} ";


    @BeforeClass
    public static void setup() throws RepositoryException, IOException {
        NodeFactory mockFactory = new MockNodeFactory();
        mockService = new JsonMockService(mockFactory);
    }

    @Test
    public void testJcrNodeServiceWithParentNode() throws RepositoryException {
        String jsonNodeDefinition =
                        "{" +
                        "    content: {" +
                        "        dpils: {" +
                        "            testProperty: 'myvalue'" +
                        "        }" +
                        "    }" +
                        "}";

        Node parentNode = mock(Node.class);
        mockService.fromString(parentNode, jsonNodeDefinition);

        assertNodeContents(parentNode);
    }

    @Test
    public void shouldAddItemTypeInformationToNodeStructure() throws RepositoryException {

        Node parentNode = mock(Node.class);
        mockService.fromString(parentNode, JSON_NODE_DEFINITION_WITH_NODE_TYPES);

        Node assetNode = parentNode.getNode("ac2d111");
        assertNotNull(assetNode);

        JcrTestingUtils.assertProperty(assetNode.getProperty("trustEntity"), PropertyType.STRING, StringUtils.EMPTY);
        JcrTestingUtils.assertProperty(assetNode.getProperty("view"), PropertyType.STRING, StringUtils.EMPTY);

        Node binary = assetNode.getNode("binary");
        assertNotNull("Binary folder should not be null", binary);

        Node jcrContent = binary.getNode("jcr:content");
        JcrTestingUtils.assertPropertyType(jcrContent.getProperty("jcr:data"), PropertyType.BINARY);
    }

    @Test
    public void shouldCreateFullJsonStructureFromFile() throws IOException, RepositoryException {
        String jsonString = IOUtils.toString(getClass().getResourceAsStream("/mock-nodes.json"));
        assertNotNull(jsonString);

        Node parentNode = mock(Node.class);
        mockService.fromString(parentNode, jsonString);

        Node ac2d111 = parentNode.getNode("ac2d111");
        Node ac2d112 = parentNode.getNode("ac2d112");
        Node ac2d113 = parentNode.getNode("ac2d113");

        assertNotNull(ac2d111);
        assertNotNull(ac2d112);
        assertNotNull(ac2d113);
    }

    @Test
    public void testDeepHierarchies() throws RepositoryException {


        Node parentNode = mock(Node.class);
        mockService.fromString(parentNode, JSON_NODE_DEFINITION_WITH_NODE_TYPES);

        Node ac2d111Node = parentNode.getNode("ac2d111");

        Node binaryNode = parentNode.getNode("ac2d111/binary");
        assertEquals("Expected both nodes to be the same", binaryNode, ac2d111Node.getNode("binary"));

        Node jcrContentNode = parentNode.getNode("ac2d111/binary/jcr:content");
        assertEquals("Expected both nodes to be the same", jcrContentNode, binaryNode.getNode("jcr:content"));


        assertNotNull("Binary node should not be null", jcrContentNode);

    }

    @Test(expected = NoSuchElementException.class)
    public void shouldReturnWorkingNodeIterator() throws RepositoryException {

        Node parentNode = mock(Node.class);

        mockService.fromString(parentNode, JSON_NODE_DEFINITION_WITH_TWO_NODES);

        Node ac2d111Node = parentNode.getNode("ac2d111");

        NodeIterator childNodeIterator = ac2d111Node.getNodes();

        assertTrue("Should have one node", childNodeIterator.hasNext());
        assertEquals("Binary node should exist", ac2d111Node.getNode("binary"), childNodeIterator.nextNode());

        assertTrue("Should not have anymore nodes", childNodeIterator.hasNext());
        assertEquals("anotherNode node should exist", ac2d111Node.getNode("anotherNode"), childNodeIterator.nextNode());

        assertFalse("Should not have anymore nodes", childNodeIterator.hasNext());

        childNodeIterator.nextNode();

        fail("An exception should have been thrown");
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorShouldWorkWithNodesWithoutChildren() throws RepositoryException {


        Node parentNode = mock(Node.class);

        mockService.fromString(parentNode, JSON_NODE_DEFINITION_WITH_TWO_NODES);

        Node ac2d111Node = parentNode.getNode("ac2d111");

        NodeIterator anotherNodeIterator = ac2d111Node.getNode("anotherNode").getNodes();

        assertFalse("Should not have children", anotherNodeIterator.hasNext());

        anotherNodeIterator.nextNode();
        fail("An exception should have been thrown");
    }

    @Test
    public void shouldSetBinaryValue() throws RepositoryException {
        String jsonNodeDefinitionWithNodeTypes = 
                "{" +
                "    ac2d111: {" +
                "        trustEntity: 'type:String'," +
                "        view: 'type:String'," +
                "        binary: {" +
                "            nodeType: 'nt:file'," +
                "            'jcr:content': {" +
                "                'jcr:data': 'type:Binary, value:/files/air_jordan.jpg'" +
                "            }" +
                "        }," +
                "        anotherNode: {" +
                "            attri: 'valueyes'" +
                "        }" +
                "    }" +
                "} ";

        Node parentNode = mock(Node.class);

        mockService.fromString(parentNode, jsonNodeDefinitionWithNodeTypes);

        Node ac2d111Node = parentNode.getNode("ac2d111");

        Node jcrContentNode = ac2d111Node.getNode("binary/jcr:content");

        Property jcrData = jcrContentNode.getProperty("jcr:data");

        Binary airJordanBinary = jcrData.getValue().getBinary();
        InputStream airJordanInputStream = airJordanBinary.getStream();

        assertNotNull("Binary should not be null", airJordanBinary);
        assertNotNull("InputStream Should not be null either", airJordanInputStream);
    }

    @Test
    public void shouldCreateNodeStructureWithoutPassingAParentNode() throws RepositoryException {
        String jsonNodeStructure =
                "{" +
                "    products: {" +
                "        productA: {" +
                "            name: 'Air Jordan'," +
                "            confidentiality: 'Bronze'," +
                "            digitalAssets: {" +
                "                asset1: {" +
                "                    mimeType: 'jpg'," +
                "                    contentType: 'photography'," +
                "                    binary: 'type:Binary, value:/files/air_jordan.jpg'" +
                "                }" +
                "            }" +
                "        }" +
                "    }" +
                "}";

        Node rootNode = mockService.fromString(jsonNodeStructure);
        assertNotNull(rootNode);

        Node productsNode = rootNode.getNode("products");

        assertEquals("Expecting the node be named 'products' but is : " + productsNode.getName(), "products", productsNode.getName());

        Node productAnode = productsNode.getNode("productA");
        assertNotNull(productAnode);
        JcrTestingUtils.assertIteratorCount(productAnode.getNodes(), 1);


        Node digitalAssetsNode = productAnode.getNode("digitalAssets");
        JcrTestingUtils.assertIteratorCount(digitalAssetsNode.getNodes(), 1);

        Node asset1Node = productAnode.getNode("digitalAssets/asset1");
        Property mimeTypeProperty = asset1Node.getProperty("mimeType");
        assertEquals("Expected a jpg", "jpg", mimeTypeProperty.getValue().getString());

    }

    @Test
    public void shouldHandleReferences() throws IOException, RepositoryException {
        String nodeStructureWithArrays = IOUtils.toString(getClass().getResourceAsStream("/asset_list.json"));
        Node rootNode = mockService.fromString(nodeStructureWithArrays);

        assertNotNull("Expected nodestructure to be not null", rootNode);

        Node productsParentNode = rootNode.getNode("products");
        assertNotNull("Expected products node to be not null", productsParentNode);

        List<Node> productNodeList = IteratorUtils.toList(productsParentNode.getNodes());

        Node firstProduct = productNodeList.get(0);
        List<Node> digitalAssetList = IteratorUtils.toList(firstProduct.getNode("digitalAssets").getNodes());

        Node digitalAsset = digitalAssetList.get(0);

        Property binaryReference = digitalAsset.getProperty("binary_ref");
        assertEquals("Expected 123456", "123456", binaryReference.getString());
        assertEquals("Expected Reference", PropertyType.REFERENCE, binaryReference.getType());
    }


    private void assertNodeContents(Node parentNode) throws RepositoryException {
        Node contentNode = parentNode.getNode("content");
        assertNotNull("Node is null", contentNode);
        assertEquals("Names don't match", "content", contentNode.getName());

        Node dpilsNode = contentNode.getNode("dpils");
        assertNotNull("Node is null", dpilsNode);
        assertEquals("Names don't match", "dpils", dpilsNode.getName());

        Property testProperty = dpilsNode.getProperty("testProperty");
        assertNotNull("Peoperty is null", testProperty);
        assertEquals("Property Name doesn't match", "testProperty", testProperty.getName());
        assertEquals("Property Value doesn't match", "myvalue", testProperty.getString());
    }

    @Test
    public void shouldBeAbleToIterateOnNodesMultipleTimes() throws RepositoryException {
        String jsonNodeStructure =
                "{" +
                "    products: {" +
                "        productA: {" +
                "            name: 'Air Jordan'," +
                "            confidentiality: 'Bronze'," +
                "            someNode: {}," +
                "            digitalAssets: {" +
                "                asset1: {" +
                "                    mimeType: 'jpg'," +
                "                    contentType: 'photography'," +
                "                    binary: 'type:Binary, value:/files/air_jordan.jpg'" +
                "                }" +
                "            }" +
                "        }" +
                "    }" +
                "}";

        Node rootNode = mockService.fromString(jsonNodeStructure);
        assertNotNull(rootNode);

        Node productANode = rootNode.getNode("products/productA");

        assertNotNull(productANode);

        //Call to JcrTestingUtils.assertIteratorCount will traverse the iterator
        NodeIterator productAIterator = productANode.getNodes();
        JcrTestingUtils.assertIteratorCount(productAIterator, 2);

        //Traversing the iterator again should result in no nodes found
        JcrTestingUtils.assertIteratorCount(productAIterator, 0);

        //Calling getNodes() should return a fresh iterator which we can use to traverse the node tree again.
        JcrTestingUtils.assertIteratorCount(productANode.getNodes(), 2);

    }

    @Test
    public void shouldBeAbleToRetrieveNestedPropertyFromAncestors() throws RepositoryException {
        String jsonNodeStructure =
                "{" +
                "    products: {" +
                "        productA: {" +
                "            name: 'Air Jordan'," +
                "            confidentiality: 'Bronze'," +
                "            someNode: {}," +
                "            digitalAssets: {" +
                "                asset1: {" +
                "                    mimeType: 'jpg'," +
                "                    contentType: 'photography'," +
                "                    binary: 'type:Binary, value:/files/air_jordan.jpg'" +
                "                }" +
                "            }" +
                "        }" +
                "    }" +
                "}";

        Node rootNode = mockService.fromString(jsonNodeStructure);
        assertNotNull(rootNode);

        Property productAName = rootNode.getProperty("products/productA/name");

        assertNotNull(productAName);
        assertEquals("Expected name to be 'Air Jordan'", "Air Jordan", productAName.getString());

        Node digitalAssetsNode = rootNode.getNode("products/productA/digitalAssets");
        Property contentTypeProperty = digitalAssetsNode.getProperty("asset1/contentType");

        assertNotNull(contentTypeProperty);
        assertEquals("Expected content type to be 'photography'", "photography", contentTypeProperty.getValue().getString());
    }


}
