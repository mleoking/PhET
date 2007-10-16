/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.mvcexample.model.CModelElement;
import edu.colorado.phet.mvcexample.model.ModelViewTransform;
import edu.colorado.phet.mvcexample.model.CModelElement.CModelElementListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * CNode is the visual representation of a CModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CNode extends PPath implements CModelElementListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color FILL_COLOR = Color.GREEN;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CModelElement _modelElement;
    private ModelViewTransform _modelViewTransform;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CNode( CModelElement modelElement, ModelViewTransform modelViewTransform ) {
        super();
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        _modelViewTransform = modelViewTransform;
        
        // pointer with origin at geometric center
        final float w = (float) _modelViewTransform.modelToView( _modelElement.getWidth() );
        final float h = (float) _modelViewTransform.modelToView( _modelElement.getHeight() );
        GeneralPath path = new GeneralPath();
        path.moveTo( w / 2, 0 );
        path.lineTo( w / 4, h / 2 );
        path.lineTo( -w / 2, h / 2 );
        path.lineTo( -w / 2, -h / 2 );
        path.lineTo( w / 4, -h / 2 );
        path.closePath();
        setPathTo( path );
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( FILL_COLOR );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() ); // unconstrained dragging
        addInputEventListener( new PBasicInputEventHandler() {

            private double _xOffset, _yOffset; // distance from mouse press to modelElement's position

            public void mousePressed( PInputEvent event ) {
                Point2D modelPosition = _modelElement.getPositionReference();
                Point2D viewPosition = _modelViewTransform.modelToView( modelPosition );
                _xOffset = event.getPosition().getX() - viewPosition.getX();
                _yOffset = event.getPosition().getY() - viewPosition.getY();
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D viewPosition = event.getPosition();
                viewPosition.setLocation( viewPosition.getX() - _xOffset, viewPosition.getY() - _yOffset );
                Point2D modelPosition = _modelViewTransform.viewToModel( viewPosition );
                _modelElement.setPosition( modelPosition );
            }
        } );
        
        positionChanged();
        orientationChanged();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _modelElement.removeListener( this );
    }

    //----------------------------------------------------------------------------
    // CModelElementListener implementation
    //----------------------------------------------------------------------------
    
    public void orientationChanged() {
        setRotation( _modelElement.getOrientation() );
    }

    public void positionChanged() {
        Point2D modelPosition = _modelElement.getPositionReference();
        Point2D viewPosition = _modelViewTransform.modelToView( modelPosition );
        setOffset( viewPosition );
    }
}
