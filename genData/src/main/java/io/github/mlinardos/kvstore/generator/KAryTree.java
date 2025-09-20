package io.github.mlinardos.kvstore.generator;


import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class KAryTree {

    Node root;
    int Height;
    Map<Integer, HashSet<String>> keysPerLevel;
    Map<Integer,Integer> nodesPerLevel;



    public KAryTree() {
        this.root = null;
        this.Height = 0;
    }
    public KAryTree(Node root, int Height) {
        this.root = root;
        this.Height = Height;
    }
    //breadth first traversal
    public void traverse() {

        System.out.println("Tree Traversal, Height: " + this.Height);
        LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
        queue.offer(this.root);
        while(!queue.isEmpty()) {
            Node node = queue.poll();
            System.out.println(" Node key:" +node.getKey() +" Node Value:"+ node.getValue()+ " Node Type:" + node.getType() + " Node Level:" + node.currentLevel + " Node NumChildren:" + node.getNumChildren());
            for(int i=0; i<node.getNumChildren(); i++) {
                queue.offer(node.getChild(i));
            }
        }
    }
    public String toString() {
        StringBuilder result = new StringBuilder();
              recursiveDepthFirstTraversal(this.root, result);
        return result.toString();
    }


    public void recursiveDepthFirstTraversal(Node node, StringBuilder result) {
        if (node == null) {
            return ;
        }
        if(node.getType()  == NodeType.ROOT){
            result.append('"'+node.getKey()+'"' + "  ->  [ ");
        } else if (node.getType() == NodeType.INTERNAL) {
            result.append( '"'+node.getKey()+'"' + "  ->  [ ");
        } else {
            if(node.getValue().getClass() == String.class) {
                result.append( '"'+node.getKey()+'"' + "  ->  " + '"' + node.getValue() + '"' + "  ");
            } else {
                result.append( '"'+node.getKey()+'"' + "  ->  " + node.getValue() + "  ");
            }


        }
        for(int i=0; i<node.getNumChildren(); i++) {
            recursiveDepthFirstTraversal(node.getChild(i), result);
            if( (i+1)  <node.getNumChildren() && node.getChild(i+1)!= null  ) result.append(" | ");
        }
        if(node.getType()  == NodeType.ROOT){
            result.append(" ]");
        }
        else if (node.getType() == NodeType.INTERNAL) {
            result.append( " ]");
        }

    }


}
