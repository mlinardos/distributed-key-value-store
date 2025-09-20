package io.github.mlinardos.kvstore.server;

import java.util.concurrent.locks.ReentrantLock;

public class Trie {

    protected TrieNode root;
    private final ReentrantLock lock = new ReentrantLock();



    public Trie() {
        root = new TrieNode();
    }
    public void insert(String key, Object value){
        lock.lock();
        try{
            TrieNode current = root;
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            TrieNode node = current.getChild(ch);
            if (node == null) {
                node = new TrieNode();
                current.putChild(ch, node);
            }
            current = node;
        }
        current.setCompleteKey(true);
        current.setValue(value);}
        finally{
            lock.unlock();
        }
    }

    public void delete(String key){
        lock.lock();
        try {
            delete(root, key, 0);
        }
        finally {
            lock.unlock();
        }

    }

    private boolean delete(TrieNode current, String key, int index) {
        if (index == key.length()) {
            if (!current.isCompleteKey()) {
                return false;
            }
            current.setCompleteKey(false);
            return current.getChildren().isEmpty();
        }
        char ch = key.charAt(index);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, key, index + 1) && !node.isCompleteKey();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }


    public Object search(String key){
        lock.lock();
        Object result =null ;
        try {
            TrieNode current = root;
            boolean found = false;
            for (int i = 0; i < key.length(); i++) {
                char ch = key.charAt(i);
                TrieNode node = current.getChild(ch);
                if (node == null) {
                    throw new RuntimeException("Key not found");
                }
                current = node;
            }
            if (current.isCompleteKey()) {
                result = current.getValue();
                found = true;
            }
            if (!found) {
                throw new RuntimeException("Key not found");
            }
        }
        finally {
            lock.unlock();
        }
        return result;
    }



}
