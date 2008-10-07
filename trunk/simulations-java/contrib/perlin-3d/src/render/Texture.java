//<pre>

package render;
import java.awt.*;

/** Texture handling
 */

public final class Texture {

   private int[] texels;
   private int height, width;
   private String name;
   private ImagePyramid pyramid = null;
   public static boolean useMIP = true;
   private boolean mip;

   /** Constructor using an array of pixels
    * @param pix array of pixel colors using packed ints
    * @param h height of the image
    * @param w width of the image
    * @param name texture name
    * @param mip flag determining whether to use the multi image pyramid (MIP). 
    */
   public Texture(int[] pix, int h, int w, String name, boolean mip) {
      texels = pix;
      height = h;
      width = w;
      pyramid = new ImagePyramid(new ImageBuffer(pix, w, h));
      this.mip = mip;
   }

   /** Constructor using an image object
    * @param src Image object
    * @param component the component that the image is associated with
    * @param name texture name
    * @param mip flag determining whether to use the multi image pyramid (MIP). 
    */
   public Texture(Image src, String name, Component component, boolean mip) {
      ImageBuffer buffer = new ImageBuffer(src, component);
      texels = buffer.pix;
      height = buffer.getHeight();
      width = buffer.getWidth();
      pyramid = new ImagePyramid(buffer);
      this.mip = mip;
   }

	
   /** Returns the integer value from the texture at [u, v ].
    * @param u horizontal component
    * @param v vertical component
    * @param dx size of the pixel ( for use with mip )
    * @param dx size of the pixel ( for use with mip )
    * @param mult the scale factor for dx and dy ( for use with mip )
    */
   public final int getTexel(double u, double v, int dx, int dy, int mult) {
      if (u >= 0 && u < 1 && v >= 0 && v < 1) {
         if (mip && useMIP) {
            double size = (1. * dx) * (1. * dy) / (mult * mult);
            return pyramid.get(u, v, size);
         }
         return texels[(int) (v * height) * width + (int) (u * width)];

      }
      return texels[0];
   }
}