// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.graph;

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
public class BarNode extends PPath {
    
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
    
    private final double _barWidth;
    private final double _graphHeight;
    private final double _arrowHeadWidth;
    private final GeneralPath _barShape;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BarNode( double barWidth, Color color, double graphHeight ) {
        
        setPickable( false );
        setChildrenPickable( false );
        
        _barWidth = barWidth;
        _graphHeight = graphHeight;
        _arrowHeadWidth = barWidth + 15;
        
        _barShape = new GeneralPath();
        
        setPaint( color );
        setStroke( STROKE );
        setStrokePaint( STROKE_COLOR );
        
        setBarHeight( 1 );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setBarHeight( final double barHeight ) {
        
        _barShape.reset();
        
        if ( barHeight > _graphHeight ) {
            // rectangle with arrowhead at the top, origin at bottom center
            final double adjustedBarLength = _graphHeight - ( ( 1 - ARROW_PERCENT_ABOVE_GRAPH ) * ARROW_HEAD_HEIGHT );
            _barShape.moveTo( (float) -( _barWidth / 2 ), 0f );
            _barShape.lineTo( (float) ( _barWidth / 2 ), 0f );
            _barShape.lineTo( (float) ( _barWidth / 2 ), (float) -adjustedBarLength );
            _barShape.lineTo( (float) ( _arrowHeadWidth / 2 ), (float) -adjustedBarLength );
            _barShape.lineTo( 0f, (float) -( adjustedBarLength + ARROW_HEAD_HEIGHT ) );
            _barShape.lineTo( (float) -( _arrowHeadWidth / 2 ), (float) -adjustedBarLength );
            _barShape.lineTo( (float) -( _barWidth / 2 ), (float) -adjustedBarLength );
            _barShape.closePath();
        }
        else if ( barHeight > 0 ) {
            // rectangle, origin at bottom center
            _barShape.moveTo( (float) -( _barWidth / 2 ), 0f );
            _barShape.lineTo( (float) ( _barWidth / 2 ), 0f );
            _barShape.lineTo( (float) ( _barWidth / 2 ), (float) -barHeight );
            _barShape.lineTo( (float) -( _barWidth / 2 ), (float) -barHeight );
            _barShape.closePath();
        }
        
        setPathTo( _barShape );
    }
}
