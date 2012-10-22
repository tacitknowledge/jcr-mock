package com.tacitknowledge.jcr.testing.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.jackrabbit.JcrConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to let users redefine primary node types of existing registered node types.
 * For example, the definition for the OOTB nt:file node type is the following:
 *
 *        [nt:file] > nt:hierarchyNode primaryitem jcr:content
 *        + jcr:content (nt:base) mandatory
 *
 * This definition states that the nt:file node should contain a mandatory child node named jcr:content of type nt:base
 * (or a subtype of nt:base).  However, since nt:base is the most basic of the nodetypes (and does not enforce any
 * restrictions, it is a custom practice that JCR implementors use nt:resource as a node type of jcr:content (which
 * specifies a Binary property named jcr:data).
 *
 * This class will provide a mapping mechanism for overriding OOTB node types for the JCR Mocking Framework since it's
 * impossible to redefine OOTB node types via the NodeTypeManager.registerNodeType method.
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class NodeTypeMapper {
    private static Map<String, Map<String,String>> parentToChildNodeTypeMap = new HashMap<String, Map<String,String>>();

    static{
        parentToChildNodeTypeMap.put(JcrConstants.NT_FILE, new HashMap<String,String>(){{
            put(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
        }});
    }

    public static String getNodeTypeFor(String parentNodeType, String nodeName){
        Map<String,String> childNodeMap = parentToChildNodeTypeMap.get(parentNodeType);
        String nodeType = StringUtils.EMPTY;
        if (childNodeMap != null) {
            nodeType = childNodeMap.get(nodeName);
            if(nodeType == null) nodeType = StringUtils.EMPTY;
        }
        return nodeType;
    }

}
