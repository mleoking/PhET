// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * @author John Blanco
 */
public class RnaPolymerase extends ShapeChangingModelObject {

    private static final double WIDTH = 340;
    private static final double HEIGHT = 150;

    public RnaPolymerase( Point2D position ) {
        super( new Ellipse2D.Double( position.getX() - WIDTH / 2, position.getY() - HEIGHT / 2, WIDTH, HEIGHT ) );
    }

    public Paint getPaint() {
        return new Color( 0, 200, 40 );
    }
}
