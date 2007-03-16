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

public class OyoahaButtonColorPool implements OyoahaColorPool, OyoahaThemeSchemeListener
{
  //original color
  protected Color enabled;
  protected Color selected;

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

  /**
   * enabled and selected can't be null
   *
   */
  public OyoahaButtonColorPool(OyoahaLookAndFeel lnf, Color enabled, Color selected, Boolean useEnabledColorForSelected, Boolean generateUnselectedPressed)
  {
    lnf.addOyoahaThemeSchemeListener(this);
    scheme = lnf.getOyoahaThemeScheme();

    this.enabled = enabled;
    this.selected = selected;
    this.useEnabledColorForSelected = useEnabledColorForSelected.booleanValue();
    this.generateUnselectedPressed = generateUnselectedPressed.booleanValue();

    init();
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  protected void init()
  {
    if(scheme.isCustomEnabledColor())
    {
      unselected_enabled = OyoahaPoolUtilities.getColor(enabled, scheme.getEnabled(), 100);
    }
    else
    {
      unselected_enabled = enabled;
    }

    if (selected!=null)
    {
        if(useEnabledColorForSelected) //selected must use the same color than enabled
        {
          if(scheme.isCustomEnabledColor())
          {
            selected_enabled = OyoahaPoolUtilities.getColor(selected, scheme.getEnabled(), 100);
          }
          else
          {
            selected_enabled = selected;
          }
        }
        else
        {
          selected_enabled = OyoahaPoolUtilities.getColor(selected, scheme.getSelected(), 50);
        }
    }
    else
    {
        if(useEnabledColorForSelected) //selected must use the same color than enabled
        {
            selected_enabled = OyoahaPoolUtilities.getColor(enabled, scheme.getEnabled(), 100);
        }
        else
        {
            selected_enabled = OyoahaPoolUtilities.getColor(enabled, scheme.getSelected(), 50);
        }
    }

    unselected_rollover = OyoahaPoolUtilities.getColor(enabled, scheme.getRollover(), 100);
    
    if (selected!=null)
    selected_rollover = OyoahaPoolUtilities.getColor(selected, scheme.getRollover(), 100);
    
    unselected_disabled = OyoahaPoolUtilities.getColor3(enabled, scheme.getEnabled());
    
    if (selected!=null)
    selected_disabled = OyoahaPoolUtilities.getColor3(selected, scheme.getSelected());

    if(generateUnselectedPressed)
    unselected_pressed = OyoahaPoolUtilities.getColor(enabled, scheme.getPressed(), 100);
    else
    unselected_pressed = null;

    if (selected!=null)
    selected_pressed = OyoahaPoolUtilities.getColor(selected, scheme.getPressed(), 100);
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
    return (Color)OyoahaStateRule.getObject(state, unselected_enabled, selected_enabled, unselected_pressed, selected_pressed, unselected_rollover, selected_rollover, unselected_disabled, selected_disabled);
  }

  /**
   * transfom a color in regard of the state
   */
  public Color getColor(Color color, int state)
  {
    return OyoahaDefaultColorPool.getColor(color, state, scheme, useEnabledColorForSelected);
  }
}