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
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaListCellRenderer extends JLabel implements ListCellRenderer, Serializable
{
  protected boolean selected;
  protected boolean hasFocus;
  protected boolean drawsFocusBorderAroundIcon;
  protected boolean isLeftToRight = true;
  protected JList list;
  protected Border border;

  public OyoahaListCellRenderer()
  {
    setHorizontalAlignment(JLabel.LEFT);

    Object value = UIManager.get("List.drawsFocusBorderAroundIcon");
    drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).booleanValue());

    border = UIManager.getBorder("List.selectionBorder");
    setOpaque(false);
  }

  public int getStatus()
  {
    if(selected)
    return OyoahaUtilities.SELECTED_ENABLED;

    return OyoahaUtilities.UNSELECTED_ENABLED;
  }

  public boolean hasFocus()
  {
    return hasFocus;
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus)
  {
    this.list = list;
    this.selected = selected;
    this.hasFocus = hasFocus;
    this.isLeftToRight = OyoahaUtilities.isLeftToRight(list);

    if(selected)
    {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else
    {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    if (value instanceof Icon)
    {
      setIcon((Icon)value);
      setText("");
    }
    else
    {
      setIcon(null);
      setText((value == null) ? "" : value.toString());
    }

    setOpaque(false);
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    setBorder(border);

    return this;
  }

  protected void setUI(ComponentUI ui) {}
  public void validate() {}
  public void revalidate() {}
  public void repaint(long tm, int x, int y, int width, int height) {}
  public void repaint(Rectangle r){}

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
  {
    if (propertyName=="text")
    super.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
  public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
  public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
  public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
  public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
  public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
  public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

//- - - - - - - - - - - - - - - - - -

  public void paint(Graphics g)
  {
    if(list==null)
    return;

    Color bColor;

    if(selected)
    {
      bColor = list.getSelectionBackground();
    }
    else
    {
      bColor = list.getBackground();
    }

    g.setColor(bColor);

    int imageOffset;

    if(drawsFocusBorderAroundIcon)
    {
      imageOffset = 0;
    }
    else
    {
      imageOffset = getLabelStart();
    }

    if(selected)
    {
      OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("List.Renderer");

      if(isLeftToRight)
      {
        Shape s = OyoahaUtilities.normalizeClip(g, imageOffset, 0, getWidth() - 1 - imageOffset, getHeight());

        if(o!=null && bColor instanceof UIResource)
        {
          o.paintBackground(g, this, imageOffset, 0, getWidth() - 1 - imageOffset, getHeight(), OyoahaUtilities.SELECTED_ENABLED);
        }
        else
        {
          g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight());
        }

        g.setClip(s);
      }
      else
      {
        Shape s = OyoahaUtilities.normalizeClip(g, 0, 0, getWidth() - 1 - imageOffset, getHeight());

        if(o!=null && bColor instanceof UIResource)
        {
          o.paintBackground(g, this, 0, 0, getWidth() - 1 - imageOffset, getHeight(), OyoahaUtilities.SELECTED_ENABLED);
        }
        else
        {
          g.fillRect(0, 0, getWidth() - 1 - imageOffset, getHeight());
        }

        g.setClip(s);
      }
    }

    super.paint(g);
  }

  protected int getLabelStart()
  {
    Icon currentI = getIcon();

    if(currentI != null && getText() != null)
    {
      return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
    }

    return 0;
  }

  protected int getBorderStart()
  {
    Icon currentI = getIcon();

    if(currentI != null && getText() != null)
    {
      return currentI.getIconWidth() + Math.max(0, (getIconTextGap()/2));
    }

    return 0;
  }

  protected void paintBorder(Graphics g)
  {
    Border border = getBorder();

    if(border!=null)
    {
      int imageOffset;

      if(drawsFocusBorderAroundIcon)
      {
        imageOffset = 0;
      }
      else
      {
        imageOffset = getBorderStart();
      }

      if(isLeftToRight)
      {
        border.paintBorder(this, g, imageOffset, 0, getWidth()-1-imageOffset, getHeight()-1);
      }
      else
      {
        border.paintBorder(this, g, 0, 0, getWidth()-1-imageOffset, getHeight()-1);
      }
    }
  }

  public Dimension getPreferredSize()
  {
    Dimension retDimension = super.getPreferredSize();

    if(retDimension != null)
    {
      retDimension = new Dimension(retDimension.width + 3, retDimension.height);
    }

    return retDimension;
  }
}