package io.github.mlinardos.kvstore.generator;

import java.util.ArrayList;
import java.util.List;

public class Node {

    String key;
    List<Node> children;
    int numChildren;
    int currentLevel;
    Object Value;
    NodeType type;

    public Node(String Key, int currentLevel, NodeType type) {
        this.key = Key;
        this.children = new ArrayList<>();
        this.numChildren = 0;
        this.currentLevel = currentLevel;
        this.Value = null;
        this.type = type;
    }

    public Node(int currentLevel){
        this.key = null;
        this.children = new ArrayList<>();
        this.numChildren = 0;
        this.currentLevel = currentLevel;
        this.Value = null;
        this.type = null;

    }
    public void setValue(Object value) {
        if(this.type == NodeType.LEAF) {
            this.Value = value;
        }
    }
    public Object getValue() {
        return this.Value;
    }


    public void addChild(Node child) {
        this.children.add(child);
    }

    public Node getChild(int index) {
        return this.children.get(index);
    }
    public int getNumChildren() {
        return this.numChildren;
    }
    public String getKey() {
        return this.key;
    }
    public NodeType getType() {
        return this.type;
    }


    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }
}

