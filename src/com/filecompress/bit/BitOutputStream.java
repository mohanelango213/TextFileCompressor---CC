package com.filecompress.bit;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

public class BitOutputStream implements AutoCloseable {

    private final OutputStream outputStream;
    private int byteLength = 0;
    private byte dataByte = 0;

    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public void writeBit(int bit) throws IOException {
        dataByte = (byte) (dataByte << 1 | bit);
        byteLength++;

        if (byteLength == 8) {
            outputStream.write(dataByte);
            byteLength = 0;
            dataByte = 0;
        }
    }

    public void writeBitSet(BitSet bitSet) throws IOException {
        for (int i = 0; i < (bitSet.length() - 1); i++) {
            writeBit(bitSet.get(i) ? 1 : 0);
        }
    }

    @Override
    public void close() throws Exception {
        if (byteLength != 0) {
            outputStream.write(dataByte);
        }
        outputStream.flush();
        outputStream.close();
    }
}
