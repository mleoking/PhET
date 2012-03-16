/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-12-17 14:53:30 -0800 (Fri, 17 Dec 2010) $
 * $Revision: 14814 $
 *
 * Copyright (C) 2000-2005  The Jmol Development Team
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
package org.jmol.popup;

import org.jmol.api.*;
import org.jmol.i18n.GT;
import org.jmol.util.Elements;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

import java.awt.Component;
import java.awt.Container;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class JmolPopup extends SimplePopup {
  
  private int aboutComputedMenuBaseCount;

  private static String strMenuStructure;
  
  private JMenu mainMenu;
  

  final private static int MENUITEM_HEIGHT = 20;
  final private static int MAX_ITEMS = 25;
  final private static int TITLE_MAX_WIDTH = 20;

  static public JmolPopup newJmolPopup(JmolViewer viewer, boolean doTranslate,
                                       String menu, boolean asPopup) {
    strMenuStructure = menu;
    GT.setDoTranslate(true);
    JmolPopup popup;
    try {
      popup = new JmolPopup(viewer, asPopup);
    } catch (Exception e) {
      Logger.error("JmolPopup not loaded");
      popup = null;
    }
    if (popup != null)
      try {
        popup.updateComputedMenus();
      } catch (NullPointerException e) {
        // ignore -- the frame just wasn't ready yet;
        // updateComputedMenus() will be called again when the frame is ready; 
      }
    GT.setDoTranslate(doTranslate);
    return popup;
  }

  private JmolPopup(JmolViewer viewer, boolean asPopup) {
    super(viewer);
    this.asPopup = asPopup;
    PopupResource bundle = new MainPopupResourceBundle(strMenuStructure, menuText);
    String title = "popupMenu";
    if (asPopup) {
      swingPopup = new JPopupMenu("Jmol");
      build(title, swingPopup, bundle);
    } else {
      mainMenu = new JMenu("Jmol");
      build(title, mainMenu, bundle);
    }
  }

  public Container getJMenu() {
    return swingPopup;
  }


  public String getMenu(String title) {
    int pt = title.indexOf("|"); 
    if (pt >= 0) {
      String type = title.substring(pt);
      title = title.substring(0, pt);
      if (type.indexOf("current") >= 0) {
        return getMenuCurrent();
      }
    }
    return (new MainPopupResourceBundle(strMenuStructure, null)).getMenu(title);
  }
  
  @Override
  protected void checkSpecialMenu(String item, Object subMenu, String word) {
    // these will need tweaking:
    if ("aboutComputedMenu".equals(item)) {
      aboutComputedMenuBaseCount = getMenuItemCount(subMenu);
    } else if ("modelSetMenu".equals(item)) {
      nullModelSetName = word;
      enableMenu(subMenu, false);
    }
  }

  @SuppressWarnings("unchecked")
  public void updateComputedMenus() {
    updateMode = UPDATE_ALL;
    getViewerData();
    //System.out.println("jmolPopup updateComputedMenus " + modelSetFileName + " " + modelSetName + " " + atomCount);
    updateSelectMenu();
    updateFileMenu();
    updateElementsComputedMenu(viewer.getElementsPresentBitSet(modelIndex));
    updateHeteroComputedMenu(viewer.getHeteroList(modelIndex));
    updateSurfMoComputedMenu((Map<String, Object>) modelInfo.get("moData"));
    updateFileTypeDependentMenus();
    updatePDBComputedMenus();
    updateMode = UPDATE_CONFIG;
    updateConfigurationComputedMenu();
    updateSYMMETRYComputedMenus();
    updateFRAMESbyModelComputedMenu();
    updateModelSetComputedMenu();
    updateLanguageSubmenu();
    updateAboutSubmenu();
  }

  private void updateFileMenu() {
    Object menu = htMenus.get("fileMenu");
    if (menu == null)
      return;
    String text = getMenuText("writeFileTextVARIABLE");
    menu = htMenus.get("writeFileTextVARIABLE");
    if (modelSetFileName.equals("zapped") || modelSetFileName.equals("")) {
      setLabel(menu, GT._("No atoms loaded"));
      enableMenuItem(menu, false);
    } else {
      setLabel(menu, GT._(text, modelSetFileName, true));
      enableMenuItem(menu, true);
    }
  }

  void updateSelectMenu() {
    Object menu = htMenus.get("selectMenuText");
    if (menu == null)
      return;
    enableMenu(menu, atomCount != 0);
    setLabel(menu, GT._(getMenuText("selectMenuText"), viewer.getSelectionCount(), true));
  }

  void updateElementsComputedMenu(BitSet elementsPresentBitSet) {
    Object menu = htMenus.get("elementsComputedMenu");
    if (menu == null)
      return;
    removeAll(menu);
    enableMenu(menu, false);
    if (elementsPresentBitSet == null)
      return;
    for (int i = elementsPresentBitSet.nextSetBit(0); i >= 0; i = elementsPresentBitSet
        .nextSetBit(i + 1)) {
      String elementName = Elements.elementNameFromNumber(i);
      String elementSymbol = Elements.elementSymbolFromNumber(i);
      String entryName = elementSymbol + " - " + elementName;
      addMenuItem(menu, entryName, "SELECT " + elementName, null);
    }
    for (int i = JmolConstants.firstIsotope; i < Elements.altElementMax; ++i) {
      int n = Elements.elementNumberMax + i;
      if (elementsPresentBitSet.get(n)) {
        n = Elements.altElementNumberFromIndex(i);
        String elementName = Elements.elementNameFromNumber(n);
        String elementSymbol = Elements.elementSymbolFromNumber(n);
        String entryName = elementSymbol + " - " + elementName;
        addMenuItem(menu, entryName, "SELECT " + elementName, null);
      }
    }
    enableMenu(menu, true);
  }

  void updateHeteroComputedMenu(Map<String, String> htHetero) {
    Object menu = htMenus.get("PDBheteroComputedMenu");
    if (menu == null)
      return;
    removeAll(menu);
    enableMenu(menu, false);
    if (htHetero == null)
      return;
    int n = 0;
    for (Map.Entry<String, String> hetero : htHetero.entrySet()) {
      String heteroCode = hetero.getKey();
      String heteroName = hetero.getValue();
      if (heteroName.length() > 20)
        heteroName = heteroName.substring(0, 20) + "...";
      String entryName = heteroCode + " - " + heteroName;
      addMenuItem(menu, entryName, "SELECT [" + heteroCode + "]", null);
      n++;
    }
    enableMenu(menu, (n > 0));
  }

  @SuppressWarnings("unchecked")
  void updateSurfMoComputedMenu(Map<String, Object> moData) {
    Object menu = htMenus.get("surfMoComputedMenuText");
    if (menu == null)
      return;
    removeAll(menu);
    List<Map<String, Object>> mos = (moData == null ? null : (List<Map<String, Object>>) (moData.get("mos")));
    int nOrb = (mos == null ? 0 : mos.size());
    String text = getMenuText("surfMoComputedMenuText");
    if (nOrb == 0) {
      setLabel(menu, GT._(text, ""));
      enableMenu(menu, false);
      return;
    }
    setLabel(menu, GT._(text, nOrb));
    enableMenu(menu, true);
    Object subMenu = menu;
    int nmod = (nOrb % MAX_ITEMS);
    if (nmod == 0)
      nmod = MAX_ITEMS;
    int pt = (nOrb > MAX_ITEMS ? 0 : Integer.MIN_VALUE);
    for (int i = nOrb; --i >= 0;) {
      if (pt >= 0 && (pt++ % nmod) == 0) {
        if (pt == nmod + 1)
          nmod = MAX_ITEMS;
        String id = "mo" + pt + "Menu";
        subMenu = newMenu(Math.max(i + 2 - nmod, 1) + "..." + (i + 1),
            getId(menu) + "." + id);
        addMenuSubMenu(menu, subMenu);
        htMenus.put(id, subMenu);
        pt = 1;
      }
      Map<String, Object> mo = mos.get(i);
      String entryName = "#" + (i + 1) + " " 
          + (mo.containsKey("type") ? (String)mo.get("type") + " " : "")
          + (mo.containsKey("symmetry") ? (String)mo.get("symmetry") + " ": "")
          + (mo.containsKey("energy") ? mo.get("energy") : "") ;
      String script = "mo " + (i + 1);
      addMenuItem(subMenu, entryName, script, null);
    }
  }

  void updatePDBComputedMenus() {

    Object menu = htMenus.get("PDBaaResiduesComputedMenu");
    if (menu == null)
      return;
    removeAll(menu);
    enableMenu(menu, false);

    Object menu1 = htMenus.get("PDBnucleicResiduesComputedMenu");
    if (menu1 == null)
      return;
    removeAll(menu1);
    enableMenu(menu1, false);

    Object menu2 = htMenus.get("PDBcarboResiduesComputedMenu");
    if (menu2 == null)
      return;
    removeAll(menu2);
    enableMenu(menu2, false);
    if (modelSetInfo == null)
      return;
    int n = (modelIndex < 0 ? 0 : modelIndex + 1);
    String[] lists = ((String[]) modelSetInfo.get("group3Lists"));
    group3List = (lists == null ? null : lists[n]);
    group3Counts = (lists == null ? null : ((int[][]) modelSetInfo
        .get("group3Counts"))[n]);

    if (group3List == null)
      return;
    //next is correct as "<=" because it includes "UNK"
    int nItems = 0;
    for (int i = 1; i < JmolConstants.GROUPID_AMINO_MAX; ++i)
      nItems += updateGroup3List(menu, JmolConstants.predefinedGroup3Names[i]);
    nItems += augmentGroup3List(menu, "p>", true);
    enableMenu(menu, (nItems > 0));
    enableMenu(htMenus.get("PDBproteinMenu"), (nItems > 0));

    nItems = augmentGroup3List(menu1, "n>", false);
    enableMenu(menu1, nItems > 0);
    enableMenu(htMenus.get("PDBnucleicMenu"), (nItems > 0));

    nItems = augmentGroup3List(menu2, "c>", false);
    enableMenu(menu2, nItems > 0);
    enableMenu(htMenus.get("PDBcarboMenu"), (nItems > 0));
  }

  String group3List;
  int[] group3Counts;

  int updateGroup3List(Object menu, String name) {
    int nItems = 0;
    int n = group3Counts[group3List.indexOf(name) / 6];
    String script = null;
    if (n > 0) {
      script ="SELECT " + name;
      name += "  (" + n + ")";
      nItems++;
    } else {
      script = null;
    }
    Object item = addMenuItem(menu, name, script, getId(menu) + "." + name);
    if (n == 0)
      enableMenuItem(item, false);
    return nItems;
  }

  int augmentGroup3List(Object menu, String type, boolean addSeparator) {
    int pt = JmolConstants.GROUPID_AMINO_MAX * 6 - 6;
    // ...... p>AFN]o>ODH]n>+T ]
    int nItems = 0;
    while (true) {
      pt = group3List.indexOf(type, pt);
      if (pt < 0)
        break;
      if (nItems++ == 0 && addSeparator)
        addMenuSeparator(menu);
      int n = group3Counts[pt / 6];
      String heteroCode = group3List.substring(pt + 2, pt + 5);
      String name = heteroCode + "  (" + n + ")";
      addMenuItem(menu, name, "SELECT [" + heteroCode + "]", getId(menu) + "." + name);
      pt++;
    }
    return nItems;
  }

  void updateSYMMETRYComputedMenus() {
    updateSYMMETRYSelectComputedMenu();
    updateSYMMETRYShowComputedMenu();
  }

  @SuppressWarnings("unchecked")
  private void updateSYMMETRYShowComputedMenu() {
    Object menu = htMenus.get("SYMMETRYShowComputedMenu");
    if (menu == null)
      return;
    removeAll(menu);
    enableMenu(menu, false);
    if (!isSymmetry || modelIndex < 0)
      return;
    Map<String, Object> info = (Map<String, Object>) viewer.getProperty("DATA_API", "spaceGroupInfo", null);
    if (info == null)
      return;
    Object[][] infolist = (Object[][]) info.get("operations");
    if (infolist == null)
      return;
    String name = (String) info.get("spaceGroupName");
    setLabel(menu, name == null ? GT._("Space Group") : name);
    Object subMenu = menu;
    int nmod = MAX_ITEMS;
    int pt = (infolist.length > MAX_ITEMS ? 0 : Integer.MIN_VALUE);
    for (int i = 0; i < infolist.length; i++) {
      if (pt >= 0 && (pt++ % nmod) == 0) {
        String id = "drawsymop" + pt + "Menu";
        subMenu = newMenu((i + 1) + "..."
            + Math.min(i + MAX_ITEMS, infolist.length), getId(menu) + "." + id);
        addMenuSubMenu(menu, subMenu);
        htMenus.put(id, subMenu);
        pt = 1;
      }
      String entryName = (i + 1) + " " + infolist[i][2] + " (" + infolist[i][0] + ")";
      enableMenuItem(addMenuItem(subMenu, entryName, "draw SYMOP " + (i + 1), null), true);
    }
    enableMenu(menu, true);
  }

  private void updateSYMMETRYSelectComputedMenu() {
    Object menu = htMenus.get("SYMMETRYSelectComputedMenu");
    if (menu == null)
      return;
    removeAll(menu);
    enableMenu(menu, false);
    if (!isSymmetry || modelIndex < 0)
      return;
    String[] list = (String[]) modelInfo.get("symmetryOperations");
    if (list == null)
      return;
    int[] cellRange = (int[]) modelInfo.get("unitCellRange");
    boolean haveUnitCellRange = (cellRange != null);
    Object subMenu = menu;
    int nmod = MAX_ITEMS;
    int pt = (list.length > MAX_ITEMS ? 0 : Integer.MIN_VALUE);
    for (int i = 0; i < list.length; i++) {
      if (pt >= 0 && (pt++ % nmod) == 0) {
        String id = "symop" + pt + "Menu";
        subMenu = newMenu((i + 1) + "..."
            + Math.min(i + MAX_ITEMS, list.length), getId(menu) + "." + id);
        addMenuSubMenu(menu, subMenu);
        htMenus.put(id, subMenu);
        pt = 1;
      }
      String entryName = "symop=" + (i + 1) + " # " + list[i];
      enableMenuItem(addMenuItem(subMenu, entryName, "SELECT symop=" + (i + 1), null),
          haveUnitCellRange);
    }
    enableMenu(menu, true);
  }

  void updateFRAMESbyModelComputedMenu() {
    //allowing this in case we move it later
    Object menu = htMenus.get("FRAMESbyModelComputedMenu");
    if (menu == null)
      return;
    enableMenu(menu, (modelCount > 1));
    setLabel(menu, (modelIndex < 0 ? GT._(getMenuText("allModelsText"), modelCount, true)
        : getModelLabel()));
    removeAll(menu);
    if (modelCount < 2)
      return;
    addCheckboxMenuItem(menu, GT._("All", true), "frame 0 ##", null,
        (modelIndex < 0), false);

    Object subMenu = menu;
    int nmod = MAX_ITEMS;
    int pt = (modelCount > MAX_ITEMS ? 0 : Integer.MIN_VALUE);
    for (int i = 0; i < modelCount; i++) {
      if (pt >= 0 && (pt++ % nmod) == 0) {
        String id = "model" + pt + "Menu";
        subMenu = newMenu(
            (i + 1) + "..." + Math.min(i + MAX_ITEMS, modelCount), getId(menu)
                + "." + id);
        addMenuSubMenu(menu, subMenu);
        htMenus.put(id, subMenu);
        pt = 1;
      }
      String script = "" + viewer.getModelNumberDotted(i);
      String entryName = viewer.getModelName(i);
      if (!entryName.equals(script))
        entryName = script + ": " + entryName;
      if (entryName.length() > 50)
        entryName = entryName.substring(0, 45) + "...";
      addCheckboxMenuItem(subMenu, entryName, "model " + script + " ##", null,
          (modelIndex == i), false);
    }
  }

  private String configurationSelected = "";

  private void updateConfigurationComputedMenu() {
    Object menu = htMenus.get("configurationComputedMenu");
    if (menu == null)
      return;
    enableMenu(menu, isMultiConfiguration);
    if (!isMultiConfiguration)
      return;
    int nAltLocs = altlocs.length();
    setLabel(menu, GT._(getMenuText("configurationMenuText"), nAltLocs, true));
    removeAll(menu);
    String script = "hide none ##CONFIG";
    addCheckboxMenuItem(menu, GT._("All", true), script, null,
        (updateMode == UPDATE_CONFIG && configurationSelected.equals(script)), false);
    for (int i = 0; i < nAltLocs; i++) {
      script = "configuration " + (i + 1) + "; hide thisModel and not selected ##CONFIG";
      String entryName = "" + (i + 1) + " -- \"" + altlocs.charAt(i) + "\"";
      addCheckboxMenuItem(menu, entryName, script, null,
          (updateMode == UPDATE_CONFIG && configurationSelected.equals(script)), false);
    }
  }

  @SuppressWarnings("unchecked")
  private void updateModelSetComputedMenu() {
    Object menu = htMenus.get("modelSetMenu");
    if (menu == null)
      return;
    removeAll(menu);
    renameMenu(menu, nullModelSetName);
    enableMenu(menu, false);
    enableMenu(htMenus.get("surfaceMenu"), !isZapped);
    enableMenu(htMenus.get("measureMenu"), !isZapped);
    enableMenu(htMenus.get("pickingMenu"), !isZapped);
    enableMenu(htMenus.get("computationMenu"), !isZapped);
    if (modelSetName == null || isZapped)
      return;
    if (isMultiFrame) {
      modelSetName = GT._(getMenuText("modelSetCollectionText"), modelCount);
      if (modelSetName.length() > TITLE_MAX_WIDTH)
        modelSetName = modelSetName.substring(0, TITLE_MAX_WIDTH) + "...";
    } else if (viewer.getBooleanProperty("hideNameInPopup")) {
      modelSetName = getMenuText("hiddenModelSetText");
    } else if (modelSetName.length() > TITLE_MAX_WIDTH) {
      modelSetName = modelSetName.substring(0, TITLE_MAX_WIDTH) + "...";
    }
    renameMenu(menu, modelSetName);
    enableMenu(menu, true);
    // 100 here is totally arbitrary. You can do a minimization on any number of atoms
    enableMenu(htMenus.get("computationMenu"), atomCount <= 100);
    addMenuItem(menu, GT._(getMenuText("atomsText"), atomCount, true));
    addMenuItem(menu, GT._(getMenuText("bondsText"), viewer
        .getBondCountInModel(modelIndex), true));
    if (isPDB) {
      addMenuSeparator(menu);
      addMenuItem(menu, GT._(getMenuText("groupsText"), viewer
          .getGroupCountInModel(modelIndex), true));
      addMenuItem(menu, GT._(getMenuText("chainsText"), viewer
          .getChainCountInModel(modelIndex), true));
      addMenuItem(menu, GT._(getMenuText("polymersText"), viewer
          .getPolymerCountInModel(modelIndex), true));
      Object submenu = htMenus.get("BiomoleculesMenu");
      if (submenu == null) {
        submenu = newMenu(GT._(getMenuText("biomoleculesMenuText")),
            getId(menu) + ".biomolecules");
        addMenuSubMenu(menu, submenu);
      }
      removeAll(submenu);
      enableMenu(submenu, false);
      List<Map<String, Object>> biomolecules;
      if (modelIndex >= 0
          && (biomolecules = (List<Map<String, Object>>) viewer.getModelAuxiliaryInfo(modelIndex,
              "biomolecules")) != null) {
        enableMenu(submenu, true);
        int nBiomolecules = biomolecules.size();
        for (int i = 0; i < nBiomolecules; i++) {
          String script = (isMultiFrame ? ""
              : "save orientation;load \"\" FILTER \"biomolecule " + (i + 1) + "\";restore orientation;");
          int nAtoms = ((Integer) biomolecules.get(i).get("atomCount")).intValue();
          String entryName = GT._(getMenuText(isMultiFrame ? "biomoleculeText"
              : "loadBiomoleculeText"), new Object[] { Integer.valueOf(i + 1),
              Integer.valueOf(nAtoms) });
          addMenuItem(submenu, entryName, script, null);
        }
      }
    }
    if (isApplet && viewer.showModelSetDownload()
        && !viewer.getBooleanProperty("hideNameInPopup")) {
      addMenuSeparator(menu);
      addMenuItem(menu, GT._(getMenuText("viewMenuText"), 
          modelSetFileName, true), "show url", null);
    }
  }

  private String getModelLabel() {
    return GT._(getMenuText("modelMenuText"), (modelIndex + 1) + "/" + modelCount, true);
  }

  private void updateAboutSubmenu() {
    Object menu = htMenus.get("aboutComputedMenu");
    if (menu == null)
      return;
    for (int i = getMenuItemCount(menu); --i >= aboutComputedMenuBaseCount;)
      removeMenuItem(menu, i);
      
    Object subMenu = newMenu("About molecule", "modelSetMenu");  
      // No need to localize this, as it will be overwritten with the model's name      
    addMenuSubMenu(menu, subMenu);
    htMenus.put("modelSetMenu", subMenu);
    updateModelSetComputedMenu();

    subMenu = newMenu("Jmol " + JmolConstants.version + (isSigned ? " (signed)" : ""), "aboutJmolMenu");
    addMenuSubMenu(menu, subMenu);
    htMenus.put("aboutJmolMenu", subMenu);
    addMenuItem(subMenu, JmolConstants.date);
    addMenuItem(subMenu, "http://www.jmol.org", "show url \"http://www.jmol.org\"", null);
    addMenuItem(subMenu, GT._("Mouse Manual"), "show url \"http://wiki.jmol.org/index.php/Mouse_Manual\"", null);
    addMenuItem(subMenu, GT._("Translations"), "show url \"http://wiki.jmol.org/index.php/Internationalisation\"", null);

    subMenu = newMenu(GT._("System", true), "systemMenu");        
    addMenuSubMenu(menu, subMenu);
    htMenus.put("systemMenu", subMenu);
    addMenuItem(subMenu, viewer.getOperatingSystemName());
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    if (availableProcessors > 0)
      addMenuItem(subMenu, (availableProcessors == 1) ? GT._("1 processor", true)
          : GT._("{0} processors", availableProcessors, true));
    else
      addMenuItem(subMenu, GT._("unknown processor count", true));      
    addMenuSeparator(subMenu);
    addMenuItem(subMenu, GT._("Java version:", true));
    addMenuItem(subMenu, viewer.getJavaVendor());
    addMenuItem(subMenu, viewer.getJavaVersion());
    addMenuSeparator(subMenu);
    addMenuItem(subMenu, GT._("Java memory usage:", true));    
    Runtime runtime = Runtime.getRuntime();
    //runtime.gc();
    long mbTotal = convertToMegabytes(runtime.totalMemory());
    long mbFree = convertToMegabytes(runtime.freeMemory());
    long mbMax = convertToMegabytes(maxMemoryForNewerJvm());
    addMenuItem(subMenu, GT._("{0} MB total", new Object[] { new Long(mbTotal) },
        true));
    addMenuItem(subMenu, GT._("{0} MB free", new Object[] { new Long(mbFree) },
        true));
    if (mbMax > 0)
      addMenuItem(subMenu, GT._("{0} MB maximum",
          new Object[] { new Long(mbMax) }, true));
    else
      addMenuItem(subMenu, GT._("unknown maximum", true));
  }

  private void updateLanguageSubmenu() {
    Object menu = htMenus.get("languageComputedMenu");
    if (menu == null)
      return;
    for (int i = getMenuItemCount(menu); --i >= 0;)
      removeMenuItem(menu, i);
    String language = GT.getLanguage();
    String id = getId(menu);
    GT.Language[] languages = GT.getLanguageList();
    for (int i = 0; i < languages.length; i++) {
      if (language.equals(languages[i].code)) {
        languages[i].forceDisplay();
      }
      if (languages[i].shouldDisplay()) {
        String code = languages[i].code;
        String name = languages[i].language;
        String nativeName = languages[i].nativeLanguage;
        String menuLabel = code + " - " + GT._(name, true);
        if ((nativeName != null) && (!nativeName.equals(GT._(name, true)))) {
          menuLabel += " - " + nativeName; 
        }
        addCheckboxMenuItem(
            menu,
            menuLabel,
            "language = \"" + code + "\" ##" + name,
            id + "." + code,
            language.equals(code), false);
      }
    }
  }

  private long convertToMegabytes(long num) {
    if (num <= Long.MAX_VALUE - 512 * 1024)
      num += 512 * 1024;
    return num / (1024 * 1024);
  }

  /**
   * (1) setOption --> set setOption true or set setOption false
   * @param item 
   *  
   * @param what option to set
   * @param TF   true or false
   */
  @Override
  protected void setCheckBoxValue(JMenuItem item, String what, boolean TF) {    
    super.setCheckBoxValue(item, what, TF);
    if (what.indexOf("#CONFIG") >= 0) {
      configurationSelected = what;
      updateConfigurationComputedMenu();
      updateModelSetComputedMenu();
      return;
    }
  }

  @Override
  protected void updateForShow() {
    getViewerData();
    updateMode = UPDATE_SHOW;
    updateSelectMenu();
    updateFRAMESbyModelComputedMenu();
    updateModelSetComputedMenu();
    updateAboutSubmenu();
  }

  @Override
  public void show(int x, int y) {
    super.show(x, y, false);
    if (x < 0) {
      getViewerData();
      setFrankMenu(currentMenuItemId);
      thisx = -x - 50;
      if (nFrankList > 1) {
        thisy = y - nFrankList * MENUITEM_HEIGHT;
        showFrankMenu(thisx, thisy);
        return;
      }
    }
    restorePopupMenu();
    if (asPopup)
      showPopupMenu(thisx, thisy);
  }

  private Object[][] frankList = new Object[10][]; //enough to cover menu drilling
  private int nFrankList = 0;
  private String currentFrankId = null;

  private void setFrankMenu(String id) {
    if (currentFrankId != null && currentFrankId == id && nFrankList > 0)
      return;
    if (frankPopup == null)
      createFrankPopup();
    resetFrankMenu();
    if (id == null)
      return;
    currentFrankId = id;
    nFrankList = 0;
    frankList[nFrankList++] = new Object[] { null, null, null };
    addMenuItem(frankPopup, getMenuText("mainMenuText"), "MAIN", "");
    for (int i = id.indexOf(".", 2) + 1;;) {
      int iNew = id.indexOf(".", i);
      if (iNew < 0)
        break;
      String strMenu = id.substring(i, iNew);
      Object menu = htMenus.get(strMenu);
      frankList[nFrankList++] = new Object[] { getParent(menu), menu,
          Integer.valueOf(getPosition(menu)) };
      addMenuSubMenu(frankPopup, menu);
      i = iNew + 1;
    }
  }

  protected Object getParent(Object menu) {
    return ((JMenu) menu).getParent();
  }

  protected int getPosition(Object menu) {
    Object p = getParent(menu);
    if (p instanceof JPopupMenu) {
      for (int i = ((JPopupMenu) p).getComponentCount(); --i >= 0;)
        if (((JPopupMenu) p).getComponent(i) == menu)
          return i;
    } else {
      for (int i = ((JMenu) p).getItemCount(); --i >= 0;)
        if (((JMenu) p).getItem(i) == menu)
          return i;
    }
    return -1;
  }

  @Override
  void restorePopupMenu() {
    if (nFrankList < 2)
      return;
    // first entry is just the main item
    for (int i = nFrankList; --i > 0;) {
      insertMenuSubMenu(frankList[i][0], frankList[i][1],
          ((Integer) frankList[i][2]).intValue());
    }
    nFrankList = 1;
  }

  ////////////////////////////////////////////////////////////////

  public void installMainMenu(Object objMenuBar) {
    if (objMenuBar instanceof JMenuBar) {
      JMenuBar mb = (JMenuBar) objMenuBar;
      mb.remove(0);
      mb.add(mainMenu, 0);
    }
  }

  void insertMenuSubMenu(Object menu, Object subMenu, int index) {
    if (menu instanceof JPopupMenu)
      ((JPopupMenu) menu).insert((JMenu) subMenu, index);
    else
      ((JMenu) menu).insert((JMenu) subMenu, index);
  }

  void createFrankPopup() {
    frankPopup = new JPopupMenu("Frank");
  }

  void showFrankMenu(int x, int y) {
    if (display == null)
      return;
    try {
      frankPopup.show(display, x, y);
    } catch (Exception e) {
      // probably a permissions problem in Java 7
    }
  }

  void resetFrankMenu() {
    frankPopup.removeAll();
  }

  long maxMemoryForNewerJvm() {
    return Runtime.getRuntime().maxMemory();
  }

  private String getMenuCurrent() {
    StringBuffer sb = new StringBuffer();
    Object menu = htMenus.get("popupMenu");
    getMenuCurrent(sb, 0, menu, "PopupMenu");
    return sb.toString();
  }

  private void getMenuCurrent(StringBuffer sb, int level,
                                     Object menu, String menuName) {
    String name = menuName;
    Component[] subMenus = 
      (menu instanceof JPopupMenu ? ((JPopupMenu) menu).getComponents()
       : ((JMenu) menu).getPopupMenu().getComponents());
    for (int i = 0; i < subMenus.length; i++) {
      Object m = subMenus[i];
      String flags;
      if (m instanceof JMenu) {
        JMenu jm = (JMenu) m;
        name = jm.getName();
        flags = "enabled:" + jm.isEnabled();
        addCurrentItem(sb, 'M', level, name, jm.getText(), null, flags);
        getMenuCurrent(sb, level + 1, ((JMenu) m).getPopupMenu(), name);
      } else if (m instanceof JMenuItem) {
        JMenuItem jmi = (JMenuItem) m;
        flags = "enabled:" + jmi.isEnabled();
        if (m instanceof JCheckBoxMenuItem)
          flags += ";checked:" + ((JCheckBoxMenuItem) m).getState();
        String script = fixScript(jmi.getName(), jmi.getActionCommand());
        addCurrentItem(sb, 'I', level, jmi.getName(), jmi.getText(), script,
            flags);
      } else {
        addCurrentItem(sb, 'S', level, name, null, null, null);
      }
    }
  }

}
