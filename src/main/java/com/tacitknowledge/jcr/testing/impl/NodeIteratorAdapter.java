package com.tacitknowledge.jcr.testing.impl;

import org.apache.commons.collections.IteratorUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Iterator;
import java.util.List;

public class NodeIteratorAdapter implements NodeIterator {

    private int position;
    private Iterator<Node> iterator;
    private List<Node> nodeList;

    public NodeIteratorAdapter(Iterator<Node> iterator) {
        this.nodeList = IteratorUtils.toList(iterator);
        this.iterator = nodeList.iterator();
        this.position = 0;
    }

    @Override
    public Node nextNode() {
        Node next = iterator.next();
        removeFromList(1);
        return next;
    }

    @Override
    public void skip(long skipNum) {
        removeFromList((int) skipNum);
        iterator = nodeList.iterator();
    }

    private void removeFromList(int skipNum) {
        int size = nodeList.size();
        nodeList = nodeList.subList(skipNum, size);
        position += skipNum;
    }

    @Override
    public long getSize() {
        return nodeList.size();
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Object next() {
        Node next = iterator.next();
        removeFromList(1);
        return next;
    }

    @Override
    public void remove() {
        removeFromList(1);
    }
}
