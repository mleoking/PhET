/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon;

import javax.swing.JOptionPane;

/**
 * To translate common strings, we bundle phetcommon-strings*.properties into a JAR, along with this main.
 * This main insures that we have a mainclass that can be run when the "Test" button is pressed in
 * translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CommonStringsMain {

    public static void main( String[] args ) {
        JOptionPane.showMessageDialog( null, "Common strings cannot be tested." );
    }
}
