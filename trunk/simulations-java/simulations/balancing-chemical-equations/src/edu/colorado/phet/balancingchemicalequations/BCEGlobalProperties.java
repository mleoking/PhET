// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;

/**
 * Encapsulates all global properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEGlobalProperties implements Resettable {

    // user properties
    private final Frame frame;
    private final Property<Boolean> moleculesVisibleProperty = new Property<Boolean>( true );

    // developer controls
    private final boolean isDev;
    private final Property<Color> canvasColorProperty = new Property<Color>( BCEColors.CANVAS_BACKGROUND );
    private final Property<Color> boxColorProperty = new Property<Color>( BCEColors.CANVAS_BACKGROUND );

    public BCEGlobalProperties( Frame frame, boolean isDev ) {
        this.frame = frame;
        this.isDev = isDev;
    }

    public Frame getFrame() {
        return frame;
    }

    public Property<Boolean> getMoleculesVisibleProperty() {
        return moleculesVisibleProperty;
    }

    public boolean isDev() {
        return isDev;
    }

    public Property<Color> getCanvasColorProperty() {
        return canvasColorProperty;
    }

    public Property<Color> getBoxColorProperty() {
        return boxColorProperty;
    }

    public void reset() {
        moleculesVisibleProperty.reset();
        canvasColorProperty.reset();
        boxColorProperty.reset();
    }
}
