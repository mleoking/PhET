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

public class OyoahaDefaultImagePool implements OyoahaPool, OyoahaThemeSchemeListener
{
  //Image source
  protected Image u_enabled;
  protected Image s_enabled;
  protected Image u_rollover;
  protected Image s_rollover;
  protected Image u_pressed;
  protected Image s_pressed;
  protected Image u_disabled;
  protected Image s_disabled;

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

  public OyoahaDefaultImagePool(OyoahaLookAndFeel lnf, Image u_enabled, Image s_enabled, Image u_rollover, Image s_rollover, Image u_disabled, Image s_disabled, Image u_pressed, Image s_pressed, Boolean useEnabledColorForSelected, Boolean generateUnselectedPressed)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.u_enabled = u_enabled;
    this.s_enabled = s_enabled;
    this.u_rollover = u_rollover;
    this.s_rollover = s_rollover;
    this.u_disabled = u_disabled;
    this.s_disabled = s_disabled;
    this.u_pressed = u_pressed;
    this.s_pressed = s_pressed;
    this.useEnabledColorForSelected = useEnabledColorForSelected.booleanValue();
    this.generateUnselectedPressed = generateUnselectedPressed.booleanValue();

    init(true, true, true, true, true);
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public Dimension getSize() //use only the enabled image
  {
    if(cachedSize==null)
    {
      Image tmp = getFirstNonNullImage();

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        cachedSize = new Dimension(w,h);
      }
      else
      {
        cachedSize = new Dimension(0,0);
      }
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

  protected Image getFirstNonNullImage()
  {
    if(u_enabled!=null)
    return u_enabled;

    if(s_enabled!=null)
    return s_enabled;

    if(u_rollover!=null)
    return u_rollover;

    if(s_rollover!=null)
    return s_rollover;

    if(u_disabled!=null)
    return u_disabled;

    if(s_disabled!=null)
    return s_disabled;

    if(u_pressed!=null)
    return u_pressed;

    return s_pressed;
  }

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
    if(u_enabled!=null)
    {
      u_enabled.flush();
      u_enabled = null;
    }

    if(s_enabled!=null)
    {
      s_enabled.flush();
      s_enabled = null;
    }

    if(u_rollover!=null)
    {
      u_rollover.flush();
      u_rollover = null;
    }

    if(s_rollover!=null)
    {
      s_rollover.flush();
      s_rollover = null;
    }

    if(u_pressed!=null)
    {
      u_pressed.flush();
      u_pressed = null;
    }

    if(s_pressed!=null)
    {
      s_pressed.flush();
      s_pressed = null;
    }

    if(u_disabled!=null)
    {
      u_disabled.flush();
      u_disabled = null;
    }

    if(s_disabled!=null)
    {
      s_disabled.flush();
      s_disabled = null;
    }

    disposeDirty(true, true, true, true, true);
  }

  protected void init(boolean _enabled, boolean _selected, boolean _rollover, boolean _disabled, boolean _pressed)
  {
    if(!_enabled && !_selected && !_rollover && !_disabled && !_pressed)
    {
      return;
    }

    if(_enabled)
    {
      Image tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_ENABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        if(scheme.isCustomEnabledColor())
        {
          unselected_enabled = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getEnabled());
        }
        else //simply recreate the image
        {
          unselected_enabled = OyoahaUtilities.loadImage(source, w, h);
        }
      }

      if(useEnabledColorForSelected)
      {
        tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_ENABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

        if(tmp!=null)
        {
          int w = tmp.getWidth(null);
          int h = tmp.getHeight(null);

          int[] source = new int[w * h];

          try
          {
            PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
            pg.grabPixels();
          }
          catch(Exception exception)
          {

          }

          if(scheme.isCustomEnabledColor())
          {
            selected_enabled = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getEnabled());
          }
          else //simply recreate the image
          {
            selected_enabled = OyoahaUtilities.loadImage(source, w, h);
          }
        }
      }
    }

    if(_selected && !useEnabledColorForSelected)
    {
      Image tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_ENABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        selected_enabled = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getSelected());
      }
    }

    if(_pressed)
    {
      Image tmp = null;

      if(generateUnselectedPressed)
      {
        tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_PRESSED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

        if(tmp!=null)
        {
          int w = tmp.getWidth(null);
          int h = tmp.getHeight(null);

          int[] source = new int[w * h];

          try
          {
            PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
            pg.grabPixels();
          }
          catch(Exception exception)
          {

          }

          unselected_pressed = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getPressed());
        }
      }
      else
      {
        unselected_pressed = null;
      }

      tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_PRESSED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        selected_pressed = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getPressed());
      }
    }

    if(_rollover)
    {
      Image tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_ROLLOVER, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        unselected_rollover = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getRollover());
      }

      tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_ROLLOVER, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        selected_rollover = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getRollover());
      }
    }

    if(_disabled)
    {
      Image tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_DISABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        unselected_disabled = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getDisabled());
      }

      tmp = (Image)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_DISABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);

      if(tmp!=null)
      {
        int w = tmp.getWidth(null);
        int h = tmp.getHeight(null);

        int[] source = new int[w * h];

        try
        {
          PixelGrabber pg = new PixelGrabber(tmp, 0, 0, w, h, source, 0, w);
          pg.grabPixels();
        }
        catch(Exception exception)
        {

        }

        selected_disabled = OyoahaPoolUtilities.getColorizedImage2(source, w, h, scheme.getDisabled());
      }
    }
  }
}