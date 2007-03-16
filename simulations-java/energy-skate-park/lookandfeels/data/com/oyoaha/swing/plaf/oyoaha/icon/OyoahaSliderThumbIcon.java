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

package com.oyoaha.swing.plaf.oyoaha.icon;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaSliderThumbIcon implements Icon, UIResource
{
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
      if(!(c instanceof JSlider))
      {
        return;
      }

      JSlider slider = (JSlider)c;
      int orientation = slider.getOrientation();
      int state = OyoahaUtilities.getStatus(c);
      g.translate(x, y);

      if(slider.isEnabled())
      {
          if(orientation==JSlider.HORIZONTAL)
          {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
            g.drawLine(1,0,13,0);  // top
            g.drawLine(0,1,0,8);  // left
            g.drawLine(1,9,7,15); // left slant
            
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
            g.drawLine(14,1,14,8);  // right
            g.drawLine(7,15,14,8);  // right slant
          }
          else
          {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
            g.drawLine(1,0,8,0); // top
            g.drawLine(0,1,0,13); // left
            g.drawLine(9,1,15,7); // top slant
            
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
            g.drawLine(1,14,8,14); // bottom
            g.drawLine(9,13,15,7); // bottom slant
          }
      }
      else
      {
          g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));
          
          if(orientation==JSlider.HORIZONTAL)
          {
            g.drawLine(1,0,13,0);  // top
            g.drawLine(0,1,0,8);  // left
            g.drawLine(14,1,14,8);  // right
            g.drawLine(1,9,7,15); // left slant
            g.drawLine(7,15,14,8);  // right slant
          }
          else
          {
            g.drawLine(1,0,8,0); // top
            g.drawLine(0,1,0,13); // left
            g.drawLine(1,14,8,14); // bottom
            g.drawLine(9,1,15,7); // top slant
            g.drawLine(9,13,15,7); // bottom slant
          }
      }

      // Fill in the background
      if(state==OyoahaUtilities.UNSELECTED_DISABLED || state==OyoahaUtilities.SELECTED_DISABLED)
      g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_ENABLED));
      else
      g.setColor(OyoahaUtilities.getColor(state));

      if(orientation==JSlider.HORIZONTAL)
      {
        g.fillRect(1,1,13,8);

        g.drawLine(2,9,12,9);
        g.drawLine(3,10,11,10);
        g.drawLine(4,11,10,11);
        g.drawLine(5,12,9,12);
        g.drawLine(6,13,8,13);
        g.drawLine(7,14,7,14);
      }
      else
      {
        g.fillRect(1,1,8,13);

        g.drawLine(9,2,9,12);
        g.drawLine(10,3,10,11);
        g.drawLine(11,4,11,10);
        g.drawLine(12,5,12,9);
        g.drawLine(13,6,13,8);
        g.drawLine(14,7,14,7);
      }

      g.translate(-x,-y);
  }

    public int getIconWidth()
    {
        return 15;
    }

    public int getIconHeight()
    {
        return 15;
    }
}