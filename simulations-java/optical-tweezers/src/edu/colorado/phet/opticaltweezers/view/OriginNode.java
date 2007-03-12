/* Copyright 2006, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * OriginNode is the node used to indicate the (0,0) origin of a composite node.
 * A cross is drawn to indicate the origin point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OriginNode extends PhetPNode {

    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final double LENGTH = 10;
    
    public OriginNode( Color color ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // vertical line
        PPath vpath = new PPath();
        vpath.setPathTo( new Line2D.Double( 0, -LENGTH/2, 0, +LENGTH/2 ) );
        vpath.setStroke( STROKE );
        vpath.setStrokePaint( color );
        addChild( vpath );
        
        // horizontal line
        PPath hpath = new PPath();
        hpath.setPathTo( new Line2D.Double( -LENGTH/2, 0, +LENGTH/2, 0 ) );
        hpath.setStroke( STROKE );
        hpath.setStrokePaint( color );
        addChild( hpath );
    }
    
    public void setOffset( double x, double y ) {
        throw new UnsupportedOperationException( "don't do this to an OriginNode" );
    }
    
    public void setOffset( Point2D p ) {
        throw new UnsupportedOperationException( "don't do this to an OriginNode" );
    }
    
    public void setTransform( AffineTransform xform ) {
        throw new UnsupportedOperationException( "don't do this to an OriginNode" );
    }
}
