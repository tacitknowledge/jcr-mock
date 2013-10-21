package com.tacitknowledge.jcr.testing.impl;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.Property;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertyIteratorAdapterTest {

    private Iterator<Property> iterator;
    private PropertyIteratorAdapter propertyIteratorAdapter;
    private Property property;
    private List<Property> list;
    private Property secondProperty;
    private Property thirdProperty;
    private Property fourthProperty;
    private Property fifthProperty;

    @Before
    public void setUp() throws Exception {
        iterator = mock(Iterator.class);
        property = mock(Property.class);

        when(iterator.next()).thenReturn(property);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);

        propertyIteratorAdapter = new PropertyIteratorAdapter(iterator);
        list = new ArrayList<Property>();
        secondProperty = mock(Property.class);
        thirdProperty = mock(Property.class);
        fourthProperty = mock(Property.class);
        fifthProperty = mock(Property.class);

        when(property.toString()).thenReturn("1");
        when(secondProperty.toString()).thenReturn("2");
        when(thirdProperty.toString()).thenReturn("3");
        when(fourthProperty.toString()).thenReturn("4");

        list.add(property);
        list.add(secondProperty);
        list.add(thirdProperty);
        list.add(fourthProperty);
        list.add(fifthProperty);

    }

    @Test
    public void shouldRetrieveNextProperty() {
        Property actual = propertyIteratorAdapter.nextProperty();
        assertEquals(property, actual);

    }

    @Test
    public void shouldRetrieveHaveNextProperty() {
        when(iterator.hasNext()).thenReturn(true);
        boolean hasNext = propertyIteratorAdapter.hasNext();
        assertTrue(hasNext);
    }

    @Test
    public void shouldRetrieveNext() {

        Property actual = propertyIteratorAdapter.next();

        assertEquals(property, actual);

    }

    @Test
    public void shouldCallIteratorRemover() {
        propertyIteratorAdapter.remove();
        assertEquals(0, propertyIteratorAdapter.getSize());
    }

    @Test
    public void shouldSkipProperly() {

        propertyIteratorAdapter = new PropertyIteratorAdapter(list.iterator());

        propertyIteratorAdapter.skip(1);
        assertEquals(secondProperty, propertyIteratorAdapter.nextProperty());

        propertyIteratorAdapter.skip(1);
        assertEquals(fourthProperty, propertyIteratorAdapter.nextProperty());

        assertEquals(fifthProperty, propertyIteratorAdapter.nextProperty());

    }

    @Test
    public void shouldShowCurrentPosition() {
        propertyIteratorAdapter = new PropertyIteratorAdapter(list.iterator());

        assertEquals(0, propertyIteratorAdapter.getPosition());

        propertyIteratorAdapter.skip(2);

        assertEquals(2, propertyIteratorAdapter.getPosition());

        propertyIteratorAdapter.next();

        assertEquals(3, propertyIteratorAdapter.getPosition());

        propertyIteratorAdapter.nextProperty();

        assertEquals(4, propertyIteratorAdapter.getPosition());

        propertyIteratorAdapter.remove();

        assertEquals(5, propertyIteratorAdapter.getPosition());

    }

    @Test
    public void shouldRetrieveCorrectSize() {
        propertyIteratorAdapter = new PropertyIteratorAdapter(list.iterator());

        assertEquals(5, propertyIteratorAdapter.getSize());

        propertyIteratorAdapter.skip(2);

        assertEquals(3, propertyIteratorAdapter.getSize());

        propertyIteratorAdapter.next();

        assertEquals(2, propertyIteratorAdapter.getSize());

        propertyIteratorAdapter.nextProperty();

        assertEquals(1, propertyIteratorAdapter.getSize());

        propertyIteratorAdapter.remove();

        assertEquals(0, propertyIteratorAdapter.getSize());

    }


}
