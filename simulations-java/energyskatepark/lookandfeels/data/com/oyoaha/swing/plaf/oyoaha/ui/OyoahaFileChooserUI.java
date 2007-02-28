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
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.*;

import com.oyoaha.swing.plaf.oyoaha.*;
import com.oyoaha.swing.plaf.oyoaha.icon.*;
import com.oyoaha.swing.plaf.oyoaha.filechooser.*;

public class OyoahaFileChooserUI extends BasicFileChooserUI implements ActionListener
{
  public OyoahaFileChooserUI(JFileChooser b)
  {    
    super(b);    
  }

  public static ComponentUI createUI(JComponent c)
  { 
    return new OyoahaFileChooserUI((JFileChooser)c);
  }

  protected JPanel centerPanel;

  protected JComboBox directoryComboBox;
  protected OyoahaDirectoryComboBoxModel directoryComboBoxModel;
  protected Action directoryComboBoxAction = new DirectoryComboBoxAction();

  protected FilterComboBoxModel filterComboBoxModel;
  protected JComboBox filterComboBox;

  protected JTextField filenameTextField;

  //use table instead?
  protected JList list;

  protected JButton approveButton;
  protected JButton cancelButton;
  protected JButton upFolderButton;

  protected JPanel bottomPanel;

  //- - - - - - - - - - - - - - - - - - - - - - - - -

  protected static final Dimension hstrut10 = new Dimension(6, 1);
  protected static final Dimension vstrut10 = new Dimension(1, 6);
  protected static final Insets insets = new Insets(0,3,0,3);
  protected static final Insets shrinkwrap = new Insets(0,0,0,0);

  //- - - - - - - - - - - - - - - - - - - - - - - - -

  protected int    lookInLabelMnemonic = 0;
  protected String lookInLabelText = null;

  protected int    fileNameLabelMnemonic = 0;
  protected String fileNameLabelText = null;

  protected int    filesOfTypeLabelMnemonic = 0;
  protected String filesOfTypeLabelText = null;

  protected String upFolderToolTipText = null;
  protected String upFolderAccessibleName = null;

  protected String homeFolderToolTipText = null;
  protected String homeFolderAccessibleName = null;

  protected String newFolderToolTipText = null;
  protected String newFolderAccessibleName = null;

  protected String listViewButtonToolTipText = null;
  protected String listViewButtonAccessibleName = null;

  protected String detailsViewButtonToolTipText = null;
  protected String detailsViewButtonAccessibleName = null;

  //- - - - - - - - - - - - - - - - - - - - - - - - -

  protected PropertyChangeListener propertyChangeListener = null;
  protected AncestorListener ancestorListener = null;
  protected AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();
  protected OyoahaDirectoryModel model = null;
  protected FileView fileView;
  protected Action newFolderAction;

  protected Icon sortIcon;
  protected String sortButtonText;

  protected int organizedMode;
  protected int sortedMode;

  protected JPopupMenu popup;

  //- - - - - - - - - - - - - - - - - - - - - - - - -

  public void uninstallComponents(JFileChooser fc)
  {
    fc.removeAll();
    popup = null;
  }

  protected void installIcons(JFileChooser fc)
  {
    super.installIcons(fc);
    sortIcon = UIManager.getIcon("FileChooser.sortIcon");
  }

  protected void installStrings(JFileChooser fc)
  {
    super.installStrings(fc);
    sortButtonText = UIManager.getString("FileChooser.sortButtonText");

    //- - - - - - - -

    lookInLabelMnemonic = UIManager.getInt("FileChooser.lookInLabelMnemonic");
    lookInLabelText = UIManager.getString("FileChooser.lookInLabelText");

    fileNameLabelMnemonic = UIManager.getInt("FileChooser.fileNameLabelMnemonic");
    fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText");

    filesOfTypeLabelMnemonic = UIManager.getInt("FileChooser.filesOfTypeLabelMnemonic");
    filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText");

    upFolderToolTipText =  UIManager.getString("FileChooser.upFolderToolTipText");
    upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName");

    newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText");
    newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName");
  }

  protected void uninstallIcons(JFileChooser fc)
  {
    super.uninstallIcons(fc);
    sortIcon = null;
  }

  protected void uninstallStrings(JFileChooser fc)
  {
    super.uninstallStrings(fc);
    sortButtonText = null;
  }

  protected void uninstallListeners(JFileChooser fc)
  {
    if(propertyChangeListener != null)
    {
      fc.removePropertyChangeListener(propertyChangeListener);
    }

    fc.removePropertyChangeListener(model);
//SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
//SwingUtilities.replaceUIActionMap(fc, null);
    fc.removeAncestorListener(ancestorListener);
    ancestorListener = null;
  }
  
  protected boolean getControlButtonsAreShown()
  {
    //return fc.getControlButtonsAreShown();
    return true;
  }

  public void installComponents(JFileChooser fc)
  {
    OyoahaFileView fview = getOyoahaFileView(fc);

    Object od = fview.getFileChooserPreference("organize");

    if(od==null)
    {
      organizedMode = OyoahaDirectoryModel.USE_EXTENTION;
    }
    else
    {
      organizedMode = OyoahaDirectoryModel.getModeFromString(od.toString());
    }

    od = fview.getFileChooserPreference("sort");

    if(od==null)
    {
      sortedMode = OyoahaDirectoryModel.BY_NAME;
    }
    else
    {
      sortedMode = OyoahaDirectoryModel.geSortFromString(od.toString());
    }

    //- - - - - - - - - - - - - - - - - - - -

    fc.setLayout(new BoxLayout(fc, BoxLayout.Y_AXIS));
    fc.add(Box.createRigidArea(vstrut10));

    //- - - - - - - - - - - - - - - - - - - -

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
    fc.add(topPanel);
    fc.add(Box.createRigidArea(vstrut10));

    JButton sortButton = new JButton(sortIcon);
    sortButton.setToolTipText("Sort options");
    sortButton.getAccessibleContext().setAccessibleName("Sort options");
    sortButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    sortButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    sortButton.setMargin(shrinkwrap);

    sortButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JPopupMenu pop = getSortPopupMenu();
        JButton button = (JButton)e.getSource();
        pop.show(button,0,button.getHeight());
      }
    });
    
    topPanel.add(Box.createRigidArea(hstrut10));
    topPanel.add(sortButton);
    topPanel.add(Box.createRigidArea(hstrut10));

    /*
    JLabel l = new JLabel(lookInLabelText);
    l.setDisplayedMnemonic(lookInLabelMnemonic);
    l.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    l.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    
    topPanel.add(Box.createRigidArea(hstrut10));
    topPanel.add(l);
    topPanel.add(Box.createRigidArea(hstrut10));
    */

    directoryComboBox = new JComboBox()
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
      }

      public Dimension getPreferredSize()
      {
        return new Dimension(180, super.getPreferredSize().height);
      }

      public Dimension getMinimumSize()
      {
        return getPreferredSize();
      }
    };

    directoryComboBox.putClientProperty( "JComboBox.lightweightKeyboardNavigation", "Lightweight" );
    //l.setLabelFor(directoryComboBox);
    directoryComboBoxModel = createDirectoryComboBoxModel(fc);
    directoryComboBox.setModel(directoryComboBoxModel);
    directoryComboBox.addActionListener(directoryComboBoxAction);
    directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
    directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    directoryComboBox.setAlignmentY(JComponent.CENTER_ALIGNMENT);

    topPanel.add(directoryComboBox);
    topPanel.add(Box.createRigidArea(hstrut10));

    upFolderButton = new JButton(upFolderIcon);
    upFolderButton.setToolTipText(upFolderToolTipText);
    upFolderButton.getAccessibleContext().setAccessibleName(upFolderAccessibleName);
    upFolderButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    upFolderButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    upFolderButton.setMargin(shrinkwrap);

    upFolderButton.addActionListener(getChangeToParentDirectoryAction());
    topPanel.add(upFolderButton);
    topPanel.add(Box.createRigidArea(hstrut10));

    JButton b = new JButton(newFolderIcon);
    b.setToolTipText(newFolderToolTipText);
    b.getAccessibleContext().setAccessibleName(newFolderAccessibleName);
    b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    b.setMargin(shrinkwrap);

    b.addActionListener(getNewFolderAction());
    topPanel.add(b);
    topPanel.add(Box.createRigidArea(hstrut10));

    /*b = new JButton(sortIcon);
    b.setToolTipText("Sort options");
    b.getAccessibleContext().setAccessibleName("Sort options");
    b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    b.setMargin(shrinkwrap);

    b.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JPopupMenu pop = getSortPopupMenu();
        JButton button = (JButton)e.getSource();
        pop.show(button,0,button.getHeight());
      }
    });
    topPanel.add(b);
    topPanel.add(Box.createRigidArea(hstrut10));*/

    //- - - - - - - - - - - - - - - - - - - -

    centerPanel = new JPanel(new BorderLayout());
    JComponent p = createList(fc);

    centerPanel.add(p, BorderLayout.CENTER);
    centerPanel.add(getAccessoryPanel(), BorderLayout.EAST);
    JComponent accessory = fc.getAccessory();

    if(accessory != null)
    {
      getAccessoryPanel().add(accessory);
    }

    fc.add(centerPanel);

    //- - - - - - - - - - - - - - - - - - - -

    JPanel p1 = getBottomPanel();
    p1.setLayout(new GridBagLayout());

    //--------------------------------------------------------------------------

    JLabel fnl = new JLabel(fileNameLabelText);
    fnl.setDisplayedMnemonic(fileNameLabelMnemonic);

    filenameTextField = new JTextField()
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
      }
    };

    filenameTextField.setEditable(true);

    fnl.setLabelFor(filenameTextField);
    filenameTextField.addActionListener(getApproveSelectionAction());

    filenameTextField.addFocusListener(new FocusAdapter()
    {
        public void focusGained(FocusEvent e)
        {
          list.clearSelection();
        }
      }
    );

    File f = fc.getSelectedFile();

    if(f != null)
    {
      setFileName(fc.getName(f));
    }

    JLabel ftl = new JLabel(filesOfTypeLabelText);
    ftl.setDisplayedMnemonic(filesOfTypeLabelMnemonic);

    filterComboBoxModel = createFilterComboBoxModel();
    fc.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    ftl.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());

    approveButton = new JButton(getApproveButtonText(fc))
    {
      public Dimension getMaximumSize()
      {
        return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ? approveButton.getPreferredSize() : cancelButton.getPreferredSize();
      }
    };

    approveButton.setMnemonic(getApproveButtonMnemonic(fc));
    approveButton.addActionListener(getApproveSelectionAction());
    approveButton.setToolTipText(getApproveButtonToolTipText(fc));

    cancelButton = new JButton(cancelButtonText)
    {
      public Dimension getMaximumSize()
      {
        return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ? approveButton.getPreferredSize() : cancelButton.getPreferredSize();
      }
    };

    cancelButton.setMnemonic(cancelButtonMnemonic);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());

    //--------------------------------------------------------------------------

    installComponent(p1, fnl,                    GridBagConstraints.WEST,   GridBagConstraints.BOTH, insets, 0, 0, 1, 1, 0.0D, 0.0D);
    installComponent(p1, filenameTextField,      GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 1, 0, 1, 1, 1.0D, 0.0D);
    installComponent(p1, approveButton,          GridBagConstraints.EAST,   GridBagConstraints.BOTH, insets, 2, 0, 1, 1, 0.0D, 0.0D);

    installComponent(p1, new JPanel(),           GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 1, 2, 1, 1.0D, 0.0D);

    installComponent(p1, ftl,                    GridBagConstraints.WEST,   GridBagConstraints.BOTH, insets, 0, 2, 1, 1, 0.0D, 0.0D);
    installComponent(p1, filterComboBox,         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 1, 2, 1, 1, 1.0D, 0.0D);
    installComponent(p1, cancelButton,           GridBagConstraints.EAST,   GridBagConstraints.BOTH, insets, 2, 2, 1, 1, 0.0D, 0.0D);

    //--------------------------------------------------------------------------

    fc.add(Box.createRigidArea(vstrut10));

    Dimension dd = p1.getPreferredSize();
    p1.setMaximumSize(new Dimension(Short.MAX_VALUE, dd.height));

    fc.add(p1);
    fc.add(Box.createRigidArea(vstrut10));

    if(getControlButtonsAreShown())
    {
      addControlButtons();
    }
    else
    {
      removeControlButtons();
    }
  }
  
  public final static void installComponent(Container container, Component obj, int anchor, int constraints, Insets insets, int i, int j, int k, int l, double d, double d1)
  {
    GridBagLayout gridbaglayout = (GridBagLayout)container.getLayout();
    GridBagConstraints gridbagconstraints = new GridBagConstraints();
    gridbagconstraints.gridx = i;
    gridbagconstraints.gridy = j;
    gridbagconstraints.gridwidth = k;
    gridbagconstraints.gridheight = l;
    gridbagconstraints.weightx = d;
    gridbagconstraints.weighty = d1;
    gridbagconstraints.fill = constraints;
    gridbagconstraints.insets = insets;
    gridbagconstraints.anchor = anchor;

    container.add(obj);
    gridbaglayout.setConstraints(obj, gridbagconstraints);
  }

  public void actionPerformed(ActionEvent e)
  {
    String name = ((JComponent)e.getSource()).getName();

    if(name==null)
    {
      return;
    }

    if(name.startsWith("use_"))
    {
      if(name.equals("use_extention"))
      {
        organizedMode = OyoahaDirectoryModel.USE_EXTENTION;
      }
      else
      if(name.equals("use_csextention"))
      {
        organizedMode = OyoahaDirectoryModel.USE_CASESENSITIVE_EXTENTION;
      }
      else
      if(name.equals("use_type"))
      {
        organizedMode = OyoahaDirectoryModel.USE_TYPE;
      }
      else
      if(name.equals("use_none"))
      {
        organizedMode = OyoahaDirectoryModel.USE_NONE;
      }

      OyoahaDirectoryModel m = getOyoahaModel();

      if(m!=null)
      {
        list.clearSelection();
        m.setUse(organizedMode);
      }
    }
    else
    if(name.startsWith("by_"))
    {
      list.clearSelection();

      if(name.equals("by_name"))
      {
        sortedMode = OyoahaDirectoryModel.BY_NAME;
      }
      else
      if(name.equals("by_csname"))
      {
        sortedMode = OyoahaDirectoryModel.BY_CASESENSITIVE_NAME;
      }
      else
      if(name.equals("by_date"))
      {
        sortedMode = OyoahaDirectoryModel.BY_DATE;
      }
      else
      if(name.equals("by_size"))
      {
        sortedMode = OyoahaDirectoryModel.BY_SIZE;
      }

      OyoahaDirectoryModel m = getOyoahaModel();

      if(m!=null)
      {
        list.clearSelection();
        m.setMode(sortedMode);
      }
    }
    else
    {
      if(name.equals("inverse"))
      {
        OyoahaDirectoryModel m = getOyoahaModel();

        if(m!=null)
        {
          list.clearSelection();
          m.inverse();
        }
      }
    }
  }

  protected JPopupMenu getSortPopupMenu()
  {
    if(popup==null)
    {
      popup = new JPopupMenu();

      JMenu menu1 = new JMenu("organize by...");

      JMenuItem extention = new JRadioButtonMenuItem("Extention");
      JMenuItem csextention = new JRadioButtonMenuItem("Extention (Case Sensitive)");
      JMenuItem type = new JRadioButtonMenuItem("Type");
      JMenuItem none = new JRadioButtonMenuItem("None");

      extention.setName("use_extention");
      csextention.setName("use_csextention");
      type.setName("use_type");
      none.setName("use_none");

      ButtonGroup group = new ButtonGroup();

      group.add(extention);
      group.add(csextention);
      group.add(type);
      group.add(none);

      switch(organizedMode)
      {
        case OyoahaDirectoryModel.USE_CASESENSITIVE_EXTENTION:
        csextention.setSelected(true);
        break;
        case OyoahaDirectoryModel.USE_TYPE:
        type.setSelected(true);
        break;
        case OyoahaDirectoryModel.USE_NONE:
        none.setSelected(true);
        break;
        default:
        extention.setSelected(true);
        break;
      }

      extention.addActionListener(this);
      csextention.addActionListener(this);
      type.addActionListener(this);
      none.addActionListener(this);

      menu1.add(extention);
      menu1.add(csextention);
      menu1.add(type);
      menu1.add(none);

      popup.add(menu1);

      JMenu menu2 = new JMenu("sort by...");

      JMenuItem name = new JRadioButtonMenuItem("Name");
      JMenuItem csname = new JRadioButtonMenuItem("Name (Case Sensitive)");
      JMenuItem size = new JRadioButtonMenuItem("Size");
      JMenuItem date = new JRadioButtonMenuItem("Date");

      name.setName("by_name");
      csname.setName("by_csname");
      size.setName("by_size");
      date.setName("by_date");

      group = new ButtonGroup();

      group.add(name);
      group.add(csname);
      group.add(size);
      group.add(date);

      switch(sortedMode)
      {
        case OyoahaDirectoryModel.BY_CASESENSITIVE_NAME:
        csname.setSelected(true);
        break;
        case OyoahaDirectoryModel.BY_DATE:
        date.setSelected(true);
        break;
        case OyoahaDirectoryModel.BY_SIZE:
        size.setSelected(true);
        break;
        default:
        name.setSelected(true);
        break;
      }

      name.addActionListener(this);
      csname.addActionListener(this);
      size.addActionListener(this);
      date.addActionListener(this);

      menu2.add(name);
      menu2.add(csname);
      menu2.add(size);
      menu2.add(date);

      JMenuItem inverse = new JCheckBoxMenuItem("Inverse sort order");
      inverse.setName("inverse");
      inverse.addActionListener(this);
      menu2.add(inverse);

      popup.add(menu2);
    }

    return popup;
  }

  protected JPanel getBottomPanel()
  {
    if(bottomPanel == null)
    {
      bottomPanel = new JPanel();
    }

    return bottomPanel;
  }

  protected void createModel()
  {
    model = new OyoahaDirectoryModel(getFileChooser(), getOyoahaFileView(getFileChooser()));
  }

  public OyoahaDirectoryModel getOyoahaModel()
  {
    return model;
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

//InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
//ActionMap actionMap = getActionMap();
//SwingUtilities.replaceUIActionMap(fc, actionMap);
  }

  /*protected InputMap getInputMap(int condition)
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
  }*/

  protected JComponent createList(JFileChooser fc)
  {
    list = new JList();

    list.setCellRenderer(new FileRenderer());

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
    scrollpane.setPreferredSize(new Dimension(400,200));
    
    return scrollpane;
  }

//------------------------------------------------------------------------------

  protected JTextField editCell = null;
  protected int lastIndex = -1;
  protected int currentEditingIndex = -1;
  protected boolean editing = false;
  protected int editX = 20;

  protected void setEditIndex(int i)
  {
    lastIndex = i;
  }

  protected void resetEditIndex()
  {
    lastIndex = -1;
  }

  protected void cancelEdit()
  {
    editing = false;

    if(editCell!=null && editCell.isVisible())
    {
      //if value has changed try to rename the item
      if(currentEditingIndex>=0)
      {
        File f = (File)list.getModel().getElementAt(currentEditingIndex);
        String newFileName = editCell.getText();
        newFileName = newFileName.trim();

        if(!newFileName.equals(getFileChooser().getName(f)))
        {
          File f2 = getFileChooser().getFileSystemView().createFileObject(getFileChooser().getCurrentDirectory(), newFileName);

          if(f.renameTo(f2))
          {
            getOyoahaModel().replace(f, f2);
          }
          else
          {
            //TODO - show a dialog indicating failure
          }
        }
      }

      currentEditingIndex = -1;
      editCell.setVisible(false);
      editCell.setText("");
      editCell.setBounds(0,0,1,1);
      list.remove(editCell);
    }
  }

  protected JTextField getEditCell()
  {
      if(editCell==null)
      {
        editCell = new JTextField();
        editCell.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.BLACK), 1), BorderFactory.createEmptyBorder(0,2,0,2)));
        editCell.addActionListener(new EditActionListener());
      }

      return editCell;
  }

  protected class EditActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      cancelEdit();
      list.repaint();
    }
  }

//------------------------------------------------------------------------------

  public void clearIconCache()
  {
    getOyoahaFileView(getFileChooser()).clearIconCache();
  }

  public OyoahaFileView getOyoahaFileView(JFileChooser fc)
  {
    FileView tmp = getFileView(fc);

    if(!(tmp instanceof OyoahaFileView))
    return new OyoahaFileView(fc);

    return (OyoahaFileView)tmp;
  }

  public FileView getFileView(JFileChooser fc)
  {
    if(fileView==null)
    {
      fileView = new OyoahaFileView(fc);
    }

    return fileView;
  }

  protected class FileRenderer extends OyoahaListCellRenderer
  {
    protected FileRendererIcon fileRendererIcon;

    public FileRenderer()
    {
      this.drawsFocusBorderAroundIcon = false;
      fileRendererIcon = new FileRendererIcon();
      setIcon(fileRendererIcon);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus)
    {
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

      if(selected || hasFocus)
      {
        editX = this.getBorderStart();
        setForeground(list.getSelectionForeground());
        fileRendererIcon.color = null;
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
            return d;
        }
        
        return super.getPreferredSize();
    }
  }

  public void uninstallUI(JComponent c)
  {
    c.removePropertyChangeListener(filterComboBoxModel);
    cancelButton.removeActionListener(getCancelSelectionAction());
    approveButton.removeActionListener(getApproveSelectionAction());
    filenameTextField.removeActionListener(getApproveSelectionAction());

    super.uninstallUI(c);
  }

  protected void setFileSelected()
  {
    File f = getFileChooser().getSelectedFile();

    if(f != null && getOyoahaModel().contains(f))
    {
      list.setSelectedIndex(getOyoahaModel().indexOf(f));
      list.ensureIndexIsVisible(list.getSelectedIndex());
    }
    else
    {
      list.clearSelection();
    }
  }

  protected void doSelectedFileChanged(PropertyChangeEvent e)
  {
    cancelEdit();
    File f = (File) e.getNewValue();

    if(f != null)
    {
      setFileName(getFileChooser().getName(f));
    }
    else
    {
      setFileName(null);
    }

    setFileSelected();
  }

  protected void doDirectoryChanged(PropertyChangeEvent e)
  {
    cancelEdit();
    resetEditIndex();
    clearIconCache();
    list.clearSelection();
    File currentDirectory = getFileChooser().getCurrentDirectory();

    if(currentDirectory != null)
    {
      directoryComboBoxModel.addItem(currentDirectory);
      // Enable the newFolder action if the current directory
      // is writable.
      // PENDING(jeff) - broken - fix
      getNewFolderAction().setEnabled(currentDirectory.canWrite());

      if(currentDirectory.getParent() == null)
      {
        upFolderButton.setEnabled(false);
      }
      else
      {
        upFolderButton.setEnabled(true);
      }
    }
  }

  protected void doFilterChanged(PropertyChangeEvent e)
  {
    cancelEdit();
    resetEditIndex();
    clearIconCache();
    list.clearSelection();
  }

  protected void doFileSelectionModeChanged(PropertyChangeEvent e)
  {
    cancelEdit();
    resetEditIndex();
    clearIconCache();
    list.clearSelection();
  }

  protected void doMultiSelectionChanged(PropertyChangeEvent e)
  {
    if(getFileChooser().isMultiSelectionEnabled())
    {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    else
    {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.clearSelection();
      getFileChooser().setSelectedFiles(null);
    }
  }

  protected void doAccessoryChanged(PropertyChangeEvent e)
  {
    if(getAccessoryPanel() != null)
    {
      if(e.getOldValue() != null)
      {
        getAccessoryPanel().remove((JComponent) e.getOldValue());
      }

      JComponent accessory = (JComponent) e.getNewValue();

      if(accessory != null)
      {
        getAccessoryPanel().add(accessory, BorderLayout.CENTER);
      }
    }
  }

  protected void doApproveButtonTextChanged(PropertyChangeEvent e)
  {
    JFileChooser chooser = getFileChooser();
    approveButton.setText(getApproveButtonText(chooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
    approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
  }

  protected void doDialogTypeChanged(PropertyChangeEvent e)
  {
    JFileChooser chooser = getFileChooser();
    approveButton.setText(getApproveButtonText(chooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
    approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
  }

  protected void doApproveButtonMnemonicChanged(PropertyChangeEvent e)
  {
    approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
  }

  protected void doControlButtonsChanged(PropertyChangeEvent e)
  {
    if(getControlButtonsAreShown())
    {
        addControlButtons();
    }
    else
    {
        removeControlButtons();
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
        /*else
        if(s.equals(JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY))
        {
          doControlButtonsChanged(e);
        }*/
      }
    };
  }

  protected void removeControlButtons()
  {
    approveButton.setVisible(false);
    cancelButton.setVisible(false);
  }

  protected void addControlButtons()
  {
    approveButton.setVisible(true);
    cancelButton.setVisible(true);
  }

  public void ensureFileIsVisible(JFileChooser fc, File f)
  {
    if(getOyoahaModel().contains(f))
    {
      list.ensureIndexIsVisible(getOyoahaModel().indexOf(f));
    }
  }

  public void rescanCurrentDirectory(JFileChooser fc)
  {
    getOyoahaModel().invalidateFileCache();
    getOyoahaModel().validateFileCache();
  }

  public String getFileName()
  {
    if(filenameTextField != null)
    {
      return filenameTextField.getText();
    }
    else
    {
      return null;
    }
  }

  public void setFileName(String filename)
  {
    if(filenameTextField != null)
    {
      filenameTextField.setText(filename);
    }
  }

  public String getDirectoryName()
  {
    //TODO
    return null;
  }

  public void setDirectoryName(String dirname)
  {
    //TODO
  }

  protected OyoahaDirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser fc)
  {
    return new OyoahaDirectoryComboBoxRenderer(fc);
  }

  protected OyoahaDirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser fc)
  {
//System.out.println("createDirectoryComboBoxModel " + getOyoahaFileView(fc) + " " + getOyoahaFileView(fc).getFileSystem());    
    return getOyoahaFileView(fc).getFileSystem();
  }

  protected int getDepth(File file)
  {
    int depth = 0;

    while(file.getParent() != null)
    {
      depth++;
      file = getFileChooser().getFileSystemView().createFileObject(file.getParent());
    }

    return depth;
  }

  protected FilterComboBoxRenderer createFilterComboBoxRenderer()
  {
    return new FilterComboBoxRenderer();
  }

  public class FilterComboBoxRenderer extends OyoahaListCellRenderer
  {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      FileFilter filter = (FileFilter)value;

      if(filter != null)
      {
        ((JLabel)c).setText(filter.getDescription());
      }

      return c;
    }
  }

  protected FilterComboBoxModel createFilterComboBoxModel()
  {
      return new FilterComboBoxModel();
  }

  protected class FilterComboBoxModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener
  {
      protected FileFilter[] filters;

      protected FilterComboBoxModel()
      {
          super();
          filters = getFileChooser().getChoosableFileFilters();
      }

      public void propertyChange(PropertyChangeEvent e)
      {
          String prop = e.getPropertyName();

          if(prop == JFileChooser.CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY)
          {
              filters = (FileFilter[]) e.getNewValue();
              fireContentsChanged(this, -1, -1);
          }
      }

      public void setSelectedItem(Object filter)
      {
          if(filter != null)
          {
              getFileChooser().setFileFilter((FileFilter) filter);
              fireContentsChanged(this, -1, -1);
          }
      }

      public Object getSelectedItem()
      {
          // Ensure that the current filter is in the list.
          // NOTE: we shouldnt' have to do this, since JFileChooser adds
          // the filter to the choosable filters list when the filter
          // is set. Lets be paranoid just in case someone overrides
          // setFileFilter in JFileChooser.
          FileFilter currentFilter = getFileChooser().getFileFilter();
          boolean found = false;

          if(currentFilter != null)
          {
              for(int i=0; i < filters.length; i++)
              {
                  if(filters[i] == currentFilter)
                  {
                      found = true;
                  }
              }

              if(found == false)
              {
                  getFileChooser().addChoosableFileFilter(currentFilter);
              }
          }
          return getFileChooser().getFileFilter();
      }

      public int getSize()
      {
          if(filters != null)
          {
              return filters.length;
          }
          else
          {
              return 0;
          }
      }

      public Object getElementAt(int index)
      {
          if(index > getSize() - 1)
          {
              // This shouldn't happen. Try to recover gracefully.
              return getFileChooser().getFileFilter();
          }

          if(filters != null)
          {
              return filters[index];
          }
          else
          {
              return null;
          }
      }
  }

  public void valueChanged(ListSelectionEvent e)
  {
      File f = getFileChooser().getSelectedFile();

      if (!e.getValueIsAdjusting() && f != null && !getFileChooser().isTraversable(f))
      {
          setFileName(getFileChooser().getName(f));
      }
  }

  protected class DirectoryComboBoxAction extends AbstractAction
  {
    protected DirectoryComboBoxAction()
    {
      super("DirectoryComboBoxAction");
    }

    public void actionPerformed(ActionEvent e)
    {
      getFileChooser().setCurrentDirectory(((OyoahaDirectoryComboBoxNode)directoryComboBox.getSelectedItem()).getFile());
    }
  }

  protected JButton getApproveButton(JFileChooser fc)
  {
    return approveButton;
  }

  public Action getNewFolderAction()
  {
    if(newFolderAction==null)
    {
      newFolderAction = new OyoahaNewFolderAction();
    }

    return newFolderAction;
  }

  protected class OyoahaNewFolderAction extends AbstractAction
  {
    protected OyoahaNewFolderAction()
    {
      super("New Folder");
    }

    public void actionPerformed(ActionEvent e)
    {
      JFileChooser fc = getFileChooser();
      File currentDirectory = fc.getCurrentDirectory();
      File newFolder = null;

      list.clearSelection();

      try
      {
        newFolder = fc.getFileSystemView().createNewFolder(currentDirectory);
      }
      catch (IOException exc)
      {
        //JOptionPane.showMessageDialog(fc, newFolderErrorText + newFolderErrorSeparator + exc, newFolderErrorText, JOptionPane.ERROR_MESSAGE);
        return;
      }

      fc.rescanCurrentDirectory();
      fc.ensureFileIsVisible(newFolder);
    }
  }

  protected Action changeToParentDirectoryAction;

  public Action getChangeToParentDirectoryAction()
  {
    if(changeToParentDirectoryAction==null)
    {
      changeToParentDirectoryAction = new OyoahaChangeToParentDirectoryAction();
    }

    return changeToParentDirectoryAction;
  }

  protected MouseListener createDoubleClickListener(JFileChooser fc, JList list)
  {
    return new OyoahaDoubleClickListener(list);
  }

  public ListSelectionListener createListSelectionListener(JFileChooser fc)
  {
    return new OyoahaSelectionListener();
  }

  protected int lastRowPosition;

  protected class OyoahaChangeToParentDirectoryAction extends AbstractAction
  {
    protected OyoahaChangeToParentDirectoryAction()
    {
      super("Go Up");
    }
    public void actionPerformed(ActionEvent e)
    {
      getFileChooser().changeToParentDirectory();
      //list.ensureIndexIsVisible(lastRowPosition);
    }
  }

  protected class OyoahaDoubleClickListener implements MouseListener
  {
    protected JList list;

    public OyoahaDoubleClickListener(JList list)
    {
      this.list = list;
    }

    public void mouseEntered(MouseEvent e)
    {

    }

    public void mouseExited(MouseEvent e)
    {

    }

    public void mousePressed(MouseEvent e)
    {
      if(e.getClickCount()==2)
      {
        resetEditIndex();
        cancelEdit();
      }
    }

    public void mouseClicked(MouseEvent e)
    {

    }

    public void mouseReleased(MouseEvent e)
    {
      if(e.isPopupTrigger())
      {

      }
      /*else
      if(e.getModifiers()!=InputEvent.BUTTON1_MASK)
      {
        return;
      }*/
      else
      if(e.getClickCount()==2)
      {
        int index = list.locationToIndex(e.getPoint());

        if(index >= 0)
        {
          File f = (File) list.getModel().getElementAt(index);

          if(getFileChooser().isTraversable(f))
          {
            lastRowPosition = index;
            list.ensureIndexIsVisible(0);
            list.clearSelection();

            getFileChooser().setCurrentDirectory(f);
          }
          else
          {
            getFileChooser().approveSelection();
          }
        }
      }
      else
      if(e.getClickCount()==1)
      {
        int index = list.locationToIndex(e.getPoint());

        if(index >= 0 && lastIndex == index && editing == false)
        {
          Thread thread = new Thread(new OyoahaEditCell(index));
          thread.start();
        }
        else
        {
          if(index >= 0)
          {
            setEditIndex(index);
          }
          else
          {
            resetEditIndex();
          }

          cancelEdit();
        }
      }
      else
      {
        resetEditIndex();
        cancelEdit();
      }

      list.repaint();
    }
  }

  protected class OyoahaEditCell implements Runnable
  {
    protected int index;

    public OyoahaEditCell(int index)
    {
      this.index = index;
    }

    public void run()
    {
      try
      {
        Thread.sleep(100);

        if(lastIndex == index && editing == false)
        {
          editing = true;
          Rectangle r = list.getCellBounds(index, index);
          Rectangle rv = list.getVisibleRect();

          currentEditingIndex = index;

          JTextField editCell = getEditCell();
          editCell.setBounds(0,0,1,1);
          editCell.setVisible(true);
          File f = (File)list.getSelectedValue();
          editCell.setText(getFileChooser().getName(f));
          list.add(editCell);
          editCell.setBounds(editX + r.x, r.y, r.width-editX, r.height);
          editCell.selectAll();
        }
      }
      catch(Exception e)
      {

      }
    }
  }

  protected class OyoahaSelectionListener implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting())
      {
        JFileChooser chooser = getFileChooser();
        JList list = (JList)e.getSource();

        if(chooser.isMultiSelectionEnabled())
        {
          Object[] objects = list.getSelectedValues();

          if(objects != null)
          {
            File[] files = new File[objects.length];

            for(int i = 0; i < objects.length; i++)
            {
              files[i] = (File)objects[i];
            }
//System.out.println("chooser.setSelectedFiles " + files.length);
            chooser.setSelectedFiles(files);
          }
        }
        else
        {
          File file = (File)list.getSelectedValue();

          if(file != null)
          {
            chooser.setSelectedFile(file);
          }
        }
      }
    }
  }
  
  
  
  
  
/*  private Action approveSelectionAction = new OyoahaApproveSelectionAction();
  
  
  public Action getApproveSelectionAction() 
  {
System.out.println("getApproveSelectionAction " + approveSelectionAction);	
    return approveSelectionAction;
  }*/
  
  /**
   * Responds to an Open or Save request
   */
/*  protected class OyoahaApproveSelectionAction extends AbstractAction 
  {
      protected OyoahaApproveSelectionAction() 
      {
          super("approveSelection");
      }
      
      public void actionPerformed(ActionEvent e) 
      {
        if (isDirectorySelected()) 
        {
  	        File dir = getDirectory();
  	        if (dir != null) 
            {
  	            try 
                {
  		            // Strip trailing ".."
  		            dir = dir.getCanonicalFile();
  	            } 
                catch (IOException ex) 
                {
  		            // Ok, use f as is
  	            }
                
  	            getFileChooser().setCurrentDirectory(dir);
  	            return;
  	        }
        }

        JFileChooser chooser = getFileChooser();

        String filename = getFileName();
        FileSystemView fs = chooser.getFileSystemView();
        File dir = chooser.getCurrentDirectory();

        if (filename != null) 
        {
  	        // Remove whitespace from beginning and end of filename
  	        filename = filename.trim();
        }

        if (filename == null || filename.equals("")) 
        {
  	        // no file selected, multiple selection off, therefore cancel the approve action
//resetGlobFilter();
  	        return;
        }

        File selectedFile = null;
        File[] selectedFiles = null;

        if (filename != null && !filename.equals("")) 
        {
  	        // Unix: Resolve '~' to user's home directory
  	        if (File.separatorChar == '/') 
            {
  	            if (filename.startsWith("~/")) 
                {
  		            filename = System.getProperty("user.home") + filename.substring(1);
  	            } 
                else if (filename.equals("~")) 
                {
  		            filename = System.getProperty("user.home");
  	            }
  	        }

  	        if (chooser.isMultiSelectionEnabled() && filename.startsWith("\"")) 
            {
  	            ArrayList fList = new ArrayList();
  	            filename = filename.substring(1);
  	    
                if (filename.endsWith("\"")) 
                {
  		            filename = filename.substring(0, filename.length()-1);
  	            }
  	    
                do 
                {
  		            String str;
                    
  		            int i = filename.indexOf("\" \"");
  		            if (i > 0) 
                    {
  		                str = filename.substring(0, i);
  		                filename = filename.substring(i+3);
  		            } 
                    else 
                    {
  		                str = filename;
  		                filename = "";
  		            }
                    
  		            File file = fs.createFileObject(str);
  		
                    if (!file.isAbsolute()) 
                    {
  		                file = fs.getChild(dir, str);
  		            }
        
  		            fList.add(file);
                    
  	            } 
                while (filename.length() > 0);
                
  	            if (fList.size() > 0) 
                {
  		            selectedFiles = (File[])fList.toArray(new File[fList.size()]);
  	            }
                
//resetGlobFilter();
  	        } 
            else 
            {
                selectedFile = fs.createFileObject(filename);
  	    
                if(!selectedFile.isAbsolute()) 
                {
  	                selectedFile = fs.getChild(dir, filename);
  	            }
                
  	            // check for wildcard pattern
  	            /*FileFilter currentFilter = chooser.getFileFilter();
                
  	            if (!selectedFile.exists() && isGlobPattern(filename)) 
                {
  		            if (globFilter == null) 
                    {
  		                globFilter = new GlobFilter();
  		            }
            
  		            globFilter.setPattern(filename);
  		
                    if (!(currentFilter instanceof GlobFilter)) 
                    {
  		                actualFileFilter = currentFilter;
  		            }
            
  		            chooser.setFileFilter(null);
  		            chooser.setFileFilter(globFilter);
  		            return;
  	            }

  	            resetGlobFilter();*/
/*
  	            // Check for directory change action
  	            boolean isDir = (selectedFile != null && selectedFile.isDirectory());
  	            boolean isTrav = (selectedFile != null && chooser.isTraversable(selectedFile));
  	            boolean isDirSelEnabled = chooser.isDirectorySelectionEnabled();
  	            boolean isFileSelEnabled = chooser.isFileSelectionEnabled();

  	            if (isDir && isTrav && !isDirSelEnabled) 
                {
  		            chooser.setCurrentDirectory(selectedFile);
  		            return;
  	            } 
                else 
                if ((isDir || !isFileSelEnabled) && (!isDir || !isDirSelEnabled) && (!isDirSelEnabled || selectedFile.exists())) 
                {
  		            selectedFile = null;
  	            }
  	        }
        }
        
        if (selectedFiles != null || selectedFile != null) 
        {
  	        if (selectedFiles != null) 
            {
  	            chooser.setSelectedFiles(selectedFiles);
  	        } 
            else 
            if (chooser.isMultiSelectionEnabled()) 
            {
  	            chooser.setSelectedFiles(new File[] { selectedFile });
  	        } 
            else 
            {
  	            chooser.setSelectedFile(selectedFile);
  	        }
            
  	        chooser.approveSelection();
        } 
        else 
        {
  	        if (chooser.isMultiSelectionEnabled()) 
            { 
  	            chooser.setSelectedFiles(null);
  	        } 
            else
            {
  	            chooser.setSelectedFile(null);
  	        }
            
  	        chooser.cancelSelection();
        }
    }
  }

  /*private void resetGlobFilter() 
  {
      if (actualFileFilter != null) 
      {
          JFileChooser chooser = getFileChooser();
          FileFilter currentFilter = chooser.getFileFilter();
          if (currentFilter != null && currentFilter.equals(globFilter)) 
          {
  	        chooser.setFileFilter(actualFileFilter);
  	        chooser.removeChoosableFileFilter(globFilter);
          }
          actualFileFilter = null;
      }
  }

  private static boolean isGlobPattern(String filename) 
  {
    return ((File.separatorChar == '\\' && filename.indexOf('*') >= 0) || (File.separatorChar == '/' && (filename.indexOf('*') >= 0 || filename.indexOf('?') >= 0 || filename.indexOf('[') >= 0)));
  }*/

  /* A file filter which accepts file patterns containing
   * the special wildcard '*' on windows, plus '?', and '[ ]' on Unix.
   */
  /*class GlobFilter extends FileFilter 
  {
      Pattern pattern;
      String globPattern;

      public void setPattern(String globPattern) 
      {
          char[] gPat = globPattern.toCharArray();
          char[] rPat = new char[gPat.length * 2];
          boolean isWin32 = (File.separatorChar == '\\');
          boolean inBrackets = false;
          StringBuffer buf = new StringBuffer();
          int j = 0;

          this.globPattern = globPattern;

      if (isWin32) 
      {
  	// On windows, a pattern ending with *.* is equal to ending with *
  	int len = gPat.length;
  	if (globPattern.endsWith("*.*")) {
  	    len -= 2;
  	}
  	for (int i = 0; i < len; i++) {
  	    if (gPat[i] == '*') {
  		rPat[j++] = '.';
  	    }
  	    rPat[j++] = gPat[i];
  	}
      } else {
  	for (int i = 0; i < gPat.length; i++) {
  	    switch(gPat[i]) {
  	      case '*':
  		if (!inBrackets) {
  		    rPat[j++] = '.';
  		}
  		rPat[j++] = '*';
  		break;

  	      case '?':
  		rPat[j++] = inBrackets ? '?' : '.';
  		break;

  	      case '[':
  		inBrackets = true;
  		rPat[j++] = gPat[i];

  		if (i < gPat.length - 1) {
  		    switch (gPat[i+1]) {
  		      case '!':
  		      case '^':
  			rPat[j++] = '^';
  			i++;
  			break;

  		      case ']':
  			rPat[j++] = gPat[++i];
  			break;
  		    }
  		}
  		break;

  	      case ']':
  		rPat[j++] = gPat[i];
  		inBrackets = false;
  		break;

  	      case '\\':
  		if (i == 0 && gPat.length > 1 && gPat[1] == '~') {
  		    rPat[j++] = gPat[++i];
  		} else {
  		    rPat[j++] = '\\';
  		    if (i < gPat.length - 1 && "*?[]".indexOf(gPat[i+1]) >= 0) {
  			rPat[j++] = gPat[++i];
  		    } else {
  			rPat[j++] = '\\';
  		    }
  		}
  		break;

  	      default:
  		//if ("+()|^$.{}<>".indexOf(gPat[i]) >= 0) {
  		if (!Character.isLetterOrDigit(gPat[i])) 
        {
  		    rPat[j++] = '\\';
  		}
        
  		rPat[j++] = gPat[i];
  		break;
  	    }
  	}
      }
      
      this.pattern = Pattern.compile(new String(rPat, 0, j), Pattern.CASE_INSENSITIVE);
  }

      public boolean accept(File f) 
      {
          if (f == null) 
          {
  	        return false;
          }
          
          if (f.isDirectory()) 
          {
  	        return true;
          }
          
          return pattern.matcher(f.getName()).matches();
      }

      public String getDescription() 
      {
          return globPattern;
      }
  }*/
}