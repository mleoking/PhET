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
import com.oyoaha.swing.plaf.oyoaha.pool.*;

public class OyoahaImageBorder extends AbstractBorder implements UIResource
{
    protected OyoahaSlicePool pool;
    protected OyoahaStateRule rule;

    public OyoahaImageBorder(OyoahaSlicePool pool, OyoahaStateRule rule)
    {
        this.pool = pool;
        this.rule = rule;
    }

    public Insets getBorderInsets(Component c)
    {
        return pool.getBorderInsets();
    }

    public Insets getBorderInsets(Component c, Insets _insets)
    {
        return pool.getBorderInsets(_insets);
    }

    public boolean isBorderOpaque()
    {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        int state = rule.getState(OyoahaUtilities.getStatus(c));

        if(state==OyoahaUtilities.UNVISIBLE)
        {
            return;
        }

        Image[] images;

        //check if this component use a non-ResourceUI color
        //if it's true ask to the pool a custom color border
        //keep reference inside a hastable inside the pool
        //use a internal thread to clear unused custom color border
        if (OyoahaUtilities.isOpaque(c) && c.getBackground()!=null && !(c.getBackground() instanceof UIResource))
        {  
            images = pool.getImages(state, c.getBackground());
        }
        else
        {
            images = pool.getImages(state);
        }

        if(images==null)
        {
            return;
        }

        Insets borderInsets = getBorderInsets(c);

        drawHorizontal(c, g, images[4], images[6], x, y, width, height, borderInsets);
        drawVertical(c, g, images[5], images[7], x, y, width, height, borderInsets);

        if(images[0]!=null)
        {
            Shape s = OyoahaUtilities.normalizeClip(g, x, y, borderInsets.left, borderInsets.top);
            g.drawImage(images[0], x, y, null);
            g.setClip(s);
        }

        if(images[1]!=null)
        {
            Shape s = OyoahaUtilities.normalizeClip(g, x, y+(height-borderInsets.bottom), borderInsets.left, borderInsets.bottom);
            g.drawImage(images[1], x, y+(height-borderInsets.bottom), null);
            g.setClip(s);
        }

        if(images[2]!=null)
        {
            Shape s = OyoahaUtilities.normalizeClip(g, x+(width-borderInsets.right), y+(height-borderInsets.bottom), borderInsets.right, borderInsets.bottom);
            g.drawImage(images[2], x+(width-borderInsets.right), y+(height-borderInsets.bottom), null);
            g.setClip(s);
        }

        if(images[3]!=null)
        {
            Shape s = OyoahaUtilities.normalizeClip(g, x+(width-borderInsets.right), y, borderInsets.right, borderInsets.top);
            g.drawImage(images[3], x+(width-borderInsets.right), y, null);
            g.setClip(s);
        }
    }

    private void drawHorizontal(Component c, Graphics g, Image top, Image bottom, int _x, int _y, int _width, int _height, Insets insets)
    {
        Shape s = OyoahaUtilities.normalizeClip(g, _x+insets.left, _y, _width-(insets.left+insets.right), insets.top);

        if(top!=null)
        {
            int w2 = top.getWidth(null);

            for(int i=0;i<_width;i+=w2)
            {
                g.drawImage(top, i, _y, null);
            }
        }

        g.setClip(s);
        int w = (_height-insets.bottom)+_y;
        s = OyoahaUtilities.normalizeClip(g, _x+insets.left, w, _width-(insets.left+insets.right), insets.bottom);

        if(bottom!=null)
        {
            int w2 = bottom.getWidth(null);

            for(int i=0;i<_width;i+=w2)
            {
                g.drawImage(bottom, i, w, null);
            }
        }

        g.setClip(s);
    }

    private void drawVertical(Component c, Graphics g, Image left, Image right, int _x, int _y, int _width, int _height, Insets insets)
    {
        Shape s = OyoahaUtilities.normalizeClip(g, _x, _y+insets.top, insets.left, _height-(insets.top+insets.bottom));

        if(left!=null)
        {
            int h2 = left.getHeight(null);

            for(int i=0;i<_height;i+=h2)
            {
                g.drawImage(left, _x, i, null);
            }
        }

        g.setClip(s);
        int h = (_width-insets.right)+_x;
        s = OyoahaUtilities.normalizeClip(g, h, _y+insets.top, insets.right, _height-(insets.top+insets.bottom));

        if(right!=null)
        {
            int h2 = right.getHeight(null);

            for(int i=0;i<_height;i+=h2)
            {
                g.drawImage(right, h, i, null);
            }
        }

        g.setClip(s);
    }
}