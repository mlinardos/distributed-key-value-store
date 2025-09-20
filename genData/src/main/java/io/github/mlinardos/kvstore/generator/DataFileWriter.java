package io.github.mlinardos.kvstore.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class DataFileWriter {

    public static void writeDataFile(List<String> dataLines, String Name) {
        try (PrintWriter writer = new PrintWriter(new File(Name))) {

            dataLines.stream().forEach(writer::println);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
