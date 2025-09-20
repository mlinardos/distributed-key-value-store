package io.github.mlinardos.kvstore.generator;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TypeFileReader {
    public static Map<String, Type> readFile(String filePath){
        Map<String,Type> keyNameToTypeMap = new HashMap<>();
        //open file to read

        //read file line by line
        try (Scanner scanner = new Scanner(new File(filePath)))
        {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] keyTypePair = line.split(" ");
                String key = keyTypePair[0];
                String type = keyTypePair[1];
                if (type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("int")) {
                    keyNameToTypeMap.put(key, Integer.class);
                } else if (type.equalsIgnoreCase("String")) {
                    keyNameToTypeMap.put(key, String.class);
                } else if (type.equalsIgnoreCase("float")) {
                    keyNameToTypeMap.put(key, Float.class);
                } else if (type.equalsIgnoreCase("Boolean")) {
                    keyNameToTypeMap.put(key, Boolean.class);
                } else if (type.equalsIgnoreCase("Character")) {
                    keyNameToTypeMap.put(key, Character.class);
                }
            }
            return keyNameToTypeMap;
        }
        catch (Exception e){
                System.out.println("Error reading file : "+ e.getMessage());
            }

        return null;

    }
}
