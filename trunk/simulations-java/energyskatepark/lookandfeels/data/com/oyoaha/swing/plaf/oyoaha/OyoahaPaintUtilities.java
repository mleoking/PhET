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

public interface OyoahaPaintUtilities
{

//- - - - -

  /**
  *
  */
  public void initialize();

  /**
  *
  */
  public void uninitialize();

//- - - - -

  /**
  *
  */
  public void paintBackground(Graphics g, Component c);

  /**
  *
  */
  public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, int status);


  /**
  *
  */
  public void paintBackground(Graphics g, Component c, int x, int y, int width, int height, Color color, int status);

  /**
  *
  */
  public void paintColorBackground(Graphics g, Component c, int x, int y, int width, int height, Color color, int status);

//- - - - -

  /**
  *
  */
  public Point getAPosition(Component c);

  /**
  *
  */
  public boolean intersects(int x, int y, int width, int height, int x1, int y1, int width1, int height1);

  /**
  *
  */
  public Shape normalizeClip(Graphics g, int x, int y, int width, int height);

  /**
  *
  */
  public boolean isOpaque(Component c);

  /**
  *
  */
  public Color getBackground(Component c);

  /**
  *
  */
  public boolean hasFocus(Component c);

  /**
   *
   */
  public boolean isDefaultButton(Component c);

  /**
   *
   */
  public boolean isLeftToRight(Component c);

//- - - - -

  /**
  *
  */
  public void setAlphaChannel(Graphics g, Component c, float f);


//- - - - - ABSOLUTE

  /**
  *
  */
  public void paintAMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i);

  /**
  *
  */
  public void paintAGradient(Graphics g, Component c, int x, int y, int width, int height, Color color1, Color color2, boolean horizontal, boolean vertical, int state);

  /**
  *
  */
  public void paintAScrollMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i, int decalX, int decalY);

  /**
  *
  */
  public void paintAImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos);

  /**
  *
  */
  public void paintAImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos, int w, int h);

  /**
  *
  */
  public void paintAScaling(Graphics g, Component c, int x, int y, int width, int height, Image i);

//- - - - - RELATIVE

  /**
  *
  */
  public void paintRMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i);

  /**
  *
  */
  public void paintRScrollMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i, int decalX, int decalY);

  /**
  *
  */
  public void paintRImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos);

  /**
  *
  */
  public void paintRScaling(Graphics g, Component c, int x, int y, int width, int height, Image i);

//- - - - -
}