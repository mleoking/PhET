package edu.colorado.phet.rotation.util.blackdirt;

import java.awt.*;
import java.util.*;
import java.lang.String;

//               picturetemp1 = picturetemp.parseInt(pictureString, 16);
public class WMFToolkit{
      private int screenResolution;
      private int screenWidth;
      private int screenHeight;
      private int twipsPerPixel;

      private int red;
      private int green;
      private int blue;

      public static int vbBlue   = 16711680; // ff0000
      public static int vbGreen  =    65280; // 00ff00
      public static int vbRed    =      255; // 0000ff


   public WMFToolkit()  {
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      screenWidth = d.width;
      screenHeight = d.height;
      screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
      twipsPerPixel = (int) (1440/screenResolution);
      red = 0;
      green = 0;
      blue = 0;
   }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenResolution() {
        return screenResolution;
    }

    public int getTwipsPerPixel() {
        return twipsPerPixel;
    }

    public short twip2pixel( short t) {
        return (short) (t/twipsPerPixel);
    }


    public void setColors( int colorValue) {
      blue = (int)((colorValue & vbBlue)/ 65536);
      green = (int)((colorValue & vbGreen)/ 256);
      red = (int)((colorValue & vbRed));
    }

    public String getRGBColor( int colorValue) {
      //forgive hack please
      String twozeros = "00";
      String fourzeros = "0000";
      String rgbval;
      int rgblen;
      blue = (int)((colorValue & vbBlue)/ 65536);
      green = (int)((colorValue & vbGreen)/ 256);
      red = (int)((colorValue & vbRed));
      int i = red * 65536 + green * 256 + blue;
      rgbval = Integer.toHexString(i);
      rgblen = rgbval.length();

      if (rgblen == 6) return (rgbval);
      if (rgblen == 5) return ("0" + rgbval);
      if (rgblen == 4) return ("00" + rgbval);
      if (rgblen == 3) return ("000" + rgbval);
      if (rgblen == 2) return ("0000" + rgbval);
      return ("00000" + rgbval);
    }





}
