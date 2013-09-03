package com.tacitknowledge.jcr.testing.impl;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.*;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockNodeFactoryTest {

    private NodeTypeManager nodeTypeManager;
    private MockNodeFactory nodeFactory;
    private Node parent;
    private String name;
    private String propertyValue;
    private int propertyType;
    private Property property;
    private PropertyDefinition propertyDefinition;

    @Before
    public void setUp() throws Exception {
        nodeTypeManager = mock(NodeTypeManager.class);
        nodeFactory = new MockNodeFactory(nodeTypeManager);
        parent = mock(Node.class);
        name = "name";
        propertyValue = "propertyValue";
        propertyType = 1;
        property = mock(Property.class);
        propertyDefinition = mock(PropertyDefinition.class);

        when(propertyDefinition.getName()).thenReturn("Name");
        when(propertyDefinition.getRequiredType()).thenReturn(1);
    }

    @Test
    public void shouldCreateProperty() throws RepositoryException {
        nodeFactory.createProperty(parent, name, propertyValue, propertyType);

        Property property = parent.getProperty(name);

        assertNotNull(property);
        assertEquals(propertyType, property.getType());
        assertEquals(name, property.getName());

        Value value = property.getValue();
        assertEquals(propertyValue, value.getString());

    }

    @Test
    public void shouldAddValueToAlreadyCreatedProperty() throws RepositoryException {
        when(parent.getProperty(name)).thenReturn(property);

        nodeFactory.createProperty(parent, name, propertyValue, propertyType);

        assertNotNull(property.getValue());
    }

    @Test
    public void shouldDoNothingIfPropertyIsAlreadySetup() throws RepositoryException {
        when(parent.getProperty(name)).thenReturn(property);
        Value value = mock(Value.class);
        when(property.getValue()).thenReturn(value);

        nodeFactory.createProperty(parent, name, propertyValue, propertyType);

        assertEquals(value, property.getValue());

    }

    @Test
    public void shouldCreateNodeWithNullNodeType() throws RepositoryException {

        NodeType nodeType = null;
        Node childNode = nodeFactory.createNode(parent, name, nodeType);
        assertNotNull(childNode);
    }

    @Test
    public void shouldCreateNodeWithNodeType() throws RepositoryException {
        NodeType nodeType = mock(NodeType.class);
        Node childNode = nodeFactory.createNode(parent, name, nodeType);
        assertTrue(childNode.isNodeType(nodeType.getName()));
        assertEquals(nodeType, childNode.getPrimaryNodeType());
        assertNotNull(childNode);
    }

    @Test
    public void shouldCreateNodeIfParentIsNull() throws RepositoryException {
        Node childNode = nodeFactory.createNode(null, name);
        assertNotNull(childNode);
        assertEquals(name, childNode.getName());
        assertTrue(childNode.isNode());
    }

    @Test
    public void shouldNotCreateChildNodeIfAlreadyExist() throws RepositoryException {
        Node childNode = mock(Node.class);
        when(parent.getNode(name)).thenReturn(childNode);
        Node actual = nodeFactory.createNode(parent, name);
        assertEquals(childNode, actual);
    }

    @Test
    public void shouldCreatePropertyFromDefinitionWithNullDefaultValues() throws RepositoryException {
        Value[] defaultValues = null;
        when(propertyDefinition.getDefaultValues()).thenReturn(defaultValues);
        nodeFactory.createPropertyFromDefinition(parent, propertyDefinition);
        Property actualProperty = parent.getProperty("Name");
        assertNotNull(actualProperty);
        assertEquals(1, actualProperty.getType());
    }

    @Test
    public void shouldCreatePropertyFromDefinitionWithDefaultValues() throws RepositoryException {
        Value value = mock(Value.class);
        Value[] defaultValues = {value};
        when(propertyDefinition.getDefaultValues()).thenReturn(defaultValues);
        nodeFactory.createPropertyFromDefinition(parent, propertyDefinition);
        Property actualProperty = parent.getProperty("Name");
        assertNotNull(actualProperty);
        assertEquals(1, actualProperty.getType());
        assertEquals(value, actualProperty.getValue());
    }

    @Test
    public void shouldCreatePropertyFromDefinitionWithDefaultValuesEmpty() throws RepositoryException {
        Value[] defaultValues = {};
        when(propertyDefinition.getDefaultValues()).thenReturn(defaultValues);
        nodeFactory.createPropertyFromDefinition(parent, propertyDefinition);
        Property actualProperty = parent.getProperty("Name");
        assertNotNull(actualProperty);
        assertEquals(1, actualProperty.getType());
    }

    @Test
    public void shouldCreatePropertyFromDefinitionWithDefaultValuesMultipleProperties() throws RepositoryException {
        Value[] defaultValues = {};
        when(propertyDefinition.getDefaultValues()).thenReturn(defaultValues);
        when(propertyDefinition.isMultiple()).thenReturn(true);
        nodeFactory.createPropertyFromDefinition(parent, propertyDefinition);
        Property actualProperty = parent.getProperty("Name");
        assertNotNull(actualProperty);
        assertEquals(1, actualProperty.getType());
        assertTrue(actualProperty.isMultiple());
        assertEquals(defaultValues, actualProperty.getValues());
    }

    @Test
    public void shouldCreateIterator() throws RepositoryException {
        List<Node> childNodes = new ArrayList<Node>();
        nodeFactory.createIteratorFor(parent, childNodes);
        assertNotNull(parent.getNodes());
    }

    @Test
    public void shouldCreateBinaryValue() throws RepositoryException {

        nodeFactory.createValueFor(property, "/files/air_jordan.jpg", PropertyType.BINARY);

        assertNotNull(property.getBinary());
    }

    @Test
    public void shouldCreateBooleanValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "true", PropertyType.BOOLEAN);
        assertTrue(property.getBoolean());
    }

    @Test
    public void shouldCreateDoubleValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "1.0", PropertyType.DOUBLE);
        assertEquals(1.0, property.getDouble());
    }

    @Test
    public void shouldCreateDecimalValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "1.01", PropertyType.DECIMAL);
        assertEquals(new BigDecimal("1.01"), property.getDecimal());
    }

    @Test
    public void shouldCreateDateValue() throws RepositoryException {
        nodeFactory.createValueFor(property, "12/12/2012", PropertyType.DATE);
        assertNotNull(property.getDate());
        assertNotNull(property.getString());
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
        when(nodeTypeManager.getNodeType("nodeType")).thenReturn(nodeType);
        when(nodeTypeManager.getNodeType("primary")).thenReturn(nodeType);
        when(nodeDefinition.getDeclaringNodeType()).thenReturn(mockNodeType);
        when(nodeDefinition.getRequiredPrimaryTypeNames()).thenReturn(primaryTypeNames);
        when(nodeType.getChildNodeDefinitions()).thenReturn(nodeDefinitions).thenReturn(emptyDefinitions);

        Node childNode = nodeFactory.createNode(parent, name, "nodeType");

        assertNotNull(childNode);
    }

    @Test
    public void shouldThrowExceptionIfRepositoryExceptionOccurs() throws RepositoryException {
        RepositoryException repositoryException = mock(RepositoryException.class);
        when(nodeTypeManager.getNodeType("nodeType")).thenThrow(repositoryException);
        try {
            nodeFactory.createNode(parent, name, "nodeType");
            fail("Should have thrown runtime exception");
        } catch (RuntimeException re) {
            //should throw this exception
        }
    }
}
