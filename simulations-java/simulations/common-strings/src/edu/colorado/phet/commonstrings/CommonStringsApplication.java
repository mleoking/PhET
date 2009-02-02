/* Copyright 2008, University of Colorado */

package edu.colorado.phet.commonstrings;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * To translate common strings, we bundle phetcommon-strings*.properties into a JAR, along with this main.
 * This main insures that we have a mainclass that can be run when the "Test" button is pressed in
 * translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CommonStringsApplication {
    
    private static final String VERSION_ID = new PhetResources( "common-strings" ).getVersion().formatForAboutDialog();
    
    private CommonStringsApplication() {}

    public static void main( String[] args ) {
        String message = "<html>PhET common strings" + " : " + "version " + VERSION_ID + "<br><br>" + 
              "This JAR file contains common strings used by all PhET simulations." + "<br>" +
              "You can use this JAR file to translate common strings with Translation Utility." + "<br>" +
              "But testing those translations is not currently supported." + "</html>";
        String title = "";
        JOptionPane.showMessageDialog( null, message, title, JOptionPane.PLAIN_MESSAGE );
    }
}
