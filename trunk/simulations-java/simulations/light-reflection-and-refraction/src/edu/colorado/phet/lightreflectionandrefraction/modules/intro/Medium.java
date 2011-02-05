// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class Medium {
    private final Shape shape;
    private double indexOfRefraction;
    private final Color color;

    public Medium( Shape shape, double indexOfRefraction, Color color ) {
        this.shape = shape;
        this.indexOfRefraction = indexOfRefraction;
        this.color = color;
    }

    public double getIndexOfRefraction() {
        return indexOfRefraction;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }
}
