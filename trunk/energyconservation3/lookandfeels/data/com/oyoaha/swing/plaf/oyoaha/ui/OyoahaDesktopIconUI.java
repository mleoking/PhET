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
import java.beans.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaDesktopIconUI extends BasicDesktopIconUI implements OyoahaButtonLikeComponent
{
  protected JButton button;
  protected JLabel label;
  protected TitleListener titleListener;

  public static ComponentUI createUI(JComponent c)
  {
      return new OyoahaDesktopIconUI();
  }

  public boolean isBorderPainted(Component c)
  {
    return true; //((JButton)c).isBorderPainted();
  }

  protected void installDefaults()
  {
    super.installDefaults();
    LookAndFeel.installColorsAndFont(desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
    OyoahaUtilities.setOpaque(desktopIcon);
  }

  protected void installComponents()
  {
      JInternalFrame frame = desktopIcon.getInternalFrame();
      Icon icon = frame.getFrameIcon();
      String title = frame.getTitle();

      button = new JButton (title);
      label = new JLabel(icon);

      button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          deiconize();
        }
      });

      label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

      button.setBorder(BorderFactory.createEmptyBorder());
      button.setFont(desktopIcon.getFont());
      button.setBackground(desktopIcon.getBackground());
      button.setForeground(desktopIcon.getForeground());

      button.setBorderPainted(false);
      button.setContentAreaFilled(false);

      int buttonH = button.getPreferredSize().height;

      desktopIcon.setLayout(new BorderLayout(2, 0));
      desktopIcon.add(label, BorderLayout.NORTH);
      desktopIcon.add(button, BorderLayout.CENTER);
      desktopIcon.getInternalFrame().addPropertyChangeListener(titleListener = new TitleListener());
  }

  protected void uninstallComponents()
  {
    desktopIcon.setLayout(null);
    desktopIcon.remove(button);
    desktopIcon.remove(label);
    desktopIcon.getInternalFrame().removePropertyChangeListener(titleListener);
  }

  public Dimension getPreferredSize(JComponent c)
  {
     return c.getLayout().preferredLayoutSize(c);
  }
  
  public void update(Graphics g, JComponent c)
  {
    OyoahaUtilities.paintBackground(g,c);
    paint(g,c);
  }

  protected class TitleListener implements PropertyChangeListener
  {
    public void propertyChange (PropertyChangeEvent e)
    {
      if (e.getPropertyName().equals("title"))
      {
        button.setText((String)e.getNewValue());
      }

      if (e.getPropertyName().equals("frameIcon"))
      {
        label.setIcon((Icon)e.getNewValue());
      }
    }
  }
}