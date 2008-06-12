/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class PHSliderNode extends PNode {
    
    // whether the max is on top or bottom
    private static final boolean MAX_AT_TOP = true;
    
    // Range of the slider
    private static final IntegerRange RANGE = new IntegerRange( -1, 15, 7 );

    // Track
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Color ACID_COLOR = PHScaleConstants.H3O_COLOR;
    private static final Color BASE_COLOR = PHScaleConstants.OH_COLOR;
    
    // Knob
    private static final Color KNOB_FILL_COLOR = Color.WHITE;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    private static final int KNOB_STROKE_WIDTH = 1;
    
    // Ticks
    private static final double TICK_LENGTH = 10;
    private static final int TICK_SPACING = 2;
    private static final Font TICK_FONT = new PhetFont( 16 );
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = Color.BLACK;
    private static final double TICK_LABEL_SPACING = 2;

    // Labels
    private static final Font ACID_BASE_FONT = new PhetFont( 16 );

    private final TrackNode _trackNode;
    private final KnobNode _knobNode;
    private double _pH;
    
    public PHSliderNode( PDimension trackSize, PDimension knobSize ) {
        super();
        
        _trackNode = new TrackNode( trackSize );
        _knobNode = new KnobNode( knobSize );
        
        PComposite tickNodes = new PComposite();
        TickMarkNode tickMarkNode = null;
        final double xOffset = 0;
        double yOffset = 0;
        final double yDelta = trackSize.getHeight() / ( RANGE.getMax() - RANGE.getMin() );
        if ( MAX_AT_TOP ) {
            for ( int i = RANGE.getMax(); i >= RANGE.getMin(); i-- ) {
                if ( i % TICK_SPACING == 0 ) {
                    tickMarkNode = new TickMarkNode( String.valueOf( i ) );
                }
                else {
                    tickMarkNode = new TickMarkNode();
                }
                tickNodes.addChild( tickMarkNode );
                tickMarkNode.setOffset( xOffset, yOffset );
                yOffset += yDelta;
            }
        }
        else {
            for ( int i = RANGE.getMin(); i <= RANGE.getMax(); i++ ) {
                if ( i % TICK_SPACING == 0 ) {
                    tickMarkNode = new TickMarkNode( String.valueOf( i ) );
                }
                else {
                    tickMarkNode = new TickMarkNode();
                }
                tickNodes.addChild( tickMarkNode );
                tickMarkNode.setOffset( xOffset, yOffset );
                yOffset += yDelta;
            }
        }
        
        PText acidLabelNode = new PText( PHScaleStrings.LABEL_ACID );
        acidLabelNode.setFont( ACID_BASE_FONT );
        acidLabelNode.rotate( -Math.PI / 2 );
        
        PText baseLabelNode = new PText( PHScaleStrings.LABEL_BASE );
        baseLabelNode.setFont( ACID_BASE_FONT );
        baseLabelNode.rotate( -Math.PI / 2 );

        addChild( _trackNode );
        addChild( acidLabelNode );
        addChild( baseLabelNode );
        addChild( tickNodes );
        addChild( _knobNode );
        
        _trackNode.setOffset( 0, 0 ); // origin at the upper-left corner of the track
        tickNodes.setOffset( _trackNode.getFullBoundsReference().getMaxX() + 5, _trackNode.getYOffset() );
        _knobNode.setOffset( _trackNode.getFullBoundsReference().getMaxX() - ( 0.75 * _knobNode.getFullBoundsReference().getWidth() ), 0 );
        final int xSpacing = 4; // space between range labels and track
        final int yMargin = 5; // range labels are offset this amount from ends of track
        if ( MAX_AT_TOP ) {
            acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + xSpacing ), _trackNode.getFullBoundsReference().getHeight() - yMargin ); // bottom left of the track
            baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + xSpacing ), baseLabelNode.getFullBoundsReference().getHeight() + yMargin ); // top left of the track
        }
        else {
            baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + xSpacing ), _trackNode.getFullBoundsReference().getHeight() - yMargin ); // bottom left of the track
            acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + xSpacing ), acidLabelNode.getFullBoundsReference().getHeight() + yMargin ); // top left of the track    
        }
        
        // initialize
        setPH( RANGE.getDefault() );
    }
    
    public double getPH() {
        return _pH;
    }
    
    public void setPH( double pH ) {
        if ( pH < RANGE.getMin() || pH > RANGE.getMax() ) {
            throw new IllegalArgumentException( "pH is out of range: " + pH );
        }
        if ( pH != _pH ) {
            double xOffset = _knobNode.getXOffset();
            double yOffset = ( ( RANGE.getMax() - pH ) / ( RANGE.getMax() - RANGE.getMin() ) ) * _trackNode.getFullBoundsReference().getHeight();
            _knobNode.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * The slider track, filled with a gradient that indicates the transition from acid to base.
     */
    private static class TrackNode extends PNode {
        public TrackNode( PDimension size ) {
            super();
            PPath pathNode = new PPath();
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            Paint trackPaint = null;
            if ( MAX_AT_TOP ) {
                trackPaint = new GradientPaint( 0f, 0f, BASE_COLOR, 0f, (float)size.getHeight(), ACID_COLOR );
            }
            else {
                trackPaint = new GradientPaint( 0f, 0f, ACID_COLOR, 0f, (float)size.getHeight(), BASE_COLOR );
            }
            pathNode.setPaint( trackPaint );
            pathNode.setStroke( TRACK_STROKE );
            pathNode.setStrokePaint( TRACK_COLOR );
            addChild( pathNode );
        }
    }
    
    /*
     * The slider knob, origin at tip.
     */
    private static class KnobNode extends PNode {
        public KnobNode( PDimension size ) {

            // tip of knob points down
            float w = (float) size.getWidth();
            float h = (float) size.getHeight();
            GeneralPath knobPath = new GeneralPath();
            knobPath.moveTo( 0f, 0f );
            knobPath.lineTo( w, 0f );
            knobPath.lineTo( w, 0.65f * h );
            knobPath.lineTo( w / 2f, h );
            knobPath.lineTo( 0f, 0.65f * h );
            knobPath.closePath();

            PPath pathNode = new PPath();
            pathNode.setPathTo( knobPath );
            pathNode.setPaint( KNOB_FILL_COLOR );
            pathNode.setStroke( new BasicStroke( KNOB_STROKE_WIDTH ) );
            pathNode.setStrokePaint( KNOB_STROKE_COLOR );
            addChild( pathNode );
            
            // tip of knob points to the right
            rotate( -Math.PI/2 );
        }
    }
    
    private static class TickMarkNode extends PComposite {
        
        public TickMarkNode() {
            this( null );
        }
        
        public TickMarkNode( String label ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            PPath tickNode = new PPath( new Line2D.Double( 0, 0, TICK_LENGTH, 0 ) );
            tickNode.setStroke( TICK_STROKE );
            tickNode.setStrokePaint( TICK_COLOR );
            tickNode.setOffset( 0, 0 );
            addChild( tickNode );
            
            PText labelNode = null;
            if ( label != null ) {
                labelNode = new PText( label );
                labelNode.setFont( TICK_FONT );
                labelNode.setOffset( tickNode.getFullBoundsReference().getMaxX() + TICK_LABEL_SPACING, -labelNode.getFullBoundsReference().getHeight() / 2 );
                addChild( labelNode );
            }
        }
    }
}
