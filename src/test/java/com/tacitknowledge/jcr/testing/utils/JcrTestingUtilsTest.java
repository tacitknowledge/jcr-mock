package com.tacitknowledge.jcr.testing.utils;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JcrTestingUtilsTest {

    private Property property;
    private List<Node> list;
    private Node node;
    private Iterator<Node> iterator;

    @Before
    public void setUp() throws Exception {
        property = mock(Property.class);
        when(property.getType()).thenReturn(1);
        Value value = mock(Value.class);
        when(value.getString()).thenReturn("value");
        when(property.getValue()).thenReturn(value);
        list = new ArrayList<Node>();
        node = mock(Node.class);

        list.add(node);
        list.add(node);
        list.add(node);
        list.add(node);
        list.add(node);

        iterator = list.iterator();
    }

    @Test
    public void shouldAssertIteratorCountCorrectly() {
        JcrTestingUtils.assertIteratorCount(iterator, 5);
    }

    @Test
    public void shouldThrowAssertionErrorIfCountIsIncorrect() {

        try {
            JcrTestingUtils.assertIteratorCount(iterator, 6);
            fail("Should have thrown AssertionError since count is not right.");
        } catch (AssertionError error) {
            assertEquals("Expected 1 node expected:<6> but was:<5>", error.getMessage());
        }
    }

    @Test
    public void shouldAssertPropertyTypeIsExpected() throws RepositoryException {
        JcrTestingUtils.assertPropertyType(property, 1);
    }

    @Test
    public void shouldAssertPropertyIsExpected() throws RepositoryException {
        JcrTestingUtils.assertProperty(property, 1, "value");
    }

}
