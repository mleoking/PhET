/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ExampleNode is the visual representation of an ExampleModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleNode extends PPath implements Observer, PropertyChangeListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleModelElement _exampleModelElement;
    private ModelViewTransform _modelViewTransform;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleNode( ExampleModelElement exampleModelElement, ModelViewTransform modelViewTransform ) {
        super();
        
        _exampleModelElement = exampleModelElement;
        _exampleModelElement.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() ); // unconstrained dragging
        addPropertyChangeListener( this ); // update model when node is dragged
        
        updateNodeSize();
        updateNodePositionAndOrientation();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _exampleModelElement.deleteObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the node when the model changes.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _exampleModelElement ) {
            if ( arg == ExampleModelElement.PROPERTY_SIZE ) {
                updateNodeSize();
            }
            if ( arg == ExampleModelElement.PROPERTY_POSITION ) {
                updateNodePositionAndOrientation();
            }
            if ( arg == ExampleModelElement.PROPERTY_ORIENTATION ) {
                updateNodePositionAndOrientation();
            }
        }
    }
    
    private void updateNodeSize() {
        // pointer with origin at geometric center
        Dimension size = _exampleModelElement.getSizeReference();
        final float w = (float) _modelViewTransform.modelToView( size.getWidth() );
        final float h = (float) _modelViewTransform.modelToView( size.getHeight() );
        GeneralPath path = new GeneralPath();
        path.moveTo( w/2, 0 );
        path.lineTo( w/4, h/2 );
        path.lineTo( -w/2, h/2 );
        path.lineTo( -w/2, -h/2 );
        path.lineTo( w/4, -h/2 );
        path.closePath();
        setPathTo( path );
    }
    
    private void updateNodePositionAndOrientation() {
        Point2D modelPosition = _exampleModelElement.getPositionReference();
        double orientation = _exampleModelElement.getOrientation();
        Point2D viewPosition = _modelViewTransform.modelToView( modelPosition );
        removePropertyChangeListener( this );
        setRotation( orientation );
        setOffset( viewPosition );
        addPropertyChangeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // PropertyChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the model when the node changes.
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
            updateModelPosition();
        }
    }
    
    private void updateModelPosition() {
        Point2D viewPoint = getOffset();
        Point2D modelPoint = _modelViewTransform.viewToModel( viewPoint );
        _exampleModelElement.deleteObserver( this );
        _exampleModelElement.setPosition( modelPoint );
        _exampleModelElement.addObserver( this );
    }
}
