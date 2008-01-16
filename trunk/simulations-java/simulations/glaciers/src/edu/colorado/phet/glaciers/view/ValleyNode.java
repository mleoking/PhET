/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Valley;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ValleyNode is the visual representation of a valley.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ValleyNode extends PComposite {

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
        
        _valley = valley;
        _mvt = mvt;
        
        PImage imageNode = new PImage( GlaciersImages.VALLEY );
        addChild( imageNode );
        setPickable( false );
        
        //XXX position this node's upper-left corner at the highest point in the valley, at x=0
        double x = 0;
        double y = _valley.getElevation( x );
        Point2D highPoint =  new Point2D.Double( x, y );
        Point2D pView = _mvt.modelToView( highPoint );
        setOffset( pView );
    }
    
    public void cleanup() {}
}
