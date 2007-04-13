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

package com.oyoaha.swing.plaf.oyoaha.flash;

import java.awt.*;

import com.oyoaha.swing.plaf.oyoaha.*;
import com.oyoaha.swing.plaf.oyoaha.pool.*;
import com.oyoaha.swing.plaf.oyoaha.background.*;

public class OyoahaFlashImageBackground extends OyoahaImageBackground implements OyoahaFlash
{
  protected int currentW = -1;
  protected int currentH = -1;

  protected int stepX;
  protected int stepY;

  protected boolean wup;
  protected boolean hup;

  public OyoahaFlashImageBackground(OyoahaPool pool, Integer pos, Integer stepX, Integer stepY, OyoahaStateRule rule)
  {
    super(pool, pos, rule);

    this.stepX = stepX.intValue();
    this.stepY = stepY.intValue();
  }

  public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, int status)
  {
    Image i = pool.getImage(status);

    if(i!=null)
    {
      if(currentW==-1)
      {
        currentW = i.getWidth(c);
        currentH = i.getHeight(c);
      }

      if(wup)
      {
        if(currentW>=i.getWidth(c))
        {
          currentW = i.getWidth(c);
          wup = false;
        }
      }
      else
      {
        if(currentW<=0)
        {
          currentW = 0;
          wup = true;
        }
      }

      if(hup)
      {
        if(currentH>=i.getHeight(c))
        {
          currentH = i.getHeight(c);
          hup = false;
        }
      }
      else
      {
        if(currentH<=0)
        {
          currentH = 0;
          hup = true;
        }
      }

      OyoahaUtilities.paintAImageAt(g, c, x, y, width, height, i, pos, currentW, currentH);
    }
    else
    {
      OyoahaUtilities.paintColorBackground(g, c, x, y, width, height, OyoahaUtilities.getBackground(c), status);
    }
  }

  public void update()
  {
    if(wup)
    {
      currentW += stepX;
    }
    else
    {
      currentW -= stepX;
    }

    if(hup)
    {
      currentH += stepY;
    }
    else
    {
      currentH -= stepY;
    }
  }

  public void reset()
  {
    currentW = currentH = -1;
    hup = false;
    wup = false;
  }
}