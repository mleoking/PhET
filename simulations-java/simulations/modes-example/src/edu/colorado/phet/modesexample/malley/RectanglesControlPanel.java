// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the Rectangles module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RectanglesControlPanel extends ControlPanel {

    public RectanglesControlPanel( Property<RectanglesMode> currentMode, ArrayList<RectanglesMode> modes, Resettable resettable ) {
        addControlFullWidth( new ModeSelectionControl( currentMode, modes ) );
        addResetAllButton( resettable );
    }
}
