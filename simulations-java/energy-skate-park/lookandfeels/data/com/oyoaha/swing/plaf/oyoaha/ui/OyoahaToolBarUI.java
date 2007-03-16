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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.border.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaToolBarUI extends MetalToolBarUI
{
  protected Border border;
  protected Hashtable borderTable = new Hashtable();

  protected JButton last;
  protected JButton next;

  public static ComponentUI createUI( JComponent c )
  {
    return new OyoahaToolBarUI();
  }

  protected void setBorderToRollover(Component c)
  {
    if (c instanceof JButton)
    {
      JButton b = (JButton)c;

      if(b.getUI() instanceof MetalButtonUI)
      {
        if(border==null)
        {
          border = UIManager.getBorder("Button.border");
        }

        if(b.getBorder() instanceof UIResource && b.getBorder()!=border)
        {
          borderTable.put(b, b.getBorder());
          b.setBorder(border);
        }
      }
    }
  }

  protected void setBorderToNonRollover(Component c)
  {
    setBorderToRollover(c);
  }

  protected void installNormalBorders (JComponent c)
  {
    // Put back the normal borders on buttons
    Component[] components = c.getComponents();

    for (int i=0;i<components.length;i++)
    {
      setBorderToNormal(components[i]);
    }
  }

  public void update(Graphics g, JComponent c)
  {
    OyoahaUtilities.paintBackground(g, c);
  }

  //-----

  protected void setState(JToolBar t, int _state)
  {
    Border b = t.getBorder();

    if(b!=null && b instanceof com.oyoaha.swing.plaf.oyoaha.border.OyoahaToolBarBorder)
    {
      ((com.oyoaha.swing.plaf.oyoaha.border.OyoahaToolBarBorder)b).setState(_state);
    }
  }

  protected int getState(JToolBar t)
  {
    Border b = t.getBorder();

    if(b!=null && b instanceof com.oyoaha.swing.plaf.oyoaha.border.OyoahaToolBarBorder)
    {
      return ((com.oyoaha.swing.plaf.oyoaha.border.OyoahaToolBarBorder)b).getState();
    }

    return OyoahaUtilities.UNSELECTED_ENABLED;
  }

  protected MouseInputListener createDockingListener( )
  {
    return new OyoahaDockingListener(toolBar);
  }

  protected void setDragOffset(Point p)
  {
    super.setDragOffset(p);
  }

  protected Rectangle getBumps()
  {
    Rectangle r = new Rectangle();

    if(toolBar.getSize().height <= toolBar.getSize().width)  // horizontal
    {
      if(OyoahaUtilities.isLeftToRight(toolBar))
      {
        r.setBounds( 0, 0, 14, toolBar.getSize().height);
      }
      else
      {
        r.setBounds( toolBar.getSize().width-14, 0, 14, toolBar.getSize().height );
      }
    }
    else  // vertical
    {
      r.setBounds( 0, 0, toolBar.getSize().width, 14 );
    }

    return r;
  }

  class OyoahaDockingListener extends DockingListener
  {
    protected boolean rolloverEnabled;
    
    public OyoahaDockingListener(JToolBar t)
    {
      super(t);
      rolloverEnabled = OyoahaUtilities.isRolloverEnabled(t);
    }

    public void mouseMoved(MouseEvent e)
    {
      super.mouseMoved(e);

      if(!toolBar.isFloatable() || !rolloverEnabled)
      {
        return;
      }

      Rectangle r = getBumps();

      if(r.contains(e.getPoint()))
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_ROLLOVER);
      }
      else
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_ENABLED);
      }

      toolBar.repaint(r.x, r.y, r.width, r.height);
    }

    public void mouseExited(MouseEvent e)
    {
      if(!toolBar.isFloatable())
      {
        return;
      }

      if(rolloverEnabled && getState(toolBar)!=OyoahaUtilities.UNSELECTED_PRESSED)
      {
        Rectangle r = getBumps();
        setState(toolBar, OyoahaUtilities.UNSELECTED_ENABLED);
        toolBar.repaint(r.x, r.y, r.width, r.height);
      }

      super.mouseExited(e);
    }

    public void mouseEntered(MouseEvent e)
    {
      super.mouseEntered(e);

      if(!toolBar.isFloatable() || !rolloverEnabled)
      {
        return;
      }

      Rectangle r = getBumps();

      if(r.contains(e.getPoint()))
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_ROLLOVER);
      }
      else
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_ENABLED);
      }

      toolBar.repaint(r.x, r.y, r.width, r.height);
    }

    public void mouseReleased(MouseEvent e)
    {
      super.mouseReleased(e);

      if(!toolBar.isFloatable() || !rolloverEnabled)
      {
        return;
      }

      if(getState(toolBar)!=OyoahaUtilities.UNSELECTED_PRESSED)
      return;

      Rectangle r = getBumps();

      if(r.contains(e.getPoint()))
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_ROLLOVER);
      }
      else
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_ENABLED);
      }

      toolBar.repaint(r.x, r.y, r.width, r.height);
    }

    public void mousePressed(MouseEvent e)
    {
      super.mousePressed(e);

      if(!toolBar.isFloatable() || !rolloverEnabled)
      {
        return;
      }

      if (!toolBar.isEnabled())
      {
        return;
      }

      Rectangle r = getBumps();

      if(r.contains(e.getPoint()))
      {
        setState(toolBar, OyoahaUtilities.UNSELECTED_PRESSED);
        toolBar.repaint(r.x, r.y, r.width, r.height);

        Point dragOffset = e.getPoint();

        if(!OyoahaUtilities.isLeftToRight(toolBar))
        {
          dragOffset.x -= toolBar.getSize().width - toolBar.getPreferredSize().width;
        }

        setDragOffset(dragOffset);
      }
      else
      {
        if(getState(toolBar)==OyoahaUtilities.UNSELECTED_PRESSED)
        {
          setState(toolBar, OyoahaUtilities.UNSELECTED_ENABLED);
          toolBar.repaint(r.x, r.y, r.width, r.height);
        }
      }
    }

    public void mouseDragged( MouseEvent e )
    {
      if(!toolBar.isFloatable())
      {
        return;
      }

      if(!rolloverEnabled || getState(toolBar)==OyoahaUtilities.UNSELECTED_PRESSED)
      {
        super.mouseDragged(e);
      }
    }
  }
}
