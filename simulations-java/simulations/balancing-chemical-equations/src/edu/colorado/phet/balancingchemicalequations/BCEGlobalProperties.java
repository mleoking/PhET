// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;

/**
 * Encapsulates all global properties.
 * These are generally things that can be controlled from the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEGlobalProperties implements Resettable {

    public final Frame frame; // parent frame for secondary windows

    // user controls (Options menu)
    public final Property<Boolean> moleculesVisible = new Property<Boolean>( true ); // are molecules visible in boxes?

    // developer controls (Developer menu)
    public final Property<Color> canvasColor = new Property<Color>( BCEConstants.CANVAS_BACKGROUND );
    public final Property<Color> boxColor = new Property<Color>( BCEConstants.BOX_COLOR );
    public final Property<Boolean> answersVisible = new Property<Boolean>( false ); // shows the answers in all tabs
    public final Property<Boolean> playAllEquations = new Property<Boolean>( false ); // plays all equations for a specified game level
    public final Property<Boolean> popupsWhyButtonVisible = new Property<Boolean>( true ); // adds a "Show Why" button to "Not Balanced" game popup
    public final Property<Boolean> popupsCloseButtonVisible = new Property<Boolean>( false ); // adds a close (X) button to game popups
    public final Property<Boolean> popupsTitleBarVisible = new Property<Boolean>( false ); // adds a title bar to game pops

    public BCEGlobalProperties( Frame frame ) {
        this.frame = frame;
    }

    public void reset() {
        moleculesVisible.reset();
        canvasColor.reset();
        boxColor.reset();
        answersVisible.reset();
        playAllEquations.reset();
        popupsWhyButtonVisible.reset();
        popupsCloseButtonVisible.reset();
        popupsTitleBarVisible.reset();
    }
}
