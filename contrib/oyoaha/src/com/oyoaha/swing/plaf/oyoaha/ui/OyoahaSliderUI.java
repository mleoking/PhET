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

public class OyoahaSliderUI extends MetalSliderUI
{
  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaSliderUI();
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

  public void update(Graphics g, JComponent c)
  {
    Rectangle r = OyoahaUtilities.getFullRect(c);
    OyoahaUtilities.paintBackground(g, c, r.x, r.y, r.width, r.height, OyoahaUtilities.getBackground(c), OyoahaUtilities.UNSELECTED_ENABLED);
    paint(g,c);
  }

  protected boolean drawInverted()
  {
    if(slider.getOrientation()==JSlider.HORIZONTAL)
    {
      if(OyoahaUtilities.isLeftToRight(slider))
      {
        return slider.getInverted();
      }
      else
      {
        return !slider.getInverted();
      }
    }
    else
    {
      return slider.getInverted();
    }
  }

  public void paintFocus(Graphics g)
  {
    boolean leftToRight = OyoahaUtilities.isLeftToRight(slider);
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    g.translate( trackRect.x, trackRect.y );

    int trackLeft = 0;
    int trackTop = 0;
    int trackRight = 0;
    int trackBottom = 0;

    if(slider.getOrientation() == JSlider.HORIZONTAL)
    {
      trackBottom = (trackRect.height - 1) - getThumbOverhang();
      trackTop = trackBottom - (getTrackWidth() - 1);
      trackRight = trackRect.width - 1;
    }
    else
    {
      if (leftToRight)
      {
        trackLeft = (trackRect.width - getThumbOverhang()) - getTrackWidth();
        trackRight = (trackRect.width - getThumbOverhang()) - 1;
      }
      else
      {
        trackLeft = getThumbOverhang();
        trackRight = getThumbOverhang() + getTrackWidth() - 1;
      }

      trackBottom = trackRect.height - 1;
    }

    g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.FOCUS));
    //g.drawRect(trackLeft+1, trackTop+1, (trackRight-trackLeft)-2, (trackBottom-trackTop)-2);

    if (slider.getOrientation() == JSlider.HORIZONTAL)
    {
        //g.drawLine(trackLeft, trackTop-2, trackRight, trackTop-2);
        g.drawLine(trackLeft, trackBottom+2, trackRight, trackBottom+2);
    }
    else
    {
        if (!leftToRight)
        g.drawLine(trackLeft-2, trackTop, trackLeft-2, trackBottom);
        else
        g.drawLine(trackRight+2, trackTop, trackRight+2, trackBottom);

    }

    g.translate(-trackRect.x, -trackRect.y);
  }

  protected Dimension getThumbSize()
  {
      Dimension size = new Dimension();

      if ( slider.getOrientation() == JSlider.VERTICAL )
      {
          size.width = vertThumbIcon.getIconWidth()+1;
          size.height = vertThumbIcon.getIconHeight();
      }
      else
      {
          size.width = horizThumbIcon.getIconWidth();
          size.height = horizThumbIcon.getIconHeight()+1;
      }

      return size;
  }

  public void paintTrack(Graphics g)
  {
    boolean leftToRight = OyoahaUtilities.isLeftToRight(slider);
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    Color trackColor = !slider.isEnabled() ? scheme.getDisabled() : slider.getForeground();

    g.translate( trackRect.x, trackRect.y );

    int trackLeft = 0;
    int trackTop = 0;
    int trackRight = 0;
    int trackBottom = 0;

    if(slider.getOrientation() == JSlider.HORIZONTAL)
    {
      trackBottom = (trackRect.height - 1) - getThumbOverhang();
      trackTop = trackBottom - (getTrackWidth() - 1);
      trackRight = trackRect.width - 1;
    }
    else
    {
      if (leftToRight)
      {
        trackLeft = (trackRect.width - getThumbOverhang()) - getTrackWidth();
        trackRight = (trackRect.width - getThumbOverhang()) - 1;
      }
      else
      {
        trackLeft = getThumbOverhang();
        trackRight = getThumbOverhang() + getTrackWidth() - 1;
      }

      trackBottom = trackRect.height - 1;
    }

    Shape s = OyoahaUtilities.normalizeClip(g, trackLeft, trackTop, trackRight-trackLeft, trackBottom-trackTop);

    OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("SliderTrack");
    Color color = slider.getBackground();

    if(o!=null && color instanceof UIResource)
    {
      o.paintBackground(g, slider, trackLeft, trackTop, trackRight-trackLeft, trackBottom-trackTop, OyoahaUtilities.getStatus(slider));
    }
    else
    {
      OyoahaUtilities.paintColorBackground(g, slider, trackLeft, trackTop, trackRight-trackLeft, trackBottom-trackTop, color, OyoahaUtilities.getStatus(slider));
    }

    g.setClip(s);

    if (slider.isEnabled())
    {
      /*g.setColor(scheme.getBlack());
      g.drawRect(trackLeft, trackTop, (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1);

      if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
      {
          g.setColor(scheme.getWhite());
          g.drawLine(trackLeft + 1, trackBottom, trackRight, trackBottom);
          g.drawLine(trackRight, trackTop + 1, trackRight, trackBottom);

          g.setColor(scheme.getGray());
          g.drawLine(trackLeft + 1, trackTop + 1, trackRight - 2, trackTop + 1);
          g.drawLine(trackLeft + 1, trackTop + 1, trackLeft + 1, trackBottom - 2);
      }*/
      
      
GradientPaint gp;
        
/*gp = new GradientPaint(0, trackBottom-3, new Color(0,0,0,0), 0, trackBottom, new Color(0,0,0,100), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(trackLeft, trackBottom-3, trackRight-trackLeft, 3);*/

gp = new GradientPaint(0, trackTop+3, new Color(0,0,0,0), 0, trackTop, new Color(0,0,0,100), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(trackLeft, trackTop, trackRight-trackLeft, 3);



gp = new GradientPaint(trackLeft, 0, new Color(0,0,0,100), trackLeft+3, 0, new Color(0,0,0,0), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(trackLeft, trackTop, 3, trackBottom-trackTop);

/*gp = new GradientPaint(trackRight-3, 0, new Color(0,0,0,0), trackRight, 0, new Color(0,0,0,100), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(trackRight-3, trackTop, 3, trackBottom-trackTop);*/ 





      
      
      
      g.setColor(scheme.getWhite());
      g.drawLine(trackLeft, trackBottom, trackRight, trackBottom);
      g.drawLine(trackRight, trackTop, trackRight, trackBottom);

      /*g.setColor(scheme.getGray());
      g.drawLine(trackLeft+1, trackTop+1, trackRight-1, trackTop+1);
      g.drawLine(trackLeft+1, trackTop+1, trackLeft+1, trackBottom-1);
      
g.drawLine(trackLeft-1, trackBottom-1, trackRight-1, trackBottom-1);
g.drawLine(trackRight-1, trackTop-1, trackRight-1, trackBottom-1);*/     
      
      g.setColor(scheme.getBlack());
      g.drawLine(trackLeft, trackTop, trackRight-1, trackTop);
      g.drawLine(trackLeft, trackTop, trackLeft, trackBottom-1);
    }
    else
    {
      g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_DISABLED));
      g.drawRect(trackLeft, trackTop, (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1 );
    }

    if (filledSlider)
    {
      int middleOfThumb = 0;
      int fillTop = 0;
      int fillLeft = 0;
      int fillBottom = 0;
      int fillRight = 0;

      if (slider.getOrientation() == JSlider.HORIZONTAL)
      {
        middleOfThumb = thumbRect.x + (thumbRect.width / 2);
        middleOfThumb -= trackRect.x;
        //fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
        //fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;
fillTop = trackTop+1;
fillBottom = trackBottom-1;
        
        if (!drawInverted())
        {
          //fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
fillLeft = trackLeft+1;          
          fillRight = middleOfThumb;
        }
        else
        {
          fillLeft = middleOfThumb;
//fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;
fillRight = trackRight-1;
        }
      }
      else
      {
        middleOfThumb = thumbRect.y + (thumbRect.height / 2);
        middleOfThumb -= trackRect.y;
        //fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
        //fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;

fillLeft = trackLeft+1;
fillRight = trackRight-1;

        if (!drawInverted())
        {
          fillTop = middleOfThumb;
          //fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;
fillBottom = trackBottom-1;          
        }
        else
        {
          //fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
fillTop = trackTop+1;          
          fillBottom = middleOfThumb;
        }
      }

      if(slider.isEnabled())
      {
        //g.setColor(slider.getBackground());
//g.setColor(Color.blue);
//g.drawLine(fillLeft, fillTop, fillRight, fillTop);
//g.drawLine(fillLeft, fillTop, fillLeft, fillBottom);

//OyoahaUtilities.setAlphaChannel(g,slider,0.5f);

Color tcolor = slider.getBackground();

if(tcolor instanceof UIResource)
g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.SELECTED_ENABLED));
else
g.setColor(tcolor);

g.fillRect(fillLeft, fillTop, fillRight-fillLeft, fillBottom-fillTop);


g.setColor(scheme.getWhite());
g.drawLine(fillLeft, fillTop, fillRight, fillTop);
g.drawLine(fillLeft, fillTop, fillLeft, fillBottom);

g.setColor(scheme.getBlack());
g.drawLine(fillLeft, fillBottom, fillRight, fillBottom);

//OyoahaUtilities.setAlphaChannel(g,slider,1.0f);
//GradientPaint gp;
        
//gp = new GradientPaint(0, fillBottom, new Color(0,0,0,0), 0, fillBottom-3, new Color(0,0,0,100), false);
//((Graphics2D)g).setPaint(gp);
//g.fillRect(fillLeft, fillBottom-3, fillRight-fillLeft, 3);

//gp = new GradientPaint(trackLeft, 0, new Color(0,0,0,100), trackLeft+3, 0, new Color(0,0,0,0), false);
//((Graphics2D)g).setPaint(gp);
//g.fillRect(trackLeft, trackTop, 3, trackBottom-trackTop);







      }
      else
      {
        g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_DISABLED));
        g.fillRect(fillLeft, fillTop, fillRight - fillLeft, trackBottom - trackTop);
      }
    }

    g.translate(-trackRect.x, -trackRect.y);
  }
  
  /*public void paintLabels(Graphics g) 
  {
    Rectangle labelBounds = labelRect;

        java.util.Dictionary dictionary = (java.util.Dictionary)slider.getLabelTable();
        
        if (dictionary != null) 
        {
            java.util.Enumeration keys = dictionary.keys();
            
            while (keys.hasMoreElements()) 
            {
                Integer key = (Integer)keys.nextElement();
                Component label = (Component)dictionary.get(key);
                
                label.setEnabled(slider.isEnabled());

                if (slider.getOrientation() == JSlider.HORIZONTAL) 
                {
		            g.translate( 0, labelBounds.y );
                    paintHorizontalLabel(g, key.intValue(), label);
		            g.translate( 0, -labelBounds.y );
                }
                else 
                {
		            int offset = 0;
                    
		            if(!OyoahaUtilities.isLeftToRight(slider)) 
                    {
		                offset = labelBounds.width - label.getPreferredSize().width;
		            }
                    
		            g.translate( labelBounds.x + offset, 0 );
                    paintVerticalLabel( g, key.intValue(), label);
		            g.translate( -labelBounds.x - offset, 0 );
                }
            }
        }
  }*/
  
    protected void paintHorizontalLabel(Graphics g, int value, Component label) 
    {
        label.setEnabled(slider.isEnabled());
        super.paintHorizontalLabel(g, value, label);
    }

    protected void paintVerticalLabel(Graphics g, int value, Component label) 
    {
        label.setEnabled(slider.isEnabled());
        super.paintVerticalLabel(g, value, label);
    }
  
  public void paintTicks(Graphics g)
  {
    Rectangle tickBounds = tickRect;
    int i;
    int maj, min, max;
    int w = tickBounds.width;
    int h = tickBounds.height;
    int centerEffect, tickHeight;

    //g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
    g.setColor(slider.isEnabled() ? OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK) : OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));

    maj = slider.getMajorTickSpacing();
    min = slider.getMinorTickSpacing();

    if ( slider.getOrientation() == JSlider.HORIZONTAL )
    {
       g.translate( 0, tickBounds.y);

        int value = slider.getMinimum();
        int xPos = 0;

        if ( slider.getMinorTickSpacing() > 0 )
        {
            while ( value <= slider.getMaximum() )
            {
                xPos = xPositionForValue( value );
                paintMinorTickForHorizSlider( g, tickBounds, xPos );
                value += slider.getMinorTickSpacing();
            }
        }

        if ( slider.getMajorTickSpacing() > 0 )
        {
            value = slider.getMinimum();

            while ( value <= slider.getMaximum() )
            {
                xPos = xPositionForValue( value );
                paintMajorTickForHorizSlider( g, tickBounds, xPos );
                value += slider.getMajorTickSpacing();
            }
        }

        g.translate( 0, -tickBounds.y);
    }
    else
    {
       g.translate(tickBounds.x, 0);

        int value = slider.getMinimum();
        int yPos = 0;

        if ( slider.getMinorTickSpacing() > 0 )
        {
            int offset = 0;
            if(!OyoahaUtilities.isLeftToRight(slider))
            {
                offset = tickBounds.width - tickBounds.width / 2;
                g.translate(offset, 0);
            }

            while ( value <= slider.getMaximum() )
            {
                yPos = yPositionForValue( value );
                paintMinorTickForVertSlider( g, tickBounds, yPos );
                value += slider.getMinorTickSpacing();
            }

            if(!OyoahaUtilities.isLeftToRight(slider))
            {
                g.translate(-offset, 0);
            }
        }

        if ( slider.getMajorTickSpacing() > 0 )
        {
            value = slider.getMinimum();
            if(!OyoahaUtilities.isLeftToRight(slider))
            {
                g.translate(2, 0);
            }

            while ( value <= slider.getMaximum() )
            {
                yPos = yPositionForValue( value );
                paintMajorTickForVertSlider( g, tickBounds, yPos );
                value += slider.getMajorTickSpacing();
            }

            if(!OyoahaUtilities.isLeftToRight(slider))
            {
                g.translate(-2, 0);
            }
        }
        g.translate(-tickBounds.x, 0);
    }
  }

    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x )
    {
      g.setColor(slider.isEnabled() ? OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK) : OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));
      g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER + (tickLength / 2));
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x )
    {
      g.setColor(slider.isEnabled() ? OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK) : OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));
      g.drawLine( x, TICK_BUFFER , x, TICK_BUFFER + (tickLength - 1) );
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y )
    {
      g.setColor(slider.isEnabled() ? OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK) : OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));

      if (OyoahaUtilities.isLeftToRight(slider))
      {
        g.drawLine( TICK_BUFFER, y, TICK_BUFFER + (tickLength / 2), y );
      }
      else
      {
        g.drawLine( 0, y, tickLength/2, y );
      }
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y )
    {
      g.setColor(slider.isEnabled() ? OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK) : OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));

      if (OyoahaUtilities.isLeftToRight(slider))
      {
        g.drawLine( TICK_BUFFER, y, TICK_BUFFER + tickLength, y );
      }
      else
      {
        g.drawLine( 0, y, tickLength, y );
      }
    }
}