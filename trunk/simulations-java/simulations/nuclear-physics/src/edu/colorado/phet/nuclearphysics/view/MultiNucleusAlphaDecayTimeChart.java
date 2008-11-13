/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.Polonium211CompositeNucleus;
import edu.colorado.phet.nuclearphysics.util.PhetButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * This class displays a "strip chart" of the decay time for multiple nuclei,
 * and also allows the user to adjust the half life when it is possible to do
 * so.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayTimeChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Total amount of time in milliseconds represented by this chart.
    private static final double TIME_SPAN = 3200;

    // Constants for controlling the appearance of the chart.
    private static final Color BORDER_COLOR = Color.DARK_GRAY;
    private static final float BORDER_STROKE_WIDTH = 6f;
    private static final Stroke BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = new Color( 246, 242, 175 );
    private static final float AXES_LINE_WIDTH = 0.5f;
    private static final Stroke AXES_STROKE = new BasicStroke( AXES_LINE_WIDTH );
    private static final Color AXES_LINE_COLOR = Color.BLACK;
    private static final double TICK_MARK_LENGTH = 3;
    private static final float TICK_MARK_WIDTH = 2;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke( TICK_MARK_WIDTH );
    private static final Font TICK_MARK_LABEL_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final Color TICK_MARK_COLOR = AXES_LINE_COLOR;
    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final float HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
    private static final Stroke HALF_LIFE_LINE_STROKE = new BasicStroke( HALF_LIFE_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f }, 0 );
    private static final Color HALF_LIFE_LINE_COLOR = new Color (0x990000);
    private static final Color HALF_LIFE_TEXT_COLOR = HALF_LIFE_LINE_COLOR;
    private static final Font HALF_LIFE_FONT = new PhetFont( Font.BOLD, 16 );

    // Constants that control the location of the origin.
    private static final double X_ORIGIN_PROPORTION = 0.30;
    private static final double Y_ORIGIN_PROPORTION = 0.65;

    // Tweakable values that can be used to adjust the way the time lines
    // appear on the charts.
    private static final double PRE_DECAY_TIME_LINE_POS_FRACTION = 0.33;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION = 0.55;
    private static final double TIMELINE_START_OFFEST = TICK_MARK_LENGTH * 4;

    // Half life of the nucleus we are displaying.
    private static final double HALF_LIFE = 516; // In milliseconds.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the nucleus whose decay is being charted.
    Polonium211CompositeNucleus _nucleus;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _halfLifeMarkerLine;
    private ArrowNode _xAxisOfGraph;
    private ArrowNode _yAxisOfGraph;
    private ArrayList _xAxisTickMarks;
    private ArrayList _xAxisTickMarkLabels;
    private ArrayList _yAxisTickMarks;
    private ArrayList _yAxisTickMarkLabels;
    private PText _xAxisLabel;
    private PText _yAxisLabel1;
    private PText _yAxisLabel2;
    private PText _halfLifeLabel;

    // Parent node that will be non-pickable and will contain all of the
    // non-interactive portions of the chart.
    private PComposite _nonPickableChartNode;

    // Variables used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;

    // Boolean that tracks whether decay has occurred.
    boolean _decayHasOccurred = false;

    // Factor for converting milliseconds to pixels.
    double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

    // Clock that we listen to for moving the time line and performing resets.
    ConstantDtClock _clock;

    // Flag for tracking if chart is cleared.
    boolean _chartCleared = false;

    // Button for resetting this chart.
    PhetButtonNode _resetButtonNode;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public MultiNucleusAlphaDecayTimeChart( ConstantDtClock clock, Polonium211CompositeNucleus nucleus ) {

        _clock = clock;
        _nucleus = nucleus;

        // Register as a clock listener.
        _clock.addClockListener( new ClockAdapter() {

            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTick( clockEvent );
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                _chartCleared = false;
                resetTimeLine();
            }
        } );

        // Register as a listener for the nucleus so that we can be informed
        // when it decays.
        _nucleus.addListener( new AtomicNucleus.Listener() {

            public void positionChanged() {
            // Do nothing, since we don't care about this.
            }

            public void atomicWeightChanged( AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts ) {
                if ( byProducts != null ) {
                }
            }
        } );

        // Set up the parent node that will contain the non-interactive
        // portions of the chart.
        _nonPickableChartNode = new PComposite();
        _nonPickableChartNode.setPickable( false );
        _nonPickableChartNode.setChildrenPickable( false );
        addChild( _nonPickableChartNode );

        // Create the border for this chart.
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        _nonPickableChartNode.addChild( _borderNode );

        // Create the x & y axes of the graph.  The initial position is arbitrary
        // and the actual positioning will be done by the update functions.
        _xAxisOfGraph = new ArrowNode( new Point2D.Double( 10, 10 ), new Point2D.Double( 20, 20 ), 9, 7, 1 );
        _xAxisOfGraph.setStroke( AXES_STROKE );
        _xAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _xAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _xAxisOfGraph );
        _yAxisOfGraph = new ArrowNode( new Point2D.Double(), new Point2D.Double(), 9, 7, 1 );
        _yAxisOfGraph.setStroke( AXES_STROKE );
        _yAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _yAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _yAxisOfGraph );

        // Add the tick marks and their labels to the X axis.
        int numTicksOnX = (int) Math.round( ( TIME_SPAN / 1000 ) + 1 );
        _xAxisTickMarks = new ArrayList( numTicksOnX );
        _xAxisTickMarkLabels = new ArrayList( numTicksOnX );
        DecimalFormat formatter = new DecimalFormat( "0.0" );
        for ( int i = 0; i < numTicksOnX; i++ ) {
            // Create the tick mark.  It will be positioned later.
            PPath tickMark = new PPath();
            tickMark.setStroke( TICK_MARK_STROKE );
            tickMark.setStrokePaint( TICK_MARK_COLOR );
            _xAxisTickMarks.add( tickMark );
            _nonPickableChartNode.addChild( tickMark );

            // Create the label for the tick mark.
            PText tickMarkLabel = new PText( formatter.format( i ) );
            tickMarkLabel.setFont( TICK_MARK_LABEL_FONT );
            _xAxisTickMarkLabels.add( tickMarkLabel );
            _nonPickableChartNode.addChild( tickMarkLabel );
        }

        // Add the tick marks and their labels to the Y axis.  There are only
        // two, one for the weight of Polonium and one for the weight of Lead.

        _yAxisTickMarks = new ArrayList( 2 );

        PPath yTickMark1 = new PPath();
        yTickMark1.setStroke( TICK_MARK_STROKE );
        yTickMark1.setStrokePaint( TICK_MARK_COLOR );
        _yAxisTickMarks.add( yTickMark1 );
        _nonPickableChartNode.addChild( yTickMark1 );

        PPath yTickMark2 = new PPath();
        yTickMark2.setStroke( TICK_MARK_STROKE );
        yTickMark2.setStrokePaint( TICK_MARK_COLOR );
        _yAxisTickMarks.add( yTickMark2 );
        _nonPickableChartNode.addChild( yTickMark2 );

        _yAxisTickMarkLabels = new ArrayList( 2 );

        PText yTickMarkLabel1 = new PText( NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER );
        yTickMarkLabel1.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add( yTickMarkLabel1 );
        _nonPickableChartNode.addChild( yTickMarkLabel1 );

        PText yTickMarkLabel2 = new PText( NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER );
        yTickMarkLabel2.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add( yTickMarkLabel2 );
        _nonPickableChartNode.addChild( yTickMarkLabel2 );

        // Add the text for the X & Y axes.
        _xAxisLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_X_AXIS_LABEL + " (" + NuclearPhysicsStrings.DECAY_TIME_UNITS + ")" );
        _xAxisLabel.setFont( LABEL_FONT );
        _nonPickableChartNode.addChild( _xAxisLabel );
        _yAxisLabel1 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL1 );
        _yAxisLabel1.setFont( LABEL_FONT );
        _yAxisLabel1.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel1 );
        _yAxisLabel2 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL2 );
        _yAxisLabel2.setFont( LABEL_FONT );
        _yAxisLabel2.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel2 );

        // Create the line that will illustrate where the half life is.
        _halfLifeMarkerLine = new PPath();
        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
        _halfLifeMarkerLine.setPaint( BACKGROUND_COLOR );
        _nonPickableChartNode.addChild( _halfLifeMarkerLine );

        // Create the label for the half life line.
        _halfLifeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_HALF_LIFE);
        _halfLifeLabel.setFont( HALF_LIFE_FONT );
        _halfLifeLabel.setTextPaint( HALF_LIFE_TEXT_COLOR );
        _nonPickableChartNode.addChild( _halfLifeLabel );

        // Add the button for resetting the chart.
        _resetButtonNode = new PhetButtonNode( NuclearPhysicsStrings.DECAY_TIME_CLEAR_CHART );
        _resetButtonNode.setPickable( true );
        addChild( _resetButtonNode );

        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                handleResetChartButtonPressed();
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation is changed.
     * 
     * @param 
     */
    private void updateBounds( Rectangle2D rect ) {

        // Recalculate the usable area and origin for the chart.
        _usableAreaOriginX = rect.getX() + BORDER_STROKE_WIDTH;
        _usableAreaOriginY = rect.getY() + BORDER_STROKE_WIDTH;
        _usableWidth = rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight = rect.getHeight() - ( BORDER_STROKE_WIDTH * 2 );

        // Decide where the origin is located.
        _graphOriginX = _usableAreaOriginX + ( X_ORIGIN_PROPORTION * _usableWidth );
        _graphOriginY = _usableAreaOriginY + ( Y_ORIGIN_PROPORTION * _usableHeight );

        // Update the multiplier used for converting from pixels to
        // milliseconds.  Use the multiplier to tweak the span of the x axis.
        _msToPixelsFactor = 0.65 * _usableWidth / TIME_SPAN;

        // Redraw the chart based on these recalculated values.
        update();
    }

    /**
     * Redraw the chart based on the current state.
     */
    private void update() {

        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( _usableAreaOriginX, _usableAreaOriginY, _usableWidth, _usableHeight, 20, 20 ) );

        // Position the x and y axes.
        _xAxisOfGraph.setTipAndTailLocations( 
        		new Point2D.Double( _graphOriginX + ( TIME_SPAN * _msToPixelsFactor ) + 10, _graphOriginY ), 
        		new Point2D.Double( _graphOriginX, _graphOriginY ) );
        _yAxisOfGraph.setTipAndTailLocations( 
        		new Point2D.Double( _graphOriginX, _graphOriginY - _usableHeight * 0.5 ), 
        		new Point2D.Double( _graphOriginX, _graphOriginY ) );

        // Position the tick marks and their labels on the X axis.
        for ( int i = 0; i < _xAxisTickMarks.size(); i++ ) {

            // Position the tick mark itself.
            PPath tickMark = (PPath) _xAxisTickMarks.get( i );
            double tickMarkPosX = _graphOriginX + TIMELINE_START_OFFEST + ( i * 1000 * _msToPixelsFactor );
            tickMark.setPathTo( new Line2D.Double( tickMarkPosX, _graphOriginY, tickMarkPosX, _graphOriginY - TICK_MARK_LENGTH ) );

            // Position the label for the tick mark.
            PText tickMarkLabel = (PText) _xAxisTickMarkLabels.get( i );
            double tickMarkLabelPosX = tickMarkPosX - ( tickMarkLabel.getWidth() / 2 );
            tickMarkLabel.setOffset( tickMarkLabelPosX, _graphOriginY );
        }

        // Position the tick marks and their labels on the Y axis.
        PPath yAxisLowerTickMark = (PPath) _yAxisTickMarks.get( 0 );
        yAxisLowerTickMark.setPathTo( new Line2D.Double( _graphOriginX, _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION ), _graphOriginX + TICK_MARK_LENGTH, _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION ) ) );

        PPath yAxisUpperTickMark = (PPath) _yAxisTickMarks.get( 1 );
        yAxisUpperTickMark.setPathTo( new Line2D.Double( _graphOriginX, _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ), _graphOriginX + TICK_MARK_LENGTH, _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) ) );

        PText yAxisLowerTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 0 );
        yAxisLowerTickMarkLabel.setOffset( _graphOriginX - ( 1.15 * yAxisLowerTickMarkLabel.getWidth() ), yAxisLowerTickMark.getY() - ( 0.5 * yAxisLowerTickMarkLabel.getHeight() ) );

        PText yAxisUpperTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 1 );
        yAxisUpperTickMarkLabel.setOffset( _graphOriginX - ( 1.15 * yAxisUpperTickMarkLabel.getWidth() ), yAxisUpperTickMark.getY() - ( 0.5 * yAxisUpperTickMarkLabel.getHeight() ) );

        // Position the labels for the axes.
        _xAxisLabel.setOffset( _usableAreaOriginX + _usableWidth - (_xAxisLabel.getWidth() * 1.2),
        		_graphOriginY + ( (PText) _xAxisTickMarkLabels.get( 0 ) ).getHeight() );
        _yAxisLabel2.setOffset( yAxisUpperTickMarkLabel.getOffset().getX() - ( 2.0 * _yAxisLabel1.getFont().getSize() ),
        		_graphOriginY );
        _yAxisLabel1.setOffset( _yAxisLabel2.getOffset().getX() - ( 1.1 * _yAxisLabel2.getFont().getSize() ),
        		_graphOriginY );

        // Position the marker for the half life.
        _halfLifeMarkerLine.reset();
        _halfLifeMarkerLine.moveTo( (float) ( _graphOriginX + TIMELINE_START_OFFEST + ( HALF_LIFE * _msToPixelsFactor ) ), (float) _graphOriginY );
        _halfLifeMarkerLine.lineTo( (float) ( _graphOriginX + TIMELINE_START_OFFEST + ( HALF_LIFE * _msToPixelsFactor ) ), (float) ( _usableAreaOriginY + ( 0.1 * _usableHeight ) ) );

        // Position the label for the half life.
        _halfLifeLabel.setOffset( _halfLifeMarkerLine.getX() - (_halfLifeLabel.getFullBoundsReference().width / 2),
                _graphOriginY + ( 0.5 * _halfLifeLabel.getFullBoundsReference().height ) );

        // Position the reset button.  Align it to the left side of the decay time label.
        _resetButtonNode.setOffset( _usableAreaOriginX + 10, 
        		_usableAreaOriginY + _usableHeight - _resetButtonNode.getFullBoundsReference().height - 10);
    }

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param rect - Position on the canvas where this chart should appear.
     */
    public void componentResized( Rectangle2D rect ) {
        updateBounds( rect );
    }

    /**
     * Move the appropriate time line forward on the chart and make other
     * time-driven updates to the chart.
     * 
     * @param clockEvent
     */
    private void handleClockTick( ClockEvent clockEvent ) {

    	// TODO: JPB TBD
    }

    /**
     * Reset the time lines back to 0.
     */
    private void resetTimeLine() {

        _decayHasOccurred = false;
        update();
    }

    /**
     * Reset the chart.
     */
    public void reset() {

        // Clear out the time line.
        resetTimeLine();

        // Clear the flag that holds off updates after the chart is cleared.
        _chartCleared = false;

        // Redraw the chart.
        update();
    }

    private void handleResetChartButtonPressed() {
    	// TODO: JPB TBD
    }
}
