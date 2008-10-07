//<pre>
// Copyright 2001 Ken Perlin

package render;

import java.awt.*;
import java.awt.image.*;

/**
 */

public class ImageBuffer {

   
   public ImageBuffer(int width, int height) {
      init(width, height);
   }

   public ImageBuffer(int[] pixels, int w, int h) {
      pix = pixels;
      width = w;
      height = h;
   }

   public ImageBuffer(Image src, Component component) {
      try {
         MediaTracker mt = new MediaTracker(component);
         mt.addImage(src, 1);
         mt.waitForAll();
         init(src.getWidth(component), src.getHeight(component));
         (new PixelGrabber(src, 0, 0, width, height, pix, 0, width)).grabPixels();
      } catch (InterruptedException e) {
      }
   }
   public int getWidth() {
      return width;
   }
   public int getHeight() {
      return height;
   }
   public int get(int x, int y) {
      x = Math.max(0, Math.min(width - 1, x));
      y = Math.max(0, Math.min(height - 1, y));
      return pix[x + y * width];
   }
   public void set(int x, int y, int rgb) {
      if (x >= 0 && x < width && y >= 0 && y < height)
         pix[x + y * width] = rgb;
   }
   void init(int w, int h) {
      width = w;
      height = h;
      pix = new int[w * h];
   }
   public int pix[] = null, width = 0, height = 0;
}
