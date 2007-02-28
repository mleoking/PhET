//
// Copyright (c) 1997, Jason Marshall.  All Rights Reserved
//
// The author makes no representations or warranties regarding the suitability,
// reliability or stability of this code.  This code is provided AS IS.  The
// author shall not be liable for any damages suffered as a result of using,
// modifying or redistributing this software or any derivitives thereof.
// Permission to use, reproduce, modify and/or (re)distribute this software is
// hereby granted.

package com.oyoaha.swing.plaf.oyoaha;

/**
 * @(#)PNGImageProducer.java	0.88 97/4/14 Jason Marshall
 **/

import java.awt.image.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.zip.*;

// TODO:  Need an ImageFormatException or somesuch..  instead of throwing
// IOExceptions all over the place.  -JDM

/**
 * ImageProducer which produces an Image from a PNG image
 * Note that making this implement the Runnable interface it but one way to
 * handle the problems inherent with asynchronous image production.  This class
 * is provided more as a proof of concept, and sample implementation of the PNG
 * decoder in Java.
 *
 * @version	0.88 14 May 1997
 * @author 	Jason Marshall
 */

public class PNGImageProducer implements ImageProducer, Runnable {
    private int dataWidth;
    private int dataHeight;
    private int width = -1;
    private int height = -1;
    private int sigmask = 0xffff;
    private ColorModel model;
    private Object pixels;
    private int ipixels[];
    private byte bpixels[];
    private Hashtable properties;
    private Vector theConsumers;
    private boolean multipass;
    private boolean complete;
    private boolean error;
    // TODO: make private when inner class created -JDM
    InputStream underlyingStream;
    // TODO: make private when inner class created -JDM
    DataInputStream inputStream;
    private Thread controlThread;
    private boolean infoAvailable = false;
    private boolean completePasses = false;

    //TODO: set from system properties -JDM
    private int updateDelay = 750;

    // Image decoding state variables

    private boolean headerFound = false;
    private int compressionMethod = -1;
    private int depth = -1;
    private int colorType = -1;
    private int filterMethod = -1;
    private int interlaceMethod = -1;
    private int pass;
    private byte palette[];
    private boolean transparency;

    // TODO: make private when innerclass created -JDM
    int chunkLength;
    // TODO: make private when innerclass created -JDM
    int chunkType;
    // TODO: make private when innerclass created -JDM
    boolean needChunkInfo = true;

    static final int CHUNK_bKGD = 0x624B4744;   // "bKGD"
    static final int CHUNK_cHRM = 0x6348524D;   // "cHRM"
    static final int CHUNK_gAMA = 0x67414D41;   // "gAMA"
    static final int CHUNK_hIST = 0x68495354;   // "hIST"
    static final int CHUNK_IDAT = 0x49444154;   // "IDAT"
    static final int CHUNK_IEND = 0x49454E44;   // "IEND"
    static final int CHUNK_IHDR = 0x49484452;   // "IHDR"
    static final int CHUNK_PLTE = 0x504C5445;   // "PLTE"
    static final int CHUNK_pHYs = 0x70485973;   // "pHYs"
    static final int CHUNK_sBIT = 0x73424954;   // "sBIT"
    static final int CHUNK_tEXt = 0x74455874;   // "tEXt"
    static final int CHUNK_tIME = 0x74494D45;   // "tIME"
    static final int CHUNK_tRNS = 0x74524E53;   // "tIME"
    static final int CHUNK_zTXt = 0x7A545874;   // "zTXt"

    static final int startingRow[]  =  { 0, 0, 0, 4, 0, 2, 0, 1 };
    static final int startingCol[]  =  { 0, 0, 4, 0, 2, 0, 1, 0 };
    static final int rowInc[]       =  { 1, 8, 8, 8, 4, 4, 2, 2 };
    static final int colInc[]       =  { 1, 8, 8, 4, 4, 2, 2, 1 };
    static final int blockHeight[]  =  { 1, 8, 8, 4, 4, 2, 2, 1 };
    static final int blockWidth[]   =  { 1, 8, 4, 4, 2, 2, 1, 1 };

    /**
     *
     **/

    public PNGImageProducer(InputStream is) {
        theConsumers = new Vector();
        properties = new Hashtable();
        if (!(is instanceof BufferedInputStream))
            is = new BufferedInputStream(is, 1024);
        underlyingStream = is;
        inputStream = new DataInputStream(underlyingStream);
    }

    public void dispose()
    {
      try
      {
        if(underlyingStream!=null)
        {
          underlyingStream.close();
          underlyingStream = null;
        }

        if(inputStream!=null)
        {
          inputStream.close();
          inputStream = null;
        }
      }
      catch(Exception e)
      {

      }
    }

    public synchronized void addConsumer(ImageConsumer ic) {
        if (theConsumers.contains(ic)) {
            return;
        }

        theConsumers.addElement(ic);

        try {
            initConsumer(ic);
            sendPixels(ic, 0, 0, width, height);
            if ((complete) && (isConsumer(ic))) {
                if (error) {
                    ic.imageComplete(ImageConsumer.IMAGEERROR);
                } else {
                    ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
                }
                removeConsumer(ic);
            }
        } catch (Exception e) {
            if (isConsumer(ic)) {
                ic.imageComplete(ImageConsumer.IMAGEERROR);
            }
        }
    }

    private void blockFill(int rowStart) {
        int counter;
        int dw = dataWidth;
        int pass = this.pass;
        int w = blockWidth[pass];
        int sCol = startingCol[pass];
        int cInc = colInc[pass];
        int wInc = cInc - w;
        int maxW = rowStart + dw - w;
        int len;

        int h = blockHeight[pass];
        int maxH = rowStart + (dw * h);
        int startPos = rowStart + sCol;

        counter = startPos;

        if (colorType == 3) {
            byte bpix[] = bpixels;
            byte pixel;
            len = bpix.length;

            for (; counter <= maxW;) {
                int end = counter + w;
                pixel = bpix[counter++];
                for (; counter < end; counter++) {
                    bpix[counter] = pixel;
                }
                counter += wInc;
            }
            maxW += w;

            if (counter < maxW) {
                for (pixel = bpix[counter++]; counter < maxW; counter++) {
                    bpix[counter] = pixel;
                }
            }

            if (len < maxH)
                maxH = len;

            for (counter = startPos + dw; counter < maxH; counter += dw) {
                System.arraycopy(bpix, startPos, bpix, counter, dw - sCol);
            }
        } else {
            int ipix[] = ipixels;
            int pixel;
            len = ipix.length;

            for (; counter <= maxW;) {
                int end = counter + w;
                pixel = ipix[counter++];
                for (; counter < end; counter++) {
                    ipix[counter] = pixel;
                }
                counter += wInc;
            }

            maxW += w;

            if (counter < maxW) {
                for (pixel = ipix[counter++]; counter < maxW; counter++) {
                    ipix[counter] = pixel;
                }
            }

            if (len < maxH)
                maxH = len;

            for (counter = startPos + dw; counter < maxH; counter += dw) {
                System.arraycopy(ipix, startPos, ipix, counter, dw - sCol);
            }
        }
    }

    private boolean filterRow(byte inbuf[], int pix[], int upix[], int rowFilter, int boff) 
    {
        int rowWidth = pix.length;

        switch (rowFilter) 
        {
          case 0:
            for (int x = 0; x < rowWidth; x++) 
            {
                pix[x] = 0xff & inbuf[x];
            }
            break;
          case 1: 
          {
            int x = 0;
            for ( ; x < boff; x++) 
            {
                pix[x] = 0xff & inbuf[x];
            }
            for ( ; x < rowWidth; x++) 
            {
                pix[x] = 0xff & (inbuf[x] + pix[x - boff]);
            }
            break;
          }
          case 2: 
          {
            if (upix != null)
            {
                for (int x = 0; x < rowWidth; x++) 
                {
                    pix[x] = 0xff & (upix[x] + inbuf[x]);
                }
            } 
            else 
            {
                for (int x = 0; x < rowWidth; x++) 
                {
                    pix[x] = 0xff & inbuf[x];
                }
            }
            break;
          }
          case 3: 
          {
            if (upix != null) 
            {
                int x = 0;
                for ( ; x < boff; x++) 
                {
                    int rval = upix[x];
                    pix[x] = 0xff & ((rval>>1) + inbuf[x]);
                }
                for ( ; x < rowWidth; x++) 
                {
                    int rval = upix[x] + pix[x - boff];
                    pix[x] = 0xff & ((rval>>1) + inbuf[x]);
                }
            } 
            else 
            {
                int x = 0;
                for ( ; x < boff; x++) 
                {
                    pix[x] = 0xff & inbuf[x];
                }
                for ( ; x < rowWidth; x++) 
                {
                    int rval = pix[x - boff];
                    pix[x] = 0xff & ((rval>>1) + inbuf[x]);
                }
            }
            break;
          }
          case 4: {
            if (upix != null) 
            {
                int x = 0;
                for ( ; x < boff; x++) 
                {
                    pix[x] = 0xff & (upix[x] + inbuf[x]);
                }
                for ( ; x < rowWidth; x++) {
                    int a, b, c, p, pa, pb, pc, rval;
                    a = pix[x - boff];
                    b = upix[x];
                    c = upix[x - boff];
                    p = a + b - c;
                    pa = p > a ? p - a : a - p;
                    pb = p > b ? p - b : b - p;
                    pc = p > c ? p - c : c - p;
                    if ((pa <= pb) && (pa <= pc)) 
                    {
                        rval = a;
                    } 
                    else 
                    if (pb <= pc) 
                    {
                        rval = b;
                    } 
                    else 
                    {
                        rval = c;
                    }
                    pix[x] = 0xff & (rval + inbuf[x]);
                }
            } else {
                int x = 0;
                for ( ; x < boff; x++) {
                    pix[x] = 0xff & inbuf[x];
                }
                for ( ; x < rowWidth; x++) {
                    int rval = pix[x - boff];
                    pix[x] = 0xff & (rval + inbuf[x]);
                }
            }
            break;
          }
          default:
            return false;
        }
        return true;
    }

    private void handlebKGD() throws IOException {
        inputStream.skip(chunkLength);
    }

    private void handlecHRM() throws IOException {
        inputStream.skip(chunkLength);
    }

    private void handleChunk() throws IOException {
        if (needChunkInfo) {
            chunkLength = inputStream.readInt();
            chunkType = inputStream.readInt();
            needChunkInfo = false;
        }

        // Guarantee that chunks can't overread their bounds
        /*inputStream = new DataInputStream(
                        new MeteredInputStream(underlyingStream,
                                                chunkLength));
        */
        switch (chunkType) {
          case CHUNK_bKGD:
            handlebKGD();
            break;
          case CHUNK_cHRM:
            handlecHRM();
            break;
          case CHUNK_gAMA:
            handlegAMA();
            break;
          case CHUNK_hIST:
            handlehIST();
            break;
          case CHUNK_IDAT:
            handleIDAT();
            break;
          case CHUNK_IEND:
            handleIEND();
            break;
          case CHUNK_IHDR:
            handleIHDR();
            break;
          case CHUNK_pHYs:
            handlepHYs();
            break;
          case CHUNK_PLTE:
            handlePLTE();
            break;
          case CHUNK_sBIT:
            handlesBIT();
            break;
          case CHUNK_tEXt:
            handletEXt();
            break;
          case CHUNK_tIME:
            handletIME();
            break;
          case CHUNK_tRNS:
            handletRNS();
            break;
          case CHUNK_zTXt:
            handlezTXt();
            break;
          default:
            System.err.println("unrecognized chunk type " +
                                Integer.toHexString(chunkType) + ". skipping");
            inputStream.skip(chunkLength);
        }
        //inputStream = new DataInputStream(underlyingStream);
        int crc = inputStream.readInt();
        needChunkInfo = true;
    }

    private void handlegAMA() throws IOException {
        inputStream.skip(chunkLength);
    }

    private void handlehIST() throws IOException {
        inputStream.skip(chunkLength);
    }

    /**
     * Handle the image data chunks.  Note that sending info to ImageConsumers
     * is delayed until the first IDAT chunk is seen.  This allows for any
     * changes in ancilliary chunks that may alter the overall properties of
     * the image, such as aspect ratio altering the image dimensions.  We
     * assume that all ancilliary chunks which have these effects will appear
     * before the first IDAT chunk, and once seen, image properties are set in
     * stone.
     **/

    private void handleIDAT() throws IOException {
        if (!infoAvailable) {
            if (width == -1)
                width = dataWidth;
            if (height == -1)
                height = dataHeight;
            setColorModel();
            if (interlaceMethod != 0)
                multipass = true;

            Vector consumers;
            synchronized (this) {
                infoAvailable = true;
                consumers = (Vector) theConsumers.clone();
            }
            for (int i = 0; i < consumers.size(); i++) {
                initConsumer((ImageConsumer) consumers.elementAt(i));
            }
        }

        readImageData();

        sendPixels(0, 0, width, height);
    }

    private void handleIEND() throws IOException {
        complete = true;
    }

    private void handleIHDR() throws IOException {
        if (headerFound)
            throw new IOException("Extraneous IHDR chunk encountered.");
        if (chunkLength != 13)
            throw new IOException("IHDR chunk length wrong: " + chunkLength);
        dataWidth = inputStream.readInt();
        dataHeight = inputStream.readInt();
        depth = inputStream.read();
        colorType = inputStream.read();
        compressionMethod = inputStream.read();
        filterMethod = inputStream.read();
        interlaceMethod = inputStream.read();
    }

    private void handlePLTE() throws IOException {
        if (colorType == 3) {
            palette = new byte[chunkLength];
            inputStream.readFully(palette);
        } else {
            // Ignore suggested palette
            inputStream.skip(chunkLength);
        }
    }

    private void handlepHYs() throws IOException {
        /*  Not yet implemented -JDM
        int w = inputStream.readInt();
        int h = inputStream.readInt();
        inputStream.read();
        width = dataWidth * w / h;
        height = dataHeight * h / w;
        */
        inputStream.skip(chunkLength);
    }

    private void handlesBIT() throws IOException {
        inputStream.skip(chunkLength);
    }

    private void handletEXt() throws IOException {
        inputStream.skip(chunkLength);
    }

    private void handletIME() throws IOException {
        if (chunkLength != 7)
            System.err.println("tIME chunk length incorrect: " + chunkLength);
        inputStream.skip(chunkLength);
    }

    private void handletRNS() throws IOException {
        int chunkLen = chunkLength;
		if (palette == null)
			throw new IOException("tRNS chunk encountered before pLTE");

		int len = palette.length;

        switch (colorType) {
          case 3: {
            transparency = true;
            int transLength = len/3;
            byte[] trans = new byte[transLength];
            inputStream.readFully(trans, 0, chunkLength);

            byte b = (byte) 0xff;

            for (int i = len; i < transLength; i++) {
                trans[i] = b;
            }

            byte[] newPalette = new byte[len + transLength];

            for (int i = newPalette.length; i > 0;) {
                newPalette[--i] = trans[--transLength];
                newPalette[--i] = palette[--len];
                newPalette[--i] = palette[--len];
                newPalette[--i] = palette[--len];
            }

            palette = newPalette;
            break;
          }
          default:
            inputStream.skip(chunkLength);
        }
    }

    private void handlezTXt() throws IOException {
        inputStream.skip(chunkLength);
    }

    private void handleSignature() throws IOException {
        if ((inputStream.read() != 137) ||
            (inputStream.read() != 80) ||
            (inputStream.read() != 78) ||
            (inputStream.read() != 71) ||
            (inputStream.read() != 13) ||
            (inputStream.read() != 10) ||
            (inputStream.read() != 26) ||
            (inputStream.read() != 10)) {
            throw new IOException("Not a PNG File");
        }
    }

    private void initConsumer(ImageConsumer ic) {
        if (infoAvailable) {
            if (isConsumer(ic)) {
                ic.setDimensions(width, height);
            }
            if (isConsumer(ic)) {
                ic.setProperties(properties);
            }
            if (isConsumer(ic)) {
                ic.setColorModel(model);
            }
            if (isConsumer(ic)) {
                ic.setHints(multipass ?
                                        (ImageConsumer.TOPDOWNLEFTRIGHT |
                                        ImageConsumer.COMPLETESCANLINES)
                                    : (ImageConsumer.TOPDOWNLEFTRIGHT |
                                        ImageConsumer.COMPLETESCANLINES |
                                        ImageConsumer.SINGLEPASS |
                                        ImageConsumer.SINGLEFRAME));
            }
        }
    }

    private void insertGreyPixels(int pix[], int offset, int samples) {
        int p = pix[0];
        int ipix[] = ipixels;
        int cInc = colInc[pass];
        int rs = 0;

        switch (colorType) {
          case 0: {
            switch (depth) {
              case 1: {
                for (int j = 0; j < samples; j++, offset += cInc) {
                    if (rs != 0) {
                        rs--;
                    } else {
                        rs = 7;
                        p = pix[j>>3];
                    }
                    ipix[offset] = (p>>rs) & 0x1;
                }
                break;
              }
              case 2: {
                for (int j = 0; j < samples; j++, offset += cInc) {
                    if (rs != 0) {
                        rs -= 2;
                    } else {
                        rs = 6;
                        p = pix[j>>2];
                    }
                    ipix[offset] = (p>>rs) & 0x3;
                }
                break;
              }
              case 4: {
                for (int j = 0; j < samples; j++, offset += cInc) {
                    if (rs != 0) {
                        rs = 0;
                    } else {
                        rs = 4;
                        p = pix[j>>1];
                    }
                    ipix[offset] = (p>>rs) & 0xf;
                }
                break;
              }
              case 8: {
                for (int j = 0; j < samples; offset += cInc) {
                    ipix[offset] = (byte) pix[j++];
                }
                break;
              }
              case 16: {
                samples = samples<<1;
                for (int j = 0; j < samples; j += 2, offset += cInc) {
                    ipix[offset] = pix[j];
                }
                break;
              }
              default:
                break;
            }
            break;
          }
          case 4: {
            if (depth == 8) {
                for (int j = 0; j < samples; offset += cInc) {
                    ipix[offset] = (pix[j++]<<8) | pix[j++];
                }
            } else {
                samples = samples<<1;
                for (int j = 0; j < samples; j += 2, offset += cInc) {
                    ipix[offset] = (pix[j]<<8) | pix[j+=2];
                }
            }
            break;
          }
        }
    }

    private void insertPalettedPixels(int pix[], int offset, int samples) 
    {
        int rs = 0;
        int p = pix[0];
        byte bpix[] = bpixels;
        int cInc = colInc[pass];

        switch (depth) 
        {
          case 1: 
          {
            for (int j = 0; j < samples; j++, offset += cInc) 
            {
                if (rs != 0) 
                {
                    rs--;
                } 
                else 
                {
                    rs = 7;
                    p = pix[j>>3];
    
                }
                
                bpix[offset] = (byte) ((p>>rs) & 0x1);
            }
            break;
          }
          case 2: 
          {
            for (int j = 0; j < samples; j++, offset += cInc) 
            {
                if (rs != 0) 
                {
                    rs -= 2;
                } 
                else 
                {
                    rs = 6;
                    p = pix[j>>2];
                }
                
                bpix[offset] = (byte) ((p>>rs) & 0x3);
            }
            break;
          }
          case 4: 
          {
            for (int j = 0; j < samples; j++, offset += cInc) 
            {
                if (rs != 0) 
                {
                    rs = 0;
                } 
                else 
                {
                    rs = 4;
                    p = pix[j>>1];      
                }
                
                bpix[offset] = (byte) ((p>>rs) & 0xf);
            }
            break;
          }
          case 8: 
          {
            for (int j = 0; j < samples; j++, offset += cInc) 
            {
                bpix[offset] = (byte)pix[j];
            }
            break;
          }
          default:
            break;
        }
    }

    private void insertPixels(int pix[], int offset, int samples) {
        switch (colorType) {
          case 0:
          case 4: {
            insertGreyPixels(pix, offset, samples);
            break;
          }
          case 2: {
            int j = 0;
            int ipix[] = ipixels;
            int cInc = colInc[pass];
            if (depth == 8) {
                for (j = 0; j < samples; offset += cInc) {
                    ipix[offset] = (pix[j++]<<16) | (pix[j++]<<8) | pix[j++];
                }
            } else {
                samples = samples<<1;
                for (j = 0; j < samples; j += 2, offset += cInc) {
                    ipix[offset] = (pix[j]<<16) | (pix[j+=2]<<8) | pix[j+=2];
                }
            }
            break;
          }
          case 3: {
            insertPalettedPixels(pix, offset, samples);
            break;
          }
          case 6: {
            int j = 0;
            int ipix[] = ipixels;
            int cInc = colInc[pass];
            if (depth == 8) {
                for (j = 0; j < samples; offset += cInc) {
                    ipix[offset] = (pix[j++]<<16) | (pix[j++]<<8) | pix[j++] |
                                    (pix[j++]<<24);
                }
            } else {
                samples = samples<<1;
                for (j = 0; j < samples; j += 2, offset += cInc) {
                    ipix[offset] = (pix[j]<<16) | (pix[j+=2]<<8) | pix[j+=2] |
                                    (pix[j+=2]<<24);
                }
            }
            break;
          }
          default:
            break;
        }
    }

    /**
     * This method determines if a given ImageConsumer object
     * is currently registered with this ImageProducer as one
     * of its consumers.
     * @see ImageConsumer
     */

    public synchronized boolean isConsumer(ImageConsumer ic) {
        return theConsumers.contains(ic);
    }

    /**
     * Read Image data in off of a compression stream
     **/

    private void readImageData() throws IOException {
        long time = System.currentTimeMillis();

        InputStream dataStream = new SequenceInputStream(
                                        new IDATEnumeration(this));

        DataInputStream dis = new DataInputStream(
                                new BufferedInputStream(
                                    new InflaterInputStream(dataStream,
                                                            new Inflater())));

        int bps, filterOffset;

        switch (colorType) {
          case 0:
          case 3:
            bps = depth;
            break;
          case 2:
            bps = 3 * depth;
            break;
          case 4:
            bps = depth<<1;
            break;
          case 6:
            bps = depth<<2;
            break;
          default:
            // should never happen
            throw new IOException("Unknown color type encountered.");
        }

        filterOffset = (bps + 7)>>3;

        for (pass = (multipass ? 1 : 0); pass < 8; pass++) {
            int pass = this.pass;
            int rInc = rowInc[pass];
            int cInc = colInc[pass];
            int sCol = startingCol[pass];

            int val = ((dataWidth - sCol + cInc - 1) / cInc);

            int samples = val * filterOffset;
            int rowSize = (val * bps)>>3;

            int sRow = startingRow[pass];

            if ((dataHeight <= sRow) || (rowSize == 0))
                continue;

            int sInc = rInc * dataWidth;

            byte inbuf[] = new byte[rowSize];
            int pix[] = new int[rowSize];
            int upix[] = null;
            int temp[] = new int[rowSize];

            // next Y value and number of rows to report to sendPixels
            int nextY = sRow;
            int rows = 0;
            int rowStart = sRow * dataWidth;

            for (int y = sRow; y < dataHeight; y += rInc, rowStart += sInc) {
                rows += rInc;

                int rowFilter = dis.read();
                dis.readFully(inbuf);

                if (!filterRow(inbuf, pix, upix, rowFilter, filterOffset)) {
                    throw new IOException("Unknown filter type: " + rowFilter);
                }

                insertPixels(pix, rowStart + sCol, samples);

                if (multipass && (pass < 6)) {
                    blockFill(rowStart);
                }

                upix = pix;
                pix = temp;
                temp = upix;

                if (!completePasses) {
                    long newTime = System.currentTimeMillis();
                    if ((newTime - time) > updateDelay) {
                        sendPixels(0, nextY, width, rows);
                        rows = 0;
                        nextY = y + rInc;
                        time = newTime;
                    }
                }
                Thread.yield();
            }
            if (!multipass)
                break;
            if (completePasses || (rows > 0)) {
                sendPixels(0, 0, width, height);
                time = System.currentTimeMillis();
            }
        }
        while(dis.read() != -1)
            System.err.println("Leftover data encountered.");
    }

    /**
    * Remove an ImageConsumer from the list of consumers interested in
    * data for this image.
    * @see ImageConsumer
    */

    public synchronized void removeConsumer(ImageConsumer ic) {
        theConsumers.removeElement(ic);
    }

    /**
    * Requests that a given ImageConsumer have the image data delivered
    * one more time in top-down, left-right order.
    * @see ImageConsumer
    */

    public void requestTopDownLeftRightResend(ImageConsumer ic) {
        // Ignored.  The data is either single frame and already in TDLR
        // format or it is multi-frame and TDLR resends aren't critical.
    }

    /**
     * Primary processing of Image data.
     **/

    public void run() {
        try {
            // Verify signature
            handleSignature();
            while (!complete && !error) {
                handleChunk();
            }
        } catch (Exception e) {
            System.err.println("PNGImageProducer: " + e);
            e.printStackTrace(System.err);
            error = true;
        }
        synchronized (this) {
            for (int c = 0; c < theConsumers.size(); c++) {
                ImageConsumer ic = (ImageConsumer) theConsumers.elementAt(c);
                if (error) {
                    ic.imageComplete(ImageConsumer.IMAGEERROR);
                } else {
                    ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
                }
            }
        }
    }

    private synchronized void sendPixels(int x, int y, int w, int h) {
        int off = dataWidth * y + x;
        Enumeration enum = theConsumers.elements();
        while (enum.hasMoreElements()) {
            ImageConsumer ic = (ImageConsumer) enum.nextElement();
            if ((pixels != null) && (isConsumer(ic))) {
                if (pixels instanceof byte[]) {
                    ic.setPixels(x, y, w, h, model, bpixels, off, dataWidth);
                } else {
                    ic.setPixels(x, y, w, h, model, ipixels, off, dataWidth);
                }
            }
        }
    }

    private void sendPixels(ImageConsumer ic, int x, int y, int w, int h) {
        int off = dataWidth * y + x;
        if ((pixels != null) && (isConsumer(ic))) {
            if (pixels instanceof byte[]) {
                ic.setPixels(x, y, w, h, model, bpixels, off, dataWidth);
            } else {
                ic.setPixels(x, y, w, h, model, ipixels, off, dataWidth);
            }
        }
    }

    private void setColorModel() throws IOException {
        int mask = 0;
        switch (depth) {
          case 1:
            mask = 0x1;
            break;
          case 2:
            mask = 0x3;
            break;
          case 4:
            mask = 0xf;
            break;
          case 8:
          case 16:
            mask = 0xff;
            break;
        }

        int count = width * height;

        switch (colorType) {
          case 3:
            if (palette == null)
                throw new IOException("No palette located");
            bpixels = new byte[count];
            pixels = bpixels;
            if (transparency) {
                model = new IndexColorModel(depth, palette.length/4, palette,
                                            0, true);
            } else {
                model = new IndexColorModel(depth, palette.length/3, palette,
                                            0, false);
            }
            break;
          case 0:
            ipixels = new int[count];
            pixels = ipixels;
            if (depth < 8) {
                model = new DirectColorModel(depth, mask, mask, mask);
            } else {
                model = new DirectColorModel(8, mask, mask, mask);
            }
            break;
          case 2:
            ipixels = new int[count];
            pixels = ipixels;
            model = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
            break;
          case 4:
            int smask;
            if (depth < 8) {
                smask = mask << depth;
                model = new DirectColorModel(depth * 2, smask, smask, smask,
                                                mask);
            } else {
                smask = mask << 8;
                model = new DirectColorModel(16, smask, smask, smask, mask);
            }
            ipixels = new int[count];
            pixels = ipixels;
            break;
          case 6:
            ipixels = new int[count];
            pixels = ipixels;
            model = ColorModel.getRGBdefault();
            break;
          default:
            throw new IOException("Image has unknown color type");
        }
    }

    private void start() {
        if (controlThread == null) {
            synchronized (this) {
                controlThread = new Thread(this);
                try {
                    controlThread.setPriority(Thread.NORM_PRIORITY - 2);
                } catch (Exception e) {}
                controlThread.start();
            }
        }
    }

    /**
    * Adds an ImageConsumer to the list of consumers interested in
    * data for this image, and immediately start delivery of the
    * image data through the ImageConsumer interface.
    * @see ImageConsumer
    */

    public void startProduction(ImageConsumer ic) {
        addConsumer(ic);
        start();
    }
}


/**
 * Support class, used to eat the IDAT headers dividing up the deflated stream
 **/

class IDATEnumeration implements Enumeration {
    InputStream underlyingStream;
    PNGImageProducer owner;
    boolean firstStream = true;

    public IDATEnumeration(PNGImageProducer owner) {
        this.owner = owner;
        this.underlyingStream = owner.underlyingStream;
    }

    public Object nextElement() {
        firstStream = false;
        return new MeteredInputStream(underlyingStream, owner.chunkLength);
    }

    public boolean hasMoreElements() {
        DataInputStream dis = new DataInputStream(underlyingStream);
        if (!firstStream) {
            try {
                int crc = dis.readInt();
                owner.needChunkInfo = false;
                owner.chunkLength = dis.readInt();
                owner.chunkType = dis.readInt();
            } catch (IOException ioe) {
                return false;
            }
        }
        if (owner.chunkType == PNGImageProducer.CHUNK_IDAT) {
            return true;
        }
        return false;
    }
}
