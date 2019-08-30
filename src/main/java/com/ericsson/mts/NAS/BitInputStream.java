package com.ericsson.mts.NAS;/*
 * Copyright 2019 Ericsson, https://www.ericsson.com/en
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.ericsson.mts.NAS.exceptions.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Read bits in middle endian
 */

public class BitInputStream extends InputStream {
    private final InputStream byteStream;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private int currentBit = 0;
    private int currentByte;

    public BitInputStream(InputStream byteStream) throws IOException {
        this.byteStream = byteStream;
        currentByte = byteStream.read();
    }

    /**
     * Peek current byte
     * @return current byte
     * @throws IOException When bitInputStream isn't at the beginning of an octet
     */
    public synchronized int peekByte() throws IOException {
        if(currentBit != 0){
            throw new IOException("Can't peek byte");
        }
        return currentByte;
    }

    public synchronized int read() throws IOException {
        throw new IOException("Don't use read()");
    }

    /**
     * Read one bit according to middle endian
     * @return 0 or 1
     * @throws IOException if an I/O error occurs.
     */
    public synchronized int readBit() throws IOException {
        int result = (currentByte >> currentBit) & 0x01;
        currentBit++;
        if (currentBit > 7) {
            currentBit = 0;
        }
        if (currentBit == 0) {
            currentByte = byteStream.read();
        }
        return result;
    }

    /**
     * Read nBits bits according to middle endian
     * @param nBits number of bits to be read (max 32)
     * @return nBits into an int
     * @throws IOException if an I/O error occurs.
     * @throws DecodingException if nBits {@literal >} 32
     */
    public synchronized int readBits(int nBits) throws IOException, DecodingException {
        int result = 0, bytePow = 0;
        if (nBits > 32) {
            throw new DecodingException("nBits (" + nBits + ") sup to 32");
        }

        while (nBits != 0) {
            if (bytePow == 8) {
                bytePow = 0;
                result = result << 8;
            }
            result |= (readBit() << bytePow);
            nBits--;
            bytePow++;
        }
        return result;
    }

    /**
     * Read nBits bits according to middle endian
     * @param nBits number of bits to be read
     * @return nBits into an bigInteger
     * @throws IOException if an I/O error occurs.
     */
    public synchronized BigInteger bigReadBits(int nBits) throws IOException {
        BigInteger result = BigInteger.ZERO;
        int pow = 0;
        for (int i = 0; i < nBits; i++) {
            result = result.or(BigInteger.valueOf(readBit() << pow));
            pow++;
        }
        return result;
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
        return (super.available() * 8) + ((currentBit == 0) ? 0 : (8 - currentBit));
    }

    /**
     * Switch first four bits of the current byte with the last four bits
     * @throws IOException if an I/O error occurs.
     */
    public synchronized void switchByte() throws IOException {
        if(currentBit != 0){
            throw new IOException("Can't switch byte");
        }
        int firstBits = currentByte & 0x0f;
        currentByte = ((currentByte >> 4) & 0x0f) | ((firstBits << 4) & 0xf0);
    }

    /**
     * Close current bitInputStream
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        byteStream.close();
    }
}
