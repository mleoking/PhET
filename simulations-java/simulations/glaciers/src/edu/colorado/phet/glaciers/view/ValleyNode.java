/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.glaciers.GlaciersImages;
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
    
    private static final double DX = 100; // meters
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Valley _valley;
    private ModelViewTransform _mvt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ValleyNode( Valley valley, ModelViewTransform mvt, double minX, double maxX ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _valley = valley;
        _mvt = mvt;
        
        TexturePaint paint = new TexturePaint( GlaciersImages.DIRT_TEXTURE, new Rectangle2D.Double( 0, 0, 100, 100 ) );
        
        GeneralPath path = createValleyFloorPath( minX, maxX, DX );
        PPath pathNode = new PPath( path );
        pathNode.setStroke( null );
        pathNode.setPaint( paint );
        addChild( pathNode );
    }
    
    public void cleanup() {}
    
    /*
     * Creates a path that follows the contour of the valley floor.
     * The parameters are specified in model coordinates.
     * The returned path is in view coordinates.
     * 
     * @param minX starting x coordinate (meters)
     * @param maxX ending x coordinate (meters)
     * @param dx interval between x samples (meters)
     * @return GeneralPath path in view coordinates
     */
    private GeneralPath createValleyFloorPath( final double minX, final double maxX, final double dx ) {
        
        assert( minX < maxX );
        assert( dx > 0 );
        
        GeneralPath path = new GeneralPath();
        Point2D pModel = new Point2D.Double();
        Point2D pView = new Point2D.Double();
        double x = minX;
        double elevation = 0;
        
        // approximate the valley floor, from left to right
        while ( x <= maxX ) {
            elevation = _valley.getElevation( x );
            pModel.setLocation( x, elevation );
            pView = _mvt.modelToView( pModel, pView );
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
        pView = _mvt.modelToView( pModel, pView );
        path.lineTo( (float) pView.getX(), (float) pView.getY() );
        
        // horizontal line to sea level at x=start
        pModel.setLocation( 0, 0 );
        pView = _mvt.modelToView( pModel, pView );
        path.lineTo( (float) pView.getX(), (float) pView.getY() );
        
        // close the path
        path.closePath();
        
        return path;
    }
}
