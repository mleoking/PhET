/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Valley;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * MountainsNode is the visual representation of the mountain range that borders the valley.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MountainsNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double HEIGHT_ABOVE_VALLEY = 1000; // meters
    private static final Color COLOR = new Color( 81, 86, 90 ); // dark gray
    private static final double DX = 100; // meters
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MountainsNode( Valley valley, ModelViewTransform mvt, double minX, double maxX ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        GeneralPath path = createMountainPath( valley, mvt, minX, maxX, DX );
        PPath pathNode = new PPath( path );
        pathNode.setStroke( null );
        pathNode.setPaint( COLOR );
        addChild( pathNode );
    }
    
    /*
     * Creates a path that is a fixed height above the contour of the valley floor.
     * The parameters are specified in model coordinates.
     * The returned path is in view coordinates.
     * 
     * @param minX starting x coordinate (meters)
     * @param maxX ending x coordinate (meters)
     * @param dx interval between x samples (meters)
     * @return GeneralPath path in view coordinates
     */
    private static GeneralPath createMountainPath( Valley valley, ModelViewTransform mvt, final double minX, final double maxX, final double dx ) {
        
        assert( minX < maxX );
        assert( dx > 0 );
        
        GeneralPath path = new GeneralPath();
        Point2D pModel = new Point2D.Double();
        Point2D pView = new Point2D.Double();
        double x = minX;
        double elevation = 0;
        
        // approximate the mountains, from left to right
        while ( x <= maxX ) {
            elevation = valley.getElevation( x ) + HEIGHT_ABOVE_VALLEY;
            pModel.setLocation( x, elevation );
            pView = mvt.modelToView( pModel, pView );
            if ( x == minX ) {
                path.moveTo( (float) pView.getX(), (float) pView.getY() );
            }
            else {
                path.lineTo( (float) pView.getX(), (float) pView.getY() );
            }
            x += dx;
        }
        
        // vertical line down to sea level at x=end
        pModel.setLocation( x - dx, 0 );
        pView = mvt.modelToView( pModel, pView );
        path.lineTo( (float) pView.getX(), (float) pView.getY() );
        
        // horizontal line to sea level at x=start
        pModel.setLocation( 0, 0 );
        pView = mvt.modelToView( pModel, pView );
        path.lineTo( (float) pView.getX(), (float) pView.getY() );
        
        // close the path
        path.closePath();
        
        return path;
    }
}
