/*
 * OpaquePropertyChangeListener.java
 *
 * Created on June 8, 2006, 12:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.birosoft.liquid.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

/** This class is used for forcing certain components to appear 
 * transparent when the LiquidLookAndFeel.panelTransparency property is true.
 *
 * @author xendren
 */
public class OpaquePropertyChangeListener implements PropertyChangeListener {
    private final static String PROPERTY_NAME = "opaque"; 
    private JComponent component = null;
    
    /** Creates a new instance of OpaquePropertyChangeListener */
    public OpaquePropertyChangeListener(JComponent component) {
        this.component = component;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("opaque") && evt.getSource() != this) {
            component.setOpaque(false);
        }
    }
    
    /**
     * Returns the ButtonListener for the passed in Button, or null if one
     * could not be found.
     */
    public static PropertyChangeListener getComponentListener(JComponent comp) {
        PropertyChangeListener[] listeners = comp.getPropertyChangeListeners(PROPERTY_NAME);

        if (listeners != null) {
            for (int counter = 0; counter < listeners.length; counter++) {
                if (listeners[counter] instanceof OpaquePropertyChangeListener) {
                    return (OpaquePropertyChangeListener)listeners[counter];
                }
            }
        }
        return null;
    }
    
}
