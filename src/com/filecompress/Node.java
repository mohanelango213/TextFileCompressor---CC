package com.filecompress;

import java.io.Serializable;

public class Node implements Serializable, Comparable<Node> {
    private Character element = null;
    private Node leftNode = null;
    private Node rightNode = null;
    private transient int count = 0;

    public Node(Character c, int count) {
        element = c;
        this.count = count;
    }

    public Node() {
    }

    public Character getElement() {
        return element;
    }
    public void setElement(Character c) {
        element = c;
    }

    public int getCount() {
        return count;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public boolean isLeafNode() {
        return rightNode == null && leftNode == null;
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(count, node.getCount());
    }

    public String toString() {
        if (element != null) {
            return "\"" + TreeUtil.getPrintableChar(element) + "\"" + "| Count:" + count + " | ASCII:" + (int) element + "\n";
        } else {
            return (leftNode != null ? leftNode.toString() : "") +
                    (rightNode != null ? rightNode.toString() : "");

        }
    }
}