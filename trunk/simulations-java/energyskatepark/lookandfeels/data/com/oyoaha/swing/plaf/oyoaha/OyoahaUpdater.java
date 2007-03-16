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
import javax.swing.*;

public class OyoahaUpdater implements Runnable
{
  protected boolean enabledBackgroundAnimation;
  protected boolean enabledFocusAnimation;

  protected int count;

  protected Component root;
  protected Component component;

  protected OyoahaFlash cacheComponent;
  protected OyoahaFlash cacheComponentBorder;
  protected OyoahaFlash cacheComponentIcon;

  protected OyoahaFlash cacheRoot;

  public OyoahaUpdater(boolean _enabledFocusAnimation, boolean _enabledBackgroundAnimation)
  {
    enabledFocusAnimation = _enabledFocusAnimation;
    enabledBackgroundAnimation = _enabledBackgroundAnimation;
  }

  private boolean anime;

  public void start()
  {
    anime = true;

    count = 0;

    root = null;
    component = null;

    cacheComponent = null;
    cacheComponentBorder = null;
    cacheComponentIcon = null;
    cacheRoot = null;

    Thread thread = new Thread(this);
    thread.start();
  }

  public void stop()
  {
    anime = false;

    enabledBackgroundAnimation = false;
    enabledFocusAnimation = false;

    count = 0;

    root = null;
    component = null;

    cacheComponent = null;
    cacheComponentBorder = null;
    cacheComponentIcon = null;
    cacheRoot = null;
  }

  public void run()
  {
    while(anime)
    {
      try
      {
        Thread.sleep(110);
      }
      catch(Exception e)
      {

      }

      update();
    }
  }

  private void getRootComponent()
  {
    Component tmp = OyoahaUtilities.getRollover();

    if(tmp==null)
    {
      tmp = OyoahaUtilities.getPressed();
    }

    if(tmp!=component)
    {
      component = tmp;

      if(component!=null)
      {
        cacheComponent  = OyoahaUtilities.getFlash(component);
        cacheComponentBorder = OyoahaUtilities.getFlashBorder(component);
        cacheComponentIcon = OyoahaUtilities.getFlashIcon(component);

        resetOyoahaAnimated(cacheComponent);
        resetOyoahaAnimated(cacheComponentBorder);
        resetOyoahaAnimated(cacheComponentIcon);

        tmp = SwingUtilities.getRoot(component);

        if(tmp!=root)
        {
          root = tmp;
          cacheRoot  = OyoahaUtilities.getFlash(root);
          //resetOyoahaAnimated(cacheRoot);
          if(root!=null)
          root.repaint();
        }
      }
    }
  }

  private void resetOyoahaAnimated(OyoahaFlash o)
  {
    if(o!=null)
    {
      o.reset();
    }
  }

  private void updateOyoahaAnimated(OyoahaFlash o)
  {
    if(o!=null)
    {
      o.update();
    }
  }

  private boolean rootIsValide()
  {
    if(root==null)
    {
      return false;
    }

    if(!root.isVisible())
    {
      return false;
    }

    return true;
  }

  private void update()
  {
    getRootComponent();

    if(!rootIsValide())
    {
      return;
    }

    if(enabledBackgroundAnimation)
    {
      count++;

      if(count>35)
      {
        count = 0;

        if(root!=null)
        {
          updateOyoahaAnimated(cacheRoot);
          root.repaint();
        }
      }
    }

    if(component!=null)
    {
      updateOyoahaAnimated(cacheComponent);
      updateOyoahaAnimated(cacheComponentBorder);
      updateOyoahaAnimated(cacheComponentIcon);
      component.repaint();
    }
  }
}