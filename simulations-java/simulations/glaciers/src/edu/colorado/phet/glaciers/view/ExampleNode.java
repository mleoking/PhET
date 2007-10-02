/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
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
    
    private ExampleModelElement _modelElement;
    private ModelViewTransform _modelViewTransform;
    
    public ExampleNode( ExampleModelElement modelElement, ModelViewTransform modelViewTransform ) {
        super();
        
        _modelElement = modelElement;
        _modelElement.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final float r = (float) _modelViewTransform.modelToView( _modelElement.getRadius() );
        
        // pointer with origin at center
        GeneralPath path = new GeneralPath();
        path.moveTo( r, 0 );
        path.lineTo( 0.5f * r, r );
        path.lineTo( -r, r );
        path.lineTo( -r, -r );
        path.lineTo( 0.5f * r, -r );
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
        _modelElement.deleteObserver( this );
    }

    public void update( Observable o, Object arg ) {
        if ( o == _modelElement ) {
            updateNode();
        }
    }
    
    private void updateNode() {
        Point2D modelPosition = _modelElement.getPositionReference();
        double orientation = _modelElement.getOrientation();
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
        _modelElement.deleteObserver( this );
        _modelElement.setPosition( modelPoint );
        _modelElement.addObserver( this );
    }
}
