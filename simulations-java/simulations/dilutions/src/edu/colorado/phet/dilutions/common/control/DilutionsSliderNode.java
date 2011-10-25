// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler.Orientation;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.common.view.HorizontalTickMarkNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for vertical sliders in the Dilutions simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DilutionsSliderNode extends PhetPNode {

    // Slider for controlling amount of solute
    public static class SoluteAmountSliderNode extends DilutionsSliderNode {
        public SoluteAmountSliderNode( String title, String minLabel, String maxLabel, PDimension trackSize, final Property<Double> soluteAmount, DoubleRange range ) {
            super( title, minLabel, maxLabel, trackSize, soluteAmount, range );
        }
    }

    // Slider for controlling volume of solution
    public static class SolutionVolumeSliderNode extends DilutionsSliderNode {
        public SolutionVolumeSliderNode( String title, String minLabel, String maxLabel, PDimension trackSize, final Property<Double> solutionVolume,
                                         DoubleRange range ) {
            super( title, minLabel, maxLabel, trackSize, solutionVolume, range );
        }
    }

    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );

    // track
    private static final Color TRACK_FILL_COLOR = Color.BLACK;

    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 45, 15 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_NORMAL_COLOR = new Color( 89, 156, 212 );
    private static final Color KNOB_HIGHLIGHT_COLOR = KNOB_NORMAL_COLOR.brighter();
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;

    // tick marks
    private static final double TICK_LENGTH = ( KNOB_SIZE.getWidth() / 2 ) + 3;
    private static final PhetFont TICK_FONT = new PhetFont( 14 );

    private final LinearFunction function;
    private final TrackNode trackNode;
    private final ThumbNode thumbNode;

    public DilutionsSliderNode( String title, String minLabel, String maxLabel, PDimension trackSize, final Property<Double> modelValue,
                                DoubleRange range ) {

        this.function = new LinearFunction( range.getMin(), range.getMax(), trackSize.getHeight(), 0 );

        // nodes
        TitleNode titleNode = new TitleNode( title );
        trackNode = new TrackNode( trackSize, TRACK_FILL_COLOR );
        thumbNode = new ThumbNode( this, trackNode, range, new VoidFunction1<Double>() {
            public void apply( Double value ) {
                modelValue.set( value );
            }
        } );
        final HorizontalTickMarkNode maxNode = new HorizontalTickMarkNode( maxLabel, TICK_FONT, TICK_LENGTH );
        final HorizontalTickMarkNode minNode = new HorizontalTickMarkNode( minLabel, TICK_FONT, TICK_LENGTH );

        // rendering order
        {
            addChild( titleNode );
            addChild( maxNode );
            addChild( minNode );
            addChild( trackNode );
            addChild( thumbNode );
        }

        // layout
        {
            // max label at top of track
            maxNode.setOffset( 0, 0 );
            // min label at bottom of track
            minNode.setOffset( 0, trackSize.getHeight() );
            // title centered above track
            titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 trackNode.getFullBoundsReference().getMinY() - ( maxNode.getFullBoundsReference().getHeight() / 2 ) - 4 - titleNode.getFullBoundsReference().getHeight() );
            // thumb centered in track
            thumbNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                                 trackNode.getFullBoundsReference().getCenterY() );

        }

        modelValue.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                updateNode( value );
            }
        } );
    }

    private void updateNode( double value ) {
        // knob location
        thumbNode.setOffset( thumbNode.getXOffset(), function.evaluate( value ) );
    }

    // Title above the slider
    private static class TitleNode extends HTMLNode {
        public TitleNode( String html ) {
            super( html );
            setFont( TITLE_FONT );
        }
    }

    // The track that the thumb moves in. Origin is at upper-left corner.
    private static class TrackNode extends PPath {
        public TrackNode( PDimension size, Color fillColor ) {
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setPaint( fillColor );
        }
    }

    // The slider thumb, rounded rectangle with a horizontal line through the center. Origin is at the thumb's geometric center.
    private static class ThumbNode extends PComposite {

        public ThumbNode( PNode relativeNode, PNode trackNode, DoubleRange range, VoidFunction1<Double> updateFunction ) {

            PPath pathNode = new PPath() {{
                final double arcWidth = 0.25 * KNOB_SIZE.getWidth();
                setPathTo( new RoundRectangle2D.Double( -KNOB_SIZE.getWidth() / 2, -KNOB_SIZE.getHeight() / 2,
                                                        KNOB_SIZE.getWidth(), KNOB_SIZE.getHeight(),
                                                        arcWidth, arcWidth ) );
                setPaint( KNOB_NORMAL_COLOR );
                setStroke( KNOB_STROKE );
                setStrokePaint( KNOB_STROKE_COLOR );
            }};
            addChild( pathNode );

            PPath lineNode = new PPath() {{
                setPathTo( new Line2D.Double( -( KNOB_SIZE.getWidth() / 2 ) + 3, 0, ( KNOB_SIZE.getWidth() / 2 ) - 3, 0 ) );
                setStrokePaint( Color.WHITE );
            }};
            addChild( lineNode );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( pathNode, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new SliderThumbDragHandler( Orientation.VERTICAL, relativeNode, trackNode, this, range, updateFunction ) );
        }
    }
}
