package com.tacitknowledge.jcr.testing;

import com.tacitknowledge.jcr.testing.utils.NodeTypeResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;

/**
* @author Daniel Valencia (daniel@tacitknowledge.com)
*/
public abstract class AbstractNodeFactory implements NodeFactory {
    private NodeTypeManager nodeTypeManager;
    private NodeTypeResolver nodeTypeResolver;

    public AbstractNodeFactory(NodeTypeManager nodeTypeManager, NodeTypeResolver nodeTypeResolver) {
        this.nodeTypeManager = nodeTypeManager;
        this.nodeTypeResolver = nodeTypeResolver;
    }

    public Node createNode(Node parentNode, String nodeName, String nodeTypeName) throws RepositoryException {

        NodeType nodeType;
        try {
            nodeType = nodeTypeManager.getNodeType(nodeTypeName);
        } catch (RepositoryException e) {
            throw new RuntimeException("Node type " + nodeTypeName + " is invalid or doesn't exist.", e);
        }

        PropertyDefinition[] propertyDefinitions = nodeType.getPropertyDefinitions();
        Node childNode = createNode(parentNode, nodeName, nodeType);

        for(PropertyDefinition propertyDefinition : propertyDefinitions){
            createPropertyFromDefinition(childNode, propertyDefinition);
        }

        NodeDefinition[] nodeDefinitions = nodeType.getChildNodeDefinitions();
        for(NodeDefinition nodeDefinition: nodeDefinitions){
            String nodeDefinitionName = nodeDefinition.getName();
            String primaryTypeName = nodeTypeResolver.resolvePrimaryType(nodeDefinition);
            createNode(childNode, nodeDefinitionName, primaryTypeName);
        }
        return childNode;
    }

}
