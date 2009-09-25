/* Copyright 2009, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Image;

import javax.swing.Icon;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

/**
 * Image files used in this application.
 * Loaded statically so that we can see if anything is missing without visiting all features.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TUImages {
    
    private TUImages() {}
    
    // PhET logo
    public static final Image PHET_LOGO = TUResources.getCommonImage( PhetLookAndFeel.PHET_LOGO_120x50 );
    
    // initial dialog
    public static final Icon CONTINUE_ICON = TUResources.getIcon( "continueButton.png" );
    public static final Icon CANCEL_ICON = TUResources.getIcon( "cancelButton.png" );
    
    // toolbar
    public static final Icon TEST_ICON = TUResources.getIcon( "testButton.png" );
    public static final Icon SAVE_ICON = TUResources.getIcon( "saveButton.png" );
    public static final Icon LOAD_ICON = TUResources.getIcon( "loadButton.png" );
    public static final Icon SUBMIT_ICON = TUResources.getIcon( "submitButton.png" );
    public static final Icon FIND_ICON = TUResources.getIcon( "findButton.png" );
    public static final Icon HELP_ICON = TUResources.getIcon( "helpButton.png" );
    
    // Find dialog
    public static final Icon NEXT_ICON = TUResources.getIcon( "nextArrow.png" );
    public static final Icon PREVIOUS_ICON = TUResources.getIcon( "previousArrow.png" );
    
    // Misc
    public static final Icon ERROR_ICON = TUResources.getIcon( "errorIcon.png" );
}
