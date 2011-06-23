// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * For testing and prototyping.
 * TODO: This class should be removed when no longer used.
 *
 * @author John Blanco
 */
public class TestSquare extends ShapeChangingModelObject {

    private final Color color;


    /**
     * Constructor.
     */
    public TestSquare( double xCenter, double yCenter, double width, Color color ) {
        super( new Rectangle2D.Double( xCenter - width / 2, yCenter - width / 2, width, width ) );
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
