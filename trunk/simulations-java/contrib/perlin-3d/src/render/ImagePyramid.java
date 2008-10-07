// <pre>
// Copyright 2001 Ken Perlin

package render;

/**
 * Multi Image Pyramid (MIP) processing
*/

public class ImagePyramid {
   private String notice = "Copyright 2001 Ken Perlin. All rights reserved.";

   /** Creates a multi-image maps from an image buffer
    * @param base the original image 
    * @see ImageBuffer
    */
   public ImagePyramid(ImageBuffer base) {
      pyramid[0] = new ImageBuffer(base.width + 1, base.height + 1);
      for (int y = 0; y < base.height; y++)
         for (int x = 0; x < base.width; x++)
            pyramid[0].pix[x + y * pyramid[0].width] = 0x00ffffff & base.pix[x + y * base.width];

      for (int level = 1; level < pyramid.length; level++) {

         ImageBuffer parent = pyramid[level - 1];
         int w = parent.getWidth() - 1;
         int h = parent.getHeight() - 1;
         if (w <= 1 || h <= 1)
            break;

         ImageBuffer tmp = new ImageBuffer(w, h / 2 + 1);
         for (int x = 0; x <= w; x++)
            for (int y = 0; y < h; y += 2) {
               int A = parent.get(x, y - 1), B = parent.get(x, y), C = parent.get(x, y + 1), D = parent.get(x, y + 2);
               tmp.set(x, y >> 1, (unpack(A, 0) + 3 * (unpack(B, 0) + unpack(C, 0)) + unpack(D, 0) >> 3) << 16 | (unpack(A, 1) + 3 * (unpack(B, 1) + unpack(C, 1)) + unpack(D, 1) >> 3) << 8 | (unpack(A, 2) + 3 * (unpack(B, 2) + unpack(C, 2)) + unpack(D, 2) >> 3));
            }

         pyramid[level] = new ImageBuffer(w / 2 + 1, h / 2 + 1);
         for (int x = 0; x <= w; x += 2)
            for (int y = 0; y <= h / 2; y++) {
               int A = tmp.get(x - 1, y), B = tmp.get(x, y), C = tmp.get(x + 1, y), D = tmp.get(x + 2, y);
               pyramid[level].set(x >> 1, y, (unpack(A, 0) + 3 * (unpack(B, 0) + unpack(C, 0)) + unpack(D, 0) >> 3) << 16 | (unpack(A, 1) + 3 * (unpack(B, 1) + unpack(C, 1)) + unpack(D, 1) >> 3) << 8 | (unpack(A, 2) + 3 * (unpack(B, 2) + unpack(C, 2)) + unpack(D, 2) >> 3));
            }
      }
   }

   /** Returns a sample at a particular fractional location and size
    * @param u the horizontal component
    * @param v the vertical component
    * @param s the size of the pixel
   */
   public int get(double u, double v, double s) {
      u = Math.max(0, Math.min(1, u));
      v = Math.max(0, Math.min(1, v));
      if (s <= 0) {
         ImageBuffer p0 = pyramid[0];
         return p0.get((int) (u * (p0.getWidth() - 1)), (int) (v * (p0.getHeight() - 1))) | 0xff000000;
      }

      int level = 0, targetWidth = (int) (2 / s);
      for (; pyramid[level + 2] != null; level++)
         if (pyramid[level].getWidth() - 1 <= targetWidth)
            break;

      ImageBuffer p0 = pyramid[level], p1 = pyramid[level + 1];
      int w0 = p0.getWidth(), h0 = p0.getHeight(), w1 = p1.getWidth(), h1 = p1.getHeight();
      double X0 = (w0 - 1) * u - .5, Y0 = (h0 - 1) * v - .5, X1 = (w1 - 1) * u - .5, Y1 = (h1 - 1) * v - .5;
      int x0 = (int) X0, dx0 = f2i(X0 - x0), y0 = (int) Y0, dy0 = f2i(Y0 - y0), i0 = x0 + w0 * y0, x1 = (int) X1, dx1 = f2i(X1 - x1), y1 = (int) Y1, dy1 = f2i(Y1 - y1), i1 = x1 + w1 * y1;

      return lerpRGB(f2i(w0 * s - 1), lerpRGB(dy0, lerpRGB(dx0, p0.pix[i0], p0.pix[i0 + 1]), lerpRGB(dx0, p0.pix[i0 + w0], p0.pix[i0 + w0 + 1])), lerpRGB(dy1, lerpRGB(dx1, p1.pix[i1], p1.pix[i1 + 1]), lerpRGB(dx1, p1.pix[i1 + w1], p1.pix[i1 + w1 + 1]))) | 0xff000000;
   }
   private int f2i(double t) {
      return Math.max(0, Math.min(255, (int) (256 * t)));
   }
   private int lerpRGB(int t, int a, int b) {
      return lerp(t, a & 0xff0000, b & 0xff0000) & 0xff0000 | lerp(t, a & 0x00ff00, b & 0x00ff00) & 0x00ff00 | lerp(t, a & 0x0000ff, b & 0x0000ff);
   }
   private int lerp(int t, int a, int b) {
      return a + (t * (b - a) >> 8);
   }

   /** returns the width of an image at the given level 
    * @param level the level within the pyramid
   */
   public int getWidth(int level) {
      return pyramid[level].getWidth();
   }

   /** returns the height of an image at the given level 
    * @param level the level within the pyramid
   */
   public int getHeight(int level) {
      return pyramid[level].getHeight();
   }

   /** returns a pixel of an image at the given location in the specified level 
    * @param level the level within the pyramid
    * @param x, y location    
   */
   public int get(int level, int x, int y) {
      return pyramid[level].get(x, y);
   }

   private int unpack(int packed, int i) {
      switch (i) {
         case 0 :
            return (packed >> 16) & 255;
         case 1 :
            return (packed >> 8) & 255;
         default :
            return (packed) & 255;
      }
   }

   private ImageBuffer pyramid[] = new ImageBuffer[12];
}
