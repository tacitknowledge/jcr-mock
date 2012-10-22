package com.tacitknowledge.jcr.testing.utils;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.Binary;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class PropertyTypeEnumTest {

    private PropertyTypeEnum stringType;
    private PropertyTypeEnum binaryType;
    private PropertyTypeEnum longType;
    private PropertyTypeEnum dateType;
    private PropertyTypeEnum decimalType;
    private PropertyTypeEnum doubleType;
    private PropertyTypeEnum referenceType;

    @Before
    public void beforeEachTest(){
        stringType = PropertyTypeEnum.fromType(PropertyType.STRING);
        binaryType = PropertyTypeEnum.fromType(PropertyType.BINARY);
        longType = PropertyTypeEnum.fromType(PropertyType.LONG);
        dateType = PropertyTypeEnum.fromType(PropertyType.DATE);
        decimalType = PropertyTypeEnum.fromType(PropertyType.DECIMAL);
        doubleType = PropertyTypeEnum.fromType(PropertyType.DOUBLE);
        referenceType = PropertyTypeEnum.fromType(PropertyType.REFERENCE);
    }

    @Test
    public void shouldGetCorrectObjectValueAndType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, RepositoryException {
        Value value = mock(Value.class);
        Binary mockedBinary = mock(Binary.class);
        String mockedString = "It freaking works!!!";
        Long mockedLong = 123L;
        String reference = "/reference/String";
        Double mockedDouble = 3.0;
        BigDecimal mockedBigDecimal = new BigDecimal(2.0);
        boolean mockedBoolean = true;
        Calendar mockedCalendar = Calendar.getInstance();


        when(value.getBinary()).thenReturn(mockedBinary);
        when(value.getString()).thenReturn(mockedString);
        when(value.getLong()).thenReturn(mockedLong);
        when(value.getDecimal()).thenReturn(mockedBigDecimal);
        when(value.getBoolean()).thenReturn(mockedBoolean);
        when(value.getDouble()).thenReturn(mockedDouble);
        when(value.getDate()).thenReturn(mockedCalendar);

        assertEquals(mockedString, stringType.getObjectValue(value));
        assertEquals(mockedBinary, binaryType.getObjectValue(value));
        assertEquals(mockedLong, longType.getObjectValue(value));
        assertEquals(mockedCalendar, dateType.getObjectValue(value));
        assertEquals(mockedBigDecimal, decimalType.getObjectValue(value));
        assertEquals(mockedDouble, doubleType.getObjectValue(value));

        when(value.getString()).thenReturn(reference);
        assertEquals(reference, referenceType.getObjectValue(value));
    }

}
