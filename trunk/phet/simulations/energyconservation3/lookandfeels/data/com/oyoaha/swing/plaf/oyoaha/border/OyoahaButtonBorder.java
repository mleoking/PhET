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

public class OyoahaButtonBorder extends AbstractBorder implements OyoahaButtonBorderLike, UIResource
{
    protected int gapExt;
    protected int gapInt;

    public OyoahaButtonBorder()
    {
        this(1, 2);
    }

    public OyoahaButtonBorder(int gapExt, int gapInt)
    {
        this.gapExt = gapExt;
        this.gapInt = gapInt;
    }

    public OyoahaButtonBorder(Integer gapExt, Integer gapInt)
    {
        this.gapExt = gapExt.intValue();
        this.gapInt = gapInt.intValue();
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        int state = OyoahaUtilities.getStatus(c);

        g.setColor(OyoahaUtilities.getColor(c, state));

        x += gapExt;
        y += gapExt;
        width -= (gapExt*2)+4;
        height -= (gapExt*2)+4;

        if (state==OyoahaUtilities.UNSELECTED_ROLLOVER || state==OyoahaUtilities.UNSELECTED_ENABLED)
        {
            GradientPaint gp;

            gp = new GradientPaint(x+width, 0, new Color(0,0,0,100), x+width+4, 0, new Color(0,0,0,0), false);
            ((Graphics2D)g).setPaint(gp);
            g.fillRect(x+width, y+4, 4, height-4);

            gp = new GradientPaint(0, y+height, new Color(0,0,0,100), 0, y+height+4, new Color(0,0,0,0), false);
            ((Graphics2D)g).setPaint(gp);
            g.fillRect(x+4, y+height, width-4, 4);

            gp = new GradientPaint(x+width, y+height, new Color(0,0,0,80), x+width+3, y+height+3, new Color(0,0,0,0), false);
            ((Graphics2D)g).setPaint(gp);
            g.fillRect(x+width, y+height, 4, 4);
        }

        if(state==OyoahaUtilities.UNSELECTED_DISABLED) //gray
        {
            g.drawRect(x,y,width,height);
        }
        else
        if( state==OyoahaUtilities.SELECTED_DISABLED) //gray
        {
            g.drawRect(x,y,width,height);

            if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
            {
                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
                g.drawLine(x, y+height, x+width, y+height);
                g.drawLine(x+width, y, x+width, y+height);

                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));
                g.drawLine(x, y, x+width, y);
                g.drawLine(x, y, x, y+height);
            }
            else
            {
                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));
                g.drawLine(x, y, x+width, y);
                g.drawLine(x, y, x, y+height);
                g.drawLine(x, y+height, x+width, y+height);
                g.drawLine(x+width, y, x+width, y+height);
            }
        }
        else
        if(state==OyoahaUtilities.UNSELECTED_PRESSED || state==OyoahaUtilities.SELECTED_ROLLOVER || state==OyoahaUtilities.SELECTED_PRESSED || state==OyoahaUtilities.SELECTED_ENABLED) //down
        {
            g.drawRect(x,y,width,height);

            if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
            {
                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
                g.drawLine(x, y+height, x+width, y+height);
                g.drawLine(x+width, y, x+width, y+height);

                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
                g.drawLine(x, y, x+width, y);
                g.drawLine(x, y, x, y+height);
            }
            else
            {
                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
                g.drawLine(x, y, x+width, y);
                g.drawLine(x, y, x, y+height);
                g.drawLine(x+1, y+height, x+width, y+height);
                g.drawLine(x+width, y+1, x+width, y+height);
            }
        }
        else
        if(state==OyoahaUtilities.UNSELECTED_ROLLOVER || state==OyoahaUtilities.UNSELECTED_ENABLED) //up
        {
            g.drawRect(x,y,width,height);

            if (OyoahaUtilities.getBoolean("oyoaha3dBorder"))
            {
                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.WHITE));
                g.drawLine(x, y, x+width, y);
                g.drawLine(x, y, x, y+height);

                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
                g.drawLine(x+1, y+height, x+width, y+height);
                g.drawLine(x+width, y+1, x+width, y+height);
            }
            else
            {
                g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK));
                g.drawLine(x, y, x+width, y);
                g.drawLine(x, y, x, y+height);
                g.drawLine(x+1, y+height, x+width, y+height);
                g.drawLine(x+width, y+1, x+width, y+height);
            }
        }
        else //normal
        {
            g.drawRect(x,y,width,height);
        }
    }

    public Insets getBorderInsets(Component c)
    {
        int s = gapExt+gapInt+1;
        return new Insets(s, s, s+4, s+4);
    }

    public Insets getBorderInsets(Component c, Insets insets)
    {
        int s = gapExt+gapInt+1;
        insets.top = s;
        insets.left = s;
        insets.bottom = s+4;
        insets.right = s+4;
        return insets;
    }

    public boolean isBorderOpaque()
    {
        return false;
    }

    public int getTopInsets()
    {
        return 0;
    }

    public int getLeftInsets()
    {
        return 0;
    }

    public int getBottomInsets()
    {
        return 4;
    }

    public int getRightInsets()
    {
        return 4;
    }

    public int getArcWidth()
    {
        return 0;
    }

    public int getArcHeight()
    {
        return 0;
    }
}