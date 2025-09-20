package io.github.mlinardos.kvstore.generator;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class genData {
    public static void main(String[] args) {
        if(args.length < 10 || (!args[0].equals("-k") || !args[2].equals("-n") || !args[4].equals("-d") || !args[6].equals("-l") || !args[8].equals("-m"))){
            System.out.println("Usage: java -cp io.github.mlinardos.kvstore.generator.genData -k <keyFile> -n <number Of Lines> -d <max Level Of Nesting> -l <max Length Of Sting> -m <keys inside each value>");
            System.exit(1);
        }
        String keyFileName = args[1];
        int numberOfTrees = Integer.parseInt(args[3]);
        int maxHeight = Integer.parseInt(args[5]) +1;
        int maxStringLength = Integer.parseInt(args[7]);
        int maxChildren = Integer.parseInt(args[9]);
        Map<String, Type> typeMap = TypeFileReader.readFile(keyFileName);
        if(typeMap == null){
            System.out.println("Error reading key file");
            System.exit(1);
        }
        TreeGenerator treeGenerator = new TreeGenerator(typeMap,maxHeight,maxChildren,numberOfTrees,maxStringLength);
        List<String> dataLines = treeGenerator.getTreesAsStrings();
        DataFileWriter.writeDataFile(dataLines,"dataToIndex.txt");
    }
}