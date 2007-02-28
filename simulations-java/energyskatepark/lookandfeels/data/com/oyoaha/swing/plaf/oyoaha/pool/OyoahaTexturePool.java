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

package com.oyoaha.swing.plaf.oyoaha.pool;

import java.awt.*;
import java.awt.image.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTexturePool implements OyoahaPool, OyoahaThemeSchemeListener
{
  //image source
  protected Image enabled;

  //Image generated
  protected Image unselected_enabled;

  //oyoaha theme scheme to have right color
  protected OyoahaThemeScheme scheme;

  //get the insets border in cache
  protected Dimension cachedSize;

  public OyoahaTexturePool(OyoahaLookAndFeel lnf, Image enabled)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();
    this.enabled = enabled;
    init();
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public Dimension getSize()
  {
    if(cachedSize==null)
    {
      int w = enabled.getWidth(null);
      int h = enabled.getHeight(null);

      cachedSize = new Dimension(w,h);
    }

    return cachedSize;
  }

  public int getWidth()
  {
    return getSize().width;
  }

  public int getHeight()
  {
    return getSize().height;
  }

  public Image getImage(int state)
  {
    if(unselected_enabled!=null)
    return unselected_enabled;
    else
    return enabled;
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public void updateThemeScheme(OyoahaThemeScheme scheme, OyoahaThemeSchemeChanged changed)
  {
    this.scheme = scheme;

    if(changed.enabled)
    {
      disposeDirty();
      init();
    }
  }

  protected void disposeDirty()
  {
    if(unselected_enabled!=null)
    {
      unselected_enabled.flush();
      unselected_enabled = null;
    }
  }

  public void dispose()
  {
    if(enabled!=null)
    {
      enabled.flush();
      enabled = null;
    }

    disposeDirty();
  }

  protected void init()
  {
    if(!scheme.isCustomEnabledColor())
    {
      return;
    }

    int w = enabled.getWidth(null);
    int h = enabled.getHeight(null);

    int[] source = new int[w*h];

    try
    {
      PixelGrabber pg = new PixelGrabber(enabled, 0, 0, w, h, source, 0, w);
      pg.grabPixels();
    }
    catch(Exception exception)
    {
    
    }

    unselected_enabled = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getEnabled());
  }
}