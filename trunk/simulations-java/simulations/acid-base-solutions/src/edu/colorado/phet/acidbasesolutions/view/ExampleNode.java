/* Copyright 2007, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.acidbasesolutions.model.ExampleModelElement;
import edu.colorado.phet.acidbasesolutions.model.ExampleModelElement.ExampleModelElementListener;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * ExampleNode is the visual representation of an ExampleModelElement.
 * Note that the node has no knowledge of any model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleNode extends PPath {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------   
    
    private final ExampleModelElement _modelElement;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleNode( ExampleModelElement modelElement ) {
        super();
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
        
        _modelElement = modelElement;
        
        // When the model element changes, update this node.
        modelElement.addExampleModelElementListener( new ExampleModelElementListener() {

            public void orientationChanged() {
                setOrientation( _modelElement.getOrientation() );
            }

            public void positionChanged() {
                setPosition( _modelElement.getPositionReference() );
            }
        });
        
        // When this node is dragged, update the model element.
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                Point2D p = _modelElement.getPosition();
                Point2D pNew = new Point2D.Double( p.getX() + delta.getWidth(), p.getY() + delta.getHeight() );
                _modelElement.setPosition( pNew );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public void setSize( double width, double height ) {
        // pointer with origin at geometric center
        final float w = (float) width;
        final float h = (float) height;
        GeneralPath path = new GeneralPath();
        path.moveTo( w / 2, 0 );
        path.lineTo( w / 4, h / 2 );
        path.lineTo( -w / 2, h / 2 );
        path.lineTo( -w / 2, -h / 2 );
        path.lineTo( w / 4, -h / 2 );
        path.closePath();
        setPathTo( path );
    }
    
    public void setPosition( Point2D position ) {
        setOffset( position );
    }

    public void setOrientation( double orientation ) {
        setRotation( orientation );
    }
}
