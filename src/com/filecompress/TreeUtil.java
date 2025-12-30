package com.filecompress;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class TreeUtil {

    //code for testing
    public static void main(String[] args) {
        Node rootNode = new Node();
        Node leftNode = new Node();
        leftNode.setLeftNode(new Node('A', 100));
        leftNode.setRightNode(new Node('K', 101));
        rootNode.setLeftNode(leftNode);

        Node rightNode = new Node();
        rightNode.setLeftNode(new Node('B', 200));
        rightNode.setRightNode(new Node('C', 300));
        rootNode.setRightNode(rightNode);

        System.out.println(rootNode);

        HashMap<Character, BitSet> characterVsPath = createCharacterPathFromTree(rootNode);
        System.out.println(characterVsPath);

        System.out.println(reConstructTree(characterVsPath));
    }


    private static Node reConstructTree(HashMap<Character, BitSet> characterVsPath) {

        Node rootNode = new Node(null, 0);
        for (Map.Entry<Character, BitSet> entry : characterVsPath.entrySet()) {
            createPath(rootNode, entry.getKey(), entry.getValue());
        }
        return rootNode;
    }

    private static void createPath(Node rootNode, Character key, BitSet value) {
        Node nextNode = rootNode;
        for (int i = 0; i < value.length(); i++) {
            if (value.get(i)) {
                if (nextNode.getRightNode() == null) {
                    nextNode.setRightNode(new Node());
                }
                nextNode = nextNode.getRightNode();
            } else {
                if (nextNode.getLeftNode() == null) {
                    nextNode.setLeftNode(new Node());
                }
                nextNode = nextNode.getLeftNode();
            }
        }
        nextNode.setElement(key);
    }

    private static HashMap<Character, BitSet> createCharacterPathFromTree(Node rootNode) {
        HashMap<Character, BitSet> characterVsPath = new HashMap<>();
        createCharacterPathFromTree(rootNode, new BitSet(), 0, characterVsPath);
        return characterVsPath;
    }

    private static void createCharacterPathFromTree(Node rootNode, BitSet path, int depth, HashMap<Character, BitSet> characterVsPath) {
        if (rootNode.isLeafNode()) {
            path.set(depth + 1);

            characterVsPath.put(rootNode.getElement(), path);
            return;
        }

        if (rootNode.getLeftNode() != null) {
            BitSet clonedPath = (BitSet) path.clone();
            createCharacterPathFromTree(rootNode.getLeftNode(), clonedPath, depth + 1, characterVsPath);
        }

        if (rootNode.getRightNode() != null) {
            BitSet clonedPath = (BitSet) path.clone();
            clonedPath.set(depth);
            createCharacterPathFromTree(rootNode.getRightNode(), clonedPath, depth + 1, characterVsPath);
        }
    }

    public static void printCharacterVsPath(HashMap<Character, BitSet> characterVsPath, HashMap<Character, Integer> characterCountMap) {
        System.out.println("------------------------------ Character Vs Path ------------------------------");
        for (Map.Entry<Character, BitSet> entry : characterVsPath.entrySet()) {
            System.out.print(getPrintableChar(entry.getKey()) + " : ");
            BitSet bitset = entry.getValue();
            for (int i = 0; i < bitset.length(); i++) {
                System.out.print(bitset.get(i) ? '1' : '0');
            }
            System.out.print(" : Count-" + characterCountMap.get(entry.getKey()));
            System.out.print(" : ASCII-" + (int) entry.getKey());
            System.out.println();
        }
        System.out.println("------------------------------ Character Vs Path Ends ------------------------------");
    }

    public static String getPrintableChar(char c) {
        switch (c) {
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            case ' ':
                return "[space]"; // Optional: clearer than an empty gap
            default:
                return String.valueOf(c);
        }
    }

}
