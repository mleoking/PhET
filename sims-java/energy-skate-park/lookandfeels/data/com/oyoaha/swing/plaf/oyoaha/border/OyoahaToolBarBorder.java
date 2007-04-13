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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaToolBarBorder extends AbstractBorder implements UIResource, SwingConstants
{
    protected int state = OyoahaUtilities.UNSELECTED_ENABLED;

    public void setState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return state;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        if(c.isEnabled() && ((JToolBar)c).isFloatable())
        {
            g.translate(x, y);

            if(((JToolBar)c).getOrientation() == HORIZONTAL)
            {
                if( OyoahaUtilities.isLeftToRight(c))
                {
                    paintBumpArea(g, c, 2, 2, 10, c.getSize().height - 4);
                }
                else
                {
                    paintBumpArea(g, c, c.getBounds().width-12, 2, 10, c.getSize().height - 4);
                }
            }
            else // vertical
            {
                paintBumpArea(g, c, 2, 2, c.getSize().width - 4, 10);
            }

            g.translate(-x, -y);
        }
    }

    protected void paintBumpArea(Graphics g, Component c, int x, int y, int w, int h)
    {
        if(!(c instanceof JComponent))
        {
            return;
        }

        OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("ToolBarBorder");
        Color color = c.getBackground();

        if(o!=null && color instanceof UIResource)
        {
            o.paintBackground(g, c, x, y, w, h, state);
        }
        else
        {
            g.setColor(color);
            g.fillRect(x, y, w, h);
        }

        //paint bump mapping here
        if(((JToolBar)c).getOrientation() == HORIZONTAL)
        {
            OyoahaUtilities.paintBump(g, c, x, y, w, h, state, 1, -1);
        }
        else // vertical
        {
            OyoahaUtilities.paintBump(g, c, x, y, w, h, state, -1, 1);
        }
    }

    public Insets getBorderInsets(Component c)
    {
        Insets borderInsets = new Insets(2, 2, 2, 2);

        if(((JToolBar) c).isFloatable())
        {
            if(((JToolBar)c).getOrientation() == HORIZONTAL)
            {
                borderInsets.left = 16;
            }
            else // vertical
            {
                borderInsets.top = 16;
            }
        }

        Insets margin = ((JToolBar)c).getMargin();

        if(margin != null)
        {
            borderInsets.left   += margin.left;
            borderInsets.top    += margin.top;
            borderInsets.right  += margin.right;
            borderInsets.bottom += margin.bottom;
        }

        return borderInsets;
    }
}
