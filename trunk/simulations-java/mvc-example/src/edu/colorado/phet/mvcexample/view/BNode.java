/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.mvcexample.model.ModelViewTransform;
import edu.colorado.phet.mvcexample.model.BModelElement.BModelElementListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BNode is the visual representation of a BModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BNode extends PPath implements BModelElementListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color FILL_COLOR = Color.BLUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ModelViewTransform _modelViewTransform;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BNode( double modelWidth, double modelHeight, ModelViewTransform modelViewTransform ) {
        super();
        
        _modelViewTransform = modelViewTransform;
        
        // pointer with origin at geometric center
        final float w = (float) _modelViewTransform.modelToView( modelWidth );
        final float h = (float) _modelViewTransform.modelToView( modelHeight );
        GeneralPath path = new GeneralPath();
        path.moveTo( w/2, 0 );
        path.lineTo( w/4, h/2 );
        path.lineTo( -w/2, h/2 );
        path.lineTo( -w/2, -h/2 );
        path.lineTo( w/4, -h/2 );
        path.closePath();
        setPathTo( path );
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( FILL_COLOR );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() ); // unconstrained dragging
    }
    
    //----------------------------------------------------------------------------
    // BModelElementListener implementation (model changes)
    //----------------------------------------------------------------------------

    public void positionChanged(Point2D oldPosition, Point2D newPosition) {
        Point2D viewPosition = _modelViewTransform.modelToView( newPosition );
        setOffset( viewPosition );
    }
    
    public void orientationChanged(double oldOrientation, double newOrientation) {
        setRotation( newOrientation );
    }
}
