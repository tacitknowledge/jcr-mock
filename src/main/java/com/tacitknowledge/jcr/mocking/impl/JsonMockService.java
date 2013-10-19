package com.tacitknowledge.jcr.mocking.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tacitknowledge.jcr.mocking.JcrMockService;
import com.tacitknowledge.jcr.mocking.domain.PropertyDefinitionMap;
import com.tacitknowledge.jcr.testing.NodeFactory;
import org.apache.commons.lang3.StringUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.*;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class JsonMockService implements JcrMockService {

    private final NodeFactory nodeFactory;

    public JsonMockService(NodeFactory nodeFactory){
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Node fromString(Node parentNode, String jsonNodeStructure) throws RepositoryException {
        JsonObject object = parseJson(jsonNodeStructure);
        return buildChildNodes(object, parentNode);
    }

    @Override
    public Node fromString(String jsonNodeStructure) throws RepositoryException {
        JsonObject jsonNodeObject = parseJson(jsonNodeStructure);
        return buildChildNodes(jsonNodeObject, null);
    }

    /**
     * Builds nodes from the given JSON object as children of the given parent
     * @param parentJsonObject JsonObject to create the children from
     * @param parent Parent node
     * @return Parent node
     * @throws RepositoryException If a repository error happens
     */
    private Node buildChildNodes(JsonObject parentJsonObject, Node parent) throws RepositoryException {
        Node childNode;
        List<Node> childNodes = new ArrayList<Node>();
        if(parent == null){
            parent = nodeFactory.createNode(StringUtils.EMPTY);
        }
        Set<Map.Entry<String, JsonElement>> childElements = parentJsonObject.entrySet();

        for(Map.Entry<String, JsonElement> childEntry: childElements){
            String childElementName = childEntry.getKey();
            JsonElement childElement = childEntry.getValue();
            if(childElement.isJsonObject()){
                JsonObject childJsonObject = childElement.getAsJsonObject();
                JsonElement nodeTypeElement = childJsonObject.get(NodeFactory.JCR_PRIMARY_TYPE);
                if(nodeTypeElement != null ){
                    String nodeType = nodeTypeElement.getAsString();
                    childNode = nodeFactory.createNode(parent, childElementName, nodeType);
                }else{
                    childNode = nodeFactory.createNode(parent, childElementName);
                }
                childNodes.add(childNode);
                buildChildNodes(childJsonObject, childNode);
            }else if(childElement.isJsonPrimitive()){
                String childElementValue = childElement.getAsString();
                PropertyDefinitionMap propertyDefinitionMap = new PropertyDefinitionMap(childElementValue);
                int propertyType = propertyDefinitionMap.getType();
                String propertyValue = propertyDefinitionMap.getValue();
                nodeFactory.createProperty(parent, childElementName, propertyValue, propertyType);
            }else if(childElement.isJsonArray()){
	            String[] values = readMultiValuedProperty(childElement);
	            nodeFactory.createMultiValuedProperty(parent, childElementName, values);
            }
        }
        nodeFactory.createIteratorFor(parent, childNodes);

        return parent;
    }

	private String[] readMultiValuedProperty(JsonElement propertyElement) {
		List<String> childElementValues = new ArrayList<String>();
		JsonArray jsonArray = propertyElement.getAsJsonArray();
		Iterator<JsonElement> arrayIterator = jsonArray.iterator();
		while(arrayIterator.hasNext()) {
			JsonElement element = arrayIterator.next();
			if(!element.isJsonPrimitive()) {
				return new String[] {};
			}
			childElementValues.add(element.getAsString());
		}
		return childElementValues.toArray(new String[childElementValues.size()]);
	}

    /**
     * Parses a given String as JSON
     * @param jsonString - JSON object as String
     * @return JsonObject
     */
    private JsonObject parseJson(String jsonString) {
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        return parser.parse(jsonString).getAsJsonObject();
    }
}
