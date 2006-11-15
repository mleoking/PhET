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

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
            setPreferredSize( new Dimension( 40, (int)getPreferredSize().getHeight() ) );

            addFocusListener( new FocusAdapter() {
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
                    }
                }

                private void resetValue() {
                    setText( "0" );
                    requestFocus();
                }
            } );
        }
    }
