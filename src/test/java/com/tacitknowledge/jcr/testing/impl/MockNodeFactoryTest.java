package com.tacitknowledge.jcr.testing.impl;

import com.tacitknowledge.jcr.testing.NodeFactory;
import com.tacitknowledge.jcr.testing.utils.JcrTestingUtils;
import com.tacitknowledge.jcr.testing.utils.NodeTypeResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class MockNodeFactoryTest {
    private NodeFactory nodeFactory;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void beforeEachTest() throws RepositoryException, IOException {
        NodeTypeManager nodeTypeManager = TransientRepositoryManager.createNodeTypeManager();
        this.nodeFactory = new MockNodeFactory(nodeTypeManager, new NodeTypeResolver());
    }

    @Test
    public void shouldCreateNodeWithNodeType() throws RepositoryException {
        Node parentNode = mock(Node.class);
        Node binaryNode = nodeFactory.createNode(parentNode, "binary", NodeType.NT_FILE);

        NodeType primaryNodeType = binaryNode.getPrimaryNodeType();
        assertTrue("Type should be nt:file", primaryNodeType.isNodeType(NodeType.NT_FILE));

        Node jcrContent = binaryNode.getNode("jcr:content");
        assertNotNull("jcr:content node should not be null", jcrContent);

        JcrTestingUtils.assertPropertyType(jcrContent.getProperty("jcr:data"), PropertyType.BINARY);
        JcrTestingUtils.assertPropertyType(jcrContent.getProperty("jcr:lastModified"), PropertyType.DATE);
        JcrTestingUtils.assertPropertyType(jcrContent.getProperty("jcr:lastModifiedBy"), PropertyType.STRING);
        JcrTestingUtils.assertPropertyType(jcrContent.getProperty("jcr:mimeType"), PropertyType.STRING);
    }

    @Test
    public void shouldThrowRuntimeExceptionIfEmptyTypeIsPassed() throws RepositoryException {
        Node parentNode = mock(Node.class);

        expectedException.expect(RuntimeException.class);
        nodeFactory.createNode(parentNode, "invalid", "");
    }

    @Test
    public void shouldThrowRuntimeExceptionIfNodeTypeDoesNotExist() throws RepositoryException {
        Node parentNode = mock(Node.class);

        expectedException.expect(RuntimeException.class);
        nodeFactory.createNode(parentNode, "invalid", "nt:doesntExist");
    }

    @Test
    public void shouldCreateCorrectStringValue() throws RepositoryException {
        Value value;
        String valueStr = "My String Value";

        Property property = mock(Property.class);

        value = nodeFactory.createValueFor(property, valueStr, PropertyType.STRING);

        assertEquals("Value should be of type String", PropertyType.STRING, value.getType());
        assertEquals("Value should be 'My String Value'", valueStr, value.getString());
    }

    @Test
    public void shouldCreateCorrectBooleanValue() throws RepositoryException {
        Value value;
        String boolVal = "true";

        Property property = mock(Property.class);

        value = nodeFactory.createValueFor(property, boolVal, PropertyType.BOOLEAN);

        assertEquals("Value should be of type Boolean", PropertyType.BOOLEAN, value.getType());
        assertEquals("Value should be true", true, value.getBoolean());

        // Now test unhappy path
        String invalidBoolValue = "this is not boolean";

        expectedException.expect(IllegalArgumentException.class);
        value = nodeFactory.createValueFor(property, invalidBoolValue, PropertyType.BOOLEAN);

    }

    @Test
    public void shouldReturnValidBinaryObject() throws RepositoryException, IOException {
        String binaryStr = "/files/air_jordan.jpg";

        Value binaryValue;

        Property property = mock(Property.class);

        binaryValue = nodeFactory.createValueFor(property, binaryStr, PropertyType.BINARY);

        assertEquals("Value should be of type Binary", PropertyType.BINARY, binaryValue.getType());
        assertNotNull("Value should not be null", binaryValue.getBinary());
        assertNotNull("InputStream should be not null", binaryValue.getBinary().getStream());

        InputStream is = binaryValue.getBinary().getStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[256];
        int bytesRead;

        try {
            while((bytesRead = is.read(buffer)) > 0){
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Problem reading input stream");
        } finally {
            if(is != null) is.close();
            if(byteArrayOutputStream != null) byteArrayOutputStream.close();
        }

        assertTrue("Shoul have some bytes", byteArrayOutputStream.toByteArray().length > 0);

        // Unhappy Path

        String pathThatDoesNotExist = "Invalid Path";

        expectedException.expect(IllegalArgumentException.class);
        binaryValue = nodeFactory.createValueFor(property, pathThatDoesNotExist, PropertyType.BINARY);
    }

    @Test
    public void shouldReturValidDoubleValue() throws RepositoryException {
        String doubleStr = "9.0";
        Value returnedValue;

        Property property = mock(Property.class);

        returnedValue = nodeFactory.createValueFor(property, doubleStr, PropertyType.DOUBLE);

        assertEquals("Expected a Double", 9.0, returnedValue.getDouble(), 0);

        //Unhappy Path

        String invalidDouble = "This ain't a double";

        expectedException.expect(NumberFormatException.class);
        nodeFactory.createValueFor(property, invalidDouble, PropertyType.DOUBLE);
    }

    @Test
    public void shouldReturnValidDecimalValue() throws RepositoryException {
        String decimalStr = "5.7";

        Value returnedValue;

        Property property = mock(Property.class);

        returnedValue = nodeFactory.createValueFor(property, decimalStr, PropertyType.DECIMAL);

        assertEquals("Expected a big decimal", new BigDecimal(decimalStr), returnedValue.getDecimal());

        String invalidBigDecimal = "This ain't no decimal";

        expectedException.expect(NumberFormatException.class);
        nodeFactory.createValueFor(property, invalidBigDecimal, PropertyType.DECIMAL);
    }

    @Test
    public void shouldReturnValidDateValue() throws RepositoryException {
        String dateStr = "09/24/1982";   // This is the supported format

        Value returnedValue;

        Property property = mock(Property.class);

        returnedValue = nodeFactory.createValueFor(property, dateStr, PropertyType.DATE);

        Calendar date = returnedValue.getDate();
        assertNotNull("Expected a Calendar instance", date);
        assertEquals("Expected same day", 24, date.get(Calendar.DAY_OF_MONTH));
        assertEquals("Expected same day", Calendar.SEPTEMBER, date.get(Calendar.MONTH));
        assertEquals("Expected same day", 1982, date.get(Calendar.YEAR));

        String unsupportedDateFormat = "Sep 24, 1982";

        expectedException.expect(RuntimeException.class);
        nodeFactory.createValueFor(property, unsupportedDateFormat, PropertyType.DATE);
    }
}
