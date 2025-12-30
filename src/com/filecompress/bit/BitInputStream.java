package com.filecompress.bit;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {
    InputStream inputStream;
    byte currentByte = 0;
    int bitCount = 0;

    public BitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int readBit() throws IOException {
        if (bitCount == 8) {
            byte[] singleByte = new byte[1];
            int length = inputStream.read(singleByte);
            if (length == 0) {
                return -1;
            }
            currentByte = singleByte[0];
            bitCount = 0;
        }

        int bit = (currentByte & 128) == 128 ? 1 : 0;
        currentByte = (byte) (currentByte << 1);
        bitCount++;
        return bit;
    }
}
