package com.tacitknowledge.jcr.testing.utils;

import org.apache.commons.lang3.StringUtils;

import javax.jcr.nodetype.NodeDefinition;

/**
 * @author Daniel Valencia (Daniel.Valencia@nike.com)
 */
public class NodeTypeResolver {

    /**
     * ToDo: Add Javadoc
     * @param nodeDefinition
     * @return
     */
    public String resolvePrimaryType(NodeDefinition nodeDefinition) {
        String primaryTypeName = nodeDefinition.getRequiredPrimaryTypeNames()[0];

        String parentNodeTypeName = nodeDefinition.getDeclaringNodeType().getName();

        String nodeType = NodeTypeMapper.getNodeTypeFor(parentNodeTypeName, nodeDefinition.getName());

        if(!StringUtils.EMPTY.equals(nodeType)){
            primaryTypeName = nodeType;
        }
        return primaryTypeName;
    }
}
