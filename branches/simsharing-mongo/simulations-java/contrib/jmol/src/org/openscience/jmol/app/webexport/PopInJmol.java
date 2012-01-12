/* $RCSfile$
 * $Author jonathan gutow$
 * $Date Aug 5, 2007 9:19:06 AM $
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
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
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */
package org.openscience.jmol.app.webexport;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jmol.api.JmolViewer;
import org.jmol.i18n.GT;
import org.jmol.util.TextFormat;

class PopInJmol extends WebPanel implements ChangeListener {

  PopInJmol(JmolViewer viewer, JFileChooser fc, WebPanel[] webPanels,
      int panelIndex) {
    super(viewer, fc, webPanels, panelIndex);
    panelName = "pop_in";
    listLabel = GT._("These names will be used as filenames for the applets");
    // description = "Create a web page with images that convert to live Jmol
    // applets when a user clicks a link";
  }

  @Override
  JPanel appletParamPanel() {
    // Create the appletSize spinner so the user can decide how big
    // the applet should be.
    SpinnerNumberModel appletSizeModelW = new SpinnerNumberModel(WebExport
        .getPopInWidth(), // initial value
        50, // min
        1000, // max
        25); // step size
    SpinnerNumberModel appletSizeModelH = new SpinnerNumberModel(WebExport
        .getPopInHeight(), // initial value
        50, // min
        1000, // max
        25); // step size
    appletSizeSpinnerW = new JSpinner(appletSizeModelW);
    appletSizeSpinnerW.addChangeListener(this);
    appletSizeSpinnerH = new JSpinner(appletSizeModelH);
    appletSizeSpinnerH.addChangeListener(this);

    // panel to hold spinner and label
    JPanel appletSizeWHPanel = new JPanel();
    appletSizeWHPanel.add(new JLabel(GT._("Applet width:")));
    appletSizeWHPanel.add(appletSizeSpinnerW);
    appletSizeWHPanel.add(new JLabel(GT._("height:")));
    appletSizeWHPanel.add(appletSizeSpinnerH);
    return (appletSizeWHPanel);
  }

  @Override
  String fixHtml(String html) {
    return html;
  }

  @Override
  String getAppletDefs(int i, String html, StringBuffer appletDefs,
                       JmolInstance instance) {
    String divClass = (i % 2 == 0 ? "floatRightDiv" : "floatLeftDiv");
    String name = instance.name;
    String javaname = instance.javaname;
    int JmolSizeW = instance.width;
    int JmolSizeH = instance.height;
    String widgetDefs = "";
    if (!instance.whichWidgets.isEmpty()) {
      widgetDefs += "<div id=\"JmolCntl" + i
          + "\" style=\"display:none;\">";
      for (int j = 0; j < nWidgets; j++) {
        if (instance.whichWidgets.get(j)) {
          widgetDefs += "\n<div>" 
              + theWidgets.widgetList[j].getJavaScript(i, instance) 
              + "</div>\n"; //each widget in one line
        }
      }
      widgetDefs += "</div>";
   }
    if (useAppletJS) {
      appletInfoDivs += "\n<div id=\"" + javaname + "_caption\">\n"
          + GT.escapeHTML(GT._("insert a caption for {0} here.", name))
          + "\n</div>";
      appletInfoDivs += "\n<div id=\"" + javaname + "_note\">\n"
          + GT.escapeHTML(GT._("insert a note for {0} here.", name))
          + "\n</div>";
      //TODO fix pure javascript to work with widgets...
      appletDefs.append("\naddJmolDiv(" + i + ",'" + divClass + "','"
          + javaname + "'," + JmolSizeW + "," + JmolSizeH + ")");
    } else {
      String s = htmlAppletTemplate;
      s = TextFormat.simpleReplace(s, "@CLASS@", "" + divClass);
      s = TextFormat.simpleReplace(s, "@I@", "" + i);
      s = TextFormat.simpleReplace(s, "@WIDTH@", "" + JmolSizeW);
      s = TextFormat.simpleReplace(s, "@HEIGHT@", "" + JmolSizeH);
      s = TextFormat.simpleReplace(s, "@NAME@", GT.escapeHTML(name));
      s = TextFormat.simpleReplace(s, "@APPLETNAME@", GT.escapeHTML(javaname));
      s = TextFormat.simpleReplace(s, "@LEFTWIDGETS@", "");// no left widgets
                                                           // for now
      s = TextFormat.simpleReplace(s, "@RIGHTWIDGETS@", widgetDefs);
      appletDefs.append(s);
    }
    return html;
  }

  public void stateChanged(ChangeEvent e) {
    if (e.getSource() == appletSizeSpinnerW
        || e.getSource() == appletSizeSpinnerH) {
      int width = ((SpinnerNumberModel) (appletSizeSpinnerW.getModel()))
          .getNumber().intValue();
      int height = ((SpinnerNumberModel) (appletSizeSpinnerH.getModel()))
          .getNumber().intValue();
      WebExport.setPopInDim(width, height);
      JList whichList = getInstanceList();
      int[] list = whichList.getSelectedIndices();
      if (list.length != 1)// may want to make this work on multiple selections
        return;
      int index = whichList.getSelectedIndex();
      JmolInstance instance = (JmolInstance) whichList.getModel().getElementAt(
          index);
      instance.width = width;
      instance.height = height;
      viewer.createImage(instance.pictFile, "PNG", null, 2, width, height);
      return;
    }

    if (e.getSource() == appletSizeSpinnerP) {
      int percent = ((SpinnerNumberModel) (appletSizeSpinnerP.getModel()))
          .getNumber().intValue();
      WebExport.setScriptButtonPercent(percent);
      return;
    }
  }
}
