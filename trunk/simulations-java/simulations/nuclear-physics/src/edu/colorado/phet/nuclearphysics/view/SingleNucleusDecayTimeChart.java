/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResizeArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.module.betadecay.singlenucleus.SingleNucleusBetaDecayModel;
import edu.colorado.phet.nuclearphysics.util.PhetButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This class displays a "strip chart" of the decay time for multiple
 * iterations of a single nucleus.  It has a linear mode for decay times
 * that are a few seconds or less and an exponential mode for longer decay
 * times.
 *
 * @author John Blanco
 */
public class SingleNucleusDecayTimeChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Total amount of time in milliseconds represented by this chart.
    private static final double DEFAULT_TIME_SPAN = 3200;
    
    // Minimum allowable half life.
    private static final double MIN_HALF_LIFE = 10; // In milliseconds.
    
    // Constants for controlling the appearance of the chart.
    private static final Color  BORDER_COLOR = Color.DARK_GRAY;
    private static final float  BORDER_STROKE_WIDTH = 6f;
    private static final Stroke BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final float  AXES_LINE_WIDTH = 0.5f;
    private static final Stroke AXES_STROKE = new BasicStroke( AXES_LINE_WIDTH );
    private static final Color  AXES_LINE_COLOR = Color.BLACK;
    private static final double TICK_MARK_LENGTH = 3;
    private static final float  TICK_MARK_WIDTH = 2;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke( TICK_MARK_WIDTH );
    private static final Font   TICK_MARK_LABEL_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final Color  TICK_MARK_COLOR = AXES_LINE_COLOR;
    private static final Font   SMALL_LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final Font   LARGE_LABEL_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Font   ISOTOPE_LABEL_FONT = new PhetFont( Font.BOLD, 20 );
    private static final float  HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
    private static final Stroke HALF_LIFE_LINE_STROKE = new BasicStroke( HALF_LIFE_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f }, 0 );
    private static final Color  HALF_LIFE_LINE_COLOR = new Color (238, 0, 0);
    private static final Color  HALF_LIFE_TEXT_COLOR = HALF_LIFE_LINE_COLOR;
    private static final Font   HALF_LIFE_FONT = new PhetFont( Font.BOLD, 16 );
	private static final Color  TIME_LINE_BASE_COLOR = Color.BLUE;
    private static final double RESIZE_HANDLE_SIZE = 35;
    private static final double MAX_ISOTOPE_LABEL_WIDTH_PROPORTION = 0.05;
    private static final double MAX_ISOTOPE_LABEL_HEIGHT_PROPORTION = 0.2;

    // Constants that control the location of the origin.
    private static final double X_ORIGIN_PROPORTION_NORMAL_MODE = 0.27;
    private static final double X_ORIGIN_PROPORTION_EXPONENTIAL_MODE = 0.22;
    private static final double Y_ORIGIN_PROPORTION = 0.65;

    // Tweakable values that can be used to adjust where the nuclei appear on
    // the chart.
    private static final double PRE_DECAY_TIME_LINE_POS_FRACTION = 0.20;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION_NORMAL = 0.50;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION_EXPONENTIAL = 0.42;
    private static final double TIME_ZERO_OFFSET = 100; // In milliseconds
    private static final int    INITIAL_FALL_COUNT = 5; // Number of clock ticks for nucleus to fall from upper to lower line.
    private static final double TIME_ZERO_OFFSET_PROPORTION = 0.05; // Proportion of total time span

    // Constants that control the way the nuclei look.
    private static final double NUCLEUS_SIZE_PROPORTION = 0.15;  // Fraction of the overall height of the chart.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the model containing the nuclei that are being plotted.
    SingleNucleusBetaDecayModel _model;
    
    // Variables for tracking information about the nuclei.
    AtomicNucleus _currentNucleus;
    EnhancedLabeledNucleusNode _undecayedNucleusNode;
    ArrayList<PNode> _decayedNucleusNodes = new ArrayList<PNode>();
    
    // Time span covered by this chart, in milliseconds.
    private double _timeSpan = DEFAULT_TIME_SPAN;
    
    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _halfLifeMarkerLine;
    private PText _halfLifeLabel;
    private PText _halfLifeInfinityText;
    private ArrowNode _xAxisOfGraph;
    private ArrayList<PhetPPath> _xAxisTickMarks;
    private ArrayList<PText> _xAxisTickMarkLabels;
    private ArrayList<PhetPPath> _yAxisTickMarks;
    private ShadowHTMLNode _yAxisUpperTickMarkLabel;
    private ShadowHTMLNode _yAxisLowerTickMarkLabel;
    private PText _xAxisLabel;
    private PText _yAxisLabel;
    private TimeDisplayNode _timeDisplay;
    private PText _decayTimeLabel;
    private LogarithmicTimeLineNode _exponentialTimeLine;
    private ResizeArrowNode _halfLifeHandleNode;

    // Parent node that will be non-pickable and will contain all of the
    // non-interactive portions of the chart.
    private PComposite _nonPickableChartNode;
    
    // Parent node that will have interactive portions of graph.
    private PNode _pickableChartNode;

    // Variables used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;
    double _nucleusNodeRadius;

    // Factor for converting milliseconds to pixels.
    double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

    // Clock that we listen to for moving the nuclei and performing resets.
    ConstantDtClock _clock;

    // Button for resetting this chart.
    PhetButtonNode _resetButtonNode;
    
    // Miscellaneous other variables for controlling chart appearance and
    // behavior.
    boolean _chartCleared = false;
    boolean _exponentialMode = false;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public SingleNucleusDecayTimeChart( SingleNucleusBetaDecayModel model ) {

        _clock = model.getClock();
        _model = model;

        if (_model.getNucleusType() == NucleusType.HEAVY_CUSTOM){
        	_exponentialMode = true;
        }
        else{
        	_exponentialMode = false;
        }
        
        // Register as a clock listener.
        _clock.addClockListener( new ClockAdapter() {

            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked( clockEvent );
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        } );
        
        // Listen to the model for notifications of relevant events.
        _model.addListener( new NuclearDecayListenerAdapter(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
            
            public void nucleusTypeChanged(){
            	handleNucleusTypeChanged();
            }

            public void halfLifeChanged(){
            	clearDecayedNuclei();
            	positionHalfLifeMarker();
            }
        });

        // Set up the parent node that will contain the non-interactive
        // portions of the chart.
        _nonPickableChartNode = new PComposite();
        _nonPickableChartNode.setPickable( false );
        _nonPickableChartNode.setChildrenPickable( false );
        addChild( _nonPickableChartNode );

        // Set up the parent node that will contain the interactive portions
        // of the chart.
        _pickableChartNode = new PNode();
        _pickableChartNode.setPickable( true );
        _pickableChartNode.setChildrenPickable( true );
        addChild( _pickableChartNode );

        // Create the border for this chart.
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( NuclearPhysicsConstants.CHART_BACKGROUND_COLOR );
        _nonPickableChartNode.addChild( _borderNode );

        // Create the x axis of the graph.  The initial position is arbitrary
        // and the actual positioning will be done by the update function(s).
        _xAxisOfGraph = new ArrowNode( new Point2D.Double( 10, 10 ), new Point2D.Double( 20, 20 ), 9, 7, 1 );
        _xAxisOfGraph.setStroke( AXES_STROKE );
        _xAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _xAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _xAxisOfGraph );

        // Add the tick marks and their labels to the X axis.
        int numTicksOnX = (int) Math.round( ( DEFAULT_TIME_SPAN / 1000 ) + 1 );
        _xAxisTickMarks = new ArrayList<PhetPPath>( numTicksOnX );
        _xAxisTickMarkLabels = new ArrayList<PText>( numTicksOnX );
        DecimalFormat formatter = new DecimalFormat( "0.0" );
        for ( int i = 0; i < numTicksOnX; i++ ) {
            // Create the tick mark.  It will be positioned later.
        	PhetPPath tickMark = new PhetPPath();
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

        _yAxisTickMarks = new ArrayList<PhetPPath>( 2 );

        PhetPPath yTickMark1 = new PhetPPath( TICK_MARK_STROKE, TICK_MARK_COLOR );
        _yAxisTickMarks.add( yTickMark1 );
        _nonPickableChartNode.addChild( yTickMark1 );

        PhetPPath yTickMark2 = new PhetPPath( TICK_MARK_STROKE, TICK_MARK_COLOR );
        _yAxisTickMarks.add( yTickMark2 );
        _nonPickableChartNode.addChild( yTickMark2 );

        _yAxisUpperTickMarkLabel = new ShadowHTMLNode();
        _yAxisUpperTickMarkLabel.setFont( ISOTOPE_LABEL_FONT );
        _nonPickableChartNode.addChild( _yAxisUpperTickMarkLabel );

        _yAxisLowerTickMarkLabel = new ShadowHTMLNode();
        _yAxisLowerTickMarkLabel.setFont( ISOTOPE_LABEL_FONT );
        _nonPickableChartNode.addChild( _yAxisLowerTickMarkLabel );

        // Add the text for the X & Y axes.
        _xAxisLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_X_AXIS_LABEL + " (" + NuclearPhysicsStrings.DECAY_TIME_UNITS + ")" );
        _xAxisLabel.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _xAxisLabel );
        _yAxisLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL_ISOTOPE );
        _yAxisLabel.setFont( LARGE_LABEL_FONT );
        _yAxisLabel.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel );

        // Add the display of the decay time.
        _timeDisplay = new TimeDisplayNode();
        _timeDisplay.setTime(0);
        _nonPickableChartNode.addChild(_timeDisplay);
        _decayTimeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_LABEL );
        _decayTimeLabel.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _decayTimeLabel );
        
        // Add the exponential time line, which is only displayed when the
        // chart needs to depict exponential time.
        _exponentialTimeLine = new LogarithmicTimeLineNode();
        _nonPickableChartNode.addChild(_exponentialTimeLine);

        // Create the line that will illustrate where the half life is.
        _halfLifeMarkerLine = new PPath();
        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
        _halfLifeMarkerLine.setPaint( NuclearPhysicsConstants.CHART_BACKGROUND_COLOR );
        _nonPickableChartNode.addChild( _halfLifeMarkerLine );
        
        // Create the handle that will allow the user to control the half life.
        _halfLifeHandleNode = new ResizeArrowNode(RESIZE_HANDLE_SIZE, 0, Color.GREEN, Color.YELLOW);
        _pickableChartNode.addChild( _halfLifeHandleNode );
        _halfLifeHandleNode.addInputEventListener(new PBasicInputEventHandler(){
        	boolean halfLifeChanged;
        	public void mousePressed(PInputEvent event) {
        		halfLifeChanged = false;
        		_model.getClock().setPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		_model.getClock().setPaused(false);
        		if (halfLifeChanged){
        			clearDecayedNuclei();
        		}
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double newHalfLife = _model.getHalfLife() + (d.width / _msToPixelsFactor);
                if (newHalfLife >= MIN_HALF_LIFE && newHalfLife <= (_timeSpan * 0.95)){
	                _model.setHalfLife(newHalfLife);
	        		halfLifeChanged = true;
                }
            }
        });

        // Create the label for the half life line.
        _halfLifeLabel = new PText( NuclearPhysicsStrings.HALF_LIFE_LABEL );
        _halfLifeLabel.setFont( HALF_LIFE_FONT );
        _halfLifeLabel.setTextPaint( HALF_LIFE_TEXT_COLOR );
        _nonPickableChartNode.addChild( _halfLifeLabel );
        
        // Create the "infinity indication" for the half life marker which is
        // used to indicate when the half life becomes essentially infinite.
        _halfLifeInfinityText = new PText( NuclearPhysicsStrings.INFINITY_SYMBOL );
        _halfLifeInfinityText.setFont( HALF_LIFE_FONT );
        _halfLifeInfinityText.setTextPaint( HALF_LIFE_TEXT_COLOR );
        _nonPickableChartNode.addChild( _halfLifeInfinityText );
        
        // Add the button for resetting the chart.
        _resetButtonNode = new PhetButtonNode( NuclearPhysicsStrings.DECAY_TIME_CLEAR_CHART );
        _resetButtonNode.setPickable( true );
        _pickableChartNode.addChild( _resetButtonNode );

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
    
    public void setTimeSpan( double timeSpan ){
    	_timeSpan = timeSpan;
    	_msToPixelsFactor = ((_usableWidth - _graphOriginX) * 0.98) / _timeSpan;
    	update();
    }

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

        // Update the multiplier used for converting from pixels to
        // milliseconds.  Use the multiplier to tweak the span of the x axis
        // if needed.
        _msToPixelsFactor = 0.70 * _usableWidth / _timeSpan;
        
        // Update the radius value used to position nucleus nodes so that they
        // are centered at the desired location.
        _nucleusNodeRadius = _usableHeight * NUCLEUS_SIZE_PROPORTION / 2;

        // Redraw the chart based on these recalculated values.
        update();
    }

    /**
     * Redraw the chart based on the current state.
     */
    private void update() {
    	
    	if ((_usableWidth <= 0) || (_usableHeight <= 0)){
    		// This sometimes happens during initialization.  Ignore it.
    		return;
    	}
    	
        // Decide where the origin is located.
        if (_exponentialMode){
            _graphOriginX = _usableAreaOriginX + ( X_ORIGIN_PROPORTION_EXPONENTIAL_MODE * _usableWidth );
        }
        else{
            _graphOriginX = _usableAreaOriginX + ( X_ORIGIN_PROPORTION_NORMAL_MODE * _usableWidth );
        }
        _graphOriginY = _usableAreaOriginY + ( Y_ORIGIN_PROPORTION * _usableHeight );

        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( _usableAreaOriginX, _usableAreaOriginY, _usableWidth, _usableHeight, 20, 20 ) );

        // Position the x and y axes.
        _xAxisOfGraph.setTipAndTailLocations( 
        		new Point2D.Double( _usableAreaOriginX + _usableWidth - 10, _graphOriginY ), 
        		new Point2D.Double( _graphOriginX, _graphOriginY ) );

        // Position the tick marks and their labels on the X axis.
        updateXAxisTickMarksAndLabels();

        // Set the visibility of the Y axis label based on the chart mode.
       	_yAxisLabel.setVisible(!_exponentialMode);
        
        // Position the tick marks and their labels on the Y axis.
        double preDecayPosY = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION );
        double postDecayPosY;
        if (_exponentialMode){
            postDecayPosY = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION_EXPONENTIAL );
        }
        else{
            postDecayPosY = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION_NORMAL );
        }
        PPath yAxisLowerTickMark = (PPath)_yAxisTickMarks.get( 0 );
        yAxisLowerTickMark.setPathTo( new Line2D.Double( _graphOriginX - TICK_MARK_LENGTH, postDecayPosY, 
        		_graphOriginX, postDecayPosY ));

        PPath yAxisUpperTickMark = (PPath) _yAxisTickMarks.get( 1 );
        yAxisUpperTickMark.setPathTo( new Line2D.Double( _graphOriginX - TICK_MARK_LENGTH, preDecayPosY, 
        		_graphOriginX, preDecayPosY ) );

        updateNucleusGraphLabels();
        
        _yAxisLowerTickMarkLabel.setOffset( 
        		_graphOriginX - _yAxisLowerTickMarkLabel.getFullBoundsReference().getWidth() 
        			- ( yAxisLowerTickMark.getWidth() * 1.5 ),
        		yAxisLowerTickMark.getY() - ( 0.5 * _yAxisLowerTickMarkLabel.getFullBoundsReference().height ) );

        _yAxisUpperTickMarkLabel.setOffset( 
        		_graphOriginX - _yAxisUpperTickMarkLabel.getFullBoundsReference().getWidth()
        			- ( yAxisUpperTickMark.getWidth() * 1.5 ),
        		yAxisUpperTickMark.getY() - ( 0.5 * _yAxisUpperTickMarkLabel.getFullBoundsReference().height ) );

        // Position the labels for the axes.
        _xAxisLabel.setOffset( _graphOriginX - (_xAxisLabel.getFullBoundsReference().width / 2),
        		((PNode)_xAxisTickMarkLabels.get(0)).getFullBoundsReference().getMaxY() );
        double yAxisLabelCenter = yAxisUpperTickMark.getY() + 
        	((yAxisLowerTickMark.getY() - yAxisUpperTickMark.getY()) / 2);
        _yAxisLabel.setScale(1);
        _yAxisLabel.setScale(calculateScaleForTargetSize(_yAxisLabel, 
        		new PDimension(Double.POSITIVE_INFINITY, _usableHeight * Y_ORIGIN_PROPORTION * 0.9)));
        _yAxisLabel.setOffset( 
        		_yAxisLowerTickMarkLabel.getOffset().getX() - ( _yAxisLabel.getFullBoundsReference().width * 1.2 ),
        		yAxisLabelCenter + (_yAxisLabel.getFullBounds().height / 2) );
        
        // Update the exponential time line, including whether or not it is
        // visible.
        _exponentialTimeLine.setVisible(_exponentialMode);
        _exponentialTimeLine.setSize((int)(_usableWidth - _graphOriginX - (TIME_ZERO_OFFSET * _msToPixelsFactor) - 5),
        		(int)(_usableHeight * 0.3));
        _exponentialTimeLine.setOffset(_graphOriginX + (TIME_ZERO_OFFSET * _msToPixelsFactor), 
        		_graphOriginY - _exponentialTimeLine.getFullBounds().height);
        
        // Position the half life marker.
        positionHalfLifeMarker();

        // Position the time display.
        _timeDisplay.setSize(_usableWidth * 0.15, _usableHeight * 0.35);
        _timeDisplay.setOffset( _usableAreaOriginX + _usableWidth * 0.015, _usableAreaOriginY + _usableHeight * 0.1);
        PBounds _timeDisplayBounds = _timeDisplay.getFullBoundsReference();
        _decayTimeLabel.setOffset(_timeDisplayBounds.getCenterX() - _decayTimeLabel.getFullBoundsReference().width / 2,
        		_timeDisplayBounds.getMaxY());
        
        // Position the reset button.
        _resetButtonNode.setOffset( _timeDisplayBounds.getCenterX() - _resetButtonNode.getFullBoundsReference().width / 2, 
        		_usableAreaOriginY + _usableHeight - _resetButtonNode.getFullBoundsReference().height - 5);
        
        // Rescale the nucleus nodes and set their positions.
        updateNucleusNodesScale();
        positionCurrentNucleus();
    	Iterator it = _decayedNucleusNodes.iterator();
    	while (it.hasNext()){
    		positionDecayedNucleusNode((EnhancedLabeledNucleusNode)it.next());
    	}
    }

	private void updateXAxisTickMarksAndLabels() {
		
    	// Remove the existing tick marks and labels.
    	for (PNode tickMark : _xAxisTickMarks){
    		_nonPickableChartNode.removeChild(tickMark);
    	}
    	for (PNode tickMarkLabel : _xAxisTickMarkLabels){
    		_nonPickableChartNode.removeChild(tickMarkLabel);
    	}
    	_xAxisTickMarks.clear();
    	_xAxisTickMarkLabels.clear();
    	
    	int numTickMarks = 0;
    	if (_timeSpan < 10000){
    		// Tick marks are 1 second apart.
    		numTickMarks = (int)(_timeSpan / 1000 + 1);
    		
    		for (int i = 0; i < numTickMarks; i++){
    			String tickMarkText;
    			if (i == 0){
    				tickMarkText = "0.0";
    			}
    			else{
    				tickMarkText = Integer.toString( i );
    			}
    			addXAxisTickMark(i * 1000, tickMarkText);
    		}
    	}
    	else if (_timeSpan < HalfLifeInfo.convertYearsToMs(100)){
    		// Tick marks are 10 yrs apart.
    		numTickMarks = (int)(_timeSpan / HalfLifeInfo.convertYearsToMs(10) + 1);
    		
    		for (int i = 0; i < numTickMarks; i++){
    			String tickMarkText;
    			if (i == 0){
    				tickMarkText = "0.0";
    			}
    			else{
    				tickMarkText = Integer.toString(i * 10);
    			}
    			addXAxisTickMark(i * HalfLifeInfo.convertYearsToMs(10), tickMarkText);
    		}
    	}
    	else if (_timeSpan < HalfLifeInfo.convertYearsToMs(1E9)){
    		// Tick marks are 5000 yrs apart.  This is generally used for
    		// the Carbon 14 range.
    		numTickMarks = (int)(_timeSpan / HalfLifeInfo.convertYearsToMs(5000) + 1);
    		
    		for (int i = 0; i < numTickMarks; i++){
    			String tickMarkText;
    			if (i == 0){
    				tickMarkText = "0.0";
    			}
    			else{
    				tickMarkText = Integer.toString(i * 5000);
    			}
    			addXAxisTickMark(i * HalfLifeInfo.convertYearsToMs(5000), tickMarkText);
    		}
    	}
    	else{
    		// Space the tick marks four billion years apart.
    		numTickMarks = (int)(_timeSpan / HalfLifeInfo.convertYearsToMs(4E9) + 1);
    		
    		for (int i = 0; i < numTickMarks; i++){
    			addXAxisTickMark(i * HalfLifeInfo.convertYearsToMs(4E9),
    					String.format("%.1f", (float)(i * 4)));
    		}
    	}
    	
        // Position and size the label for the lower X axis.
    	double unitsLabelYPos = _graphOriginY + 5;
    	if (_xAxisTickMarkLabels.size() > 0){
    		unitsLabelYPos = _xAxisTickMarkLabels.get(0).getFullBoundsReference().getMaxY();
    	}
    	
        _xAxisLabel.setText(NuclearPhysicsStrings.DECAY_TIME_CHART_X_AXIS_LABEL + " (" + getXAxisUnitsText() + ")");
        _xAxisLabel.setOffset( _graphOriginX - (_xAxisLabel.getFullBoundsReference().width / 2), unitsLabelYPos);


//		for ( int i = 0; i < _xAxisTickMarks.size(); i++ ) {
//        	
//            // Position the tick mark itself.
//            PPath tickMark = (PPath) _xAxisTickMarks.get( i );
//            double tickMarkPosX = _graphOriginX + (TIME_ZERO_OFFSET * _msToPixelsFactor) 
//                    + ( i * 1000 * _msToPixelsFactor );
//            tickMark.setPathTo( new Line2D.Double( tickMarkPosX, _graphOriginY, tickMarkPosX, _graphOriginY - TICK_MARK_LENGTH ) );
//
//            // Position the label for the tick mark.
//            PText tickMarkLabel = (PText) _xAxisTickMarkLabels.get( i );
//            double tickMarkLabelPosX = tickMarkPosX - ( tickMarkLabel.getWidth() / 2 );
//            tickMarkLabel.setOffset( tickMarkLabelPosX, _graphOriginY );
//            
//            // Set the visibility.
//            tickMark.setVisible(!_exponentialMode);
//            tickMarkLabel.setVisible(!_exponentialMode);
//        }
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
     * Update the chart by moving the active nuclei or any other time-
     * dependent visual representation.
     * 
     * @param clockEvent
     */
    private void handleClockTicked( ClockEvent clockEvent ) {

    	if ((_currentNucleus != null) && (_currentNucleus.isPaused() == false)){
    		// There is an active nucleus that we are watching.  Make any updates.
    		if (!_currentNucleus.hasDecayed()){
    			if (_undecayedNucleusNode == null){
    				// This situation generally indicates that the nucleus was
    				// reset, so create a new node for it.
    				_undecayedNucleusNode = createNucleusNode(_currentNucleus);
    				_nonPickableChartNode.addChild(_undecayedNucleusNode);
    			}
    			positionCurrentNucleus();
    			_timeDisplay.setTime(_currentNucleus.getAdjustedActivatedTime());
    		}
    		else{
    			if (_undecayedNucleusNode != null){
        			// The nucleus must have decayed since the last tick.
    				// Create a node to represent the decayed nucleus and
    				// add it to the list of decayed nuclei.
        			EnhancedLabeledNucleusNode newlyDecayedNucleusNode = createNucleusNode(_currentNucleus);
        			_nonPickableChartNode.addChild(newlyDecayedNucleusNode);
        			newlyDecayedNucleusNode.setDecayTime(_currentNucleus.getAdjustedActivatedTime());
        			newlyDecayedNucleusNode.startFalling();
        			_decayedNucleusNodes.add(newlyDecayedNucleusNode);
        			_nonPickableChartNode.removeChild(_undecayedNucleusNode);
        			// TODO: check that this gets garbage collected and that no other
        			// cleanup is needed.
        			_undecayedNucleusNode = null;
    			}
    		}
    	}
    	
    	// Check for any falling nodes and position them.
    	Iterator it = _decayedNucleusNodes.iterator();
    	while (it.hasNext()){
    		EnhancedLabeledNucleusNode decayedNucleusNode = (EnhancedLabeledNucleusNode)it.next();
    		if (decayedNucleusNode.isFalling()){
    			decayedNucleusNode.decrementFallCount();
    			positionDecayedNucleusNode(decayedNucleusNode);
    		}
    	}
    }
    
	private void handleModelElementAdded(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		if (_currentNucleus != null){
    			// Because of the overall design, we shouldn't get a notice of
    			// an element being added without the previous nucleus having
    			// been removed.
    			System.err.println("Error: Got nucleusAdded event before existing nucleus removed.");
    		}
    		_currentNucleus = (AtomicNucleus)modelElement;
    		_undecayedNucleusNode = createNucleusNode(_currentNucleus);
    		_nonPickableChartNode.addChild(_undecayedNucleusNode);
    		positionHalfLifeMarker();
    	}
	}

    private void handleModelElementRemoved(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		if (_currentNucleus == null){
    			// This shouldn't be called when we don't have a nucleus, so
    			// there is a problem in the code that should be debugged.
    			System.err.println("Error: Got nucleusAdded event before existing nucleus removed.");
    			return;
    		}
    		_currentNucleus = null;
    		if (_undecayedNucleusNode != null){
        		_nonPickableChartNode.removeChild(_undecayedNucleusNode);
        		_undecayedNucleusNode = null;
    		}
    	}
	}

    /**
     * Handle a signal from the model that indicates that the nucleus type has
     * changed.
     */
	private void handleNucleusTypeChanged() {
		clearDecayedNuclei();
    	if (_model.getNucleusType() == NucleusType.HEAVY_CUSTOM){
    		_exponentialMode = true;
    		updateNucleusGraphLabels();
    	}
    	else{
    		_exponentialMode = false;
    	}
    	
    	update();
	};
	
    private void updateNucleusGraphLabels(){
    	
    	// Get the display information for the current nuclei.
    	NucleusDisplayInfo preDecayDisplayInfo = 
    		NucleusDisplayInfo.getDisplayInfoForNucleusType(_model.getNucleusType());
    	NucleusDisplayInfo postDecayDisplayInfo = 
    		NucleusDisplayInfo.getDisplayInfoForNucleusType(AtomicNucleus.getPostDecayNuclei(_model.getNucleusType()).get(0));

		if (preDecayDisplayInfo != null){
			_yAxisUpperTickMarkLabel.setHtml("<html><sup><font size=-1>" + preDecayDisplayInfo.getIsotopeNumberString() 
					+ "</font></sup>" + preDecayDisplayInfo.getChemicalSymbol());
			_yAxisUpperTickMarkLabel.setColor(preDecayDisplayInfo.getLabelColor());
			if (preDecayDisplayInfo.getLabelColor() == Color.BLACK){
				_yAxisUpperTickMarkLabel.setShadowColor(Color.WHITE);
			}
			else {
				_yAxisUpperTickMarkLabel.setShadowColor(Color.BLACK);
			}
		}
		else{
			_yAxisUpperTickMarkLabel.setHtml("X");
			_yAxisUpperTickMarkLabel.setColor(Color.RED);
		}
		
		if (postDecayDisplayInfo != null){
			_yAxisLowerTickMarkLabel.setHtml("<html><sup><font size=-1>" + postDecayDisplayInfo.getIsotopeNumberString() 
					+ "</font></sup>" + postDecayDisplayInfo.getChemicalSymbol());
			_yAxisLowerTickMarkLabel.setColor(postDecayDisplayInfo.getLabelColor());
			if (postDecayDisplayInfo.getLabelColor() == Color.BLACK){
				_yAxisLowerTickMarkLabel.setShadowColor(Color.WHITE);
			}
			else {
				_yAxisLowerTickMarkLabel.setShadowColor(Color.BLACK);
			}
		}
		else{
			_yAxisLowerTickMarkLabel.setHtml("X");
			_yAxisLowerTickMarkLabel.setColor(Color.BLACK);
		}
		
		// Scale the labels.  This helps them to be more visible when the
		// sim is enlarged.
		Dimension2D size = new PDimension(_usableWidth * MAX_ISOTOPE_LABEL_WIDTH_PROPORTION, 
				_usableHeight * MAX_ISOTOPE_LABEL_HEIGHT_PROPORTION);
		_yAxisUpperTickMarkLabel.setScale(1);
		_yAxisUpperTickMarkLabel.setScale(calculateScaleForTargetSize(_yAxisUpperTickMarkLabel, size));
		_yAxisLowerTickMarkLabel.setScale(1);
		_yAxisLowerTickMarkLabel.setScale(calculateScaleForTargetSize(_yAxisLowerTickMarkLabel, size));
    }
    
    /**
     * Position the half life marker on the chart based on the values of the
     * half life for the nucleus in the model.
     */
    private void positionHalfLifeMarker(){
        // Position the marker for the half life.
    	double halfLife = _model.getHalfLife();
    	double halfLifeMarkerXPos = 0;
    	if (_exponentialMode){
    		if (halfLife == Double.POSITIVE_INFINITY){
    			halfLifeMarkerXPos = _xAxisOfGraph.getFullBoundsReference().getMaxX();
    		}
    		else{
        		halfLifeMarkerXPos = _exponentialTimeLine.mapTimeToHorizPixels(halfLife) + _graphOriginX +
   		           (TIME_ZERO_OFFSET * _msToPixelsFactor);
        		halfLifeMarkerXPos = Math.min(halfLifeMarkerXPos, _xAxisOfGraph.getFullBoundsReference().getMaxX());
    		}
    	}
    	else{
    		halfLifeMarkerXPos = _graphOriginX + ((TIME_ZERO_OFFSET_PROPORTION * _timeSpan) + halfLife) * _msToPixelsFactor;
    	}
        _halfLifeMarkerLine.reset();
        _halfLifeMarkerLine.moveTo( (float)halfLifeMarkerXPos, 
        		(float)(_graphOriginY + ((_usableHeight - _graphOriginY) * 0.4)) );
        _halfLifeMarkerLine.lineTo( (float)halfLifeMarkerXPos, 
        		(float) ( _usableAreaOriginY + ( 0.1 * _usableHeight ) ) );
        
        // If it is a custom nucleus, position and show the handle.
        if ((_model.getNucleusType() == NucleusType.HEAVY_CUSTOM) || (_model.getNucleusType() == NucleusType.LIGHT_CUSTOM)){
        	_halfLifeHandleNode.setVisible(true);
        	_halfLifeHandleNode.setOffset( _halfLifeMarkerLine.getX(), _halfLifeMarkerLine.getY() + (_graphOriginY - _halfLifeMarkerLine.getY()) / 2 );
        }
        else{
        	_halfLifeHandleNode.setVisible(false);
        }
        
        // Position the textual label for the half life.
        _halfLifeLabel.setOffset( _halfLifeMarkerLine.getX() - (_halfLifeLabel.getFullBoundsReference().width / 2),
        		(float)(_graphOriginY + ((_usableHeight - _graphOriginY) * 0.5)) );
        
        // Hide the x axis label if there is overlap with the half life label.
        if (_xAxisLabel.getFullBoundsReference().intersects(_halfLifeLabel.getFullBoundsReference())){
        	_xAxisLabel.setVisible(false);
        }
        else{
        	_xAxisLabel.setVisible(true);
        }
        
        // Position the infinity marker, set its scale, and set its visibility.
        _halfLifeInfinityText.setScale(1);
        if (_halfLifeMarkerLine.getFullBoundsReference().height > 0 &&
            _halfLifeInfinityText.getFullBoundsReference().height > 0){

        	// Tweak the multiplier on the following line as needed.
            double desiredHeight = _halfLifeMarkerLine.getFullBoundsReference().height * 0.7;
            
            _halfLifeInfinityText.setScale(desiredHeight / _halfLifeInfinityText.getFullBoundsReference().height);
        }
        
        _halfLifeInfinityText.setOffset(
        		_halfLifeMarkerLine.getX() - _halfLifeInfinityText.getFullBoundsReference().width,
        		_halfLifeMarkerLine.getFullBoundsReference().getMinY() - 
        		_halfLifeInfinityText.getFullBoundsReference().height * 0.4);
        
        _halfLifeInfinityText.setVisible(_model.getHalfLife() == Double.POSITIVE_INFINITY);
    }
    
	/**
	 * Update the scale settings for the nucleus nodes.  This is generally
	 * done if and when the chart is resized.
	 */
	private void updateNucleusNodesScale(){

		if ( _nucleusNodeRadius > 0 ){
			
			if (_undecayedNucleusNode != null){
				_undecayedNucleusNode.setScale(1);
				_undecayedNucleusNode.setScale((_nucleusNodeRadius * 2) / _undecayedNucleusNode.getFullBoundsReference().height);
			}
			
	    	Iterator it = _decayedNucleusNodes.iterator();
	    	while (it.hasNext()){
	    		PNode nucleusNode = (PNode)it.next();
	    		nucleusNode.setScale(1);
	    		nucleusNode.setScale((_nucleusNodeRadius * 2) / nucleusNode.getFullBoundsReference().height);
	    	}
    	}
	}
	
    /**
     * Get the units string for the x axis label.  Note that this does not
     * handle all ranges of time.  Feel free to add new ranges as needed.
     */
    private String getXAxisUnitsText(){
    	
    	String unitsText;
    	if (_timeSpan > HalfLifeInfo.convertYearsToMs(100000)){
    		// Use billions of years for the units.
    		unitsText = NuclearPhysicsStrings.TIME_GRAPH_UNITS_BILLION_YRS;
    	}
    	else if (_timeSpan > 10000){
    		// Use years for the units.
    		unitsText = NuclearPhysicsStrings.TIME_GRAPH_UNITS_YRS;
    	}
    	else {
    		// Use seconds for the units.
    		unitsText = NuclearPhysicsStrings.TIME_GRAPH_UNITS_SECONDS;
    	}
    	
    	return unitsText;
    }
    
    /**
     * Reset the chart.
     */
    private void reset() {

        // Clear the flag that holds off updates after the chart is cleared.
        _chartCleared = false;
        
        // Reset exponential mode.
        _exponentialMode = false;

        // Redraw the chart.
        update();
    }

    private void handleResetChartButtonPressed() {
    	clearDecayedNuclei();
    }
    
	/**
	 * Create the appropriate visual representation for a nucleus.
	 */
	private EnhancedLabeledNucleusNode createNucleusNode(AtomicNucleus nucleus){
    	
    	EnhancedLabeledNucleusNode nucleusNode;
    	
    	NucleusDisplayInfo displayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusConfig(nucleus.getNumProtons(),
    			nucleus.getNumNeutrons());
    	
    	if (displayInfo != null){
    		nucleusNode = new EnhancedLabeledNucleusNode(displayInfo.getImageName(),
                    displayInfo.getIsotopeNumberString(), 
                    displayInfo.getChemicalSymbol(), 
                    displayInfo.getLabelColor() );
    	}
    	else{
    		System.err.println(getClass().getName() + " - Warning: Couldn't locate display info for nucleus.");
    		nucleusNode = new EnhancedLabeledNucleusNode("polonium-nucleus-small.png", "", "", Color.WHITE );
    	}
    	
    	if (_nucleusNodeRadius > 0){
    		nucleusNode.setScale((_nucleusNodeRadius * 2) / nucleusNode.getFullBoundsReference().height);
    	}
    	
    	return nucleusNode;
	}
	
	/**
	 * Position the current nucleus on the time chart.
	 */
	private void positionCurrentNucleus(){
		if (_currentNucleus == null || _currentNucleus.hasDecayed() || _currentNucleus.isPaused()){
			// Nothing to do.
			return;
		}
		
    	double yPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) - _nucleusNodeRadius;
    	double xPos;
    	if (_exponentialMode){
    		xPos = _exponentialTimeLine.mapTimeToHorizPixels(_currentNucleus.getAdjustedActivatedTime()) + 
    		       _graphOriginX + (TIME_ZERO_OFFSET * _msToPixelsFactor) - _nucleusNodeRadius;
    	}
    	else{
        	xPos = _graphOriginX + ((TIME_ZERO_OFFSET_PROPORTION * _timeSpan) + 
        			_currentNucleus.getAdjustedActivatedTime()) * _msToPixelsFactor - _nucleusNodeRadius;
    	}

        _undecayedNucleusNode.setOffset(xPos, yPos);
	}
	
	/**
	 * Position a nucleus node that correspond to a nucleus that has decayed.
	 */
	private void positionDecayedNucleusNode(EnhancedLabeledNucleusNode nucleusNode){
		
		double xPos, yPos;
		double postDecayHeight = _exponentialMode ? POST_DECAY_TIME_LINE_POS_FRACTION_EXPONENTIAL :
			POST_DECAY_TIME_LINE_POS_FRACTION_NORMAL;
		
		// Set the X axis position based on the time at which decay occurred.
    	if (_exponentialMode){
    		xPos = _exponentialTimeLine.mapTimeToHorizPixels(_currentNucleus.getAdjustedActivatedTime()) + 
    		       _graphOriginX + (TIME_ZERO_OFFSET * _msToPixelsFactor) - _nucleusNodeRadius;
    	}
    	else{
        	xPos = _graphOriginX + ((TIME_ZERO_OFFSET_PROPORTION * _timeSpan) + 
        			_currentNucleus.getAdjustedActivatedTime()) * _msToPixelsFactor - _nucleusNodeRadius;
    	}
    	
		if (nucleusNode.getFallCount() != 0){
			// The nucleus is falling, indicating that the corresponding
			// nucleus decayed recently.  Position it in the space between
			// the upper and lower lines based on its fall counter.
			double fallDistance = _usableHeight * (postDecayHeight - PRE_DECAY_TIME_LINE_POS_FRACTION);
        	yPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) 
        	        + (fallDistance * (1 - (double)nucleusNode.getFallCount() / (double)INITIAL_FALL_COUNT))
        	        - _nucleusNodeRadius;
		}
		else{
			// The nucleus has completely fallen, so position it on the lower line.
        	yPos = _usableAreaOriginY + _usableHeight * postDecayHeight - _nucleusNodeRadius;
		}
		
		nucleusNode.setOffset(xPos, yPos);
	}
	
	private void clearDecayedNuclei(){
    	Iterator it = _decayedNucleusNodes.iterator();
    	while (it.hasNext()){
    		EnhancedLabeledNucleusNode decayedNucleusNode = (EnhancedLabeledNucleusNode)it.next();
    		_nonPickableChartNode.removeChild(decayedNucleusNode);
    	}
    	_decayedNucleusNodes.clear();
	}
	
    /**
     * Convenience method for adding tick marks and their labels to the X axis.
     * 
     * @param time
     * @param label
     */
    private void addXAxisTickMark(double time, String label){
    	
    	double timeZeroPosX = _graphOriginX + (TIME_ZERO_OFFSET_PROPORTION * _timeSpan * _msToPixelsFactor);
		PhetPPath tickMark = new PhetPPath(TICK_MARK_COLOR);
		tickMark.setPathTo(new Line2D.Double(0, 0, 0, -TICK_MARK_LENGTH));
		tickMark.setStroke(TICK_MARK_STROKE);
		tickMark.setOffset(timeZeroPosX + (time * _msToPixelsFactor), _graphOriginY);
		_nonPickableChartNode.addChild(tickMark);
		_xAxisTickMarks.add(tickMark);
		PText tickMarkLabel = new PText();
		tickMarkLabel.setText(label);
		tickMarkLabel.setFont(TICK_MARK_LABEL_FONT);
		tickMarkLabel.setOffset(
				tickMark.getOffset().getX() - tickMarkLabel.getFullBoundsReference().width / 2,
				_graphOriginY + (tickMarkLabel.getFullBoundsReference().height * 0.1));
		_nonPickableChartNode.addChild(tickMarkLabel);
		_xAxisTickMarkLabels.add(tickMarkLabel);
    }
    
    /**
     * Utility method for calculating the scale factor needed to make a PNode
     * fit within the specified size.
     */
    private double calculateScaleForTargetSize(PNode node, Dimension2D desiredSize){
    	
    	PBounds bounds = node.getFullBounds();
    	
    	double xDimensionScale = desiredSize.getWidth() / bounds.width;
    	double yDimensionScale = desiredSize.getHeight() / bounds.height;
    	
    	return Math.min(xDimensionScale, yDimensionScale);
    }
    
	//------------------------------------------------------------------------
    // Inner Classes
    //------------------------------------------------------------------------
    
    /**
     * Class that represents the time display readout.
     */
    private class TimeDisplayNode extends PNode{
    	
    	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
        private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
        private final double MILLISECONDS_PER_SECOND = 1000;
        private final double MILLISECONDS_PER_MINUTE = 60000;
        private final double MILLISECONDS_PER_HOUR = 3600000;
        private final double MILLISECONDS_PER_DAY = 86400000;
        private final double MILLISECONDS_PER_YEAR = 3.16e10;
        private final double MILLISECONDS_PER_MILLENIUM = 3.16e13;
        private final double MILLISECONDS_PER_MILLION_YEARS = 3.16e16;
        private final double MILLISECONDS_PER_BILLION_YEARS = 3.16e19;
        private final double MILLISECONDS_PER_TRILLION_YEARS = 3.16e22;
        private final double MILLISECONDS_PER_QUADRILLION_YEARS = 3.16e25;
        private final int EXPONENT_SCALE = 80; // percent
        
        private double _readoutWidth;
        private double _readoutHeight;
    	private PPath _background;
    	private RoundRectangle2D _backgroundShape;
    	private double _currentTimeInMilliseconds;
    	private HTMLNode _timeText;
    	private HTMLNode _unitsText;
    	private PText _spaceText;
    	private HTMLNode _dummyTextNormal;       // Used for scaling.
    	private HTMLNode _dummyTextExponential;  // Used for scaling.
        private DecimalFormat _timeFormatterNoDecimals = new DecimalFormat( "##0" );
        private DecimalFormat _timeFormatterOneDecimal = new DecimalFormat( "##0.0" );
        private ConstantPowerOfTenNumberFormat _thousandsFormatter = new ConstantPowerOfTenNumberFormat("0", 3, EXPONENT_SCALE);
        private ConstantPowerOfTenNumberFormat _millionsFormatter = new ConstantPowerOfTenNumberFormat("0", 6, EXPONENT_SCALE);
        private ConstantPowerOfTenNumberFormat _billionsFormatter = new ConstantPowerOfTenNumberFormat("0", 9, EXPONENT_SCALE);
        private ConstantPowerOfTenNumberFormat _trillionsFormatter = new ConstantPowerOfTenNumberFormat("0", 12, EXPONENT_SCALE);
    	
    	TimeDisplayNode(){
    		_readoutWidth = _usableWidth * 0.22;   // Somewhat arbitrary default values, size is expected to be set through method.
    		_readoutHeight = _usableHeight * 0.2;  // Somewhat arbitrary default values, size is expected to be set through method.
    		_backgroundShape = new RoundRectangle2D.Double(0, 0, _readoutWidth, _readoutHeight, 8, 8);
    		_background = new PPath(_backgroundShape);
    		_background.setPaint(BACKGROUND_COLOR);
    		addChild(_background);
    		_timeText = new HTMLNode();
    		_timeText.setFont(TIME_FONT);
    		addChild(_timeText);
    		_spaceText = new PText(" ");
    		_spaceText.setFont(TIME_FONT);
    		addChild(_spaceText);
    		_unitsText = new HTMLNode();
    		_unitsText.setFont(TIME_FONT);
    		addChild(_unitsText);
    		_dummyTextNormal = new HTMLNode(_timeFormatterNoDecimals.format(9999));
    		_dummyTextNormal.setFont(TIME_FONT);
    		_dummyTextExponential = new HTMLNode(_trillionsFormatter.format(999E12));
    		_dummyTextExponential.setFont(TIME_FONT);
    	}
    	
    	public void setSize(double width, double height){
    		_readoutWidth = width;
    		_readoutHeight = height;
    		_backgroundShape.setFrame( 0, 0, _readoutWidth, _readoutHeight );
    		_background.setPathTo(_backgroundShape);
    		
    		updateTextScaling();
    		updateTimeDisplay();
    	}
    	
    	private void updateTextScaling(){
    		
    		_timeText.setScale(1);
    		_unitsText.setScale(1);
    		_dummyTextExponential.setScale(1);
    		_dummyTextNormal.setScale(1);
    		_spaceText.setScale(1);
    		
    		double maxTextHeight = _readoutHeight * 0.9;
    		double maxTextWidth = _readoutWidth * 0.9;
    		double scalingFactor = 1;
    		double unscaledWidth;
    		double unscaledHeight;

    		if (_currentTimeInMilliseconds > MILLISECONDS_PER_MILLENIUM){
        		unscaledWidth = _dummyTextExponential.getFullBoundsReference().width + _unitsText.getFullBoundsReference().width;
        		unscaledHeight = _dummyTextExponential.getFullBoundsReference().height;
    		}
    		else{
        		unscaledWidth = _dummyTextNormal.getFullBoundsReference().width + _unitsText.getFullBoundsReference().width;
        		unscaledHeight = _dummyTextNormal.getFullBoundsReference().height;
    		}
    		
    		if (unscaledWidth > maxTextWidth){
    			// Scaling is required for this to fit.
    			scalingFactor = maxTextWidth / unscaledWidth;
    		}
    		if (unscaledHeight > maxTextHeight){
    			// Scaling is required for this to fit.
    			if (maxTextHeight / unscaledHeight < scalingFactor){
    				scalingFactor = maxTextHeight / unscaledHeight;
    			}
    			scalingFactor = maxTextWidth / unscaledWidth;
    		}
    		
    		if (scalingFactor != 0){
        		_timeText.setScale(scalingFactor);
        		_spaceText.setScale(scalingFactor);
        		_dummyTextNormal.setScale(scalingFactor);
        		_dummyTextExponential.setScale(scalingFactor);
        		_unitsText.setScale(scalingFactor);
    		}
    	}
    	
    	public void setTime(double milliseconds){

    		_currentTimeInMilliseconds = milliseconds;
    		
    		if (milliseconds < MILLISECONDS_PER_SECOND){
    			// Milliseconds range.
                _timeText.setHTML( _timeFormatterNoDecimals.format(milliseconds) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_MILLISECONDS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_MINUTE){
    			// Seconds range.
                _timeText.setHTML( _timeFormatterOneDecimal.format(milliseconds / MILLISECONDS_PER_SECOND) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_SECONDS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_HOUR){
    			// Minutes range.
                _timeText.setHTML( _timeFormatterOneDecimal.format(milliseconds / MILLISECONDS_PER_MINUTE) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_MINUTES );
    		}
    		else if (milliseconds < MILLISECONDS_PER_DAY){
    			// Hours range.
                _timeText.setHTML( _timeFormatterOneDecimal.format(milliseconds / MILLISECONDS_PER_HOUR) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_HOURS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_YEAR){
    			// Days range.
                _timeText.setHTML( _timeFormatterNoDecimals.format(milliseconds / MILLISECONDS_PER_DAY) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_DAYS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_MILLENIUM){
    			// Years range.
                _timeText.setHTML( _timeFormatterNoDecimals.format(milliseconds / MILLISECONDS_PER_YEAR) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YEARS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_MILLION_YEARS){
    			// Thousand years range (millenia).
                _timeText.setHTML( _thousandsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_BILLION_YEARS){
    			// Million years range.
                _timeText.setHTML( _millionsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_TRILLION_YEARS){
    			// Billion years range.
                _timeText.setHTML( _billionsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
                _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
    		}
    		else if (milliseconds < MILLISECONDS_PER_QUADRILLION_YEARS){
    			// Trillion years range.
                _timeText.setHTML( _trillionsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
                _unitsText.setHTML(NuclearPhysicsStrings.READOUT_UNITS_YRS);
    		}
    		else {
    			_timeText.setHTML( "\u221e"); // Infinity.
    			_unitsText.setHTML("");
    		}
    		
   			updateTextScaling();
            updateTimeDisplay();
    	}
    	
    	private void updateTimeDisplay(){
    		double xPos;
    		double yPos;
    		
    		if (_currentTimeInMilliseconds < MILLISECONDS_PER_MILLENIUM){
        		xPos = _readoutWidth / 2 - 
        		        (_dummyTextNormal.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width + 
        	        	 _unitsText.getFullBoundsReference().width) / 2;
        		xPos += _dummyTextNormal.getFullBoundsReference().width - _timeText.getFullBoundsReference().width;
        		yPos = _readoutHeight / 2 - _dummyTextNormal.getFullBoundsReference().height / 2;
    		}
    		else if (_unitsText.getHTML() != ""){
        		xPos = _readoutWidth / 2 - 
           		        (_dummyTextExponential.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width + 
		        		 _unitsText.getFullBoundsReference().width) / 2;
        		xPos += _dummyTextExponential.getFullBoundsReference().width - _timeText.getFullBoundsReference().width;
        		yPos = _readoutHeight / 2 - _dummyTextExponential.getFullBoundsReference().height / 2;
    		}
    		else{
    			// This is probably the case where the infinity symbol is being displayed.
        		xPos = _readoutWidth / 2 - _timeText.getFullBoundsReference().width / 2;
        		yPos = _readoutHeight / 2 - _timeText.getFullBoundsReference().height / 2;
    		}
    		
    		_timeText.setOffset(xPos, yPos);
    		_spaceText.setOffset(xPos + _timeText.getFullBoundsReference().width, yPos);
    		_unitsText.setOffset(xPos + _timeText.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width, 
    				_timeText.getFullBoundsReference().getMaxY() - _unitsText.getFullBoundsReference().height);
    	}
    }

    /**
     * This class extends the LabeledNucleusNode such that it has a few pieces
     * of information that will make it easier to manage on the chart.
     */
    private class EnhancedLabeledNucleusNode extends LabeledNucleusImageNode {

    	private int _fallCount;     // Counter used for making nucleus node fall incrementally from upper
    	                            
    	public EnhancedLabeledNucleusNode(String imageName, String isotopeNumber, String chemicalSymbol,
				Color labelColor) {
			super(imageName, isotopeNumber, chemicalSymbol, labelColor);
			
			_fallCount = 0;
		}
		
		protected void startFalling(){
			_fallCount = INITIAL_FALL_COUNT;
		}

		protected boolean isFalling(){
			return _fallCount != 0;
		}
		
		protected void decrementFallCount(){
			if (_fallCount > 0){
				_fallCount--;
			}
		}
		
		protected int getFallCount() {
			return _fallCount;
		}

		protected void setDecayTime(double time) {
		}
    }
    
    /**
     * This class represents the exponential time line on which time increases
     * exponentially from left to right.
     * 
     * @author John Blanco
     */
    private class LogarithmicTimeLineNode extends PNode {
    	
    	public  static final double EXPONENTIAL_TIME_LINE_LENGTH = 3.2e22;  // Roughly a trillion years in milliseconds. 
    	private static final double LINE_HEIGHT_PROPORTION = 0.5; // Height of time line as a function of overall
    	                                                          // height of the node.
    	
    	private static final boolean SHOW_OUTLINE = false;        // For debugging.

    	private PPath _outline;
    	private double _timeToPositionMultiplier;
    	private int _width = 0;
    	private int _height = 0;
    	private ArrayList<PhetPPath> _timeLineSections = new ArrayList<PhetPPath>();
    	private double [] _timeLineSectionValues = { 
    			                                       1000,      /* milliseconds in a second */
    			                                       60000,     /* milliseconds in a minute */
    			                                       3600000,   /* milliseconds in an hour */
    			                                       86400000,  /* milliseconds in a day */
    			                                       31.6e9,    /* milliseconds in a year */
    			                                       3.16e13,   /* milliseconds in a millenium */
    			                                       3.16e16,   /* milliseconds in a million years */
    			                                       3.16e19,   /* milliseconds in a billion years */
    			                                       EXPONENTIAL_TIME_LINE_LENGTH
    			                                   };
    	
    	private ArrayList<PText> _sectionLabels = new ArrayList<PText>();

    	/**
    	 * Constructor.
    	 */
    	public LogarithmicTimeLineNode(){
    		_outline = new PPath();
    		_outline.setStrokePaint(Color.RED);
    		_outline.setVisible(SHOW_OUTLINE);
    		addChild(_outline);
    		
    		// Create the sections that will comprise the time line.
    		for (int i = 0; i < _timeLineSectionValues.length; i++){
    			PhetPPath section = new PhetPPath();
    			addChild(section);
    			_timeLineSections.add(section);
    		}
    		
    		// Create the labels for the sections.
    		PText label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_MILLISECONDS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_SECONDS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_MINUTES);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_HOURS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_DAYS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_YRS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_MILLENIA);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_MILLION_YRS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    		label = new PText();
    		label.setText(NuclearPhysicsStrings.TIME_GRAPH_UNITS_BILLION_YRS);
    		label.setFont(SMALL_LABEL_FONT);
    		_sectionLabels.add(label);
			addChild(label);
    	}
    	
    	public void setSize(int width, int height){
    		if ((width < 0 || (height < 0))){
    			// Non-usable values - ignore request.
    			return;
    		}
    		_width = width;
    		_height = height;
    		
    		this.update();
    	}
    	
    	private void update(){
    		
    		// Set the conversion multiplier, which is used to map time values
    		// to positions on the time line.
    		_timeToPositionMultiplier = (double)_width/Math.log(EXPONENTIAL_TIME_LINE_LENGTH);
    		
    		// Figure out how labels should be scaled to fit in alloted space.
    		// This only handles the vertical dimension.
    		double desiredHeight = _height * (1 - LINE_HEIGHT_PROPORTION);
    		PText exampleLabel = (PText)_sectionLabels.get(0);
    		exampleLabel.setScale(1);
    		double labelScaleFactor = desiredHeight / exampleLabel.getHeight();

    		// Set the overall shape, which may or may not be visible.
    		_outline.setPathToRectangle(0, 0, (float)_width, (float)_height);

    		// Set the shape for each section of the overall time line.
    		for (int i = 0; i < _timeLineSections.size(); i++){
    			PhetPPath section = (PhetPPath)_timeLineSections.get(i);
    			float sectionXPos, sectionWidth, sectionHeight;
    			if (i == 0){
    				sectionXPos = 0;
    			}
    			else{
    				sectionXPos = (float)((PhetPPath)_timeLineSections.get(i - 1)).getFullBounds().getMaxX();
    			}
    			sectionWidth = (float)(mapTimeToHorizPixels(_timeLineSectionValues[i]) - sectionXPos);
    			sectionHeight = (float)(_height * LINE_HEIGHT_PROPORTION);
    			section.setPathToRectangle(0, 0, sectionWidth, sectionHeight);
    			section.setOffset(sectionXPos, 0);
    			section.setPaint(new GradientPaint(0, 0, Color.WHITE, sectionWidth,
    					sectionHeight, TIME_LINE_BASE_COLOR));
    			
    			// Position the label for this section.
    			if (_sectionLabels.size() > i){
    				PText label = (PText)_sectionLabels.get(i);
    				label.setScale(1);
    				label.setScale(labelScaleFactor);
    				label.setOffset(sectionXPos + 4, section.getFullBounds().getMaxY());
    			}
    		}
    	}
    
    	/**
    	 * Convert a time value into a position in pixels along the time line.
    	 * 
    	 * @param time - Time, in milliseconds, to convert to position.
    	 * @return position in pixels along the time line.
    	 */
    	public int mapTimeToHorizPixels(double time){
    		int pixels;
    		if (time < 0){
    			pixels = 0;
    		}
    		else{
    		    pixels = (int)Math.round(_timeToPositionMultiplier * Math.log(time + 1));
    		}
    		
    		return pixels;
    	}
    }
}
