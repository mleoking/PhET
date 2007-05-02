/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * CloseButton is the stanard PhET close button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CloseButton extends JButton {
    
    private static final String IMAGE_CLOSE_BUTTON = "buttons/closeButton.png";

    public CloseButton() {
        super();
        Image image = PhetCommonResources.getInstance().getImage( IMAGE_CLOSE_BUTTON );
        Icon icon = new ImageIcon( image );
        setIcon( icon );
        setOpaque( false );
        setMargin( new Insets( 0, 0, 0, 0 ) );
    }
}
