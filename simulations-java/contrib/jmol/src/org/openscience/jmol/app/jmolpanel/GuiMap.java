/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-23 22:23:54 -0700 (Wed, 23 Mar 2011) $
 * $Revision: 15337 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jmol.app.jmolpanel;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBox;

import org.jmol.console.KeyJMenu;
import org.jmol.console.KeyJMenuItem;
import org.jmol.console.KeyJCheckBox;
import org.jmol.console.KeyJCheckBoxMenuItem;
import org.jmol.console.KeyJRadioButtonMenuItem;
import org.jmol.i18n.GT;

class GuiMap {

  Map<String, AbstractButton> map = new Hashtable<String, AbstractButton>();
  
  Map<String, String> labels;
  
  // keys here refer to keys listed in org.openscience.jmol.Properties.Jmol-resources.properties
  // actions are either defined there, as xxxScript=, or by 
  // Actions created in DisplayPanel.java
  
  private void setupLabels() {
    labels = new Hashtable<String, String>();
    labels.put("macros", GT._("&Macros"));
    labels.put("file", GT._("&File"));
    labels.put("newwin", GT._("&New"));
    labels.put("open", GT._("&Open"));
    labels.put("openTip", GT._("Open a file."));
    labels.put("openurl", GT._("Open &URL"));
    labels.put("openpdb", GT._("&Get PDB"));
    labels.put("openmol", GT._("Get &MOL"));
    labels.put("reloadScript", GT._("&Reload"));
    labels.put("editor", GT._("Scrip&t Editor..."));  // new %t 11.7.45
    labels.put("console", GT._("Conso&le..."));
    labels.put("jconsole", GT._("Jmol Java &Console"));
    labels.put("atomsetchooser", GT._("AtomSet&Chooser..."));
    labels.put("saveas", GT._("&Save As..."));
    labels.put("exportMenu", GT._("&Export"));
    labels.put("export", GT._("Export &Image..."));
    labels.put("exportTip", GT._("Save current view as an image."));
    labels.put("toweb", GT._("Export to &Web Page..."));
    labels.put("towebTip", GT._("Export one or more views to a web page."));
    labels.put("povray", GT._("Render in POV-&Ray..."));
    labels.put("povrayTip", GT._("Render in POV-Ray"));
    labels.put("write", GT._("Write &State..."));
    labels.put("writeTip", GT._("Save current view as a Jmol state script."));
    labels.put("print", GT._("&Print..."));
    labels.put("printTip", GT._("Print view."));
    labels.put("close", GT._("&Close"));
    labels.put("exit", GT._("E&xit"));
    labels.put("recentFiles", GT._("Recent &Files..."));
    labels.put("edit", GT._("&Edit"));
    // labels.put("makecrystal", GT. _("Make crystal..."));
    labels.put("selectall", GT._("Select &All"));
    labels.put("deselectall", GT._("Deselect All"));
    labels.put("copyImage", GT._("Copy &Image"));
    labels.put("copyScript", GT._("Copy Script"));
    labels.put("prefs", GT._("Pr&eferences..."));
    labels.put("pasteClipboard", GT._("&Paste"));
    labels.put("editSelectAllScript", GT._("Select &All"));
    labels.put("selectMenu", GT._("&Select"));
    labels.put("selectMenuText", GT._("&Select"));
    labels.put("selectAllScript", GT._("&All"));
    labels.put("selectNoneScript", GT._("&None"));
    labels.put("selectHydrogenScript", GT._("Hydrogen"));
    labels.put("selectCarbonScript", GT._("Carbon"));
    labels.put("selectNitrogenScript", GT._("Nitrogen"));
    labels.put("selectOxygenScript", GT._("Oxygen"));
    labels.put("selectPhosphorusScript", GT._("Phosphorus"));
    labels.put("selectSulfurScript", GT._("Sulfur"));
    labels.put("selectAminoScript", GT._("Amino"));
    labels.put("selectNucleicScript", GT._("Nucleic"));
    labels.put("selectWaterScript", GT._("Water"));
    labels.put("selectHeteroScript", GT._("Hetero"));
    labels.put("display", GT._("&Display"));
    labels.put("atomMenu", GT._("&Atom"));
    labels.put("atomNoneScript", GT._("&None"));
    labels.put("atom15Script", GT._("{0}% van der Waals", "15"));
    labels.put("atom20Script", GT._("{0}% van der Waals", "20"));
    labels.put("atom25Script", GT._("{0}% van der Waals", "25"));
    labels.put("atom100Script", GT._("{0}% van der Waals", "100"));
    labels.put("bondMenu", GT._("&Bond"));
    labels.put("bondNoneScript", GT._("&None"));
    labels.put("bondWireframeScript", GT._("&Wireframe"));
    labels.put("bond100Script", GT._("{0} \u00C5", "0.10"));
    labels.put("bond150Script", GT._("{0} \u00C5", "0.15"));
    labels.put("bond200Script", GT._("{0} \u00C5", "0.20"));
    labels.put("labelMenu", GT._("&Label"));
    labels.put("labelNoneScript", GT._("&None"));
    labels.put("labelSymbolScript", GT._("&Symbol"));
    labels.put("labelNameScript", GT._("&Name"));
    labels.put("labelNumberScript", GT._("&Number"));
    labels.put("labelCenteredScript", GT._("&Centered"));
    labels.put("labelUpperRightScript", GT._("&Upper right"));
    labels.put("vectorMenu", GT._("&Vector"));
    labels.put("vectorOffScript", GT._("&None"));
    labels.put("vectorOnScript", GT._("&On"));
    labels.put("vector3Script", GT._("{0} pixels", "3"));
    labels.put("vector005Script", GT._("{0} \u00C5", "0.05"));
    labels.put("vector01Script", GT._("{0} \u00C5", "0.1"));
    labels.put("vectorScale02Script", GT._("Scale {0}", "0.2"));
    labels.put("vectorScale05Script", GT._("Scale {0}", "0.5"));
    labels.put("vectorScale1Script", GT._("Scale {0}", "1"));
    labels.put("vectorScale2Script", GT._("Scale {0}", "2"));
    labels.put("vectorScale5Script", GT._("Scale {0}", "5"));
    labels.put("zoomMenu", GT._("&Zoom"));
    labels.put("zoom100Script", GT._("{0}%", "100"));
    labels.put("zoom150Script", GT._("{0}%", "150"));
    labels.put("zoom200Script", GT._("{0}%", "200"));
    labels.put("zoom400Script", GT._("{0}%", "400"));
    labels.put("zoom800Script", GT._("{0}%", "800"));
    labels.put("perspectiveCheck", GT._("&Perspective Depth"));
    labels.put("axesCheck", GT._("A&xes"));
    labels.put("boundboxCheck", GT._("B&ounding Box"));
    labels.put("hydrogensCheck", GT._("&Hydrogens"));
    labels.put("vectorsCheck", GT._("V&ectors"));
    labels.put("measurementsCheck", GT._("&Measurements"));
    labels.put("resize", GT._("Resi&ze"));
    labels.put("view", GT._("&View"));
    labels.put("front", GT._("&Front"));
    labels.put("top", GT._("&Top"));
    labels.put("bottom", GT._("&Bottom"));
    labels.put("right", GT._("&Right"));
    labels.put("left", GT._("&Left"));
    labels.put("transform", GT._("Tr&ansform..."));
    labels.put("definecenter", GT._("Define &Center"));
    labels.put("tools", GT._("&Tools"));
    labels.put("gauss", GT._("&Gaussian..."));
    labels.put("viewMeasurementTable", GT._("&Measurements") + "...");
    labels.put("distanceUnitsMenu", GT._("Distance &Units"));
    labels.put("distanceNanometersScript", GT._("&Nanometers 1E-9"));
    labels.put("distanceAngstromsScript", GT._("&Angstroms 1E-10"));
    labels.put("distancePicometersScript", GT._("&Picometers 1E-12"));
    labels.put("animateMenu", GT._("&Animate..."));
    labels.put("vibrateMenu", GT._("&Vibrate..."));
    labels.put("graph", GT._("&Graph..."));
    labels.put("chemicalShifts", GT._("Calculate chemical &shifts..."));
    labels.put("crystprop", GT._("&Crystal Properties"));
    labels.put("animateOnceScript", GT._("&Once"));
    labels.put("animateLoopScript", GT._("&Loop"));
    labels.put("animatePalindromeScript", GT._("P&alindrome"));
    labels.put("animateStopScript", GT._("&Stop animation"));
    labels.put("animateRewindScript", GT._("&Rewind to first frame"));
    labels.put("animateRewindScriptTip", GT._("Rewind to first frame"));
    labels.put("animateNextScript", GT._("Go to &next frame"));
    labels.put("animateNextScriptTip", GT._("Go to next frame"));
    labels.put("animatePrevScript", GT._("Go to &previous frame"));
    labels.put("animatePrevScriptTip", GT._("Go to previous frame"));
    labels.put("animateAllScript", GT._("All &frames"));
    labels.put("animateAllScriptTip", GT._("All frames"));
    labels.put("animateLastScript", GT._("Go to &last frame"));
    labels.put("animateLastScriptTip", GT._("Go to last frame"));
    labels.put("vibrateStartScript", GT._("Start &vibration"));
    labels.put("vibrateStopScript", GT._("&Stop vibration"));
    labels.put("vibrateRewindScript", GT._("&First frequency"));
    labels.put("vibrateNextScript", GT._("&Next frequency"));
    labels.put("vibratePrevScript", GT._("&Previous frequency"));
    labels.put("help", GT._("&Help"));
    labels.put("about", GT._("About Jmol"));
    labels.put("uguide", GT._("User Guide"));
    labels.put("whatsnew", GT._("What's New"));
    labels.put("Prefs.showHydrogens", GT._("Hydrogens"));
    labels.put("Prefs.showMeasurements", GT._("Measurements"));
    labels.put("Prefs.perspectiveDepth", GT._("Perspective Depth"));
    labels.put("Prefs.showAxes", GT._("Axes"));
    labels.put("Prefs.showBoundingBox", GT._("Bounding Box"));
    labels.put("Prefs.axesOrientationRasmol", GT
        ._("RasMol/Chime compatible axes orientation/rotations"));
    labels.put("Prefs.openFilePreview", GT
        ._("File Preview (requires restarting Jmol)"));
    labels.put("Prefs.clearHistory", GT
        ._("Clear history (requires restarting Jmol)"));
    labels.put("Prefs.isLabelAtomColor", GT._("Use Atom Color"));
    labels.put("Prefs.isBondAtomColor", GT._("Use Atom Color"));
    labels.put("rotateScriptTip", GT._("Rotate molecule."));
    labels.put("pickScriptTip", GT
        ._("Select a set of atoms using SHIFT-LEFT-DRAG."));
    labels.put("pickMeasureScriptTip", GT
        ._("Click atoms to measure distances"));
    labels.put("pickCenterScriptTip", GT
        ._("Click an atom to center on it"));
    labels.put("homeTip", GT._("Return molecule to home position."));
    labels.put("modelkitScriptTip", GT._("Open the model kit."));
    labels.put("JavaConsole.clear", GT._("Clear"));
  }

  String getLabel(String key) {
    if (labels == null)
      setupLabels();
    String label = labels.get(key);
    return label;
  }

  JMenu newJMenu(String key) {
    return new KeyJMenu(key, getLabel(key), map);
  }
  
  JMenuItem newJMenuItem(String key) {
    return new KeyJMenuItem(key, getLabel(key), map);
  }
  JCheckBoxMenuItem newJCheckBoxMenuItem(String key, boolean isChecked) {
    return new KeyJCheckBoxMenuItem(key, getLabel(key), map, isChecked);
  }
  JRadioButtonMenuItem newJRadioButtonMenuItem(String key) {
    return new KeyJRadioButtonMenuItem(key, getLabel(key), map);
  }
  JCheckBox newJCheckBox(String key, boolean isChecked) {
    return new KeyJCheckBox(key, getLabel(key), map, isChecked);
  }
  JButton newJButton(String key) {
    JButton jb = new JButton(getLabel(key));
    map.put(key, jb);
    return jb;
  }

  Object get(String key) {
    return map.get(key);
  }

  void setSelected(String key, boolean b) {
    ((AbstractButton)get(key)).setSelected(b);
  }

  public void updateLabels() {
    boolean doTranslate = GT.getDoTranslate();
    GT.setDoTranslate(true);
    setupLabels();
    KeyJMenuItem.setAbstractButtonLabels(map, labels);
    GT.setDoTranslate(doTranslate);
  }


}

