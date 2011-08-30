// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 *
 * @author John Blanco
 */
public class MessengerRna extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Color used by this molecule.  Since mRNA is depicted as a line and not
    // as a closed shape, a transparent color is used.  This enables reuse of
    // generic biomolecule classes.
    private static final Color NOMINAL_COLOR = new Color( 0, 0, 0, 0 );

    // Minimum distance between points that define the shape.  This is done so
    // that this doesn't end up defined by so many points that the shape is
    // strange looking.
    private static final double MIN_DISTANCE_BETWEEN_POINTS = 50;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final List<Point2D> shapeDefiningPoints = new ArrayList<Point2D>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( Point2D position ) {
        super( new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );
        setPosition( position );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Grow the mRNA to the provided location, which essentially means a length
     * is being added.  This is usually done in small amounts, and is likely to
     * look weird if an attempt is made to grow to a distant point.
     */
    public void growTo( double x, double y ) {
        if ( shapeDefiningPoints.size() >= 2 ) {
            // If the current last point is less than the min distance from
            // the 2nd to last point, replace the current last point.
            Point2D secondToLastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 2 );
            Point2D lastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 );
            if ( lastPoint.distance( secondToLastPoint ) < MIN_DISTANCE_BETWEEN_POINTS ) {
                shapeDefiningPoints.remove( lastPoint );
            }
        }
        shapeDefiningPoints.add( new Point2D.Double( x, y ) );
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( shapeDefiningPoints ) );
    }
}
