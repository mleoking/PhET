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
import javax.swing.border.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaInternalFrameTitlePane extends BasicInternalFrameTitlePane
{
  protected int buttonsWidth = 0;
  protected int paletteTitleHeight;
  protected Icon paletteCloseIcon;
  protected boolean isPalette = false;
  protected static final Border emptyBorder = new EmptyBorder(0,0,0,0);

  public OyoahaInternalFrameTitlePane(JInternalFrame _frame)
  {
    super(_frame);
  }

  protected void installDefaults()
  {
    super.installDefaults();
    setFont(UIManager.getFont("InternalFrame.font"));
    paletteTitleHeight = UIManager.getInt("InternalFrame.paletteTitleHeight");
    paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
  }

  protected void createButtons()
  {
    super.createButtons();

    Boolean paintActive = frame.isSelected() ? Boolean.TRUE:Boolean.FALSE;

    javax.swing.border.Border border = UIManager.getBorder("InternalFrame.buttonBorder");

    iconButton.putClientProperty("paintActive", paintActive);
    iconButton.setBorder(border);
    iconButton.getAccessibleContext().setAccessibleName("Iconify");

    maxButton.putClientProperty("paintActive", paintActive);
    maxButton.setBorder(border);
    maxButton.getAccessibleContext().setAccessibleName("Maximize");

    closeButton.putClientProperty("paintActive", paintActive);
    closeButton.setBorder(border);
    closeButton.getAccessibleContext().setAccessibleName("Close");

    closeButton.setBackground(MetalLookAndFeel.getPrimaryControlShadow());
  }

  protected void assembleSystemMenu()
  {

  }

  protected void addSystemMenuItems(JMenu systemMenu)
  {

  }

  protected void addSubComponents()
  {
    add(iconButton);
    add(maxButton);
    add(closeButton);
  }

  protected PropertyChangeListener createPropertyChangeListener()
  {
    return new OyoahaPropertyChangeHandler();
  }

  protected LayoutManager createLayout()
  {
    return new OyoahaTitlePaneLayout();
  }

  public void paint(Graphics g)
  {
    if(isPalette)
    {
      paintPalette(g);
      return;
    }

    boolean isSelected = frame.isSelected();

    OyoahaBackgroundObject oyoaha = OyoahaUtilities.getBackgroundObject("InternalFrameTitlePane");
    Color color = UIManager.getColor("Button.background");

    Rectangle r = getBounds();

    if(oyoaha!=null && color instanceof javax.swing.plaf.UIResource)
    {
      if(isSelected)
      oyoaha.paintBackground(g, this, 0, 0, r.width, r.height, OyoahaUtilities.SELECTED_ENABLED);
      else
      oyoaha.paintBackground(g, this, 0, 0, r.width, r.height, OyoahaUtilities.getStatus(this));
    }
    else
    {
      if(isSelected)
      OyoahaUtilities.paintColorBackground(g, this, 0, 0, r.width, r.height, color, OyoahaUtilities.SELECTED_ENABLED);
      else
      OyoahaUtilities.paintColorBackground(g, this, 0, 0, r.width, r.height, color, OyoahaUtilities.getStatus(this));
    }

    if(frame.getTitle() != null)
    {
      Font f = g.getFont();
      g.setFont(UIManager.getFont("InternalFrame.titleFont"));

      if(isSelected)
      g.setColor(selectedTextColor);
      else
      g.setColor(notSelectedTextColor);

      FontMetrics fm = g.getFontMetrics();
      int fmHeight = fm.getHeight() - fm.getLeading();
      int baseline = (18 - fmHeight) / 2 + fm.getAscent() + fm.getLeading();

      String title = frame.getTitle();

      int maxw = r.width-buttonsWidth;

      if(fm.stringWidth(title)<maxw)
      {
        g.drawString(title, 2, baseline);
        OyoahaUtilities.paintBump(g, this, fm.stringWidth(title)+5, 1, r.width-(buttonsWidth+fm.stringWidth(title)+6), r.height-2, OyoahaUtilities.getStatus(this), -1, 1);
      }
      else
      {
        char[] buf = title.toCharArray();

        int ww = 0;
        int i = 1;

        while(ww<maxw && i<buf.length)
        {
          ww = fm.charsWidth(buf, 0, i++);
        }

        g.drawChars(buf, 0, i-1, 2, baseline);
      }

      g.setFont(f);
    }
    else
    {
      //paint bump
      OyoahaUtilities.paintBump(g, this, 1, 1, r.width-(buttonsWidth+2), r.height-2, OyoahaUtilities.getStatus(this), -1, 1);
    }

    paintChildren(g);
  }

  public void paintPalette(Graphics g)
  {
    int width = getWidth();
    int height = getHeight();

    OyoahaBackgroundObject oyoaha = OyoahaUtilities.getBackgroundObject("InternalFrameTitlePane");
    Color color = UIManager.getColor("Button.background");

    Rectangle r = getBounds();

    if(oyoaha!=null && color instanceof javax.swing.plaf.UIResource)
    {
      if(frame.isSelected())
      oyoaha.paintBackground(g, this, 0, 0, width, height, OyoahaUtilities.SELECTED_ENABLED);
      else
      oyoaha.paintBackground(g, this, 0, 0, width, height, OyoahaUtilities.getStatus(this));
    }
    else
    {
      if(frame.isSelected())
      OyoahaUtilities.paintColorBackground(g, this, 0, 0, width, height, color, OyoahaUtilities.SELECTED_ENABLED);
      else
      OyoahaUtilities.paintColorBackground(g, this, 0, 0, width, height, color, OyoahaUtilities.getStatus(this));
    }

    OyoahaUtilities.paintBump(g, this, 1, 1, width-(buttonsWidth+2), height-2, OyoahaUtilities.getStatus(this), -1, 1);
    paintChildren(g);
  }

  public void setPalette(boolean b)
  {
    isPalette = b;

    if(isPalette)
    {
      closeButton.setIcon(paletteCloseIcon);

      if(frame.isMaximizable())
      {
        remove(maxButton);
      }

      if(frame.isIconifiable())
      {
        remove(iconButton);
      }
    }
    else
    {
      closeButton.setIcon(closeIcon);

      if(frame.isMaximizable())
      {
        add(maxButton);
      }

      if(frame.isIconifiable())
      {
        add(iconButton);
      }
    }

    repaint();
    revalidate();
  }

  protected final JInternalFrame getFrame()
  {
    return frame;
  }

  protected final JButton getIconButton()
  {
    return iconButton;
  }

  protected final JButton getCloseButton()
  {
    return closeButton;
  }

  protected final JButton getMaxButton()
  {
    return maxButton;
  }

  protected class OyoahaPropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler
  {
    public void propertyChange(PropertyChangeEvent evt)
    {
      String prop = (String)evt.getPropertyName();

      if(prop.equals(JInternalFrame.IS_SELECTED_PROPERTY))
      {
        Boolean b = (Boolean)evt.getNewValue();
        getIconButton().putClientProperty("paintActive", b);
        getCloseButton().putClientProperty("paintActive", b);
        getMaxButton().putClientProperty("paintActive", b);
        repaint();
      }

      super.propertyChange(evt);
    }
  }

  protected class OyoahaTitlePaneLayout implements LayoutManager
  {
    public void addLayoutComponent(String name, Component c)
    {

    }

    public void removeLayoutComponent(Component c)
    {

    }

    public Dimension preferredLayoutSize(Container c)
    {
      return minimumLayoutSize(c);
    }

    public Dimension getPreferredSize(Container c)
    {
      return minimumLayoutSize(c);
    }
    
    public Dimension minimumLayoutSize(Container c) 
    {
        // Calculate width.
        int width = 22;

        if (frame.isClosable()) 
        {
            width += 19;
        }
        
        if (frame.isMaximizable()) 
        {
            width += 19;
        }
        
        if (frame.isIconifiable()) 
        {
            width += 19;
        }

        Font font = getFont();

        if(font==null)
        {
            font = UIManager.getFont("InternalFrame.font");

            if(font==null)
            {
              font = OyoahaLookAndFeel.getWindowTitleFont();
              setFont(font);
            }
            else
            {
              setFont(font);
            }
        }

        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        
        String frameTitle = frame.getTitle();
        int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
        int title_length = frameTitle != null ? frameTitle.length() : 0;

        // Leave room for three characters in the title.
        if (title_length > 3) 
        {
            int subtitle_w = fm.stringWidth(frameTitle.substring(0, 3) + "...");
            width += (title_w < subtitle_w) ? title_w : subtitle_w;
        } 
        else 
        {
            width += title_w;
        }

        // Calculate height.
        int fontHeight = fm.getHeight();
        fontHeight += 7;

        Icon icon = getFrame().getFrameIcon();
        int iconHeight = 0;

        if (icon != null)
        {
            iconHeight = icon.getIconHeight();
        }

        iconHeight += 5;
  
        int height = Math.max( fontHeight, iconHeight);

        Dimension dim = new Dimension(width, height);

        // Take into account the border insets if any.
        if (getBorder() != null) 
        {
            Insets insets = getBorder().getBorderInsets(c);
            dim.height += insets.top + insets.bottom;
            dim.width += insets.left + insets.right;
        }
        
        return dim;
    }

    /*protected int computeHeight()
    {
      if(isPalette)
      {
        return paletteTitleHeight;
      }

      Font font = getFont();

      if(font==null)
      {
        font = UIManager.getFont("InternalFrame.font");

        if(font==null)
        {
          font = OyoahaLookAndFeel.getWindowTitleFont();
          setFont(font);
        }
        else
        {
          setFont(font);
        }
      }

      FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

      int fontHeight = fm.getHeight();
      fontHeight += 7;

      Icon icon = getFrame().getFrameIcon();
      int iconHeight = 0;

      if (icon != null)
      {
        iconHeight = icon.getIconHeight();
      }

      iconHeight += 5;

      int finalHeight = Math.max(fontHeight, iconHeight);

      return finalHeight;
    }*/

    public void layoutContainer(Container c)
    {
      JInternalFrame frame = getFrame();
      boolean leftToRight = OyoahaUtilities.isLeftToRight(frame);

      int w = getWidth();
      int x = leftToRight ? w : 0;
      int y = 2;
      int spacing;

      // assumes all buttons have the same dimensions
      // these dimensions include the borders
      JButton closeButton = getCloseButton();
      int buttonHeight = closeButton.getIcon().getIconHeight();
      int buttonWidth = closeButton.getIcon().getIconWidth();

      if(frame.isClosable())
      {
        if(isPalette)
        {
          spacing = 2;
          x += leftToRight ? -spacing -(buttonWidth+2) : spacing;
          closeButton.setBounds(x, y, buttonWidth+2, getHeight()-4);
          if( !leftToRight ) x += (buttonWidth+2);
        }
        else
        {
          spacing = 2;
          x += leftToRight ? -spacing -buttonWidth : spacing;
          closeButton.setBounds(x, y, buttonWidth, buttonHeight);
          if( !leftToRight ) x += buttonWidth;
        }
      }

      if(frame.isMaximizable() && !isPalette)
      {
        JButton maxButton = getMaxButton();
        //spacing = frame.isClosable() ? 4 : 2;
        spacing = 2;
        x += leftToRight ? -spacing -buttonWidth : spacing;
        maxButton.setBounds(x, y, buttonWidth, buttonHeight);
        if( !leftToRight ) x += buttonWidth;
      }

      if(frame.isIconifiable() && !isPalette)
      {
        JButton iconButton = getIconButton();
        spacing = frame.isMaximizable() ? 2 : (frame.isClosable() ? 4 : 2);
        x += leftToRight ? -spacing -buttonWidth : spacing;
        iconButton.setBounds(x, y, buttonWidth, buttonHeight);
        if( !leftToRight ) x += buttonWidth;
      }

      buttonsWidth = leftToRight ? w - x : x;
    }
  }
}