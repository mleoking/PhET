/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Draws a cross.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CrossNode extends PComposite {
    
    public CrossNode() {
        this( 15, Color.RED, new BasicStroke( 3f ) );
    }

    public CrossNode( double length, Color color, Stroke stroke ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        PPath verticalLineNode = new PPath( new Line2D.Double( 0, -length / 2, 0, length / 2 ) );
        verticalLineNode.setStroke( stroke );
        verticalLineNode.setStrokePaint( color );
        addChild( verticalLineNode );

        PPath horizontalLineNode = new PPath( new Line2D.Double( -length / 2, 0, length / 2, 0 ) );
        horizontalLineNode.setStroke( stroke );
        horizontalLineNode.setStrokePaint( color );
        addChild( horizontalLineNode );
    }
}
