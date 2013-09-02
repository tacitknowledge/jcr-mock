package com.tacitknowledge.jcr.testing.impl;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeIteratorAdapterTest {

    private Iterator<Node> iterator;
    private NodeIteratorAdapter nodeIteratorAdapter;
    private Node node;
    private List<Node> list;
    private Node secondNode;
    private Node thirdNode;
    private Node fourthNode;
    private Node fifthNode;

    @Before
    public void setUp() throws Exception {
        iterator = mock(Iterator.class);
        node = mock(Node.class);

        when(iterator.next()).thenReturn(node);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);

        nodeIteratorAdapter = new NodeIteratorAdapter(iterator);
        list = new ArrayList<Node>();
        secondNode = mock(Node.class);
        thirdNode = mock(Node.class);
        fourthNode = mock(Node.class);
        fifthNode = mock(Node.class);

        when(node.toString()).thenReturn("1");
        when(secondNode.toString()).thenReturn("2");
        when(thirdNode.toString()).thenReturn("3");
        when(fourthNode.toString()).thenReturn("4");

        list.add(node);
        list.add(secondNode);
        list.add(thirdNode);
        list.add(fourthNode);
        list.add(fifthNode);

    }

    @Test
    public void shouldRetrieveNextNodes() {

        Node actual = nodeIteratorAdapter.nextNode();
        assertEquals(node, actual);

    }

    @Test
    public void shouldRetrieveNextNode() {
        when(iterator.hasNext()).thenReturn(true);
        boolean hasNext = nodeIteratorAdapter.hasNext();
        assertTrue(hasNext);
    }

    @Test
    public void shouldRetrieveNextObject() {

        Node actual = (Node) nodeIteratorAdapter.next();

        assertEquals(node, actual);

    }

    @Test
    public void shouldCallIteratorRemover() {
        nodeIteratorAdapter.remove();
        assertEquals(0, nodeIteratorAdapter.getSize());
    }

    @Test
    public void shouldSkipProperly() {

        nodeIteratorAdapter = new NodeIteratorAdapter(list.iterator());

        nodeIteratorAdapter.skip(1);
        assertEquals(secondNode, nodeIteratorAdapter.nextNode());

        nodeIteratorAdapter.skip(1);
        assertEquals(fourthNode, nodeIteratorAdapter.nextNode());

        assertEquals(fifthNode, nodeIteratorAdapter.nextNode());

    }

    @Test
    public void shouldShowCurrentPosition() {
        nodeIteratorAdapter = new NodeIteratorAdapter(list.iterator());

        assertEquals(0, nodeIteratorAdapter.getPosition());

        nodeIteratorAdapter.skip(2);

        assertEquals(2, nodeIteratorAdapter.getPosition());

        nodeIteratorAdapter.next();

        assertEquals(3, nodeIteratorAdapter.getPosition());

        nodeIteratorAdapter.nextNode();

        assertEquals(4, nodeIteratorAdapter.getPosition());

        nodeIteratorAdapter.remove();

        assertEquals(5, nodeIteratorAdapter.getPosition());

    }

    @Test
    public void shouldRetrieveCorrectSize() {
        nodeIteratorAdapter = new NodeIteratorAdapter(list.iterator());

        assertEquals(5, nodeIteratorAdapter.getSize());

        nodeIteratorAdapter.skip(2);

        assertEquals(3, nodeIteratorAdapter.getSize());

        nodeIteratorAdapter.next();

        assertEquals(2, nodeIteratorAdapter.getSize());

        nodeIteratorAdapter.nextNode();

        assertEquals(1, nodeIteratorAdapter.getSize());

        nodeIteratorAdapter.remove();

        assertEquals(0, nodeIteratorAdapter.getSize());

    }


}
