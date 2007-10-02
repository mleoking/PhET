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
    
    private ExampleModelElement _exampleModelElement;
    private ModelViewTransform _modelViewTransform;
    
    public ExampleNode( ExampleModelElement exampleModelElement, ModelViewTransform modelViewTransform ) {
        super();
        
        _exampleModelElement = exampleModelElement;
        _exampleModelElement.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        // pointer with origin at center
        Dimension size = _exampleModelElement.getSize();
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
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() ); // unconstrained dragging
        addPropertyChangeListener( this ); // update model when view changes
        
        updateNode();
    }
    
    public void cleanup() {
        _exampleModelElement.deleteObserver( this );
    }

    public void update( Observable o, Object arg ) {
        if ( o == _exampleModelElement ) {
            updateNode();
        }
    }
    
    private void updateNode() {
        Point2D modelPosition = _exampleModelElement.getPositionReference();
        double orientation = _exampleModelElement.getOrientation();
        Point2D viewPosition = _modelViewTransform.modelToView( modelPosition );
        removePropertyChangeListener( this );
        setRotation( orientation );
        setOffset( viewPosition );
        addPropertyChangeListener( this );
    }
    
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
            updateModel();
        }
    }
    
    private void updateModel() {
        Point2D viewPoint = getOffset();
        Point2D modelPoint = _modelViewTransform.viewToModel( viewPoint );
        _exampleModelElement.deleteObserver( this );
        _exampleModelElement.setPosition( modelPoint );
        _exampleModelElement.addObserver( this );
    }
}
