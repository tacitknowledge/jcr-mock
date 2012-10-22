package com.tacitknowledge.jcr.testing.utils;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public enum PropertyTypeEnum {
    BINARY(PropertyType.BINARY){
        @Override
        public Object getObjectValue(Value value) throws RepositoryException {
            return value.getBinary();
        }
    },
    BOOLEAN(PropertyType.BOOLEAN){
        @Override
        public Object getObjectValue(Value value) throws RepositoryException {
            return value.getBoolean();
        }
    },
    DATE(PropertyType.DATE){
        @Override
        public Object getObjectValue(Value value) throws RepositoryException {
            return value.getDate();
        }
    },
    DECIMAL(PropertyType.DECIMAL){
        @Override
        public Object getObjectValue(Value value) throws RepositoryException {
            return value.getDecimal();
        }
    },
    DOUBLE(PropertyType.DOUBLE){
        @Override
        public Object getObjectValue(Value value) throws RepositoryException {
            return value.getDouble();
        }
    },
    LONG(PropertyType.LONG){
       @Override
       public Object getObjectValue(Value value) throws RepositoryException {
            return value.getLong();
        }
    },
    REFERENCE(PropertyType.REFERENCE),
    STRING(PropertyType.STRING);

    private int propertyType;

    PropertyTypeEnum(int propertyType) {
        this.propertyType = propertyType;
    }

    public int getPropertyType(){
        return propertyType;
    }

    public Object getObjectValue(Value value) throws RepositoryException {
        return value.getString();
    }

    public static PropertyTypeEnum fromType(int type){
        PropertyTypeEnum propertyTypeEnum = null;

        for(PropertyTypeEnum enumVal: values()){
            if(type == enumVal.getPropertyType()){
                propertyTypeEnum = enumVal;
            }
        }

        if(propertyTypeEnum == null) throw new RuntimeException("There's no PropertyTypeEnum for type: " + type);

        return propertyTypeEnum;
    }
}
