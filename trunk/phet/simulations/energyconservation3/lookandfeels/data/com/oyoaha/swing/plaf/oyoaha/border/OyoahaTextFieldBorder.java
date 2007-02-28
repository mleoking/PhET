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
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTextFieldBorder implements Border, UIResource
{
    protected Insets insets;

    public OyoahaTextFieldBorder()
    {
        insets = new Insets(4,4,4,4);
    }

    public OyoahaTextFieldBorder(int size)
    {
        insets = new Insets(size, size, size, size);
    }

    public OyoahaTextFieldBorder(int top, int left, int bottom, int right)
    {
        insets = new Insets(top, left, bottom, right);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        OyoahaThemeScheme scheme = OyoahaUtilities.getScheme();

        if(c.isEnabled())
        {
            if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
            {
                GradientPaint gp;

                gp = new GradientPaint(0, y+4, new Color(0,0,0,0), 0, y, new Color(0,0,0,100), false);
                ((Graphics2D)g).setPaint(gp);
                g.fillRect(x, y, width, 4);

                gp = new GradientPaint(x, 0, new Color(0,0,0,100), x+4, 0, new Color(0,0,0,0), false);
                ((Graphics2D)g).setPaint(gp);
                g.fillRect(x, y, 4, height);

                g.setColor(scheme.getWhite());
                g.drawLine(width-1, y, width-1, height-1);
                g.drawLine(x, height-1, width-1, height-1);

                g.setColor(scheme.getBlack());
                g.drawLine(x, y, width-1, y);
                g.drawLine(x, y, x, height-1);
            }
            else
            {
                g.setColor(scheme.getBlack());
                g.drawRect(x, y, width-1, height-1);
            }
        }
        else
        {
            g.setColor(scheme.getGray());
            g.drawRect(x, y, width-1, height-1);
        }
    }

    public Insets getBorderInsets(Component c)
    {
        return insets;
    }

    public boolean isBorderOpaque()
    {
        return true;
    }
}