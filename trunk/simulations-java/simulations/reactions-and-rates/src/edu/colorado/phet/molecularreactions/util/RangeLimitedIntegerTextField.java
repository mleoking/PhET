/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.util;

import edu.colorado.phet.molecularreactions.MRConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * RangeLimitedIntegerTextField
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RangeLimitedIntegerTextField extends JTextField {
    private int maxValue;
    private int minValue;

    public RangeLimitedIntegerTextField( int minValue, int maxValue ) {
        this.minValue = minValue;
        setHorizontalAlignment( JTextField.RIGHT );
        this.maxValue = maxValue;
        setText( "0" );
        setPreferredSize( new Dimension( 30, (int)getPreferredSize().getHeight() ) );

        addFocusListener( new InputValidator() );
    }

    /**
     * Validates the input
     */
    private class InputValidator implements FocusListener {

        public void focusGained( FocusEvent e ) {
            selectAll();
        }

        public void focusLost( FocusEvent e ) {

            if( e.isTemporary() ) {
                return;
            }

            String s = getText();
            try {
                int i = Integer.parseInt( s );
                if( i < RangeLimitedIntegerTextField.this.minValue
                    || i > RangeLimitedIntegerTextField.this.maxValue ) {
                    resetValue();
                }
            }
            catch( NumberFormatException nfe ) {
                setText( "0" );
            }
        }

        private void resetValue() {
            setText( new Integer( RangeLimitedIntegerTextField.this.maxValue ).toString() );
            JOptionPane.showMessageDialog( RangeLimitedIntegerTextField.this,
                                           MRConfig.RESOURCES.getLocalizedString( "messages.max-value-exceeded" ) );
            requestFocus();
        }
    }
}
