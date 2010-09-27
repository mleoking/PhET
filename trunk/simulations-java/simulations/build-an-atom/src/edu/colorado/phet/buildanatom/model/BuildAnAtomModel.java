/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Rectangle2D;


/**
 * Main model class.  Units are picometers (1E-12).
 */
public class BuildAnAtomModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Rectangle2D MODEL_BOUNDS = new Rectangle2D.Double(-150, -150, 300, 300);

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        super();

        this.clock = clock;
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    // TODO: This is for testing, let's
    public Rectangle2D getBounds(){
        return MODEL_BOUNDS;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }
}
