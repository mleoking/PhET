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

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaDefaultColorPool implements OyoahaColorPool, OyoahaThemeSchemeListener
{
  //original color
  protected Color u_enabled;
  protected Color s_enabled;
  protected Color u_rollover;
  protected Color s_rollover;
  protected Color u_disabled;
  protected Color s_disabled;
  protected Color u_pressed;
  protected Color s_pressed;

  //color generated
  protected Color unselected_enabled;   //UNSELECTED_ENABLED
  protected Color selected_enabled;     //SELECTED_ENABLED
  protected Color unselected_rollover;  //UNSELECTED_ROLLOVER
  protected Color selected_rollover;    //SELECTED_ROLLOVER
  protected Color unselected_disabled;  //UNSELECTED_DISABLED
  protected Color selected_disabled;    //SELECTED_DISABLED
  protected Color unselected_pressed;   //UNSELECTED_PRESSED
  protected Color selected_pressed;     //SELECTED_PRESSED

  //oyoaha theme scheme to have right color
  protected OyoahaThemeScheme scheme;

  //force selected to use the same color than enabled
  protected boolean useEnabledColorForSelected;
  //force the unselected_pressed to null
  protected boolean generateUnselectedPressed;

  //for default constructor
  protected boolean useSystemColor;

  public OyoahaDefaultColorPool(OyoahaLookAndFeel lnf)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.useEnabledColorForSelected = false;
    this.useSystemColor = true;
  }

  public OyoahaDefaultColorPool(OyoahaLookAndFeel lnf, Color u_enabled, Color s_enabled, Color u_rollover, Color s_rollover, Color u_disabled, Color s_disabled, Color u_pressed, Color s_pressed, Boolean useEnabledColorForSelected, Boolean generateUnselectedPressed)
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
    this.useSystemColor = false;

    init();
  }

  protected void init()
  {
    if(useSystemColor)
    {
      return;
    }

    if(scheme.isCustomEnabledColor())
    {
      unselected_enabled = OyoahaPoolUtilities.getColor2(u_enabled, scheme.getEnabled());
    }
    else
    {
      unselected_enabled = u_enabled;
    }

    if(useEnabledColorForSelected) //selected must use the same color than enabled
    {
      if(scheme.isCustomEnabledColor())
      {
        selected_enabled = OyoahaPoolUtilities.getColor(s_enabled, scheme.getEnabled(), 100);
      }
      else
      {
        selected_enabled = s_enabled;
      }
    }
    else
    {
      selected_enabled = OyoahaPoolUtilities.getColor(s_enabled, scheme.getSelected(), 50);
      //selected_enabled = scheme.getSelected();
    }

    Color tmp = (Color)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_ROLLOVER, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);
    if(tmp!=null)
    unselected_rollover = OyoahaPoolUtilities.getColor(tmp, scheme.getRollover(), 100);
    else
    unselected_rollover = scheme.getRollover();

    tmp = (Color)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_ROLLOVER, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);
    if(tmp!=null)
    selected_rollover = OyoahaPoolUtilities.getColor(tmp, scheme.getRollover(), 100);
    else
    selected_rollover = scheme.getRollover();

    tmp = (Color)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_DISABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);
    if(tmp!=null)
    unselected_disabled = OyoahaPoolUtilities.getColor3(tmp, scheme.getEnabled());
    else
    unselected_disabled = scheme.getDisabled();

    tmp = (Color)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_DISABLED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);
    if(tmp!=null)
    selected_disabled = OyoahaPoolUtilities.getColor3(tmp, scheme.getSelected());
    else
    selected_disabled = scheme.getDisabled();

    if(this.generateUnselectedPressed)
    {
      tmp = (Color)OyoahaStateRule.getObject(OyoahaUtilities.UNSELECTED_PRESSED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);
      if(tmp!=null)
      unselected_pressed = OyoahaPoolUtilities.getColor(tmp, scheme.getPressed(), 100);
      else
      unselected_pressed = scheme.getPressed();
    }
    else
    {
      unselected_pressed = null;
    }

    tmp = (Color)OyoahaStateRule.getObject(OyoahaUtilities.SELECTED_PRESSED, u_enabled, s_enabled, u_pressed, s_pressed, u_rollover, s_rollover, u_disabled, s_disabled);
    if(tmp!=null)
    selected_pressed = OyoahaPoolUtilities.getColor2(tmp, scheme.getPressed());
    else
    selected_pressed = scheme.getPressed();
  }

  public void updateThemeScheme(OyoahaThemeScheme scheme, OyoahaThemeSchemeChanged changed)
  {
    this.scheme = scheme;
    init();
  }

  public void dispose()
  {
    unselected_enabled = null;
    selected_enabled = null;
    unselected_rollover = null;
    selected_rollover = null;
    unselected_disabled = null;
    selected_disabled = null;
    unselected_pressed = null;
    selected_pressed = null;
  }

  public Color getColor(int state)
  {
    if(useSystemColor)
    {
      return (Color)OyoahaStateRule.getObject(state, scheme.getEnabled(), scheme.getSelected(), scheme.getPressed(), scheme.getPressed(), scheme.getRollover(), scheme.getRollover(), scheme.getDisabled(), null);
    }

    return (Color)OyoahaStateRule.getObject(state, unselected_enabled, selected_enabled, unselected_pressed, selected_pressed, unselected_rollover, selected_rollover, unselected_disabled, null);
  }

  /**
   * transfom a color in regard of the state
   */
  public Color getColor(Color color, int state)
  {
    if(useSystemColor)
    {
      if(state==OyoahaUtilities.UNSELECTED_ENABLED)
      {
        if(!scheme.isCustomEnabledColor())
        return color;
      }
      else
      if(state==OyoahaUtilities.SELECTED_ENABLED && useEnabledColorForSelected)
      {
        if(!scheme.isCustomEnabledColor())
        return color;
      }
    }

    return OyoahaDefaultColorPool.getColor(color, state, scheme, useEnabledColorForSelected);
  }

  public final static Color getColor(Color color, int state, OyoahaThemeScheme scheme, boolean useEnabledColorForSelected)
  {
    switch(state)
    {
      case OyoahaUtilities.UNSELECTED_DISABLED:
      case OyoahaUtilities.SELECTED_DISABLED:
        return OyoahaPoolUtilities.getColor2(color, scheme.getDisabled());
      case OyoahaUtilities.UNSELECTED_PRESSED:
      case OyoahaUtilities.SELECTED_PRESSED:
        return OyoahaPoolUtilities.getColor2(color, scheme.getPressed());
      case OyoahaUtilities.UNSELECTED_ROLLOVER:
      case OyoahaUtilities.SELECTED_ROLLOVER:
        return OyoahaPoolUtilities.getColor2(color, scheme.getRollover());
      case OyoahaUtilities.SELECTED_ENABLED:
        if(useEnabledColorForSelected)
        {
          if(scheme.isCustomEnabledColor())
          return OyoahaPoolUtilities.getColor2(color, scheme.getEnabled());

          return color;
        }
        return OyoahaPoolUtilities.getColor2(color, scheme.getSelected());
      case OyoahaUtilities.UNSELECTED_ENABLED:
        if(scheme.isCustomEnabledColor())
        return OyoahaPoolUtilities.getColor2(color, scheme.getEnabled());
      break;
    }

    return color;
  }
}