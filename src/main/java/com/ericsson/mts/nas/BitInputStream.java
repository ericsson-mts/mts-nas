package com.ericsson.mts.nas;/*
 /*
 * Copyright 2019 Ericsson, https://www.ericsson.com/en
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class BitInputStream extends InputStream {
    private final InputStream byteStream;
    private int currentBit = 0;
    private int currentByte;

    public BitInputStream(final InputStream byteStream) {
        this.byteStream = new InputStream(){
            public int available() throws IOException {
                return byteStream.available();
            }

            @Override
            public int read() throws IOException {
                int value = byteStream.read();
                if(-1 == value){
                    throw new RuntimeException("reached end of stream");
                } else {
                    return value;
                }
            }
        };
    }

    public synchronized int read() throws IOException {
        if (currentBit == 0) {
            return byteStream.read();
        } else {
            int nextByte = byteStream.read();
            int result = ((currentByte << currentBit) | (nextByte >> (8 - currentBit))) & 0xFF;
            currentByte = nextByte;
            return result;
        }
    }

    public synchronized int readBit() throws IOException {
        if (currentBit == 0) {
            currentByte = byteStream.read();
        }
        currentBit++;
        int result = currentByte >> (8 - currentBit) & 0x1;
        if (currentBit > 7) {
            currentBit = 0;
        }
        return result;
    }

    public synchronized byte[] readBitsAsArray(int nBits) throws IOException {
        int arrayLen = (nBits / 8) + (nBits % 8 > 0 ? 1 : 0);
        byte[] array = new byte[arrayLen];

        for (int i = 0; i < nBits; i++) {
            int index = arrayLen - i / 8;
            int bit = readBit();
            array[index] = (byte) (array[index] | (bit << (8 - (8 - (arrayLen * 8 - i) % 8))) & 0xff);
        }
        return array;
    }

    public synchronized byte[] readAlignedBitArray(int nBits) throws IOException {
        int nBytes = nBits / 8 + ((nBits % 8 > 0) ? 1 : 0);
        byte[] bytes = new byte[nBytes];
        for (int i = 0; i < nBits; i++) {
            int index = i / 8;
            bytes[index] = (byte) (bytes[index] | readBit() << (7 - (i % 8)) & 0xff);
        }
        return bytes;
    }


    public synchronized int readBits(int nBits) throws IOException {
        int result = 0;
        if (nBits > 32) {
            throw new RuntimeException("nBits (" + nBits + ") sup to 32");
        }
        for (int i = 0; i < nBits; i++) {
            result = ((result << 1) | readBit());
        }
        return result;
    }

    public synchronized BigInteger bigReadBits(int nBits) throws IOException {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < nBits; i++) {
            result = result.shiftLeft(1).or(BigInteger.valueOf(readBit()));
//            result = ((result << 1) | readBit());
        }
        return result;
    }


    public void skipUnreadedBits() {
        currentBit = 0;
    }

    public byte[] readUnalignedByteArray(int len) throws IOException {
        byte[] arr = new byte[len];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) read();
        }
        return arr;
    }


    public byte[] readAlignedByteArray(int len) throws IOException {
        skipUnreadedBits();
        byte[] arr = new byte[len];
        if (-1 == byteStream.read(arr)) {
            throw new IOException("reach end of buffer");
        }
        return arr;
    }

    public String print8bits() throws Exception {
        StringBuilder str = new StringBuilder();
        while (currentBit != 0) {
            if ((this.readBit() & 0x01) == 0x01) {
                str.append("1");
            } else {
                str.append("0");
            }
        }
        str.append("\t\t");
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 4; j++) {
                if ((this.readBit() & 0x1) == 0x1) {
                    str.append("1");
                } else {
                    str.append("0");
                }
            }
            str.append("\t");
        }
        System.out.println("Tra-number " + str);
        throw new Exception();
    }

    /**
     * Returns an estimate of the number of bits that can be read (or
     * skipped over) from this input stream without blocking by the next
     * invocation of a method for this input stream.
     * @return number of bits
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
        return (byteStream.available() * 8) + ((currentBit == 0) ? 0 : (8 - currentBit));
    }

    @Override
    public void close() throws IOException {
        byteStream.close();
    }
}