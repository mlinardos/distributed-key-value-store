package io.github.mlinardos.kvstore.generator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class TreeGenerator {


    List<KAryTree> listOfTrees;
    Map<String, Type> keyNameToTypeMap;
    List<String> topKeyNames;
    int MaxHeight;
    int MaxChildren;
    int numberOfTrees;
    int maxStringLength;

    public TreeGenerator( Map<String, Type> keyNameToTypeMap, int MaxHeight, int MaxChildren, int numberOfTrees,int maxStringLength) {
        this.listOfTrees = new ArrayList<>();
        this.keyNameToTypeMap = keyNameToTypeMap;
        this.MaxHeight = MaxHeight;
        this.MaxChildren = MaxChildren;
        this.numberOfTrees = numberOfTrees;
        this.topKeyNames = generateTopKeys(numberOfTrees);
        this.maxStringLength = maxStringLength;
        generateRoots();
        populateTrees();
    }

    public void printTrees(){
        for (KAryTree tree : listOfTrees) {
            System.out.println("****************************");
            tree.traverse();
        }
    }

    public List<String> generateTopKeys(int numberOfTrees){
        List<String> topKeyNames = new ArrayList<>();
        for (int i = 1; i <= numberOfTrees; i++) {
            topKeyNames.add("person"+i);
        }
       return topKeyNames;
    }

    public void generateRoots(){
        for(int i=0; i<numberOfTrees; i++){
            KAryTree tree = new KAryTree();
            tree.keysPerLevel = new java.util.HashMap<>();
            tree.nodesPerLevel = new java.util.HashMap<>();
            tree.Height = TreeRandomizer.generateTreeHeight(MaxHeight);
            tree.root = new Node( topKeyNames.get(i), 0, NodeType.ROOT);
            listOfTrees.add(tree);
        }
    }
    public void populateTrees(){
        for (KAryTree tree : listOfTrees) {
            populateTree(tree);
        }
    }

    public void setNodeAsLeaf(Node node,KAryTree tree){
        if(node.type != NodeType.ROOT) {
            node.type = NodeType.LEAF;
            node.key = generateUniqueKeyForNodeLevel(tree, node.currentLevel);
            node.Value = TreeRandomizer.generateRandomValue(this.keyNameToTypeMap.get(node.key), maxStringLength);
        }
         return ;

    }

    public String generateUniqueKeyForNodeLevel(KAryTree tree, int level){
        String key;
        HashSet<String> keys = tree.keysPerLevel.get(level);
        if(keys == null){
            keys = new HashSet<>();
            tree.keysPerLevel.put(level, keys);
            key = TreeRandomizer.generateRandomKey(this.keyNameToTypeMap);
        }
        else {
            key = TreeRandomizer.generateRandomKey(this.keyNameToTypeMap);
            while (keys.contains(key)) {
                key = TreeRandomizer.generateRandomKey(this.keyNameToTypeMap);
            }
        }
        keys.add(key);
        return key;
    }
    public void setNodeAsInternal(Node node,KAryTree tree){
       if(node.type != NodeType.ROOT) {
           node.type = NodeType.INTERNAL;
           node.key = generateUniqueKeyForNodeLevel(tree, node.currentLevel);
       }



    }
    public void generateChildren(Node node, LinkedBlockingQueue<Node> queue){
        for(int i=0; i<node.numChildren; i++) {
            Node child = new Node(node.currentLevel+1);
            node.addChild(child);
            queue.offer(child);
        }
    }


    public void populateTree(KAryTree tree){

        LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
        queue.offer(tree.root);
        while(!queue.isEmpty()) {
            Node node = queue.poll();
            if(node.currentLevel == tree.Height){
                if(node.getType() == NodeType.ROOT){
                    return ;
                }
                else{
                    setNodeAsLeaf(node,tree);
                }
            }
            else{
                int numChildren = generateNumberOfChildrenPerNode(MaxChildren,tree, node.currentLevel);
                node.setNumChildren(numChildren);
                if(numChildren == 0){
                    setNodeAsLeaf(node,tree);
                }
                else{
                    setNodeAsInternal(node,tree);
                    generateChildren(node,queue);

                }

            }
        }

    }

    public int generateNumberOfChildrenPerNode(int MaxChildren, KAryTree tree,int currentLevel){
        Map<Integer,Integer> childrenCountMap = tree.nodesPerLevel;
        int childrenCount ;
        if(childrenCountMap.get(currentLevel+1) == null){
            childrenCount = TreeRandomizer.generateNodeChildrenCount(MaxChildren);
              childrenCountMap.put(currentLevel+1,childrenCount);
        }
        else{
             childrenCount = TreeRandomizer.generateNodeChildrenCount(MaxChildren - childrenCountMap.get(currentLevel));
           childrenCountMap.put(currentLevel+1,childrenCountMap.get(currentLevel+1)+childrenCount);
        }


        return childrenCount;
    }

    public List<String> getTreesAsStrings(){
        List<String> treesAsStrings = new ArrayList<>();
        for (KAryTree tree : listOfTrees) {
            treesAsStrings.add(tree.toString());
        }
        return treesAsStrings;

    }

}
