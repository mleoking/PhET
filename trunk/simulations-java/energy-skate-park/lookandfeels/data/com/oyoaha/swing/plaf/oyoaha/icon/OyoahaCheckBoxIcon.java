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

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaCheckBoxIcon extends MetalCheckBoxIcon implements UIResource
{
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        AbstractButton cb = (AbstractButton)c;
        ButtonModel model = cb.getModel();
        int controlSize = getControlSize()-1;

        x+=3;

        boolean drawCheck = model.isSelected();

        int state = OyoahaUtilities.getStatus(c);

        if(state==OyoahaUtilities.UNSELECTED_DISABLED || state==OyoahaUtilities.SELECTED_DISABLED)
        {
            g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_DISABLED));
            g.drawRect(x, y, controlSize, controlSize);
        }
        else
        {
            g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.BLACK));
            g.drawRect(x, y, controlSize, controlSize);

            switch(state)
            {
                case OyoahaUtilities.UNSELECTED_PRESSED:
                case OyoahaUtilities.SELECTED_PRESSED:
                g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_ENABLED));
                break;
                case OyoahaUtilities.UNSELECTED_DISABLED:
                case OyoahaUtilities.SELECTED_DISABLED:
                g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_DISABLED));
                break;
                case OyoahaUtilities.UNSELECTED_ROLLOVER:
                case OyoahaUtilities.SELECTED_ROLLOVER:
                g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.UNSELECTED_ENABLED));
                break;
                default:
                g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.BLACK));
                break;
            }
        }

        g.drawRect(x+1, y+1, controlSize-2, controlSize-2);

        if (model.isSelected())
        {
            if(state!=OyoahaUtilities.UNSELECTED_DISABLED && state!=OyoahaUtilities.SELECTED_DISABLED)
            g.setColor(OyoahaUtilities.getColor(OyoahaUtilities.BLACK));
            drawCheck(c,g,x,y);
        }
    }

    protected void drawCheck(Component c, Graphics g, int x, int y)
    {
        int controlSize = getControlSize();
        g.fillRect( x+3, y+5, 2, controlSize-8 );
        g.drawLine( x+(controlSize-4), y+3, x+5, y+(controlSize-6) );
        g.drawLine( x+(controlSize-4), y+4, x+5, y+(controlSize-5) );
    }

    public int getIconWidth()
    {
        return getControlSize()+4;
    }
}
