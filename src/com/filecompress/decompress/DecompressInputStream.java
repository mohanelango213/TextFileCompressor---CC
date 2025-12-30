package com.filecompress.decompress;

import com.filecompress.Node;

import java.io.IOException;
import java.io.InputStream;

public class DecompressInputStream implements AutoCloseable {
    InputStream inputStream;
    Node rootNode;
    byte[] byteArray;
    long dataLength = 0;
    Node currentNode;
    int bitCount = 0;
    long readLength = 0;


    public DecompressInputStream(InputStream inputStream, Node rootNode, long dataLength) {
        this.inputStream = inputStream;
        this.rootNode = rootNode;
        this.currentNode = rootNode;
        this.dataLength = dataLength;
    }

    public Character readCharacter() throws IOException {

        if (bitCount == 0) {
            if (!loadNextByte())
                return null;
        }

        return navigateTree();
    }

    private boolean loadNextByte() throws IOException {
        bitCount = 8;
        byteArray = new byte[1];
        long length = inputStream.read(byteArray);
        return length != 0;
    }

    private Character navigateTree() throws IOException {
        boolean bit = (byteArray[0] & 128) == 128;
        byteArray[0] = (byte) (byteArray[0] << 1);
        bitCount--;
        readLength++;
        if (readLength > dataLength) {
            return null;
        }

        if (bit) {
            currentNode = currentNode.getRightNode();
        } else {
            currentNode = currentNode.getLeftNode();
        }

        if (currentNode.isLeafNode()) {
            Character element = currentNode.getElement();
            currentNode = rootNode;
            return element;
        }
        if (bitCount == 0) {
            loadNextByte();
        }
        return navigateTree();
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}
