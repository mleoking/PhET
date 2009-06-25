/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.ExampleModelElement.ExampleModelElementListener;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ExampleNode is the visual representation of an ExampleModelElement.
 * Note that the node has no knowledge of any model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleNode extends PPath {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleNode( final ExampleModelElement modelElement ) {
        super();
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
        
        modelElement.addExampleModelElementListener( new ExampleModelElementListener() {

            public void orientationChanged() {
                setOrientation( modelElement.getOrientation() );
            }

            public void positionChanged() {
                setPosition( modelElement.getPositionReference() );
            }
        });
        
        setSize( modelElement.getWidth(), modelElement.getHeight() );
        setPosition( modelElement.getPositionReference() );
        setOrientation( modelElement.getOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    private void setSize( double width, double height ) {
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
    
    private void setPosition( Point2D position ) {
        setOffset( position );
    }

    private void setOrientation( double orientation ) {
        setRotation( orientation );
    }
}
