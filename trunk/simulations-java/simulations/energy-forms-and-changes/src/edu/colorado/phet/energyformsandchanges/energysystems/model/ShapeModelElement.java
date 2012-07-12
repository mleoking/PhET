// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Color;
import java.awt.Shape;

/**
 * TODO: This is temporary for prototyping purposes, should be removed before
 * deployment.
 *
 * @author John Blanco
 */
public class ShapeModelElement extends PositionableModelElement {

    // Node shape, which is independent of its position.
    private final Shape shape;

    private final Color color;

    public ShapeModelElement( Shape shape, Color color ) {
        this.shape = shape;
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }
}
