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
import edu.colorado.phet.glaciers.model.ExampleModelElement.ExampleModelElementListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ExampleNode is the visual representation of an ExampleModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleNode extends PPath implements ExampleModelElementListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ModelViewTransform _modelViewTransform;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleNode( ModelViewTransform modelViewTransform ) {
        super();
        
        _modelViewTransform = modelViewTransform;
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() ); // unconstrained dragging
    }
    
    //----------------------------------------------------------------------------
    // Model changes
    //----------------------------------------------------------------------------

    public void sizeChanged(Dimension oldSize, Dimension newSize) {
        // pointer with origin at geometric center
        final float w = (float) _modelViewTransform.modelToView( newSize.getWidth() );
        final float h = (float) _modelViewTransform.modelToView( newSize.getHeight() );
        GeneralPath path = new GeneralPath();
        path.moveTo( w/2, 0 );
        path.lineTo( w/4, h/2 );
        path.lineTo( -w/2, h/2 );
        path.lineTo( -w/2, -h/2 );
        path.lineTo( w/4, -h/2 );
        path.closePath();
        setPathTo( path );
    }
    
    public void positionChanged(Point2D oldPosition, Point2D newPosition) {
        Point2D viewPosition = _modelViewTransform.modelToView( newPosition );
        setOffset( viewPosition );
    }
    
    public void orientationChanged(double oldOrientation, double newOrientation) {
        setRotation( newOrientation );
    }

}
