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
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class OyoahaSpinnerUI  extends BasicSpinnerUI 
{
  protected static final OyoahaArrowButtonHandler nextButtonHandler = new OyoahaArrowButtonHandler(true);
  protected static final OyoahaArrowButtonHandler previousButtonHandler = new OyoahaArrowButtonHandler(false);
  
  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaSpinnerUI();
  }
  
  protected Component createPreviousButton() 
  {
    JButton b = new OyoahaScrollButton(SwingConstants.SOUTH, ((Integer)(UIManager.get("ScrollBar.width"))).intValue(), false, true, true);
	b.addActionListener(previousButtonHandler);
	b.addMouseListener(previousButtonHandler);
	return b;
  }

  protected Component createNextButton() 
  {
    JButton b = new OyoahaScrollButton(SwingConstants.NORTH, ((Integer)(UIManager.get("ScrollBar.width"))).intValue(), false, true, true);
    b.addActionListener(nextButtonHandler);
    b.addMouseListener(nextButtonHandler);
    return b;
  }
  
  protected static class OyoahaArrowButtonHandler extends MouseAdapter implements ActionListener 
  {
	protected final javax.swing.Timer autoRepeatTimer;
	protected final boolean isNext;
	protected JSpinner spinner = null;

	public OyoahaArrowButtonHandler(boolean isNext) 
    {
	    this.isNext = isNext;
	    autoRepeatTimer = new javax.swing.Timer(60, this);
	    autoRepeatTimer.setInitialDelay(300);
	}

	protected JSpinner eventToSpinner(AWTEvent e) 
    {
	    Object src = e.getSource();
        
	    while ((src instanceof Component) && !(src instanceof JSpinner)) 
        {
		    src = ((Component)src).getParent();
	    }
        
	    return (src instanceof JSpinner) ? (JSpinner)src : null;
	}

	public void actionPerformed(ActionEvent e) 
    {
	    if (spinner != null) 
        {
            try 
            {
                spinner.commitEdit();
                Object value = (isNext) ? spinner.getNextValue() : spinner.getPreviousValue();
                
                if (value != null) 
                {
                    spinner.setValue(value);
                }
            } 
            catch (IllegalArgumentException iae) 
            {
                UIManager.getLookAndFeel().provideErrorFeedback(spinner);
            } 
            catch (ParseException pe) 
            {
                UIManager.getLookAndFeel().provideErrorFeedback(spinner);
            }
	    }
	}

	public void mousePressed(MouseEvent e) 
    {
	    if (SwingUtilities.isLeftMouseButton(e) && e.getComponent().isEnabled()) 
        {
		    spinner = eventToSpinner(e);
		    autoRepeatTimer.start();
	    }
	}

	public void mouseReleased(MouseEvent e) 
    {
	    autoRepeatTimer.stop();	    
	    spinner = null;
	}
  }
}