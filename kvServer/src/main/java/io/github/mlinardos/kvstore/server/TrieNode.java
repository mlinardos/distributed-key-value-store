package io.github.mlinardos.kvstore.server;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private Map<Character,TrieNode> children;
    private boolean isCompleteKey;
    private Object value;

    public TrieNode() {
        children = new HashMap<>();
        isCompleteKey = false;
        value = null;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public void setChildren(Map<Character, TrieNode> children) {
        this.children = children;
    }

    public boolean isCompleteKey() {
        return isCompleteKey;
    }

    public void setCompleteKey(boolean completeKey) {
        isCompleteKey = completeKey;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void putChild(Character c, TrieNode node) {
        children.put(c, node);
    }

    public TrieNode getChild(Character c) {
        return children.get(c);
    }





   }

