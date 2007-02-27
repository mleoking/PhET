/* * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                  *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *                                                                              *
 * The starting point for Liquid Look and Feel was the XP Look and Feel written *
 * by Stefan Krause.                                                            *
 * The original header of this file was:                                        *
 ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
/* * * * * * *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        XP Look and Feel                                                      *
 *                                                                              *
 *  (C) Copyright 2002, by Stefan Krause, Taufik Romdhane and Contributors      *
 *                                                                              *
 *                                                                              *
 * The XP Look and Feel started as as extension to the Metouia Look and Feel.   *
 * The original header of this file was:                                        *
 ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Metouia Look And Feel: a free pluggable look and feel for java        *
 *                         http://mlf.sourceforge.net                           *
 *          (C) Copyright 2002, by Taoufik Romdhane and Contributors.           *
 *                                                                              *
 *   This library is free software; you can redistribute it and/or modify it    *
 *   under the terms of the GNU Lesser General Public License as published by   *
 *   the Free Software Foundation; either version 2.1 of the License, or (at    *
 *   your option) any later version.                                            *
 *                                                                              *
 *   This library is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
 *   See the GNU Lesser General Public License for more details.                *
 *                                                                              *
 *   You should have received a copy of the GNU General Public License along    *
 *   with this program; if not, write to the Free Software Foundation, Inc.,    *
 *   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.                    *
 *                                                                              *
 *   Original Author:  Taoufik Romdhane                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import com.birosoft.liquid.borders.*;
import com.birosoft.liquid.skin.SkinImageCache;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;

import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.DefaultEditorKit;
import org.jvnet.lafplugin.ComponentPluginManager;

import org.jvnet.lafplugin.PluginManager;
import java.util.Set;
import java.util.Map;
import org.jvnet.lafplugin.LafPlugin;
import java.util.Iterator;
import java.util.Collections;


/**
 * The Liquid look and feel. There's a special property to switch the support for
 * rounded windows of internal frames on or off:
 * Use Liquidlookandfeel.roundedWindows = false to turn this support off. It causes
 * slower rendering especially in the case of dragging the internal frame.
 * By default it is tuned on.
 *
 * Another property sets whether the windows look and feel should be used for
 * the file chooser: xplookandfeel.windowslfforfilechooser= true
 * If set to true it'll be used. Currently this leads to a black border around the icons.
 * By default this option is disabled.
 *
 * @author Taoufik Romdhane, Stefan Krause
 */
public class LiquidLookAndFeel extends BasicLookAndFeel {

// *Start LAF-PLUGIN SUPPORT*
    public static final String PLUGIN_XML = "META-INF/liquid-plugin.xml";
    protected static ComponentPluginManager pluginManager = new ComponentPluginManager(PLUGIN_XML);
// *End LAF-PLUGIN SUPPORT*
    
    protected static UIDefaults uiDefaults;
    protected static boolean defaultRowBackgroundMode = true;
    protected static boolean showTableGrids = false;
    protected static boolean panelTransparency = true;
    protected static boolean winDecoPanther = false;
    protected static boolean toolbarButtonsFocusable = false;
    protected static boolean toolbarFlattedButtons = true;
    protected static boolean toolbarFlattedButtonsRollover = true;
    private static boolean bgStipples = true;
    private static boolean isInstalled = false;
    protected static String fontName = "Helvetica";

    /**
     * Returns true if running on Windows.
     */
    static boolean isWindows() {
        String osName = System.getProperty("os.name");
        if (osName != null && osName.indexOf("Windows") != -1) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if the running JRE is pre Java 5 (Tiger).
     */
    static boolean isPreTiger() {
        String jre = System.getProperty("java.version", "");
        String versionString = jre.substring(2, 3);
        Integer version = versionString != null ? Integer.valueOf(versionString) : null;
        
        if (version != null && version.intValue() < 5) {
            return true;
        }
        return false;
    }

    /**
     * The installation state of the Metouia Look and Feel.
     */
    protected Toolkit awtToolkit = Toolkit.getDefaultToolkit();
    private HashMap colorMap = new HashMap();
    Border focusCellHighlightBorder = new LiquidFocusCellHighlightBorder(new Color(
                86, 46, 0));
    Border listBorder = new LiquidListBorder();
    Border zeroEmptyBorder = new EmptyBorder(0, 0, 0, 0);
    Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[] {
                "ctrl C", DefaultEditorKit.copyAction, "ctrl V",
                DefaultEditorKit.pasteAction, "ctrl X",
                DefaultEditorKit.cutAction, "COPY", DefaultEditorKit.copyAction,
                "PASTE", DefaultEditorKit.pasteAction, "CUT",
                DefaultEditorKit.cutAction, "shift LEFT",
                DefaultEditorKit.selectionBackwardAction, "shift KP_LEFT",
                DefaultEditorKit.selectionBackwardAction, "shift RIGHT",
                DefaultEditorKit.selectionForwardAction, "shift KP_RIGHT",
                DefaultEditorKit.selectionForwardAction, "ctrl LEFT",
                DefaultEditorKit.previousWordAction, "ctrl KP_LEFT",
                DefaultEditorKit.previousWordAction, "ctrl RIGHT",
                DefaultEditorKit.nextWordAction, "ctrl KP_RIGHT",
                DefaultEditorKit.nextWordAction, "ctrl shift LEFT",
                DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift KP_LEFT",
                DefaultEditorKit.selectionPreviousWordAction, "ctrl shift RIGHT",
                DefaultEditorKit.selectionNextWordAction, "ctrl shift KP_RIGHT",
                DefaultEditorKit.selectionNextWordAction, "ctrl A",
                DefaultEditorKit.selectAllAction, "HOME",
                DefaultEditorKit.beginLineAction, "END",
                DefaultEditorKit.endLineAction, "shift HOME",
                DefaultEditorKit.selectionBeginLineAction, "shift END",
                DefaultEditorKit.selectionEndLineAction, "typed \010",
                DefaultEditorKit.deletePrevCharAction, "DELETE",
                DefaultEditorKit.deleteNextCharAction, "RIGHT",
                DefaultEditorKit.forwardAction, "LEFT",
                DefaultEditorKit.backwardAction, "KP_RIGHT",
                DefaultEditorKit.forwardAction, "KP_LEFT",
                DefaultEditorKit.backwardAction, "ENTER",
                JTextField.notifyAction, "ctrl BACK_SLASH",
                "unselect" /*DefaultEditorKit.unselectAction*/,
                "control shift O",
                "toggle-componentOrientation" /*DefaultEditorKit.toggleComponentOrientation*/
            });
    Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[] {
                "ctrl C", DefaultEditorKit.copyAction, "ctrl V",
                DefaultEditorKit.pasteAction, "ctrl X",
                DefaultEditorKit.cutAction, "COPY", DefaultEditorKit.copyAction,
                "PASTE", DefaultEditorKit.pasteAction, "CUT",
                DefaultEditorKit.cutAction, "shift LEFT",
                DefaultEditorKit.selectionBackwardAction, "shift KP_LEFT",
                DefaultEditorKit.selectionBackwardAction, "shift RIGHT",
                DefaultEditorKit.selectionForwardAction, "shift KP_RIGHT",
                DefaultEditorKit.selectionForwardAction, "ctrl LEFT",
                DefaultEditorKit.previousWordAction, "ctrl KP_LEFT",
                DefaultEditorKit.previousWordAction, "ctrl RIGHT",
                DefaultEditorKit.nextWordAction, "ctrl KP_RIGHT",
                DefaultEditorKit.nextWordAction, "ctrl shift LEFT",
                DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift KP_LEFT",
                DefaultEditorKit.selectionPreviousWordAction, "ctrl shift RIGHT",
                DefaultEditorKit.selectionNextWordAction, "ctrl shift KP_RIGHT",
                DefaultEditorKit.selectionNextWordAction, "ctrl A",
                DefaultEditorKit.selectAllAction, "HOME",
                DefaultEditorKit.beginLineAction, "END",
                DefaultEditorKit.endLineAction, "shift HOME",
                DefaultEditorKit.selectionBeginLineAction, "shift END",
                DefaultEditorKit.selectionEndLineAction,
                
            "UP", DefaultEditorKit.upAction, "KP_UP", DefaultEditorKit.upAction,
                "DOWN", DefaultEditorKit.downAction, "KP_DOWN",
                DefaultEditorKit.downAction, "PAGE_UP",
                DefaultEditorKit.pageUpAction, "PAGE_DOWN",
                DefaultEditorKit.pageDownAction, "shift PAGE_UP",
                "selection-page-up", "shift PAGE_DOWN", "selection-page-down",
                "ctrl shift PAGE_UP", "selection-page-left",
                "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP",
                DefaultEditorKit.selectionUpAction, "shift KP_UP",
                DefaultEditorKit.selectionUpAction, "shift DOWN",
                DefaultEditorKit.selectionDownAction, "shift KP_DOWN",
                DefaultEditorKit.selectionDownAction, "ENTER",
                DefaultEditorKit.insertBreakAction, "typed \010",
                DefaultEditorKit.deletePrevCharAction, "DELETE",
                DefaultEditorKit.deleteNextCharAction, "RIGHT",
                DefaultEditorKit.forwardAction, "LEFT",
                DefaultEditorKit.backwardAction, "KP_RIGHT",
                DefaultEditorKit.forwardAction, "KP_LEFT",
                DefaultEditorKit.backwardAction, "TAB",
                DefaultEditorKit.insertTabAction, "ctrl BACK_SLASH",
                "unselect" /*DefaultEditorKit.unselectAction*/, "ctrl HOME",
                DefaultEditorKit.beginAction, "ctrl END",
                DefaultEditorKit.endAction, "ctrl shift HOME",
                DefaultEditorKit.selectionBeginAction, "ctrl shift END",
                DefaultEditorKit.selectionEndAction, "ctrl T",
                "next-link-action", "ctrl shift T", "previous-link-action",
                "ctrl SPACE", "activate-link-action", "control shift O",
                "toggle-componentOrientation" /*DefaultEditorKit.toggleComponentOrientation*/
            });
    Object formattedInputMap = new UIDefaults.LazyInputMap(new Object[] {
                "ctrl C", DefaultEditorKit.copyAction, "ctrl V",
                DefaultEditorKit.pasteAction, "ctrl X",
                DefaultEditorKit.cutAction, "COPY", DefaultEditorKit.copyAction,
                "PASTE", DefaultEditorKit.pasteAction, "CUT",
                DefaultEditorKit.cutAction, "shift LEFT",
                DefaultEditorKit.selectionBackwardAction, "shift KP_LEFT",
                DefaultEditorKit.selectionBackwardAction, "shift RIGHT",
                DefaultEditorKit.selectionForwardAction, "shift KP_RIGHT",
                DefaultEditorKit.selectionForwardAction, "ctrl LEFT",
                DefaultEditorKit.previousWordAction, "ctrl KP_LEFT",
                DefaultEditorKit.previousWordAction, "ctrl RIGHT",
                DefaultEditorKit.nextWordAction, "ctrl KP_RIGHT",
                DefaultEditorKit.nextWordAction, "ctrl shift LEFT",
                DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift KP_LEFT",
                DefaultEditorKit.selectionPreviousWordAction, "ctrl shift RIGHT",
                DefaultEditorKit.selectionNextWordAction, "ctrl shift KP_RIGHT",
                DefaultEditorKit.selectionNextWordAction, "ctrl A",
                DefaultEditorKit.selectAllAction, "HOME",
                DefaultEditorKit.beginLineAction, "END",
                DefaultEditorKit.endLineAction, "shift HOME",
                DefaultEditorKit.selectionBeginLineAction, "shift END",
                DefaultEditorKit.selectionEndLineAction, "typed \010",
                DefaultEditorKit.deletePrevCharAction, "DELETE",
                DefaultEditorKit.deleteNextCharAction, "RIGHT",
                DefaultEditorKit.forwardAction, "LEFT",
                DefaultEditorKit.backwardAction, "KP_RIGHT",
                DefaultEditorKit.forwardAction, "KP_LEFT",
                DefaultEditorKit.backwardAction, "ENTER",
                JTextField.notifyAction, "ctrl BACK_SLASH", "unselect",
                "control shift O", "toggle-componentOrientation", "ESCAPE",
                "reset-field-edit", "UP", "increment", "KP_UP", "increment",
                "DOWN", "decrement", "KP_DOWN", "decrement",
            });
    private boolean windowslfforfilechooser = false;


    /**
     * This constructor installs the Liquid Look and Feel with the default color
     * theme.
     */
    public LiquidLookAndFeel() {
        windowslfforfilechooser = false;

        if (!isInstalled) {
            isInstalled = true;

            //UIManager.put("ScrollBar.alternateLayout",Boolean.TRUE);
            UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo(
                    "LiquidLookAndFeel", "com.birosoft.liquid.LiquidLookAndFeel"));
            
// First we'll load any properties from the "liquidlnf.conf" file            
            loadConfProperties();

// Any SYstem properties prefixed with liquidlnf will take precedence            
            loadSystemProperties();
        }
    }

    /**
     * Return a string that identifies this look and feel.  This string
     * will be used by applications/services that want to recognize
     * well known look and feel implementations.  Presently
     * the well known names are "Motif", "Windows", "Mac", "Metal".  Note
     * that a LookAndFeel derived from a well known superclass
     * that doesn't make any fundamental changes to the look or feel
     * shouldn't override this method.
     *
     * @return The Liquid Look and Feel identifier.
     */
    public String getID() {
        return "Liquid";
    }

    /**
     * Return a short string that identifies this look and feel, e.g.
     * "CDE/Motif".  This string should be appropriate for a menu item.
     * Distinct look and feels should have different names, e.g.
     * a subclass of MotifLookAndFeel that changes the way a few components
     * are rendered should be called "CDE/Motif My Way"; something
     * that would be useful to a user trying to select a L&F from a list
     * of names.
     *
     * @return The look and feel short name.
     */
    public String getName() {
        return "Liquid";
    }

    public static ColorUIResource getControl() {
        return (ColorUIResource) uiDefaults.get("control");
    }

    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     *
     * @return The look and feel short description.
     */
    public String getDescription() {
        return "The Liquid Look and Feel version 2.9.1. Published under the GNU Lesser General Public License.";
    }

    /**
     * If the underlying platform has a "native" look and feel, and this
     * is an implementation of it, return true.  For example a CDE/Motif
     * look and implementation would return true when the underlying
     * platform was Solaris.
     */
    public boolean isNativeLookAndFeel() {
        return false;
    }

    /**
     * Return true if the underlying platform supports and or permits
     * this look and feel.  This method returns false if the look
     * and feel depends on special resources or legal agreements that
     * aren't defined for the current platform.
     */
    public final boolean isSupportedLookAndFeel() {
        return true;
    }

    public boolean getSupportsWindowDecorations() {
        return true;
    }

    private void loadConfProperties() {
        Properties liquidProps = new Properties();

        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            liquidProps.load(classLoader.getResourceAsStream("liquidlnf.conf"));

            boolean boolValue = false;
            String stringValue = null;

// Panel Transparency?            
            stringValue = liquidProps.getProperty("panelTransparency", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setPanelTransparency(boolValue);
            }

// Show Table Grids?            
            stringValue = liquidProps.getProperty("showTableGrids", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setShowTableGrids(boolValue);
            }

// Show Stipples?            
            stringValue = liquidProps.getProperty("stipples", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setStipples(boolValue);
            }

// Show Alternate Background?            
            stringValue = liquidProps.getProperty("alternateBackground", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setDefaultRowBackgroundMode(boolValue);
            }

// Custom Window Decorations?            
            stringValue = liquidProps.getProperty("windowDecoration", null);
            if( stringValue != null ) {
                LiquidLookAndFeel.setLiquidDecorations(stringValue != null && stringValue.length() > 0, stringValue);
//                System.out.println("Setting windowDecoration = " + stringValue);
            }

// Flattened Toolbar Buttons?            
            stringValue = liquidProps.getProperty("toolbarFlattedButtons", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setToolbarFlattedButtons(boolValue);
            }

// Flattened Toolbar Buttons Rollover?            
            stringValue = liquidProps.getProperty("toolbarFlattedButtonsRollover", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setToolbarFlattedButtonsRollover(boolValue);
            }

// Toolbar Buttons Focusable?            
            stringValue = liquidProps.getProperty("toolbarButtonsFocusable", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setToolbarButtonsFocusable(boolValue);
            }
        } catch (Exception e) {
// 20060202 MEV - Trying to print the stacktrace when running via Webstart
// causes this exception to get overshadowed because of lack of permissions
// to the util.deploy pacakge.  For now, we'll gracefully assume that there 
// is no liquidlnf.conf config file present or we're running in Webstart.
//            e.printStackTrace();
// 20061113 MEV - We should change to logging this for info level at some point
//            System.err.println("Warning: Problems trying to load Liquid options from liquidlnf.conf file.");
        }
    }

    private void loadSystemProperties() {
//        Properties liquidProps = System.getProperties();

        try {
            boolean boolValue = false;
            String stringValue = null;

// Panel Transparency?            
            stringValue = System.getProperty("liquidlnf.panelTransparency", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setPanelTransparency(boolValue);
            }

// Show Table Grids?            
            stringValue = System.getProperty("liquidlnf.showTableGrids", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setShowTableGrids(boolValue);
            }

// Show Stipples?            
            stringValue = System.getProperty("liquidlnf.stipples", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setStipples(boolValue);
            }

// Show Alternate Background?            
            stringValue = System.getProperty("liquidlnf.alternateBackground", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setDefaultRowBackgroundMode(boolValue);
//                System.out.println("Setting liquidlnf.alternateBackground = " + stringValue);
            }

// Custom Window Decorations?            
            stringValue = System.getProperty("liquidlnf.windowDecoration", null);
            if( stringValue != null ) {
                LiquidLookAndFeel.setLiquidDecorations(stringValue != null && stringValue.length() > 0, stringValue);
//                System.out.println("Setting liquidlnf.windowDecoration = " + stringValue);
            }

// Flattened Toolbar Buttons?            
            stringValue = System.getProperty("liquidlnf.toolbarFlattedButtons", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setToolbarFlattedButtons(boolValue);
            }

// Flattened Toolbar Buttons Rollover?            
            stringValue = System.getProperty("liquidlnf.toolbarFlattedButtonsRollover", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setToolbarFlattedButtonsRollover(boolValue);
            }

// Toolbar Buttons Focusable?            
            stringValue = System.getProperty("liquidlnf.toolbarButtonsFocusable", null);
            if( stringValue != null ) {
                boolValue = Boolean.valueOf(stringValue).booleanValue();
                LiquidLookAndFeel.setToolbarButtonsFocusable(boolValue);
            }
        } catch (Exception e) {
// 20060202 MEV - Trying to print the stacktrace when running via Webstart
// causes this exception to get overshadowed because of lack of permissions
// to the util.deploy pacakge.  For now, we'll gracefully assume that there 
// is no liquidlnf.conf config file present or we're running in Webstart.
//            e.printStackTrace();
// 20061113 MEV - We should change to logging this for info level at some point
//            System.err.println("Warning: Problems trying to load Liquid options from System Properties.");
        }
    }

    
    /**
     * Initializes the uiClassID to BasicComponentUI mapping.
     * The JComponent classes define their own uiClassID constants. This table
     * must map those constants to a BasicComponentUI class of the appropriate
     * type.
     *
     * @param table The ui defaults table.
     */
    protected void initClassDefaults(UIDefaults table) {
        uiDefaults = table;
        super.initClassDefaults(table);

        table.putDefaults(new Object[] {
                "ButtonUI", "com.birosoft.liquid.LiquidButtonUI", "CheckBoxUI",
                "com.birosoft.liquid.LiquidCheckBoxUI", "TextFieldUI",
                "com.birosoft.liquid.LiquidTextFieldUI", "FormattedTextFieldUI",
                "com.birosoft.liquid.LiquidTextFieldUI", "PasswordTextFieldUI",
                "com.birosoft.liquid.LiquidPasswordFieldUI", "PasswordFieldUI",
                "com.birosoft.liquid.LiquidPasswordFieldUI", "SliderUI",
                "com.birosoft.liquid.LiquidSliderUI", "SpinnerUI",
                "com.birosoft.liquid.LiquidSpinnerUI", "ToolBarUI",
                "com.birosoft.liquid.LiquidToolBarUI", "MenuBarUI",
                "com.birosoft.liquid.LiquidMenuBarUI", "MenuUI",
                "com.birosoft.liquid.LiquidMenuUI", "PanelUI",
                "com.birosoft.liquid.LiquidPanelUI", "MenuItemUI",
                "com.birosoft.liquid.LiquidMenuItemUI", "CheckBoxMenuItemUI",
                "com.birosoft.liquid.LiquidCheckBoxMenuItemUI",
                "RadioButtonMenuItemUI",
                "com.birosoft.liquid.LiquidRadioButtonMenuItemUI", "TableUI",
                "com.birosoft.liquid.LiquidTableUI", "TableHeaderUI",
                "com.birosoft.liquid.LiquidTableHeaderUI", "ScrollBarUI",
                "com.birosoft.liquid.LiquidScrollBarUI", "TabbedPaneUI",
                "com.birosoft.liquid.LiquidTabbedPaneUI", "ToggleButtonUI",
                "com.birosoft.liquid.LiquidToggleButtonUI", "ScrollPaneUI",
                "com.birosoft.liquid.LiquidScrollPaneUI", "ProgressBarUI",
                "com.birosoft.liquid.LiquidProgressBarUI", "InternalFrameUI",
                "com.birosoft.liquid.LiquidInternalFrameUI","DesktopIconUI",
                "com.birosoft.liquid.LiquidDesktopIconUI", "RadioButtonUI",
                "com.birosoft.liquid.LiquidRadioButtonUI", "ComboBoxUI",
                "com.birosoft.liquid.LiquidComboBoxUI", "ListUI",
                "com.birosoft.liquid.LiquidListUI", "SeparatorUI",
                "com.birosoft.liquid.LiquidSeparatorUI", "PopupMenuSeparatorUI",
                "com.birosoft.liquid.LiquidPopupMenuSeparatorUI", "SplitPaneUI",
                "com.birosoft.liquid.LiquidSplitPaneUI", "FileChooserUI",
                "com.birosoft.liquid.FileChooserBasicUI", "RootPaneUI",
                "com.birosoft.liquid.LiquidRootPaneUI", "OptionPaneUI",
                "com.birosoft.liquid.LiquidOptionPaneUI"
            });

// *Start LAF-PLUGIN SUPPORT*
        pluginManager.processAllDefaultsEntries(table, null);
// *End LAF-PLUGIN SUPPORT*
    }

    /*
    contrast=7
    */

    /**
     * Initializes the system colors.
     *
     * @param table The ui defaults table.
     */
    protected void initSystemColorDefaults(UIDefaults table) {
        colorMap.put("activeBackground", "#3E91EB");
        colorMap.put("activeBlend", "#3E91EB");
        colorMap.put("activeForeground", "#FFFFFF");
        colorMap.put("activeTitleBtnBg", "#AFD6FF");
        colorMap.put("alternateBackground", "#EEF6FF");
        colorMap.put("background", "#F6F5F4");
        colorMap.put("buttonBackground", "#D7E7F9");
        colorMap.put("buttonForeground", "#000000");
        colorMap.put("foreground", "#000000");
        colorMap.put("inactiveBackground", "#AFD6FF");
        colorMap.put("inactiveBlend", "#AFD6FF");
        colorMap.put("inactiveForeground", "#232323");
        colorMap.put("inactiveTitleBtnBg", "#DAEEFF");
        colorMap.put("linkColor", "#0000C0");
        colorMap.put("selectBackground", "#A9D1FF");
        colorMap.put("selectForeground", "#030303");
        colorMap.put("visitedLinkColor", "#800080");
        colorMap.put("windowBackground", "#FFFFFF");
        colorMap.put("windowForeground", "#000000");

        String[] defaultSystemColors = {
                "desktop", (String) colorMap.get("alternateBackground"), /* + Color of the desktop background */
                "activeCaption", (String) colorMap.get("activeBackground"), /* + Color for captions (title bars) when they are active. */
                "activeCaptionText", (String) colorMap.get("activeForeground"), /* + Text color for text in captions (title bars). */
                "activeCaptionBorder", (String) colorMap.get("activeBackground"), /* + Border color for caption (title bar) window borders. */
                "inactiveCaption", (String) colorMap.get("inactiveBackground"), /* + Color for captions (title bars) when not active. */
                "inactiveCaptionText",
                (String) colorMap.get("inactiveForeground"), /* + Text color for text in inactive captions (title bars). */
                "inactiveCaptionBorder",
                (String) colorMap.get("inactiveBackground"), /* + Border color for inactive caption (title bar) window borders. */
                "window", (String) colorMap.get("background"), /* + Default color for the interior of windows */
                "windowBorder", (String) colorMap.get("windowBackground"), /* + ??? */
                "windowText", (String) colorMap.get("windowForeground"), /* + ??? */
                "menu", (String) colorMap.get("background"), /* + ??? */
                "menuText", (String) colorMap.get("foreground"), /* + ??? */
                "text", (String) colorMap.get("windowBackground"), /* + Text background color */
                "textText", (String) colorMap.get("windowForeground"), /* Text foreground color */
                "textHighlight", (String) colorMap.get("selectBackground"), /* + Text background color when selected */
                "textHighlightText", (String) colorMap.get("selectForeground"), /* + Text color when selected */
                "textInactiveText", "#A7A5A3", /* + Text color when disabled */
                "control", (String) colorMap.get("background"), /* + Default color for controls (buttons, sliders, etc) */
                "controlText", (String) colorMap.get("buttonForeground"), /* + Default color for text in controls */
                "controlHighlight", (String) colorMap.get("buttonBackground"), /* + Highlight color for controls */
                "controlLtHighlight", (String) colorMap.get("selectBackground"), /* + Light highlight color for controls */
                "controlShadow", "#BBBBBB", /* Shadow color for controls */
                "controlLightShadow", "#000000", /* Shadow color for controls */
                "controlDkShadow", "#000000", /* Dark shadow color for controls */
                "scrollbar", "#000000", /* Scrollbar ??? color. PENDING(jeff) foreground? background? ?*/
                "info", (String) colorMap.get("foreground"), /* ??? */
                "infoText", (String) colorMap.get("foreground") /* ??? */
            };
        loadSystemColors(table, defaultSystemColors, false);
    }

    /**
     * Initializes the default values for many ui widgets and puts them in the
     * given ui defaults table.
     * Here is the place where borders can be changed.
     *
     * @param table The ui defaults table.
     */
    protected void initComponentDefaults(UIDefaults table) {
        /* As you can see this is still work in progress... */

        // Let Basic Look and Feel do the basic and complete initializations:
        super.initComponentDefaults(table);

        // Replace the Basic borders:
        Border border = new EmptyBorder(0, 0, 0, 0);

        //border=new EmptyBorder(4, 16, 4, 16);
        //table.put("Button.border", new EmptyBorder(4, 16, 4, 16));
        table.put("Button.margin", getButtonMargin()); //MetouiaBorderUtilities.getButtonBorder());
        table.put("Button.border", new BasicBorders.MarginBorder()); //MetouiaBorderUtilities.getButtonBorder());
        table.put("ToggleButton.margin", getButtonMargin()); //, MetouiaBorderUtilities.getButtonBorder());
        table.put("ToggleButton.border", new BasicBorders.MarginBorder()); //new BasicBorders.MarginBorder()); //, MetouiaBorderUtilities.getButtonBorder());
        table.put("ToggleButton.background", table.get("window"));
        table.put("TextField.border", new LiquidTextFieldBorder());
        table.put("PasswordField.border", new LiquidTextFieldBorder());
        table.put("Spinner.border",
            new LiquidTextFieldBorder(new Insets(2, 2, 2, 2)));

        //border=new EmptyBorder(2,11,2,2);
        table.put("ToolBar.background", table.get("window"));
        table.put("MenuBar.background", table.get("window"));
        border = new EmptyBorder(2, 2, 2, 2);
        table.put("InternalFrame.border", border);
        table.put("InternalFrame.paletteBorder", border);
        table.put("InternalFrame.optionDialogBorder",
            new LiquidInternalFrameBorder());
//        table.put("DesktopIcon.border",
//            new LiquidInternalFrameBorder());
//        table.put("DesktopIcon.width", new Integer(160));

        //    table.put("InternalFrame.paletteBorder", new MetouiaPaletteBorder());
        //    table.put("InternalFrame.optionDialogBorder", new MetouiaOptionDialogBorder());
        border = new EmptyBorder(3, 1, 3, 1);
        table.put("Menu.border", border);
        table.put("MenuItem.border", border);
        table.put("CheckBoxMenuItem.border", border);
        table.put("RadioButtonMenuItem.border", border);
        table.put("CheckBoxMenuItem.checkIcon", loadIcon("menucheck.png", this));
        table.put("CheckBoxMenuItem.checkedIcon",
            loadIcon("menuchecked.png", this));
        table.put("RadioButtonMenuItem.checkIcon",
            loadIcon("menuradio.png", this));
        table.put("RadioButtonMenuItem.checkedIcon",
            loadIcon("menuradio_down.png", this));
        table.put("MenuItem.arrowIcon", loadIcon("menuarrow.png", this));
        table.put("MenuItem.selArrowIcon", loadIcon("menuarrowsel.png", this));
        table.put("Menu.submenuPopupOffsetX", new Integer(-3));
        table.put("Menu.submenuPopupOffsetY", new Integer(4));
        border = new LiquidPopupMenuBorder();
        table.put("PopupMenu.border", border);

        // Tweak some subtle values:
        table.put("SplitPane.dividerSize", new Integer(7));
        table.put("InternalFrame.paletteTitleHeight", new Integer(18));
        table.put("InternalFrame.frameTitleHeight", new Integer(24));
        table.put("InternalFrame.paletteTitleFont", getFont(Font.PLAIN, 12));
        table.put("InternalFrame.titleFont", getFont(Font.BOLD, 13));
        table.put("InternalFrame.pantherTitleFont", getFont(Font.BOLD, 13));
        table.put("Panel.background", table.get("window"));

        table.put("TabbedPane.selectedTabPadInsets", new Insets(1, 1, 1, 1)); // OK
        table.put("TabbedPane.tabAreaInsets", new Insets(4, 2, 0, 0)); // OK
        table.put("TabbedPane.contentBorderInsets", new Insets(5, 0, 0, 0)); // OK
        table.put("TabbedPane.unselected", table.get("shadow"));
        table.put("TabbedPane.tabsOverlapBorder", Boolean.TRUE);
        
        table.put("Checkbox.select", table.get("shadow"));
        table.put("PopupMenu.background", table.get("window"));
        table.put("PopupMenu.foreground", Color.black);

        table.put("TextField.selectionForeground",
            table.get("textHighlightText"));
        table.put("TextField.selectionBackground", table.get("textHighlight"));
        table.put("TextField.background", table.get("text"));
        table.put("TextField.disabledBackground", table.get("window"));

        table.put("TextField.focusInputMap", fieldInputMap);
        table.put("PasswordField.focusInputMap", fieldInputMap);
        table.put("TextArea.focusInputMap", multilineInputMap);
        table.put("TextPane.focusInputMap", multilineInputMap);
        table.put("TextPane.background", table.get("text"));
        table.put("EditorPane.focusInputMap", multilineInputMap);
        table.put("FormattedTextField.focusInputMap", formattedInputMap);

        table.put("List.background", table.get("text"));
        table.put("List.border", zeroEmptyBorder);
        table.put("List.selectionForeground", table.get("textHighlightText"));
        table.put("List.selectionBackground", table.get("textHighlight"));
        table.put("List.focusCellHighlightBorder", focusCellHighlightBorder);

        table.put("ScrollPane.border", listBorder);

        table.put("ComboBox.border", new EmptyBorder(1, 1, 1, 1));
        table.put("ComboBox.foreground", table.get("textHighlightText"));
        table.put("ComboBox.background", table.get("text"));
        table.put("ComboBox.selectionForeground", table.get("textHighlightText"));
        table.put("ComboBox.selectionBackground", table.get("textHighlight"));

// *Start 20060201 evickroy - Added mappings for UP, DOWN, and SPACE        
        table.put("ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
                             "ESCAPE", "hidePopup",
                            "PAGE_UP", "pageUpPassThrough",
                          "PAGE_DOWN", "pageDownPassThrough",
                               "HOME", "homePassThrough",
                                "END", "endPassThrough",
                               "DOWN", "selectNext",
                            "KP_DOWN", "selectNext",
                           "alt DOWN", "togglePopup",
                        "alt KP_DOWN", "togglePopup",
                             "alt UP", "togglePopup",
                          "alt KP_UP", "togglePopup",
                              "SPACE", "spacePopup",
                             "ENTER", "enterPressed",
                                 "UP", "selectPrevious",
                              "KP_UP", "selectPrevious"
                  }));
// *End 20060201

        table.put("InternalFrame.paletteCloseIcon",
            loadIcon("closebutton.png", this));
        table.put("InternalFrame.closeIcon",
            loadWinButtonIcon("close.png", this));
        table.put("InternalFrame.maximizeIcon",
            loadWinButtonIcon("maximize.png", this));
        table.put("InternalFrame.iconifyIcon",
            loadWinButtonIcon("minimize.png", this));
        table.put("InternalFrame.minimizeIcon",
            loadWinButtonIcon("restore.png", this));
        table.put("InternalFrame.icon", loadIcon("internalframeicon.png", this));
        table.put("InternalFrame.pantherFrameIcon", null);
            //loadIcon("panther-blue.png", this));
        table.put("InternalFrame.pantherIcon", null);
            //loadIcon("panther-blue.png", this));
        table.put("InternalFrame.pantherIconInactive", null);
            //loadIcon("panther-gray.png", this));

        // JTable defaults
        table.put("Table.background", table.get("text"));
        table.put("Table.foreground", table.get("controlText"));
        table.put("Table.selectionBackground", table.get("textHighlight"));
        table.put("Table.selectionForeground", table.get("textHighlightText"));
        table.put("Table.focusCellBackground", table.get("textHighlight"));
        table.put("Table.focusCellForeground", table.get("textHighlightText"));
        table.put("Table.focusCellHighlightBorder", focusCellHighlightBorder);
        table.put("Table.scrollPaneBorder", listBorder);

        table.put("TableHeader.font", getFont(Font.PLAIN, 12));
        table.put("TableHeader.foreground", table.get("textText"));
        table.put("TableHeader.background", table.get("window"));
        table.put("TableHeader.cellBorder", zeroEmptyBorder);

        table.put("ToolTip.background", new Color(255, 255, 225));
        table.put("ToolTip.foreground", new Color(0, 0, 0));
        table.put("ToolTip.font", getFont(Font.PLAIN, 12));
        table.put("ToolTip.border",
            new CompoundBorder(new LineBorder(Color.black, 1),
                new EmptyBorder(2, 2, 2, 2)));

        table.put("Tree.font", getFont(Font.PLAIN, 12));
        table.put("Tree.selectionForeground", table.get("textHighlightText"));
        table.put("Tree.selectionBackground", table.get("textHighlight"));
        table.put("Tree.foreground", table.get("textText"));
        table.put("Tree.background", table.get("text"));
        table.put("Tree.hash", table.get("text"));
        table.put("Tree.expandedIcon", loadIcon("treeminus.png", this));
        table.put("Tree.collapsedIcon", loadIcon("treeplus.png", this));
        table.put("Tree.openIcon", loadIcon("treefolderopened.png", this));
        table.put("Tree.closedIcon", loadIcon("treefolderclosed.png", this));
        table.put("Tree.leafIcon", loadIcon("treeleaf.png", this));
        table.put("Tree.rowHeight", new Integer(18));
        table.put("Tree.selectionBorderColor", new Color(86, 46, 0));

        table.put("SplitPane.background", table.get("text"));
        table.put("SplitPane.border", listBorder);
        table.put("SplitPaneDivider.border", zeroEmptyBorder);

        table.put("FileView.directoryIcon",
            loadIcon("treefolderclosed.png", this));
        table.put("FileView.computerIcon", loadIcon("computericon.png", this));
        table.put("FileView.fileIcon", loadIcon("document.png", this));
        table.put("FileView.floppyDriveIcon", loadIcon("floppy.png", this));
        table.put("FileView.hardDriveIcon", loadIcon("harddisk.png", this));

        table.put("FileChooser.detailsViewIcon",
            loadIcon("filedetails.png", this));
        table.put("FileChooser.homeFolderIcon",
            loadIcon("desktopicon.png", this));
        table.put("FileChooser.listViewIcon", loadIcon("filelist.png", this));
        table.put("FileChooser.newFolderIcon", loadIcon("newfolder.png", this));
        table.put("FileChooser.upFolderIcon",
            loadIcon("parentdirectory.png", this));
        table.put("FileChooser.upFolderIcon",
            loadIcon("parentdirectory.png", this));

        table.put("OptionPane.errorIcon", loadIcon("error.png", this));
        table.put("OptionPane.informationIcon",
            loadIcon("information.png", this));
        table.put("OptionPane.warningIcon", loadIcon("warning.png", this));
        table.put("OptionPane.questionIcon", loadIcon("question.png", this));

        table.put("RootPane.colorChooserDialogBorder",
            LiquidFrameBorder.getInstance());
        table.put("RootPane.errorDialogBorder", LiquidFrameBorder.getInstance());
        table.put("RootPane.fileChooserDialogBorder",
            LiquidFrameBorder.getInstance());
        table.put("RootPane.frameBorder", LiquidFrameBorder.getInstance());
        table.put("RootPane.informationDialogBorder",
            LiquidFrameBorder.getInstance());
        table.put("RootPane.plainDialogBorder", LiquidFrameBorder.getInstance());
        table.put("RootPane.questionDialogBorder",
            LiquidFrameBorder.getInstance());
        table.put("RootPane.warningDialogBorder",
            LiquidFrameBorder.getInstance());

// ProgressBar defaults        
        table.put("ProgressBar.horizontalSize", new Dimension(146, 14));
        table.put("ProgressBar.verticalSize", new Dimension(14, 146));
        table.put("ProgressBar.font", getFont(Font.PLAIN, 12));
		
//        table.put("ProgressBar.repaintInterval", new Integer(200));
//        table.put("ProgressBar.cycleTime", new Integer(5600));
    }

    /**
     * Loads an image icon.
     *
     * @param file The image file name.
     * @param invoker The refence of the invoking class, whose classloader will be
     *                used for loading the image.
     */
    public static ImageIcon loadIcon(final String file, final Object invoker) {
        // When we return an IconUIResource the disabled icon of a file choose won't be painted greyed out,
        // thus we simply stay with an icon
        return loadIconImmediately(file, invoker);
    }

    public static ImageIcon loadWinButtonIcon(String file, final Object invoker) {
        // When we return an IconUIResource the disabled icon of a file choose won't be painted greyed out,
        // thus we simply stay with an icon
        if (LiquidLookAndFeel.winDecoPanther) {
            file = "panther-" + file;
        }

        return loadIconImmediately(file, invoker);
    }

    /**
     * Loads an image icon immediately.
     *
     * @param file The image file name.
     * @param invoker The refence of the invoking class, whose classloader will be
     *                used for loading the image.
     */
    public static ImageIcon loadIconImmediately(String file, Object invoker) {
        try {
            Image img = SkinImageCache.getInstance().getImage(file);
            ImageIcon icon = new ImageIcon(img);

            /*Toolkit.getDefaultToolkit().createImage(
              readStream(invoker.getClass().getResourceAsStream(file))));
             */
            if (icon.getIconWidth() <= 0) {
                System.out.println("******************** File " + file +
                    " not found. Exiting");
                System.exit(1);
            }

            return icon;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("Error getting resource " + file);

            return null;
        }
    }

    protected Font getFont(int type, int size) {
        return new Font(fontName, type, size);
    }

    private InsetsUIResource getButtonMargin() {
        //int screenResolution = awtToolkit.getScreenResolution();
        //if (screenResolution < 100) {
        //    return new InsetsUIResource(2, 16, 2, 16);
        //}
        return new InsetsUIResource(4, 16, 4, 16);
    }

    public static Color getLightControl() {
        return (Color) uiDefaults.get("control");
    }

    /**
     * used for SliderUI Ticks
     */
    public static Color getDarkControl() {
        return new Color(127, 127, 127);
    }

    public static Color getBackgroundColor() {
//        return (Color) uiDefaults.get("window");
          return new Color( 200,240,200).brighter();
    }

    public static Color getDesktopColor() {
        return (Color) uiDefaults.get("desktop");
    }

    protected static Color getWindowTitleInactiveForeground() {
        return (Color) uiDefaults.get("inactiveCaptionText");
    }

    public static Color getWindowBackground() {
        return (Color) uiDefaults.get("window");
    }

    public static Color getButtonBackground() {
//        return new Color( 200,240,200);
        return (Color) uiDefaults.get("controlHighlight");
    }

    /**
     * @deprecated Table rows are not only gui elements
     * which use different colors for row background.
     *
     */
    public static void setDefaultTableBackgroundMode(boolean b) {
        setDefaultRowBackgroundMode(b);
    }

    /**
     * This method will be used for setting background
     * mode of gui elements which have rows (tables, lists, combos...).
     *
     */
    public static void setDefaultRowBackgroundMode(boolean b) {
        defaultRowBackgroundMode = b;

        if (!b) {
            showTableGrids = true;
        }
    }

    /**
     * This method will be used for setting windows decorations
     * If true, then defaults to Liquid decorations for internal frames.
     *
     * @param useWinDeco Indicates if custom Window decorations should be painted 
     *        or should the system decorate the Windows.
     */
    public static void setLiquidDecorations(boolean useWinDeco) {
        setLiquidDecorations(useWinDeco, "liquid");
    }

    /**
     * Overloaded method will be used for setting windows and internal frames decorations.
     * If true, then defaults to Liquid decorations for internal frames.
     *
     * @param useWinDeco Indicates if custom Window decorations should be painted 
     *        or should the system decorate the Windows.
     * @param internalFrameType The type of decorations to use for JInternalFrames.
     *        Values are "mac" or "panther" = Mac OSX style decorations or "liquid"
     *        for custom Liquid style decorations. 
     */
    public static void setLiquidDecorations(boolean useWinDeco, String internalFrameType) {
        javax.swing.JFrame.setDefaultLookAndFeelDecorated(useWinDeco);
        javax.swing.JDialog.setDefaultLookAndFeelDecorated(useWinDeco);

        
        if (internalFrameType.equalsIgnoreCase("panther") || internalFrameType.equalsIgnoreCase("mac")) {
            winDecoPanther = true;
        }
        else if(internalFrameType.equalsIgnoreCase("liquid")) {
            winDecoPanther = false;
        }
        
// We may need to reload these InternalFrame Icons depending on the decorations style
        UIManager.put("InternalFrame.closeIcon",
            loadWinButtonIcon("close.png", null));
        UIManager.put("InternalFrame.maximizeIcon",
            loadWinButtonIcon("maximize.png", null));
        UIManager.put("InternalFrame.iconifyIcon",
            loadWinButtonIcon("minimize.png", null));
        UIManager.put("InternalFrame.minimizeIcon",
            loadWinButtonIcon("restore.png", null));
    }

    protected static boolean areStipplesUsed() {
        return bgStipples; 
    }

    public static void setStipples(boolean b) {
        bgStipples = b;
    }

    /**
     * @param showTableGrids The showTableGrids to set.
     */
    public static void setShowTableGrids(boolean showTableGrids) {
        LiquidLookAndFeel.showTableGrids = showTableGrids;
    }

    /**
     * @param autoTransparency The autoTransparency to set.
     */
    public static void setPanelTransparency(boolean autoTransparency) {
        LiquidLookAndFeel.panelTransparency = autoTransparency;
    }

    /**
     * @param flatedButtons The flatedButtons to set.
     */
    public static void setToolbarButtonsFocusable(boolean focusableButtons) {
        LiquidLookAndFeel.toolbarButtonsFocusable = focusableButtons;
    }

    /**
     * @param flatedButtons The flatedButtons to set.
     */
    public static void setToolbarFlattedButtons(boolean flatedButtons) {
        LiquidLookAndFeel.toolbarFlattedButtons = flatedButtons;
    }

    /**
     * @param flatedButtons The flatedButtons to set.
     * @param paintRollover For flatedButtons, should we paint the rollover effect?
     */
    public static void setToolbarFlattedButtons(boolean flatedButtons, boolean paintRollover) {
        LiquidLookAndFeel.setToolbarFlattedButtons(flatedButtons);
        LiquidLookAndFeel.setToolbarFlattedButtonsRollover(paintRollover);
    }

    /**
     * @param paintRollover For flatedButtons, should we paint the rollover effect?
     */
    public static void setToolbarFlattedButtonsRollover(boolean paintRollover) {
        LiquidLookAndFeel.toolbarFlattedButtonsRollover = paintRollover;
    }

    /**
     * Invoked during <code>UIManager#setLookAndFeel</code>. In addition to
     * the superclass behavior, we install the ShadowPopupFactory.
     * 
     * @see #uninitialize
     */
    public void initialize() {
            super.initialize();

// *Start LAF-PLUGIN SUPPORT*
            pluginManager.initializeAll();
// *End LAF-PLUGIN SUPPORT*
    }

    /**
     * @see #initialize
     */
    public void uninitialize() {
            super.uninitialize();

// *Start LAF-PLUGIN SUPPORT*
            pluginManager.uninitializeAll();
// *End LAF-PLUGIN SUPPORT*
    }

}
