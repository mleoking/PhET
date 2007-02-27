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

public class OyoahaStateRule
{
  public final static int EXCLUSIVE = 0;
  public final static int ENABLED = 1;
  public final static int FLIP = 2;
  public final static int SMART = 3;

  protected boolean u_enabled;
  protected boolean u_pressed;
  protected boolean u_rollover;
  protected boolean u_disabled;

  protected boolean s_enabled;
  protected boolean s_pressed;
  protected boolean s_rollover;
  protected boolean s_disabled;

  protected boolean opaque;
  protected int mode;

  /**
  * OyoahaStateRule provide a way to pool, background, icon, border to know which state is allowed to draw/generate
  */
  public OyoahaStateRule(Boolean u_enabled, Boolean u_pressed, Boolean u_rollover, Boolean u_disabled, Boolean s_enabled, Boolean s_pressed, Boolean s_rollover, Boolean s_disabled, Boolean opaque, Integer mode)
  {
    this.u_enabled = u_enabled.booleanValue();
    this.u_pressed = u_pressed.booleanValue();
    this.u_rollover = u_rollover.booleanValue();
    this.u_disabled = u_disabled.booleanValue();

    this.s_enabled = s_enabled.booleanValue();
    this.s_pressed = s_pressed.booleanValue();
    this.s_rollover = s_rollover.booleanValue();
    this.s_disabled = s_disabled.booleanValue();

    this.opaque = opaque.booleanValue();
    this.mode = mode.intValue();
  }

  /**
  * check if this state can be used
  */
  public boolean isLegalState(int state)
  {
    return state!=getState(state);
  }

   /**
    * return if the object which use this rule is opaque or not
    */
   public boolean isOpaque()
   {
    return opaque;
   }

  /**
  * return the better object for a given state
  * all argument can be null
  *
  * return null if there is no object for a state
  * or if state equal OyoahaUtilities.UNVISIBLE
  *
  */
  public static Object getObject(int state, Object u_enabled, Object s_enabled, Object u_pressed, Object s_pressed, Object u_rollover, Object s_rollover, Object u_disabled, Object s_disabled)
  {
    if(state==OyoahaUtilities.UNVISIBLE)
    {
      return null;
    }

    switch (state)
    {
      case OyoahaUtilities.UNSELECTED_ENABLED:
              return u_enabled;
      case OyoahaUtilities.SELECTED_ENABLED:
              if(s_enabled!=null)
              return s_enabled;

              return u_enabled;
      case OyoahaUtilities.UNSELECTED_PRESSED:
              if(u_pressed!=null)
              return u_pressed;

              if(s_pressed!=null)
              return s_pressed;

              return u_enabled;
      case OyoahaUtilities.SELECTED_PRESSED:
              if(s_pressed!=null)
              return s_pressed;

              if(u_pressed!=null)
              return u_pressed;

              return s_enabled;
      case OyoahaUtilities.UNSELECTED_ROLLOVER:
              if(u_rollover!=null)
              return u_rollover;

              return u_enabled;
      case OyoahaUtilities.SELECTED_ROLLOVER:
              if(s_rollover!=null)
              return s_rollover;

              return s_enabled;
      case OyoahaUtilities.UNSELECTED_DISABLED:
              if(u_disabled!=null)
              return u_disabled;

              return u_enabled;
      case OyoahaUtilities.SELECTED_DISABLED:
              if(s_disabled!=null)
              return s_disabled;

              return s_enabled;
    }

    return null;
  }

  /**
  * return the state which must be used
  *
  * state represent the component state
  */
  public int getState(int state)
  {
    switch (state)
    {
      case OyoahaUtilities.UNVISIBLE:
              return OyoahaUtilities.UNVISIBLE;
      case OyoahaUtilities.UNSELECTED_ENABLED:
              if(u_enabled)
              return OyoahaUtilities.UNSELECTED_ENABLED;
      break;
      case OyoahaUtilities.SELECTED_ENABLED:
              if(s_enabled)
              {
                return OyoahaUtilities.SELECTED_ENABLED;
              }
              else
              if(mode!=EXCLUSIVE) //ENABLED FLIP SMART
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
      case OyoahaUtilities.UNSELECTED_PRESSED:
              if(u_pressed)
              {
                return OyoahaUtilities.UNSELECTED_PRESSED;
              }
              else
              if(mode==ENABLED)
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
              else
              if(mode==FLIP)
              {
                if(s_pressed)
                return OyoahaUtilities.SELECTED_PRESSED;
              }
              else
              if(mode==SMART)
              {
                if(s_pressed)
                return OyoahaUtilities.SELECTED_PRESSED;

                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
      case OyoahaUtilities.SELECTED_PRESSED:
              if(s_pressed)
              {
                return OyoahaUtilities.SELECTED_PRESSED;
              }
              else
              if(mode==ENABLED)
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
              else
              if(mode==FLIP)
              {
                if(u_pressed)
                return OyoahaUtilities.UNSELECTED_PRESSED;
              }
              else
              if(mode==SMART)
              {
                if(u_pressed)
                return OyoahaUtilities.UNSELECTED_PRESSED;

                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
      case OyoahaUtilities.UNSELECTED_ROLLOVER:
              if(u_rollover)
              {
                return OyoahaUtilities.UNSELECTED_ROLLOVER;
              }
              else
              if(mode==ENABLED)
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
              else
              if(mode==FLIP)
              {
                if(s_rollover)
                return OyoahaUtilities.SELECTED_ROLLOVER;
              }
              else
              if(mode==SMART)
              {
                if(s_rollover)
                return OyoahaUtilities.SELECTED_ROLLOVER;

                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
      case OyoahaUtilities.SELECTED_ROLLOVER:
              if(s_rollover)
              {
                return OyoahaUtilities.SELECTED_ROLLOVER;
              }
              else
              if(mode==ENABLED)
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
              else
              if(mode==FLIP)
              {
                if(u_rollover)
                return OyoahaUtilities.UNSELECTED_ROLLOVER;
              }
              else
              if(mode==SMART)
              {
                if(u_rollover)
                return OyoahaUtilities.UNSELECTED_ROLLOVER;

                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
      case OyoahaUtilities.UNSELECTED_DISABLED:
              if(u_disabled)
              {
                return OyoahaUtilities.UNSELECTED_DISABLED;
              }
              else
              if(mode==ENABLED)
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
              else
              if(mode==FLIP)
              {
                if(s_disabled)
                return OyoahaUtilities.SELECTED_DISABLED;
              }
              else
              if(mode==SMART)
              {
                if(s_disabled)
                return OyoahaUtilities.SELECTED_DISABLED;

                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
      case OyoahaUtilities.SELECTED_DISABLED:
              if(s_disabled)
              {
                return OyoahaUtilities.SELECTED_DISABLED;
              }
              else
              if(mode==ENABLED)
              {
                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
              else
              if(mode==FLIP)
              {
                if(u_disabled)
                return OyoahaUtilities.UNSELECTED_DISABLED;
              }
              else
              if(mode==SMART)
              {
                if(u_disabled)
                return OyoahaUtilities.UNSELECTED_DISABLED;

                if(u_enabled)
                return OyoahaUtilities.UNSELECTED_ENABLED;
              }
      break;
    }

    return OyoahaUtilities.UNVISIBLE;
  }
}