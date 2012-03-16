// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LogYAxisNode is the log y-axis (tick marks and labels) for the bar graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LogYAxisNode extends PComposite {
    
    private final double _tickSpacing;
    
    public LogYAxisNode( PDimension graphOutlineSize, int numberOfTicks, 
            double topMargin, int maxExponent, int exponentSpacing, double tickLength, 
            Stroke tickStroke, Color tickColor, Font labelFont, Color labelColor,
            Stroke gridlineStroke, Color gridlineColor ) {
        
        setPickable( false );
        setChildrenPickable( false );
        
        final double usableHeight = graphOutlineSize.getHeight() - topMargin;
        _tickSpacing = usableHeight / ( numberOfTicks - 1 );
        
        double y = topMargin;
        int exponent = maxExponent;
        for ( int i = 0; i < numberOfTicks; i++ ) {
            
            if ( i % exponentSpacing == 0 ) {
                
                PPath leftTickNode = new PPath( new Line2D.Double( -( tickLength / 2 ), y, +( tickLength / 2 ), y ) );
                leftTickNode.setStroke( tickStroke );
                leftTickNode.setStrokePaint( tickColor );
                addChild( leftTickNode );
                
                String s = "<html>10<sup>" + String.valueOf( exponent ) + "</sup></html>";
                HTMLNode labelNode = new HTMLNode( s );
                labelNode.setFont( labelFont );
                labelNode.setHTMLColor( labelColor );
                double xOffset = leftTickNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - 5;
                double yOffset = leftTickNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
                labelNode.setOffset( xOffset, yOffset );
                addChild( labelNode );
            }

            PPath gridlineNode = new PPath( new Line2D.Double( +( tickLength / 2 ), y, graphOutlineSize.getWidth() - ( tickLength / 2 ), y ) );
            gridlineNode.setStroke( gridlineStroke );
            gridlineNode.setStrokePaint( gridlineColor );
            addChild( gridlineNode );

            y += _tickSpacing;
            exponent--;
        }
    }
    
    public double getTickSpacing() {
        return _tickSpacing;
    }
}
