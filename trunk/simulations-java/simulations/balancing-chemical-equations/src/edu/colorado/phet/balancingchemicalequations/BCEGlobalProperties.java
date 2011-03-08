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
    public final Property<Boolean> showAnswers = new Property<Boolean>( false ); // shows the answers in all tabs
    public final Property<Boolean> showChartsAndScalesInGame = new Property<Boolean>( true ); // shows chart or scale in "Not Balanced" popup
    public final Property<Boolean> playAllEquations = new Property<Boolean>( false ); // plays all equations for a specified game level

    public BCEGlobalProperties( Frame frame ) {
        this.frame = frame;
    }

    public void reset() {
        moleculesVisible.reset();
        canvasColor.reset();
        boxColor.reset();
        showAnswers.reset();
        showChartsAndScalesInGame.reset();
        playAllEquations.reset();
    }
}
