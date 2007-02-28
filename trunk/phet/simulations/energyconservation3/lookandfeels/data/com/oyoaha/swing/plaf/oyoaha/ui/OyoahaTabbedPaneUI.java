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
import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTabbedPaneUI extends BasicTabbedPaneUI//MetalTabbedPaneUI
{
  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaTabbedPaneUI();
  }

    /*protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected)
    {
      Rectangle tabRect = rects[tabIndex];
      int nudge = 0;
      switch(tabPlacement)
      {
        case LEFT:
            nudge = isSelected? -1 : 1;
        break;
        case RIGHT:
            nudge = isSelected? 1 : -1;
        break;
        case BOTTOM:
        case TOP:
        default:
            nudge = tabRect.width % 2;
      }

      return nudge;
  }*/

  /*protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected)
  {
      Rectangle tabRect = rects[tabIndex];
      int nudge = 0;

      switch(tabPlacement)
      {
         case BOTTOM:
            nudge = isSelected? 1 : -1;
        break;
        case LEFT:
        case RIGHT:
            nudge = tabRect.height % 2;
        break;
        case TOP:
        default:
            nudge = isSelected? -1 : 1;;
      }

      return nudge;
  }*/

 public void update(Graphics g, JComponent c)
  {
    //OyoahaUtilities.paintBackground(g,c);
    
    /*g.setColor(Color.red);
    Dimension d = c.getSize();
    g.fillRect(0,0,d.width,d.height);*/
    
    /*GradientPaint gp = new GradientPaint(0, 20, new Color(0,0,0,0), 0, 35, new Color(0,0,0,100), false);
    ((Graphics2D)g).setPaint(gp);
    Dimension d = c.getSize();
    g.fillRect(0,0,d.width,35);*/
    
    paint(g,c);
  }

  protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
  {
    if(isSelected)
    {
      Insets insets = getContentBorderInsets(tabPlacement);
      
      switch(tabPlacement)
      {
        case TOP:
                h+=insets.top;
        break;
        case BOTTOM:
                y-=insets.bottom;
                h+=insets.bottom;
        break;
        case LEFT:
                w+=insets.left;
        break;
        case RIGHT:
                x-=insets.right;
                w+=insets.right;
        break;
      }
    }

    Color color = tabPane.getBackgroundAt(tabIndex);
    OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject(tabPane.getComponentAt(tabIndex));
    //OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("TabbedPane");

    Shape s = OyoahaUtilities.normalizeClip(g, x, y, w, h);

    if(!(color instanceof UIResource))
    {
      g.setColor(color);
      g.fillRect(x, y, w, h);
    }
    else
    if(o!=null)
    {
      if(isSelected)
      {
        color = OyoahaUtilities.getBackground(tabPane.getComponentAt(tabIndex));
        
        if (!(color instanceof UIResource))
        {
            g.setColor(color);
            g.fillRect(x, y, w, h);
        }
        else
        {
            o.paintBackground(g, tabPane, x, y, w, h, OyoahaUtilities.UNSELECTED_ENABLED);
        }
      }
    }
    else
    {
      if(isSelected)
      {
        g.setColor(OyoahaUtilities.getBackground(tabPane.getComponentAt(tabIndex)));
        g.fillRect(x, y, w, h);
      }
    }

    g.setClip(s);
  }
  
  protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected ) 
  {
      g.setColor(lightHighlight);  

      switch (tabPlacement) 
      {
        case LEFT:
            g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight
            g.drawLine(x, y+2, x, y+h-3); // left highlight
            g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
            g.drawLine(x+2, y, x+w-1, y); // top highlight

            g.setColor(shadow);
            g.drawLine(x+2, y+h-2, x+w-1, y+h-2); // bottom shadow

            g.setColor(darkShadow);
            g.drawLine(x+2, y+h-1, x+w-1, y+h-1); // bottom dark shadow
            break;
        case RIGHT:
            g.drawLine(x, y, x+w-3, y); // top highlight

            g.setColor(shadow);
            g.drawLine(x, y+h-2, x+w-3, y+h-2); // bottom shadow
            g.drawLine(x+w-2, y+2, x+w-2, y+h-3); // right shadow

            g.setColor(darkShadow);
            g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right dark shadow
            g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
            g.drawLine(x+w-1, y+2, x+w-1, y+h-3); // right dark shadow
            g.drawLine(x, y+h-1, x+w-3, y+h-1); // bottom dark shadow
            break;              
        case BOTTOM:
            g.drawLine(x, y, x, y+h-3); // left highlight
            g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight

            g.setColor(shadow);
            g.drawLine(x+2, y+h-2, x+w-3, y+h-2); // bottom shadow
            g.drawLine(x+w-2, y, x+w-2, y+h-3); // right shadow

            g.setColor(darkShadow);
            g.drawLine(x+2, y+h-1, x+w-3, y+h-1); // bottom dark shadow
            g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
            g.drawLine(x+w-1, y, x+w-1, y+h-3); // right dark shadow
            break;
        case TOP:
        default:           
            
            if (isSelected)
            {
                g.drawLine(x, y, x, y+h); // left highlight
                g.drawLine(x, y, x+w-1, y); // top highlight              
                g.setColor(shadow);  
                g.drawLine(x+w-2, y+1, x+w-2, y+h-1); // right shadow
                g.setColor(darkShadow); 
                g.drawLine(x+w-1, y, x+w-1, y+h-1); // right dark-shadow
                
                
                GradientPaint gp;
                
                if(tabIndex<tabPane.getTabCount()-1)
                {
                    gp = new GradientPaint(x+w-1, 0, new Color(0,0,0,100), x+w+5, 0, new Color(0,0,0,0), false);
                    ((Graphics2D)g).setPaint(gp);
                    g.fillRect(x+w-1, y+4, 6, y+h-5);
                }
                
/*if(tabIndex>0)
{
    gp = new GradientPaint(x-6, 0, new Color(0,0,0,0), x, 0, new Color(0,0,0,100), false);
    ((Graphics2D)g).setPaint(gp);
    g.fillRect(x-6, y+4, 6, y+h-5);
}*/
            }
            else
            {
                g.drawLine(x, y, x, y+h); // left highlight
                g.drawLine(x, y, x+w-1, y); // top highlight 
                g.setColor(shadow); 
                g.drawLine(x+w-1, y, x+w-1, y+h); // right shadow
                

/*GradientPaint gp = new GradientPaint(0, y+h-6, new Color(0,0,0,0), 0, y+h, new Color(0,0,0,100), false);
((Graphics2D)g).setPaint(gp);
g.fillRect(x, y+h-6, w, 6);*/
            }
      }
  }

  /*protected void paintTopTabBorder(int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected)
  {
    int currentRun = getRunForTab( tabPane.getTabCount(), tabIndex );
    int lastIndex = lastTabInRun( tabPane.getTabCount(), currentRun );
    int firstIndex = tabRuns[ currentRun ];

    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();
    //Color color = (tabPane.isEnabled())? scheme.getBlack() : scheme.getGray();

    if(isSelected)
    {
      //top
      g.setColor(scheme.getWhite());
      g.drawLine(x,y-2,x+w,y-2);
      //g.setColor(color);
      //g.drawLine(x+1,y,x+w,y);

      //right
      //g.setColor(color);
      
      g.setColor(scheme.getGray());
      g.drawLine(x+w-1,y-1,x+w-1,y+h);
      g.setColor(scheme.getBlack());
      g.drawLine(x+w,y-2,x+w,y+h+1);

      //left
      g.setColor(scheme.getWhite());
      g.drawLine(x,y-1,x,y+h);
      //g.setColor(color);
      //g.drawLine(x+1,y,x+1,y+h);
    }
    else
    {
      g.setColor(scheme.getGray());

      //top
      g.drawLine(x,y+1,x+w,y+1);

      //right
      g.drawLine(x+w,y+1,x+w,y+h);

      //left
      g.drawLine(x,y+1,x,y+h);
    }
  }

  /*protected void paintBottomTabBorder( int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected)
  {
    int currentRun = getRunForTab( tabPane.getTabCount(), tabIndex );
    int lastIndex = lastTabInRun( tabPane.getTabCount(), currentRun );
    int firstIndex = tabRuns[ currentRun ];

    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();
    Color color = (tabPane.isEnabled())? scheme.getBlack() : scheme.getGray();

    if(isSelected)
    {
      //right
      g.setColor(color);
      g.drawLine(x+w-1,y-2,x+w-1,y+h);
      g.setColor(scheme.getGray());
      g.drawLine(x+w,y-2,x+w,y+h+1);

      //left
      g.setColor(scheme.getWhite());
      g.drawLine(x,y-2,x,y+h);
      g.setColor(color);
      g.drawLine(x+1,y-2,x+1,y+h+1);

      //bottom
      g.setColor(color);
      g.drawLine(x+1,y+h,x+w-1,y+h);
      g.setColor(scheme.getGray());
      g.drawLine(x,y+h+1,x+w-1,y+h+1);
    }
    else
    {
      g.setColor(scheme.getGray());

      //right
      g.drawLine(x+w,y,x+w,y+(h-1));

      //left
      g.drawLine(x,y,x,y+(h-1));

      //bottom
      g.drawLine(x,y+(h-1),x+w,y+(h-1));
    }
  }

  protected void paintRightTabBorder( int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected)
  {
    int currentRun = getRunForTab( tabPane.getTabCount(), tabIndex );
    int lastIndex = lastTabInRun( tabPane.getTabCount(), currentRun );
    int firstIndex = tabRuns[ currentRun ];

    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();
    Color color = (tabPane.isEnabled())? scheme.getBlack() : scheme.getGray();

    if(isSelected)
    {
      //top
      g.setColor(scheme.getWhite());
      g.drawLine(x-2,y-1,x+w,y-1);
      g.setColor(color);
      g.drawLine(x-2,y,x+w,y);

      //right
      g.setColor(color);
      g.drawLine(x+w,y+1,x+w,y+h);
      g.setColor(scheme.getGray());
      g.drawLine(x+w+1,y-1,x+w+1,y+h);

      //bottom
      g.setColor(color);
      g.drawLine(x-2,y+h,x+w,y+h);
      g.setColor(scheme.getGray());
      g.drawLine(x-2,y+h+1,x+w+1,y+h+1);
    }
    else
    {
      g.setColor(scheme.getGray());

      //top
      g.drawLine(x,y,x+w-1,y);

      //bottom
      g.drawLine(x,y+h,x+w-1,y+h);

      //right
      g.drawLine(x+w-1,y,x+w-1,y+h);
    }
  }

  protected void paintLeftTabBorder( int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected)
  {
    int currentRun = getRunForTab( tabPane.getTabCount(), tabIndex );
    int lastIndex = lastTabInRun( tabPane.getTabCount(), currentRun );
    int firstIndex = tabRuns[ currentRun ];

    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();
    Color color = (tabPane.isEnabled())? scheme.getBlack() : scheme.getGray();

    if(isSelected)
    {
      //top
      g.setColor(scheme.getWhite());
      g.drawLine(x-1,y-1,x+w,y-1);
      g.setColor(color);
      g.drawLine(x,y,x+w,y);

      //bottom
      g.setColor(color);
      g.drawLine(x,y+h,x+w,y+h);
      g.setColor(scheme.getGray());
      g.drawLine(x-1,y+h+1,x+w-1,y+h+1);

      //left
      g.setColor(scheme.getWhite());
      g.drawLine(x-1,y-1,x-1,y+h);
      g.setColor(color);
      g.drawLine(x,y,x,y+h);
    }
    else
    {
      g.setColor(scheme.getGray());

      //top
      g.drawLine(x+1,y,x+w,y);

      //bottom
      g.drawLine(x+1,y+h,x+w,y+h);

      //left
      g.drawLine(x+1,y,x+1,y+h);
    }
  }*/

  protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected)
  {
    if(tabPane.hasFocus() && isSelected)
    {
      paintFocus(g, tabPane.getTitleAt(tabIndex), (getIconForTab(tabIndex)!=null)? true : false, rects[tabIndex], textRect, iconRect);
      
      /*Rectangle tabRect = rects[tabIndex];
      g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.FOCUS));

      switch(tabPlacement)
      {
        case RIGHT:
        g.drawLine(tabRect.x-2, tabRect.y+1, tabRect.x+tabRect.width-2, tabRect.y+1);
        g.drawLine(tabRect.x+tabRect.width-1, tabRect.y+1, tabRect.x+tabRect.width-1, tabRect.y+tabRect.height-1);
        g.drawLine(tabRect.x-2, tabRect.y+tabRect.height-1, tabRect.x+tabRect.width-2, tabRect.y+tabRect.height-1);
        break;
        case BOTTOM:
        g.drawLine(tabRect.x+2, tabRect.y-2, tabRect.x+2, tabRect.y+tabRect.height-1);
        g.drawLine(tabRect.x+2, tabRect.y+tabRect.height-1, tabRect.x+tabRect.width-2, tabRect.y+tabRect.height-1);
        g.drawLine(tabRect.x+tabRect.width-2, tabRect.y-2, tabRect.x+tabRect.width-2, tabRect.y+tabRect.height-1);
        break;
        case TOP:
        g.drawLine(tabRect.x+1, tabRect.y+1, tabRect.x+1, tabRect.y+tabRect.height);
        g.drawLine(tabRect.x+2, tabRect.y+1, tabRect.x+tabRect.width-2, tabRect.y+1);
        g.drawLine(tabRect.x+tabRect.width-2, tabRect.y+1, tabRect.x+tabRect.width-2, tabRect.y+tabRect.height);
        break;
        case LEFT:
        g.drawLine(tabRect.x+2, tabRect.y+1, tabRect.x+tabRect.width, tabRect.y+1);
        g.drawLine(tabRect.x+1, tabRect.y+1, tabRect.x+1, tabRect.y+tabRect.height-1);
        g.drawLine(tabRect.x+2, tabRect.y+tabRect.height-1, tabRect.x+tabRect.width, tabRect.y+tabRect.height-1);
      }*/
      
      
    }
  }
  
  protected void paintFocus(Graphics g, String text, boolean isIcon, Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
  {
    Rectangle focusRect = new Rectangle();

    if (text!=null)
    {
        String t = text.toLowerCase();
        t = t.trim();
        
        //workaround the textRect if wrong with HTML component
        //this workaround just doesn't paint the focus in this case
        if (t.startsWith("<html>") && t.endsWith("</html>"))
        {
            return;
        }
    }
    
    if(text!=null && !text.equals(""))
    {
      if (!isIcon)
      {
        focusRect.setBounds(textRect);
      }
      else
      {
        focusRect.setBounds(iconRect.union(textRect));
      }
    }
    else
    if(isIcon)
    {
      focusRect.setBounds(iconRect);
    }
    else
    {
      focusRect.setBounds(viewRect);
    }

    g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.FOCUS));

    int h = focusRect.y+focusRect.height-1;
    g.drawLine(focusRect.x, focusRect.y, focusRect.x+focusRect.width-1, focusRect.y);
    g.drawLine(focusRect.x, h, focusRect.x+focusRect.width-1, h);
  }

  //paint the gap...
  /*protected void paintContentBorderTopEdge( Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h )
  {
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if (tabPlacement != TOP || selectedIndex < 0 || (rects[selectedIndex].y + rects[selectedIndex].height + 1 < y))
    {
      g.setColor(scheme.getWhite());
      g.drawLine(x, y, x+w-1, y);
    }
    else
    {
      Rectangle selRect = rects[selectedIndex];

      g.setColor(scheme.getWhite());
      g.drawLine(x, y, selRect.x, y);

      if(selRect.x+selRect.width<x+w-1)
      {
        g.setColor(scheme.getWhite());
        g.drawLine(selRect.x + selRect.width, y, x+w-1, y);
      }
      else
      {
        g.setColor(scheme.getWhite());
        g.drawLine(x+w-1, y, x+w-1, y);
      }
    }
  }*/

  /*protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
  {
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if (tabPlacement != BOTTOM || selectedIndex < 0 || (rects[selectedIndex].y - 1 > h))
    {
      g.setColor(scheme.getBlack());
      g.drawLine(x, y+h-2, x+w-1, y+h-2);
      g.setColor(scheme.getGray());
      g.drawLine(x, y+h-1, x+w-1, y+h-1);
    }
    else
    {
      Rectangle selRect = rects[selectedIndex];

      g.setColor(scheme.getBlack());
      g.drawLine(x, y+h-2, selRect.x, y+h-2);
      g.setColor(scheme.getGray());
      g.drawLine(x, y+h-1, selRect.x, y+h-1);

      if (selRect.x+selRect.width<x+w-1)
      {
        g.setColor(scheme.getBlack());
        g.drawLine(selRect.x+selRect.width, y+h-2, x+w-1, y+h-2);
        g.setColor(scheme.getGray());
        g.drawLine(selRect.x+selRect.width, y+h-1, x+w-1, y+h-1);
      }
      else
      {
        g.setColor(scheme.getBlack());
        g.drawLine(x+w-1, y+h-2, x+w-1, y+h-2);
        g.setColor(scheme.getGray());
        g.drawLine(x+w-1, y+h-1, x+w-1, y+h-1);
      }
    }
  }*/

  /*protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
  {
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if (tabPlacement != LEFT || selectedIndex < 0 || (rects[selectedIndex].x + rects[selectedIndex].width + 1< x))
    {
      g.setColor(scheme.getWhite());
      g.drawLine(x, y, x, y+h-2);
    }
    else
    {
      Rectangle selRect = rects[selectedIndex];

      g.setColor(scheme.getWhite());
      g.drawLine(x, y, x, selRect.y-1);

      if(selRect.y+selRect.height<y+h-2)
      {
        g.drawLine(x, selRect.y+selRect.height+1, x, y+h-2);
      }
      else
      {
        g.drawLine(x, y+h-2, x, y+h-2);
      }
    }
  }*/

  /*protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
  {
    OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

    if (tabPlacement != RIGHT || selectedIndex < 0 || rects[selectedIndex].x - 1 > w)
    {
      g.setColor(scheme.getBlack());
      g.drawLine(x+w-2, y+1, x+w-2, y+h-2);
      g.setColor(scheme.getGray());
      g.drawLine(x+w-1, y, x+w-1, y+h-2);
    }
    else
    {
      Rectangle selRect = rects[selectedIndex];

      g.setColor(scheme.getBlack());
      g.drawLine(x+w-2, y+1, x+w-2, selRect.y-1);
      g.setColor(scheme.getGray());
      g.drawLine(x+w-1, y, x+w-1, selRect.y-2);

      if (selRect.y + selRect.height < y + h - 2)
      {
        g.setColor(scheme.getBlack());
        g.drawLine(x+w-2, selRect.y+selRect.height+1, x+w-2, y+h-2);
        g.setColor(scheme.getGray());
        g.drawLine(x+w-1, selRect.y+selRect.height+1, x+w-1, y+h-2);
      }
      else
      {
        g.setColor(scheme.getBlack());
        g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2);
        g.setColor(scheme.getGray());
        g.drawLine(x+w-1, y+h-2, x+w-1, y+h-2);
      }
    }
  }*/
  
  protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) 
  {
    if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex))
    {
        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    }
    else
    {
        String t = title.toLowerCase();
        t = t.trim();
        
        if (t.startsWith("<html>") && t.endsWith("</html>"))
        {
            super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
        }
        else
        {
            g.setFont(font);
            
            Color c = tabPane.getBackgroundAt(tabIndex);
            
            if(!isSelected && c instanceof UIResource)
            g.setColor(c);
            else
            g.setColor(c.darker());
                
	        g.drawString(title,textRect.x,textRect.y + metrics.getAscent());
        }
    }
  }
}