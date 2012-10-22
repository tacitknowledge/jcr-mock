package com.tacitknowledge.jcr.testing;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public interface NodeFactory {

    String NODE_TYPE = "nodeType";

    void createProperty(Node parent, String name, String propertyValue, int propertyType) throws RepositoryException;

    void createPropertyFromDefinition(Node parentNode, PropertyDefinition propertyDefinition) throws RepositoryException;

    Node createNode(Node parentNode, String nodeName, String nodeTypeName) throws RepositoryException;

    Node createNode(Node parent, String name) throws RepositoryException;

    Node createNode(String name) throws RepositoryException;

    void createIteratorFor(Node parent, List<Node> childNodes) throws RepositoryException;

    Value createValueFor(Property property, String valueStr, int valueType) throws RepositoryException;

    Node createNode(Node parent, String name, NodeType nodeType) throws RepositoryException;
}
