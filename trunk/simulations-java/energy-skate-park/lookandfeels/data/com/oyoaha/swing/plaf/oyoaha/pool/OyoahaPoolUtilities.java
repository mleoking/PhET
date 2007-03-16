/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha.pool;

import java.awt.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public abstract class OyoahaPoolUtilities implements OyoahaPool
{
  public static final Insets getInsets(Image _image)
  {
    try
    {
      Object comment = _image.getProperty("comment", null);

      if(comment!=null && comment instanceof String)
      {
        return OyoahaThemeLoaderUtilities.readInsets((String)comment);
      }
    }
    catch(Exception e)
    {

    }

    return new Insets(2,2,2,2);
  }

  public static final Rectangle[] getSlices(Insets insets, int w, int h, boolean createBackground)
  {
    int ww = w-(insets.left+insets.right);
    int hh = h-(insets.top+insets.bottom);

    Rectangle[] r = new Rectangle[9];

    //background
    if(createBackground)
    r[0] = new Rectangle(insets.left, insets.top, ww, hh);
    else
    r[0] = null;

    //corner
    if(insets.left>0 && insets.top>0)
    r[1] = new Rectangle(0, 0, insets.left, insets.top);
    else
    r[1] = null;

    if(insets.left>0 && insets.bottom>0)
    r[2] = new Rectangle(0, h-insets.bottom, insets.left, insets.bottom);
    else
    r[2] = null;

    if(insets.right>0 && insets.bottom>0)
    r[3] = new Rectangle(w-insets.right, h-insets.bottom, insets.right, insets.bottom);
    else
    r[3] = null;

    if(insets.right>0 && insets.top>0)
    r[4] = new Rectangle(w-insets.right, 0, insets.right, insets.top);
    else
    r[4] = null;

    //sides
    if(insets.top>0)
    r[5] = new Rectangle(insets.left, 0, ww, insets.top);
    else
    r[5] = null;

    if(insets.left>0)
    r[6] = new Rectangle(0, insets.top, insets.left, hh);
    else
    r[6] = null;

    if(insets.bottom>0)
    r[7] = new Rectangle(insets.left, h-insets.bottom, ww, insets.bottom);
    else
    r[7] = null;

    if(insets.right>0)
    r[8] = new Rectangle(w-insets.right, insets.top, insets.right, hh);
    else
    r[8] = null;

    return r;
  }

  public static final Rectangle[] getSlices(Image _image)
  {
    try
    {
      Object comment = _image.getProperty("comment", null);

      if(comment!=null && comment instanceof String)
      {
        String c = (String)comment;

        StringTokenizer tok = new StringTokenizer(c);

        int size = new Integer(c).intValue();

        Rectangle[] r = new Rectangle[size];

        for(int i=0;i<size;i++)
        {
          r[i] = OyoahaThemeLoaderUtilities.readRectangle(tok.nextToken());
        }

        return r;
      }
    }
    catch(Exception e)
    {

    }

    return null;
  }

  protected final static float ALPHA = 0.00732421875f;

  protected static int getValue(int lux)
  {
    if (lux<64)
    {
      return(int)((lux*lux)*ALPHA);
    }
    else
    if (lux<191)
    {
      int p = lux-127;
      return (int)(60-((p*p)*ALPHA));
    }

    int p = lux-255;
    return (int)((p*p)*ALPHA);
  }

  public static final Color getColor(Color _color, Color _scheme, int strong)
  {
    int r = _color.getRed();
    int g = _color.getGreen();
    int b = _color.getBlue();

    int rr = _scheme.getRed();
    int gg = _scheme.getGreen();
    int bb = _scheme.getBlue();

    int lux = r+g+b;

      double original = (100-strong)/100.0d;
      double colorized = strong/100.0d;

      r = (int)((r*original)+(rr*colorized));
      g = (int)((g*original)+(gg*colorized));
      b = (int)((b*original)+(bb*colorized));
      
      int lux2 = r+g+b;
      
      int diff = (lux-lux2)/3;
      
      if (diff>10 || diff<-10)
      {
          r += diff;
          g += diff;
          b += diff;
          
          if(r>255)
          r = 255;
          else
          if(r<0)
          r = 0;
    
          if(g>255)
          g = 255;
          else
          if(g<0)
          g = 0;
          
          if(b>255)
          b = 255;
          else
          if(b<0)
          b = 0;
       }  

    return new Color(r,g,b);

    //return new Color((int)((r*original)+(rr*colorized)), (int)((g*original)+(gg*colorized)), (int)((b*original)+(bb*colorized)));
  }

  public static final Color getColor2(Color _color, Color _scheme)
  {
    int r = _color.getRed();
    int g = _color.getGreen();
    int b = _color.getBlue();

    int rr = _scheme.getRed();
    int gg = _scheme.getGreen();
    int bb = _scheme.getBlue();

    return new Color((int)((r*0.1)+(rr*0.9)), (int)((g*0.1)+(gg*0.9)), (int)((b*0.1)+(bb*0.9)));
    
    //return getColor(_color, _scheme, 100);
  }

  public static final Color getColor3(Color _color, Color _scheme)
  {
    int r = _color.getRed();
    int g = _color.getGreen();
    int b = _color.getBlue();

    int rr = _scheme.getRed();
    int gg = _scheme.getGreen();
    int bb = _scheme.getBlue();

    return new Color((int)((r*0.2)+(rr*0.8)), (int)((g*0.2)+(gg*0.8)), (int)((b*0.2)+(bb*0.8)));
    
    //return getColor(_color, _scheme, 20);
  }

  protected final static float fup = 1.1f;
  protected final static float fdown = 0.9f;

  public final static ColorUIResource brighten(Color c, int i)
  {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();

    r = (int)(r*(fup*i));
    g = (int)(g*(fup*i));
    b = (int)(b*(fup*i));

    if(r>255)
    r=255;
                                         
    if(g>255)
    g=255;

    if(b>255)
    b=255;

    return new ColorUIResource(r,g,b);
  }

  public final static ColorUIResource darken(Color c, int i)
  {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();

    while(i-->0)
    {
      r *= fdown;
      g *= fdown;
      b *= fdown;
    }

    return new ColorUIResource(r,g,b);
  }

  //used by button border
  public static final Image getColorizedImage(int[] source, int w, int h, Color color, int strong)
  {
    int[] chg = new int[w*h];
    //return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, getColorized(source, chg, w, h, color, strong), 0, w));
    return OyoahaUtilities.loadImage(getColorized(source, chg, w, h, color, strong), w, h);
  }

  //used by regular background
  public static final Image getColorizedImage2(int[] source, int w, int h, Color color)
  {
    int[] chg = new int[w*h];
    //return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, getColorized2(source, chg, w, h, color), 0, w));
    return OyoahaUtilities.loadImage(getColorized2(source, chg, w, h, color), w, h);
  }

  //used for disabled
  public static final Image getColorizedImage3(int[] source, int w, int h, Color color)
  {
    int[] chg = new int[w*h];
    //return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, getColorized3(source, chg, w, h, color), 0, w));
    return OyoahaUtilities.loadImage(getColorized3(source, chg, w, h, color), w, h);
  }

  public static final int[] getColorized(int[] org, int[] chg, int w, int h, Color color, int strong)
  {
    int rr = color.getRed();
    int gg = color.getGreen();
    int bb = color.getBlue();

    for(int i=0;i<w*h;i++)
    {
      int a = (org[i] >> 24) & 0xFF;
      int r = (org[i] >> 16) & 0xFF;
      int g = (org[i] >> 8) & 0xFF;
      int b = (org[i] >> 0) & 0xFF;

      int lux = r+g+b;

      double original = (100-strong)/100.0d;
      double colorized = strong/100.0d;

      r = (int)((r*original)+(rr*colorized));
      g = (int)((g*original)+(gg*colorized));
      b = (int)((b*original)+(bb*colorized));
      
      int lux2 = r+g+b;
      
      int diff = (lux-lux2)/3;
      
      if (diff>10 || diff<-10)
      {
          r += diff;
          g += diff;
          b += diff;
          
          if(r>255)
          r = 255;
          else
          if(r<0)
          r = 0;
    
          if(g>255)
          g = 255;
          else
          if(g<0)
          g = 0;
          
          if(b>255)
          b = 255;
          else
          if(b<0)
          b = 0;
       }   

      chg[i] = a<<24 | r<<16 | g<<8 | b<<0;
    }

    return chg;
  }

  public static final int[] getColorized2(int[] org, int[] chg, int w, int h, Color color)
  {
    int rr = color.getRed();
    int gg = color.getGreen();
    int bb = color.getBlue();

    for(int i=0;i<w*h;i++)
    {
      int a = (org[i] >> 24) & 0xFF;
      int r = (org[i] >> 16) & 0xFF;
      int g = (org[i] >> 8) & 0xFF;
      int b = (org[i] >> 0) & 0xFF;

      r = (int)((r*0.9)+(rr*0.1));
      g = (int)((g*0.9)+(gg*0.1));
      b = (int)((b*0.9)+(bb*0.1));

      chg[i] = a<<24 | r<<16 | g<<8 | b<<0;
    }

    return chg;
    
    //return getColorized(org, chg, w, h, color, 100);
  }

  public static final int[] getColorized3(int[] org, int[] chg, int w, int h, Color color)
  {
    int rr = color.getRed();
    int gg = color.getGreen();
    int bb = color.getBlue();

    for(int i=0;i<w*h;i++)
    {
      int a = (org[i] >> 24) & 0xFF;
      int r = (org[i] >> 16) & 0xFF;
      int g = (org[i] >> 8) & 0xFF;
      int b = (org[i] >> 0) & 0xFF;

      //a = a/2;
      r = (int)((r*0.2)+(rr*0.8));
      g = (int)((g*0.2)+(gg*0.8));
      b = (int)((b*0.2)+(bb*0.8));

      chg[i] = a<<24 | r<<16 | g<<8 | b<<0;
    }

    return chg;
    
    //return getColorized(org, chg, w, h, color, 20);
  }

  public static final Image cropToImage(int[] source, int x, int y, int w, int h, int originw, int originh)
  {
    //return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, crop(source, x, y, w, h, originw, originh), 0, w));
    return OyoahaUtilities.loadImage(crop(source, x, y, w, h, originw, originh), w, h);
  }

  public static final int[] crop(int[] source, int x, int y, int w, int h, int originw, int originh)
  {
    int[] target = new int[w*h];

    int posy = 0;
    int posx = 0;

    for(int i=y;i<y+h;i++,posy++)
    {
      posx = 0;

      for(int j=x;j<x+w;j++,posx++)
      {
        target[(posy*w)+posx] = source[(i*originw)+j];
      }
    }

    return target;
  }

  public static final Image createImageFromBump(Image bump, Color black, Color white, Color gray)
  {
    int w = bump.getWidth(null);
    int h = bump.getHeight(null);
    int[] bump_source = new int[w * h];

    try
    {
      PixelGrabber pg = new PixelGrabber(bump, 0, 0, w, h, bump_source, 0, w);
      pg.grabPixels();
    }
    catch(Exception exception)
    {
      return null;
    }

    return createImageFromBump(bump_source, w, h, black, white, gray);
  }

  public static final Image createImageFromBump(int[] bump_source, int width, int height, Color black, Color white, Color gray)
  {
    int blackRGB = black.getRGB();
    int whiteRGB = white.getRGB();
    int grayRGB = gray.getRGB();

    final int unvisible = ((0 & 0xFF) << 24) | ((255 & 0xFF) << 16) | ((255 & 0xFF) << 8)  | ((255 & 0xFF) << 0);

    final int blackTest =  ((255 & 0xFF) << 24) | ((0 & 0xFF) << 16) | ((0 & 0xFF) << 8)  | ((0 & 0xFF) << 0);
    final int whiteTest = ((255 & 0xFF) << 24) | ((255 & 0xFF) << 16) | ((255 & 0xFF) << 8)  | ((255 & 0xFF) << 0);
    final int grayTest =  ((255 & 0xFF) << 24) | ((100 & 0xFF) << 16) | ((100 & 0xFF) << 8)  | ((100 & 0xFF) << 0);

    int[] target = new int[width*height];

    /*if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
    {*/
        for(int i=0;i<target.length;i++)
        {
          switch(bump_source[i])
          {
            case blackTest:
            target[i] = blackRGB;
            break;
            case grayTest:
            target[i] = grayRGB;
            break;
            case whiteTest:
            target[i] = whiteRGB;
            break;
            default:
            target[i] = unvisible;
            break;
          }
        }
    /*}
    else
    {
        for(int i=0;i<target.length;i++)
        {
          switch(bump_source[i])
          {
            case blackTest:
            target[i] = whiteRGB;
            break;
            case grayTest:
            target[i] = unvisible;
            break;
            case whiteTest:
            target[i] = blackRGB;
            break;
            default:
            target[i] = unvisible;
            break;
          }
        }
    }*/

    //return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, target, 0, width));
    return OyoahaUtilities.loadImage(target, width, height);
  }
}