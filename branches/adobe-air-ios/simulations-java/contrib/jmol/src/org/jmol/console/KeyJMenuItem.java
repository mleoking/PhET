/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-25 02:42:30 -0500 (Thu, 25 Jun 2009) $
 * $Revision: 11113 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net, www.jmol.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.console;


import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

public class KeyJMenuItem extends JMenuItem implements GetKey {

  private String key;
  public String getKey() {
    return key;
  }
  
  public static String getKey(Object obj) {
    return (((GetKey)obj).getKey());
  }
  
  public KeyJMenuItem(String key, String label, Map<String, AbstractButton> menuMap) {
    super(getLabelWithoutMnemonic(label));
    this.key = key;
    map(this, key, label, menuMap);
  }
  
  public static void setAbstractButtonLabels(Map<String, AbstractButton> menuMap,
                               Map<String, String> labels) {
    Iterator<String> e = menuMap.keySet().iterator();
    while (e.hasNext()) {
      String key = e.next();
      AbstractButton m = menuMap.get(key);
      String label = labels.get(key);
      if (key.indexOf("Tip") == key.length() - 3) {
        m.setToolTipText(labels.get(key));
      } else {
        char mnemonic = KeyJMenuItem.getMnemonic(label);
        if (mnemonic != ' ')
          m.setMnemonic(mnemonic);
        label = KeyJMenuItem.getLabelWithoutMnemonic(label);
        m.setText(label);
      }
    }
  }
  
  static String getLabelWithoutMnemonic(String label) {
    if (label == null) {
      return null;
    }
    int index = label.indexOf('&');
    if (index == -1) {
      return label;
    }
    return label.substring(0, index) +
      ((index < label.length() - 1) ? label.substring(index + 1) : "");
  }
  
  static char getMnemonic(String label) {
    if (label == null) {
      return ' ';
    }
    int index = label.indexOf('&');
    if ((index == -1) || (index == label.length() - 1)){
      return ' ';
    }
    return label.charAt(index + 1);
  }

  static void map(AbstractButton button, String key, String label,
                         Map<String, AbstractButton> menuMap) {
    char mnemonic = KeyJMenuItem.getMnemonic(label);
    if (mnemonic != ' ')
      button.setMnemonic(mnemonic);
    menuMap.put(key, button);
  }

}


