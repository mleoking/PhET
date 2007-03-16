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

public class OyoahaContainerBorder implements Border, OyoahaFlash, UIResource
{
    protected Border enabled;
    protected Border pressed;
    protected Border disabled;
    protected Border selected;
    protected Border rollover;

    protected OyoahaStateRule rule;

    public OyoahaContainerBorder(Border enabled, Border pressed, Border disabled, Border selected, Border rollover, OyoahaStateRule rule)
    {
        this.enabled = enabled;
        this.pressed = pressed;
        this.disabled = disabled;
        this.selected = selected;
        this.rollover = rollover;

        this.rule = rule;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        int state = rule.getState(OyoahaUtilities.getStatus(c));

        if(state==OyoahaUtilities.UNVISIBLE)
        {
            return;
        }

        Border b = (Border)OyoahaStateRule.getObject(state, enabled, selected, pressed, null, rollover, null, disabled, null);

        if(b!=null)
        {
            b.paintBorder(c, g, x, y, w, h);
        }
    }

    public Insets getBorderInsets(Component c)
    {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }

    public Insets getBorderInsets(Component c, Insets insets)
    {
        Insets tmp = new Insets(0,0,0,0);

        if(enabled!=null)
        {
            if(enabled instanceof AbstractBorder)
            ((AbstractBorder)enabled).getBorderInsets(c, tmp);
            else
            tmp = enabled.getBorderInsets(c);

            insets.top = Math.max(insets.top, tmp.top);
            insets.bottom = Math.max(insets.bottom, tmp.bottom);
            insets.left = Math.max(insets.left, tmp.left);
            insets.right = Math.max(insets.right, tmp.right);
        }

        if(pressed!=null)
        {
            if(pressed instanceof AbstractBorder)
            ((AbstractBorder)pressed).getBorderInsets(c, tmp);
            else
            tmp = pressed.getBorderInsets(c);

            insets.top = Math.max(insets.top, tmp.top);
            insets.bottom = Math.max(insets.bottom, tmp.bottom);
            insets.left = Math.max(insets.left, tmp.left);
            insets.right = Math.max(insets.right, tmp.right);
        }

        if(disabled!=null)
        {
            if(disabled instanceof AbstractBorder)
            ((AbstractBorder)disabled).getBorderInsets(c, tmp);
            else
            tmp = disabled.getBorderInsets(c);

            insets.top = Math.max(insets.top, tmp.top);
            insets.bottom = Math.max(insets.bottom, tmp.bottom);
            insets.left = Math.max(insets.left, tmp.left);
            insets.right = Math.max(insets.right, tmp.right);
        }

        if(selected!=null)
        {
            if(selected instanceof AbstractBorder)
            ((AbstractBorder)selected).getBorderInsets(c, tmp);
            else
            tmp = selected.getBorderInsets(c);

            insets.top = Math.max(insets.top, tmp.top);
            insets.bottom = Math.max(insets.bottom, tmp.bottom);
            insets.left = Math.max(insets.left, tmp.left);
            insets.right = Math.max(insets.right, tmp.right);
        }

        if(rollover!=null)
        {
            if(rollover instanceof AbstractBorder)
            ((AbstractBorder)rollover).getBorderInsets(c, tmp);
            else
            tmp = rollover.getBorderInsets(c);

            insets.top = Math.max(insets.top, tmp.top);
            insets.bottom = Math.max(insets.bottom, tmp.bottom);
            insets.left = Math.max(insets.left, tmp.left);
            insets.right = Math.max(insets.right, tmp.right);
        }

        return insets;
    }

    public boolean isBorderOpaque()
    {
        boolean bool = enabled.isBorderOpaque() && pressed.isBorderOpaque();

        if(bool)
        {
            if(selected!=null)
            {
                bool = selected.isBorderOpaque();
                if(!bool)
                return false;
            }

            if(rollover!=null)
            {
                bool = rollover.isBorderOpaque();
                if(!bool)
                return false;
            }

            if(disabled!=null)
            {
                bool = disabled.isBorderOpaque();
                if(!bool)
                return false;
            }
        }

        return bool;
    }

    public void update()
    {
        if(rollover!=null && rollover instanceof OyoahaFlash)
        ((OyoahaFlash)rollover).update();

        if(pressed!=null && pressed instanceof OyoahaFlash)
        ((OyoahaFlash)pressed).update();
    }

    public void reset()
    {
        if(rollover!=null && rollover instanceof OyoahaFlash)
        ((OyoahaFlash)rollover).reset();

        if(pressed!=null && pressed instanceof OyoahaFlash)
        ((OyoahaFlash)pressed).reset();
    }
}