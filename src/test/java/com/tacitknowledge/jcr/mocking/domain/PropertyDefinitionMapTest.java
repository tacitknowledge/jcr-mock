package com.tacitknowledge.jcr.mocking.domain;

import org.junit.Test;

import javax.jcr.PropertyType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class PropertyDefinitionMapTest {

    @Test
    public void shouldReturnCorrectPropertyType() {
        String valueString = "type:Binary, value:/files/air_jordan.jpg";
        PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(valueString);

        int propertyType = propertyDefinitionMap.getType();

        assertEquals("Expecting Binary Type", PropertyType.BINARY, propertyType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForUnsupportedPropertyTypes() {
        String valueString = "type:File";

        PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(valueString);

        propertyDefinitionMap.getType();

        fail("An IllegalArgumentException exception was expected");
    }

    @Test
    public void shouldReturnCorrectPropertyValue() throws Exception {
        String valueString = "type:Binary,value:/files/air_jordan.jpg";
        PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(valueString);

        String propertyValue = propertyDefinitionMap.getValue();

        assertEquals("Expecting File Path", "/files/air_jordan.jpg", propertyValue);

        // Now let's test a different scenario in which we have a new attribute
        valueString = "type:Binary,value:/files/air_jordan.jpg ,required:true";
        propertyDefinitionMap = new PropertyDefinitionMap(valueString );
        propertyValue = propertyDefinitionMap.getValue();

        assertEquals("Expected type to be Binary", PropertyType.BINARY, propertyDefinitionMap.getType());

        assertEquals("Expecting File Path", "/files/air_jordan.jpg", propertyValue);
        
        //issue #23 test commas in a property value
        valueString = "Here's a test with a comma,";
        propertyDefinitionMap = new PropertyDefinitionMap(valueString);
        propertyValue = propertyDefinitionMap.getValue();

        assertEquals("Expected type to be Binary", PropertyType.BINARY, propertyDefinitionMap.getType());

        assertEquals("Expecting File Path", valueString, propertyValue);
        
    }

    @Test
    public void shouldSupportPropertyDefinitionsWithColonInItsName() throws Exception
    {
        String propertyDefinition = "nt:file";

        PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(propertyDefinition);
        String propertyValue = propertyDefinitionMap.getValue();

        assertEquals("Expected value to be nt:file", "nt:file", propertyValue);
    }
}
