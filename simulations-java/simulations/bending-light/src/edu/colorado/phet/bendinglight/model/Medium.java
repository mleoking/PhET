// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.*;

/**
 * A Medium is a substance through which LightRay instances propagate.
 *
 * @author Sam Reid
 */
public class Medium {
    public final Shape shape;
    private final MediumState mediumState;
    public final Color color;//Color is based on the index of refraction at red wavelength

    public Medium( Shape shape, MediumState mediumState, Color color ) {
        this.shape = shape;
        this.mediumState = mediumState;
        this.color = color;
    }

    public double getIndexOfRefraction( double wavelength ) {
        return mediumState.dispersionFunction.getIndexOfRefraction( wavelength );
    }

    public boolean isMystery() {
        return mediumState.mystery;
    }

    public MediumState getMediumState() {
        return mediumState;
    }
}
