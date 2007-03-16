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
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaSplitPaneUI extends BasicSplitPaneUI
{
  public static ComponentUI createUI(JComponent x)
  {
    return new OyoahaSplitPaneUI();
  }

  public BasicSplitPaneDivider createDefaultDivider()
  {
    return new OyoahaSplitPaneDivider(this);
  }

  public void update(Graphics g, JComponent c)
  { 
    Dimension d = c.getSize();
    OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject(c);

    if(o!=null)
    {
      o.paintBackground(g, c, 0, 0, d.width, d.height, OyoahaUtilities.UNSELECTED_ENABLED);
    }
    else
    {
      g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_ENABLED));
      g.fillRect(0, 0, d.width, d.height);
    }

    Color color = OyoahaUtilities.getBackground(c);

    if(!(color instanceof UIResource))
    {
      //paint only the target area with color
      g.setColor(color);

      Component child;
      Rectangle cBounds;

      JSplitPane splitPane = (JSplitPane)c;

      if(splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
      {
        child = splitPane.getLeftComponent();
        
        if(child != null && !OyoahaUtilities.isOpaque(child))
        {       
          cBounds = child.getBounds();
          g.fillRect(cBounds.x, cBounds.y, cBounds.width, cBounds.height);
        }

        child = splitPane.getRightComponent();

        if(child != null && !OyoahaUtilities.isOpaque(child))
        {         
          cBounds = child.getBounds();
          g.fillRect(cBounds.x, cBounds.y, cBounds.width, cBounds.height);
        }
      }
      else
      {
        child = splitPane.getLeftComponent();
        
        if(child != null && !OyoahaUtilities.isOpaque(child))
        {      
          cBounds = child.getBounds();
          g.fillRect(cBounds.x, cBounds.y, cBounds.width, cBounds.height);
        }

        child = splitPane.getRightComponent();

        if(child != null && !OyoahaUtilities.isOpaque(child))
        {                   
          cBounds = child.getBounds();
          g.fillRect(cBounds.x, cBounds.y, cBounds.width, cBounds.height);
        }
      }
    }

    paint(g, c);
  }
}