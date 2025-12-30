package com.filecompress.decompress;

import com.filecompress.ByteUtil;
import com.filecompress.Constants;
import com.filecompress.Node;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class FileDecompressor {
    public static void main(String[] args) {
        try {
            File compressedFile = new File(Constants.DECOMPRESS_FILE_INPUT);
            if (!compressedFile.exists()) {
                System.err.println("File not Found : " + compressedFile.getAbsolutePath());
                System.exit(-1);
            }

            System.out.println("Decompressing... " + compressedFile.getAbsolutePath());
            double startTimeInMillis = System.currentTimeMillis();
            FileInputStream fis = new FileInputStream(compressedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] byteArray = new byte[Long.BYTES];
            bis.read(byteArray);
            long metaLength = ByteUtil.getLongFromBytes(byteArray);
            bis.read(byteArray);
            long dataLength = ByteUtil.getLongFromBytes(byteArray);

            byteArray = new byte[Math.toIntExact(metaLength)];
            bis.read(byteArray);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            HashMap<Character, BitSet> characterVsPath = (HashMap<Character, BitSet>) objectInputStream.readObject();

            Node rootNode = reConstructTree(characterVsPath);

            File outputFile = new File(Constants.DECOMPRESS_FILE_OUTPUT);
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            DecompressInputStream decompressInputStream = new DecompressInputStream(bis, rootNode, dataLength);
            Character character = decompressInputStream.readCharacter();
            while (character != null) {
                bos.write(character);
                character = decompressInputStream.readCharacter();
            }
            bos.flush();
            bos.close();
            decompressInputStream.close();

            double endTimeInMillis = System.currentTimeMillis();

            System.out.println("Decompressed Successfully");
            System.out.println("Time Taken : " + (endTimeInMillis - startTimeInMillis) / 1000 + " Seconds");
            double inputSize = compressedFile.length();
            double outputSize = outputFile.length();
            System.out.printf("Before Decompression : %.3f MB\n", inputSize / 1_000_000); //convert to MB
            System.out.printf("After Decompression : %.3f MB\n", outputSize / 1_000_000); //convert to MB
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        for (int i = 0; i < value.length() - 1; i++) {
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
        if (nextNode.getElement() != null) {
            System.err.println("Override found : " + nextNode.getElement());
            System.exit(-1);
        }
        nextNode.setElement(key);
    }
}
