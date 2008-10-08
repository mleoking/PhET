package render;

//import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

public class TextureReader {
  
    private static final int DEBUG_IMAGE_LOAD = 0x00000001;
    private static int DEBUG = 0x00000000; // | DEBUG_IMAGE_LOAD;
  
    public static OTexture readTexture(String filename) throws IOException {
        return readPixels(readImage(filename));
    }
    
    public static OTexture readTexture(URL fileURL) throws IOException {
      return readPixels(readImage(fileURL));
    }
    
    private static URL getResourceFromString(String filename) 
    {
      // Try to load resource from jar
      URL url = ClassLoader.getSystemResource(filename);
      // If not found in jar, then load from disk
      if (url == null) {
          try {
              url = new URL("file", "localhost", filename);
          } catch (Exception urlException) {
            try
            {
              url = new URL(filename);
            }
            catch (MalformedURLException e) {}
          } // ignore
      }
      return url;
    }

    
   /* private static BufferedImage readImage2(URL fileURL) throws IOException {
   return ImageIO.read(fileURL);
   }*/    
   
   /*  private static BufferedImage readImage2(String resourceName) throws IOException {
   URL url = getResource(resourceName);
   if (url == null) {
   throw new RuntimeException("Error reading resource " + resourceName);
   }
   return ImageIO.read(url);
   }*/
    
    
    public static BufferedImage readImage(String resourceName) throws IOException 
    {
      URL url = getResourceFromString(resourceName);
      if (url == null)
        throw new RuntimeException("Error reading resource " + resourceName);
      Image img = loadImage(url);      
      return toBufferedImage(img);
    }
    
    public static BufferedImage readImage(URL fileURL) throws IOException {
      return toBufferedImage(loadImage(fileURL));
    }
    
    private static Image loadImage(URL url) {
      
      if( (DEBUG & DEBUG_IMAGE_LOAD) != 0)
        System.err.println("TextureReader.loadImage()");
      
      Image im = null;
      try {
        im = Toolkit.getDefaultToolkit().getImage(url);
        MediaTracker tracker = new MediaTracker(TRACKER);
        tracker.addImage(im, 0);
        tracker.waitForID(0);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      
      if( (DEBUG & DEBUG_IMAGE_LOAD) != 0)      
        System.err.println("verifying...");
      
      if (im == null || im.getWidth(null) < 0) { 
        System.err.println("Null Image: "+url);
      }
      else {
        if( (DEBUG & DEBUG_IMAGE_LOAD) != 0)
          System.err.println("ok.");
      }
      return im;
    }
    static final Component TRACKER = new Canvas(); // hack

    
    /** NOTE: only works with BufferedImage.TYPE_INT_RGB for now */
    private static BufferedImage toBufferedImage(Image img) throws IOException 
    {
      BufferedImage bImage = new BufferedImage
        (img.getWidth(null), img.getHeight(null),BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bImage.createGraphics();
      g.drawImage(img,0,0,null);
      return bImage;
    }

    private static OTexture readPixels(BufferedImage img) {
        int[] packedPixels = new int[img.getWidth() * img.getHeight()];

        PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), packedPixels, 0, img.getWidth());
        try {
            pixelgrabber.grabPixels();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        ByteBuffer unpackedPixels = ByteBuffer.allocateDirect(packedPixels.length * 3);
        for (int row = img.getHeight() - 1; row >= 0; row--) {
            for (int col = 0; col < img.getWidth(); col++) {
                int packedPixel = packedPixels[row * img.getWidth() + col];
                unpackedPixels.put((byte) ((packedPixel >> 16) & 0xFF));
                unpackedPixels.put((byte) ((packedPixel >> 8) & 0xFF));
                unpackedPixels.put((byte) ((packedPixel >> 0) & 0xFF));
            }
        }
        return new OTexture(unpackedPixels, img.getWidth(), img.getHeight());
    }

    public static class OTexture {
        private ByteBuffer pixels;
        private int width;
        private int height;

        public OTexture(ByteBuffer pixels, int width, int height) {
            this.height = height;
            this.pixels = pixels;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public ByteBuffer getPixels() {
            return pixels;
        }

        public int getWidth() {
            return width;
        }
    }
    
    public static void main(String[] args) throws Exception
    {
      //System.out.println(loadImage(new URL("http://cat.nyu.edu/~dhowe/img/amata_thumb.jpg")));
      URL u =  new URL("http://cat.nyu.edu/~dhowe/img/amata_thumb.jpg");
      //System.out.println("u="+u);
      System.out.println(TextureReader.readImage(u));  
      System.out.println(TextureReader.readImage("/c:slag.jpg")); 
    }
}