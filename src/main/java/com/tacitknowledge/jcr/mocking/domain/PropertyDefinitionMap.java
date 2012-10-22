package com.tacitknowledge.jcr.mocking.domain;

import org.apache.commons.lang3.StringUtils;

import javax.jcr.PropertyType;
import java.util.HashMap;

/**
 * @author Daniel Valencia (Daniel.Valencia@nike.com)
 */
public class PropertyDefinitionMap extends HashMap<String, String>{

    public static final String VALUE = "value";
    public static final String TYPE = "type";

    public PropertyDefinitionMap(String propertyDefinition){
        if(propertyDefinition == null){
            throw new RuntimeException("Property definition must not be null");
        }

        String[] tokens = propertyDefinition.split(",");
        for(String token: tokens){
            String[] keyValuePair = token.split(":");
            if(keyValuePair.length == 2){
                put(keyValuePair[0].trim().toLowerCase(), keyValuePair[1].trim());
            }
        }

        // If property definition doesn't contain any tokens then the definition corresponds to the value.
        if(size() == 0){
            put(VALUE, propertyDefinition);
        }
    }

    public String getValue(){
        String value = get(VALUE);
        if(value == null) value = StringUtils.EMPTY;
        return value;
    }

    public int getType(){
        int intType = PropertyType.STRING;
        String type = get(TYPE);
        if(type != null){
            intType = PropertyType.valueFromName(type);
        }
        return intType;
    }
}
