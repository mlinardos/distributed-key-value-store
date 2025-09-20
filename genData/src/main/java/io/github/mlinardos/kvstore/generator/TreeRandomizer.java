package io.github.mlinardos.kvstore.generator;

import java.lang.reflect.Type;
import java.util.Map;

public class TreeRandomizer {


    public static int generateTreeHeight(int maxHeight) {
        return (int) Math.round((Math.random() * maxHeight));
    }

    public static int generateNodeChildrenCount(int maxNumberOfChildren) {
        if(maxNumberOfChildren <= 0){
            return 0;
        }
        else {
            return (int) Math.round((Math.random() * maxNumberOfChildren));
        }
    }



    public static String generateRandomKey( Map<String, Type>  keyNameToTypeMap) {
        int randomIndex = (int) (Math.random() * keyNameToTypeMap.size());

      return (String) keyNameToTypeMap.keySet().toArray()[randomIndex];


    }
   public Type getTypeForKey( Map<String, Type>  keyNameToTypeMap,String key) {
        return keyNameToTypeMap.get(key);
    }
    public static Object generateRandomValue(Type type, int maxStringLength) {
        if (type == Integer.class) {
            return (int) (Math.random() * 100);
        } else if (type == String.class) {
            int stringLength = (int) Math.round ( (Math.random() * maxStringLength));
            String randomString = "";
            for (int i = 0; i < stringLength; i++) {
                randomString += (char) ((int) (Math.random() * 26) + 97);
            }
            return randomString;
        } else if (type == Double.class) {
            return Math.random() * 100;
        } else if (type == Boolean.class) {
            return Math.random() > 0.5;
        } else if (type == Float.class) {
            return (float) (Math.random() * 100);
        } else if (type == Long.class) {
            return (long) (Math.random() * 100);
        } else if (type == Short.class) {
            return (short) (Math.random() * 100);
        } else if (type == Byte.class) {
            return (byte) (Math.random() * 100);
        } else if (type == Character.class) {
            return (char) ((int) (Math.random() * 26) + 97);
        }

        return null;
    }



}
