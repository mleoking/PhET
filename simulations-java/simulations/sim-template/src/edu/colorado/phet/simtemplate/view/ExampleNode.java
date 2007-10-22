/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.ExampleModelElement.ExampleModelElementListener;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * ExampleNode is the visual representation of an ExampleModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleNode extends PPath implements ExampleModelElementListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleModelElement _modelElement;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleNode( ExampleModelElement modelElement ) {
        super();
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
        
        addInputEventListener( new CursorHandler() );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( ExampleNode.this.getParent() );
                Point2D p = _modelElement.getPosition();
                Point2D pNew = new Point2D.Double( p.getX() + delta.getWidth(), p.getY() + delta.getHeight() );
                _modelElement.setPosition( pNew );
            }
        } );
        
        sizeChanged();
        positionChanged();
        orientationChanged();
    }
    
    public void cleanup() {
        _modelElement.removeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Model changes
    //----------------------------------------------------------------------------

    public void sizeChanged() {
        // pointer with origin at geometric center
        Dimension size = _modelElement.getSize();
        final float w = (float) size.getWidth();
        final float h = (float) size.getHeight();
        GeneralPath path = new GeneralPath();
        path.moveTo( w / 2, 0 );
        path.lineTo( w / 4, h / 2 );
        path.lineTo( -w / 2, h / 2 );
        path.lineTo( -w / 2, -h / 2 );
        path.lineTo( w / 4, -h / 2 );
        path.closePath();
        setPathTo( path );
    }

    public void positionChanged() {
        setOffset( _modelElement.getPositionReference() );
    }

    public void orientationChanged() {
        setRotation( _modelElement.getOrientation() );
    }

}
