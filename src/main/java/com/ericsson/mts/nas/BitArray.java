/*
 * Copyright 2019 Ericsson, https://www.ericsson.com/en
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ericsson.mts.nas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class BitArray {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private BigInteger length = BigInteger.ZERO;
    private int nextBit = 0;
    private int currentByte;

    public ByteArrayOutputStream getOutputStream() {

        return outputStream;
    }

    public synchronized void write(int b) throws IOException {
        if (nextBit == 0) {
            outputStream.write(b);
            length = length.add(BigInteger.valueOf(8));
        } else {
            for (int i = 0; i < 8; i++) {
                this.writeBit((b >> (8 - i)));
            }
        }
    }

    public synchronized void writeBit(int bit) throws IOException {
        currentByte = currentByte << 1 | (bit & 0x0001);
        length = length.add(BigInteger.ONE);
        nextBit++;
        if (nextBit == 8) {
            outputStream.write(currentByte);
            nextBit = 0;
            currentByte = 0;
        }
    }

    public synchronized void skipAlignedBits() throws IOException {
        while (nextBit != 0) {
            writeBit(0);
        }
    }

    public void concatBitArray(BitArray bitArray) throws IOException {
        ByteArrayOutputStream outputStream = bitArray.getOutputStream();
        for (byte octet : outputStream.toByteArray()) {
            this.write(octet);
        }
    }

    public BigInteger getLength() {
        return length;
    }

    public String getBinaryMessage() throws IOException {
        this.skipAlignedBits();
        return bytesToHex(outputStream.toByteArray());
    }

    public byte[] getBinaryArray() {
        return outputStream.toByteArray();
    }

    public String getActualBinaryMessage() {
        //Use for debug
        return bytesToHex(outputStream.toByteArray()) + Integer.toString(currentByte, 16);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
