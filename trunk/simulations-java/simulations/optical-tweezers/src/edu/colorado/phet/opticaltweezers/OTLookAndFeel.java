/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

/**
 * OTLookAndFeel describes the look-and-field properties for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTLookAndFeel extends PhetLookAndFeel {

    public static final Color BACKGROUND_COLOR = new Color( 215, 229, 255 );
    
    public OTLookAndFeel() {
        super();
        setBackgroundColor( BACKGROUND_COLOR );
        setTextFieldBackgroundColor( Color.WHITE );
    }
}
