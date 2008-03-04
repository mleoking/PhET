/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

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
    
    public ExampleNode() {
        super();
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( Color.ORANGE );
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
