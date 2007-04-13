/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.ComponentUI;


/**
 * This class represents the UI delegate for the JButton component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidToggleButtonUI extends LiquidButtonUI {
    
    /**
     * The Cached UI delegate.
     */
    private static final LiquidToggleButtonUI toggleButtonUI = new LiquidToggleButtonUI();
    
    private final static String propertyPrefix = "ToggleButton.";
    
    protected String getPropertyPrefix() {
        return propertyPrefix;
    }

    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(final JComponent c) {
        JToggleButton b = (JToggleButton) c;
        b.setRolloverEnabled(true);

        //     If we used an transparent toolbutton skin we would have to add:
        c.setOpaque(false);
        c.addPropertyChangeListener("opaque",new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                c.setOpaque(false);
            }
        });
        return toggleButtonUI;
    }
    
    public void paint(Graphics g, JComponent c) {
        
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        
        buttonIndexModel.setButton(button);
        buttonIndexModel.setCheckForDefaultButton(false);
        int index=buttonIndexModel.getIndexForState();
        if (index > 3) index -= 4;
        if (model.isArmed() && model.isPressed() || model.isSelected())
            index = 2;
        if (button.hasFocus() && index==0) index = 1; // my change
        if (button.getHeight()<21 || button.getWidth()<21) {
            getSkinToolbar().draw(g, index, button.getWidth(), button.getHeight());
            // don't paint the focus when button is too small
            button.setFocusPainted(false);
        } else {
            if (button.getClientProperty("JToolBar.isToolbarButton") == Boolean.TRUE) {
                getSkinToolbar().draw(g, index, button.getWidth(), button.getHeight());
            } else {
                getSkinButton().draw(g,index,button.getWidth(),button.getHeight());
            }
        }
        if (index==4 && button.isFocusPainted()) {
            Rectangle bounds = button.getBounds();
            paintFocus(g, bounds.height/2-5);
        }
        super.paint(g, c);
    }
}