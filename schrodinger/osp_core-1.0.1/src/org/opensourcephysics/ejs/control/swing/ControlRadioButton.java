/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.ejs.control.swing;
import javax.swing.JRadioButton;
import org.opensourcephysics.ejs.control.ControlElement;
import org.opensourcephysics.ejs.control.value.BooleanValue;
import org.opensourcephysics.ejs.control.value.Value;

/**
 * A configurable checkbox. It will trigger an action when the
 * state changes. It has a boolean internal value, which is
 * returned as a Boolean object.
 */
public class ControlRadioButton extends ControlSwingElement {
  static final int VARIABLE = 4;
  protected JRadioButton radioButton;
  private ControlRadioButton mySelf = this;
  private ControlContainer parent;
  private BooleanValue internalValue;
  private boolean defaultState, defaultStateSet;
  private String imageFile = null, selectedimageFile = null;

  // ------------------------------------------------
  // Visual component
  // ------------------------------------------------
  public ControlRadioButton(Object _visual) {
    super(_visual);
  }

  protected java.awt.Component createVisual(Object _visual) {
    if(_visual instanceof JRadioButton) {
      radioButton = (JRadioButton) _visual;
    } else {
      radioButton = new JRadioButton();
    }
    internalValue = new BooleanValue(radioButton.isSelected());
    defaultStateSet = false;
    parent = null;
    radioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent _e) {
        setInternalValue(radioButton.isSelected());
      }
    });
    return radioButton;
  }

  public void reset() {
    if(defaultStateSet) {
      radioButton.setSelected(defaultState);
      setInternalValue(defaultState);
    }
  }

  private void setInternalValue(boolean _state) {
    internalValue.value = _state;
    if(parent!=null) {
      parent.informRadioGroup(mySelf, _state);
    }
    variableChanged(VARIABLE, internalValue);
    invokeActions();
    if(internalValue.value) {
      invokeActions(ControlSwingElement.ACTION_ON);
    } else {
      invokeActions(ControlSwingElement.ACTION_OFF);
    }
  }

  public void setParent(ControlContainer _aParent) {
    parent = _aParent;
  }

  void reportChanges() {
    variableChangedDoNotUpdate(VARIABLE, internalValue);
  }

  // ------------------------------------------------
  // Properties
  // ------------------------------------------------
  static private java.util.ArrayList infoList = null;

  public java.util.ArrayList getPropertyList() {
    if(infoList==null) {
      infoList = new java.util.ArrayList();
      infoList.add("text");
      infoList.add("image");
      infoList.add("selectedimage");
      infoList.add("alignment");
      infoList.add("variable");
      infoList.add("selected");
      infoList.add("action");
      infoList.add("actionon");
      infoList.add("actionoff");
      infoList.addAll(super.getPropertyList());
    }
    return infoList;
  }

  public String getPropertyInfo(String _property) {
    if(_property.equals("text")) {
      return "String NotTrimmed";
    }
    if(_property.equals("image")) {
      return "File|String";
    }
    if(_property.equals("selectedimage")) {
      return "File|String";
    }
    if(_property.equals("alignment")) {
      return "Alignment|int";
    }
    if(_property.equals("variable")) {
      return "boolean";
    }
    if(_property.equals("selected")) {
      return "boolean CONSTANT POSTPROCESS";
    }
    if(_property.equals("action")) {
      return "Action CONSTANT";
    }
    if(_property.equals("actionon")) {
      return "Action CONSTANT";
    }
    if(_property.equals("actionoff")) {
      return "Action CONSTANT";
    }
    if(_property.equals("enabled")) {
      return "boolean BASIC"; // Not hidden
    }
    return super.getPropertyInfo(_property);
  }

  // ------------------------------------------------
  // Set and Get the values of the properties
  // ------------------------------------------------
  public void setValue(int _index, Value _value) {
    switch(_index) {
    case 0 :
      radioButton.setText(_value.getString());
      break;    // text
    case 1 :    // image
      if(_value.getString().equals(imageFile)) {
        return; // no need to do it again
      }
      radioButton.setIcon(getIcon(imageFile = _value.getString()));
      break;
    case 2 :    // selectedImage
      if(_value.getString().equals(selectedimageFile)) {
        return; // no need to do it again
      }
      radioButton.setSelectedIcon(getIcon(selectedimageFile = _value.getString()));
      break;
    case 3 :
      radioButton.setHorizontalAlignment(_value.getInteger());
      break; // alignment
    case VARIABLE :
      radioButton.setSelected(internalValue.value = _value.getBoolean());
      break;
    case 5 :
      defaultStateSet = true;
      defaultState = _value.getBoolean();
      setActive(false);
      reset();
      setActive(true);
      break;
    case 6 : // action
      removeAction(ControlElement.ACTION, getProperty("action"));
      addAction(ControlElement.ACTION, _value.getString());
      break;
    case 7 : // actionon
      removeAction(ControlSwingElement.ACTION_ON, getProperty("actionon"));
      addAction(ControlSwingElement.ACTION_ON, _value.getString());
      break;
    case 8 : // actionoff
      removeAction(ControlSwingElement.ACTION_OFF, getProperty("actionoff"));
      addAction(ControlSwingElement.ACTION_OFF, _value.getString());
      break;
    default :
      super.setValue(_index-9, _value);
      break;
    }
  }

  public void setDefaultValue(int _index) {
    switch(_index) {
    case 0 :
      radioButton.setText("");
      break;
    case 1 :
      radioButton.setIcon(null);
      imageFile = null;
      break;
    case 2 :
      radioButton.setIcon(null);
      selectedimageFile = null;
      break;
    case 3 :
      radioButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      break;
    case VARIABLE :
      break; // Do nothing
    case 5 :
      defaultStateSet = false;
      break;
    case 6 :
      removeAction(ControlElement.ACTION, getProperty("action"));
      break;
    case 7 :
      removeAction(ControlSwingElement.ACTION_ON, getProperty("actionon"));
      break;
    case 8 :
      removeAction(ControlSwingElement.ACTION_OFF, getProperty("actionoff"));
      break;
    default :
      super.setDefaultValue(_index-9);
      break;
    }
  }

  public Value getValue(int _index) {
    switch(_index) {
    case VARIABLE :
      return internalValue;
    case 0 :
    case 1 :
    case 2 :
    case 3 :
    case 5 :
    case 6 :
    case 7 :
    case 8 :
      return null;
    default :
      return super.getValue(_index-9);
    }
  }
} // End of class

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
