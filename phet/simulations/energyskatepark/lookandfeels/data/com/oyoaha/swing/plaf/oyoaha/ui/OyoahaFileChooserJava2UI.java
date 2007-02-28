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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.*;
import java.text.*;

import com.oyoaha.swing.plaf.oyoaha.*;
import com.oyoaha.swing.plaf.oyoaha.icon.*;
import com.oyoaha.swing.plaf.oyoaha.filechooser.*;

public class OyoahaFileChooserJava2UI extends OyoahaFileChooserUI
{
  public OyoahaFileChooserJava2UI(JFileChooser b)
  {
    super(b);
  }

  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaFileChooserJava2UI((JFileChooser)c);
  }

  protected void uninstallListeners(JFileChooser fc)
  {
    if(propertyChangeListener != null)
    {
      fc.removePropertyChangeListener(propertyChangeListener);
    }

    fc.removePropertyChangeListener(model);
    SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
    SwingUtilities.replaceUIActionMap(fc, null);
    fc.removeAncestorListener(ancestorListener);
    ancestorListener = null;
  }

  protected void installListeners(JFileChooser fc)
  {
    propertyChangeListener = createPropertyChangeListener(fc);

    if(propertyChangeListener != null)
    {
      fc.addPropertyChangeListener(propertyChangeListener);
    }

    fc.addPropertyChangeListener(model);

    ancestorListener = new AncestorListener()
    {
      public void ancestorAdded(AncestorEvent e)
      {
        JButton approveButton = getApproveButton(getFileChooser());

        if(approveButton != null)
        {
          approveButton.requestFocus();
        }
      }

      public void ancestorRemoved(AncestorEvent e)
      {

      }

      public void ancestorMoved(AncestorEvent e)
      {

      }
    };
    fc.addAncestorListener(ancestorListener);

    InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
    ActionMap actionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(fc, actionMap);
  }
  
  protected boolean getControlButtonsAreShown()
  {
    return this.getFileChooser().getControlButtonsAreShown();
  }

  protected InputMap getInputMap(int condition)
  {
    if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    {
      return (InputMap)UIManager.get("FileChooser.ancestorInputMap");
    }

    return null;
  }

  protected ActionMap getActionMap()
  {
    return createActionMap();
  }

  protected ActionMap createActionMap()
  {
    AbstractAction escAction = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(editing)
        {
         cancelEdit();
         list.repaint();
        }
        else
        {
          getFileChooser().cancelSelection();
        }
      }

      public boolean isEnabled()
      {
        return getFileChooser().isEnabled();
      }
    };

    ActionMap map = new ActionMapUIResource();
    map.put("cancelSelection", escAction);
    return map;
  }

  protected JComponent createList(JFileChooser fc)
  {
    list = new JList();
    list.setCellRenderer(new FileRendererJava2());

    if(fc.isMultiSelectionEnabled())
    {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    else
    {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    list.setModel(getOyoahaModel());
    list.addListSelectionListener(createListSelectionListener(fc));

    list.addMouseListener(createDoubleClickListener(fc, list));
    JScrollPane scrollpane = new JScrollPane(list);
    scrollpane.setPreferredSize(new Dimension(400,220));
    
    if (OyoahaUtilities.isVersion("1.4"))
    {
        list.setLayoutOrientation(1);
    }

    return scrollpane;
  }

  protected class FileRendererJava2 extends OyoahaListCellRenderer
  {
    protected FileRendererIcon fileRendererIcon;

    public FileRendererJava2()
    {
      this.drawsFocusBorderAroundIcon = false;
      fileRendererIcon = new FileRendererIcon();
      setIcon(fileRendererIcon);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus)
    {
      setComponentOrientation(list.getComponentOrientation());

      this.list = list;
      this.selected = selected;
      this.hasFocus = selected;
      this.isLeftToRight = OyoahaUtilities.isLeftToRight(list);

      File file = (File)value;
      String fileName = getFileChooser().getName(file);

      if(sortedMode==OyoahaDirectoryModel.BY_DATE)
      {
        DateFormat df = DateFormat.getDateInstance();
        setText(df.format(new Date(file.lastModified())) + " - " + fileName.toLowerCase());
      }
      else
      if(sortedMode==OyoahaDirectoryModel.BY_SIZE)
      {
        if(file.isDirectory())
        {
          setText(fileName.toLowerCase());
        }
        else
        {
          long l = file.length();
          NumberFormat nf = NumberFormat.getNumberInstance();
          setText(nf.format(l) + " - "  + fileName.toLowerCase());
        }
      }
      else
      if(sortedMode==OyoahaDirectoryModel.BY_CASESENSITIVE_NAME)
      {
        setText(fileName);
      }
      else
      {
        setText(fileName.toLowerCase());
      }

      Icon icon = getOyoahaFileView(getFileChooser()).getIcon(file);

      if(icon==null)
      {
        icon = getFileChooser().getIcon(file);
      }

      fileRendererIcon.icon = icon;

      //- - - - - - - - - - - - -

      setComponentOrientation(list.getComponentOrientation());

      if(selected || hasFocus)
      {
        editX = this.getBorderStart();
        setForeground(list.getSelectionForeground());
        fileRendererIcon.color=null;
      }

      Color c = getOyoahaFileView(getFileChooser()).getColor(file);

      if(c!=null)
      {
        fileRendererIcon.color = c;
      }
      else
      {
        fileRendererIcon.color = null;
      }

      setForeground(list.getForeground());

      setOpaque(false);
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder(UIManager.getBorder("List.selectionBorder"));

      return this;
    }

    public void paint(Graphics g)
    {
      int imageOffset;

      if(drawsFocusBorderAroundIcon)
      {
        imageOffset = 0;
      }
      else
      {
        imageOffset = getLabelStart()-1;

        if(imageOffset<0)
        imageOffset = 0;
      }

      Color bColor;

      if(selected)
      {
        bColor = this.list.getSelectionBackground();
      }
      else
      {
        bColor = this.list.getBackground();
      }

      if(bColor==null)
      {
        bColor = getBackground();
      }

      g.setColor(bColor);

      if(selected)
      {
        OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("Tree.Renderer");

        if(isLeftToRight)
        {
          Shape s = OyoahaUtilities.normalizeClip(g, imageOffset, 0, getWidth() - 1 - imageOffset, getHeight());

          if(o!=null && bColor instanceof UIResource)
          {
            o.paintBackground(g, this, imageOffset, 0, getWidth() - 1 - imageOffset, getHeight(), OyoahaUtilities.SELECTED_ENABLED);
          }
          else
          {
            OyoahaUtilities.paintColorBackground(g, this, imageOffset, 0, getWidth() - 1 - imageOffset, getHeight(), bColor, OyoahaUtilities.SELECTED_ENABLED);
          }

          g.setClip(s);
        }
        else
        {
          Shape s = OyoahaUtilities.normalizeClip(g, 0, 0, getWidth() - 1 - imageOffset, getHeight());

          if(o!=null && bColor instanceof UIResource)
          {
            o.paintBackground(g, this, 0, 0, getWidth() - 1 - imageOffset, getHeight(), OyoahaUtilities.SELECTED_ENABLED);
          }
          else
          {
            OyoahaUtilities.paintColorBackground(g, this, 0, 0, getWidth() - 1 - imageOffset, getHeight(), bColor, OyoahaUtilities.SELECTED_ENABLED);
          }

          g.setClip(s);
        }
      }

      super.paint(g);
    }
    

    public Dimension getPreferredSize()
    {
        if (OyoahaUtilities.isVersion("1.4"))
        {
            Dimension d = super.getPreferredSize();
            d.width = 200;
            d.height = 24;
            return d;
        }
        
        return super.getPreferredSize();
    }
  }

  public PropertyChangeListener createPropertyChangeListener(JFileChooser fc)
  {
    return new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent e)
      {
        String s = e.getPropertyName();

        if(s.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
        {
          doSelectedFileChanged(e);
        }
        else
        if(s.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY))
        {
          doDirectoryChanged(e);
        }
        else
        if(s.equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY))
        {
          doFilterChanged(e);
        }
        else
        if(s.equals(JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY))
        {
          doFileSelectionModeChanged(e);
        }
        else
        if(s.equals(JFileChooser.MULTI_SELECTION_ENABLED_CHANGED_PROPERTY))
        {
          doMultiSelectionChanged(e);
        }
        else
        if(s.equals(JFileChooser.ACCESSORY_CHANGED_PROPERTY))
        {
          doAccessoryChanged(e);
        }
        else
        if(s.equals(JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY))
        {
          doApproveButtonTextChanged(e);
        }
        else
        if(s.equals(JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY))
        {
          doDialogTypeChanged(e);
        }
        else
        if(s.equals(JFileChooser.APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY))
        {
          doApproveButtonMnemonicChanged(e);
        }
        else
        if(s.equals(JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY))
        {
          doControlButtonsChanged(e);
        }
      }
    };
  }
}