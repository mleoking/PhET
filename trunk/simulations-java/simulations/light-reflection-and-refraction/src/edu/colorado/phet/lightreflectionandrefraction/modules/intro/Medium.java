// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class Medium {
    private final Shape shape;
    private double indexOfRefraction;

    public Medium( Shape shape ) {
        this.shape = shape;
    }

    public double getIndexOfRefraction() {
        return indexOfRefraction;
    }

    public Shape getShape() {
        return shape;
    }
}
