package com.tacitknowledge.jcr.testing.impl;

import com.tacitknowledge.jcr.testing.NodeFactory;
import org.apache.commons.lang3.StringUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.jcr.Binary;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Factory for Mock nodes and properties
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class MockNodeFactory implements NodeFactory {

    private Session session = mock(Session.class);

    public Node createNode(Node parentNode, String nodeName, String nodeTypeName) throws RepositoryException {

        NodeType nodeType = mock(NodeType.class);
        when(nodeType.getName()).thenReturn(nodeTypeName);

        Node childNode = createNode(parentNode, nodeName, nodeType);

        return childNode;
    }

    @Override
    public void createProperty(Node parent, String name, String propertyValue, int propertyType) throws RepositoryException {
        Property property = parent.getProperty(name);
        if (property == null) {
            property = mock(Property.class);
            Value value = createValueFor(property, propertyValue, propertyType);
            when(property.getValue()).thenReturn(value);
            when(property.getString()).thenReturn(propertyValue);
            when(property.getName()).thenReturn(name);
            when(property.getType()).thenReturn(propertyType);
            when(parent.getProperty(name)).thenReturn(property);
        } else if (property.getValue() == null) {
            createValue(property, propertyValue, propertyType);
        }

        when(property.getParent()).thenReturn(parent);
        when(property.getSession()).thenReturn(session);
        when(parent.getSession()).thenReturn(session);
        when(parent.hasProperty(name)).thenReturn(true);
        when(parent.hasProperties()).thenReturn(true);

        String parentPath = parent.getPath();
        when(property.getPath()).thenReturn(parentPath + "/" + name);
        buildParentHierarchy(parent, property, name);
    }

    @Override
    public Node createNode(Node parent, String name, NodeType nodeType) throws RepositoryException {
        Node childNode = createNode(parent, name);
        if (nodeType != null) {
            when(childNode.isNodeType(nodeType.getName())).thenReturn(true); // Default node type
            when(childNode.getPrimaryNodeType()).thenReturn(nodeType);
        }
        when(childNode.getSession()).thenReturn(session);
        return childNode;
    }

    @Override
    public Node createNode(Node parent, String name) throws RepositoryException {
        Node childNode = null;
        if (parent != null) {
            childNode = parent.getNode(name);
        }
        if (childNode == null) {
            childNode = createNode(name);
            when(childNode.getParent()).thenReturn(parent);
            buildParentHierarchy(parent, childNode, name);
        }
        when(childNode.getPath()).thenAnswer(new Answer<String>()
        {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                Node theNode = (Node) invocationOnMock.getMock();
                return buildPathForNode(theNode);
            }
        });
        when(childNode.getSession()).thenReturn(session);
        return childNode;
    }

    @Override
    public Node createNode(String name) throws RepositoryException
    {
        Node childNode = mock(Node.class);
        when(childNode.getName()).thenReturn(name);
        if(StringUtils.EMPTY.equals(name))
        {
            when(childNode.getPath()).thenReturn("/");
        }

        when(childNode.isNode()).thenReturn(true);
        return childNode;
    }

    @Override
    public void createPropertyFromDefinition(Node parentNode, PropertyDefinition propertyDefinition) throws RepositoryException {
        String propertyName = propertyDefinition.getName();
        int propertyType = propertyDefinition.getRequiredType();
        Property property = mock(Property.class);

        when(parentNode.getProperty(propertyName)).thenReturn(property);
        when(parentNode.hasProperty(propertyName)).thenReturn(true);
        when(parentNode.hasProperties()).thenReturn(true);
        when(property.getType()).thenReturn(propertyType);
        when(parentNode.getSession()).thenReturn(session);
        when(property.getSession()).thenReturn(session);

        Value[] defaultValues = propertyDefinition.getDefaultValues();

        if (defaultValues != null) {
            if (propertyDefinition.isMultiple()) {
                when(property.isMultiple()).thenReturn(true);
                when(property.getValues()).thenReturn(defaultValues);
            } else if (defaultValues.length > 0) {
                Value value = defaultValues[0];
                when(property.getValue()).thenReturn(value);
            }
        }

    }

    @Override
    public void createIteratorFor(Node parent, final List<Node> childNodes) throws RepositoryException {
        when(parent.getNodes()).thenAnswer(new Answer<NodeIterator>()
        {
            @Override
            public NodeIterator answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                return new NodeIteratorAdapter(childNodes.iterator());
            }
        });

        when(parent.getSession()).thenReturn(session);
    }

    @Override
    public Value createValueFor(Property property, String valueStr, int valueType) throws RepositoryException {
        Value returnValue = mock(Value.class);
        when(returnValue.getType()).thenReturn(valueType);

        switch (valueType) {
            case PropertyType.STRING:
                createStringValueFor(property, returnValue, valueStr);
                break;
            case PropertyType.BINARY:
                createBinaryValueFor(property, returnValue, valueStr);
                break;
            case PropertyType.BOOLEAN:
                createBooleanValueFor(property, returnValue, valueStr);
                break;
            case PropertyType.DOUBLE:
                createDoubleValueFor(property, returnValue, valueStr);
                break;
            case PropertyType.DECIMAL:
                createDecimalValueFor(property, returnValue, valueStr);
                break;
            case PropertyType.DATE:
                createDateValueFor(property, returnValue, valueStr);
            default:
                createStringValueFor(property, returnValue, valueStr);
                break;
        }

        when(property.getSession()).thenReturn(session);
        return returnValue;
    }

    private String buildPathForNode(final Node node) throws RepositoryException
    {
        if(node != null && !StringUtils.EMPTY.equals(node.getName()))
        {
            return buildPathForNode(node.getParent()) + "/" + node.getName();
        }

        return StringUtils.EMPTY;
    }

    private void createDateValueFor(Property property, Value returnValue, String valueStr) throws RepositoryException {
        Calendar calendar;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = simpleDateFormat.parse(valueStr);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid format for date value: " + valueStr, e);
        }
        when(property.getDate()).thenReturn(calendar);
        when(returnValue.getDate()).thenReturn(calendar);
    }

    private void createDecimalValueFor(Property property, Value returnValue, String valueStr) throws RepositoryException {
        BigDecimal decimalValue = new BigDecimal(valueStr);
        when(property.getDecimal()).thenReturn(decimalValue);
        when(returnValue.getDecimal()).thenReturn(decimalValue);
    }

    private void createDoubleValueFor(Property property, Value returnValue, String valueStr) throws RepositoryException {
        double doubleVal = Double.parseDouble(valueStr);
        when(property.getDouble()).thenReturn(doubleVal);
        when(returnValue.getDouble()).thenReturn(doubleVal);
    }

    private void createStringValueFor(Property property, Value returnValue, String valueStr) throws RepositoryException {
        when(property.getString()).thenReturn(valueStr);
        when(returnValue.getString()).thenReturn(valueStr);
    }

    private void createBooleanValueFor(Property property, Value returnValue, String valueStr) throws RepositoryException {
        Boolean booleanVal = Boolean.valueOf(valueStr);
        when(property.getBoolean()).thenReturn(booleanVal);
        when(returnValue.getBoolean()).thenReturn(booleanVal);
    }

    private void createValue(Property property, String propertyValue, int propertyType) throws RepositoryException {
        Value value = createValueFor(property, propertyValue, propertyType);
        when(property.getValue()).thenReturn(value);
    }

    private void createBinaryValueFor(Property property, Value valueObject, String propertyValue) throws RepositoryException {
        InputStream binaryInputStream = getClass().getResourceAsStream(propertyValue);

        if (binaryInputStream == null)
            throw new IllegalArgumentException("Path to binary doesn't exist: " + propertyValue);

        Binary binary = mock(Binary.class);
        when(property.getBinary()).thenReturn(binary);
        when(binary.getStream()).thenReturn(binaryInputStream);
        when(valueObject.getBinary()).thenReturn(binary);
    }

    private void buildParentHierarchy(Node parent, Item childItem, String itemPath) throws RepositoryException
    {
        if (parent != null)
        {
            if(childItem.isNode())
            {
                when(parent.getNode(itemPath)).thenReturn((Node)childItem);
	            when(parent.hasNode(itemPath)).thenReturn(true);
            }
            else
            {
                when(parent.getProperty(itemPath)).thenReturn((Property)childItem);
	            when(parent.hasProperty(itemPath)).thenReturn(true);
            }

            String parentName = parent.getName();

            if (parentName != null) {
                buildParentHierarchy(parent.getParent(), childItem, parentName + "/" + itemPath);
            }
        }
    }

}
