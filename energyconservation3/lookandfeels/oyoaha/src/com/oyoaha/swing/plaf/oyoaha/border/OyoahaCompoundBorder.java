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

public class OyoahaCompoundBorder implements Border, OyoahaFlash, UIResource
{
    protected Border outsideBorder;
    protected Border insideBorder;

    public OyoahaCompoundBorder(Border outsideBorder, Border insideBorder)
    {
        this.outsideBorder = outsideBorder;
        this.insideBorder = insideBorder;
    }

    public boolean isBorderOpaque()
    {
        return(outsideBorder != null && outsideBorder.isBorderOpaque()) && (insideBorder != null && insideBorder.isBorderOpaque());
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        Insets  nextInsets;
        int px, py, pw, ph;

        px = x;
        py = y;
        pw = width;
        ph = height;

        if(outsideBorder != null)
        {
            outsideBorder.paintBorder(c, g, px, py, pw, ph);

            nextInsets = outsideBorder.getBorderInsets(c);
            px += nextInsets.left;
            py += nextInsets.top;
            pw = pw - nextInsets.right - nextInsets.left;
            ph = ph - nextInsets.bottom - nextInsets.top;
        }

        if(insideBorder != null)
        {
            insideBorder.paintBorder(c, g, px, py, pw, ph);
        }
    }

    public Insets getBorderInsets(Component c)
    {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }

    public Insets getBorderInsets(Component c, Insets insets)
    {
        Insets  nextInsets;

        insets.top = insets.left = insets.right = insets.bottom = 0;

        if(outsideBorder != null)
        {
            nextInsets = outsideBorder.getBorderInsets(c);
            insets.top += nextInsets.top;
            insets.left += nextInsets.left;
            insets.right += nextInsets.right;
            insets.bottom += nextInsets.bottom;
        }

        if(insideBorder != null)
        {
            nextInsets = insideBorder.getBorderInsets(c);
            insets.top += nextInsets.top;
            insets.left += nextInsets.left;
            insets.right += nextInsets.right;
            insets.bottom += nextInsets.bottom;
        }

        return insets;
    }

    public void update()
    {
        if(outsideBorder!=null && outsideBorder instanceof OyoahaFlash)
        ((OyoahaFlash)outsideBorder).update();

        if(insideBorder!=null && insideBorder instanceof OyoahaFlash)
        ((OyoahaFlash)insideBorder).update();
    }

    public void reset()
    {
        if(outsideBorder!=null && outsideBorder instanceof OyoahaFlash)
        ((OyoahaFlash)outsideBorder).reset();

        if(insideBorder!=null && insideBorder instanceof OyoahaFlash)
        ((OyoahaFlash)insideBorder).reset();
    }
}