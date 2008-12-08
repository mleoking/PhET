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
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel;
import edu.colorado.phet.nuclearphysics.util.PhetButtonNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This class displays a "strip chart" of the decay time for multiple
 * iterations of a single nucleus.  It has a linear mode for decay times
 * that are a few seconds or less and an exponential mode for longer decay
 * times.
 *
 * @author John Blanco
 */
public class SingleNucleusAlphaDecayTimeChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Total amount of time in milliseconds represented by this chart.
    private static final double TIME_SPAN = 3200;
    
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
    private static final Font   LARGE_LABEL_FONT = new PhetFont( Font.BOLD, 20 );
    private static final float  HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
    private static final Stroke HALF_LIFE_LINE_STROKE = new BasicStroke( HALF_LIFE_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f }, 0 );
    private static final Color  HALF_LIFE_LINE_COLOR = new Color (0x990000);
    private static final Color  HALF_LIFE_TEXT_COLOR = HALF_LIFE_LINE_COLOR;
    private static final Font   HALF_LIFE_FONT = new PhetFont( Font.BOLD, 16 );

    // Constants that control the location of the origin.
    private static final double X_ORIGIN_PROPORTION_NORMAL_MODE = 0.27;
    private static final double X_ORIGIN_PROPORTION_EXPONENTIAL_MODE = 0.20;
    private static final double Y_ORIGIN_PROPORTION = 0.65;

    // Tweakable values that can be used to adjust where the nuclei appear on
    // the chart.
    private static final double PRE_DECAY_TIME_LINE_POS_FRACTION = 0.20;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION = 0.50;
    private static final double TIME_ZERO_OFFSET = 100; // In milliseconds
    private static final int INITIAL_FALL_COUNT = 5; // Number of clock ticks for nucleus to fall from upper to lower line.

    // Constants that control the way the nuclei look.
    private static final double NUCLEUS_SIZE_PROPORTION = 0.15;  // Fraction of the overall height of the chart.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the model containing the nuclei that are being plotted.
    SingleNucleusAlphaDecayModel _model;
    
    // Variables for tracking information about the nuclei.
    AlphaDecayCompositeNucleus _currentNucleus;
    EnhancedLabeledNucleusNode _undecayedNucleusNode;
    ArrayList _decayedNucleusNodes = new ArrayList();
    
    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _halfLifeMarkerLine;
    private PText _halfLifeLabel;
    private ArrowNode _xAxisOfGraph;
    private ArrayList _xAxisTickMarks;
    private ArrayList _xAxisTickMarkLabels;
    private ArrayList _yAxisTickMarks;
    private ArrayList _yAxisTickMarkLabels;
    private PText _xAxisLabel;
    private PText _yAxisLabel1;
    private PText _yAxisLabel2;
    private TimeDisplayNode _timeDisplayNode;
    private PText _decayTimeLabel;

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
    // TODO: JPB TBD - Do we really listen to this for resets?  Update comment if not.
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

    public SingleNucleusAlphaDecayTimeChart( SingleNucleusAlphaDecayModel model ) {

        _clock = model.getClock();
        _model = model;

        if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
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
                _chartCleared = false;
            }
        } );
        
        // Listen to the model for notifications of relevant events.
        _model.addListener( new AlphaDecayAdapter(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
            
            public void nucleusTypeChanged(){
            	clearDecayedNuclei();
            	if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
            		_exponentialMode = true;
            	}
            	else{
            		_exponentialMode = false;
            	}
            	update();
            };
            
            public void halfLifeChanged(){
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
        _borderNode.setPaint( NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR );
        _nonPickableChartNode.addChild( _borderNode );

        // Create the x axis of the graph.  The initial position is arbitrary
        // and the actual positioning will be done by the update function(s).
        _xAxisOfGraph = new ArrowNode( new Point2D.Double( 10, 10 ), new Point2D.Double( 20, 20 ), 9, 7, 1 );
        _xAxisOfGraph.setStroke( AXES_STROKE );
        _xAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _xAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _xAxisOfGraph );

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
        _xAxisLabel.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _xAxisLabel );
        _yAxisLabel1 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL1 );
        _yAxisLabel1.setFont( SMALL_LABEL_FONT );
        _yAxisLabel1.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel1 );
        _yAxisLabel2 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL2 );
        _yAxisLabel2.setFont( SMALL_LABEL_FONT );
        _yAxisLabel2.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel2 );

        // Add the display of the decay time.
        _timeDisplayNode = new TimeDisplayNode();
        _timeDisplayNode.setTime(0);
        _nonPickableChartNode.addChild(_timeDisplayNode);
        _decayTimeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_LABEL );
        _decayTimeLabel.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _decayTimeLabel );

        // Create the line that will illustrate where the half life is.
        _halfLifeMarkerLine = new PPath();
        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
        _halfLifeMarkerLine.setPaint( NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR );
        _nonPickableChartNode.addChild( _halfLifeMarkerLine );

        // Create the label for the half life line.
        _halfLifeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_HALF_LIFE );
        _halfLifeLabel.setFont( HALF_LIFE_FONT );
        _halfLifeLabel.setTextPaint( HALF_LIFE_TEXT_COLOR );
        _nonPickableChartNode.addChild( _halfLifeLabel );
        
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
        // milliseconds.  Use the multiplier to tweak the span of the x axis.
        _msToPixelsFactor = 0.70 * _usableWidth / TIME_SPAN;
        
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
        		new Point2D.Double( _graphOriginX + ( TIME_SPAN * _msToPixelsFactor ) + 10, _graphOriginY ), 
        		new Point2D.Double( _graphOriginX, _graphOriginY ) );

        // Position the tick marks and their labels on the X axis.
        for ( int i = 0; i < _xAxisTickMarks.size(); i++ ) {

            // Position the tick mark itself.
            PPath tickMark = (PPath) _xAxisTickMarks.get( i );
            double tickMarkPosX = _graphOriginX + (TIME_ZERO_OFFSET * _msToPixelsFactor) 
                    + ( i * 1000 * _msToPixelsFactor );
            tickMark.setPathTo( new Line2D.Double( tickMarkPosX, _graphOriginY, tickMarkPosX, _graphOriginY - TICK_MARK_LENGTH ) );

            // Position the label for the tick mark.
            PText tickMarkLabel = (PText) _xAxisTickMarkLabels.get( i );
            double tickMarkLabelPosX = tickMarkPosX - ( tickMarkLabel.getWidth() / 2 );
            tickMarkLabel.setOffset( tickMarkLabelPosX, _graphOriginY );
        }

        // Set the visibility of the Y axis markers based on the chart mode.
        // These will be positioned anyway later.
       	for (int i = 0; i < _yAxisTickMarks.size(); i++){
           	((PNode)_yAxisTickMarks.get(i)).setVisible(!_exponentialMode);
           	((PNode)_yAxisTickMarkLabels.get(i)).setVisible(!_exponentialMode);
       	}
       	_yAxisLabel1.setVisible(!_exponentialMode);
       	_yAxisLabel2.setVisible(!_exponentialMode);
        
        // Position the tick marks and their labels on the Y axis.
        double preDecayPosY = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION );
        double postDecayPosY = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION );
        PPath yAxisLowerTickMark = (PPath) _yAxisTickMarks.get( 0 );
        yAxisLowerTickMark.setPathTo( new Line2D.Double( _graphOriginX - TICK_MARK_LENGTH, postDecayPosY, 
        		_graphOriginX, postDecayPosY ));

        PPath yAxisUpperTickMark = (PPath) _yAxisTickMarks.get( 1 );
        yAxisUpperTickMark.setPathTo( new Line2D.Double( _graphOriginX - TICK_MARK_LENGTH, preDecayPosY, 
        		_graphOriginX, preDecayPosY ) );

        PText yAxisLowerTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 0 );
        yAxisLowerTickMarkLabel.setOffset( _graphOriginX - ( 1.15 * yAxisLowerTickMarkLabel.getWidth() ), yAxisLowerTickMark.getY() - ( 0.5 * yAxisLowerTickMarkLabel.getHeight() ) );

        PText yAxisUpperTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 1 );
        yAxisUpperTickMarkLabel.setOffset( _graphOriginX - ( 1.15 * yAxisUpperTickMarkLabel.getWidth() ), yAxisUpperTickMark.getY() - ( 0.5 * yAxisUpperTickMarkLabel.getHeight() ) );

        // Position the half life marker.
        positionHalfLifeMarker();

        // Position the labels for the axes.
        _xAxisLabel.setOffset( _usableAreaOriginX + _usableWidth - (_xAxisLabel.getWidth() * 1.2), 
        		((PNode)_xAxisTickMarkLabels.get(0)).getFullBoundsReference().getMaxY() );
        double yAxisLabelCenter = yAxisUpperTickMark.getY() 
                + ((yAxisLowerTickMark.getY() - yAxisUpperTickMark.getY()) / 2);
        _yAxisLabel2.setOffset( yAxisUpperTickMarkLabel.getOffset().getX() - ( 2.0 * _yAxisLabel1.getFont().getSize() ),
        		yAxisLabelCenter + (_yAxisLabel2.getFullBounds().height / 2) );
        _yAxisLabel1.setOffset( _yAxisLabel2.getOffset().getX() - ( 1.1 * _yAxisLabel2.getFont().getSize() ),
        		yAxisLabelCenter + (_yAxisLabel1.getFullBounds().height / 2) );
        
        // Position the time display.
        _timeDisplayNode.setSize(_usableWidth * 0.15, _usableHeight * 0.35);
        _timeDisplayNode.setOffset( _usableAreaOriginX + _usableWidth * 0.03, _usableAreaOriginY + _usableHeight * 0.1);
        PBounds _timeDisplayBounds = _timeDisplayNode.getFullBoundsReference();
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

    	if (_currentNucleus != null){
    		if (!_currentNucleus.hasDecayed()){
    			if (_undecayedNucleusNode == null){
    				// This situation generally indicates that the nucleus was
    				// reset, so create a new node for it.
    				_undecayedNucleusNode = createNucleusNode(_currentNucleus);
    				_nonPickableChartNode.addChild(_undecayedNucleusNode);
    			}
    			positionCurrentNucleus();
    			_timeDisplayNode.setTime(_currentNucleus.getElapsedPreDecayTime());
    		}
    		else{
    			if (_undecayedNucleusNode != null){
        			// The nucleus must have decayed since the last tick.
    				// Create a node to represent the decayed nucleus and
    				// add it to the list of decayed nuclei.
        			EnhancedLabeledNucleusNode newlyDecayedNucleusNode = createNucleusNode(_currentNucleus);
        			_nonPickableChartNode.addChild(newlyDecayedNucleusNode);
        			newlyDecayedNucleusNode.setDecayTime(_currentNucleus.getElapsedPreDecayTime());
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
    	
    	if (modelElement instanceof AlphaDecayCompositeNucleus){
    		if (_currentNucleus != null){
    			// Because of the overall design, we shouldn't get a notice of
    			// an element being added without the previous nucleus having
    			// been removed.
    			System.err.println("Error: Got nucleusAdded event before existing nucleus removed.");
    		}
    		_currentNucleus = (AlphaDecayCompositeNucleus)modelElement;
    		_undecayedNucleusNode = createNucleusNode(_currentNucleus);
    		_nonPickableChartNode.addChild(_undecayedNucleusNode);
    	}
	}

    private void handleModelElementRemoved(Object modelElement) {
    	
    	if (modelElement instanceof AlphaDecayCompositeNucleus){
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
    
    private void positionHalfLifeMarker(){
        // Position the marker for the half life.
    	double halfLife = _model.getHalfLife() * 1000;  // Get half life and convert to ms.
        _halfLifeMarkerLine.reset();
        _halfLifeMarkerLine.moveTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
        		(float) _graphOriginY );
        _halfLifeMarkerLine.lineTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
        		(float) ( _usableAreaOriginY + ( 0.1 * _usableHeight ) ) );
        
        // Position the textual label for the half life.
        _halfLifeLabel.setOffset( _halfLifeMarkerLine.getX() - (_halfLifeLabel.getFullBoundsReference().width / 2),
        		((PNode)_xAxisTickMarkLabels.get(0)).getFullBoundsReference().getMaxY() );
        if (_xAxisLabel.getFullBoundsReference().intersects(_halfLifeLabel.getFullBoundsReference())){
        	_xAxisLabel.setVisible(false);
        }
        else{
        	_xAxisLabel.setVisible(true);
        }
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
     * Reset the chart.
     */
    public void reset() {

        // Clear the flag that holds off updates after the chart is cleared.
        _chartCleared = false;

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

    	switch (nucleus.getNumProtons()){
    	case 84:
    		// Create a labeled nucleus representing Polonium.
    		nucleusNode = new EnhancedLabeledNucleusNode("Polonium Nucleus Small.png",
                    NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                    NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
    		break;
    		
    	case 83:
    		// This nucleus is bismuth, which we use as the pre-decay custom
    		// nucleus.
    		nucleusNode = new EnhancedLabeledNucleusNode("Polonium Nucleus Small.png", 
    				"", // No isotope number.
                    NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR );
    		break;
    		
    	case 82:
    		// Create a labeled nucleus representing Lead.
    		nucleusNode = new EnhancedLabeledNucleusNode("Lead Nucleus Small.png",
                    NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER, 
                    NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.LEAD_LABEL_COLOR );
    		break;
    		
    	case 81:
    		// This is thallium, which we use as the post-decay custom nucleus.
    		nucleusNode = new EnhancedLabeledNucleusNode("Lead Nucleus Small.png",
    				"", // No isotope number.
                    NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR );
    		break;
    		
    	default:
    		assert false;  // This is not a nucleus type that we know how to handle.
    		throw new InvalidParameterException("Unrecognized nucleus type.");
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
		if (_currentNucleus == null || _currentNucleus.hasDecayed()){
			// Nothing to do.
			return;
		}
		
    	double xPos = _graphOriginX + ((_currentNucleus.getElapsedPreDecayTime() * 1000 + TIME_ZERO_OFFSET) 
    	        * _msToPixelsFactor) - _nucleusNodeRadius;
    	double yPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) - _nucleusNodeRadius;

        _undecayedNucleusNode.setOffset(xPos, yPos);
	}
	
	/**
	 * Position a nucleus node that correspond to a nucleus that has decayed.
	 */
	private void positionDecayedNucleusNode(EnhancedLabeledNucleusNode nucleusNode){
		
		double xPos, yPos;
		
		// Set the X axis position based on the time at which decay occurred.
    	xPos = _graphOriginX + (nucleusNode.getDecayTime() * 1000 + TIME_ZERO_OFFSET) * _msToPixelsFactor 
    	        - _nucleusNodeRadius;
    	
		if (nucleusNode.getFallCount() != 0){
			// The nucleus is falling, indicating that the corresponding
			// nucleus decayed recently.  Position it in the space between
			// the upper and lower lines based on its fall counter.
			double fallDistance = _usableHeight * (POST_DECAY_TIME_LINE_POS_FRACTION - PRE_DECAY_TIME_LINE_POS_FRACTION);
        	yPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) 
        	        + (fallDistance * (1 - (double)nucleusNode.getFallCount() / (double)INITIAL_FALL_COUNT))
        	        - _nucleusNodeRadius;
		}
		else{
			// The nucleus has completely fallen, so position it on the lower line.
        	yPos = _usableAreaOriginY + _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION - _nucleusNodeRadius;
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
    
	//------------------------------------------------------------------------
    // Inner Classes
    //------------------------------------------------------------------------
    
    /**
     * Class the represents the time display.
     */
    private class TimeDisplayNode extends PNode{
    	
    	private final double TEXT_HEIGHT_PROPORTION = 0.8;
    	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
        private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
    	private PPath _background;
    	private RoundRectangle2D _backgroundShape;
    	private PText _timeText;
    	private PText _unitsText;
        DecimalFormat timeFormatter = new DecimalFormat( "##0.0" );
    	
    	TimeDisplayNode(){
    		_backgroundShape = new RoundRectangle2D.Double(0, 0, _usableWidth * 0.2, _usableHeight * 0.2, 8, 8);
    		_background = new PPath(_backgroundShape);
    		_background.setPaint(BACKGROUND_COLOR);
    		addChild(_background);
    		_timeText = new PText();
    		_timeText.setFont(TIME_FONT);
    		addChild(_timeText);
    		_unitsText = new PText();
    		_unitsText.setFont(TIME_FONT);
    		addChild(_unitsText);
    	}
    	
    	public void setSize(double width, double height){
    		_backgroundShape.setFrame( 0, 0, width, height );
    		_background.setPathTo(_backgroundShape);
    		_timeText.setScale(1);
    		double desiredTextHeight = height * TEXT_HEIGHT_PROPORTION;
    		double currentTextHeight = _timeText.getFullBoundsReference().height;
    		if (desiredTextHeight > 0 && currentTextHeight > 0){
        		_timeText.setScale(desiredTextHeight / currentTextHeight);
        		_unitsText.setScale(desiredTextHeight / currentTextHeight * 0.67);
    		}
    		update();
    	}
    	
    	public void setTime(double seconds){
            _timeText.setText( new String (timeFormatter.format(seconds)) );
            // TODO: JPB TBD - Need to make this a resource, need to make it handle different scales.
            _unitsText.setText("sec");
            update();
    	}
    	
    	private void update(){
    		double width = _backgroundShape.getWidth();
    		double height = _backgroundShape.getHeight();
    		_timeText.setOffset( width * 0.2, height / 2 - _timeText.getFullBoundsReference().height / 2 );
    		_unitsText.setOffset( width * 0.6, _timeText.getFullBoundsReference().getMaxY() 
    				- _unitsText.getFullBoundsReference().height * 1.1);
    	}
    }

    /**
     * This class extends the LabeledNucleusNode such that it has a few pieces
     * of information that will make it easier to manage on the chart.
     */
    private class EnhancedLabeledNucleusNode extends LabeledNucleusNode {

    	private int _fallCount;     // Counter used for making nucleus node fall incrementally from upper
    	                            // to lower position on the graph.
    	private double _decayTime;  // Time at which the associated nucleus decayed.
    	
		public EnhancedLabeledNucleusNode(String imageName, String isotopeNumber, String chemicalSymbol,
				Color labelColor) {
			super(imageName, isotopeNumber, chemicalSymbol, labelColor);
			
			_fallCount = 0;
			_decayTime = Double.POSITIVE_INFINITY;
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

		protected double getDecayTime() {
			return _decayTime;
		}

		protected void setDecayTime(double time) {
			_decayTime = time;
		}
    }
}
