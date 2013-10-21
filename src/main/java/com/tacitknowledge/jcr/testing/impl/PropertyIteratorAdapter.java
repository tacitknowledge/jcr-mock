package com.tacitknowledge.jcr.testing.impl;

import org.apache.commons.collections.IteratorUtils;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alayouni
 * Date: 10/21/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyIteratorAdapter implements PropertyIterator {
	private int position;
	private Iterator<Property> iterator;
	private List<Property> propertyList;

	public PropertyIteratorAdapter(Iterator<Property> iterator) {
		this.propertyList = IteratorUtils.toList(iterator);
		this.iterator = propertyList.iterator();
		this.position = 0;
	}

	@Override
	public Property nextProperty() {
		Property next = iterator.next();
		removeFromList(1);
		return next;
	}

	@Override
	public void skip(long skipNum) {
		removeFromList((int) skipNum);
		iterator = propertyList.iterator();
	}

	private void removeFromList(int skipNum) {
		int size = propertyList.size();
		propertyList = propertyList.subList(skipNum, size);
		position += skipNum;
	}

	@Override
	public long getSize() {
		return propertyList.size();
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
	public Property next() {
		Property next = iterator.next();
		removeFromList(1);
		return next;
	}

	@Override
	public void remove() {
		removeFromList(1);
	}
}
