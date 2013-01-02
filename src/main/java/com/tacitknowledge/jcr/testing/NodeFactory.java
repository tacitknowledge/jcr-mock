package com.tacitknowledge.jcr.testing;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.List;

/**
 * Interface for Node Factory classes
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public interface NodeFactory {

    String NODE_TYPE = "nodeType";

    void createProperty(Node parent, String name, String propertyValue, int propertyType) throws RepositoryException;

    void createPropertyFromDefinition(Node parentNode, PropertyDefinition propertyDefinition) throws RepositoryException;

    Node createNode(Node parentNode, String nodeName, String nodeTypeName) throws RepositoryException;

    Node createNode(Node parent, String name) throws RepositoryException;

    Node createNode(String name) throws RepositoryException;

    /**
     * Creates a node iterator for the given child nodes in relation with their parent.
     * The parent node must not be null
     * @param parent Parent node for the children and the iterator. Must not be null.
     * @param childNodes Child nodes to iterate through
     * @throws RepositoryException If a repository error happens
     */
    void createIteratorFor(Node parent, List<Node> childNodes) throws RepositoryException;

    Value createValueFor(Property property, String valueStr, int valueType) throws RepositoryException;

    Node createNode(Node parent, String name, NodeType nodeType) throws RepositoryException;
}
