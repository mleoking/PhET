/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-08-17 20:56:43 -0700 (Tue, 17 Aug 2010) $
 * $Revision: 14027 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.g3d;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;

/**
 *<p>
 * Specifies the API to an underlying int[] buffer of ARGB values that
 * can be converted into an Image object and a short[] for z-buffer depth.
 *</p>
 *
 * @author Miguel, miguel@jmol.org
 */ 
class Platform3D {

  int windowWidth, windowHeight, windowSize;
  int bufferWidth, bufferHeight, bufferSize, bufferSizeT;

  Image imagePixelBuffer;
  int[] pBuffer, pBufferT;
  int[] zBuffer, zBufferT;

  int widthOffscreen, heightOffscreen;
  Image imageOffscreen;
  Graphics gOffscreen;

  final static boolean forcePlatformAWT = false;
  final static boolean desireClearingThread = false;
  boolean useClearingThread = true;

  ClearingThread clearingThread;

  static Platform3D createInstance(Component awtComponent) {
    if (awtComponent == null)
      return null;
    Platform3D platform = new Platform3D();
    platform.initialize(desireClearingThread);
    platform.graphicsOffscreen = platform.allocateOffscreenImage(1, 1).getGraphics();
    return platform;
  }

  final void initialize(boolean useClearingThread) {
    this.useClearingThread = useClearingThread;
    if (useClearingThread) {
      //Logger.debug("using ClearingThread");
      clearingThread = new ClearingThread();
      clearingThread.start();
    }
  }

  void allocateTBuffers(boolean antialiasTranslucent) {
    bufferSizeT = (antialiasTranslucent ? bufferSize : windowSize);
    zBufferT = new int[bufferSizeT];
    pBufferT = new int[bufferSizeT];    
  }
  
  void allocateBuffers(int width, int height, boolean antialias) {
    windowWidth = width;
    windowHeight = height;
    windowSize = width * height;
    if (antialias) {
      bufferWidth = width * 2;
      bufferHeight = height * 2;
    } else {
      bufferWidth = width;
      bufferHeight = height;
    }
    
    bufferSize = bufferWidth * bufferHeight;
    zBuffer = new int[bufferSize];
    pBuffer = new int[bufferSize];
    // original thought was that there is
    // no need for any antialiazing on a translucent buffer
    // but that's simply not true.
    // bufferSizeT = windowSize;
    imagePixelBuffer = allocateImage();
    /*
    Logger.debug("  width:" + width + " bufferWidth=" + bufferWidth +
                       "\nheight:" + height + " bufferHeight=" + bufferHeight);
    */
  }
  
  void releaseBuffers() {
    windowWidth = windowHeight = bufferWidth = bufferHeight = bufferSize = -1;
    if (imagePixelBuffer != null) {
      imagePixelBuffer.flush();
      imagePixelBuffer = null;
    }
    pBuffer = null;
    zBuffer = null;
    pBufferT = null;
    zBufferT = null;
  }

  boolean hasContent() {
    for (int i = bufferSize; --i >= 0; )
      if (zBuffer[i] != Integer.MAX_VALUE)
        return true;
    return false;
  }

  void clearScreenBuffer() {
    for (int i = bufferSize; --i >= 0; ) {
      zBuffer[i] = Integer.MAX_VALUE;
      pBuffer[i] = 0;
    }
  }

  void setBackgroundColor(int bgColor) {
    if (pBuffer == null)
      return;
    for (int i = bufferSize; --i >= 0; )
      if (pBuffer[i] == 0)
        pBuffer[i] = bgColor;
  }
  
  void clearTBuffer() {
    for (int i = bufferSizeT; --i >= 0; ) {
      zBufferT[i] = Integer.MAX_VALUE;
      pBufferT[i] = 0;
    }
  }
  
  final void obtainScreenBuffer() {
    if (useClearingThread) {
      clearingThread.obtainBufferForClient();
    } else {
      clearScreenBuffer();
    }
  }

  final void clearScreenBufferThreaded() {
    if (useClearingThread)
      clearingThread.releaseBufferForClearing();
  }
  
  void notifyEndOfRendering() {
  }

  Graphics graphicsOffscreen;
  
  boolean checkOffscreenSize(int width, int height) {
    if (width <= widthOffscreen && height <= heightOffscreen)
      return true;
    if (imageOffscreen != null) {
        gOffscreen.dispose();
        imageOffscreen.flush();
    }
    if (width > widthOffscreen)
      widthOffscreen = (width + 63) & ~63;
    if (height > heightOffscreen)
      heightOffscreen = (height + 15) & ~15;
    imageOffscreen = allocateOffscreenImage(widthOffscreen, heightOffscreen);
    gOffscreen = getGraphics(imageOffscreen);
    return false;
  }

  class ClearingThread extends Thread {


    boolean bufferHasBeenCleared = false;
    boolean clientHasBuffer = false;

    /**
     * 
     * @param argbBackground
     */
    synchronized void notifyBackgroundChange(int argbBackground) {
      //Logger.debug("notifyBackgroundChange");
      bufferHasBeenCleared = false;
      notify();
      // for now do nothing
    }

    synchronized void obtainBufferForClient() {
      //Logger.debug("obtainBufferForClient()");
      while (! bufferHasBeenCleared)
        try { wait(); } catch (InterruptedException ie) {}
      clientHasBuffer = true;
    }

    synchronized void releaseBufferForClearing() {
      //Logger.debug("releaseBufferForClearing()");
      clientHasBuffer = false;
      bufferHasBeenCleared = false;
      notify();
    }

    synchronized void waitForClientRelease() {
      //Logger.debug("waitForClientRelease()");
      while (clientHasBuffer || bufferHasBeenCleared)
        try { wait(); } catch (InterruptedException ie) {}
    }

    synchronized void notifyBufferReady() {
      //Logger.debug("notifyBufferReady()");
      bufferHasBeenCleared = true;
      notify();
    }

    @Override
    public void run() {
      /*
      Logger.debug("running clearing thread:" +
                         Thread.currentThread().getPriority());
      */
      while (true) {
        waitForClientRelease();
        clearScreenBuffer();
        notifyBufferReady();
      }
    }
  }

  ////////swing
  
  private final static DirectColorModel rgbColorModel =
    new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF, 0x00000000);

  private final static int[] sampleModelBitMasks =
  { 0x00FF0000, 0x0000FF00, 0x000000FF };
/*
  private final static DirectColorModel rgbColorModelT =
    new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);

  private final static int[] sampleModelBitMasksT =
  { 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000 };
*/
  Image allocateImage() {
    //backgroundTransparent not working with antialiasDisplay. I have no idea why. BH 9/24/08
/* DEAD CODE   if (false && backgroundTransparent)
      return new BufferedImage(
          rgbColorModelT,
          Raster.createWritableRaster(
              new SinglePixelPackedSampleModel(
                  DataBuffer.TYPE_INT,
                  windowWidth,
                  windowHeight,
                  sampleModelBitMasksT), 
              new DataBufferInt(pBuffer, windowSize),
              null),
          false, 
          null);
*/
    return new BufferedImage(
        rgbColorModel,
        Raster.createWritableRaster(
            new SinglePixelPackedSampleModel(
                DataBuffer.TYPE_INT,
                windowWidth,
                windowHeight,
                sampleModelBitMasks), 
            new DataBufferInt(pBuffer, windowSize),
            null),
        false, 
        null);
  }

  private static boolean backgroundTransparent = false;
  
  void setBackgroundTransparent(boolean tf) {
    backgroundTransparent = tf;
  }

  Image allocateOffscreenImage(int width, int height) {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }

  Graphics getGraphics(Image image) {
    return getStaticGraphics(image);
  }
  
  static Graphics getStaticGraphics(Image image) {
    Graphics2D g2d = ((BufferedImage) image).createGraphics();
    if (backgroundTransparent) {
      // what here?
    }
    // miguel 20041122
    // we need to turn off text antialiasing on OSX when
    // running in a web browser
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                         RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    // I don't know if we need these or not, but cannot hurt to have them
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_OFF);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                         RenderingHints.VALUE_RENDER_SPEED);
    return g2d;
  }

  
}
