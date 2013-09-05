package com.tacitknowledge.jcr.mocking.impl;


import com.tacitknowledge.jcr.testing.NodeFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class JsonMockingServiceTest {

    private Node parentNode;
    private NodeFactory nodeFactory;
    private JsonMockService mockService;

    @Before
    public void setUp() throws Exception {
        parentNode = mock(Node.class);
        nodeFactory = mock(NodeFactory.class);
        mockService = new JsonMockService(nodeFactory);
    }

    @Test
    public void shouldCreateNodesFromJsonString() throws RepositoryException {
        when(nodeFactory.createNode(StringUtils.EMPTY)).thenReturn(parentNode);

        Node actual = mockService.fromString("{\"childElementName\":\"propertyValue\"}");

        assertEquals(parentNode, actual);
        verify(nodeFactory).createProperty(parentNode, "childElementName", "propertyValue", PropertyType.STRING);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(nodeFactory).createIteratorFor(eq(parentNode), listCaptor.capture());
        List actualList = listCaptor.getValue();
        assertTrue(actualList.isEmpty());

    }

    @Test
    public void shouldCreateNodesFromJsonStringWithParent() throws RepositoryException {
        Node actual = mockService.fromString(parentNode, "{\"childElementName\":\"propertyValue\"}");

        assertEquals(parentNode, actual);
        verify(nodeFactory).createProperty(parentNode, "childElementName", "propertyValue", PropertyType.STRING);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(nodeFactory).createIteratorFor(eq(parentNode), listCaptor.capture());
        List actualList = listCaptor.getValue();
        assertTrue(actualList.isEmpty());
    }


    @Test
    public void shouldCreateTreeNodeFromJson() throws RepositoryException {
        Node childNode = mock(Node.class);
        when(nodeFactory.createNode(parentNode, "parentElementName")).thenReturn(childNode);
        Node actual = mockService.fromString(parentNode, "{\"parentElementName\":{\"childElementName\":\"propertyValue\"}}");

        assertEquals(parentNode, actual);

        verify(nodeFactory).createProperty(childNode, "childElementName", "propertyValue", PropertyType.STRING);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(nodeFactory).createIteratorFor(eq(parentNode), listCaptor.capture());
        List actualList = listCaptor.getValue();

        assertFalse(actualList.isEmpty());
    }

    @Test
    public void shouldCreateTreeNodeFromJsonUsingNodeType() throws RepositoryException {
        Node childNode = mock(Node.class);
        when(nodeFactory.createNode(parentNode, "parentElement", "propertyValue")).thenReturn(childNode);
        Node actual = mockService.fromString(parentNode, "{\"parentElement\":{\"nodeType\":\"propertyValue\"}}");

        assertEquals(parentNode, actual);

        verify(nodeFactory, never()).createProperty(childNode, "childElementName", "propertyValue", PropertyType.STRING);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(nodeFactory).createIteratorFor(eq(parentNode), listCaptor.capture());
        List actualList = listCaptor.getValue();

        assertFalse(actualList.isEmpty());
    }

}
