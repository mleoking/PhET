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

import javax.swing.plaf.metal.*;
import javax.swing.plaf.*;
import java.awt.*;
import javax.swing.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaInternalFrameUI extends MetalInternalFrameUI implements OyoahaButtonLikeComponent
{
  protected OyoahaInternalFrameTitlePane titlePane;

  public OyoahaInternalFrameUI(JInternalFrame b)
  {
    super(b);
  }

  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaInternalFrameUI((JInternalFrame) c);
  }

  public void update(Graphics g, JComponent c)
  {   
    OyoahaUtilities.paintBackground(g, c);
    //paint(g, c);
  }

  protected JComponent createNorthPane(JInternalFrame w)
  {
    titlePane =  new OyoahaInternalFrameTitlePane(w);
    return titlePane;
  }

  public void setPalette(boolean isPalette)
  {
    if(isPalette)
    {
      LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
    }
    else
    {
      LookAndFeel.installBorder(frame, "InternalFrame.border");
    }

    titlePane.setPalette(isPalette);
  }

  public void installDefaults()
  {
    super.installDefaults();
    LookAndFeel.installColorsAndFont(frame, "InternalFrame.background", "InternalFrame.foreground", "InternalFrame.font");
  }

  public void installUI(JComponent c)
  {
    super.installUI(c);
    OyoahaUtilities.setOpaque(c);
  }

  public void uninstallUI(JComponent c)
  {
    super.uninstallUI(c);
    OyoahaUtilities.unsetOpaque(c);
  }
  
  public boolean isBorderPainted(Component c)
  {
    return true;
  }
}