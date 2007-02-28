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

package com.oyoaha.swing.plaf.oyoaha;

import java.awt.*;
import javax.swing.*;

class OyoahaJava2PaintUtilities extends OyoahaJava1PaintUtilities
{
  /**
  *
  */
  public void setAlphaChannel(Graphics g, Component c, float f)
  {
    if(g instanceof Graphics2D)
    {
      AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f);
      ((Graphics2D)g).setComposite(ac);
    }
  }

  /**
  *
  */
  public boolean isOpaque(Component c)
  {
    return c.isOpaque();
  }

  /**
  *
  */
  public boolean hasFocus(Component c)
  {
    return c.hasFocus();
  }

  /**
   *
   */
  /*public boolean isDefaultButton(Component c)
  {
    return c.isDefaultButton();
  }*/

  /**
  *
  */
  public boolean isLeftToRight(Component c)
  {
    return c.getComponentOrientation().isLeftToRight();
  }
  
  public void paintAGradient(Graphics g, Component c, int x, int y, int width, int height, Color color1, Color color2, boolean horizontal, boolean vertical, int state)
  {
    if (state==OyoahaUtilities.SELECTED_DISABLED || state==OyoahaUtilities.UNSELECTED_DISABLED)
    {
        g.setColor(OyoahaUtilities.getColorFromScheme(state));
        g.fillRect(x, y, width, height);
        return;
    }
    
    try
    {
      if(!c.isShowing())
      return;

      Component root = SwingUtilities.getRoot(c);

      if(root instanceof JFrame)
      {
        root = ((JFrame)root).getContentPane();
      }
      else
      if(root instanceof JWindow)
      {
        root = ((JWindow)root).getContentPane();
      }
      else
      if(root instanceof JApplet)
      {
        root = ((JApplet)root).getContentPane();
      }

      Rectangle rectangle = OyoahaUtilities.getFullRect(root);

      Point origin = root.getLocationOnScreen();
      Point current = c.getLocationOnScreen();

      rectangle.x = origin.x-current.x;
      rectangle.y = origin.y-current.y;

      int x1 = (horizontal)? rectangle.x : 0;
      int y1 = (vertical)? rectangle.y : 0;
      int w1 = (horizontal)? x1+rectangle.width : 0;
      int h1 = (vertical)? y1+rectangle.height : 0;

      GradientPaint gp = new GradientPaint(x1, y1, (color1!=null)? OyoahaUtilities.getColor(color1, state) : OyoahaUtilities.getColor(state), w1, h1, (color2!=null)? OyoahaUtilities.getColor(color2, state) : OyoahaUtilities.getColor(state), false);
      ((Graphics2D)g).setPaint(gp);
      g.fillRect(x, y, width, height);
    }
    catch(Exception ex)
    {

    }
  }
}