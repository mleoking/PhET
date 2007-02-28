/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.util.OpaquePropertyChangeListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;


public class LiquidComboBoxUI extends BasicComboBoxUI {
    static int comboBoxButtonSize = 18;
    private int prevSelectedItem;
    private Skin skinCombo;

    public static ComponentUI createUI(final JComponent c) {
        //     If we used an transparent toolbutton skin we would have to add:
        c.setOpaque(false);
// 20060608 MEV - Moved this to OpaquePropertyChangeListener so it could be removed later
        c.addPropertyChangeListener("opaque",
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    c.setOpaque(false);
                }
            });

        return new LiquidComboBoxUI();
    }

    protected void installListeners() {
        super.installListeners();
//        comboBox.addPropertyChangeListener("opaque", new OpaquePropertyChangeListener(comboBox));
    }
    
    protected void uninstallListeners() {
        super.uninstallListeners();
//        comboBox.removePropertyChangeListener("opaque", OpaquePropertyChangeListener.getComponentListener(comboBox));
    }
    
    public void installUI(JComponent c) {
        super.installUI(c);
        addKeyboardActions((JComboBox) c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
    }

    
    /**
     * Paint the combobox component.
     * <I>NOTE:  This code was moved here from the LiquidComboBoxButton class because the
     * editor would be completely obscured by the combo skin for an editable combobox.</I>
     * @param g 
     * @param c 
     */
    public void paint(Graphics g, JComponent c) {
        int index = ((LiquidComboBoxButton)arrowButton).getIndexForState();

// 20060213 MEV - Correction to highlight when has focus        
//        index = (comboBox.hasFocus() && !comboBox.isEditable() ? 1 : index);

// Paint the Combo
        getSkinCombo().draw(g, index, comboBox.getWidth(), comboBox.getHeight());            
    }

    protected ComboBoxEditor createEditor() {
        return new LiquidComboBoxEditor.UIResource();
    }

    protected ComboPopup createPopup() {
        return new LiquidComboPopup(comboBox);
    }

    protected JButton createArrowButton() {
        JButton button = new LiquidComboBoxButton(comboBox,
                new LiquidCheckBoxIcon(), comboBox.isEditable(),
                currentValuePane, listBox);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);

        return button;
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new LiquidPropertyChangeListener();
    }

    /**
     * Dominik Schwald <d.schwald@nextbyte.de> wrote this method. I did some
     * improvments. Thank you Dominik.
     */
    private void addKeyboardActions(final JComboBox cb) {
        // [ENTER] --> show List
        KeyStroke ksEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                KeyEvent.VK_UNDEFINED);
        cb.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    prevSelectedItem = cb.getSelectedIndex();

                    if (cb.isPopupVisible()) {
                        cb.hidePopup();
                    } else {
                        cb.showPopup();
                    }
                }
            }, ksEnter, JComponent.WHEN_FOCUSED);

        // [ESC] --> hide List
        KeyStroke ksESC = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                KeyEvent.VK_UNDEFINED);
        cb.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cb.hidePopup();
                    cb.setSelectedIndex(prevSelectedItem);
                }
            }, ksESC, JComponent.WHEN_FOCUSED);

        // [down arrow] --> next selection
        KeyStroke ksDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                KeyEvent.VK_UNDEFINED);
        cb.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!cb.isPopupVisible()) {
                        cb.showPopup();
                        return;
                    }
                    if (cb.getSelectedIndex() <= (cb.getItemCount() - 2)) {
                        cb.setSelectedIndex(cb.getSelectedIndex() + 1);
                    } else if ((cb.getSelectedIndex() == -1) &&
                            (cb.getItemCount() > 0)) {
                        cb.setSelectedIndex(0);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }, ksDown, JComponent.WHEN_FOCUSED);

        // [right arrow] --> next selection
        KeyStroke ksRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                KeyEvent.VK_UNDEFINED);
        cb.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (cb.getSelectedIndex() <= (cb.getItemCount() - 2)) {
                        cb.setSelectedIndex(cb.getSelectedIndex() + 1);
                    } else if ((cb.getSelectedIndex() == -1) &&
                            (cb.getItemCount() > 0)) {
                        cb.setSelectedIndex(0);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }, ksRight, JComponent.WHEN_FOCUSED);

        // [up arrow] --> previous selection
        KeyStroke ksUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                KeyEvent.VK_UNDEFINED);
        cb.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (cb.getSelectedIndex() > 0) {
                        cb.setSelectedIndex(cb.getSelectedIndex() - 1);
                    } else if ((cb.getSelectedIndex() == -1) &&
                            (cb.getItemCount() > 0)) {
                        cb.setSelectedIndex(0);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }, ksUp, JComponent.WHEN_FOCUSED);

        // [left arrow] --> previous selection
        KeyStroke ksLeft = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                KeyEvent.VK_UNDEFINED);
        cb.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (cb.getSelectedIndex() > 0) {
                        cb.setSelectedIndex(cb.getSelectedIndex() - 1);
                    } else if ((cb.getSelectedIndex() == -1) &&
                            (cb.getItemCount() > 0)) {
                        cb.setSelectedIndex(0);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }, ksLeft, JComponent.WHEN_FOCUSED);
    }

    /**
     * As of Java 2 platform v1.4 this method is no longer used. Do not call or
     * override. All the functionality of this method is in the
     * MetalPropertyChangeListener.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    protected void editablePropertyChanged(PropertyChangeEvent e) {
    }

    protected LayoutManager createLayoutManager() {
        return new MetouiaComboBoxLayoutManager();
    }

    protected Rectangle rectangleForCurrentValue2() {
        int width = comboBox.getWidth();
        int height = comboBox.getHeight();
        Insets insets = getInsets();
        int buttonSize = height - (insets.top + insets.bottom);

        if (arrowButton != null) {
            buttonSize = comboBoxButtonSize;
        }

        if (comboBox.getComponentOrientation().isLeftToRight()) {
            // Liquid style editor rectangle
            return new Rectangle(insets.left + 8, insets.top,
                width - (insets.left + insets.right + buttonSize) - 7,
                height - (insets.top + insets.bottom) - 2);
        } else {
            return new Rectangle(insets.left + buttonSize, insets.top,
                width - (insets.left + insets.right + buttonSize),
                height - (insets.top + insets.bottom));
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no
     * longer used.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    protected void removeListeners() {
        if (propertyChangeListener != null) {
            comboBox.removePropertyChangeListener(propertyChangeListener);
        }
    }

    // These two methods were overloaded and made public. This was probably a
    // mistake in the implementation. The functionality that they used to
    // provide is no longer necessary and should be removed. However,
    // removing them will create an uncompatible API change.
    public void configureEditor() {
        super.configureEditor();
    }

    public void unconfigureEditor() {
        super.unconfigureEditor();
    }

    public Dimension getMinimumSize(JComponent c) {
        if (c == null) {
            return new Dimension();
        }

        if (!isMinimumSizeDirty) {
            return new Dimension(cachedMinimumSize);
        }

        Dimension size = null;

        if (!comboBox.isEditable() && (arrowButton != null) &&
                arrowButton instanceof LiquidComboBoxButton) {
            LiquidComboBoxButton button = (LiquidComboBoxButton) arrowButton;
            Insets buttonInsets = new Insets(0, 0, 0, 0);
            Insets insets = comboBox.getInsets();

            size = getDisplaySize();
            size.width += (comboBoxButtonSize + insets.left + insets.right); // Hack
            size.width += (buttonInsets.left + buttonInsets.right);
            size.width += (buttonInsets.right +
            button.getComboIcon().getIconWidth());
            size.height += (insets.top + insets.bottom);
            size.height += (buttonInsets.top + buttonInsets.bottom);
            size.height = Math.max(21, size.height);
        } else if (comboBox.isEditable() && (arrowButton != null) &&
                (editor != null)) {
            size = super.getMinimumSize(c);

            Insets margin = arrowButton.getMargin();
            Insets insets = comboBox.getInsets();

            if (editor instanceof JComponent) {
                Insets editorInsets = ((JComponent) editor).getInsets();
            }

            size.height += (margin.top + margin.bottom);
            size.height += (insets.top + insets.bottom);

            //size.height = Math.max(20,size.height);
        } else {
            size = super.getMinimumSize(c);
        }

        cachedMinimumSize.setSize(size.width, size.height);
        isMinimumSizeDirty = false;

        return new Dimension(cachedMinimumSize);
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class LiquidPropertyChangeListener
        extends BasicComboBoxUI.PropertyChangeHandler {
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange(e);

            String propertyName = e.getPropertyName();

            if (propertyName.equals("editable")) {
                LiquidComboBoxButton button = (LiquidComboBoxButton) arrowButton;
                button.setIconOnly(comboBox.isEditable());
                comboBox.repaint();
            } else if (propertyName.equals("background")) {
                Color color = (Color) e.getNewValue();
                listBox.setBackground(color);
            } else if (propertyName.equals("foreground")) {
                Color color = (Color) e.getNewValue();
                listBox.setForeground(color);
            } else if (propertyName.equals("componentOrientation")) {
                LiquidComboBoxButton button = (LiquidComboBoxButton) arrowButton;
                button.setComponentOrientation((ComponentOrientation) e.getNewValue());
            }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class MetouiaComboBoxLayoutManager implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            JComboBox cb = (JComboBox) parent;

            return parent.getPreferredSize();
        }

        public Dimension minimumLayoutSize(Container parent) {
            JComboBox cb = (JComboBox) parent;

            return parent.getMinimumSize();
        }

        public void layoutContainer(Container parent) {
            JComboBox cb = (JComboBox) parent;
            int width = cb.getWidth();
            int height = cb.getHeight();

            Rectangle cvb = null;

            if (comboBox.isEditable()) {
                if (editor != null) {
                    cvb = rectangleForCurrentValue2();
                    editor.setBounds(cvb);
                }

                if (arrowButton != null) {
                    if (editor != null && cvb != null) {
                        int start = cvb.x + cvb.width;
                        arrowButton.setBounds(start, 0, width - start, height);
                    } else {
                        arrowButton.setBounds(0, 0, width, height);
                    }
                }
            } else {
                arrowButton.setBounds(0, 0, width, height);
            }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     *
     * This class is now obsolete and doesn't do anything and
     * is only included for backwards API compatibility. Do not call or
     * override.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    public class LiquidComboPopup extends BasicComboPopup {
        public LiquidComboPopup(JComboBox cBox) {
            super(cBox);
        }

        // This method was overloaded and made public. This was probably
        // mistake in the implementation. The functionality that they used to
        // provide is no longer necessary and should be removed. However,
        // removing them will create an uncompatible API change.
        public void delegateFocus(MouseEvent e) {
            super.delegateFocus(e);
        }
    }

    public Skin getSkinCombo()
    {
        if (skinCombo == null)
        {
// RtoL stuff            boolean leftToRight = comboBox.getComponentOrientation().isLeftToRight();
// RtoL stuff            if( leftToRight )
// RtoL stuff            {
                skinCombo = new Skin("combobox.png", 4, 10, 6, 18, 4);
// RtoL stuff            }
// RtoL stuff            else
// RtoL stuff            {
// RtoL stuff                skinCombo = new Skin("comboboxrtl.png", 4, 18, 4, 10, 6);
// RtoL stuff            }
        }
        return skinCombo;
    }

}
