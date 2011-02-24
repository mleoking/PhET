// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class Medium {
    private final Shape shape;
    private MediumState mediumState;
    private final Color color;

    public Medium( Shape shape, MediumState mediumState, Color color ) {
        this.shape = shape;
        this.mediumState = mediumState;
        this.color = color;
    }

    public double getIndexOfRefraction() {
        return mediumState.index;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public boolean isMystery() {
        return mediumState.isMystery();
    }

    public MediumState getMediumState() {
        return mediumState;
    }
}
