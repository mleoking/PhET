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

package com.oyoaha.swing.plaf.oyoaha.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaSplitPaneDivider extends BasicSplitPaneDivider
{
  public OyoahaSplitPaneDivider(BasicSplitPaneUI ui)
  {
    super(ui);
    setLayout(new OyoahaDividerLayout());
  }

  public void paint(Graphics g)
  {
    super.paint(g);

    //paint bump mapping here
    if (isEnabled())
    {
        Dimension d = getSize();
        OyoahaUtilities.paintBump(g, this, 0, 0, d.width, d.height, OyoahaUtilities.getStatus(this), 1, 1);
    }
  }
  
  public boolean isEnabled()
  {
    BasicSplitPaneUI b = getBasicSplitPaneUI();
    return b.getSplitPane().isEnabled();
  }

  protected class OyoahaButtonSplitPaneDivider extends OyoahaScrollButton
  {
    public OyoahaButtonSplitPaneDivider(int direction, int width, boolean freeStanding)
    {
      super(direction, width, freeStanding);
      setBorder(null);
    }

    public int getDirection()
    {
      int orientation = getOrientationFromSuper();

      if(direction==NORTH || direction==WEST) //left
      {
        if(orientation==JSplitPane.VERTICAL_SPLIT)
        {
          return NORTH;
        }
        else
        {
          return WEST;
        }
      }
      else //right
      {
        if(orientation==JSplitPane.VERTICAL_SPLIT)
        {
          return SOUTH;
        }
        else
        {
          return EAST;
        }
      }
    }

    public void paint(Graphics g)
    {
      int orientation = getOrientationFromSuper();
      Dimension d = getSize();

      if(orientation==JSplitPane.VERTICAL_SPLIT)
      super.paint(g, d.width, d.height, (d.width+1)/2, (d.width+1)/4, OyoahaUtilities.getStatus(this));
      else
      super.paint(g, d.width, d.height, (d.height+1)/2, (d.height+1)/4, OyoahaUtilities.getStatus(this));
    }
  }

  protected JButton createLeftOneTouchButton()
  {
    JButton b = new OyoahaButtonSplitPaneDivider(SwingConstants.NORTH, getOneTouchSizeFromSuper(), true);

    b.setFocusPainted(false);
    b.setBorderPainted(false);
    b.setOpaque(false);

    return b;
  }

  protected JButton createRightOneTouchButton()
  {
    JButton b = new OyoahaButtonSplitPaneDivider(SwingConstants.SOUTH, getOneTouchSizeFromSuper(), true);

    b.setFocusPainted(false);
    b.setBorderPainted(false);
    b.setOpaque(false);

    return b;
  }

  public class OyoahaDividerLayout implements LayoutManager
  {
    public void layoutContainer(Container c)
    {
      JButton leftButton = getLeftButtonFromSuper();
      JButton rightButton = getRightButtonFromSuper();
      JSplitPane splitPane = getSplitPaneFromSuper();

      int orientation = getOrientationFromSuper();
      int oneTouchSize = getOneTouchSizeFromSuper();
      int oneTouchOffset = getOneTouchOffsetFromSuper();
      int blockSize = Math.min(getDividerSize(), oneTouchSize);

      if(leftButton != null && rightButton != null && c == OyoahaSplitPaneDivider.this)
      {
        if(splitPane.isOneTouchExpandable())
        {
          leftButton.setVisible(true);
          rightButton.setVisible(true);

          if (orientation == JSplitPane.VERTICAL_SPLIT)
          {
            leftButton.setBounds(oneTouchOffset, 0, blockSize * 2, blockSize);
            rightButton.setBounds(oneTouchOffset + oneTouchSize * 2, 0, blockSize * 2, blockSize);
          }
          else
          {
            leftButton.setBounds(0, oneTouchOffset, blockSize, blockSize * 2);
            rightButton.setBounds(0, oneTouchOffset + oneTouchSize * 2, blockSize, blockSize * 2);
          }
        }
        else
        {
          leftButton.setVisible(false);
          rightButton.setVisible(false);
        }
      }
    }

    public Dimension minimumLayoutSize(Container c)
    {
      return new Dimension(0,0);
    }

    public Dimension preferredLayoutSize(Container c)
    {
      return new Dimension(0, 0);
    }

    public void removeLayoutComponent(Component c)
    {

    }

    public void addLayoutComponent(String string, Component c)
    {

    }
  }

  protected int getOneTouchSizeFromSuper()
  {
    return super.ONE_TOUCH_SIZE;
  }

  protected int getOneTouchOffsetFromSuper()
  {
    return super.ONE_TOUCH_OFFSET;
  }

  protected int getOrientationFromSuper()
  {
    return super.orientation;
  }

  protected JSplitPane getSplitPaneFromSuper()
  {
    return super.splitPane;
  }

  protected JButton getLeftButtonFromSuper()
  {
    return super.leftButton;
  }

  protected JButton getRightButtonFromSuper()
  {
    return super.rightButton;
  }
}