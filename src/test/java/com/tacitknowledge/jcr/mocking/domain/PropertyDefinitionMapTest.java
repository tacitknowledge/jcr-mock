package com.tacitknowledge.jcr.mocking.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.jcr.PropertyType;

import static org.junit.Assert.assertEquals;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class PropertyDefinitionMapTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnCorrectPropertyType() {
        String valueString = "type:Binary, value:/files/air_jordan.jpg";
        PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(valueString);

        int propertyType = propertyDefinitionMap.getType();

        assertEquals("Expecting Binary Type", PropertyType.BINARY, propertyType);
    }

    @Test
    public void shouldThrowExceptionForUnsupportedPropertyTypes() {
        String valueString = "type:File";

        PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(valueString);

        expectedException.expect(IllegalArgumentException.class);
        propertyDefinitionMap.getType();
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

        assertEquals("Expecting File Path", "/files/air_jordan.jpg", propertyValue);

    }
}
