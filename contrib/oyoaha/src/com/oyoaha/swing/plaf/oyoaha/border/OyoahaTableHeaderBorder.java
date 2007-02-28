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

package com.oyoaha.swing.plaf.oyoaha.border;

import javax.swing.border.*;
import java.awt.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTableHeaderBorder extends AbstractBorder implements UIResource
{
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        int state = OyoahaUtilities.getStatus(c);

        width--;
        height--;

        if(state==OyoahaUtilities.UNSELECTED_DISABLED || state==OyoahaUtilities.SELECTED_DISABLED) //gray
        {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));
            g.drawLine(x+1, y+height, x+width, y+height);
            g.drawLine(x+width, y+1, x+width, y+height);
        }
        else
        if(state==OyoahaUtilities.UNSELECTED_PRESSED || state==OyoahaUtilities.SELECTED_ROLLOVER || state==OyoahaUtilities.SELECTED_PRESSED || state==OyoahaUtilities.SELECTED_ENABLED) //down
        {
            if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
            {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
            g.drawLine(x+1, y+height, x+width, y+height);
            g.drawLine(x+width, y+1, x+width, y+height);

            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
            g.drawLine(x, y, x+width, y);
            g.drawLine(x, y, x, y+height);
            }
            else
            {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
            g.drawLine(x+1, y+height, x+width, y+height);
            g.drawLine(x+width, y, x+width, y+height);
            g.drawLine(x, y, x+width, y);
            g.drawLine(x, y, x, y+height);
            }     
        }
        else
        {
            if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
            {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
            g.drawLine(x+1, y+height-3, x+width, y+height-3);
            g.drawLine(x+width, y+1, x+width, y+height-3);

            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
            g.drawLine(x, y, x+width, y);
            g.drawLine(x, y, x, y+height-3);
            }
            else
            {
            g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
            g.drawLine(x+1, y+height-3, x+width, y+height-3);
            g.drawLine(x+width, y, x+width, y+height-3);
            }

            GradientPaint gp;

            gp = new GradientPaint(0, y+height, new Color(0,0,0,0), 0, y+height-3, new Color(0,0,0,100), false);
            ((Graphics2D)g).setPaint(gp);
            g.fillRect(x+1, y+height-3, width-1, 3);  
        }
    }

    public Insets getBorderInsets(Component c)
    {
        return new Insets(1, 1, 1, 1);
    }

    public Insets getBorderInsets(Component c, Insets insets)
    {
        insets.top = 1;
        insets.left = 1;
        insets.bottom = 4;
        insets.right = 1;
        return insets;
    }

    public boolean isBorderOpaque()
    {
        return true;
    }
}