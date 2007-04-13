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

public class OyoahaJava1PaintUtilities implements OyoahaPaintUtilities
{
  /**
  *
  */
  public void initialize()
  {

  }

  /**
  *
  */
  public void uninitialize()
  {

  }

  /**
  *
  */
  public void paintBackground(Graphics g, Component c)
  {
    Rectangle r = OyoahaUtilities.getFullRect(c);
    paintBackground(g, c, r.x, r.y, r.width, r.height, getBackground(c), OyoahaUtilities.getStatus(c));
  }

  /**
  *
  */
  public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, int status)
  {
    paintBackground(g, c, x, y, width, height, null, status);
  }

  /**
  *
  */
  public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, Color color, int status)
  {
    if(OyoahaUtilities.isAlphaClasses(c))
    {
      Rectangle r = OyoahaUtilities.getViewRect(c);

      x = r.x;
      y = r.y;
      width = r.width;
      height = r.height;
      
      /*if (c instanceof JComponent)
      {
        Border border = ((JComponent)c).getBorder();
        
        if (border instanceof OyoahaButtonBorderLike)
        {
            OyoahaButtonBorderLike bbl = (OyoahaButtonBorderLike)border;
            
            x -= bbl.getLeftInsets();
            y -= bbl.getTopInsets();
            width += bbl.getLeftInsets()+bbl.getRightInsets();
            height += bbl.getTopInsets()+bbl.getBottomInsets();
        }
      }*/
    }
    else
    if(!isOpaque(c)/*!OyoahaUtilities.isOpaque(c)*/)
    {
      return;
    }

    if(c instanceof AbstractButton && !((AbstractButton)c).isContentAreaFilled())
    {
      return;
    }

    if(color==null)
    {
      color = getBackground(c);
    }

    OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject(c);

    if(x>0 || y>0)
    {
      Shape s = g.getClip();
      g.clipRect(x, y, width, height);

      if(color instanceof javax.swing.plaf.UIResource && o!=null)
      {
        o.paintBackground(g, c, x, y, width, height, status);
      }
      else
      {
        paintColorBackground(g, c, x, y, width, height, color, status);
      }

      g.setClip(s);
    }
    else
    {
      if(color instanceof javax.swing.plaf.UIResource && o!=null)
      {
        o.paintBackground(g, c, 0, 0, width, height, status);
      }
      else
      {
        paintColorBackground(g, c, 0, 0, width, height, color, status);
      }
    }
  }

  /**
  *
  */
  public void paintColorBackground(Graphics g, Component c, int x, int y, int width, int height, Color bg, int status)
  {
    if(status == OyoahaUtilities.UNSELECTED_DISABLED)
    g.setColor(OyoahaUtilities.getColor(bg, OyoahaUtilities.UNSELECTED_ENABLED));
    else
    g.setColor(OyoahaUtilities.getColor(bg, status));
    
    
//g.setColor(OyoahaUtilities.getColorFromScheme(status));
    
    g.fillRect(x,y,width,height);
  }

  //--- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

  /**
  *
  */
  public boolean isOpaque(Component c)
  {
    if(c instanceof JComponent)
    {
      return ((JComponent)c).isOpaque();
    }

    return true;
  }

  /**
  *
  */
  public boolean hasFocus(Component c)
  {
    if(c instanceof JComponent)
    {
      return ((JComponent)c).hasFocus();
    }

    return false;
  }

  /**
   *
   */
  public boolean isDefaultButton(Component c)
  {
    if(c instanceof JButton)
    {
      return ((JButton)c).isDefaultButton();
    }

    return false;
  }

  /**
  *
  */
  public boolean isLeftToRight(Component c)
  {
    return true;
  }

  /**
  *
  */
  public Color getBackground(Component c)
  {
    Color r = c.getBackground();

    if(!OyoahaUtilities.isOpaque(c))
    {
      Container p=c.getParent();

      if(p instanceof JComponent)
      {
      	r=getBackground((JComponent)p);
      }
      else
      if(p!=null)
      {
      	r=p.getBackground();
      }
    }

    return r;
  }

  public void setAlphaChannel(Graphics g, Component c, float f)
  {

  }

  //--- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

  /**
  *
  */
  public Point getAPosition(Component c)
  {
    if(!c.isShowing())
    {
      return new Point(0,0);
    }

    Component root = SwingUtilities.getRoot(c);

    if(root==null)
    {
      return new Point(0,0);
    }

    Point p = c.getLocationOnScreen();

    Point rootPosition = root.getLocationOnScreen();
    p.x -= rootPosition.x;
    p.y -= rootPosition.y;

    return p;
  }

  /**
  *
  */
  public final boolean intersects(int x, int y, int width, int height, int x1, int y1, int width1, int height1)
  {
    return !((x1 + width1 <= x) || (y1 + height1 <= y) || (x1 >= x + width) || (y1 >= y + height));
  }

  /**
  *
  */
  public final Shape normalizeClip(Graphics g, int x, int y, int width, int height)
  {
    Shape s = g.getClip();
    Rectangle r = s.getBounds();

    int xx = Math.max(x, r.x);
    int yy = Math.max(y, r.y);

    g.setClip(xx, yy, Math.min(width+x, r.width+r.x)-xx, Math.min(height+y, r.height+r.y)-yy);

    return s;
  }

//--- --- --- --- --- ---

  /**
  *
  */
  public void paintAMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    try
    {
      Shape s = normalizeClip(g, x, y, width, height);

      int w=i.getWidth(c);
      int h=i.getHeight(c);

      Point p = getAPosition(c);

      //x = y = 0;
      int x0=(x/w*w-(p.x%w));
      int y0=(y/h*h-(p.y%h));

      for(int y1=y0; y1<y+height; y1+=h)
      for(int x1=x0; x1<x+width; x1+=w)
      g.drawImage(i,x1,y1,c);

      g.setClip(s);
    }
    catch(Exception ex)
    {

    }
  }

  /**
  *
  */
  public void paintAScrollMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i, int decalX, int decalY)
  {
    try
    {
      Shape s = normalizeClip(g, x, y, width, height);

      int w=i.getWidth(c);
      int h=i.getHeight(c);

      Point p = getAPosition(c);
      p.x+=decalX;
      p.y+=decalY;

      if(p.x<0)
      p.x =-p.x;
      
      if(p.y<0)
      p.y =-p.y;
           
      int x0 =(x/w*w-(p.x%w));
      int y0 =(y/h*h-(p.y%h));
           
      for(int y1=y0; y1<y+height; y1+=h)
      {
        for(int x1=x0; x1<x+width; x1+=w)
        {
            g.drawImage(i,x1,y1,c);
        }
      }    

      g.setClip(s);
    }
    catch(Exception ex)
    {

    }
  }

  /**
  *
  */
  public void paintAImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos)
  {
    try
    {
      paintAImageAt(g, c, x, y, width, height, i, pos, i.getWidth(c), i.getHeight(c));
    }
    catch(Exception e)
    {

    }
  }

  /**
  *
  */
  public void paintAImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos, int w, int h)
  {
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

      int x1=rectangle.x+(rectangle.width-w)/2;
      int y1=rectangle.y+(rectangle.height-h)/2;
      int w1=rectangle.x+(rectangle.width-w);
      int h1=rectangle.y+(rectangle.height-h);

      switch(pos)
      {
        case OyoahaUtilities.CENTER:
        if(intersects(x, y, width, height, x1, y1, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,x1,y1,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.TOP:
        if(intersects(x, y, width, height, x1, rectangle.y, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,x1,rectangle.y,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.TOPLEFT:
        if(intersects(x, y, width, height, rectangle.x, rectangle.y, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,rectangle.x,rectangle.y,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.LEFT:
        if(intersects(x, y, width, height, rectangle.x, y1, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,rectangle.x,y1,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.LEFTBOTTOM:
        if(intersects(x, y, width, height, rectangle.x, h1, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,rectangle.x,h1,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.BOTTOM:
        if(intersects(x, y, width, height, x1, h1, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,x1,h1,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.BOTTOMRIGHT:
        if(intersects(x, y, width, height, w1, h1, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,w1,h1,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.RIGHT:
        if(intersects(x, y, width, height, w1, y1, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,w1,y1,c);
          g.setClip(s);
        }
        break;
        case OyoahaUtilities.RIGHTTOP:
        if(intersects(x, y, width, height, w1, rectangle.y, w, h))
        {
          Shape s = normalizeClip(g, x, y, width, height);
          g.drawImage(i,w1,rectangle.y,c);
          g.setClip(s);
        }
        break;
      }
    }
    catch(Exception ex)
    {

    }
  }

  /**
  *
  */
  public void paintAScaling(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
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

      Rectangle r = OyoahaUtilities.getFullRect(root);

      Point origin = root.getLocationOnScreen();
      Point current = c.getLocationOnScreen();

      r.x = origin.x-current.x;
      r.y = origin.y-current.y;

//--------

      Shape s = normalizeClip(g, x, y, width, height);
      g.drawImage(i,r.x,r.y,r.width,r.height,c);
      g.setClip(s);
    }
    catch(Exception ex)
    {

    }
  }
  
  /**
  *
  */
  public void paintAGradient(Graphics g, Component c, int x, int y, int width, int height, Color color1, Color color2, boolean horizontal, boolean vertical, int state)
  {
  
  }

//- - - - - -

  /**
  *
  */
  public void paintRMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    Shape s = normalizeClip(g, x, y, width, height);

    int w=i.getWidth(c);
    int h=i.getHeight(c);
    int x0=x/w*w;
    int y0=y/h*h;

    for(int y1=y0; y1<y+height; y1+=h)
    for(int x1=x0; x1<x+width; x1+=w)
    g.drawImage(i,x1,y1,c);

    g.setClip(s);
  }

  /**
  *
  */
  public void paintRScrollMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i, int decalX, int decalY)
  {
    Shape s = normalizeClip(g, x, y, width, height);

    int w=i.getWidth(c);
    int h=i.getHeight(c);

    int x0=(x/w*w-(decalX%w));
    int y0=(y/h*h-(decalY%h));

    for(int y1=y0; y1<y+height; y1+=h)
    for(int x1=x0; x1<x+width; x1+=w)
    g.drawImage(i,x1,y1,c);

    g.setClip(s);
  }

  /**
  *
  */
  public void paintRImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos)
  {
    Shape s = normalizeClip(g, x, y, width, height);

    int x1=x+(width-i.getWidth(c))/2;
    int y1=y+(height-i.getHeight(c))/2;
    int w1=x+(width-i.getWidth(c));
    int h1=y+(height-i.getHeight(c));

    switch(pos)
    {
      case OyoahaUtilities.CENTER:
      g.drawImage(i,x1,y1,c);
      break;
      case OyoahaUtilities.TOP:
      g.drawImage(i,x1,y,c);
      break;
      case OyoahaUtilities.TOPLEFT:
      g.drawImage(i,x,y,c);
      break;
      case OyoahaUtilities.LEFT:
      g.drawImage(i,x,y1,c);
      break;
      case OyoahaUtilities.LEFTBOTTOM:
      g.drawImage(i,x,h1,c);
      break;
      case OyoahaUtilities.BOTTOM:
      g.drawImage(i,x1,h1,c);
      break;
      case OyoahaUtilities.BOTTOMRIGHT:
      g.drawImage(i,w1,h1,c);
      break;
      case OyoahaUtilities.RIGHT:
      g.drawImage(i,w1,y1,c);
      break;
      case OyoahaUtilities.RIGHTTOP:
      g.drawImage(i,w1,y,c);
      break;
    }

    g.setClip(s);
  }

  /**
  *
  */
  public void paintRScaling(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    Shape s = normalizeClip(g, x, y, width, height);
    g.drawImage(i,x,y,width,height,c);
    g.setClip(s);
  }

//- - - - - -
}