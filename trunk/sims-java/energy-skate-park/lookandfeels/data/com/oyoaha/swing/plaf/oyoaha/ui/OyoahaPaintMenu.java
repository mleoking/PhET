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
import javax.swing.text.View;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaPaintMenu
{
  protected static Rectangle zeroRect = new Rectangle(0,0,0,0);
  protected static Rectangle iconRect = new Rectangle();
  protected static Rectangle textRect = new Rectangle();
  protected static Rectangle acceleratorRect = new Rectangle();
  protected static Rectangle checkIconRect = new Rectangle();
  protected static Rectangle arrowIconRect = new Rectangle();
  protected static Rectangle viewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
  protected static Rectangle r = new Rectangle();

  private static OyoahaPaintMenu paintMenu;

  public final static OyoahaPaintMenu getOyoahaPaintMenu()
  {
    if(paintMenu==null)
    {
      paintMenu = new OyoahaPaintMenu();
    }

    return paintMenu;
  }

  protected void resetRects()
  {
    iconRect.setBounds(zeroRect);
    textRect.setBounds(zeroRect);
    acceleratorRect.setBounds(zeroRect);
    checkIconRect.setBounds(zeroRect);
    arrowIconRect.setBounds(zeroRect);
    viewRect.setBounds(0,0,Short.MAX_VALUE, Short.MAX_VALUE);
    r.setBounds(zeroRect);
  }

  public Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon,int defaultTextIconGap, String acceleratorDelimiter, Font acceleratorFont)
  {
    JMenuItem b = (JMenuItem)c;
    Icon icon = (Icon) b.getIcon();
    String text = b.getText();
    KeyStroke accelerator =  b.getAccelerator();
    String acceleratorText = "";

    if(accelerator != null)
    {
      int modifiers = accelerator.getModifiers();

      if(modifiers > 0)
      {
        acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
        //acceleratorText += "-";
        acceleratorText += acceleratorDelimiter;
      }

      acceleratorText += KeyEvent.getKeyText(accelerator.getKeyCode());
    }

    Font font = b.getFont();
    FontMetrics fm = b.getToolkit().getFontMetrics(font);
    FontMetrics fmAccel = b.getToolkit().getFontMetrics(acceleratorFont);

    resetRects();

    layoutMenuItem(
      (JMenuItem)c, fm, text, fmAccel, acceleratorText, icon, checkIcon, arrowIcon,
      b.getVerticalAlignment(), b.getHorizontalAlignment(),
      b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
      viewRect, iconRect, textRect, acceleratorRect, checkIconRect, arrowIconRect,
      text == null ? 0 : defaultTextIconGap,
      defaultTextIconGap
      );

    // find the union of the icon and text rects
    r.setBounds(textRect);
    r = SwingUtilities.computeUnion(iconRect.x, iconRect.y, iconRect.width, iconRect.height, r);

    // Add in the accelerator
    boolean acceleratorTextIsEmpty = (acceleratorText == null) || acceleratorText.equals("");

    if(!acceleratorTextIsEmpty)
    {
      r.width += acceleratorRect.width;
      r.width += 7*defaultTextIconGap;
    }

    if(useCheckAndArrow((JMenuItem)c))
    {
      // Add in the checkIcon
      r.width += checkIconRect.width;
      r.width += defaultTextIconGap;

      // Add in the arrowIcon
      r.width += defaultTextIconGap;
      r.width += arrowIconRect.width;
    }

    r.width += 2*defaultTextIconGap;

    Insets insets = b.getInsets();
    if(insets != null)
    {
      r.width += insets.left + insets.right;
      r.height += insets.top + insets.bottom;
    }

    // if the width is even, bump it up one. This is critical
    // for the focus dash line to draw properly
    if(r.width%2 == 0)
    {
      r.width++;
    }

    // if the height is even, bump it up one. This is critical
    // for the text to center properly
    if(r.height%2 == 0)
    {
      r.height++;
    }

    return r.getSize();
  }

  protected void drawText(Graphics g, JMenuItem b, ButtonModel model, FontMetrics fm, String text, Color foreground)
  {
    if(text != null)
    {
      //View v = (View)b.getClientProperty(BasicHTML.propertyKey);
      View v = (View)b.getClientProperty("html");

      if (v != null)
      {
        v.paint(g, textRect);
      }
      else
      {
        if(!model.isEnabled())
        {
          //paint the text disabled

          if(UIManager.get("MenuItem.disabledForeground") instanceof Color)
          {
            g.setColor(UIManager.getColor("MenuItem.disabledForeground"));
            BasicGraphicsUtils.drawString(g,text,model.getMnemonic(), textRect.x, textRect.y + fm.getAscent());
          }
          else
          {
            g.setColor(b.getBackground().brighter());
            BasicGraphicsUtils.drawString(g,text,model.getMnemonic(), textRect.x, textRect.y + fm.getAscent());
            g.setColor(b.getBackground().darker());
            BasicGraphicsUtils.drawString(g,text,model.getMnemonic(), textRect.x - 1, textRect.y + fm.getAscent() - 1);
          }
        }
        else
        { //paint the text normally
          if (model.isArmed()|| (b instanceof JMenu && model.isSelected()))
          {
            g.setColor(foreground);
          }
          else
          {
            g.setColor(b.getForeground());
          }

          BasicGraphicsUtils.drawString(g,text, model.getMnemonic(), textRect.x, textRect.y + fm.getAscent());
        }
      }
    }
  }

  public void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background, Color foreground, int defaultTextIconGap, String acceleratorDelimiter, Font acceleratorFont, Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground)
  {
    JMenuItem b = (JMenuItem) c;
    JMenuItem menuItem = (JMenuItem) c;
    ButtonModel model = b.getModel();

    //   Dimension size = b.getSize();
    int menuWidth = b.getWidth();
    int menuHeight = b.getHeight();
    Insets i = c.getInsets();

    resetRects();

    viewRect.setBounds( 0, 0, menuWidth, menuHeight );

    viewRect.x += i.left;
    viewRect.y += i.top;
    viewRect.width -= (i.right + viewRect.x);
    viewRect.height -= (i.bottom + viewRect.y);

    Font holdf = g.getFont();
    Font f = c.getFont();
    g.setFont( f );
    FontMetrics fm = g.getFontMetrics( f );
    FontMetrics fmAccel = g.getFontMetrics( acceleratorFont );

    Color holdc = g.getColor();

    // get Accelerator text
    KeyStroke accelerator =  b.getAccelerator();
    String acceleratorText = "";

    if (accelerator != null)
    {
      int modifiers = accelerator.getModifiers();
      if (modifiers > 0)
      {
        acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
        //acceleratorText += "-";
        acceleratorText += acceleratorDelimiter;
      }

      acceleratorText += KeyEvent.getKeyText(accelerator.getKeyCode());
    }

    // layout the text and icon
    String text = layoutMenuItem(
      menuItem, fm, b.getText(), fmAccel, acceleratorText, b.getIcon(),
      checkIcon, arrowIcon,
      b.getVerticalAlignment(), b.getHorizontalAlignment(),
      b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
      viewRect, iconRect, textRect, acceleratorRect,
      checkIconRect, arrowIconRect,
      b.getText() == null ? 0 : defaultTextIconGap,
      defaultTextIconGap
    );

    // Paint the Check
    if (checkIcon != null)
    {
      if(model.isArmed() || (c instanceof JMenu && model.isSelected()))
      {
        g.setColor(foreground);
      }
      else
      {
        g.setColor(b.getForeground());
      }

      if(useCheckAndArrow(menuItem))
      {
        checkIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
      }

      g.setColor(holdc);
    }

    // Paint the Icon
    if(b.getIcon() != null)
    {
      Icon icon;

      if(!model.isEnabled())
      {
        icon = (Icon) b.getDisabledIcon();
      }
      else
      if(model.isPressed() && model.isArmed())
      {
        icon = (Icon) b.getPressedIcon();
        if(icon == null)
        {
          // Use default icon
          icon = (Icon) b.getIcon();
        }
      }
      else
      {
        icon = (Icon) b.getIcon();
      }

      if(icon!=null)
      {
        icon.paintIcon(c, g, iconRect.x, iconRect.y);
      }
    }

    drawText(g, b, model, fm, text, foreground);

    // Draw the Accelerator Text
    if(acceleratorText != null && !acceleratorText.equals(""))
    {
      g.setFont(acceleratorFont);

      if(!model.isEnabled())
      {
        // *** paint the acceleratorText disabled
        if (disabledForeground != null)
        {
          g.setColor(disabledForeground);
          BasicGraphicsUtils.drawString(g,acceleratorText,0, acceleratorRect.x, acceleratorRect.y + fmAccel.getAscent());
        }
        else
        {
          g.setColor(b.getBackground().brighter());
          BasicGraphicsUtils.drawString(g,acceleratorText,0, acceleratorRect.x, acceleratorRect.y + fmAccel.getAscent());
          g.setColor(b.getBackground().darker());
          BasicGraphicsUtils.drawString(g,acceleratorText,0, acceleratorRect.x - 1, acceleratorRect.y + fmAccel.getAscent() - 1);
        }
      }
      else
      {
        // *** paint the acceleratorText normally
        if (model.isArmed()|| (c instanceof JMenu && model.isSelected()))
        {
          g.setColor( acceleratorSelectionForeground );
        }
        else
        {
          g.setColor( acceleratorForeground );
        }

        BasicGraphicsUtils.drawString(g,acceleratorText, 0, acceleratorRect.x, acceleratorRect.y + fmAccel.getAscent());
      }
    }

    // Paint the Arrow
    if (arrowIcon != null)
    {
      if(model.isArmed() || (c instanceof JMenu &&model.isSelected()))
      {
        g.setColor(foreground);
      }

      if(useCheckAndArrow(menuItem))
      {
        arrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
      }
    }

    g.setColor(holdc);
    g.setFont(holdf);
  }

    protected String layoutMenuItem(
      JMenuItem menuItem,
      FontMetrics fm,
      String text,
      FontMetrics fmAccel,
      String acceleratorText,
      Icon icon,
      Icon checkIcon,
      Icon arrowIcon,
      int verticalAlignment,
      int horizontalAlignment,
      int verticalTextPosition,
      int horizontalTextPosition,
      Rectangle viewRect,
      Rectangle iconRect,
      Rectangle textRect,
      Rectangle acceleratorRect,
      Rectangle checkIconRect,
      Rectangle arrowIconRect,
      int textIconGap,
      int menuItemGap)
    {

        SwingUtilities.layoutCompoundLabel(
                            menuItem, fm, text, icon, verticalAlignment,
                            horizontalAlignment, verticalTextPosition,
                            horizontalTextPosition, viewRect, iconRect, textRect,
                            textIconGap);

        /* Initialize the acceelratorText bounds rectangle textRect.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for acceleratorTextRect.
         */
        if((acceleratorText == null) || acceleratorText.equals(""))
        {
            acceleratorRect.width = acceleratorRect.height = 0;
            acceleratorText = "";
        }
        else
        {
            acceleratorRect.width = SwingUtilities.computeStringWidth( fmAccel, acceleratorText );
            acceleratorRect.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle's width & height.
         */

	if(useCheckAndArrow(menuItem))
	{
	    if (checkIcon != null)
	    {
              checkIconRect.width = checkIcon.getIconWidth();
              checkIconRect.height = checkIcon.getIconHeight();
	    }
	    else
	    {
              checkIconRect.width = checkIconRect.height = 0;
	    }

          /* Initialize the arrowIcon bounds rectangle width & height.
           */
          if (arrowIcon != null)
          {
            arrowIconRect.width = arrowIcon.getIconWidth();
            arrowIconRect.height = arrowIcon.getIconHeight();
          }
          else
          {
            arrowIconRect.width = arrowIconRect.height = 0;
          }
        }

        Rectangle labelRect = iconRect.union(textRect);

        if(OyoahaUtilities.isLeftToRight(menuItem))
        {
          textRect.x += menuItemGap;
          iconRect.x += menuItemGap;

          // Position the Accelerator text rect
          acceleratorRect.x = viewRect.x + viewRect.width - arrowIconRect.width - menuItemGap - acceleratorRect.width;

          // Position the Check and Arrow Icons
          if(useCheckAndArrow(menuItem))
          {
            checkIconRect.x = viewRect.x + menuItemGap;
            textRect.x += menuItemGap + checkIconRect.width;
            iconRect.x += menuItemGap + checkIconRect.width;
            arrowIconRect.x = viewRect.x + viewRect.width - menuItemGap - arrowIconRect.width;
          }
        }
        else
        {
          textRect.x -= menuItemGap;
          iconRect.x -= menuItemGap;

          // Position the Accelerator text rect
          acceleratorRect.x = viewRect.x + arrowIconRect.width + menuItemGap;

          // Position the Check and Arrow Icons
          if (useCheckAndArrow(menuItem))
          {
            checkIconRect.x = viewRect.x + viewRect.width - menuItemGap - checkIconRect.width;
            textRect.x -= menuItemGap + checkIconRect.width;
            iconRect.x -= menuItemGap + checkIconRect.width;
            arrowIconRect.x = viewRect.x + menuItemGap;
          }
        }

        // Align the accelertor text and the check and arrow icons vertically
        // with the center of the label rect.
        acceleratorRect.y = labelRect.y + (labelRect.height/2) - (acceleratorRect.height/2);

        if( useCheckAndArrow(menuItem) )
        {
          arrowIconRect.y = labelRect.y + (labelRect.height/2) - (arrowIconRect.height/2);
          checkIconRect.y = labelRect.y + (labelRect.height/2) - (checkIconRect.height/2);
        }

        return text;
    }

  /*
  * Returns false if the component is a JMenu and it is a top
  * level menu (on the menubar).
  */
  protected boolean useCheckAndArrow(JMenuItem menuItem)
  {
    boolean b = true;

    if((menuItem instanceof JMenu) && (((JMenu)menuItem).isTopLevelMenu()))
    {
      b = false;
    }

    return b;
  }
}