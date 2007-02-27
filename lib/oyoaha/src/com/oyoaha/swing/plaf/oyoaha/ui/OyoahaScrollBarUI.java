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

package com.oyoaha.swing.plaf.oyoaha.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaScrollBarUI extends MetalScrollBarUI
{
  public static ComponentUI createUI(JComponent x)
  {
    return new OyoahaScrollBarUI();
  }

  public void installUI(JComponent c)
  {
    super.installUI(c);
    OyoahaUtilities.installRolloverListener(c);
  }

  public void uninstallUI(JComponent c)
  {
    super.uninstallUI(c);
    OyoahaUtilities.uninstallRolloverListener(c);
  }

  protected JButton createDecreaseButton(int orientation)
  {
    //TODO use IconImage if any;
    decreaseButton = new OyoahaScrollButton(orientation, scrollBarWidth, isFreeStanding, true, true);
    return decreaseButton;
  }

  protected JButton createIncreaseButton(int orientation)
  {
    //TODO use IconImage if any;
    increaseButton =  new OyoahaScrollButton(orientation, scrollBarWidth, isFreeStanding, true, true);
    return increaseButton;
  }

  public void update(Graphics g, JComponent c)
  {
    OyoahaUtilities.paintBackground(g,c);
    paint(g, c);
  }
  
  protected void setThumbBounds(int x, int y, int width, int height)
  {
    /* If the thumbs bounds haven't changed, we're done.
     */
    if ((thumbRect.x == x) && (thumbRect.y == y) && (thumbRect.width == width) && (thumbRect.height == height)) 
    {
        return;
    }

    int minX = Math.min(x, thumbRect.x);
    int minY = Math.min(y, thumbRect.y);
    int maxX = Math.max(x + width+4, thumbRect.x + thumbRect.width+4);
    int maxY = Math.max(y + height+4, thumbRect.y + thumbRect.height+4);

    thumbRect.setBounds(x, y, width, height);
    
    scrollbar.repaint(minX, minY, maxX-minX, maxY-minY);
  }

  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
  {
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if(scrollbar.getOrientation() == JScrollBar.VERTICAL)
    {
      Shape s = g.getClip();
      g.clipRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

      OyoahaBackgroundObject oyoaha = OyoahaUtilities.getBackgroundObject("ScrollBarTrack");
      Color color = UIManager.getColor("ScrollBar.background");

      if(oyoaha!=null)
      {
        oyoaha.paintBackground(g, c, trackBounds.x, trackBounds.y+1, trackBounds.width, trackBounds.height-1, OyoahaUtilities.getStatus(c));
      }
      else
      {
        OyoahaUtilities.paintColorBackground(g, c, trackBounds.x, trackBounds.y+1, trackBounds.width, trackBounds.height-1, color, OyoahaUtilities.getStatus(c));
      }

      if(c.isEnabled())
      {
        /*g.setColor(scheme.getBlack());
        g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y+trackBounds.height-1);
        g.drawLine(trackBounds.x+trackBounds.width-1, trackBounds.y, trackBounds.x+trackBounds.width-1, trackBounds.y+trackBounds.height-1);

        if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
        {
            g.setColor(scheme.getGray());
            g.drawLine(trackBounds.x+1, trackBounds.y, trackBounds.x+1, trackBounds.y+trackBounds.height-2);
            g.drawLine(trackBounds.x+1, trackBounds.y, trackBounds.x+trackBounds.width-2, trackBounds.y);
        }*/
        
        GradientPaint gp;
        
        gp = new GradientPaint(trackBounds.x+3, 0, new Color(0,0,0,0), trackBounds.x, 0, new Color(0,0,0,100), false);
        ((Graphics2D)g).setPaint(gp);
        g.fillRect(trackBounds.x, trackBounds.y, 3, trackBounds.height);
        
        
        
        gp = new GradientPaint(0, trackBounds.y, new Color(0,0,0,100), 0, trackBounds.y+3, new Color(0,0,0,0), false);
        ((Graphics2D)g).setPaint(gp);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, 3);
        
        
        
        
        
        
        
        
        
        g.setColor(scheme.getBlack());
        g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y+trackBounds.height-1);
        
        g.setColor(scheme.getWhite());
        g.drawLine(trackBounds.x+trackBounds.width-1, trackBounds.y, trackBounds.x+trackBounds.width-1, trackBounds.y+trackBounds.height-1);
      }
      else
      {
        g.setColor(scheme.getGray());
        g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y+trackBounds.height-1);
        g.drawLine(trackBounds.x+trackBounds.width-1, trackBounds.y, trackBounds.x+trackBounds.width-1, trackBounds.y+trackBounds.height-1);
      }

      g.setClip(s);
    }
    else  // HORIZONTAL
    {
      Shape s = g.getClip();
      g.clipRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

      OyoahaBackgroundObject oyoaha = OyoahaUtilities.getBackgroundObject("ScrollBarTrack");

      Color color = UIManager.getColor("ScrollBar.background");

      if(oyoaha!=null)
      {
        oyoaha.paintBackground(g, c, trackBounds.x+1, trackBounds.y, trackBounds.width-1, trackBounds.height, OyoahaUtilities.getStatus(c));
      }
      else
      {
        OyoahaUtilities.paintColorBackground(g, c, trackBounds.x+1, trackBounds.y, trackBounds.width-1, trackBounds.height, color, OyoahaUtilities.getStatus(c));
      }

      if (c.isEnabled())
      {
        
        
        GradientPaint gp;
        
/*gp = new GradientPaint(0, trackBounds.y+trackBounds.height-4, new Color(0,0,0,0), 0, trackBounds.y+trackBounds.height-1, new Color(0,0,0,100), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(trackBounds.x, trackBounds.y+trackBounds.height-4, trackBounds.width, 3);*/
        
        gp = new GradientPaint(0, trackBounds.y+3, new Color(0,0,0,0), 0, trackBounds.y, new Color(0,0,0,100), false);
        ((Graphics2D)g).setPaint(gp);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, 3);
        
        
        
        gp = new GradientPaint(trackBounds.x, 0, new Color(0,0,0,100), trackBounds.x+3, 0, new Color(0,0,0,0), false);
        ((Graphics2D)g).setPaint(gp);
        g.fillRect(trackBounds.x, trackBounds.y, 3, trackBounds.height);
        
/*gp = new GradientPaint(trackBounds.x+trackBounds.width-4, 0, new Color(0,0,0,0), trackBounds.x+trackBounds.width, 0, new Color(0,0,0,100), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(trackBounds.x+trackBounds.width-3, trackBounds.y, 3, trackBounds.height);*/
      
      
        /*g.setColor(scheme.getBlack());
        g.drawLine(trackBounds.x-1, trackBounds.y, trackBounds.x+trackBounds.width, trackBounds.y);
        g.drawLine(trackBounds.x-1, trackBounds.y+trackBounds.height-1, trackBounds.x+trackBounds.width, trackBounds.y+trackBounds.height-1);

        if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
        {
            g.setColor(scheme.getGray());
            g.drawLine(trackBounds.x, trackBounds.y+1, trackBounds.x, trackBounds.y+trackBounds.height-2);
            g.drawLine(trackBounds.x, trackBounds.y+1, trackBounds.x+trackBounds.width-2, trackBounds.y+1);
        }*/
        
        g.setColor(scheme.getBlack());
        g.drawLine(trackBounds.x-1, trackBounds.y, trackBounds.x+trackBounds.width, trackBounds.y);
        
        
        //g.setColor(scheme.getGray());
        //g.drawLine(trackBounds.x, trackBounds.y+1, trackBounds.x, trackBounds.y+trackBounds.height-2);
        //g.drawLine(trackBounds.x, trackBounds.y+1, trackBounds.x+trackBounds.width-2, trackBounds.y+1);
        
        g.setColor(scheme.getWhite());
        g.drawLine(trackBounds.x, trackBounds.y+trackBounds.height-1, trackBounds.x+trackBounds.width, trackBounds.y+trackBounds.height-1);
      }
      else
      {
        g.setColor(scheme.getGray());
        g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x+trackBounds.width-1, trackBounds.y);
        g.drawLine(trackBounds.x, trackBounds.y+trackBounds.height-1, trackBounds.x+trackBounds.width-1, trackBounds.y+trackBounds.height-1);
      }

      g.setClip(s);
    }
  }

  protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds )
  {
    if (!c.isEnabled())
    {
      return;
    }

    if(scrollbar.getOrientation() == JScrollBar.VERTICAL)
    {
      quickPaint(g, c, thumbBounds.getBounds(), false);
    }
    else  // HORIZONTAL
    {
      quickPaint(g, c, thumbBounds.getBounds(), true);
    }
  }

  private void quickPaint(Graphics g, JComponent c, Rectangle r, boolean horizontal)
  {
    OyoahaBackgroundObject oyoaha = OyoahaUtilities.getBackgroundObject("ScrollBarThumb");
    Color color = OyoahaUtilities.getBackground(c);
    int status = OyoahaUtilities.getStatus(c);

    /*
    Shape s = g.getClip();
    g.clipRect(r.x, r.y, r.width, r.height);

    Color color = UIManager.getColor("Button.background");

    if(oyoaha!=null)
    {
      oyoaha.paintBackground(g, c, r.x, r.y, r.width, r.height, OyoahaUtilities.getStatus(c));
    }
    else
    {
      OyoahaUtilities.paintColorBackground(g, c, r.x, r.y, r.width, r.height, color, OyoahaUtilities.getStatus(c));
    }
    
    g.setClip(s);
    */
    
    if(r.x>0 || r.y>0)
    {
      Shape s = g.getClip();
      g.clipRect(r.x, r.y, r.width, r.height);

      if(color instanceof javax.swing.plaf.UIResource && oyoaha!=null)
      {
        oyoaha.paintBackground(g, c, r.x, r.y, r.width, r.height, status);
      }
      else
      {
        OyoahaUtilities.paintColorBackground(g, c, r.x, r.y, r.width, r.height, color, status);
      }

      g.setClip(s);
    }
    else
    {
      if(color instanceof javax.swing.plaf.UIResource && oyoaha!=null)
      {
        oyoaha.paintBackground(g, c, 0, 0, r.width, r.height, status);
      }
      else
      {
        OyoahaUtilities.paintColorBackground(g, c, 0, 0, r.width, r.height, color, status);
      }
    }

    //paint bump mapping here
    OyoahaUtilities.paintBump(g, c, r.x, r.y, r.width, r.height, OyoahaUtilities.getStatus(c), 1, 1);

    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
    {
        /*g.setColor(scheme.getWhite());
        g.drawLine(r.x+1, r.y+1, r.x+1, r.y+r.height-2);
        g.drawLine(r.x+1, r.y+1, r.x+r.width-2, r.y+1);

        g.setColor(scheme.getGray());
        g.drawLine(r.x+1, r.y+r.height-2, r.x+r.width-2, r.y+r.height-2);
        g.drawLine(r.x+r.width-2, r.y+1, r.x+r.width-2, r.y+r.height-2);*/
        
        g.setColor(scheme.getWhite());
        g.drawLine(r.x, r.y, r.x, r.y+r.height);
        g.drawLine(r.x, r.y, r.x+r.width-2, r.y);

        g.setColor(scheme.getGray());
        g.drawLine(r.x+1, r.y+r.height-1, r.x+r.width-1, r.y+r.height-1);
        g.drawLine(r.x+r.width-1, r.y+1, r.x+r.width-1, r.y+r.height-1);
        
        g.setColor(scheme.getBlack());
        g.drawLine(r.x, r.y+r.height-1, r.x+r.width-1, r.y+r.height-1);
        g.drawLine(r.x+r.width-1, r.y, r.x+r.width-1, r.y+r.height-1);
    }
    else
    {
        g.setColor(scheme.getBlack());
        g.drawRect(r.x, r.y, r.width-1, r.height-1);
    }

    if (horizontal)
    {
        GradientPaint gp;
        
        gp = new GradientPaint(r.x+r.width, 0, new Color(0,0,0,100), r.x+r.width+3, 0, new Color(0,0,0,0), false);
        ((Graphics2D)g).setPaint(gp);
        g.fillRect(r.x+r.width, r.y, 3, r.height);
        
/*gp = new GradientPaint(r.x, 0, new Color(0,0,0,100), r.x-4, 0, new Color(0,0,0,0), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(r.x-3, r.y+1, 3, r.height-2);*/
    }
    else
    {
        GradientPaint gp;
        
        gp = new GradientPaint(0, r.y+r.height, new Color(0,0,0,100), 0, r.y+r.height+3, new Color(0,0,0,0), false);
        ((Graphics2D)g).setPaint(gp);
        g.fillRect(r.x, r.y+r.height, r.width, 3);
    }
  }
}