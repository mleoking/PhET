/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Draws a bullseye.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BullseyeNode extends PComposite {
    
    public BullseyeNode() {
        this( 15, Color.BLACK, new BasicStroke( 2f ) );
    }

    public BullseyeNode( double diameter, Color color, Stroke stroke ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        final double radius = diameter / 2;

        PPath innerNode = new PPath();
        innerNode.setPathTo( new Ellipse2D.Double( -radius/2, -radius/2, diameter/2, diameter/2 ) );
        innerNode.setStroke( null );
        innerNode.setPaint( color );
        addChild( innerNode );
        
        PPath outerNode = new PPath();
        outerNode.setPathTo( new Ellipse2D.Double( -radius, -radius, diameter, diameter ) );
        outerNode.setStroke( stroke );
        outerNode.setStrokePaint( color );
        addChild( outerNode );
    }
}
