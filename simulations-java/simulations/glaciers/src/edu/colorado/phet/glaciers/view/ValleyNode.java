/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Valley;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ValleyNode is the visual representation of a valley.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ValleyNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double X_MIN = 0; // meters
    private static final double X_MAX = 80000; // meters
    private static final double DX = 100; // meters
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Valley _valley;
    private ModelViewTransform _mvt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ValleyNode( Valley valley, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _valley = valley;
        _mvt = mvt;
        
        GeneralPath path = createValleyFloorPath( X_MIN, X_MAX, DX );
        PPath pathNode = new PPath( path );
        pathNode.setStroke( new BasicStroke( 4f ) );
        pathNode.setStrokePaint( GlaciersConstants.UNDERGROUND_COLOR );
        addChild( pathNode );
    }
    
    public void cleanup() {}
    
    /*
     * Creates a path that follows the contour of the valley floor.
     * The parameters are specified in model coordinates.
     * The returned path is in view coordinates.
     * 
     * @param xMin starting x coordinate (meters)
     * @param xMax ending x coordinate (meters)
     * @param dx interval between x samples (meters)
     * @return GeneralPath path in view coordinates
     */
    private GeneralPath createValleyFloorPath( final double xMin, final double xMax, final double dx ) {
        
        assert( xMin < xMax );
        assert( dx > 0 );
        
        GeneralPath path = new GeneralPath();
        Point2D pModel = new Point2D.Double();
        Point2D pView = new Point2D.Double();
        double x = xMin;
        double elevation = 0;
        
        while ( x <= xMax ) {
            elevation = _valley.getElevation( x );
            pModel.setLocation( x, elevation );
            pView = _mvt.modelToView( pModel, pView );
            if ( x == xMin ) {
                path.moveTo( (float) pView.getX(), (float) pView.getY() );
            }
            else {
                path.lineTo( (float) pView.getX(), (float) pView.getY() );
            }
            x += dx;
        }
        return path;
    }
}
