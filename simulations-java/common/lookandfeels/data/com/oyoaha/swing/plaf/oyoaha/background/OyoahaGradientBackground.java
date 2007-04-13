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

package com.oyoaha.swing.plaf.oyoaha.background;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaGradientBackground implements OyoahaBackgroundObject, UIResource
{
    protected boolean horizontal;
    protected boolean vertical;
    protected Color color1;
    protected Color color2;
    protected OyoahaStateRule rule;

    public OyoahaGradientBackground(Color color1, Color color2, Boolean horizontal, Boolean vertical, OyoahaStateRule rule)
    {
        this.color1 = color1;
        this.color2 = color2;
        this.horizontal = horizontal.booleanValue();
        this.vertical = vertical.booleanValue();
        this.rule = rule;
    }

    public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, int state)
    {
        state = rule.getState(state);

        if(state==OyoahaUtilities.UNVISIBLE)
        {
            return;
        }

        if(g instanceof Graphics2D)
        {
            if (state==OyoahaUtilities.SELECTED_DISABLED || state==OyoahaUtilities.UNSELECTED_DISABLED)
            {
                return;
            }


            if (c instanceof javax.swing.plaf.basic.BasicInternalFrameTitlePane)
            {
                Component parent = c.getParent();

                if (parent!=null)
                {
                    Rectangle r = parent.getBounds();
                    int x2 = c.getX();
                    int y2 = c.getY();

                    GradientPaint gp = new GradientPaint((horizontal)? -x : 0, (vertical)? -y : 0, (color1!=null)? OyoahaUtilities.getColor(color1, state) : OyoahaUtilities.getColor(state), (horizontal)? r.width : 0, (vertical)? r.height : 0, (color2!=null)? OyoahaUtilities.getColor(color2, state) : OyoahaUtilities.getColor(state), false);
                    ((Graphics2D)g).setPaint(gp);
                }
                else
                {
                    GradientPaint gp = new GradientPaint(0, 0, (color1!=null)? OyoahaUtilities.getColor(color1, state) : OyoahaUtilities.getColor(state), (horizontal)? c.getWidth() : 0, (vertical)? c.getHeight() : 0, (color2!=null)? OyoahaUtilities.getColor(color2, state) : OyoahaUtilities.getColor(state), false);
                    ((Graphics2D)g).setPaint(gp);
                }
            }
            else
            if (c instanceof JMenuItem && (state==OyoahaUtilities.UNSELECTED_ENABLED || state==OyoahaUtilities.UNSELECTED_DISABLED))
            {
                Component parent = c.getParent();

                if (parent!=null)
                {
                    Rectangle r = parent.getBounds();
                    int x2 = c.getX();
                    int y2 = c.getY();

                    GradientPaint gp = new GradientPaint((horizontal)? r.x-x2 : 0, (vertical)? r.y-y2 : 0, (color1!=null)? OyoahaUtilities.getColor(color1, state) : OyoahaUtilities.getColor(state), 0, r.height, (color2!=null)? OyoahaUtilities.getColor(color2, state) : OyoahaUtilities.getColor(state), false);
                    ((Graphics2D)g).setPaint(gp);
                }
                else
                {
                    GradientPaint gp = new GradientPaint(0, 0, (color1!=null)? OyoahaUtilities.getColor(color1, state) : OyoahaUtilities.getColor(state), (horizontal)? c.getWidth() : 0, (vertical)? c.getHeight() : 0, (color2!=null)? OyoahaUtilities.getColor(color2, state) : OyoahaUtilities.getColor(state), false);
                    ((Graphics2D)g).setPaint(gp);
                }
            }
            else
            {
                float x1 = (horizontal)? x : 0;
                float y1 = (vertical)? y : 0;
                float w1 = (horizontal)? x+width : 0;
                float h1 = (vertical)? y+height : 0;

                GradientPaint gp = new GradientPaint(x1, y1, (color1!=null)? color1 : OyoahaUtilities.getColor(state), w1, h1, (color2!=null)? color2 : OyoahaUtilities.getColor(state), false);
                ((Graphics2D)g).setPaint(gp);
            }
        }

        g.fillRect(x, y, width, height);
    }

    public boolean isOpaque()
    {
        return true;
    }
}