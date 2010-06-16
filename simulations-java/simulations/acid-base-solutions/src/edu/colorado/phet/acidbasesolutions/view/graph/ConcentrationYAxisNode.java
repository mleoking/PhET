/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * The log y-axis (tick marks and labels) for the Concentration bar graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationYAxisNode extends PComposite {
    
    private static final Font AXIS_LABEL_FONT = new PhetFont( 16 );
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    private static final double AXIS_LABEL_X_SPACING = 20;
    
    private final double tickSpacing;
    
    public ConcentrationYAxisNode( PDimension graphOutlineSize, int numberOfTicks, 
            double topMargin, int maxExponent, int exponentSpacing, double tickLength, 
            Stroke tickStroke, Color tickColor, Font labelFont, Color labelColor,
            Stroke gridlineStroke, Color gridlineColor ) {
        
        setPickable( false );
        setChildrenPickable( false );
        
        final double usableHeight = graphOutlineSize.getHeight() - topMargin;
        tickSpacing = usableHeight / ( numberOfTicks - 1 );
        
        double y = topMargin;
        int exponent = maxExponent;
        PComposite axisNode = new PComposite();
        addChild( axisNode );
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
                axisNode.addChild( labelNode );
            }

            PPath gridlineNode = new PPath( new Line2D.Double( +( tickLength / 2 ), y, graphOutlineSize.getWidth() - ( tickLength / 2 ), y ) );
            gridlineNode.setStroke( gridlineStroke );
            gridlineNode.setStrokePaint( gridlineColor );
            axisNode.addChild( gridlineNode );

            y += tickSpacing;
            exponent--;
        }
        
        // y-axis label
        PText labelNode = new PText( ABSStrings.CONCENTRATION_GRAPH_Y_AXIS );
        labelNode.rotate( -Math.PI / 2 );
        labelNode.setFont( AXIS_LABEL_FONT );
        labelNode.setTextPaint( AXIS_LABEL_COLOR );
        labelNode.setPickable( false );
        addChild( labelNode );
        
        // center the label on the axis
        double xOffset = axisNode.getFullBoundsReference().getX() - labelNode.getFullBoundsReference().getWidth() - AXIS_LABEL_X_SPACING;
        double yOffset = axisNode.getFullBoundsReference().getCenterY() + ( labelNode.getFullBoundsReference().getHeight() / 2 );
        labelNode.setOffset( xOffset, yOffset );
    }
    
    public double getTickSpacing() {
        return tickSpacing;
    }
}
