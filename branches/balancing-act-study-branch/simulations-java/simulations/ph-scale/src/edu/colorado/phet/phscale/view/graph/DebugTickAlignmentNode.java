// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * TickAlignmentNode draws a bunch of horizontal lines.
 * This can be used to check the vertical alignment of the pH slider ticks and the bar graph ticks.
 * We want them to be aligned to emphasize the relationship between pH and concentration.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugTickAlignmentNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color COLOR = Color.RED;
    
    public DebugTickAlignmentNode( int numberOfTicks, double tickSpacing, double width ) {
        setPickable( false );
        
        double y = 0;
        for ( int i = 0; i < numberOfTicks; i++ ) {
            
            PPath pathNode = new PPath( new Line2D.Double( 0, y, width, y) );
            pathNode.setStroke( STROKE );
            pathNode.setStrokePaint( COLOR );
            addChild( pathNode );
            
            y += tickSpacing;
        }
    }
}
