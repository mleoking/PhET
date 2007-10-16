/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.mvcexample.model.AModelElement;
import edu.colorado.phet.mvcexample.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ANode is the visual representation of an AModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ANode extends PPath {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color FILL_COLOR = Color.ORANGE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AModelElement _modelElement;
    private ModelViewTransform _modelViewTransform;
    
    private ModelObserver _modelObserver;
    private ViewObserver _viewObserver;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ANode( AModelElement modelElement, ModelViewTransform modelViewTransform ) {
        super();
        
        _modelObserver = new ModelObserver();
        _viewObserver = new ViewObserver();
        
        _modelElement = modelElement;
        _modelElement.addObserver( _modelObserver );
        
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
        addPropertyChangeListener( _viewObserver ); // update model when node is dragged
        
        updateViewPosition();
        updateViewOrientation();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _modelElement.deleteObserver( _modelObserver );
        removePropertyChangeListener( _viewObserver );
    }

    //----------------------------------------------------------------------------
    // Model changes
    //----------------------------------------------------------------------------
    
    private class ModelObserver implements Observer {

        public void update( Observable o, Object arg ) {
            if ( o == _modelElement ) {
                if ( arg == AModelElement.PROPERTY_POSITION ) {
                    updateViewPosition();
                }
                else if ( arg == AModelElement.PROPERTY_ORIENTATION ) {
                    updateViewOrientation();
                }
            }
        }
    }
    
    private void updateViewPosition() {
        Point2D modelPosition = _modelElement.getPositionReference();
        Point2D viewPosition = _modelViewTransform.modelToView( modelPosition );
        removePropertyChangeListener( _viewObserver );
        setOffset( viewPosition );
        addPropertyChangeListener( _viewObserver );
    }
    
    private void updateViewOrientation() {
        double orientation = _modelElement.getOrientation();
        removePropertyChangeListener( _viewObserver );
        setRotation( orientation );
        addPropertyChangeListener( _viewObserver );
    }
    
    //----------------------------------------------------------------------------
    // View changes
    //----------------------------------------------------------------------------
    
    private class ViewObserver implements PropertyChangeListener {
        public void propertyChange( PropertyChangeEvent event ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                updateModelPosition();
            }
        }
    }
    
    private void updateModelPosition() {
        Point2D viewPoint = getOffset();
        Point2D modelPoint = _modelViewTransform.viewToModel( viewPoint );
        _modelElement.deleteObserver( _modelObserver );
        _modelElement.setPosition( modelPoint );
        _modelElement.addObserver( _modelObserver );
    }
}
