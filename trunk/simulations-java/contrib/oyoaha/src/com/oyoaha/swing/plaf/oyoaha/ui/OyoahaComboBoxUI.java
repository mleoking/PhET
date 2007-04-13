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
import java.beans.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaComboBoxUI extends MetalComboBoxUI implements OyoahaButtonLikeComponent
{
  private static Icon comboIcon;

  public static ComponentUI createUI(JComponent c)
  {
    if(comboIcon==null)
    {
      comboIcon = UIManager.getIcon("ComboBox.icon");

      if(comboIcon==null)
      comboIcon = new MetalComboBoxIcon();
    }

    return new OyoahaComboBoxUI();
  }
  
  public boolean isBorderPainted(Component c)
  {
    return true;
  }

  protected ComboBoxEditor createEditor()
  {
    return new OyoahaComboBoxEditor(comboBox.isEnabled());
  }

  protected ComboPopup createPopup()
  {
    BasicComboPopup popup = new BasicComboPopup(comboBox);
    popup.getAccessibleContext().setAccessibleParent(comboBox);
    return popup;
  }

  protected ListCellRenderer createRenderer()
  {
    return (ListCellRenderer)(UIManager.get("List.cellRenderer"));
  }

  public void installUI(JComponent c)
  {
    super.installUI(c);

    configureArrowButton();
    OyoahaUtilities.installRolloverListener(c);
    OyoahaUtilities.setOpaque(c);
  }

  public void uninstallUI(JComponent c)
  {
    super.uninstallUI(c);

    OyoahaUtilities.uninstallRolloverListener(c);
    OyoahaUtilities.unsetOpaque(c);
  }

  public boolean isFocusTraversable(JComboBox c)
  {
    return !comboBox.isEditable();
  }

  protected void installListeners()
  {
    if ((itemListener = createItemListener()) != null)
    {
      comboBox.addItemListener(itemListener);
    }

    propertyChangeListener = new OyoahaPropertyChangeListener(comboBox);
    comboBox.addPropertyChangeListener(propertyChangeListener);

    keyListener = createKeyListener();
    focusListener = createFocusListener();
    popupKeyListener = popup.getKeyListener();
    popupMouseListener = popup.getMouseListener();
    popupMouseMotionListener = popup.getMouseMotionListener();

    if (comboBox.getModel() != null)
    {
      if ((listDataListener = createListDataListener()) != null)
      {
        comboBox.getModel().addListDataListener(listDataListener);
      }
    }
  }

  public class OyoahaPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler
  {
    protected JComboBox comboBox;
    
    public OyoahaPropertyChangeListener(JComboBox comboBox)
    {
        this.comboBox = comboBox;
    }
    
    public void propertyChange(PropertyChangeEvent e)
    {
      super.propertyChange(e);
      comboBox.setRequestFocusEnabled(true);
    }
  }

  public void update(Graphics g, JComponent c)
  {
    OyoahaUtilities.paintBackground(g, c);
    paint(g,c);
  }

  public void paint(Graphics g, JComponent component)
  {
    Dimension d = component.getSize();
    Insets insets = component.getInsets();

    int h = d.height - (insets.top+insets.bottom);
    comboIcon.paintIcon(component, g, d.width-(insets.right+2+comboIcon.getIconWidth()), ((h-comboIcon.getIconHeight())/2)+insets.top);

    if(!comboBox.isEditable())
    {
      ListCellRenderer renderer = comboBox.getRenderer();
      Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
      c.setFont(currentValuePane.getFont());

      boolean opaque = true;

      if(c instanceof JComponent)
      {
        opaque = ((JComponent)c).isOpaque();
        ((JComponent)c).setOpaque(false);
      }

      if(!comboBox.isEnabled())
      {
        c.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      }
      else
      {
        c.setForeground(comboBox.getForeground());
      }

      int cWidth = d.width - (insets.left + insets.right + comboIcon.getIconWidth() + 4);
      currentValuePane.paintComponent(g, c, component, insets.left, insets.top, cWidth, d.height-(insets.top + insets.bottom));

      if(c instanceof JComponent)
      {
        ((JComponent)c).setOpaque(opaque);
      }
    }

    // Paint the focus
    if(component.hasFocus())
    {
      g.setColor(MetalLookAndFeel.getFocusColor());
      g.drawLine(insets.left, insets.top, d.width-(insets.right+1), insets.top);
      g.drawLine(insets.left, d.height-(insets.bottom+1), d.width-(insets.right+1), d.height-(insets.bottom+1));
    }
  }

  //--- --- --- --- --- --- --- --- ---

  protected void installComponents()
  {
    if(comboBox.isEditable())
    {
      addEditor();
    }

    comboBox.add(currentValuePane);
    arrowButton = null;
  }

  protected void uninstallComponents()
  {
    unconfigureArrowButton();

    if(editor!=null)
    {
      unconfigureEditor();
    }

    comboBox.removeAll(); // Just to be safe.
    arrowButton = null;
  }

  protected JButton createArrowButton()
  {
    return null;
  }

  public void configureArrowButton()
  {
    comboBox.setRequestFocusEnabled((!comboBox.isEditable()) && comboBox.isEnabled());

    if ( keyListener != null )
    {
        comboBox.addKeyListener( keyListener );
    }
    if ( popupKeyListener != null )
    {
        comboBox.addKeyListener( popupKeyListener );
    }
    if ( focusListener != null )
    {
        comboBox.addFocusListener( focusListener );
    }
    if ( popupMouseListener != null )
    {
        comboBox.addMouseListener( popupMouseListener );
    }
    if ( popupMouseMotionListener != null )
    {
        comboBox.addMouseMotionListener( popupMouseMotionListener );
    }
  }

  public void unconfigureArrowButton()
  {
    if ( popupMouseListener != null )
    {
      comboBox.removeMouseListener( popupMouseListener );
    }

    if ( popupMouseMotionListener != null )
    {
      comboBox.removeMouseMotionListener( popupMouseMotionListener );
    }

    if ( keyListener != null )
    {
      comboBox.removeKeyListener( keyListener );
    }

    if ( popupKeyListener != null )
    {
      comboBox.removeKeyListener( popupKeyListener );
    }

    if ( focusListener != null )
    {
      comboBox.removeFocusListener( focusListener );
    }
  }
}