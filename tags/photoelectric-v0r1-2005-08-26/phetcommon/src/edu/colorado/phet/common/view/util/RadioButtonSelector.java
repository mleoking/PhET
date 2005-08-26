/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.common.view.util;

import javax.swing.*;
import java.util.Enumeration;

/**
 * RadioButtonSelector
 * <p>
 * Returns a reference to the JRadioButton or JRadioButtonMenuItem in a ButtonGroup
 * that is currently selected. This is useful for 
 */
public class RadioButtonSelector {

    // This method returns the selected radio button in a button group
    public static JRadioButton getSelection(ButtonGroup group) {
        for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if (b.getModel() == group.getSelection()) {
                return b;
            }
        }
        return null;
    }}
