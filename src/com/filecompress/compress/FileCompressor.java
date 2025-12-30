package com.filecompress.compress;

import com.filecompress.ByteUtil;
import com.filecompress.Constants;
import com.filecompress.Node;
import com.filecompress.bit.BitOutputStream;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class FileCompressor {
    public static void main(String[] args) {
        File inputFile = new File(Constants.COMPRESS_FILE_INPUT);
        File outputFile = new File(Constants.COMPRESS_FILE_OUTPUT);

        double startTimeInMillis = System.currentTimeMillis();

        if (!inputFile.exists()) {
            System.err.print("File Not Found : " + inputFile.getAbsolutePath());
            return;
        }
        System.out.println("Compressing... " + inputFile.getAbsolutePath());
        HashMap<Character, Integer> characterCountMap = getCharacterCountMap(inputFile);
        PriorityQueue<Node> priorityQueue = getCharacterCountPriorityQueue(characterCountMap);

        Node rootNode = createTreeFromPriorityQueue(priorityQueue);
        HashMap<Character, BitSet> characterVsPath = createCharacterPathFromTree(rootNode);

        long metaLength = getObjectByteLength(characterVsPath);
        long dataLength = getDataLength(characterVsPath, characterCountMap);

        compressFile(characterVsPath, metaLength, dataLength);

        double endTimeInMillis = System.currentTimeMillis();

        System.out.println("Time Taken : " + (endTimeInMillis - startTimeInMillis) / 1000 + " Seconds");
        double inputSize = inputFile.length();
        double outputSize = outputFile.length();
        System.out.printf("Before Compression : %.3f MB\n", inputSize / 1_000_000); //convert to MB
        System.out.printf("After Compression : %.3f MB\n", outputSize / 1_000_000); //convert to MB
        System.out.printf("Compression ratio : %.3f %%", (outputSize / inputSize) * 100);
    }

    private static long getDataLength(HashMap<Character, BitSet> characterVsPath, HashMap<Character, Integer> characterCountMap) {
        long dataLength = 0;
        for (Map.Entry<Character, BitSet> entry : characterVsPath.entrySet()) {
            int length = (entry.getValue().length() - 1) * characterCountMap.get(entry.getKey());
            dataLength = dataLength + length;
        }
        return dataLength;
    }

    private static void compressFile(HashMap<Character, BitSet> metaData, Long metaLength, Long dataLength) {
        try {
            FileOutputStream fos = new FileOutputStream(Constants.COMPRESS_FILE_OUTPUT);
            BufferedOutputStream buffOutputStream = new BufferedOutputStream(fos);
            buffOutputStream.write(ByteUtil.getBytesFromLong(metaLength));
            buffOutputStream.write(ByteUtil.getBytesFromLong(dataLength));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffOutputStream);
            objectOutputStream.writeObject(metaData);
            objectOutputStream.flush();

            BitOutputStream bitOutputStream = new BitOutputStream(buffOutputStream);

            FileReader fileReader = new FileReader(Constants.COMPRESS_FILE_INPUT);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            char[] characterArray = new char[512];
            long length = bufferedReader.read(characterArray);
            while (length > 0) {
                for (int i = 0; i < length; i++) {
                    bitOutputStream.writeBitSet(metaData.get(characterArray[i]));
                }
                length = bufferedReader.read(characterArray);
            }

            bitOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long getObjectByteLength(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            long size = byteArrayOutputStream.size();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return size;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<Character, BitSet> createCharacterPathFromTree(Node rootNode) {
        HashMap<Character, BitSet> characterVsPath = new HashMap<>();
        createCharacterPathFromTree(rootNode, new BitSet(), 0, characterVsPath);
        return characterVsPath;
    }

    private static void createCharacterPathFromTree(Node rootNode, BitSet path, int depth, HashMap<Character, BitSet> characterVsPath) {
        if (rootNode.isLeafNode()) {
            path.set(depth);
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

    private static HashMap<Character, Integer> getCharacterCountMap(File inputFile) {
        HashMap<Character, Integer> characterCountMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            char[] buff = new char[512];
            int length = bufferedReader.read(buff);
            while (length > 0) {
                for (int i = 0; i < length; i++) {
                    characterCountMap.put(buff[i], characterCountMap.getOrDefault(buff[i], 0) + 1);
                }
                length = bufferedReader.read(buff);
            }
            return characterCountMap;
        } catch (Exception e) {
            System.out.println("Exception while getting characterCountMap" + e);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static PriorityQueue<Node> getCharacterCountPriorityQueue(HashMap<Character, Integer> characterCountMap) {
        PriorityQueue<Node> nodePriorityQueue = new PriorityQueue<>();
        characterCountMap.forEach((key, value) -> {
            Node node = new Node(key, value);
            nodePriorityQueue.add(node);
        });
        return nodePriorityQueue;
    }

    private static Node createTreeFromPriorityQueue(PriorityQueue<Node> priorityQueue) {
        while (priorityQueue.size() > 1) {
            Node node1 = priorityQueue.poll();
            Node node2 = priorityQueue.poll();

            Node mergedNode = new Node(null, node1.getCount() + node2.getCount());
            mergedNode.setLeftNode(node1);
            mergedNode.setRightNode(node2);
            priorityQueue.add(mergedNode);
        }
        return priorityQueue.poll();
    }
}
