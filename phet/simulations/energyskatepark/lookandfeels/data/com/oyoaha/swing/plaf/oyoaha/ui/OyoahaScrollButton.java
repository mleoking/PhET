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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaScrollButton extends MetalScrollButton
{
  protected boolean drawBorder;
  protected boolean paintBackground;

  public OyoahaScrollButton(int direction, int width, boolean freeStanding)
  {
    this(direction,width,freeStanding,false,false);
  }

  public OyoahaScrollButton(int direction, int width, boolean freeStanding, boolean drawBorder)
  {
    this(direction,width,freeStanding,drawBorder,false);
  }

  public OyoahaScrollButton(int direction, int width, boolean freeStanding, boolean drawBorder, boolean paintBackground)
  {
    super(direction,width,freeStanding);
    this.drawBorder = drawBorder;
    this.paintBackground = paintBackground;
  }

  public void setBorder(Border b)
  {
    //NO BORDER
  }

  public void paint(Graphics g)
  {
    int h = getHeight();
    int w = getWidth();
    paint(g, w, h, ((h+1)/2)-1, (h+1)/4, OyoahaUtilities.getStatus(this));
  }
  
  public boolean isEnabled()
  {
    return getParent().isEnabled();
  }
  
  protected void fireActionPerformed(ActionEvent event)
  {
    if (isEnabled())
    {
        super.fireActionPerformed(event);
    }
  }

  protected void paint(Graphics g, int w, int h, int arrowWidth, int arrowHeight, int state)
  {
    OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("Button");
    Color color = this.getBackground();

    if(paintBackground)
    {
      if(o!=null && color instanceof UIResource)
      {
        o.paintBackground(g, this, 0, 0, w, h, state);
      }
      else
      {
        OyoahaUtilities.paintColorBackground(g, this, 0, 0, w, h, color, state);
      }
    }
    else
    if(state!=OyoahaUtilities.UNVISIBLE && state!=OyoahaUtilities.UNSELECTED_ENABLED)
    {
      if(o!=null && color instanceof UIResource)
      {
        o.paintBackground(g, this, 0, 0, w, h, state);
      }
      else
      {
        OyoahaUtilities.paintColorBackground(g, this, 0, 0, w, h, color, state);
      }
    }

    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if(isEnabled())
    g.setColor(scheme.getBlack());
    else
    g.setColor(scheme.getDisabled());

    if (getDirection()==NORTH)
    {
     int startY = ((h+1) - arrowHeight) / 2;
     int startX = (w / 2);

     for (int line = 0; line < arrowHeight; line++)
     {
      g.drawLine( startX-line, startY+line, startX +line+1, startY+line);
     }
    }
    else
    if (getDirection()==SOUTH)
    {
     int startY = (((h+1) - arrowHeight) / 2)+ arrowHeight-1;
     int startX = (w / 2);

     for (int line = 0; line < arrowHeight; line++)
     {
      g.drawLine( startX-line, startY-line, startX +line+1, startY-line);
     }
    }
    else
    if(getDirection()==EAST)
    {
     int startX = (((w+1) - arrowHeight) / 2) + arrowHeight-1;
     int startY = (h / 2);

     for (int line = 0; line < arrowHeight; line++)
     {
      g.drawLine(startX-line, startY-line, startX -line, startY+line+1);
     }
    }
    else
    if (getDirection()==WEST)
    {
      int startX = (((w+1) - arrowHeight) / 2);
      int startY = (h / 2);

      for (int line = 0; line < arrowHeight; line++)
      {
        g.drawLine(startX+line, startY-line, startX +line, startY+line+1);
      }
    }

    if(drawBorder)
    {
      if (!isEnabled())
      {
        g.setColor(scheme.getDisabled());
        g.drawLine(0, 0, 0, h-1);
        g.drawLine(0, 0, w-1, 0);
        g.drawLine(0, h-1, w-1, h-1);
        g.drawLine(w-1, 0, w-1, h-1);
      }
      else
      {
          /*g.setColor(scheme.getBlack());
          g.drawLine(0, 0, 0, h-1);
          g.drawLine(0, 0, w-1, 0);
          g.drawLine(0, h-1, w-1, h-1);
          g.drawLine(w-1, 0, w-1, h-1);

          if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
          {
              g.setColor(scheme.getWhite());
              g.drawLine(1, 1, 1, h-2);
              g.drawLine(1, 1, w-2, 1);

              g.setColor(scheme.getGray());
              g.drawLine(1, h-2, w-2, h-2);
              g.drawLine(w-2, 1, w-2, h-2);
          }*/
          
          g.setColor(scheme.getWhite());
          g.drawLine(0, 0, 0, h-1);
          g.drawLine(0, 0, w-1, 0);

          //g.setColor(scheme.getGray());
          //g.drawLine(1, h-2, w-2, h-2);
          //g.drawLine(w-2, 1, w-2, h-2);
          
          g.setColor(scheme.getBlack());
          g.drawLine(0, h-1, w-1, h-1);
          g.drawLine(w-1, 0, w-1, h-1);
      }
    }
  }

  protected void paintButtonPressed(Graphics g, AbstractButton b)
  {
    //DO NOTHING
  }
}