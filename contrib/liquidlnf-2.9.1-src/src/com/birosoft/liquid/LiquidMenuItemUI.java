/*
 *
 * This is an almost unchanged version of MetalMenuItemUI.
 *
 *
 * @(#)BasicFileChooserUI.java  1.45 02/04/11
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.birosoft.liquid;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinMenuItem;
import com.birosoft.liquid.util.Colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;


/**
 * LiquidMenuItemUI implementation
 *
 * @version 1.115 12/03/01
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 * @author Fredrik Lagerblad
 */
public class LiquidMenuItemUI extends MenuItemUI {
    protected static int defaultTextIconGap;
    protected static int defaultIconGap;

    /* diagnostic aids -- should be false for production builds. */
    private static final boolean TRACE = false; // trace creates and disposes
    private static final boolean VERBOSE = false; // show reuse hits/misses
    private static final boolean DEBUG = false; // show bad params, misc.

    /* Client Property keys for text and accelerator text widths */
    static final String MAX_TEXT_WIDTH = "maxTextWidth";
    static final String MAX_ACC_WIDTH = "maxAccWidth";
    static final String MAX_ICON_WIDTH = "maxIconWidth";
    static SkinMenuItem skin = new SkinMenuItem("menu.png", 0, 1, 2, 3, 4);
    static Skin topSkin = new Skin("menu_top.png", 3, 4, 10, 4, 11);

    // these rects are used for painting and preferredsize calculations.
    // they used to be regenerated constantly.  Now they are reused.
    static Rectangle zeroRect = new Rectangle(0, 0, 0, 0);
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
    static Rectangle r = new Rectangle();
    protected JMenuItem menuItem = null;
    protected Color selectionBackground;
    protected Color selectionForeground;
    protected Color disabledForeground;
    protected Color acceleratorForeground;
    protected Color acceleratorSelectionForeground;
    private String acceleratorDelimiter;
    protected Font acceleratorFont;
    protected MouseInputListener mouseInputListener;
    protected MenuDragMouseListener menuDragMouseListener;
    protected MenuKeyListener menuKeyListener;
    private PropertyChangeListener propertyChangeListener;
    protected Icon arrowIcon = null;
    protected Icon selArrowIcon = null;
    protected Icon checkIcon = null;
    protected boolean oldBorderPainted;

    /** Used for accelerator binding, lazily created. */
    InputMap windowInputMap;

    public void installUI(JComponent c) {
        menuItem = (JMenuItem) c;

        installDefaults();
        installComponents(menuItem);
        installListeners();
        installKeyboardActions();
    }

    /**
     * @since 1.3
     */
    protected void installComponents(JMenuItem menuItem) {
        BasicHTML.updateRenderer(menuItem, menuItem.getText());
    }

    protected String getPropertyPrefix() {
        return "MenuItem";
    }

    protected void installListeners() {
        if ((mouseInputListener = createMouseInputListener(menuItem)) != null) {
            menuItem.addMouseListener(mouseInputListener);
            menuItem.addMouseMotionListener(mouseInputListener);
        }

        if ((menuDragMouseListener = createMenuDragMouseListener(menuItem)) != null) {
            menuItem.addMenuDragMouseListener(menuDragMouseListener);
        }

        if ((menuKeyListener = createMenuKeyListener(menuItem)) != null) {
            menuItem.addMenuKeyListener(menuKeyListener);
        }

        if ((propertyChangeListener = createPropertyChangeListener(menuItem)) != null) {
            menuItem.addPropertyChangeListener(propertyChangeListener);
        }
    }

    protected void installKeyboardActions() {
        ActionMap actionMap = getActionMap();

        SwingUtilities.replaceUIActionMap(menuItem, actionMap);
        updateAcceleratorBinding();
    }

    public void uninstallUI(JComponent c) {
        menuItem = (JMenuItem) c;
        uninstallDefaults();
        uninstallComponents(menuItem);
        uninstallListeners();
        uninstallKeyboardActions();

        //Remove the textWidth and accWidth values from the parent's Client Properties.
        Container parent = menuItem.getParent();

        if (((parent != null) && parent instanceof JComponent) &&
                !(menuItem instanceof JMenu &&
                ((JMenu) menuItem).isTopLevelMenu())) {
            JComponent p = (JComponent) parent;
            p.putClientProperty(MAX_ACC_WIDTH, null);
            p.putClientProperty(MAX_TEXT_WIDTH, null);
        }

        menuItem = null;
    }

    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(menuItem);
        menuItem.setBorderPainted(oldBorderPainted);

        if (menuItem.getMargin() instanceof UIResource) {
            menuItem.setMargin(null);
        }

        if (arrowIcon instanceof UIResource) {
            arrowIcon = null;
        }

        if (selArrowIcon instanceof UIResource) {
            selArrowIcon = null;
        }

        if (checkIcon instanceof UIResource) {
            checkIcon = null;
        }
    }

    /**
     * @since 1.3
     */
    protected void uninstallComponents(JMenuItem menuItem) {
        BasicHTML.updateRenderer(menuItem, "");
    }

    protected void uninstallListeners() {
        if (mouseInputListener != null) {
            menuItem.removeMouseListener(mouseInputListener);
            menuItem.removeMouseMotionListener(mouseInputListener);
        }

        if (menuDragMouseListener != null) {
            menuItem.removeMenuDragMouseListener(menuDragMouseListener);
        }

        if (menuKeyListener != null) {
            menuItem.removeMenuKeyListener(menuKeyListener);
        }

        if (propertyChangeListener != null) {
            menuItem.removePropertyChangeListener(propertyChangeListener);
        }

        mouseInputListener = null;
        menuDragMouseListener = null;
        menuKeyListener = null;
        propertyChangeListener = null;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(menuItem, null);

        if (windowInputMap != null) {
            SwingUtilities.replaceUIInputMap(menuItem,
                JComponent.WHEN_IN_FOCUSED_WINDOW, null);
            windowInputMap = null;
        }
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
        return new MouseInputHandler();
    }

    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
        return new MenuDragMouseHandler();
    }

    protected MenuKeyListener createMenuKeyListener(JComponent c) {
        return new MenuKeyHandler();
    }

    private PropertyChangeListener createPropertyChangeListener(JComponent c) {
        return new PropertyChangeHandler();
    }

    ActionMap getActionMap() {
        String propertyPrefix = getPropertyPrefix();
        String uiKey = propertyPrefix + ".actionMap";
        ActionMap am = (ActionMap) UIManager.get(uiKey);

        if (am == null) {
            am = createActionMap();
            UIManager.getLookAndFeelDefaults().put(uiKey, am);
        }

        return am;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        map.put("doClick", new ClickAction());

        // Set the ActionMap's parent to the Auditory Feedback Action Map
        //		BasicLookAndFeel lf = (BasicLookAndFeel) UIManager.getLookAndFeel();
        //		ActionMap audioMap = lf.getAudioActionMap();
        //		map.setParent(audioMap);
        return map;
    }

    InputMap createInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            return new ComponentInputMapUIResource(menuItem);
        }

        return null;
    }

    void updateAcceleratorBinding() {
        KeyStroke accelerator = menuItem.getAccelerator();

        if (windowInputMap != null) {
            windowInputMap.clear();
        }

        if (accelerator != null) {
            if (windowInputMap == null) {
                windowInputMap = createInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                SwingUtilities.replaceUIInputMap(menuItem,
                    JComponent.WHEN_IN_FOCUSED_WINDOW, windowInputMap);
            }

            windowInputMap.put(accelerator, "doClick");
        }
    }

    public Dimension getMinimumSize(JComponent c) {
        Dimension d = null;
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);

        if (v != null) {
            d = getPreferredSize(c);
            d.width -= (v.getPreferredSpan(View.X_AXIS) -
            v.getMinimumSpan(View.X_AXIS));
        }

        return d;
    }

    public Dimension getPreferredSize(JComponent c) {
        return getPreferredMenuItemSize(c, checkIcon, arrowIcon,
            defaultTextIconGap);
    }

    /**
     * Renders the text of the current menu item.
     * <p>
     * @param g graphics context
     * @param menuItem menu item to render
     * @param textRect bounding rectangle for rendering the text
     * @param text string to render
     * @since 1.4
     */
    protected void paintText(Graphics g, JMenuItem menuItem,
        Rectangle textRect, String text) {
        ButtonModel model = menuItem.getModel();
        FontMetrics fm = g.getFontMetrics();
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        if (!model.isEnabled()) {
            // *** paint the text disabled
            if (UIManager.get("MenuItem.disabledForeground") instanceof Color) {
                g.setColor(UIManager.getColor("MenuItem.disabledForeground"));
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
                    mnemIndex, textRect.x, (textRect.y + fm.getAscent()) - 1);
            } else {
                g.setColor(menuItem.getBackground().brighter());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
                    mnemIndex, textRect.x, (textRect.y + fm.getAscent()) - 1);
                g.setColor(menuItem.getBackground().darker());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
                    mnemIndex, textRect.x - 1, (textRect.y + fm.getAscent()) -
                    2);
            }
        } else {
            // *** paint the text normally
            if (model.isArmed() ||
                    (menuItem instanceof JMenu && model.isSelected())) {
                g.setColor(selectionForeground); // Uses protected field.
            }

            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemIndex,
                textRect.x, (textRect.y + fm.getAscent()) - 1);
        }
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension d = null;
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);

        if (v != null) {
            d = getPreferredSize(c);
            d.width += (v.getMaximumSpan(View.X_AXIS) -
            v.getPreferredSpan(View.X_AXIS));
        }

        return d;
    }

    private void resetRects() {
        iconRect.setBounds(zeroRect);
        textRect.setBounds(zeroRect);
        acceleratorRect.setBounds(zeroRect);
        checkIconRect.setBounds(zeroRect);
        arrowIconRect.setBounds(zeroRect);
        viewRect.setBounds(0, 0, Short.MAX_VALUE, Short.MAX_VALUE);
        r.setBounds(zeroRect);
    }

    /**
     * We draw the background in paintMenuItem()
     * so override update (which fills the background of opaque
     * components by default) to just call paint().
     *
     */
    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }

    public void paint(Graphics g, JComponent c) {
        paintMenuItem(g, c, checkIcon, arrowIcon, selectionBackground,
            selectionForeground, defaultTextIconGap);
    }

    /*
     * Returns false if the component is a JMenu and it is a top
     * level menu (on the menubar).
     */
    private boolean useCheckAndArrow() {
        boolean b = true;

        if ((menuItem instanceof JMenu) &&
                (((JMenu) menuItem).isTopLevelMenu())) {
            b = false;
        }

        return b;
    }

    public MenuElement[] getPath() {
        MenuSelectionManager m = MenuSelectionManager.defaultManager();
        MenuElement[] oldPath = m.getSelectedPath();
        MenuElement[] newPath;
        int i = oldPath.length;

        if (i == 0) {
            return new MenuElement[0];
        }

        Component parent = menuItem.getParent();

        if (oldPath[i - 1].getComponent() == parent) {
            // The parent popup menu is the last so far
            newPath = new MenuElement[i + 1];
            System.arraycopy(oldPath, 0, newPath, 0, i);
            newPath[i] = menuItem;
        } else {
            // A sibling menuitem is the current selection
            //
            //  This probably needs to handle 'exit submenu into
            // a menu item.  Search backwards along the current
            // selection until you find the parent popup menu,
            // then copy up to that and add yourself...
            int j;

            for (j = oldPath.length - 1; j >= 0; j--) {
                if (oldPath[j].getComponent() == parent) {
                    break;
                }
            }

            newPath = new MenuElement[j + 2];
            System.arraycopy(oldPath, 0, newPath, 0, j + 1);
            newPath[j + 1] = menuItem;
        }

        return newPath;
    }

    /**
     * Call this method when a menu item is to be activated.
     * This method handles some of the details of menu item activation
     * such as clearing the selected path and messaging the
     * JMenuItem's doClick() method.
     *
     * @param msm  A MenuSelectionManager. The visual feedback and
     *             internal bookkeeping tasks are delegated to
     *             this MenuSelectionManager. If <code>null</code> is
     *             passed as this argument, the
     *             <code>MenuSelectionManager.defaultManager</code> is
     *             used.
     * @see MenuSelectionManager
     * @see JMenuItem#doClick(int)
     * @since 1.4
     */
    protected void doClick(MenuSelectionManager msm) {
        // Visual feedback
        if (msm == null) {
            msm = MenuSelectionManager.defaultManager();
        }

        msm.clearSelectedPath();
        menuItem.doClick(0);
    }

    /**
     * This is to see if the menu item in question is part of the
     * system menu on an internal frame.
     * The Strings that are being checked can be found in
     * MetalInternalFrameTitlePaneUI.java,
     * WindowsInternalFrameTitlePaneUI.java, and
     * MotifInternalFrameTitlePaneUI.java.
     *
     * @since 1.4
     */
    private boolean isInternalFrameSystemMenu() {
        String actionCommand = menuItem.getActionCommand();

        if ((actionCommand == "Close") || (actionCommand == "Minimize") ||
                (actionCommand == "Restore") || (actionCommand == "Maximize")) {
            return true;
        } else {
            return false;
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return new LiquidMenuItemUI();
    }

    protected void installDefaults() {
        String prefix = getPropertyPrefix();

        acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");

        menuItem.setOpaque(true);

        if ((menuItem.getMargin() == null) ||
                (menuItem.getMargin() instanceof UIResource)) {
            menuItem.setMargin(UIManager.getInsets(prefix + ".margin"));
        }

        defaultTextIconGap = 4; // Should be from table

        LookAndFeel.installBorder(menuItem, prefix + ".border");
        oldBorderPainted = menuItem.isBorderPainted();
        menuItem.setBorderPainted(((Boolean) (UIManager.get(prefix +
                ".borderPainted"))).booleanValue());
        LookAndFeel.installColorsAndFont(menuItem, prefix + ".background",
            prefix + ".foreground", prefix + ".font");

        // MenuItem specific defaults
        if ((selectionBackground == null) ||
                selectionBackground instanceof UIResource) {
            selectionBackground = UIManager.getColor(prefix +
                    ".selectionBackground");
        }

        if ((selectionForeground == null) ||
                selectionForeground instanceof UIResource) {
            selectionForeground = UIManager.getColor(prefix +
                    ".selectionForeground");
        }

        if ((disabledForeground == null) ||
                disabledForeground instanceof UIResource) {
            disabledForeground = UIManager.getColor(prefix +
                    ".disabledForeground");
        }

        if ((acceleratorForeground == null) ||
                acceleratorForeground instanceof UIResource) {
            acceleratorForeground = UIManager.getColor(prefix +
                    ".acceleratorForeground");
        }

        if ((acceleratorSelectionForeground == null) ||
                acceleratorSelectionForeground instanceof UIResource) {
            acceleratorSelectionForeground = UIManager.getColor(prefix +
                    ".acceleratorSelectionForeground");
        }

        // Get accelerator delimiter
        acceleratorDelimiter = UIManager.getString(
                "MenuItem.acceleratorDelimiter");

        if (acceleratorDelimiter == null) {
            acceleratorDelimiter = "+";
        }

        // Icons
        if (useCheckAndArrow()) {
            if ((arrowIcon == null) || arrowIcon instanceof UIResource) {
                arrowIcon = UIManager.getIcon(prefix + ".arrowIcon");
            }

            if ((checkIcon == null) || checkIcon instanceof UIResource) {
                checkIcon = UIManager.getIcon(prefix + ".checkIcon");
            }
        }

        // Changed:
        defaultTextIconGap = 8; // Should be from table
        defaultIconGap = 4; // Should be from table
    }

    protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon,
        Icon arrowIcon, int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        Icon icon = (Icon) b.getIcon();
        String text = b.getText();
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();

            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);

                //acceleratorText += "-";
                acceleratorText += acceleratorDelimiter;
            }

            int keyCode = accelerator.getKeyCode();

            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            } else {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);
        FontMetrics fmAccel = b.getFontMetrics(acceleratorFont);

        resetRects();

        layoutMenuItem(fm, text, fmAccel, acceleratorText, icon, checkIcon,
            arrowIcon, b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, acceleratorRect, checkIconRect,
            arrowIconRect, (text == null) ? 0 : defaultTextIconGap,
            defaultIconGap);

        // find the union of the icon and text rects
        r.setBounds(textRect);
        r = SwingUtilities.computeUnion(iconRect.x, iconRect.y, iconRect.width,
                iconRect.height, r);

        //r = iconRect.union(textRect);
        // To make the accelerator texts appear in a column, find the widest MenuItem text
        // and the widest accelerator text.
        //Get the parent, which stores the information.
        Container parent = menuItem.getParent();

        //Check the parent, and see that it is not a top-level menu.
        if ((parent != null) && parent instanceof JComponent &&
                !(menuItem instanceof JMenu &&
                ((JMenu) menuItem).isTopLevelMenu())) {
            JComponent p = (JComponent) parent;

            //Get widest text so far from parent, if no one exists null is returned.
            Integer maxTextWidth = (Integer) p.getClientProperty(MAX_TEXT_WIDTH);
            Integer maxAccWidth = (Integer) p.getClientProperty(MAX_ACC_WIDTH);
            Integer maxIconWidth = (Integer) p.getClientProperty(MAX_ICON_WIDTH); // WAS ME

            int maxTextValue = (maxTextWidth != null) ? maxTextWidth.intValue()
                                                      : 0;
            int maxAccValue = (maxAccWidth != null) ? maxAccWidth.intValue() : 0;
            int maxIconValue = (maxIconWidth != null) ? maxIconWidth.intValue()
                                                      : 0;

            //Compare the text widths, and adjust the r.width to the widest.
            if (r.width < maxTextValue) {
                r.width = maxTextValue;
            } else {
                p.putClientProperty(LiquidMenuItemUI.MAX_TEXT_WIDTH,
                    new Integer(r.width));
            }

            //Compare the accelarator widths.
            if (acceleratorRect.width > maxAccValue) {
                maxAccValue = acceleratorRect.width;
                p.putClientProperty(LiquidMenuItemUI.MAX_ACC_WIDTH,
                    new Integer(acceleratorRect.width));
            }

            //Compare the accelarator widths.
            if ((icon != null) && (icon.getIconWidth() > maxIconValue)) {
                maxIconValue = icon.getIconWidth();
                p.putClientProperty(LiquidMenuItemUI.MAX_ICON_WIDTH,
                    new Integer(maxIconValue));
            }

            //Add on the widest accelerator
            r.width += maxAccValue;
            r.width += defaultTextIconGap;
        }

        if (useCheckAndArrow()) {
            // Add in the checkIcon
            r.width += checkIconRect.width;
            r.width += defaultTextIconGap;

            // Add in the arrowIcon
            r.width += defaultTextIconGap;
            r.width += arrowIconRect.width;
        }

        r.width += defaultTextIconGap;

        Insets insets = b.getInsets();

        if (insets != null) {
            r.width += (insets.left + insets.right);
            r.height += (insets.top + insets.bottom);
        }

        // if the width is even, bump it up one. This is critical
        // for the focus dash line to draw properly
        if ((r.width % 2) == 0) {
            r.width++;
        }

        // if the height is even, bump it up one. This is critical
        // for the text to center properly
        if ((r.height % 2) != 0) {
            r.height--;
        }

        // If all regular (use regular text and icons of size 16x16)
        // then height of menuitem must not be larger then 20
        if (r.height < 24) {
            r.height = 20;
        }

        return r.getSize();
    }

    protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon,
        Icon arrowIcon, Color background, Color foreground,
        int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();

        Icon bicon = b.getIcon();

        JComponent p = (JComponent) b.getParent();
        Integer maxValueInt = (Integer) p.getClientProperty(LiquidMenuItemUI.MAX_ICON_WIDTH);
        int maxValue = (maxValueInt == null) ? 16 : maxValueInt.intValue();

        int menuWidth = b.getWidth();
        int menuHeight = b.getHeight();
        Insets i = c.getInsets();

        resetRects();

        viewRect.setBounds(0, 0, menuWidth, menuHeight);

        viewRect.x += 0;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);

        Font holdf = g.getFont();
        Font f = c.getFont();
        g.setFont(f);

        FontMetrics fm = g.getFontMetrics(f);
        FontMetrics fmAccel = g.getFontMetrics(acceleratorFont);

        // get Accelerator text
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();

            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);

                //acceleratorText += "-";
                acceleratorText += acceleratorDelimiter;
            }

            int keyCode = accelerator.getKeyCode();

            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            } else {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        int offset = 0;

        // layout the text and icon
        Icon ic = b.getIcon();
        Icon iCheck = checkIcon;
        Icon paintIcon = ic;

        if (useCheckAndArrow()) {
            if (c instanceof JCheckBoxMenuItem ||
                    c instanceof JRadioButtonMenuItem) {
                ic = checkIcon;

                if (checkIcon.getIconWidth() < maxValue) {
                    ic = new EmptyIcon(maxValue, checkIcon.getIconHeight());
                    offset = (maxValue - checkIcon.getIconWidth()) / 2;
                }

                paintIcon = null;
            } else if (c instanceof JMenuItem) {
                if ((ic == null) || (ic.getIconWidth() < maxValue)) {
                    int height = (ic == null) ? 2 : b.getIcon().getIconHeight();
                    int width = (ic == null) ? 2 : b.getIcon().getIconWidth();
                    offset = (maxValue - width) / 2;
                    ic = new EmptyIcon(maxValue, height);
                }
            }
        }

        String text = layoutMenuItem(fm, b.getText(), fmAccel, acceleratorText,
                ic, null /*iCheck*/, arrowIcon, b.getVerticalAlignment(),
                b.getHorizontalAlignment(), b.getVerticalTextPosition(),
                b.getHorizontalTextPosition(), viewRect, iconRect, textRect,
                acceleratorRect, checkIconRect, arrowIconRect,
                (b.getText() == null) ? 0 : defaultTextIconGap, defaultIconGap);

        // Paint background
        paintBackground(g, b, background);

        Color holdc = g.getColor();

        // Paint the Check
        if (checkIcon != null) {
            if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            } else {
                g.setColor(holdc);
            }

            if (useCheckAndArrow()) {
                if (c instanceof JCheckBoxMenuItem) {
                    if (model.isSelected()) {
                        UIManager.getIcon("CheckBoxMenuItem.checkedIcon")
                                 .paintIcon(c, g, (iconRect.x + offset) - 2,
                            iconRect.y);
                    } else {
                        checkIcon.paintIcon(c, g, (iconRect.x + offset) - 2,
                            iconRect.y);
                    }
                }

                if (c instanceof JRadioButtonMenuItem) {
                    if (model.isSelected()) {
                        UIManager.getIcon("RadioButtonMenuItem.checkedIcon")
                                 .paintIcon(c, g, iconRect.x + offset,
                            iconRect.y);
                    } else {
                        checkIcon.paintIcon(c, g, iconRect.x + offset,
                            iconRect.y);
                    }
                }
            }

            g.setColor(holdc);
        }

        // Paint the Icon
        if (paintIcon != null) {
            Icon icon;

            if (!model.isEnabled()) {
                icon = (Icon) b.getDisabledIcon();

                if (icon != null) {
                    icon.paintIcon(c, g, iconRect.x + offset, iconRect.y);
                }
            } else if (model.isPressed() && model.isArmed()) {
                icon = (Icon) b.getPressedIcon();

                if (icon == null) {
                    // Use default icon
                    icon = (Icon) b.getIcon();
                }

                if (icon != null) {
                    icon.paintIcon(c, g, iconRect.x + offset, iconRect.y);
                }
            } else if (model.isArmed() || model.isSelected()) {
                icon = (Icon) b.getIcon();

                if (icon != null) {
                    Icon disabled = b.getDisabledIcon();

                    if (disabled != null) {
                        disabled.paintIcon(c, g, iconRect.x + offset + 1,
                            iconRect.y + 1);
                    }

                    icon.paintIcon(c, g, (iconRect.x + offset) - 1,
                        iconRect.y - 1);
                }
            } else {
                icon = (Icon) b.getIcon();

                if (icon != null) {
                    icon.paintIcon(c, g, iconRect.x + offset, iconRect.y);
                }
            }
        }

        // Draw the Text
        if (text != null) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);

            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, b, textRect, text);
            }
        }

        // Draw the Accelerator Text
        if ((acceleratorText != null) && !acceleratorText.equals("")) {
            //Get the maxAccWidth from the parent to calculate the offset.
            int accOffset = 0;
            Container parent = menuItem.getParent();

            if ((parent != null) && parent instanceof JComponent) {
                Integer amaxValueInt = (Integer) p.getClientProperty(LiquidMenuItemUI.MAX_ACC_WIDTH);
                int amaxValue = (amaxValueInt != null)
                    ? amaxValueInt.intValue() : acceleratorRect.width;

                //Calculate the offset, with which the accelerator texts will be drawn with.
                accOffset = amaxValue - acceleratorRect.width;
            }

            g.setFont(acceleratorFont);

            if (!model.isEnabled()) {
                // *** paint the acceleratorText disabled
                if (disabledForeground != null) {
                    g.setColor(disabledForeground);
                    BasicGraphicsUtils.drawString(g, acceleratorText, 0,
                        acceleratorRect.x - accOffset,
                        acceleratorRect.y + fmAccel.getAscent());
                } else {
                    g.setColor(b.getBackground().brighter());
                    BasicGraphicsUtils.drawString(g, acceleratorText, 0,
                        acceleratorRect.x - accOffset,
                        acceleratorRect.y + fmAccel.getAscent());
                    g.setColor(b.getBackground().darker());
                    BasicGraphicsUtils.drawString(g, acceleratorText, 0,
                        acceleratorRect.x - accOffset - 1,
                        (acceleratorRect.y + fmAccel.getAscent()) - 1);
                }
            } else {
                // *** paint the acceleratorText normally
                if (model.isArmed() ||
                        (c instanceof JMenu && model.isSelected())) {
                    g.setColor(acceleratorSelectionForeground);
                } else {
                    g.setColor(acceleratorForeground);
                }

                BasicGraphicsUtils.drawString(g, acceleratorText, 0,
                    acceleratorRect.x - accOffset,
                    acceleratorRect.y + fmAccel.getAscent());
            }
        }

        // Paint the Arrow
        if ((arrowIcon != null) && c instanceof JMenu) {
            JMenu menu = null;

            if (c instanceof JMenu) {
                menu = (JMenu) c;
            }

            if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            }

            if (useCheckAndArrow()) // && menu.getItemCount()>0)// && menu.isEnabled())
             {
                if (model.isSelected()) {
                    UIManager.getIcon("MenuItem.selArrowIcon").paintIcon(c, g,
                        arrowIconRect.x, arrowIconRect.y);
                } else if (menu.isEnabled()) {
                    UIManager.getIcon("MenuItem.arrowIcon").paintIcon(c, g,
                        arrowIconRect.x, arrowIconRect.y);
                }
            }
        }

        g.setColor(holdc);
        g.setFont(holdf);
    }

    /**
     * Draws the background of the menu item.
     *
     * @param g the paint graphics
     * @param menuItem menu item to be painted
     * @param bgColor selection background color
     * @since 1.4
     */
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

        boolean mouseOver = model.isArmed() ||
            (menuItem instanceof JMenu && model.isSelected());

        int maxValue = menuWidth;

        g.setColor(LiquidLookAndFeel.getBackgroundColor());
        g.fillRect(0, 0, menuWidth, menuHeight);
        g.setColor(oldColor);

        if (useCheckAndArrow()) {
            JComponent p = (JComponent) menuItem.getParent();
            Integer maxValueInt = (Integer) p.getClientProperty(LiquidMenuItemUI.MAX_ICON_WIDTH);
            maxValue = (maxValueInt == null) ? 16 : maxValueInt.intValue();
            maxValue += defaultTextIconGap;
            skin.draw(g, model.isEnabled(), mouseOver, false, false, menuWidth,
                maxValue, menuHeight); // selected item

            if (!mouseOver) {
                Color old = g.getColor();

                if (LiquidLookAndFeel.areStipplesUsed()) {
                    Colors.drawStipples(g, menuItem, menuItem.getBackground());
                }

                g.setColor(old);
            }
        } else {
            boolean isRollover = (menuItem.getClientProperty("rollover") == Boolean.TRUE)
                ? true : false;

            int index = 2;

            if (!model.isEnabled()) {
                index = 2;
            } else if (model.isSelected()) {
                index = 1;
            } else if (isRollover) {
                index = 0;
            }

            topSkin.draw(g, index, 0, 1, menuWidth, menuHeight - 1);

            if (index == 2) {
                Color old = g.getColor();

                if (LiquidLookAndFeel.areStipplesUsed()) {
                    Colors.drawStipples(g, menuItem, menuItem.getBackground());
                }

                g.setColor(old);
            }
        }
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewRect rectangle.
     */
    private String layoutMenuItem(FontMetrics fm, String text,
        FontMetrics fmAccel, String acceleratorText, Icon icon, Icon checkIcon,
        Icon arrowIcon, int verticalAlignment, int horizontalAlignment,
        int verticalTextPosition, int horizontalTextPosition,
        Rectangle viewRect, Rectangle iconRect, Rectangle textRect,
        Rectangle acceleratorRect, Rectangle checkIconRect,
        Rectangle arrowIconRect, int textIconGap, int menuItemGap) {
        SwingUtilities.layoutCompoundLabel(menuItem, fm, text, icon,
            verticalAlignment, horizontalAlignment, verticalTextPosition,
            horizontalTextPosition, viewRect, iconRect, textRect, textIconGap);

        /* Initialize the acceelratorText bounds rectangle textRect.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for acceleratorTextRect.
         */
        if ((acceleratorText == null) || acceleratorText.equals("")) {
            acceleratorRect.width = acceleratorRect.height = 0;
            acceleratorText = "";
        } else {
            acceleratorRect.width = SwingUtilities.computeStringWidth(fmAccel,
                    acceleratorText);
            acceleratorRect.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle's width & height.
         */
        if (useCheckAndArrow()) {
            if (checkIcon != null) {
                checkIconRect.height = checkIcon.getIconHeight();
                checkIconRect.width = checkIcon.getIconWidth();
            } else {
                checkIconRect.width = checkIconRect.height = 0;
            }

            /* Initialize the arrowIcon bounds rectangle width & height.
             */
            if (arrowIcon != null) {
                arrowIconRect.width = arrowIcon.getIconWidth();
                arrowIconRect.height = arrowIcon.getIconHeight();
            } else {
                arrowIconRect.width = arrowIconRect.height = 0;
            }
        } else {
            checkIconRect.width = checkIconRect.height = 0;
            arrowIconRect.width = arrowIconRect.height = 0;
        }

        Rectangle labelRect = iconRect.union(textRect);

        if (true) { //useCheckAndArrow()) { //BasicGraphicsUtils.isLeftToRight(menuItem) ) { // FIX ME

            if (checkIcon != null) {
                checkIconRect.x += menuItemGap;
            } else {
                textRect.x += menuItemGap;
                iconRect.x += menuItemGap;
            }

            // Position the Accelerator text rect
            acceleratorRect.x = (viewRect.x + viewRect.width) -
                arrowIconRect.width - menuItemGap - acceleratorRect.width;
        }

        // Align the accelertor text and the check and arrow icons vertically
        // with the center of the label rect.
        acceleratorRect.y = (labelRect.y + (labelRect.height / 2)) -
            (acceleratorRect.height / 2);

        if (useCheckAndArrow()) {
            arrowIconRect.y = (labelRect.y + (labelRect.height / 2)) -
                (arrowIconRect.height / 2);
            checkIconRect.y = (labelRect.y + (labelRect.height / 2)) -
                (checkIconRect.height / 2);
            arrowIconRect.x = (viewRect.x + viewRect.width) - menuItemGap -
                arrowIconRect.width;
        }

        //        iconRect.y = labelRect.y + 10; //(labelRect.height/2) - (iconRect.height/2);
        return text;
    }

    protected class MouseInputHandler implements MouseInputListener {
        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            Point p = e.getPoint();

            if ((p.x >= 0) && (p.x < menuItem.getWidth()) && (p.y >= 0) &&
                    (p.y < menuItem.getHeight())) {
                doClick(manager);
            } else {
                manager.processMouseEvent(e);
            }
        }

        public void mouseEntered(MouseEvent e) {
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            int modifiers = e.getModifiers();

            // 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
            if ((modifiers &
                    (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK |
                    InputEvent.BUTTON3_MASK)) != 0) {
                MenuSelectionManager.defaultManager().processMouseEvent(e);
            } else {
                manager.setSelectedPath(getPath());
            }
        }

        public void mouseExited(MouseEvent e) {
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();

            int modifiers = e.getModifiers();

            // 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
            if ((modifiers &
                    (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK |
                    InputEvent.BUTTON3_MASK)) != 0) {
                MenuSelectionManager.defaultManager().processMouseEvent(e);
            } else {
                MenuElement[] path = manager.getSelectedPath();

                if (path.length > 1) {
                    MenuElement[] newPath = new MenuElement[path.length - 1];
                    int i;
                    int c;

                    for (i = 0, c = path.length - 1; i < c; i++)
                        newPath[i] = path[i];

                    manager.setSelectedPath(newPath);
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    private class MenuDragMouseHandler implements MenuDragMouseListener {
        public void menuDragMouseEntered(MenuDragMouseEvent e) {
        }

        public void menuDragMouseDragged(MenuDragMouseEvent e) {
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement[] path = e.getPath();
            manager.setSelectedPath(path);
        }

        public void menuDragMouseExited(MenuDragMouseEvent e) {
        }

        public void menuDragMouseReleased(MenuDragMouseEvent e) {
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement[] path = e.getPath();
            Point p = e.getPoint();

            if ((p.x >= 0) && (p.x < menuItem.getWidth()) && (p.y >= 0) &&
                    (p.y < menuItem.getHeight())) {
                doClick(manager);
            } else {
                manager.clearSelectedPath();
            }
        }
    }

    private class MenuKeyHandler implements MenuKeyListener {
        /**
         * Handles the mnemonic key typed in the MenuItem if this menuItem is in
         * a standalone popup menu. This invocation normally
         * handled in BasicMenuUI.MenuKeyHandler.menuKeyPressed. Ideally, the
         * MenuKeyHandlers for both BasicMenuItemUI and BasicMenuUI can be consolidated
         * into BasicPopupMenuUI but that would require an semantic change. This
         * would result in a performance win since we can shortcut a lot of the needless
         * processing from MenuSelectionManager.processKeyEvent(). See 4670831.
         */
        public void menuKeyTyped(MenuKeyEvent e) {
            if (DEBUG) {
                System.out.println("in BasicMenuItemUI.menuKeyTyped for " +
                    menuItem.getText());
            }

            int key = menuItem.getMnemonic();

            if ((key == 0) || (e.getPath().length != 2)) { // Hack! Only proceed if in a JPopupMenu

                return;
            }

            if (lower((char) key) == lower(e.getKeyChar())) {
                MenuSelectionManager manager = e.getMenuSelectionManager();
                doClick(manager);
                e.consume();
            }
        }

        public void menuKeyPressed(MenuKeyEvent e) {
            if (DEBUG) {
                System.out.println("in BasicMenuItemUI.menuKeyPressed for " +
                    menuItem.getText());
            }
        }

        public void menuKeyReleased(MenuKeyEvent e) {
        }

        private char lower(char keyChar) {
            return Character.toLowerCase(keyChar);
        }
    }

    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();

            if (name.equals("labelFor") || name.equals("displayedMnemonic") ||
                    name.equals("accelerator")) {
                updateAcceleratorBinding();
            } else if (name.equals("text") || "font".equals(name) ||
                    "foreground".equals(name)) {
                // remove the old html view client property if one
                // existed, and install a new one if the text installed
                // into the JLabel is html source.
                JMenuItem lbl = ((JMenuItem) e.getSource());
                String text = lbl.getText();
                BasicHTML.updateRenderer(lbl, text);
            }
        }
    }

    private static class ClickAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource();
            MenuSelectionManager.defaultManager().clearSelectedPath();
            mi.doClick();
        }
    }

    //////////////////////////////////////////////////////////
    public static class EmptyIcon implements Icon {
        int width;
        int height;

        public EmptyIcon(int width, int height) {
            this.height = height;
            this.width = width;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            return;
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }
    }
}
