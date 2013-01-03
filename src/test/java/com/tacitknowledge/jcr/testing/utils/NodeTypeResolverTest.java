package com.tacitknowledge.jcr.testing.utils;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** @author Gilberto Alvarado(galvarado@tacitknowledge.com) */
public class NodeTypeResolverTest
{

    private NodeDefinition nodeDefinition;

    @Before
    public void setup()
    {
        nodeDefinition = mock(NodeDefinition.class);
   }

    @Test
    public void shouldResolveDefaultNodePrimaryType() throws Exception
    {
        String[] primaryTypeNames = new String[] {"primary"};
        when(nodeDefinition.getRequiredPrimaryTypeNames()).thenReturn(primaryTypeNames);
        NodeType mockNodeType = mock(NodeType.class);
        when(nodeDefinition.getDeclaringNodeType()).thenReturn(mockNodeType);
        when(mockNodeType.getName()).thenReturn("type");

        String primaryType = NodeTypeResolver.resolvePrimaryType(nodeDefinition);
        assertNotNull(primaryType);
        assertEquals("primary", primaryType);

    }
}
