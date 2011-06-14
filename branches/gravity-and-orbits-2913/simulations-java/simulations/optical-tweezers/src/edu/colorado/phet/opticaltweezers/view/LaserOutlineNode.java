// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LaserOutlineNode draws an outline of the laser beam's shape.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserOutlineNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color OUTLINE_COLOR = Color.GRAY;
    public static final Stroke OUTLINE_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LaserOutlineNode( Laser laser, OTModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        PNode outlineNode = createOutlineNode( laser, modelViewTransform );
        addChild( outlineNode );
    }
    
    /*
     * Create the shape of the beam's outline.
     * The shape is calculated in model coordinates, then transformed to view coordinates.
     * The node returned has it's origin at its geometric center.
     */
    private PNode createOutlineNode( Laser laser, OTModelViewTransform modelViewTransform ) {
        
        // Shape is symmetric, calculate points for one quarter of the outline, 1 point for each 1 nm
        final int numberOfPoints = (int) laser.getDistanceFromObjectiveToWaist();
        Point2D[] points = new Point2D.Double[numberOfPoints];
        for ( int y = 0; y < points.length; y++ ) {
            double r = laser.getRadius( y );
            points[y] = new Point2D.Double( r, y );
        }

        int iFirst = 0;
        int iLast = points.length - 1;
        
        // Right half
        GeneralPath rightPath = new GeneralPath();
        // upper-right quadrant
        rightPath.moveTo( (float) points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 1; i > iFirst; i-- ) {
            rightPath.lineTo( (float) points[i].getX(), -(float) points[i].getY() );
        }
        // lower-right quadrant
        for ( int i = iFirst; i < iLast; i++ ) {
            rightPath.lineTo( (float) points[i].getX(), (float) points[i].getY() );
        }
        // portion coming into objective
        rightPath.lineTo( (float) points[iLast].getX(),  (float)( points[iLast].getY() + laser.getDistanceFromObjectiveToControlPanel() ) );
        // transform to view coordinates
        Shape rightShape = modelViewTransform.createTransformedShapeModelToView( rightPath );
        // node
        PPath nRight = new PPath( rightShape );
        nRight.setStroke( OUTLINE_STROKE );
        nRight.setStrokePaint( OUTLINE_COLOR );
        nRight.setPaint( null );
        
        // Left path
        GeneralPath leftPath = new GeneralPath();
        // upper-left quadrant
        leftPath.moveTo( (float) -points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 1; i > iFirst; i-- ) {
            leftPath.lineTo( (float) -points[i].getX(), -(float) points[i].getY() );
        }
        // lower-left quadrant
        for ( int i = iFirst; i < iLast; i++ ) {
            leftPath.lineTo( (float) -points[i].getX(), (float) points[i].getY() );
        }
        // portion coming into objective
        leftPath.lineTo( (float) -points[iLast].getX(),  (float)( points[iLast].getY() + laser.getDistanceFromObjectiveToControlPanel() ) );
        // transform to view coordinates
        Shape leftShape = modelViewTransform.createTransformedShapeModelToView( leftPath );
        // node
        PPath nLeft = new PPath( leftShape );
        nLeft.setStroke( OUTLINE_STROKE );
        nLeft.setStrokePaint( OUTLINE_COLOR );
        nLeft.setPaint( null );
        
        PNode parentNode = new PComposite();
        parentNode.addChild( nLeft );
        parentNode.addChild( nRight );
        
        // move origin to center of trap
        final double d = modelViewTransform.modelToView( laser.getDistanceFromObjectiveToControlPanel() );
        PBounds parentBounds = parentNode.getFullBoundsReference();
        parentNode.setOffset( parentBounds.getWidth() / 2, ( parentBounds.getHeight() / 2 ) - ( d / 2 ) );
        
        return parentNode;
    }
}
