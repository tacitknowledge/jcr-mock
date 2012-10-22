package com.tacitknowledge.jcr.testing.utils;

import org.junit.Assert;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JcrTestingUtils {

    public static void assertProperty(Property property, int expectedType, Object expectedValue) throws RepositoryException {
        assertPropertyType(property, expectedType);
        Assert.assertEquals("Expected Empty String", expectedValue, PropertyTypeEnum.fromType(expectedType).getObjectValue(property.getValue()));
    }

    public static void assertPropertyType(Property property, int expectedType) throws RepositoryException {
        Assert.assertEquals("Expected Type " + PropertyType.nameFromValue(expectedType) + " but got "
                + PropertyType.nameFromValue(property.getType())
                , expectedType, property.getType());
    }

    public static void assertIteratorCount(Iterator iterator, int expectedCount) {
        int counter = 0;

        while (iterator.hasNext()){
            assertNotNull("Expected a non null node", iterator.next());
            counter++;
        }

        assertEquals("Expected 1 node", expectedCount, counter);
    }

}
