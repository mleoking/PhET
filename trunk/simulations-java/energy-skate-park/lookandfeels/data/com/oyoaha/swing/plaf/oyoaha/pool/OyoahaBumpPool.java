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

public class OyoahaBumpPool implements OyoahaPool, OyoahaThemeSchemeListener
{
  //Image source
  protected Image enabled;
  protected Image rollover;

  //Image generated
  protected Image unselected_enabled;   //UNSELECTED_ENABLED
  protected Image unselected_rollover;  //UNSELECTED_ROLLOVER
  protected Image unselected_disabled;  //UNSELECTED_DISABLED
  protected Image unselected_pressed;   //UNSELECTED_PRESSED

  //oyoaha theme scheme to have right color
  protected OyoahaThemeScheme scheme;

  //get the insets border in cache
  protected Dimension cachedSize;

  public OyoahaBumpPool(OyoahaLookAndFeel lnf, Image enabled, Image rollover)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.enabled = enabled;
    this.rollover = rollover;

    init(true, true, true, true);
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public Dimension getSize() //use only the enabled image
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
    return (Image)OyoahaStateRule.getObject(state, unselected_enabled, null, unselected_pressed, null, unselected_rollover, null, unselected_disabled, null);
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public void updateThemeScheme(OyoahaThemeScheme scheme, OyoahaThemeSchemeChanged changed)
  {
    this.scheme = scheme;
    disposeDirty(changed.enabled, changed.rollover, changed.disabled, changed.pressed);
    init(changed.enabled, changed.rollover, changed.disabled, changed.pressed);
  }

  protected void disposeDirty(boolean enabled, boolean rollover, boolean disabled, boolean pressed)
  {
    if(!enabled && !rollover && !disabled && !pressed)
    {
      return;
    }

    if(enabled)
    {
      if(unselected_enabled!=null)
      {
        unselected_enabled.flush();
        unselected_enabled = null;
      }
    }

    if(rollover)
    {
      if(unselected_rollover!=null)
      {
        unselected_rollover.flush();
        unselected_rollover = null;
      }
    }

    if(disabled)
    {
      if(unselected_disabled!=null)
      {
        unselected_disabled.flush();
        unselected_disabled = null;
      }
    }

    if(pressed)
    {
      if(unselected_pressed!=null)
      {
        unselected_pressed.flush();
        unselected_pressed = null;
      }
    }
  }

  public void dispose()
  {
    if(enabled!=null)
    {
      enabled.flush();
      enabled = null;
    }

    if(rollover!=null)
    {
      rollover.flush();
      rollover = null;
    }

    disposeDirty(true, true, true, true);
  }

  protected void init(boolean _enabled, boolean _rollover, boolean _disabled, boolean _pressed)
  {
    if(!_enabled && !_rollover && !_disabled && !_pressed)
    {
      return;
    }

    if(rollover!=null)
    {
      int enabled_w = this.enabled.getWidth(null);
      int enabled_h = this.enabled.getHeight(null);

      int rollover_w = this.rollover.getWidth(null);
      int rollover_h = this.rollover.getHeight(null);

      int[] enabled_source = new int[enabled_w * enabled_h];
      int[] rollover_source = new int[rollover_w * rollover_h];

      try
      {
        PixelGrabber pg1 = new PixelGrabber(enabled, 0, 0, enabled_w, enabled_h, enabled_source, 0, enabled_w);
        pg1.grabPixels();
        PixelGrabber pg2 = new PixelGrabber(rollover, 0, 0, rollover_w, rollover_h, rollover_source, 0, rollover_w);
        pg2.grabPixels();
      }
      catch(Exception exception)
      {

      }

      if(_enabled)
      {
        unselected_enabled = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getBlack(), scheme.getWhite(), scheme.getGray());
      }

      if(_rollover)
      {
        unselected_rollover = OyoahaPoolUtilities.createImageFromBump(rollover_source, enabled_w, enabled_h, scheme.getRollover(), scheme.getWhite(), scheme.getGray());
      }

      if(_pressed)
      {
        unselected_pressed = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getPressed(), scheme.getWhite(), scheme.getGray());
      }

      if(_disabled)
      {
        unselected_disabled = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getDisabled(), scheme.getWhite(), scheme.getGray());
      }
    }
    else
    {
      int enabled_w = this.enabled.getWidth(null);
      int enabled_h = this.enabled.getHeight(null);

      int[] enabled_source = new int[enabled_w * enabled_h];

      try
      {
        PixelGrabber pg1 = new PixelGrabber(enabled, 0, 0, enabled_w, enabled_h, enabled_source, 0, enabled_w);
        pg1.grabPixels();
      }
      catch(Exception exception)
      {

      }

      if(_enabled)
      {
        unselected_enabled = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getBlack(), scheme.getWhite(), scheme.getGray());
      }

      if(_rollover)
      {
        unselected_rollover = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getRollover(), scheme.getWhite(), scheme.getGray());
      }

      if(_pressed)
      {
        unselected_pressed = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getPressed(), scheme.getWhite(), scheme.getGray());
      }

      if(_disabled)
      {
        unselected_disabled = OyoahaPoolUtilities.createImageFromBump(enabled_source, enabled_w, enabled_h, scheme.getDisabled(), scheme.getWhite(), scheme.getGray());
      }
    }
  }
}