package io.github.mlinardos.kvstore.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class kvClientFileReader {

    public  List<ServerAddress> readServerFile(String filePath) {

        List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] keyTypePair = line.split(" ");
                serverList.add(new ServerAddress(keyTypePair[0], Integer.parseInt(keyTypePair[1])));
            }
        }
        catch (Exception e) {
            System.out.println("Error while reading server address file: " + e.getMessage());
            return null;
        }

        return serverList;

    }

    public  List<String> readDataFile(String filePath) {

        List<String> dataLines = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                dataLines.add(line);
            }
        }
        catch (Exception e) {
            System.out.println("Error while reading data file: " + e.getMessage());
            return null;
        }
        return dataLines;

    }



}
