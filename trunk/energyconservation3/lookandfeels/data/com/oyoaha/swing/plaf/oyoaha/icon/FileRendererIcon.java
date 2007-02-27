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

import javax.swing.*;
import java.awt.*;

public class FileRendererIcon implements Icon
{
    public Icon icon;
    public Color color;
    protected int size;

    public FileRendererIcon()
    {
        size = 24;
    }
    
    public FileRendererIcon(int size)
    {
        this.size = size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        if (color!=null)
        {
            g.setColor(color);
            g.fillRect(x, y, getIconWidth()-1, getIconHeight()-1);
        }
    
        if (icon!=null)
        {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
        
            if(w<size)
            x += (size-w)/2;
    
            if(h<size)
            y += (size-h)/2;    
        
            icon.paintIcon(c, g, x, y);
        }
    }

    public int getIconWidth()
    {
        return (icon!=null && icon.getIconWidth()>size)? icon.getIconWidth() : size;
    }

    public int getIconHeight()
    {
        return (icon!=null && icon.getIconHeight()>size)? icon.getIconHeight() : size;
    }
}