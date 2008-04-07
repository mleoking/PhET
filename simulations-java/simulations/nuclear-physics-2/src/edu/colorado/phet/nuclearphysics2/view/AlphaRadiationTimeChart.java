/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

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
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.util.PhetButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * This class displays a "strip chart" of the decay time for the nucleus,
 * and it also shows the time at which the decay occurred over multiple
 * trials.
 *
 * @author John Blanco
 */
public class AlphaRadiationTimeChart extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Total amount of time in milliseconds represented by this chart.
    private static final double TIME_SPAN = 6000;
    
    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 6f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.WHITE;
    private static final float   AXES_LINE_WIDTH = 1.0f;
    private static final Stroke  AXES_STROKE = new BasicStroke( AXES_LINE_WIDTH );
    private static final double  TICK_MARK_LENGTH = 3;
    private static final float   TICK_MARK_WIDTH = 2;
    private static final Stroke  TICK_MARK_STROKE = new BasicStroke( TICK_MARK_WIDTH );
    private static final Font    TICK_MARK_LABEL_FONT = new PhetDefaultFont( Font.PLAIN, 12 );
    private static final Font    LABEL_FONT = new PhetDefaultFont( Font.PLAIN, 14 );
    private static final float   TIME_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  TIME_LINE_STROKE = new BasicStroke( TIME_LINE_STROKE_WIDTH );
    private static final Color   TIME_LINE_COLOR_PRE_DECAY = NuclearPhysics2Constants.POLONIUM_LABEL_COLOR;
    private static final Color   TIME_LINE_COLOR_POST_DECAY = Color.BLUE;
    private static final float   HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
    private static final Stroke  HALF_LIFE_LINE_STROKE = new BasicStroke(HALF_LIFE_LINE_STROKE_WIDTH, 
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3.0f, 3.0f }, 0);
    private static final Color   HALF_LIFE_LINE_COLOR = new Color (0xff9900);
    private static final Font    DECAY_TIME_FONT = new PhetDefaultFont( Font.PLAIN, 16 );
    private static final Color   DECAY_TIME_COLOR = Color.RED;
    
    // Constants that control the location of the origin.
    private static final double X_ORIGIN_PROPORTION = 0.10;
    private static final double Y_ORIGIN_PROPORTION = 0.65;
    
    // Constants that control the marker character.
    private static final String MARKER_CHAR = "*";
    private static final Font MARKER_CHAR_FONT = new PhetDefaultFont( Font.PLAIN, 20 );
    private static final Color MARKER_COLOR = Color.RED;
    
    // Max number of decays that we will keep track of.
    private static final int MAX_DECAYS = 100;
    
    // Tweakable values that can be used to adjust the way the time lines
    // appear on the charts.
    private static final double PRE_DECAY_TIME_LINE_POS_FRACTION = 0.33;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION = 0.55;
    private static final double TIMELINE_START_OFFEST = TICK_MARK_LENGTH * 4;
    
    // Half life of the nucleus we are displaying.
    private static final double HALF_LIFE = 516;  // In milliseconds.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _preDecayTimeLine;
    private PPath _postDecayTimeLine;
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
    private PText _timeToDecayLabel;
    private PText _timeToDecayText;
    private PText _timeToDecayUnits;
    private PText _halfLifeLabel;
    
    // Variables used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;
    
    // Variables that control the origins and lengths of the time lines.
    Point2D _preDecayTimeLineOrigin;
    double  _preDecayTimeLineLength;   // In milliseconds
    Point2D _postDecayTimeLineOrigin;
    double  _postDecayTimeLineLength;  // In milliseconds
    
    // Boolean that tracks whether decay has occurred.
    boolean _decayHasOccurred = false;
    
    // List of decay times for each trial and their markers.
    double _decayTimes[] = new double [MAX_DECAYS];
    int _numDecays = 0;
    PText _markers[] = new PText [MAX_DECAYS];
    int _numMarkers = 0;
   
    // Factor for converting milliseconds to pixels.
    double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

    // Clock that we listen to for moving the time line and performing resets.
    ConstantDtClock _clock;
    
    // Button for resetting this chart.
    PhetButtonNode _resetButtonNode;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaRadiationTimeChart(ConstantDtClock clock, AtomicNucleus nucleus) {
        
        // Save the clock and register as a listener.
        _clock = clock;
        _clock.addClockListener( new ClockAdapter(){
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTick(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                resetTimeLine();
            }
        });
        
        // Register as a listener for the nucleus so that we can be informed
        // when it decays.
        nucleus.addListener(new AtomicNucleus.Listener(){
            public void positionChanged(){
                // Do nothing, since we don't care about this.
            }
            public void atomicWeightChanged(int numProtons, int numNeutrons, ArrayList byProducts){
                if (byProducts != null){
                    // This is a decay event.
                    setDecayOccurred();
                }
            }
        });

        // Create the border for this chart.
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );
        
        // Create the x & y axes of the graph.  The initial position is arbitrary
        // and the actual positioning will be done by the update functions.
        _xAxisOfGraph = new ArrowNode(new Point2D.Double(10,10), new Point2D.Double(20,20), 10, 8, 2);
        _xAxisOfGraph.setPaint( Color.black );
        _xAxisOfGraph.setStroke( AXES_STROKE );
        _xAxisOfGraph.setStrokePaint( Color.black );
        addChild(_xAxisOfGraph);
        _yAxisOfGraph = new ArrowNode(new Point2D.Double(), new Point2D.Double(), 10, 8, 2);
        _yAxisOfGraph.setPaint( Color.black );
        _yAxisOfGraph.setStroke( AXES_STROKE );
        _yAxisOfGraph.setStrokePaint( Color.black );
        addChild(_yAxisOfGraph);
        
        // Add the tick marks and their labels to the X axis.
        int numTicksOnX = (int)Math.round(TIME_SPAN / 1000);
        _xAxisTickMarks = new ArrayList(numTicksOnX);
        _xAxisTickMarkLabels = new ArrayList(numTicksOnX);
        DecimalFormat formatter = new DecimalFormat("0.0");
        for (int i = 0; i < numTicksOnX; i++){
            // Create the tick mark.  It will be positioned later.
            PPath tickMark = new PPath();
            tickMark.setStroke( TICK_MARK_STROKE );
            _xAxisTickMarks.add( tickMark );
            addChild( tickMark );
            
            // Create the label for the tick mark.
            PText tickMarkLabel = new PText(formatter.format( i ));
            tickMarkLabel.setFont( TICK_MARK_LABEL_FONT );
            _xAxisTickMarkLabels.add(  tickMarkLabel );
            addChild( tickMarkLabel );
        }
        
        // Add the tick marks and their labels to the Y axis.  There are only
        // two, one for the weight of Polonium and one for the weight of Lead.
        
        _yAxisTickMarks = new ArrayList(2);
        
        PPath yTickMark1 = new PPath();
        yTickMark1.setStroke( TICK_MARK_STROKE );
        _yAxisTickMarks.add( yTickMark1 );
        addChild( yTickMark1 );
        
        PPath yTickMark2 = new PPath();
        yTickMark2.setStroke( TICK_MARK_STROKE );
        _yAxisTickMarks.add( yTickMark2 );
        addChild( yTickMark2 );
        
        _yAxisTickMarkLabels = new ArrayList(2);
        
        PText yTickMarkLabel1 = new PText(NuclearPhysics2Strings.LEAD_207_ISOTOPE_NUMBER);
        yTickMarkLabel1.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add(  yTickMarkLabel1 );
        addChild( yTickMarkLabel1 );
        
        PText yTickMarkLabel2 = new PText(NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER);
        yTickMarkLabel2.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add(  yTickMarkLabel2 );
        addChild( yTickMarkLabel2 );

        // Add the text for the X & Y axes.
        _xAxisLabel = new PText( NuclearPhysics2Strings.DECAY_TIME_CHART_X_AXIS_LABEL + " (" +
                NuclearPhysics2Strings.DECAY_TIME_UNITS + ")");
        _xAxisLabel.setFont( LABEL_FONT );
        addChild( _xAxisLabel );
        _yAxisLabel1 = new PText( NuclearPhysics2Strings.DECAY_TIME_CHART_Y_AXIS_LABEL1 );
        _yAxisLabel1.setFont( LABEL_FONT );
        _yAxisLabel1.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabel1 );
        _yAxisLabel2 = new PText( NuclearPhysics2Strings.DECAY_TIME_CHART_Y_AXIS_LABEL2 );
        _yAxisLabel2.setFont( LABEL_FONT );
        _yAxisLabel2.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabel2 );
        
        // Create the pre-decay time line.
        _preDecayTimeLine = new PPath();
        _preDecayTimeLine.setStroke( TIME_LINE_STROKE );
        _preDecayTimeLine.setStrokePaint( TIME_LINE_COLOR_PRE_DECAY );
        _preDecayTimeLine.setPaint( BACKGROUND_COLOR );
        addChild( _preDecayTimeLine );   
        
        // Create the post-decay time line.
        _postDecayTimeLine = new PPath();
        _postDecayTimeLine.setStroke( TIME_LINE_STROKE );
        _postDecayTimeLine.setStrokePaint( TIME_LINE_COLOR_POST_DECAY );
        _postDecayTimeLine.setPaint( BACKGROUND_COLOR );
        addChild( _postDecayTimeLine );   
        
        // Initialize the time line.
        _preDecayTimeLineOrigin = new Point2D.Double();
        _preDecayTimeLine.moveTo( 0, 0 );
        
        // Create the line that will illustrate where the half life is.
        _halfLifeMarkerLine = new PPath();
        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
        _halfLifeMarkerLine.setPaint( BACKGROUND_COLOR );
        addChild( _halfLifeMarkerLine );

        // Initialize the half life line.
        _preDecayTimeLineOrigin = new Point2D.Double();
        _preDecayTimeLine.moveTo( 0, 0 );

        // Create the label for the half life line.
        _halfLifeLabel = new PText( " " + NuclearPhysics2Strings.DECAY_TIME_CHART_HALF_LIFE + " ");
        _halfLifeLabel.setFont( LABEL_FONT );
        _halfLifeLabel.setPaint( HALF_LIFE_LINE_COLOR );
        addChild( _halfLifeLabel );

        // Add the text that will show the decay time.
        _timeToDecayLabel = new PText( NuclearPhysics2Strings.DECAY_TIME_LABEL );
        _timeToDecayLabel.setFont( LABEL_FONT );
        addChild( _timeToDecayLabel );
        _timeToDecayText = new PText( "0.000" );
        _timeToDecayText.setFont( LABEL_FONT );
        _timeToDecayText.setFont( DECAY_TIME_FONT );
        _timeToDecayText.setTextPaint( DECAY_TIME_COLOR );
        addChild( _timeToDecayText );
        _timeToDecayUnits = new PText( NuclearPhysics2Strings.DECAY_TIME_UNITS );
        _timeToDecayUnits.setFont( LABEL_FONT );
        addChild( _timeToDecayUnits );
        
        // Add the button for resetting the chart.
        _resetButtonNode = new PhetButtonNode(NuclearPhysics2Strings.DECAY_TIME_RESET_CHART);
        _resetButtonNode.setPickable( true );
        addChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // TODO: JPB TBD.
                System.out.println("Got the reset event.");
            }
        });
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
        _usableWidth       = rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = rect.getHeight() - ( BORDER_STROKE_WIDTH * 2);
        
        // Decide where the origin is located.
        _graphOriginX = _usableAreaOriginX + (X_ORIGIN_PROPORTION * _usableWidth);
        _graphOriginY = _usableAreaOriginY + (Y_ORIGIN_PROPORTION * _usableHeight);

        // Update the multiplier used for converting from pixels to milliseconds.
        _msToPixelsFactor = 0.85 * _usableWidth / TIME_SPAN;
        
        // Redraw the chart based on these recalculated values.
        update();
    }
    
    /**
     * Redraw the chart based on the current state.
     */
    private void update(){
        
        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                _usableAreaOriginX,
                _usableAreaOriginY,
                _usableWidth,
                _usableHeight,
                20,
                20 ) );
        
        // Position the x and y axes.
        _xAxisOfGraph.setTipAndTailLocations(
              new Point2D.Double( _graphOriginX + (TIME_SPAN * _msToPixelsFactor) + 10, _graphOriginY ), 
              new Point2D.Double( _graphOriginX, _graphOriginY ) );
        _yAxisOfGraph.setTipAndTailLocations(
                new Point2D.Double( _graphOriginX, _graphOriginY -  _usableHeight * 0.5),
                new Point2D.Double( _graphOriginX, _graphOriginY ) );
        
        // Position the tick marks and their labels on the X axis.
        for (int i = 0; i < _xAxisTickMarks.size(); i++){
            
            // Position the tick mark itself.
            PPath tickMark = (PPath)_xAxisTickMarks.get( i );
            double tickMarkPosX = _graphOriginX + TIMELINE_START_OFFEST + (i * 1000 * _msToPixelsFactor);
            tickMark.setPathTo( new Line2D.Double(tickMarkPosX, _graphOriginY, tickMarkPosX, 
                    _graphOriginY - TICK_MARK_LENGTH) );
            
            // Position the label for the tick mark.  Don't put it in if it
            // overlaps with the label for this axis. 
            PText tickMarkLabel = (PText)_xAxisTickMarkLabels.get( i );
            double tickMarkLabelPosX = tickMarkPosX - (tickMarkLabel.getWidth() / 2);
            if (tickMarkLabelPosX > _xAxisLabel.getOffset().getX() + _xAxisLabel.getWidth()){
                tickMarkLabel.setVisible( true );
                tickMarkLabel.setOffset( tickMarkLabelPosX, _graphOriginY );                
            }
            else{
                tickMarkLabel.setVisible( false );
            }
        }
        
        // Position the tick marks and their labels on the Y axis.
        PPath yAxisLowerTickMark = (PPath)_yAxisTickMarks.get( 0 );
        yAxisLowerTickMark.setPathTo( new Line2D.Double(_graphOriginX, 
                _usableAreaOriginY + (_usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION),
                _graphOriginX + TICK_MARK_LENGTH, 
                _usableAreaOriginY + (_usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION)));

        PPath yAxisUpperTickMark = (PPath)_yAxisTickMarks.get( 1 );
        yAxisUpperTickMark.setPathTo( new Line2D.Double(_graphOriginX, 
                _usableAreaOriginY + (_usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION),
                _graphOriginX + TICK_MARK_LENGTH, 
                _usableAreaOriginY + (_usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION)));
        
        PText yAxisLowerTickMarkLabel = (PText)_yAxisTickMarkLabels.get( 0 );
        yAxisLowerTickMarkLabel.setOffset(_graphOriginX - (1.1 * yAxisLowerTickMarkLabel.getWidth()), 
                yAxisLowerTickMark.getY() - (0.5 * yAxisLowerTickMarkLabel.getHeight()));                

        PText yAxisUpperTickMarkLabel = (PText)_yAxisTickMarkLabels.get( 1 );
        yAxisUpperTickMarkLabel.setOffset(_graphOriginX - (1.1 * yAxisUpperTickMarkLabel.getWidth()), 
                yAxisUpperTickMark.getY() - (0.5 * yAxisUpperTickMarkLabel.getHeight()));                

        // Position the labels for the axes.
        _xAxisLabel.setOffset( _graphOriginX, _graphOriginY + 2);
        _yAxisLabel2.setOffset( yAxisUpperTickMarkLabel.getOffset().getX() - (2.0 * _yAxisLabel1.getFont().getSize()), 
                _graphOriginY );
        _yAxisLabel1.setOffset( _yAxisLabel2.getOffset().getX() - (1.1 * _yAxisLabel2.getFont().getSize()), 
                _graphOriginY );
        
        // Position the marker for the half life.
        _halfLifeMarkerLine.reset();
        _halfLifeMarkerLine.moveTo( (float)(_graphOriginX + TIMELINE_START_OFFEST + (HALF_LIFE * _msToPixelsFactor)),
                (float)_graphOriginY );
        _halfLifeMarkerLine.lineTo( (float)(_graphOriginX + TIMELINE_START_OFFEST + (HALF_LIFE * _msToPixelsFactor)),
                (float)(_usableAreaOriginY + (0.1 * _usableHeight)));
        
        // Position the label for the half life.
        _halfLifeLabel.setOffset( _halfLifeMarkerLine.getX(), (_usableAreaOriginY + (0.1 * _usableHeight)) );
        
        // Position the pre-decay time line.
        _preDecayTimeLine.reset();
        _preDecayTimeLineOrigin.setLocation( _graphOriginX + TIMELINE_START_OFFEST, 
              _usableAreaOriginY + _usableHeight * 0.33);
        _preDecayTimeLine.moveTo( (float)_preDecayTimeLineOrigin.getX(), (float)_preDecayTimeLineOrigin.getY() );
        _preDecayTimeLine.lineTo( (float)(_preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor), 
                (float)_preDecayTimeLineOrigin.getY());
        
        // If decay has already occurred, position the post-decay time line too.
        if (_decayHasOccurred){
            _postDecayTimeLine.reset();
            
            _postDecayTimeLineOrigin.setLocation( 
                    _preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor + 1,
                    _usableAreaOriginY + (_usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION));
            _preDecayTimeLine.lineTo( (float)_postDecayTimeLineOrigin.getX(), (float)_postDecayTimeLineOrigin.getY() );
            _postDecayTimeLine.moveTo( (float)_postDecayTimeLineOrigin.getX(), (float)_postDecayTimeLineOrigin.getY() );
            _postDecayTimeLine.lineTo( 
                    (float)(_postDecayTimeLineOrigin.getX() + _postDecayTimeLineLength * _msToPixelsFactor), 
                    (float)_postDecayTimeLineOrigin.getY());            
        }
        
        // Position the decay markers.
        for (int i = 0; i < _numMarkers; i++){
            double xPos = _graphOriginX + TIMELINE_START_OFFEST +(_decayTimes[i] * _msToPixelsFactor) 
                    - (_markers[i].getWidth() / 2);
            double yPos = _preDecayTimeLineOrigin.getY();
            _markers[i].setOffset(xPos, yPos);
        }
        
        // Position the decay time indicator.
        _timeToDecayUnits.setOffset(
                _usableAreaOriginX + _usableWidth - _timeToDecayUnits.getWidth() - BORDER_STROKE_WIDTH,
                _usableAreaOriginY + 3);
        _timeToDecayText.setOffset( _timeToDecayUnits.getOffset().getX() - _timeToDecayText.getWidth() - 7, 
                _usableAreaOriginY + 3);
        _timeToDecayLabel.setOffset( _timeToDecayText.getOffset().getX() - _timeToDecayLabel.getWidth() - 7,
                _usableAreaOriginY + 3);
        
        // Position the reset button.  Center it below the decay time text.
        double xPosButton = _usableWidth - ((_timeToDecayUnits.getXOffset() + _timeToDecayUnits.getWidth() -
                _timeToDecayLabel.getXOffset()) / 2) - _resetButtonNode.getWidth() / 2;
//        double xPosButton = _usableWidth - _resetButtonNode.yugga;
        _resetButtonNode.setOffset( xPosButton, _usableAreaOriginY + _timeToDecayLabel.getHeight() * 1.3);
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
    private void handleClockTick(ClockEvent clockEvent){
        
        if (!_decayHasOccurred){

            // Extend the pre-decay time line, but not if we are
            // heading off the chart.
            if (_preDecayTimeLineLength < TIME_SPAN){
                
                // Update the length of the time line.
                _preDecayTimeLineLength += clockEvent.getSimulationTimeChange();

                // Extend the line itself.
                _preDecayTimeLine.lineTo( 
                        (float)(_preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor),
                        (float)_preDecayTimeLineOrigin.getY());
            }
            
            // Update the decay clock.
            DecimalFormat formatter = new DecimalFormat( "#0.000" );
            _timeToDecayText.setText( formatter.format( clockEvent.getSimulationTime() / 1000 ) );
        }
        else{
            
            // Extend the post-decay time line, but not if we are
            // heading off the chart.
            if (_postDecayTimeLineLength + _preDecayTimeLineLength < TIME_SPAN){
                
                // Update the length of the time line.
                _postDecayTimeLineLength += clockEvent.getSimulationTimeChange();

                // Extend the line itself.
                _postDecayTimeLine.lineTo( 
                        (float)(_postDecayTimeLineOrigin.getX() + _postDecayTimeLineLength * _msToPixelsFactor),
                        (float)_postDecayTimeLineOrigin.getY());
            }
        }
    }

    /**
     * Reset the time lines back to 0.  Note that this does NOT reset the 
     * decay markers.
     */
    private void resetTimeLine(){
        
        _decayHasOccurred = false;
        _preDecayTimeLineLength = 0;
        _postDecayTimeLineLength = 0;
        _postDecayTimeLine.reset();
        update();
    }
    
    /**
     * Reset the chart, which includes erasing all decay markers.
     */
    public void reset(){
        
        // Clear out the markers.
        for (int i=0; i<_numDecays; i++){
            removeChild( _markers[i] );
        }
        _numDecays = 0;
        _numMarkers = 0;
        
        // Clear out the time line.
        resetTimeLine();
        
        // Redraw the chart.
        update();
    }
    
    /**
     * Sets the indication that decay has occurred, which means it will draw a
     * different line from here on.
     */
    private void setDecayOccurred(){
        
        // Set our flag that tracks this.
        _decayHasOccurred = true;
        
        // Add a marker and record the time when this decay occurred.
        if (_numDecays < MAX_DECAYS){
            addDecayMarker( _preDecayTimeLineLength );
           _decayTimes[_numDecays++] = _preDecayTimeLineLength;
        }
        
        // Start drawing the post-decay line.
        _postDecayTimeLineOrigin = new Point2D.Double(
                _preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor + 1,
                _usableAreaOriginY + (_usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION));
        _preDecayTimeLine.lineTo( (float)_postDecayTimeLineOrigin.getX(), (float)_postDecayTimeLineOrigin.getY() );
        _postDecayTimeLineLength = 0;
        _postDecayTimeLine.moveTo( (float)_postDecayTimeLineOrigin.getX(), (float)_postDecayTimeLineOrigin.getY() );
    }
    
    /**
     * This method adds a marker to the chart to represent a decay event.
     * 
     * @param decayTime - Time at which to place the event.
     */
    private void addDecayMarker(double decayTime){
        
        // Create the marker and set the font attributes.
        PText marker = new PText(MARKER_CHAR);
        marker.setFont( MARKER_CHAR_FONT );
        marker.setTextPaint( MARKER_COLOR );
        
        // Position the marker, accounting for the height and width of the
        // text within it.
        double xPos = _graphOriginX + TIMELINE_START_OFFEST + (decayTime * _msToPixelsFactor) - (marker.getWidth() / 2);
        double yPos = _preDecayTimeLineOrigin.getY();//_usableAreaOriginY + _usableHeight * 0.70 - marker.getHeight() / 2;
        marker.setOffset(xPos, yPos);
        
        // Retain a reference to the marker so we can move it during updates.
        _markers[_numMarkers++] = marker;
 
        // Add this node as a child.
        addChild(marker);
    }
}
