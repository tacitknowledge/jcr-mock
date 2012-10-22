package com.tacitknowledge.jcr.mocking.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tacitknowledge.jcr.mocking.JcrMockService;
import com.tacitknowledge.jcr.mocking.domain.PropertyDefinitionMap;
import com.tacitknowledge.jcr.testing.NodeFactory;
import com.tacitknowledge.parser.JsonParser;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JsonMockService implements JcrMockService {

    private final NodeFactory nodeFactory;
    private final JsonParser jsonParser;

    public JsonMockService(NodeFactory nodeFactory){
        this.nodeFactory = nodeFactory;
        this.jsonParser = new JsonParser();
    }

    @Override
    public Node fromString(Node parentNode, String nodeDefinition) throws RepositoryException {
        JsonObject object = jsonParser.parse(nodeDefinition);
        return buildChildNodes(object, parentNode);
    }

    @Override
    public Node fromString(String jsonNodeStructure) throws RepositoryException {
        JsonObject jsonNodeObject = jsonParser.parse(jsonNodeStructure);
        return buildChildNodes(jsonNodeObject, null);
    }

    private Node buildChildNodes(JsonObject parentJsonObject, Node parent) throws RepositoryException {
        Node childNode = null;
        List<Node> childNodes = new ArrayList<Node>();
        if(parent == null){
            parent = nodeFactory.createNode("");
        }
        Set<Map.Entry<String, JsonElement>> childElements = parentJsonObject.entrySet();

        for(Map.Entry<String, JsonElement> childEntry: childElements){
            String childElementName = childEntry.getKey();
            JsonElement childElement = childEntry.getValue();
            if(childElement.isJsonObject()){
                JsonObject childJsonObject = childElement.getAsJsonObject();
                JsonElement nodeTypeElement = childJsonObject.get(NodeFactory.NODE_TYPE);
                if(nodeTypeElement != null ){
                    String nodeType = nodeTypeElement.getAsString();
                    childNode = nodeFactory.createNode(parent, childElementName, nodeType);
                }else{
                    childNode = nodeFactory.createNode(parent, childElementName);
                }
                childNodes.add(childNode);
                buildChildNodes(childJsonObject, childNode);
            }else if(childElement.isJsonArray()){

            }else if(childElement.isJsonPrimitive()){
                String childElementValue = childElement.getAsString();
                PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(childElementValue);
                int propertyType = propertyDefinitionMap.getType();
                String propertyValue = propertyDefinitionMap.getValue();
                if(!NodeFactory.NODE_TYPE.equals(childElementName)){
                    nodeFactory.createProperty(parent, childElementName, propertyValue, propertyType);
                }
            }else{
                //Should be JsonNull, ignore it
            }
        }
        nodeFactory.createIteratorFor(parent, childNodes);

        return parent;
    }


}
