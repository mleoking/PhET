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

import java.awt.*;
import java.awt.event.*;

class OyoahaRolloverListener extends MouseAdapter
{
  private Component isRollover;
  private Component isPressed;

  public void mousePressed(MouseEvent e)
  {
    Component c = e.getComponent();
    isPressed = c;

    c.repaint();

    if(c.isEnabled())
    OyoahaUtilities.playStartClick();
  }

  public void mouseReleased(MouseEvent e)
  {
    Component c = e.getComponent();
    isPressed = null;

    c.repaint();

    if(c.isEnabled())
    OyoahaUtilities.playStopClick();
  }

  public void mouseEntered(MouseEvent e)
  {
    int m = e.getModifiers();

    if(m==MouseEvent.BUTTON1_MASK || m==MouseEvent.BUTTON2_MASK || m==MouseEvent.BUTTON3_MASK)
    return;

    Component c = e.getComponent();
    isRollover = c;

    c.repaint();
  }

  public void mouseExited(MouseEvent e)
  {
    Component c = e.getComponent();
    isRollover = null;
    c.repaint();
  }

  //- - - - - - - - - - - - - - - - - -

  public boolean isPressed(Component c)
  {
    return c==isPressed;
  }

  public boolean isRollover(Component c)
  {
    return c==isRollover;
  }

  //- - - - - - - - - - - - - - - - - -

  public Component getPressed()
  {
    return isPressed;
  }

  public Component getRollover()
  {
    return isRollover;
  }
}