package com.tacitknowledge.jcr.testing.utils;

import org.apache.commons.lang3.StringUtils;

import javax.jcr.nodetype.NodeDefinition;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class NodeTypeResolver {

    /**
     * Resolves the primary type for a node given a node definition
     *
     * @param nodeDefinition Node definition
     * @return Name for node's primary type
     */
    public static String resolvePrimaryType(NodeDefinition nodeDefinition) {
        String primaryTypeName = nodeDefinition.getRequiredPrimaryTypeNames()[0];

        String parentNodeTypeName = nodeDefinition.getDeclaringNodeType().getName();

        String nodeType = NodeTypeMapper.getNodeTypeFor(parentNodeTypeName, nodeDefinition.getName());

        if(!StringUtils.EMPTY.equals(nodeType)){
            primaryTypeName = nodeType;
        }
        return primaryTypeName;
    }
}
