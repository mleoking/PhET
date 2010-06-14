/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BarNode is a bar in the bar graph.
 * The origin is at the bottom center.
 * If the bar's height exceeds the height of the graph, an arrow head is drawn
 * on the top of the bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationBarNode extends PPath {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Stroke STROKE = null;
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final double ARROW_HEAD_HEIGHT = 60;
    private static final double ARROW_PERCENT_ABOVE_GRAPH = 0.15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double barWidth;
    private final double graphHeight;
    private final double arrowHeadWidth;
    private final GeneralPath barShape;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationBarNode( double barWidth, Color color, double graphHeight ) {
        
        setPickable( false );
        setChildrenPickable( false );
        
        this.barWidth = barWidth;
        this.graphHeight = graphHeight;
        this.arrowHeadWidth = barWidth + 15;
        
        barShape = new GeneralPath();
        
        setPaint( color );
        setStroke( STROKE );
        setStrokePaint( STROKE_COLOR );
        
        setBarHeight( 1 );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setBarHeight( final double barHeight ) {
        
        barShape.reset();
        
        if ( barHeight > graphHeight ) {
            // rectangle with arrowhead at the top, origin at bottom center
            final double adjustedBarLength = graphHeight - ( ( 1 - ARROW_PERCENT_ABOVE_GRAPH ) * ARROW_HEAD_HEIGHT );
            barShape.moveTo( (float) -( barWidth / 2 ), 0f );
            barShape.lineTo( (float) ( barWidth / 2 ), 0f );
            barShape.lineTo( (float) ( barWidth / 2 ), (float) -adjustedBarLength );
            barShape.lineTo( (float) ( arrowHeadWidth / 2 ), (float) -adjustedBarLength );
            barShape.lineTo( 0f, (float) -( adjustedBarLength + ARROW_HEAD_HEIGHT ) );
            barShape.lineTo( (float) -( arrowHeadWidth / 2 ), (float) -adjustedBarLength );
            barShape.lineTo( (float) -( barWidth / 2 ), (float) -adjustedBarLength );
            barShape.closePath();
        }
        else if ( barHeight > 0 ) {
            // rectangle, origin at bottom center
            barShape.moveTo( (float) -( barWidth / 2 ), 0f );
            barShape.lineTo( (float) ( barWidth / 2 ), 0f );
            barShape.lineTo( (float) ( barWidth / 2 ), (float) -barHeight );
            barShape.lineTo( (float) -( barWidth / 2 ), (float) -barHeight );
            barShape.closePath();
        }
        
        setPathTo( barShape );
    }
}
