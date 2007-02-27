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

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import javax.swing.text.View;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTreeCellRenderer extends DefaultTreeCellRenderer
{
  transient protected Icon closedIcon;
  transient protected Icon leafIcon;
  transient protected Icon openIcon;
  
  transient protected Icon disabledClosedIcon;
  transient protected Icon disabledLeafIcon;
  transient protected Icon disabledOpenIcon;

  protected boolean selected;
  protected boolean hasFocus;
  protected boolean drawsFocusBorderAroundIcon;

  protected Color textSelectionColor;
  protected Color textNonSelectionColor;
  protected Color backgroundSelectionColor;
  protected Color backgroundNonSelectionColor;
  protected Color borderSelectionColor;

  protected boolean isLeftToRight = true;

  public OyoahaTreeCellRenderer()
  {
    setHorizontalAlignment(JLabel.LEFT);

    setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
    setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
    setOpenIcon(UIManager.getIcon("Tree.openIcon"));

    setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
    setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
    setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
    setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
    setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
    Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
    drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).booleanValue());

    setBorder(UIManager.getBorder("Tree.selectionBorder"));
    setOpaque(false);
  }

  public boolean hasFocus()
  {
    return hasFocus;
  }

  public int getStatus()
  {
    if(selected)
    return OyoahaUtilities.SELECTED_ENABLED;

    return OyoahaUtilities.UNSELECTED_ENABLED;
  }

  public Icon getDefaultOpenIcon()
  {
    return UIManager.getIcon("Tree.openIcon");
  }

  public Icon getDefaultClosedIcon()
  {
    return UIManager.getIcon("Tree.closedIcon");
  }

  public Icon getDefaultLeafIcon()
  {
    return UIManager.getIcon("Tree.leafIcon");
  }

  public void setOpenIcon(Icon newIcon)
  {
    openIcon = newIcon;
    disabledOpenIcon = null;
  }

  public Icon getOpenIcon()
  {
    return openIcon;
  }
  
  public Icon getDisabledOpenIcon() 
  {
    if(disabledOpenIcon == null) 
    {
        Icon defaultIcon = getOpenIcon();
    
        if(defaultIcon != null && defaultIcon instanceof ImageIcon) 
        {
            disabledOpenIcon = new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)defaultIcon).getImage()));
            return disabledOpenIcon;
        }
    }
    else
    {
        return disabledOpenIcon;
    }

    return getOpenIcon();
  }

  public void setClosedIcon(Icon newIcon)
  {
    closedIcon = newIcon;
    disabledClosedIcon = null;
  }

  public Icon getClosedIcon()
  {
    return closedIcon;
  }
  
  public Icon getDisabledClosedIcon() 
  {
    if(disabledClosedIcon == null) 
    {
        Icon defaultIcon = getClosedIcon();
    
        if(defaultIcon != null && defaultIcon instanceof ImageIcon) 
        {
            disabledClosedIcon = new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)defaultIcon).getImage()));
            return disabledClosedIcon;
        }
    }
    else
    {
        return disabledClosedIcon;
    }

    return getClosedIcon();
  }  

  public void setLeafIcon(Icon newIcon)
  {
    leafIcon = newIcon;
    disabledLeafIcon = null;
  }

  public Icon getLeafIcon()
  {
    return leafIcon;
  }
  
  public Icon getDisabledLeafIcon() 
  {
    if(disabledLeafIcon == null) 
    {
        Icon defaultIcon = getLeafIcon();
    
        if(defaultIcon != null && defaultIcon instanceof ImageIcon) 
        {
            disabledLeafIcon = new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)defaultIcon).getImage()));
            return disabledLeafIcon;
        }
    }
    else
    {
        return disabledLeafIcon;
    }

    return getLeafIcon();
  }

  public void setTextSelectionColor(Color newColor)
  {
    textSelectionColor = newColor;
  }

  public Color getTextSelectionColor()
  {
    return textSelectionColor;
  }

  public void setTextNonSelectionColor(Color newColor)
  {
    textNonSelectionColor = newColor;
  }

  public Color getTextNonSelectionColor()
  {
    return textNonSelectionColor;
  }

  public void setBackgroundSelectionColor(Color newColor)
  {
    backgroundSelectionColor = newColor;
  }

  public Color getBackgroundSelectionColor()
  {
    return backgroundSelectionColor;
  }

  public void setBackgroundNonSelectionColor(Color newColor)
  {
    backgroundNonSelectionColor = newColor;
  }

  public Color getBackgroundNonSelectionColor()
  {
    return backgroundNonSelectionColor;
  }

  public void setBorderSelectionColor(Color newColor)
  {
    borderSelectionColor = newColor;
  }

  public Color getBorderSelectionColor()
  {
    return borderSelectionColor;
  }

  public void setFont(Font font)
  {
    if(font instanceof FontUIResource)
    {
      font = null;
    }

    super.setFont(font);
  }

  public void setBackground(Color color)
  {
    if(color instanceof ColorUIResource)
    {
      color = null;
    }

    super.setBackground(color);
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
  {
    String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);

    this.hasFocus = hasFocus;
    setText(stringValue);

    if(selected)
    {
      setForeground(getTextSelectionColor());
    }
    else
    {
      setForeground(getTextNonSelectionColor());
    }

    if (!tree.isEnabled())
    {
      setEnabled(false);

      if (leaf)
      {
        setDisabledIcon(getDisabledLeafIcon());
      }
      else
      if (expanded)
      {
        setDisabledIcon(getDisabledOpenIcon());
      }
      else
      {
        setDisabledIcon(getDisabledClosedIcon());
      }
    }
    else
    {
      setEnabled(true);
      
      if (leaf)
      {
        setIcon(getLeafIcon());
      }
      else
      if (expanded)
      {
        setIcon(getOpenIcon());
      }
      else
      {
        setIcon(getClosedIcon());
      }
    }

    isLeftToRight = OyoahaUtilities.isLeftToRight(tree);
    this.selected = selected;

    return this;
  }

  private static Rectangle paintIconR = new Rectangle();
  private static Rectangle paintTextR = new Rectangle();
  private static Rectangle paintViewR = new Rectangle();
  private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

  public void paint(Graphics g)
  {
    Color bColor;

    if(selected)
    {
      bColor = getBackgroundSelectionColor();
    }
    else
    {
      bColor = getBackgroundNonSelectionColor();
    }

    if(bColor==null)
    {
      bColor = getBackground();
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
      OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("Tree.Renderer");

      if(isLeftToRight)
      {
        Shape s = OyoahaUtilities.normalizeClip(g, imageOffset, 0, getWidth()-(imageOffset+1), getHeight());

        if(o!=null && bColor instanceof UIResource)
        {
          o.paintBackground(g, this, imageOffset, 0, getWidth()-(imageOffset+1), getHeight(), OyoahaUtilities.SELECTED_ENABLED);
        }
        else
        {
          g.fillRect(imageOffset, 0, getWidth()-(imageOffset+1), getHeight());
        }

        g.setClip(s);
      }
      else
      {
        Shape s = OyoahaUtilities.normalizeClip(g, 0, 0, getWidth()-(imageOffset+1), getHeight());

        if(o!=null && bColor instanceof UIResource)
        {
          o.paintBackground(g, this, 0, 0, getWidth()-(imageOffset+1), getHeight(), OyoahaUtilities.SELECTED_ENABLED);
        }
        else
        {
          g.fillRect(0, 0, getWidth()-(imageOffset+1), getHeight());
        }

        g.setClip(s);
      }
    }

    //super.paint(g);
    String text = getText();
    Icon icon = (isEnabled()) ? getIcon() : getDisabledIcon();

    if ((icon == null) && (text == null))
    {
        return;
    }

    FontMetrics fm = g.getFontMetrics();
    paintViewInsets = getInsets(paintViewInsets);

    paintViewR.x = paintViewInsets.left;
    paintViewR.y = paintViewInsets.top;

    Dimension d = getSize();

    paintViewR.width = d.width - (paintViewInsets.left + paintViewInsets.right);
    paintViewR.height = d.height - (paintViewInsets.top + paintViewInsets.bottom);

    paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
    paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

    String clippedText = SwingUtilities.layoutCompoundLabel(
            this,
            fm,
            text,
            icon,
            getVerticalAlignment(),
            getHorizontalAlignment(),
            getVerticalTextPosition(),
            getHorizontalTextPosition(),
            paintViewR,
            paintIconR,
            paintTextR,
            getIconTextGap());

    if (icon != null)
    {
        icon.paintIcon(this, g, paintIconR.x, paintIconR.y);
    }

    if (text != null)
    {
        View v = (View)getClientProperty("html");

        if (v != null)
        {
            v.paint(g, paintTextR);
        }
        else
        {
            int textX = paintTextR.x;
            int textY = paintTextR.y + fm.getAscent();

            if (isEnabled())
            {
                paintEnabledText(this, g, clippedText, textX, textY);
            }
            else
            {
                paintDisabledText(this, g, clippedText, textX, textY);
            }
        }
    }

    paintBorder(g);
  }

  protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY)
  {
    g.setColor(l.getForeground());
    BasicGraphicsUtils.drawString(g, s, l.getDisplayedMnemonic(), textX, textY);
  }

  protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY)
  {
      g.setColor(UIManager.getColor("Label.disabledForeground"));
      BasicGraphicsUtils.drawString(g, s, l.getDisplayedMnemonic(), textX, textY);
  }

  protected int getLabelStart()
  {
    Icon currentI = getIcon();

    if(currentI != null && getText() != null)
    {
      if(isLeftToRight)
      return currentI.getIconWidth() + Math.max(0, getIconTextGap()-1);
      
      return currentI.getIconWidth() + Math.max(0, getIconTextGap()*2);
    }

    return 0;
  }

  protected int getBorderStart()
  {
    Icon currentI = getIcon();

    if(currentI != null && getText() != null)
    {
      if(isLeftToRight)
      return currentI.getIconWidth() + Math.max(0, (getIconTextGap()/2));
    
      return currentI.getIconWidth() + Math.max(0, getIconTextGap()*2);  
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
        border.paintBorder(this, g, imageOffset, 0, getWidth()-(imageOffset+1), getHeight()-1);
      }
      else
      {
        border.paintBorder(this, g, 0, 0, getWidth()-(imageOffset+1), getHeight()-1);
      }
    }
  }

  public Dimension getPreferredSize()
  {
    Dimension retDimension = super.getPreferredSize();

    if(retDimension != null)
    {
      retDimension = new Dimension(retDimension.width+3, retDimension.height);
    }

    return retDimension;
  }

  public void validate() {}
  public void revalidate() {}
  public void repaint(long tm, int x, int y, int width, int height) {}
  public void repaint(Rectangle r) {}

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
  {
    if (propertyName=="text")
    {
      super.firePropertyChange(propertyName, oldValue, newValue);
    }
  }

  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
  public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
  public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
  public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
  public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
  public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
  public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}