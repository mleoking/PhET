// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.util.ArrayList;

import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;

/**
 * Control panel for selecting the mode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModeSelectionControl extends GridPanel {

    public ModeSelectionControl( Property<RectanglesMode> currentMode, ArrayList<RectanglesMode> modes ) {

        setBorder( new TitledBorder( "Mode" ) );
        setGridX( 0 ); // vertical
        setAnchor( Anchor.WEST ); // left justified

        // one radio button for each mode
        for ( RectanglesMode mode : modes ) {
            add( new PropertyRadioButton<RectanglesMode>( mode.displayName, currentMode, mode ) );
        }
    }
}
