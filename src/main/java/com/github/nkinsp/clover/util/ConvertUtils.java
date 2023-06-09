package com.github.nkinsp.clover.util;

/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

/**
 */
@SuppressWarnings("rawtypes")
public class ConvertUtils{

    public static String convertToString(Object value){
        if(value == null){
            return null;
        }
        return value.toString();
    }

    public static Byte convertToByte(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Number){
            return ((Number) value).byteValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            return Byte.parseByte(strVal);
        }
        throw new RuntimeException("can not cast to byte, value : " + value);
    }

    public static Character convertToChar(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Character){
            return (Character) value;
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0){
                return null;
            }
            if(strVal.length() != 1){
                throw new RuntimeException("can not cast to char, value : " + value);
            }
            return strVal.charAt(0);
        }
        throw new RuntimeException("can not cast to char, value : " + value);
    }

    public static Short convertToShort(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Number){
            return ((Number) value).shortValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            return Short.parseShort(strVal);
        }
        throw new RuntimeException("can not cast to short, value : " + value);
    }

    public static BigDecimal convertToBigDecimal(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof BigDecimal){
            return (BigDecimal) value;
        }
        if(value instanceof BigInteger){
            return new BigDecimal((BigInteger) value);
        }
        String strVal = value.toString();
        if(strVal.length() == 0){
            return null;
        }
        if(value instanceof Map && ((Map) value).size() == 0){
            return null;
        }
        return new BigDecimal(strVal);
    }

    public static BigInteger convertToBigInteger(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof BigInteger){
            return (BigInteger) value;
        }
        if(value instanceof Float || value instanceof Double){
            return BigInteger.valueOf(((Number) value).longValue());
        }
        String strVal = value.toString();
        if(strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)){
            return null;
        }
        return new BigInteger(strVal);
    }

    public static Float convertToFloat(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Number){
            return ((Number) value).floatValue();
        }
        if(value instanceof String){
            String strVal = value.toString();
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if(strVal.indexOf(',') != 0){
                strVal = strVal.replaceAll(",", "");
            }
            return Float.parseFloat(strVal);
        }
        throw new RuntimeException("can not cast to float, value : " + value);
    }

    public static Double convertToDouble(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        if(value instanceof String){
            String strVal = value.toString();
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if(strVal.indexOf(',') != 0){
                strVal = strVal.replaceAll(",", "");
            }
            return Double.parseDouble(strVal);
        }
        throw new RuntimeException("can not cast to double, value : " + value);
    }

    public static Date convertToDate(Object value){
        return convertToDate(value, null);
    }

    public static Date convertToDate(Object value, String format){
        if(value == null){
            return null;
        }
        if(value instanceof Date){ // 使用频率最高的，应优先处理
            return (Date) value;
        }
        if(value instanceof Calendar){
            return ((Calendar) value).getTime();
        }
        long longValue = -1;
        if(value instanceof Number){
            longValue = ((Number) value).longValue();
            return new Date(longValue);
        }
        if(value instanceof String){
        	
        }
        return new Date(longValue);
    }

    public static java.sql.Date convertToSqlDate(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof java.sql.Date){
            return (java.sql.Date) value;
        }
        if(value instanceof java.util.Date){
            return new java.sql.Date(((java.util.Date) value).getTime());
        }
        if(value instanceof Calendar){
            return new java.sql.Date(((Calendar) value).getTimeInMillis());
        }
        long longValue = 0;
        if(value instanceof Number){
            longValue = ((Number) value).longValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if(isNumber(strVal)){
                longValue = Long.parseLong(strVal);
            }
        }
        if(longValue <= 0){
            throw new RuntimeException("can not cast to Date, value : " + value); // TODO 忽略 1970-01-01 之前的时间处理？
        }
        return new java.sql.Date(longValue);
    }

    public static java.sql.Time convertToSqlTime(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof java.sql.Time){
            return (java.sql.Time) value;
        }
        if(value instanceof java.util.Date){
            return new java.sql.Time(((java.util.Date) value).getTime());
        }
        if(value instanceof Calendar){
            return new java.sql.Time(((Calendar) value).getTimeInMillis());
        }
        long longValue = 0;
        if(value instanceof Number){
            longValue = ((Number) value).longValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equalsIgnoreCase(strVal)){
                return null;
            }
            if(isNumber(strVal)){
                longValue = Long.parseLong(strVal);
            }
        }
        if(longValue <= 0){
            throw new RuntimeException("can not cast to Date, value : " + value); // TODO 忽略 1970-01-01 之前的时间处理？
        }
        return new java.sql.Time(longValue);
    }

    public static java.sql.Timestamp convertToTimestamp(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Calendar){
            return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
        }
        if(value instanceof java.sql.Timestamp){
            return (java.sql.Timestamp) value;
        }
        if(value instanceof java.util.Date){
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        long longValue = 0;
        if(value instanceof Number){
            longValue = ((Number) value).longValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if(strVal.endsWith(".000000000")){
                strVal = strVal.substring(0, strVal.length() - 10);
            } else if(strVal.endsWith(".000000")){
                strVal = strVal.substring(0, strVal.length() - 7);
            }
            if(isNumber(strVal)){
                longValue = Long.parseLong(strVal);
            } 
        }
        if(longValue <= 0){
            throw new RuntimeException("can not cast to Timestamp, value : " + value);
        }
        return new java.sql.Timestamp(longValue);
    }

    public static boolean isNumber(String str){
        for(int i = 0; i < str.length(); ++i){
            char ch = str.charAt(i);
            if(ch == '+' || ch == '-'){
                if(i != 0){
                    return false;
                } 
            } else if(ch < '0' || ch > '9'){
                return false;
            }
        }
        return true;
    }

	public static Long convertToLong(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Number){
            return ((Number) value).longValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if(strVal.indexOf(',') != 0){
                strVal = strVal.replaceAll(",", "");
            }
            try{
                return Long.parseLong(strVal);
            } catch(NumberFormatException ex){
                //
            }
        }
        if(value instanceof Map){
            Map map = (Map) value;
            if(map.size() == 2
                    && map.containsKey("andIncrement")
                    && map.containsKey("andDecrement")){
                Iterator iter = map.values().iterator();
                iter.next();
                Object value2 = iter.next();
                return convertToLong(value2);
            }
        }
        throw new RuntimeException("can not cast to long, value : " + value);
    }


	public static Integer convertToInt(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Integer){
            return (Integer) value;
        }
        if(value instanceof Number){
            return ((Number) value).intValue();
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if(strVal.indexOf(',') != 0){
                strVal = strVal.replaceAll(",", "");
            }
            return Integer.parseInt(strVal);
        }
        if(value instanceof Boolean){
            return ((Boolean) value).booleanValue() ? 1 : 0;
        }
        if(value instanceof Map){
            Map map = (Map) value;
            if(map.size() == 2
                    && map.containsKey("andIncrement")
                    && map.containsKey("andDecrement")){
                Iterator iter = map.values().iterator();
                iter.next();
                Object value2 = iter.next();
                return convertToInt(value2);
            }
        }
        throw new RuntimeException("can not cast to int, value : " + value);
    }

    public static byte[] convertToBytes(Object value){
        if(value instanceof byte[]){
            return (byte[]) value;
        }
        throw new RuntimeException("can not cast to int, value : " + value);
    }

    public static Boolean convertToBoolean(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Boolean){
            return (Boolean) value;
        }
        if(value instanceof Number){
            return ((Number) value).intValue() == 1;
        }
        if(value instanceof String){
            String strVal = (String) value;
            if(strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)){
                return null;
            }
            if("true".equalsIgnoreCase(strVal) //
                    || "1".equals(strVal)){
                return Boolean.TRUE;
            }
            if("false".equalsIgnoreCase(strVal) //
                    || "0".equals(strVal)){
                return Boolean.FALSE;
            }
            if("Y".equalsIgnoreCase(strVal) //
                    || "T".equals(strVal)){
                return Boolean.TRUE;
            }
            if("F".equalsIgnoreCase(strVal) //
                    || "N".equals(strVal)){
                return Boolean.FALSE;
            }
        }
        throw new RuntimeException("can not cast to boolean, value : " + value);
    }


	@SuppressWarnings({ "unchecked" })
	public static <T> T convertTo(Object obj, Class<T> classType) {

		try {
			if (obj == null) {
				if (classType == int.class) {
					return (T) Integer.valueOf(0);
				} else if (classType == long.class) {
					return (T) Long.valueOf(0);
				} else if (classType == short.class) {
					return (T) Short.valueOf((short) 0);
				} else if (classType == byte.class) {
					return (T) Byte.valueOf((byte) 0);
				} else if (classType == float.class) {
					return (T) Float.valueOf(0);
				} else if (classType == double.class) {
					return (T) Double.valueOf(0);
				} else if (classType == boolean.class) {
					return (T) Boolean.FALSE;
				}
				return null;
			}

			if (classType == null) {
				throw new IllegalArgumentException("clazz is null");
			}

			if (classType == obj.getClass()) {
				return (T) obj;
			}

			if (obj instanceof Map) {
				if (classType == Map.class) {
					return (T) obj;
				}
			}

			if (obj instanceof Number) {

				if (classType == int.class || classType == Integer.class) {

					return (T) Integer.valueOf(((Number) obj).intValue());

				}

				if (classType == long.class || classType == Long.class) {
					return (T) Long.valueOf(((Number) obj).longValue());
				}
				if (classType == BigInteger.class) {
					return (T) BigInteger.valueOf(((Number) obj).longValue());
				}
			}

			if (classType.isAssignableFrom(obj.getClass())) {
				return (T) obj;
			}

			if (classType == boolean.class || classType == Boolean.class) {
				return (T) convertToBoolean(obj);
			}

			if (classType == byte.class || classType == Byte.class) {
				return (T) convertToByte(obj);
			}

			if (classType == char.class || classType == Character.class) {
				return (T) convertToChar(obj);
			}

			if (classType == short.class || classType == Short.class) {
				return (T) convertToShort(obj);
			}

			if (classType == int.class || classType == Integer.class) {
				return (T) convertToInt(obj);
			}

			if (classType == long.class || classType == Long.class) {
				return (T) convertToLong(obj);
			}

			if (classType == float.class || classType == Float.class) {
				return (T) convertToFloat(obj);
			}

			if (classType == double.class || classType == Double.class) {
				return (T) convertToDouble(obj);
			}

			if (classType == String.class) {
				return (T) convertToString(obj);
			}

			if (classType == BigDecimal.class) {
				return (T) convertToBigDecimal(obj);
			}

			if (classType == BigInteger.class) {
				return (T) convertToBigInteger(obj);
			}

			if (classType == Date.class) {
				return (T) convertToDate(obj);
			}

			if (classType == java.sql.Date.class) {
				return (T) convertToSqlDate(obj);
			}

			if (classType == java.sql.Time.class) {
				return (T) convertToSqlTime(obj);
			}

			if (classType == java.sql.Timestamp.class) {
				return (T) convertToTimestamp(obj);
			}

			if (obj instanceof String) {
				String strVal = (String) obj;
				if (strVal.length() == 0 //
						|| "null".equals(strVal) //
						|| "NULL".equals(strVal)) {
					return null;
				}

				if (classType == java.util.Currency.class) {
					return (T) java.util.Currency.getInstance(strVal);
				}

			}

			if (!ObjectUtils.isEmpty(obj)) {
				T newObj = BeanUtils.instantiateClass(classType);
				BeanUtils.copyProperties(obj, newObj);
				return newObj;
			}
		}  catch (Exception e) {
			throw new RuntimeException("can not cast to : " + classType.getName());
		}

		return (T) obj;
		
	}



 





    




    






}
