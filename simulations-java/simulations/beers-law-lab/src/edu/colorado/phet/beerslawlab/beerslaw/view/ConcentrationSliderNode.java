// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.ConcentrationTransform;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Horizontal concentration slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationSliderNode extends PhetPNode {

    // Track properties
    private static final PDimension TRACK_SIZE = new PDimension( 200, 15 );

    // Thumb properties
    private static final PDimension THUMB_SIZE = new PDimension( 15, 35 );
    private static final Stroke THUMB_STROKE = new BasicStroke( 1f );
    private static final Color THUMB_NORMAL_COLOR = new Color( 89, 156, 212 );
    private static final Color THUMB_HIGHLIGHT_COLOR = THUMB_NORMAL_COLOR.brighter();
    private static final Color THUMB_STROKE_COLOR = Color.BLACK;
    private static final Color THUMB_CENTER_LINE_COLOR = Color.WHITE;

    // Tick properties
    private static final boolean TICKS_VISIBLE = true;
    private static final double TICK_LENGTH = 8;
    private static final PhetFont TICK_FONT = new PhetFont( 12 );
    private static final NumberFormat TICK_FORMAT = new DefaultDecimalFormat( "0" );

    public ConcentrationSliderNode( final Property<BeersLawSolution> solution ) {

        // track that the thumb moves in
        PNode trackNode = new TrackNode( UserComponents.concentrationSliderTrack, TRACK_SIZE, solution );

        // thumb that moves in the track
        PNode thumbNode = new ThumbNode( UserComponents.concentrationSliderThumb, THUMB_SIZE, TRACK_SIZE, this, trackNode, solution );

        // min and max tick marks
        final TickNode minNode = new TickNode( solution.get().concentrationRange.getMin() );
        final TickNode maxNode = new TickNode( solution.get().concentrationRange.getMax() );

        // rendering order
        {
            if ( TICKS_VISIBLE ) {
                addChild( maxNode );
                addChild( minNode );
            }
            addChild( trackNode );
            addChild( thumbNode );
        }

        // layout
        {
            // min label at left end of track
            minNode.setOffset( 0, trackNode.getFullBoundsReference().getMaxY() );
            // max label at right end of track
            maxNode.setOffset( TRACK_SIZE.getWidth(), trackNode.getFullBoundsReference().getMaxY() );
            // thumb vertically centered in track
            thumbNode.setOffset( thumbNode.getXOffset(), trackNode.getFullBoundsReference().getCenterY() );
        }

        // update the tick marks to match the solution
        solution.addObserver( new SimpleObserver() {
            public void update() {
                final DoubleRange concentrationRange = solution.get().concentrationRange;
                final ConcentrationTransform transform = solution.get().concentrationTransform;
                minNode.setValue( transform.modelToView( concentrationRange.getMin() ) );
                maxNode.setValue( transform.modelToView( concentrationRange.getMax() ) );
            }
        } );
    }

    // Track that the thumb moves in, origin at upper left. Clicking in the track changes the value.
    private static class TrackNode extends PPath {

        private Function viewToModel;

        public TrackNode( final IUserComponent userComponent, final PDimension trackSize, final Property<BeersLawSolution> solution ) {
            setPathTo( new Rectangle2D.Double( 0, 0, trackSize.getWidth(), trackSize.getHeight() ) );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new SimSharingDragHandler( userComponent, UserComponentTypes.slider ) {

                // Clicking in the track changes the model to the corresponding value.
                @Override protected void startDrag( PInputEvent event ) {
                    super.startDrag( event );
                    handleEvent( event );
                }

                // ... and after clicking in the track, you can continue to drag.
                @Override public void drag( PInputEvent event ) {
                    super.drag( event );
                    handleEvent( event );
                }

                private void handleEvent( PInputEvent event ) {
                    double trackPosition = event.getPositionRelativeTo( TrackNode.this ).getX();
                    final double concentration = viewToModel.evaluate( trackPosition );
                    solution.get().concentration.set( concentration );
                }
            } );

            // sync view with model
            solution.addObserver( new VoidFunction1<BeersLawSolution>() {
                public void apply( BeersLawSolution solution ) {

                    // change the view-to-model function to match the solution's concentration range
                    DoubleRange concentrationRange = solution.concentrationRange;
                    viewToModel = new LinearFunction( 0, trackSize.getWidth(), concentrationRange.getMin(), concentrationRange.getMax() );

                    // change track color to match solution's color range
                    setPaint( createPaint( solution, trackSize.getWidth() ) );
                }
            } );
        }

        // Computes a gradient that corresponds to the solution's concentration range.
        private static Paint createPaint( BeersLawSolution solution, double trackWidth ) {
            return new GradientPaint( 0, 0, solution.colorRange.getMin(), (float) trackWidth, 0, solution.colorRange.getMax() );
        }
    }

    // Tick mark, a vertical line with a label centered below it.
    private static class TickNode extends PComposite {

        private final PPath tickNode;
        private final PText textNode;

        public TickNode( double value ) {
            addChild( tickNode = new PPath( new Line2D.Double( 0, 0, 0, TICK_LENGTH ) ) ); // vertical tick
            addChild( textNode = new PhetPText( TICK_FONT ) );
            setValue( value );
        }

        public void setValue( double value ) {
            textNode.setText( TICK_FORMAT.format( value ) );
            // center text below tick
            textNode.setOffset( tickNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBoundsReference().getWidth() / 2 ),
                                tickNode.getFullBoundsReference().getMaxY() + 2 );
        }
    }

    // The slider thumb, a rounded rectangle with a vertical line through its center. Origin is at the thumb's geometric center.
    private static class ThumbNode extends PComposite {

        private Function modelToView;
        private ThumbDragHandler dragHandler;

        public ThumbNode( final IUserComponent userComponent, final PDimension thumbSize, final PDimension trackSize,
                          final PNode relativeNode, final PNode trackNode, final Property<BeersLawSolution> solution ) {

            PPath bodyNode = new PPath() {{
                final double arcWidth = 0.25 * thumbSize.getWidth();
                setPathTo( new RoundRectangle2D.Double( -thumbSize.getWidth() / 2, -thumbSize.getHeight() / 2, thumbSize.getWidth(), thumbSize.getHeight(), arcWidth, arcWidth ) );
                setPaint( THUMB_NORMAL_COLOR );
                setStroke( THUMB_STROKE );
                setStrokePaint( THUMB_STROKE_COLOR );
            }};

            PPath centerLineNode = new PPath() {{
                setPathTo( new Line2D.Double( 0, -( thumbSize.getHeight() / 2 ) + 3, 0, ( thumbSize.getHeight() / 2 ) - 3 ) );
                setStrokePaint( THUMB_CENTER_LINE_COLOR );
            }};

            // rendering order
            addChild( bodyNode );
            addChild( centerLineNode );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( bodyNode, THUMB_NORMAL_COLOR, THUMB_HIGHLIGHT_COLOR ) );

            // configure for a specific solution
            final VoidFunction1<BeersLawSolution> setSolution = new VoidFunction1<BeersLawSolution>() {
                public void apply( BeersLawSolution solution ) {

                    // drag handler with solution's concentration range
                    if ( dragHandler != null ) {
                        removeInputEventListener( dragHandler );
                    }
                    dragHandler = new ThumbDragHandler( userComponent, relativeNode, trackNode, ThumbNode.this, solution.concentrationRange, solution.concentration );
                    addInputEventListener( dragHandler );

                    // model-to-view function with solution's concentration range
                    modelToView = new LinearFunction( solution.concentrationRange.getMin(), solution.concentrationRange.getMax(), 0, trackSize.getWidth() );
                }
            };
            setSolution.apply( solution.get() );

            // move the slider thumb to reflect the concentration value
            final VoidFunction1<Double> concentrationObserver = new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    setOffset( modelToView.evaluate( value ), getYOffset() );
                }
            };
            solution.get().concentration.addObserver( concentrationObserver );

            // when the solution changes, wire up to the current solution
            solution.addObserver( new ChangeObserver<BeersLawSolution>() {
                public void update( BeersLawSolution newSolution, BeersLawSolution oldSolution ) {
                    setSolution.apply( newSolution );
                    oldSolution.concentration.removeObserver( concentrationObserver );
                    newSolution.concentration.addObserver( concentrationObserver );
                }
            } );
        }
    }

    // Drag handler for the slider thumb, with data collection support.
    private static class ThumbDragHandler extends SliderThumbDragHandler {

        private final Property<Double> modelValue;

        public ThumbDragHandler( IUserComponent userComponent, PNode relativeNode, PNode trackNode, PNode thumbNode,
                                 DoubleRange range, final Property<Double> modelValue ) {
            super( userComponent, false, Orientation.HORIZONTAL, relativeNode, trackNode, thumbNode, range,
                   new VoidFunction1<Double>() {
                       public void apply( Double value ) {
                           modelValue.set( value );
                       }
                   } );
            this.modelValue = modelValue;
        }

        @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return super.getParametersForAllEvents( event ).with( ParameterKeys.value, modelValue.get() );
        }
    }
}
