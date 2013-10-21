package com.tacitknowledge.jcr.testing.impl;

import com.tacitknowledge.jcr.testing.NodeFactory;
import com.tacitknowledge.jcr.testing.utils.PropertyTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.*;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockNodeFactoryTest {

    public static final String PROPERTY_NAME = "Name";
    private MockNodeFactory nodeFactory;
    private Node parent;
    private String name;
    private String propertyValue;
    private int propertyType;
    private Property property;
    private PropertyDefinition propertyDefinition;

    @Before
    public void setUp() throws Exception {
        nodeFactory = new MockNodeFactory();
        parent = mock(Node.class);
        name = "name";
        propertyValue = "propertyValue";
        propertyType = 1;
        property = mock(Property.class);
        propertyDefinition = mock(PropertyDefinition.class);

        when(propertyDefinition.getName()).thenReturn(PROPERTY_NAME);
        when(propertyDefinition.getRequiredType()).thenReturn(1);
    }

    @Test
    public void shouldCreateProperty() throws RepositoryException {
        nodeFactory.createProperty(parent, name, propertyValue, propertyType);

        Property property = parent.getProperty(name);

        assertNotNull(property);
        assertEquals(propertyType, property.getType());
        assertEquals(name, property.getName());
        assertTrue(parent.hasProperty(name));
        assertTrue(parent.hasProperties());

        Value value = property.getValue();
        assertEquals(propertyValue, value.getString());

    }

    @Test
    public void shouldAddValueToAlreadyCreatedProperty() throws RepositoryException {
        when(parent.getProperty(name)).thenReturn(property);

        nodeFactory.createProperty(parent, name, propertyValue, propertyType);

        assertNotNull(property.getValue());
        assertTrue(parent.hasProperty(name));
        assertTrue(parent.hasProperties());
        assertNotNull(property.getSession());
    }

    @Test
    public void shouldDoNothingIfPropertyIsAlreadySetup() throws RepositoryException {
        when(parent.getProperty(name)).thenReturn(property);
        Value value = mock(Value.class);
        when(property.getValue()).thenReturn(value);

        nodeFactory.createProperty(parent, name, propertyValue, propertyType);

        assertEquals(value, property.getValue());
        assertTrue(parent.hasProperty(name));
        assertTrue(parent.hasProperties());
        assertNotNull(property.getSession());

    }

    @Test
    public void shouldCreateNodeWithNullNodeType() throws RepositoryException {
        NodeType nodeType = null;
        Node childNode = nodeFactory.createNode(parent, name, nodeType);
        assertNotNull(childNode);
        assertNotNull(childNode.getSession());
    }

    @Test
    public void shouldCreateNodeWithNodeType() throws RepositoryException {
        NodeType nodeType = mock(NodeType.class);
        Node childNode = nodeFactory.createNode(parent, name, nodeType);
        assertTrue(childNode.isNodeType(nodeType.getName()));
        assertEquals(nodeType, childNode.getPrimaryNodeType());
        assertNotNull(childNode);
        assertNotNull(childNode.getSession());

    }

    @Test
    public void shouldCreateNodeIfParentIsNull() throws RepositoryException {
        Node childNode = nodeFactory.createNode(null, name);
        assertNotNull(childNode);
        assertEquals(name, childNode.getName());
        assertTrue(childNode.isNode());
        assertNotNull(childNode.getSession());
    }

    @Test
    public void shouldNotCreateChildNodeIfAlreadyExist() throws RepositoryException {
        Node childNode = mock(Node.class);
        when(parent.getNode(name)).thenReturn(childNode);
        Node actual = nodeFactory.createNode(parent, name);
        assertEquals(childNode, actual);
        assertNotNull(actual.getSession());
    }

    @Test
    public void shouldCreateIterator() throws RepositoryException {
        List<Node> childNodes = new ArrayList<Node>();
        nodeFactory.createIteratorFor(parent, childNodes);
        assertNotNull(parent.getNodes());
        assertNotNull(parent.getSession());
    }

    @Test
    public void shouldCreateNewIteratorOnEachCallToGetNodes() throws RepositoryException {
        List<Node> childNodes = new ArrayList<Node>();
        nodeFactory.createIteratorFor(parent, childNodes);
        NodeIterator firstNodeIterator = parent.getNodes();
        NodeIterator secondNodeIterator = parent.getNodes();
        assertNotSame("Consecutive calls to getNodes() should return different iterator object", firstNodeIterator, secondNodeIterator);
    }

	@Test
	public void shouldCreatePropertyIterator() throws RepositoryException {
		Property firstProperty = mock(Property.class);
		Property secondProperty = mock(Property.class);
		Property thirdProperty = mock(Property.class);
		Property fourthProperty = mock(Property.class);
		Property fifthProperty = mock(Property.class);
		Property[] properties = {firstProperty, secondProperty, thirdProperty, fourthProperty, fifthProperty};
		List<Property> propertyList = Arrays.asList(properties);
		nodeFactory.createPropertyIteratorFor(parent, propertyList);
		PropertyIterator propertyIterator = parent.getProperties();
		assertNotNull(propertyIterator);

		//make sure we can correctly iterate over the properties
		int i = 0;
		while(propertyIterator.hasNext()) {
			Property propertyFromIterator = propertyIterator.nextProperty();
			Property propertyFromList = propertyList.get(i);
			assertEquals(propertyFromList, propertyFromIterator);
			i++;
		}
	}


	@Test
	public void shouldCreateNewPropertyIteratorOnEachCallToGetNodes() throws RepositoryException {
		List<Property> childNodes = new ArrayList<Property>();
		nodeFactory.createPropertyIteratorFor(parent, childNodes);
		PropertyIterator firstPropertyIterator = parent.getProperties();
		PropertyIterator secondPropertyIterator = parent.getProperties();
		assertNotSame("Consecutive calls to getProperties() should return different iterator object", firstPropertyIterator, secondPropertyIterator);
	}


    @Test
    public void shouldCreateBinaryValue() throws RepositoryException {

        nodeFactory.createValueFor(property, "/files/air_jordan.jpg", PropertyType.BINARY);

        assertNotNull(property.getBinary());
        assertNotNull(property.getSession());
    }

    @Test
    public void shouldCreateBooleanValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "true", PropertyType.BOOLEAN);
        assertTrue(property.getBoolean());
        assertNotNull(property.getSession());

    }

    @Test
    public void shouldCreateDoubleValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "1.0", PropertyType.DOUBLE);
        assertEquals(1.0, property.getDouble());
        assertNotNull(property.getSession());

    }

    @Test
    public void shouldCreateDecimalValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "1.01", PropertyType.DECIMAL);
        assertEquals(new BigDecimal("1.01"), property.getDecimal());
        assertNotNull(property.getSession());

    }

    @Test
    public void shouldCreateDateValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "12/12/2012", PropertyType.DATE);
        assertNotNull(property.getDate());
        assertNotNull(property.getString());
        assertNotNull(property.getSession());

    }

    @Test
    public void shouldThrowParseExceptionWhenDateIsInvalidFormat() throws RepositoryException {
        try {
            nodeFactory.createValueFor(property, "invalidDate", PropertyType.DATE);
            fail("Should have thrown runtime exception");
        } catch (RuntimeException re) {
            // runtime exception expected to happen
        }
    }


    @Test
    public void shouldCreateStringValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "stringValue", 20);
        assertEquals("stringValue", property.getString());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfBinaryDoesNotExist() throws RepositoryException {
        try {
            nodeFactory.createValueFor(property, "does not exist", PropertyType.BINARY);
            fail("Should have thrown illegal argument exception");

        } catch (IllegalArgumentException iae) {
            //expected
        }

    }

    @Test
    public void shouldCreateNodeFromNodeType() throws RepositoryException {
        NodeType nodeType = mock(NodeType.class);
        PropertyDefinition[] propertyDefinitions = new PropertyDefinition[]{propertyDefinition};
        String[] primaryTypeNames = new String[]{"primary"};
        NodeDefinition nodeDefinition = mock(NodeDefinition.class);
        NodeType mockNodeType = mock(NodeType.class);
        NodeDefinition[] nodeDefinitions = new NodeDefinition[]{nodeDefinition};
        NodeDefinition[] emptyDefinitions = new NodeDefinition[]{};

        when(nodeType.getPropertyDefinitions()).thenReturn(propertyDefinitions);
        when(nodeDefinition.getDeclaringNodeType()).thenReturn(mockNodeType);
        when(nodeDefinition.getRequiredPrimaryTypeNames()).thenReturn(primaryTypeNames);
        when(nodeType.getChildNodeDefinitions()).thenReturn(nodeDefinitions).thenReturn(emptyDefinitions);

        Node childNode = nodeFactory.createNode(parent, name, "nodeType");

        assertNotNull(childNode);
        assertNotNull(childNode.getSession());

    }

    @Test
    public void callToGetPathShouldReturnAbsolutePath() throws RepositoryException
    {
        NodeFactory myNodeFactory = new MockNodeFactory();
        Node rootNode = myNodeFactory.createNode(StringUtils.EMPTY);

        Node firstLevelNode = myNodeFactory.createNode(rootNode, "firstLevel");
        Node secondLevelNode = myNodeFactory.createNode(firstLevelNode, "secondLevel");

        myNodeFactory.createProperty(secondLevelNode, "thirdLevelProp", "some value", PropertyTypeEnum.STRING.getPropertyType());
        Property thirdLevelProp = secondLevelNode.getProperty("thirdLevelProp");

        assertEquals("Expected path to be /", "/", rootNode.getPath());
	    assertEquals("Expected path to be /", "/", rootNode.toString());
        assertEquals("Expected path to be /firstLevel", "/firstLevel", firstLevelNode.getPath());
	    assertEquals("Expected path to be /firstLevel", "/firstLevel", firstLevelNode.toString());
        assertEquals("Expected path to be /firstLevel/secondLevel", "/firstLevel/secondLevel", secondLevelNode.getPath());
	    assertEquals("Expected path to be /firstLevel/secondLevel", "/firstLevel/secondLevel", secondLevelNode.toString());
        assertEquals("Expected path to be /firstLevel/secondLevel/thirdLevelProp", "/firstLevel/secondLevel/thirdLevelProp", thirdLevelProp.getPath());
	    assertEquals("Expected path to be /firstLevel/secondLevel/thirdLevelProp", "/firstLevel/secondLevel/thirdLevelProp", thirdLevelProp.toString());
    }

    @Test
    public void shouldRetrievePropertyFromAllAscendantNodes() throws RepositoryException
    {
        Node rootNode = nodeFactory.createNode(StringUtils.EMPTY);

        Node firstLevelNode = nodeFactory.createNode(rootNode, "firstLevel");
        Node secondLevelNode = nodeFactory.createNode(firstLevelNode, "secondLevel");

        nodeFactory.createProperty(secondLevelNode, "thirdLevelProp", "some value", PropertyTypeEnum.STRING.getPropertyType());

        Property thirdLevelProp = firstLevelNode.getProperty("secondLevel/thirdLevelProp");
        assertNotNull("Expected property to be not null", thirdLevelProp);
        assertEquals("Expected property value to be 'some value'", "some value", thirdLevelProp.getString());
        assertEquals("Expected property value to be 'some value'", "some value", thirdLevelProp.getValue().getString());
	    assertEquals("Expected hasProperty() to return true", true, firstLevelNode.hasProperty("secondLevel/thirdLevelProp"));

        Property propertyFromRootNode = rootNode.getProperty("firstLevel/secondLevel/thirdLevelProp");
        assertNotNull("Expected property to be not null", propertyFromRootNode);
        assertEquals("Expected property value to be 'some value'", "some value", propertyFromRootNode.getString());
        assertEquals("Expected property value to be 'some value'", "some value", propertyFromRootNode.getValue().getString());
	    assertEquals("Expected hasProperty() to return true", true, rootNode.hasProperty("firstLevel/secondLevel/thirdLevelProp"));
	    assertEquals("Expected hasProperty() to return false", false, rootNode.hasProperty("secondLevel/thirdLevelProp"));
    }

	@Test
	public void shouldRetrieveNodeFromAllAscendantNodes() throws RepositoryException
	{
		Node rootNode = nodeFactory.createNode(StringUtils.EMPTY);

		Node firstLevelNode = nodeFactory.createNode(rootNode, "firstLevel");
		Node secondLevelNode = nodeFactory.createNode(firstLevelNode, "secondLevel");
		Node thirdLevelNode = nodeFactory.createNode(secondLevelNode, "thirdLevel");

		assertTrue("Expected root node to have firstLevel/secondLevel/thirdLevel)", rootNode.hasNode("firstLevel/secondLevel/thirdLevel"));
		assertFalse("Expected root node to NOT have secondLevel/thirdLevel)", rootNode.hasNode("secondLevel/thirdLevel"));
		assertTrue("Expected root node to have firstLevel/secondLevel)", rootNode.hasNode("firstLevel/secondLevel"));
		assertEquals("Expected root node to access firstLevel/secondLevel/thirdLevel)", rootNode.getNode("firstLevel/secondLevel/thirdLevel"), thirdLevelNode);
		assertEquals("Expected root node to access firstLevel/secondLevel)", rootNode.getNode("firstLevel/secondLevel"), secondLevelNode);

		assertTrue("Expected first level node to have secondLevel/thirdLevel)", firstLevelNode.hasNode("secondLevel/thirdLevel"));
		assertFalse("Expected first level node to NOT have firstLevel/secondLevel/thirdLevel)", firstLevelNode.hasNode("firstLevel/secondLevel/thirdLevel"));
		assertTrue("Expected first level node to have secondLevel)", firstLevelNode.hasNode("secondLevel"));
		assertEquals("Expected first level node to access secondLevel/thirdLevel)", firstLevelNode.getNode("secondLevel/thirdLevel"), thirdLevelNode);
		assertEquals("Expected first level node to access secondLevel)", firstLevelNode.getNode("secondLevel"), secondLevelNode);
	}
}
