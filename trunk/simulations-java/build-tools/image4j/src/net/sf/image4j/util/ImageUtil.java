/*
 * ImageUtil.java
 *
 * Created on 15 May 2006, 01:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.image4j.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

/**
 * Provides utility methods for handling images (<tt>java.awt.BufferedImage</tt>)
 * @author Ian McDonagh
 */
public class ImageUtil {
  /**
   * Creates a scaled copy of the source image.
   * @param src source image to be scaled
   * @param width the width for the new scaled image in pixels
   * @param height the height for the new scaled image in pixels
   * @return a copy of the source image scaled to <tt>width</tt> x <tt>height</tt> pixels.
   */
  public static BufferedImage scaleImage(BufferedImage src, int width, int height) {
    Image scaled = src.getScaledInstance(width, height, 0);
    BufferedImage ret = null;
    /*
    ColorModel cm = src.getColorModel();
    if (cm instanceof IndexColorModel) {
      ret = new BufferedImage(
          width, height, src.getType(), (IndexColorModel) cm
          );
    }
    else {
      ret = new BufferedImage(
          src.getWidth(), src.getHeight(), src.getType()
          );
    }     
    Graphics2D g = ret.createGraphics();    
    //clear alpha channel
    Composite comp = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
    Rectangle2D.Double d = new Rectangle2D.Double(0,0,ret.getWidth(),ret.getHeight());
    g.fill(d);
    g.setComposite(comp);
     */
    ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = ret.createGraphics();
    //copy image    
    g.drawImage(scaled, 0, 0, null);
    return ret;        
  }

    public static BufferedImage rescale( BufferedImage in, int width, int height ) {
        BufferedImage newImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        Graphics2D g2 = newImage.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance( (double)width/in.getWidth(), (double)height/in.getHeight() );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2.drawRenderedImage( in, at );
        return newImage;
    }
}
