package com.tacitknowledge.jcr.testing.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class NodeTypeMapperTest {

    @Test
    public void shouldWorkFineForExistingParentNodes() throws Exception {
        String nodeType = NodeTypeMapper.getNodeTypeFor("nt:file", "jcr:content");
        assertNotNull(nodeType);
        assertEquals("Expected nt:resource", "nt:resource", nodeType);
    }

    @Test
    public void shouldWorkFineForNONExistingParentNodes() throws Exception {
        String nodeType = NodeTypeMapper.getNodeTypeFor("whatever", "doesn't exist");
        assertEquals("Expected empty string", StringUtils.EMPTY, nodeType);

        nodeType = NodeTypeMapper.getNodeTypeFor("nt:file", "doesn't exist");
        assertEquals("Expected empty string", StringUtils.EMPTY, nodeType);
    }
}
