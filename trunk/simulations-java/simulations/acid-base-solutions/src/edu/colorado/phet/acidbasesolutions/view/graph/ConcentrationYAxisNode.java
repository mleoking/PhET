// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.BasicStroke;
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
/* package private */ class ConcentrationYAxisNode extends PComposite {
    
    // axes
    private static final Font AXIS_LABEL_FONT = new PhetFont( 20 );
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    private static final double AXIS_LABEL_X_SPACING = 20;
    
    // ticks
    private static final double TICK_LENGTH = 6;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Font TICK_LABEL_FONT = new PhetFont( 14 );
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    
    // horizontal gridlines
    private static final Stroke GRIDLINE_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 ); // dashed
    private static final Color GRIDLINE_COLOR = new Color( 192, 192, 192 ); // gray
    
    /**
     * Constructor.
     * 
     * @param graphOutlineSize size of the outline around the graph's data area
     * @param numberOfTicks number of tick marks
     * @param topMargin vertical distance between the top tick and the top of the graph's data area
     * @param maxExponent exponent of the maximum tick mark
     * @param exponentDelta delta between tick mark exponents
     */
    public ConcentrationYAxisNode( PDimension graphOutlineSize, int numberOfTicks, double topMargin, int maxExponent, int exponentDelta ) {
        
        setPickable( false );
        setChildrenPickable( false );
        
        final double usableHeight = graphOutlineSize.getHeight() - topMargin;
        final double tickYSpacing = usableHeight / ( numberOfTicks - 1 );
        
        double y = topMargin;
        int exponent = maxExponent;
        PComposite axisNode = new PComposite();
        addChild( axisNode );
        for ( int i = 0; i < numberOfTicks; i++ ) {
            
            if ( i % exponentDelta == 0 ) {
                
                PPath leftTickNode = new PPath( new Line2D.Double( -( TICK_LENGTH / 2 ), y, +( TICK_LENGTH / 2 ), y ) );
                leftTickNode.setStroke( TICK_STROKE );
                leftTickNode.setStrokePaint( TICK_COLOR );
                addChild( leftTickNode );
                
                String s = "<html>10<sup>" + String.valueOf( exponent ) + "</sup></html>";
                HTMLNode labelNode = new HTMLNode( s );
                labelNode.setFont( TICK_LABEL_FONT );
                labelNode.setHTMLColor( TICK_LABEL_COLOR );
                double xOffset = leftTickNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - 5;
                double yOffset = leftTickNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
                labelNode.setOffset( xOffset, yOffset );
                axisNode.addChild( labelNode );
            }

            PPath gridlineNode = new PPath( new Line2D.Double( +( TICK_LENGTH / 2 ), y, graphOutlineSize.getWidth() - ( TICK_LENGTH / 2 ), y ) );
            gridlineNode.setStroke( GRIDLINE_STROKE );
            gridlineNode.setStrokePaint( GRIDLINE_COLOR );
            axisNode.addChild( gridlineNode );

            y += tickYSpacing;
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
}
