package com.filecompress;

import java.nio.ByteBuffer;

public class ByteUtil {
    public static byte[] getBytesFromLong(Long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(value);
        return byteBuffer.array();
    }

    public static Long getLongFromBytes(byte[] byteArray) {
        ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
        longBuffer.put(byteArray);
        longBuffer.flip();
        return longBuffer.getLong();
    }

    public static void printByte(byte byteValue) {
        System.out.println();
        System.out.print("Byte Value : ");
        for (int i = 0; i < 8; i++) {
            boolean bit = (byteValue & 128) == 128;
            System.out.print(bit ? 1 : 0);
            byteValue = (byte) (byteValue << 1);
        }
        if (byteValue != 0) {
            System.out.println("Error found : " + byteValue);
        }
        System.out.println();
    }
}
