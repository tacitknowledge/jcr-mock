package com.tacitknowledge.jcr.mocking.domain;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.PropertyType;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class PropertyDefinitionMap extends HashMap<String, String>
{

    public static final String VALUE = "value";
    public static final String TYPE = "type";
    public static final String KEY_VALUE_SEPARATOR = ":";
    public static final String PAIR_SEPARATOR = ",";

    public PropertyDefinitionMap(String propertyDefinition)
    {
        if(propertyDefinition == null)
        {
            throw new RuntimeException("Property definition must not be null");
        }
        //"type:Binary,value:/files/air_jordan.jpg"
        //'type:String, value:The value of the property'
        //"type:Binary,value:/files/air_jordan.jpg ,required:true";
        Pattern pattern = Pattern.compile("type:([a-zA-Z]+),\\s*value:(.+)");
        Matcher matcher = pattern.matcher(propertyDefinition);
        if(matcher.find())
        {
        	String key = matcher.group(1);
        	put(TYPE, key);
        	String value = matcher.group(2);
        	if(value.contains(PAIR_SEPARATOR)){
        		String[] tokens = value.split(PAIR_SEPARATOR);
        		put(VALUE, tokens[0].trim());
        	}else{
        		put(VALUE, value);
        	}
        }
        else if(propertyDefinition.contains(KEY_VALUE_SEPARATOR))
        {
            String key = extractKeyFromDefinition(propertyDefinition);
            if(key.equals(TYPE) || key.equals(VALUE))
            {
                insertEntry(propertyDefinition);
            }
        }

        // If property definition doesn't contain any tokens then the definition corresponds to the value.
        if(size() == 0)
        {
            put(VALUE, propertyDefinition);
        }
    }

    public String getValue()
    {
        String value = get(VALUE);
        if(value == null) value = StringUtils.EMPTY;
        return value;
    }

    public int getType()
    {
        int intType = PropertyType.STRING;
        String type = get(TYPE);
        if(type != null)
        {
            intType = PropertyType.valueFromName(type);
        }
        return intType;
    }

    private void insertEntry(String propertyDefinition)
    {
        String key = extractKeyFromDefinition(propertyDefinition);
        String value = extractValueFromDefinition(propertyDefinition);
        put(key, value);
    }

    private String extractValueFromDefinition(String token)
    {
        return token.substring(token.indexOf(KEY_VALUE_SEPARATOR) + 1, token.length()).trim();
    }

    private String extractKeyFromDefinition(String token)
    {
        return token.substring(0, token.indexOf(KEY_VALUE_SEPARATOR)).trim();
    }
}
