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
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaLayerBackground implements OyoahaBackgroundObject, OyoahaFlash, UIResource
{
    protected OyoahaBackgroundObject one;
    protected OyoahaBackgroundObject two;
    protected OyoahaBackgroundObject three;

    public OyoahaLayerBackground(OyoahaBackgroundObject one, OyoahaBackgroundObject two, OyoahaBackgroundObject three)
    {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, int state)
    {
        if(one!=null)
        {
            one.paintBackground(g, c, x, y, width, height, state);
        }

        if(two!=null)
        {
            two.paintBackground(g, c, x, y, width, height, state);
        }

        if(three!=null)
        {
            three.paintBackground(g, c, x, y, width, height, state);
        }
    }

    public boolean isOpaque()
    {
        if(one!=null)
        {
            if(!one.isOpaque())
            return false;
        }

        if(two!=null)
        {
            if(!two.isOpaque())
            return false;
        }

        if(three!=null)
        {
            if(!three.isOpaque())
            return false;
        }

        return true;
    }

    public void update()
    {
        if(one!=null && one instanceof OyoahaFlash)
        {
            ((OyoahaFlash)one).update();
        }

        if(two!=null && two instanceof OyoahaFlash)
        {
            ((OyoahaFlash)two).update();
        }

        if(three!=null && three instanceof OyoahaFlash)
        {
            ((OyoahaFlash)three).update();
        }
    }

    public void reset()
    {
        if(one!=null && one instanceof OyoahaFlash)
        {
            ((OyoahaFlash)one).reset();
        }

        if(two!=null && two instanceof OyoahaFlash)
        {
            ((OyoahaFlash)two).reset();
        }

        if(three!=null && three instanceof OyoahaFlash)
        {
            ((OyoahaFlash)three).reset();
        }
    }
}