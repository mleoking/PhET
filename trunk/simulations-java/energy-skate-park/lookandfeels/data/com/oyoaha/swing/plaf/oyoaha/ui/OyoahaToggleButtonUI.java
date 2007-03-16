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
import javax.swing.plaf.*;
import javax.swing.*;
import javax.swing.text.View;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaToggleButtonUI extends MetalToggleButtonUI implements OyoahaButtonLikeComponent
{
  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaToggleButtonUI();
  }
  
  public boolean isBorderPainted(Component c)
  {
    return ((AbstractButton)c).isBorderPainted();
  }

  public void installUI(JComponent c)
  {
    super.installUI(c);
    OyoahaUtilities.installRolloverListener(c);
    OyoahaUtilities.setOpaque(c);
  }

  public void uninstallUI(JComponent c)
  {
    super.uninstallUI(c);
    OyoahaUtilities.uninstallRolloverListener(c);
    OyoahaUtilities.unsetOpaque(c);
  }

  public void update(Graphics g, JComponent c)
  {
    OyoahaUtilities.paintBackground(g, c);
    paint(g, c);
  }

  protected static final Rectangle iconRect = new Rectangle();
  protected static final Rectangle textRect = new Rectangle();

  public void paint(Graphics g, JComponent c)
  {
    AbstractButton b = (AbstractButton)c;
    ButtonModel model = b.getModel();

    Rectangle viewRect = OyoahaUtilities.getViewRect2(c);
    iconRect.setBounds(0,0,0,0);
    textRect.setBounds(0,0,0,0);
    
    int w = viewRect.width;
    int h = viewRect.height;

    Font f = c.getFont();
    g.setFont(f);
    FontMetrics fm = g.getFontMetrics();

    Icon altIcon = b.getIcon();
    Icon selectedIcon = null;
    Icon disabledIcon = null;

    String text = SwingUtilities.layoutCompoundLabel(
        c, fm, b.getText(), altIcon,
        b.getVerticalAlignment(), b.getHorizontalAlignment(),
        b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
        viewRect, iconRect, textRect, getDefaultTextIconGap(b)
    );

    // Paint the icon
    if(altIcon != null)
    {
      if(!model.isEnabled())
      {
        altIcon = b.getDisabledIcon();
      }
      else
      if(model.isPressed() && model.isArmed())
      {
        altIcon = b.getPressedIcon();

        if(altIcon == null)
        {
          // Use selected icon
          altIcon = b.getSelectedIcon();
        }
      }
      else
      if(model.isSelected())
      {
        if(b.isRolloverEnabled() && model.isRollover())
        {
          altIcon = (Icon) b.getRolloverSelectedIcon();
          if (altIcon == null)
          {
            altIcon = (Icon) b.getSelectedIcon();
          }
        }
        else
        {
          altIcon = (Icon) b.getSelectedIcon();
        }
      }
      else
      if(b.isRolloverEnabled() && model.isRollover())
      {
        altIcon = (Icon) b.getRolloverIcon();
      }

      if(altIcon == null)
      {
        altIcon = b.getIcon();
      }

      altIcon.paintIcon(c, g, iconRect.x, iconRect.y);
    }

    // Draw the Text
    if(text!=null && !text.equals(""))
    {
      View v = (View)c.getClientProperty("html");

      if (v != null)
      {
        v.paint(g, textRect);
      }
      else
      {
        if(model.isEnabled())
        {
          // *** paint the text normally
          g.setColor(b.getForeground());
          BasicGraphicsUtils.drawString(g, text, model.getMnemonic(), textRect.x, textRect.y+fm.getAscent());
        }
        else
        {
          // *** paint the text disabled
          //g.setColor(b.getBackground().darker());
          g.setColor(UIManager.getColor("Label.disabledForeground"));
          BasicGraphicsUtils.drawString(g, text, model.getMnemonic(), textRect.x, textRect.y+fm.getAscent());
        }
      }
    }

    if(b.hasFocus() && b.isFocusPainted())
    {
      paintFocus(g, b, viewRect, textRect, iconRect);
    }
  }

  protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
  {
    Rectangle focusRect = new Rectangle();
    String text = b.getText();
    boolean isIcon = b.getIcon() != null;

    if(text != null && !text.equals(""))
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
}