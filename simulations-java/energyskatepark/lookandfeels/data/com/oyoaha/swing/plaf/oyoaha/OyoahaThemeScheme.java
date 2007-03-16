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

package com.oyoaha.swing.plaf.oyoaha;

import java.util.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.pool.*;

public class OyoahaThemeScheme
{
  public final static String ENABLED = "enabled";
  public final static String SELECTED = "selected";
  public final static String FOCUS = "focus";
  public final static String ROLLOVER = "rollover";
  public final static String PRESSED = "pressed";
  public final static String DISABLED = "disabled";
  public final static String GRAY = "gray";
  public final static String BLACK = "black";
  public final static String WHITE = "white";

  protected ColorUIResource enabled;
  protected ColorUIResource selected;
  protected ColorUIResource focus;
  protected ColorUIResource rollover;
  protected ColorUIResource pressed;
  protected ColorUIResource disabled;
  protected ColorUIResource gray;
  protected ColorUIResource black;
  protected ColorUIResource white;

  protected boolean isDefaultMetalTheme;
  protected boolean isDefaultOyoahaTheme;

  protected boolean isCustomEnabledColor;

  public OyoahaThemeScheme()
  {
    load();
  }

  public boolean isDefaultOyoahaTheme()
  {
    return isDefaultOyoahaTheme;
  }

  public boolean isDefaultMetalTheme()
  {
    return isDefaultMetalTheme;
  }

  public static boolean isDefaultMetalTheme(MetalTheme _theme)
  {
    if(_theme instanceof DefaultMetalTheme)
    {
      return true;
    }

    MetalTheme theme = new DefaultMetalTheme();

    return (
    _theme.getControl().equals(theme.getControl()) &&
    _theme.getPrimaryControl().equals(theme.getPrimaryControl()) &&
    _theme.getFocusColor().equals(theme.getFocusColor()) &&
    _theme.getMenuSelectedBackground().equals(theme.getMenuSelectedBackground()) &&
    _theme.getControlShadow().equals(theme.getControlShadow()) &&
    _theme.getControlDisabled().equals(theme.getControlDisabled()) &&
    _theme.getControlInfo().equals(theme.getControlInfo()) &&
    _theme.getControlHighlight().equals(theme.getControlHighlight())
    );
  }

  public boolean isCustomEnabledColor()
  {
    return isCustomEnabledColor;
  }

  protected void init()
  {
    MetalTheme theme = new DefaultMetalTheme();

    isDefaultMetalTheme = (
    enabled.equals(theme.getControl()) &&
    selected.equals(theme.getPrimaryControl()) &&
    focus.equals(theme.getFocusColor()) &&
    rollover.equals(theme.getMenuSelectedBackground()) &&
    pressed.equals(theme.getControlShadow()) &&
    disabled.equals(theme.getControlDisabled()) &&
    black.equals(theme.getControlInfo()) &&
    white.equals(theme.getControlHighlight())
    );

    isCustomEnabledColor = !enabled.equals(theme.getControl());
  }

  public void load()
  {
    isDefaultOyoahaTheme = false;

    try
    {
      enabled = MetalLookAndFeel.getControl();
      selected = MetalLookAndFeel.getPrimaryControl();
      focus = MetalLookAndFeel.getFocusColor();
      rollover = MetalLookAndFeel.getMenuSelectedBackground();
      pressed = MetalLookAndFeel.getControlShadow();
      disabled = MetalLookAndFeel.getControlDisabled();
      black = MetalLookAndFeel.getBlack();
      white = MetalLookAndFeel.getWhite();
      gray = OyoahaPoolUtilities.darken(enabled, 2);

      init();
    }
    catch(NullPointerException e)
    {
      load(new DefaultMetalTheme());
    }
  }

  //-----------------

  public OyoahaThemeSchemeChanged load(OyoahaThemeLoader loader, MetalTheme _default)
  {
    try
    {
      Properties p = loader.loadDefaultOyoahaThemeScheme();

      if(p!=null && p.size()>0)
      {
        OyoahaThemeSchemeChanged changed = load(p);
        isDefaultOyoahaTheme = true;
        return changed;
      }
    }
    catch(Exception e)
    {

    }

    if(_default==null)
    return load(new DefaultMetalTheme());

    return load(_default);
  }

  public OyoahaThemeSchemeChanged load(MetalTheme theme)
  {
    isDefaultOyoahaTheme = false;

    if(theme==null)
    {
      //nothing has changed
      return new OyoahaThemeSchemeChanged();
    }

    ColorUIResource[] c = new ColorUIResource[8];

    c[0] = theme.getControl();
    c[1] = theme.getPrimaryControl();
    c[2] = theme.getFocusColor();
    c[3] = theme.getMenuSelectedBackground();
    c[4] = theme.getControlShadow();
    c[5] = theme.getControlDisabled();
    c[6] = theme.getControlInfo();
    c[7] = theme.getControlHighlight();

    return fillValue(c);
  }

  protected OyoahaThemeSchemeChanged load(Properties properties)
  {
    if(properties==null)
    {
      return loadDefault();
    }

    isDefaultOyoahaTheme = false;

    ColorUIResource[] c = new ColorUIResource[8];

    if(properties.containsKey(ENABLED))
    c[0] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(ENABLED));

    if(properties.containsKey(SELECTED))
    c[1] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(SELECTED));

    if(properties.containsKey(FOCUS))
    c[2] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(FOCUS));

    if(properties.containsKey(ROLLOVER))
    c[3] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(ROLLOVER));

    if(properties.containsKey(PRESSED))
    c[4] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(PRESSED));

    if(properties.containsKey(DISABLED))
    c[5] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(DISABLED));

    if(properties.containsKey(BLACK))
    c[6] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(BLACK));

    if(properties.containsKey(WHITE))
    c[7] = OyoahaThemeLoaderUtilities.readColor(properties.getProperty(WHITE));

    return fillValue(c);
  }

  protected OyoahaThemeSchemeChanged loadDefault()
  {
    isDefaultOyoahaTheme = false;

    ColorUIResource[] c = new ColorUIResource[8];

    c[0] = new ColorUIResource(145,173,211);
    c[1] = new ColorUIResource(214,193,174);
    c[2] = new ColorUIResource(225,255,255);
    c[3] = new ColorUIResource(255,219,174);
    c[4] = new ColorUIResource(255,115,174);
    c[5] = new ColorUIResource(225,255,255);
    c[6] = new ColorUIResource(0,0,0);
    c[7] = new ColorUIResource(225,255,255);

    return fillValue(c);
  }

  protected OyoahaThemeSchemeChanged fillValue(ColorUIResource[] c)
  {
    OyoahaThemeSchemeChanged changed = new OyoahaThemeSchemeChanged();

    if(c.length!=8)
    {
      init();
      return changed;
    }

    if(c[0]!=null)
    {
      if(enabled!=null)
      changed.enabled = !enabled.equals(c[0]);
      enabled = c[0];
    }

    if(c[1]!=null)
    {
      if(selected!=null)
      changed.selected = !selected.equals(c[1]);
      selected = c[1];
    }

    if(c[2]!=null)
    {
      if(focus!=null)
      changed.focus = !focus.equals(c[2]);
      focus = c[2];
    }

    if(c[3]!=null)
    {
      if(rollover!=null)
      changed.rollover = !rollover.equals(c[3]);
      rollover = c[3];
    }

    if(c[4]!=null)
    {
      if(pressed!=null)
      changed.pressed = !pressed.equals(c[4]);
      pressed = c[4];
    }

    if(c[5]!=null)
    {
      if(disabled!=null)
      changed.disabled = !disabled.equals(c[5]);
      disabled = c[5];
    }

    if(c[6]!=null)
    {
      if(black!=null)
      changed.black = !black.equals(c[6]);
      black = c[6];
    }

    if(c[7]!=null)
    {
      if(white!=null)
      changed.white = !white.equals(c[7]);
      white = c[7];
    }

    ColorUIResource g = OyoahaPoolUtilities.darken(enabled, 2);

    if(gray==null)
    {
      gray = g;
    }
    else
    if(!gray.equals(g))
    {
      gray = g;
      changed.gray = true;
    }

    init();

    return changed;
  }

  //-----------------

  public ColorUIResource get(int status)
  {
    switch(status)
    {
      case OyoahaUtilities.UNSELECTED_DISABLED:
      case OyoahaUtilities.SELECTED_DISABLED:
      return disabled;
      case OyoahaUtilities.UNSELECTED_PRESSED:
      case OyoahaUtilities.SELECTED_PRESSED:
      return pressed;
      case OyoahaUtilities.SELECTED_ENABLED:
      return selected;
      case OyoahaUtilities.UNSELECTED_ROLLOVER:
      case OyoahaUtilities.SELECTED_ROLLOVER:
      return rollover;
      case OyoahaUtilities.FOCUS:
      return focus;
      case OyoahaUtilities.WHITE:
      return white;
      case OyoahaUtilities.BLACK:
      return black;
      case OyoahaUtilities.GRAY:
      return gray;
      default: //OyoahaUtilities.UNSELECTED_ENABLED
      return enabled;
    }
  }

  public ColorUIResource getEnabled()
  {
    return enabled;
  }

  public ColorUIResource getSelected()
  {
    return selected;
  }

  public ColorUIResource getFocus()
  {
    return focus;
  }

  public ColorUIResource getRollover()
  {
    return rollover;
  }

  public ColorUIResource getPressed()
  {
    return pressed;
  }

  public ColorUIResource getDisabled()
  {
    return disabled;
  }

  public ColorUIResource getBlack()
  {
    return black;
  }

  public ColorUIResource getWhite()
  {
    return white;
  }

  public ColorUIResource getGray()
  {
    return gray;
  }
}