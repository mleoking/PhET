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

public class OyoahaButtonTexturePool implements OyoahaPool, OyoahaThemeSchemeListener
{
  //Image source
  protected Image enabled;
  protected Image selected;

  //Image generated
  protected Image unselected_enabled;   //UNSELECTED_ENABLED
  protected Image selected_enabled;     //SELECTED_ENABLED
  protected Image unselected_rollover;  //UNSELECTED_ROLLOVER
  protected Image selected_rollover;    //SELECTED_ROLLOVER
  protected Image unselected_disabled;  //UNSELECTED_DISABLED
  protected Image selected_disabled;    //SELECTED_DISABLED
  protected Image unselected_pressed;   //UNSELECTED_PRESSED
  protected Image selected_pressed;     //SELECTED_PRESSED

  //oyoaha theme scheme to have right color
  protected OyoahaThemeScheme scheme;

  //force selected to use the same color than enabled
  protected boolean useEnabledColorForSelected;
  //force the unselected_pressed to null
  protected boolean generateUnselectedPressed;

  //get the insets border in cache
  protected Dimension cachedSize;

  /**
   * enabled and selected can't be null
   */
  public OyoahaButtonTexturePool(OyoahaLookAndFeel lnf, Image enabled, Image selected, Boolean useEnabledColorForSelected, Boolean generateUnselectedPressed)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.enabled = enabled;
    this.selected = selected;
    this.useEnabledColorForSelected = useEnabledColorForSelected.booleanValue();
    this.generateUnselectedPressed = generateUnselectedPressed.booleanValue();

    init(true, true, true, true, true);
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
    return (Image) OyoahaStateRule.getObject(state, unselected_enabled, selected_enabled, unselected_pressed, selected_pressed, unselected_rollover, selected_rollover, unselected_disabled, selected_disabled);
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public void updateThemeScheme(OyoahaThemeScheme scheme, OyoahaThemeSchemeChanged changed)
  {
    this.scheme = scheme;
    disposeDirty(changed.enabled, changed.selected, changed.rollover, changed.disabled, changed.pressed);
    init(changed.enabled, changed.selected, changed.rollover, changed.disabled, changed.pressed);
  }

  protected void disposeDirty(boolean enabled, boolean selected, boolean rollover, boolean disabled, boolean pressed)
  {
    if(!enabled && !selected && !rollover && !disabled && !pressed)
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

      if(useEnabledColorForSelected && selected_enabled!=null)
      {
        selected_enabled.flush();
        selected_enabled = null;
      }
    }

    if(selected)
    {
      if(selected_enabled!=null && !useEnabledColorForSelected) //only is useEnabledColorForSelected is false
      {
        selected_enabled.flush();
        selected_enabled = null;
      }
    }

    if(rollover)
    {
      if(unselected_rollover!=null)
      {
        unselected_rollover.flush();
        unselected_rollover = null;
      }

      if(selected_rollover!=null)
      {
        selected_rollover.flush();
        selected_rollover = null;
      }
    }

    if(disabled)
    {
      if(unselected_disabled!=null)
      {
        unselected_disabled.flush();
        unselected_disabled = null;
      }

      if(selected_disabled!=null)
      {
        selected_disabled.flush();
        selected_disabled = null;
      }
    }

    if(pressed)
    {
      if(unselected_pressed!=null)
      {
        unselected_pressed.flush();
        unselected_pressed = null;
      }

      if(selected_pressed!=null)
      {
        selected_pressed.flush();
        selected_pressed = null;
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

    if(selected!=null)
    {
      selected.flush();
      selected = null;
    }

    disposeDirty(true, true, true, true, true);
  }

  protected void init(boolean _enabled, boolean _selected, boolean _rollover, boolean _disabled, boolean _pressed)
  {
    if(!_enabled && !_selected && !_rollover && !_disabled && !_pressed)
    {
      return;
    }

    if(selected!=null)
    {
      int enabled_w = this.enabled.getWidth(null);
      int enabled_h = this.enabled.getHeight(null);

      int selected_w = this.selected.getWidth(null);
      int selected_h = this.selected.getHeight(null);

      int[] enabled_source = new int[enabled_w * enabled_h];
      int[] selected_source = new int[selected_w * selected_h];

      try
      {
        PixelGrabber pg1 = new PixelGrabber(enabled, 0, 0, enabled_w, enabled_h, enabled_source, 0, enabled_w);
        pg1.grabPixels();
        PixelGrabber pg2 = new PixelGrabber(selected, 0, 0, selected_w, selected_h, selected_source, 0, selected_w);
        pg2.grabPixels();
      }
      catch(Exception exception)
      {

      }

      if(_enabled)
      {
        if(scheme.isCustomEnabledColor())
        {
          unselected_enabled = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getEnabled(), 100);
        }
        else
        {
          unselected_enabled = OyoahaUtilities.loadImage(enabled_source, enabled_w, enabled_h);
        }

        if(useEnabledColorForSelected)
        {
          if(scheme.isCustomEnabledColor())
          {
            selected_enabled = OyoahaPoolUtilities.getColorizedImage(selected_source, selected_w, selected_h, scheme.getEnabled(), 100);
          }
          else
          {
            selected_enabled = OyoahaUtilities.loadImage(selected_source, selected_w, selected_h);
          }
        }
      }

      if(_selected && !useEnabledColorForSelected)
      {
        selected_enabled = OyoahaPoolUtilities.getColorizedImage(selected_source, selected_w, selected_h, scheme.getSelected(), 50);
      }

      if(_rollover)
      {
        unselected_rollover = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getRollover(), 100);
        selected_rollover = OyoahaPoolUtilities.getColorizedImage(selected_source, selected_w, selected_h, scheme.getRollover(), 100);
      }

      if(_pressed)
      {
        if(generateUnselectedPressed)
        {
          unselected_pressed = OyoahaPoolUtilities.getColorizedImage(selected_source, selected_w, selected_h, scheme.getPressed(), 100);
        }
        else
        {
          unselected_pressed = null;
        }

        selected_pressed = OyoahaPoolUtilities.getColorizedImage(selected_source, selected_w, selected_h, scheme.getPressed(), 100);
      }

      if(_disabled)
      {
        unselected_disabled = OyoahaPoolUtilities.getColorizedImage3(enabled_source, enabled_w, enabled_h, /*scheme.getDisabled()*/scheme.getEnabled());
        selected_disabled = OyoahaPoolUtilities.getColorizedImage3(selected_source, selected_w, selected_h, /*scheme.getDisabled()*//*scheme.getEnabled()*/scheme.getSelected());
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
        if(scheme.isCustomEnabledColor())
        {
          unselected_enabled = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getEnabled(), 100);
        }
        else
        {
          unselected_enabled = OyoahaUtilities.loadImage(enabled_source, enabled_w, enabled_h);
        }
      }

      if(_selected && !useEnabledColorForSelected)
      {
        selected_enabled = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getSelected(), 50);
      }

      if(_rollover)
      {
        unselected_rollover = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getRollover(), 100);
      }

      if(_pressed)
      {
        if(generateUnselectedPressed)
        {
          unselected_pressed = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getPressed(), 100);
        }
        else
        {
          unselected_pressed = null;
        }

        selected_pressed = OyoahaPoolUtilities.getColorizedImage(enabled_source, enabled_w, enabled_h, scheme.getPressed(), 100);
      }

      if(_disabled)
      {
        unselected_disabled = OyoahaPoolUtilities.getColorizedImage3(enabled_source, enabled_w, enabled_h, /*scheme.getDisabled()*/scheme.getEnabled());
      }
    }
  }
}