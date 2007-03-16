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

public class OyoahaButtonBorderPool implements OyoahaSlicePool, OyoahaThemeSchemeListener
{
  //Image source
  protected Image enabled;
  protected Image selected;

  //Image generated
  protected Image[] unselected_enabled;   //UNSELECTED_ENABLED
  protected Image[] selected_enabled;     //SELECTED_ENABLED
  protected Image[] unselected_rollover;  //UNSELECTED_ROLLOVER
  protected Image[] selected_rollover;    //SELECTED_ROLLOVER
  protected Image[] unselected_disabled;  //UNSELECTED_DISABLED
  protected Image[] selected_disabled;    //SELECTED_DISABLED
  protected Image[] unselected_pressed;   //UNSELECTED_PRESSED
  protected Image[] selected_pressed;     //SELECTED_PRESSED

  //oyoaha theme scheme to have right color
  protected OyoahaThemeScheme scheme;

  //get the insets border in cache
  protected Insets cached;

  //force selected to use the same color than enabled
  protected boolean useEnabledColorForSelected;
  //force the unselected_pressed to null
  protected boolean generateUnselectedPressed;

  /**
   * enabled can't be null;
   */
  public OyoahaButtonBorderPool(OyoahaLookAndFeel lnf, Image enabled, Image selected, Boolean useEnabledColorForSelected, Boolean generateUnselectedPressed)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.enabled = enabled;
    this.selected = selected;
    this.useEnabledColorForSelected = useEnabledColorForSelected.booleanValue();
    this.generateUnselectedPressed = generateUnselectedPressed.booleanValue();

    init(true, true, true, true, true);
  }

  public OyoahaButtonBorderPool(OyoahaLookAndFeel lnf, Insets cached, Image enabled, Image selected, Boolean useEnabledColorForSelected, Boolean generateUnselectedPressed)
  {
    this.cached = cached;

    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.enabled = enabled;
    this.selected = selected;
    this.useEnabledColorForSelected = useEnabledColorForSelected.booleanValue();
    this.generateUnselectedPressed = generateUnselectedPressed.booleanValue();

    init(true, true, true, true, true);
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public Insets getBorderInsets()
  {
    return cached;
  }

  public Insets getBorderInsets(Insets insets)
  {
    if (insets==null)
    {
      insets = new Insets(0,0,0,0);
    }

    if(cached==null)
    {
      return insets;
    }

    insets.left = cached.left;
    insets.bottom = cached.bottom;
    insets.right = cached.right;
    insets.top = cached.top;

    return insets;
  }

  public Image[] getImages(int state)
  {
    return (Image[])OyoahaStateRule.getObject(state, unselected_enabled, selected_enabled, unselected_pressed, selected_pressed, unselected_rollover, selected_rollover, unselected_disabled, selected_disabled);
  }
  
  protected java.util.Hashtable customColorHastable;
  
  public Image[] getImages(int state, Color customColor)
  {
    Image[] unselected_enabled = this.unselected_enabled;
    
    if (customColorHastable==null)
    {
        customColorHastable = new java.util.Hashtable();
        
        //built the new image border
        unselected_enabled = initCustomColor(customColor);
        customColorHastable.put(customColor.toString(), unselected_enabled);
    }
    else
    {
        if (customColorHastable.containsKey(customColor.toString()))
        {
            unselected_enabled = (Image[])customColorHastable.get(customColor.toString());
        }
        else
        {
            //built the new image border
            unselected_enabled = initCustomColor(customColor);
            customColorHastable.put(customColor.toString(), unselected_enabled);
        }
    }
    
    
    return (Image[])OyoahaStateRule.getObject(state, unselected_enabled, selected_enabled, unselected_pressed, selected_pressed, unselected_rollover, selected_rollover, unselected_disabled, selected_disabled);
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
        for(int i=0;i<unselected_enabled.length;i++)
        {
          if(unselected_enabled[i]!=null)
          {
            unselected_enabled[i].flush();
            unselected_enabled[i] = null;
          }
        }

        unselected_enabled = null;
      }

      if(useEnabledColorForSelected && selected_enabled!=null) //selected must use the same color than enabled
      {
        for(int i=0;i<selected_enabled.length;i++)
        {
          if(selected_enabled[i]!=null)
          {
            selected_enabled[i].flush();
            selected_enabled[i] = null;
          }
        }

        selected_enabled = null;
      }
    }

    if(selected)
    {
      if(selected_enabled!=null && !useEnabledColorForSelected) //only is useEnabledColorForSelected is false
      {
        for(int i=0;i<selected_enabled.length;i++)
        {
          if(selected_enabled[i]!=null)
          {
            selected_enabled[i].flush();
            selected_enabled[i] = null;
          }
        }

        selected_enabled = null;
      }
    }

    if(rollover)
    {
      if(unselected_rollover!=null)
      {
        for(int i=0;i<unselected_rollover.length;i++)
        {
          if(unselected_rollover[i]!=null)
          {
            unselected_rollover[i].flush();
            unselected_rollover[i] = null;
          }
        }

        unselected_rollover = null;
      }

      if(selected_rollover!=null)
      {
        for(int i=0;i<selected_rollover.length;i++)
        {
          if(selected_rollover[i]!=null)
          {
            selected_rollover[i].flush();
            selected_rollover[i] = null;
          }
        }

        selected_rollover = null;
      }
    }

    if(disabled)
    {
      if(unselected_disabled!=null)
      {
        for(int i=0;i<unselected_disabled.length;i++)
        {
          if(unselected_disabled[i]!=null)
          {
            unselected_disabled[i].flush();
            unselected_disabled[i] = null;
          }
        }

        unselected_disabled = null;
      }

      if(selected_disabled!=null)
      {
        for(int i=0;i<selected_disabled.length;i++)
        {
          if(selected_disabled[i]!=null)
          {
            selected_disabled[i].flush();
            selected_disabled[i] = null;
          }
        }

        selected_disabled = null;
      }
    }

    if(pressed)
    {
      if(unselected_pressed!=null)
      {
        for(int i=0;i<unselected_pressed.length;i++)
        {
          if(unselected_pressed[i]!=null)
          {
            unselected_pressed[i].flush();
            unselected_pressed[i] = null;
          }
        }

        unselected_pressed = null;
      }

      if(selected_pressed!=null)
      {
        for(int i=0;i<selected_pressed.length;i++)
        {
          if(selected_pressed[i]!=null)
          {
            selected_pressed[i].flush();
            selected_pressed[i] = null;
          }
        }

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

  protected Rectangle[] getSlices(Insets insets, int w, int h)
  {
    return OyoahaPoolUtilities.getSlices(insets, w, h, false);
  }

  protected Image[] initCustomColor(Color color)
  {
    if(cached==null)
    {
      cached = OyoahaPoolUtilities.getInsets(enabled);
    }
  
    int w = this.enabled.getWidth(null);
    int h = this.enabled.getHeight(null);
  
    Rectangle[] slices = getSlices(cached, w, h);

    int[] enabled_source = new int[w * h];
    int[] source2 = new int[w * h]; //colorized version

    try
    {
        PixelGrabber pg1 = new PixelGrabber(this.enabled, 0, 0, w, h, enabled_source, 0, w);
        pg1.grabPixels();
    }
    catch(Exception exception)
    {

    }

    source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, color, 100);

    Image[] unselected_enabled = new Image[8];
    crop(unselected_enabled, source2, w, h, slices);
    
    return unselected_enabled;
  }

  protected void init(boolean _enabled, boolean _selected, boolean _rollover, boolean _disabled, boolean _pressed)
  {
    if(cached==null)
    {
      cached = OyoahaPoolUtilities.getInsets(enabled);
    }

    if(!_enabled && !_selected && !_rollover && !_disabled && !_pressed)
    {
      return;
    }

    int w = this.enabled.getWidth(null);
    int h = this.enabled.getHeight(null);

    if(this.selected!=null)
    {
      Rectangle[] slices = getSlices(cached, w, h);

      int[] enabled_source = new int[w * h];
      int[] selected_source = new int[w * h];
      int[] source2 = new int[w * h]; //colorized version

      try
      {
        PixelGrabber pg1 = new PixelGrabber(this.enabled, 0, 0, w, h, enabled_source, 0, w);
        pg1.grabPixels();
        PixelGrabber pg2 = new PixelGrabber(this.selected, 0, 0, w, h, selected_source, 0, w);
        pg2.grabPixels();
      }
      catch(Exception exception)
      {

      }

      //enabled
      if(_enabled)
      {
        if(scheme.isCustomEnabledColor()) //colorize if scheme.enable != defaultMetalTheme.enable
        {
          source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getEnabled(), 100);
          initUEnabled(source2, w, h, slices);
        }
        else
        {
          initUEnabled(enabled_source, w, h, slices);
        }

        if(useEnabledColorForSelected) //selected state must use the same color than enabled
        {
          if(scheme.isCustomEnabledColor()) //colorize if scheme.enable != defaultMetalTheme.enable
          {
            source2 = OyoahaPoolUtilities.getColorized(selected_source, source2, w, h, scheme.getEnabled(), 100);
            initSEnabled(source2, w, h, slices);
          }
          else
          {
            initSEnabled(selected_source, w, h, slices);
          }
        }
      }

      //selected
      if(!useEnabledColorForSelected && _selected)
      {
        source2 = OyoahaPoolUtilities.getColorized(selected_source, source2, w, h, scheme.getSelected(), 50);
        initSEnabled(source2, w, h, slices);
      }

      //rollover
      if(_rollover)
      {
        source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getRollover(), 100);
        initURollover(source2, w, h, slices);

        source2 = OyoahaPoolUtilities.getColorized(selected_source, source2, w, h, scheme.getRollover(), 100);
        initSRollover(source2, w, h, slices);
      }

      //disabled
      if(_disabled)
      {
        source2 = OyoahaPoolUtilities.getColorized3(enabled_source, source2, w, h, scheme.getEnabled()/*scheme.getDisabled()*/);
        initUDisabled(source2, w, h, slices);

        source2 = OyoahaPoolUtilities.getColorized3(selected_source, source2, w, h, scheme.getSelected()/*scheme.getEnabled()*//*scheme.getDisabled()*/);
        initSDisabled(source2, w, h, slices);
      }

      //pressed (selected and unselected use selected image source)
      if(_pressed)
      {
        if(generateUnselectedPressed)
        {
          source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getPressed(), 100);
          initUPressed(source2, w, h, slices);
        }
        else
        {
          unselected_pressed = null;
        }

        source2 = OyoahaPoolUtilities.getColorized(selected_source, source2, w, h, scheme.getPressed(), 100);
        initSPressed(source2, w, h, slices);
      }
    }
    else
    {
      Rectangle[] slices = getSlices(cached, w, h);

      int[] enabled_source = new int[w * h];
      int[] source2 = new int[w * h]; //colorized version

      try
      {
        PixelGrabber pg1 = new PixelGrabber(this.enabled, 0, 0, w, h, enabled_source, 0, w);
        pg1.grabPixels();
      }
      catch(Exception exception)
      {

      }

      //enabled
      if(_enabled)
      {
        if(scheme.isCustomEnabledColor()) //colorize if scheme.enable != defaultMetalTheme.enable
        {
          source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getEnabled(), 100);
          initUEnabled(source2, w, h, slices);
        }
        else
        {
          initUEnabled(enabled_source, w, h, slices);
        }
      }

      //selected
      if(!useEnabledColorForSelected && _selected)
      {
        source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getSelected(), 50);
        initSEnabled(source2, w, h, slices);
      }

      //rollover
      if(_rollover)
      {
        source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getRollover(), 100);
        initURollover(source2, w, h, slices);
      }

      //disabled
      if(_disabled)
      {
        source2 = OyoahaPoolUtilities.getColorized3(enabled_source, source2, w, h, scheme.getEnabled()/*scheme.getDisabled()*/);
        initUDisabled(source2, w, h, slices);
      }

      //pressed (selected and unselected use selected image source)
      if(_pressed)
      {
        if(generateUnselectedPressed)
        {
          source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getPressed(), 100);
          initUPressed(source2, w, h, slices);
        }
        else
        {
          unselected_pressed = null;
        }

        source2 = OyoahaPoolUtilities.getColorized(enabled_source, source2, w, h, scheme.getPressed(), 100);
        initSPressed(source2, w, h, slices);
      }
    }
  }

  protected void initUEnabled(int[] source, int w, int h, Rectangle[] slices)
  {
    if (unselected_enabled==null)
    {
      unselected_enabled = new Image[8];
    }

    crop(unselected_enabled, source, w, h, slices);
  }

  protected void initSEnabled(int[] source, int w, int h, Rectangle[] slices)
  {
    if (selected_enabled==null)
    {
      selected_enabled = new Image[8];
    }

    crop(selected_enabled, source, w, h, slices);
  }

  protected void initURollover(int[] source, int w, int h, Rectangle[] slices)
  {
    if (unselected_rollover==null)
    {
      unselected_rollover = new Image[8];
    }

    crop(unselected_rollover, source, w, h, slices);
  }

  protected void initSRollover(int[] source, int w, int h, Rectangle[] slices)
  {
    if (selected_rollover==null)
    {
      selected_rollover = new Image[8];
    }

    crop(selected_rollover, source, w, h, slices);
  }

  protected void initUPressed(int[] source, int w, int h, Rectangle[] slices)
  {
    if (unselected_pressed==null)
    {
      unselected_pressed = new Image[8];
    }

    crop(unselected_pressed, source, w, h, slices);
  }

  protected void initSPressed(int[] source, int w, int h, Rectangle[] slices)
  {
    if (selected_pressed==null)
    {
      selected_pressed = new Image[8];
    }

    crop(selected_pressed, source, w, h, slices);
  }

  protected void initUDisabled(int[] source, int w, int h, Rectangle[] slices)
  {
    if (unselected_disabled==null)
    {
      unselected_disabled = new Image[8];
    }

    crop(unselected_disabled, source, w, h, slices);
  }

  protected void initSDisabled(int[] source, int w, int h, Rectangle[] slices)
  {
    if (selected_disabled==null)
    {
      selected_disabled = new Image[8];
    }

    crop(selected_disabled, source, w, h, slices);
  }

  protected void crop(Image[] border, int[] source, int w, int h, Rectangle[] slices)
  {
      if(slices!=null && slices.length==9)
      {
        if(slices[1]!=null)
        {
          border[0] = OyoahaPoolUtilities.cropToImage(source, slices[1].x, slices[1].y, slices[1].width, slices[1].height, w, h);
        }
        else
        {
          border[0] = null;
        }

        if(slices[2]!=null)
        {
          border[1] = OyoahaPoolUtilities.cropToImage(source, slices[2].x, slices[2].y, slices[2].width, slices[2].height, w, h);
        }
        else
        {
          border[1] = null;
        }

        if(slices[3]!=null)
        {
          border[2] = OyoahaPoolUtilities.cropToImage(source, slices[3].x, slices[3].y, slices[3].width, slices[3].height, w, h);
        }
        else
        {
          border[2] = null;
        }

        if(slices[4]!=null)
        {
          border[3] = OyoahaPoolUtilities.cropToImage(source, slices[4].x, slices[4].y, slices[4].width, slices[4].height, w, h);
        }
        else
        {
          border[3] = null;
        }

        if(slices[5]!=null)
        {
          border[4] = OyoahaPoolUtilities.cropToImage(source, slices[5].x, slices[5].y, slices[5].width, slices[5].height, w, h);
        }
        else
        {
          border[4] = null;
        }

        if(slices[6]!=null)
        {
          border[5] = OyoahaPoolUtilities.cropToImage(source, slices[6].x, slices[6].y, slices[6].width, slices[6].height, w, h);
        }
        else
        {
          border[5] = null;
        }

        if(slices[7]!=null)
        {
          border[6] = OyoahaPoolUtilities.cropToImage(source, slices[7].x, slices[7].y, slices[7].width, slices[7].height, w, h);
        }
        else
        {
          border[6] = null;
        }

        if(slices[8]!=null)
        {
          border[7] = OyoahaPoolUtilities.cropToImage(source, slices[8].x, slices[8].y, slices[8].width, slices[8].height, w, h);
        }
        else
        {
          border[7] = null;
        }
      }
  }
}