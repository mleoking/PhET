// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeCreationUtils;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 *
 * @author John Blanco
 */
public class MessengerRna extends MobileBiomolecule {

    // Color used by this molecule.  Since mRNA is depicted as a line and not
    // as a closed shape, a transparent color is used.  This enables reuse of
    // generic biomolecule classes.
    private static final Color NOMINAL_COLOR = new Color( 0, 0, 0, 0 );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public MessengerRna( Point2D position ) {
        super( createShape(), NOMINAL_COLOR );
        setPosition( position );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private static Shape createShape() {
        List<Point2D> points = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 0, 0 ) );
            add( new Point2D.Double( 100, 100 ) );
            add( new Point2D.Double( 100, 200 ) );
        }};
        return ShapeCreationUtils.createCurvyLineFromPoints( points );
    }
}
