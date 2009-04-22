/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.ResizeArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.AbstractAlphaDecayNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusAlphaDecayCanvas;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusAlphaDecayModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This class displays a "strip chart" of the decay time for multiple nuclei,
 * and also allows the user to adjust the half life for some types of nuclei.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayTimeChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Total amount of time in milliseconds represented by this chart.
    private static final double TIME_SPAN = 3200;
    
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
    private static final float  HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
    private static final Stroke HALF_LIFE_LINE_STROKE = new BasicStroke( HALF_LIFE_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f }, 0 );
    private static final Color  HALF_LIFE_LINE_COLOR = new Color (238, 0, 0);
    private static final Color  HALF_LIFE_TEXT_COLOR = HALF_LIFE_LINE_COLOR;
    private static final Font   HALF_LIFE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final double RESIZE_HANDLE_SIZE = 35;

    // Constants that control the location of the origin.
    private static final double X_ORIGIN_PROPORTION = 0.25;
    private static final double Y_ORIGIN_PROPORTION = 0.65;

    // Tweakable values that can be used to adjust where the nuclei appear on
    // the chart.
    private static final double PRE_DECAY_TIME_LINE_POS_FRACTION = 0.20;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION = 0.50;
    private static final double TIME_ZERO_OFFSET = 100; // In milliseconds
    private static final int INITIAL_FALL_COUNT = 5; // Number of clock ticks for nucleus to fall from upper to lower line.

    // Constants that control the way the nuclei look.
    private static final double NUCLEUS_SIZE_PROPORTION = 0.1;  // Fraction of the overall height of the chart.
    
    // Constants that control where the nuclei fall to when they decay in
    // order to create a histogram sort of look to the decay pattern.
    private static final int NUM_HISTOGRAM_BUCKETS = 60;
    private static final double HISTOGRAM_OVERLAP_PROPORTION = 0.2;

    // Offsets used when positioning atoms prior to decay so that they look
    // like a bunch of atoms instead of just one.  The values are in terms
    // of the proportion of the chart height.
	private static final Point2D [] BUNCHING_OFFSETS = {new Point2D.Double(0, 0), 
		new Point2D.Double(-0.02, -0.025), new Point2D.Double(0.025, -0.02), new Point2D.Double(0.015, 0.025), 
		new Point2D.Double(-0.015, 0.015)};
	
	// Counter used when offsetting nucleus positions in order to make them
	// look like a bunch.
	private int _bunchingCounter = 0;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the model containing the nuclei that are being plotted.
    MultiNucleusAlphaDecayModel _model;
    
    // Reference to the canvas on which this chart resides.  Needed for
    // certain interactions.
    MultiNucleusAlphaDecayCanvas _canvas;
    
    // Variable for tracking information about the nuclei.
    private HashMap _mapNucleiToNucleiData = new HashMap();
    private int _preDecayCount;
    private int _postDecayCount;
    
    // Data structure for the histogram-ish placement of decayed nuclei.
    private int [] _decaysPerHistogramBucket = new int[NUM_HISTOGRAM_BUCKETS];

    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _halfLifeMarkerLine;
    private ResizeArrowNode _halfLifeHandleNode;
    private PText _halfLifeLabel;
    private ArrowNode _xAxisOfGraph;
    private ArrayList _xAxisTickMarks;
    private ArrayList _xAxisTickMarkLabels;
    private ArrayList _yAxisTickMarks;
    private ArrayList _yAxisTickMarkLabels;
    private PText _xAxisLabel;
    private PText _yAxisLabel1;
    private PText _yAxisLabel2;
    private ShadowPText _numUndecayedNucleiLabel;
    private PText _numUndecayedNucleiText;
    private ShadowPText _numDecayedNucleiLabel;
    private PText _numDecayedNucleiText;
    private PText _dummyNumberText;
    private PieChartNode _pieChart;
    private PieChartNode.PieValue[] _pieChartValues;

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

    // Clock that we listen to for moving the nuclei.
    ConstantDtClock _clock;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public MultiNucleusAlphaDecayTimeChart( MultiNucleusAlphaDecayModel model, MultiNucleusAlphaDecayCanvas canvas ) {

        _clock = model.getClock();
        _model = model;
        _canvas = canvas;

        // Register as a clock listener.
        _clock.addClockListener( new ClockAdapter() {

            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked( clockEvent );
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
        		switch (_model.getNucleusType()){
        		case NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM:
        			_pieChartValues[0].setColor(NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR);
        			_pieChartValues[1].setColor(NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR);
        			break;
        			
        		case NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM:
        			_pieChartValues[0].setColor(NuclearPhysicsConstants.POLONIUM_LABEL_COLOR);
        			_pieChartValues[1].setColor(NuclearPhysicsConstants.LEAD_LABEL_COLOR);
        			break;
        			
        		default:
        			// If these ever show up, someone will notice (and
        			// presumably fix the problem).
        			_pieChartValues[0].setColor(Color.PINK);
         			_pieChartValues[1].setColor(Color.ORANGE);
        			break;
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

        PText yTickMarkLabel1 = new PText();
        yTickMarkLabel1.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add( yTickMarkLabel1 );
        _nonPickableChartNode.addChild( yTickMarkLabel1 );

        PText yTickMarkLabel2 = new PText();
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
        
        // Add the pie chart.
        _pieChartValues = new PieValue[]{
                new PieChartNode.PieValue( MultiNucleusAlphaDecayModel.MAX_NUCLEI, NuclearPhysicsConstants.POLONIUM_LABEL_COLOR ),
                new PieChartNode.PieValue( 0, NuclearPhysicsConstants.LEAD_LABEL_COLOR )};
        _pieChart = new PieChartNode(_pieChartValues, new Rectangle(20, 20));  // Arbitrary initial size, resized later.
        _nonPickableChartNode.addChild( _pieChart );
        
        // Add the text for labeling the pre- and post-decay quantities of the
        // nuclei.
        _numUndecayedNucleiLabel = new ShadowPText();
        _numUndecayedNucleiLabel.setFont(LARGE_LABEL_FONT);
        _numUndecayedNucleiLabel.setTextPaint(Color.YELLOW);
        _nonPickableChartNode.addChild(_numUndecayedNucleiLabel);
        _numUndecayedNucleiText = new PText("0");
        _numUndecayedNucleiText.setFont(LARGE_LABEL_FONT);
        _nonPickableChartNode.addChild(_numUndecayedNucleiText);
        _numDecayedNucleiLabel = new ShadowPText();
        _numDecayedNucleiLabel.setFont(LARGE_LABEL_FONT);
        _nonPickableChartNode.addChild(_numDecayedNucleiLabel);
        _numDecayedNucleiText = new PText("0");
        _numDecayedNucleiText.setFont(LARGE_LABEL_FONT);
        _nonPickableChartNode.addChild(_numDecayedNucleiText);
        
        // Create a dummy text value for consistent positioning of the real
        // numerical values.
        _dummyNumberText = new PText("00");
        _dummyNumberText.setFont(LARGE_LABEL_FONT);

        // Create the line that will illustrate where the half life is.
        _halfLifeMarkerLine = new PPath();
        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
        _halfLifeMarkerLine.setPaint( NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR );
        _nonPickableChartNode.addChild( _halfLifeMarkerLine );
        
        // Create the handle that will allow the user to control the half life.
        _halfLifeHandleNode = new ResizeArrowNode(RESIZE_HANDLE_SIZE, 0, Color.GREEN, Color.YELLOW);
        _pickableChartNode.addChild( _halfLifeHandleNode );
        _halfLifeHandleNode.addInputEventListener(new PBasicInputEventHandler(){
        	boolean halfLifeChanged;
        	public void mousePressed(PInputEvent event) {
        		halfLifeChanged = false;
        		_model.setPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		_model.setPaused(false);
        		if (halfLifeChanged){
        			if (_model.resetActiveAndDecayedNuclei() != 0){
            			_canvas.autoPressResetNucleiButton();
        			}
        		}
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double newHalfLife = _model.getHalfLife() + (d.width / _msToPixelsFactor);
                if (newHalfLife >= MIN_HALF_LIFE && newHalfLife <= (TIME_SPAN * 0.95)){
	                _model.setHalfLife(newHalfLife);
	        		halfLifeChanged = true;
                }
            }
        });


        // Create the label for the half life line.
        _halfLifeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_HALF_LIFE );
        _halfLifeLabel.setFont( HALF_LIFE_FONT );
        _halfLifeLabel.setTextPaint( HALF_LIFE_TEXT_COLOR );
        _nonPickableChartNode.addChild( _halfLifeLabel );
        
        updateNucleusGraphLabels();
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
        _msToPixelsFactor = ((_usableWidth - _graphOriginX) * 0.98) / TIME_SPAN;
        
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

        // Update the text for the Y axis tick mark labels.
        setYAxisTickMarkLabelText();
        
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
        yAxisLowerTickMarkLabel.setOffset( _graphOriginX - yAxisLowerTickMark.getWidth() -
        		( 1.15 * yAxisLowerTickMarkLabel.getWidth() ), 
        		yAxisLowerTickMark.getY() - ( 0.5 * yAxisLowerTickMarkLabel.getHeight() ) );

        PText yAxisUpperTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 1 );
        yAxisUpperTickMarkLabel.setOffset( _graphOriginX - yAxisUpperTickMark.getWidth() -
        		( 1.15 * yAxisUpperTickMarkLabel.getWidth() ),
        		yAxisUpperTickMark.getY() - ( 0.5 * yAxisUpperTickMarkLabel.getHeight() ) );

        // Position the labels for the axes.
        _xAxisLabel.setOffset( _graphOriginX - (_xAxisLabel.getFullBoundsReference().width / 2),
        		((PNode)_xAxisTickMarkLabels.get(0)).getFullBoundsReference().getMaxY() );
        double yAxisLabelCenter = yAxisUpperTickMark.getY() 
                + ((yAxisLowerTickMark.getY() - yAxisUpperTickMark.getY()) / 2);
        _yAxisLabel2.setOffset( yAxisLowerTickMarkLabel.getOffset().getX() - ( 1.8 * _yAxisLabel1.getFont().getSize() ),
        		yAxisLabelCenter + (_yAxisLabel2.getFullBounds().height / 2) );
        _yAxisLabel1.setOffset( _yAxisLabel2.getOffset().getX() - ( 1.1 * _yAxisLabel2.getFont().getSize() ),
        		yAxisLabelCenter + (_yAxisLabel1.getFullBounds().height / 2) );
        
        // Position the pie chart.
        int pieChartDiameter = (int)Math.round(Math.min(_usableWidth * 0.10, _usableHeight * 0.4));
        _pieChart.setArea( new Rectangle(pieChartDiameter, pieChartDiameter) );
        PBounds pieChartBounds = _pieChart.getFullBoundsReference();
        _pieChart.setOffset(
        		_yAxisLabel1.getFullBoundsReference().getX() - _pieChart.getFullBoundsReference().getWidth(),
        		yAxisLabelCenter - _pieChart.getFullBoundsReference().height / 2 );
        
        // Position the dummy text so that it can be used as a reference for
        // positioning the real text.
        pieChartBounds = _pieChart.getFullBoundsReference(); // Refresh the reference.
        double numberTextWidth = _dummyNumberText.getFullBoundsReference().width;
        double numberTextHeight = _dummyNumberText.getFullBoundsReference().height;
        _dummyNumberText.setOffset(pieChartBounds.getX() - numberTextWidth * 1.2, 
        		preDecayPosY - (numberTextHeight / 2));

        // Update and position the labels for the quantities of the various nuclei.
        updateNucleusGraphLabels();
        _numUndecayedNucleiLabel.setOffset( 
        		_dummyNumberText.getFullBoundsReference().x - _numUndecayedNucleiLabel.getFullBoundsReference().width * 1.1,
        		preDecayPosY - (numberTextHeight / 2));
        _numDecayedNucleiLabel.setOffset(
        		_dummyNumberText.getFullBoundsReference().x - _numDecayedNucleiLabel.getFullBoundsReference().width * 1.1,
        		postDecayPosY - (numberTextHeight / 2));

        // Position the half life marker.
        positionHalfLifeMarker();

        // Update the numbers for the various nuclei.
        updateNucleiNumberText();
        
        // Update the pie chart proportions.
        updatePieChartProportions();
        
        // Rescale the nucleus nodes and set their positions.
        Set entries = _mapNucleiToNucleiData.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            AtomicNucleus nucleus = (AtomicNucleus)entry.getKey();
            NucleusData nucleusData = (NucleusData)_mapNucleiToNucleiData.get(nucleus);
            if (nucleusData != null){
            	nucleusData.updateNucleusNodeScale();
            	nucleusData.updateNucleusNodePosition();
            }
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

    	// Update the internal and visual state for each nucleus based on the
    	// state of the nuclei within the model.
        Set entries = _mapNucleiToNucleiData.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            AtomicNucleus nucleus = (AtomicNucleus)entry.getKey();
            NucleusData nucleusData = (NucleusData)_mapNucleiToNucleiData.get(nucleus);
            if (nucleusData != null){
            	nucleusData.updateNucleusDataState();
            }
        }
    }
    
    private void updateNucleusGraphLabels(){
    	if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM){
    		_numUndecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL);
    		_numUndecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.POLONIUM_LABEL_COLOR);
    		_numUndecayedNucleiLabel.setShadowColor(Color.BLACK);
    		_numDecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL);
    		_numDecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.LEAD_LABEL_COLOR);
    		_numDecayedNucleiLabel.setShadowColor(Color.WHITE);
    	}
    	else {
    		_numUndecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL);
    		_numUndecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR);
    		_numUndecayedNucleiLabel.setShadowColor(Color.BLACK);
    		_numDecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL);
    		_numDecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR);
    		_numDecayedNucleiLabel.setShadowColor(Color.WHITE);
    	}
    }
    
    /**
     * Update the labels that indicate the number of undecayed and decayed nuclei.
     */
    private void updateNucleiNumberText(){
    	
    	// Update the values.
    	_numUndecayedNucleiText.setText(Integer.toString(_preDecayCount));
    	_numDecayedNucleiText.setText(Integer.toString(_postDecayCount));
    	
    	// Update the positions so that they remain centered in their area.
		double preDecayPosY = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION );
        double postDecayPosY = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION );
        double numberTextHeight = _dummyNumberText.getFullBoundsReference().height;

        // This needs to be here, rather than in the update function, so that
        // the text can be right justified.
        double rightSideXPos = _dummyNumberText.getFullBoundsReference().getMaxX();
        _numUndecayedNucleiText.setOffset( rightSideXPos -_numUndecayedNucleiText.getFullBoundsReference().width, 
        		preDecayPosY - (numberTextHeight / 2));
        _numDecayedNucleiText.setOffset( rightSideXPos -_numDecayedNucleiText.getFullBoundsReference().width, 
        		postDecayPosY - (numberTextHeight / 2));
    }
    
    /**
     * Update the proportions represented by the pie chart based on the
     * relative numbers of the two nucleus types.
     */
    private void updatePieChartProportions(){
    	
    	if (_preDecayCount == 0 && _postDecayCount == 0){
    		// If nothing is currently in the active state, set the chart up
    		// so that it looks like the pre-decay color.
        	_pieChartValues[0].setValue(1);
        	_pieChartValues[1].setValue(0);
    		
    	}
    	else{
        	_pieChartValues[0].setValue(_preDecayCount);
        	_pieChartValues[1].setValue(_postDecayCount);
    	}
    	_pieChart.setPieValues(_pieChartValues);
    }
    
	private void handleModelElementAdded(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		// Verify that this nucleus wasn't already added.
    		assert !_mapNucleiToNucleiData.containsKey(modelElement);
    		
    		// At least for now, it is expected that all nuclei added to this
    		// chart are alpha decayers that are not moving towards decay yet.
    		assert (modelElement instanceof AbstractAlphaDecayNucleus);
    		assert (((AbstractDecayNucleus)modelElement).isDecayActive() == false);
    		
    		// Create a data set for this nucleus and add it to the internal
    		// map.
    		_mapNucleiToNucleiData.put(modelElement, new NucleusData((AbstractDecayNucleus)modelElement));
    	}
	}

    private void handleModelElementRemoved(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		NucleusData nucleusData = (NucleusData)_mapNucleiToNucleiData.get(modelElement);
    		if (nucleusData != null) {
    			// Remove this node from the chart.
    			nucleusData.removeNodeFromChart();
    			
    			// Remove this node from the map.
    			_mapNucleiToNucleiData.remove(modelElement);
    		}
    		else{
    			System.err.println("Error: Unable to locate nucleus data in map.");
    		}
    	}
	}
    
    private void positionHalfLifeMarker(){
        // Position the marker for the half life.
    	double halfLife = _model.getHalfLife();  // Get half life.
        _halfLifeMarkerLine.reset();
        _halfLifeMarkerLine.moveTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
        		(float)(_graphOriginY + ((_usableHeight - _graphOriginY) * 0.4)) );
        _halfLifeMarkerLine.lineTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
        		(float) ( _usableAreaOriginY + ( 0.1 * _usableHeight ) ) );
        
        // If the marker is overlapping with a tick mark label, redraw it so
        // that it is completely above the axis.
        for (int i = 0; i < _xAxisTickMarkLabels.size(); i++){
        	PNode tickMark = (PNode)_xAxisTickMarkLabels.get(i);
        	if (tickMark.getFullBoundsReference().intersects(_halfLifeMarkerLine.getFullBoundsReference())){
        		// Redraw the line to be above the axis.
                _halfLifeMarkerLine.reset();
                _halfLifeMarkerLine.moveTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
                		(float)_graphOriginY );
                _halfLifeMarkerLine.lineTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
                		(float) ( _usableAreaOriginY + ( 0.1 * _usableHeight ) ) );
        		break;
        	}
        }
        
        // If it is a custom nucleus, position and show the handle.
        if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
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
    }
    
    /**
     * Reset the chart.
     */
    public void reset() {
        // Redraw the chart.
        update();
    }

	private void setYAxisTickMarkLabelText(){
		
		String upperLabel, lowerLabel;
		
		switch (_model.getNucleusType()){
		case NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM:
			upperLabel = NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL;
			lowerLabel = NuclearPhysicsStrings.DECAYED_CUSTOM_NUCLEUS_CHEMICAL_SYMBOL;
			break;
			
		case NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM:
			upperLabel = NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER;
			lowerLabel = NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER;
			break;
			
		default:
			upperLabel = "";
			lowerLabel = "";
			break;
		}
		
		if (_yAxisTickMarkLabels.size() >= 2){
    		((PText)_yAxisTickMarkLabels.get(0)).setText(lowerLabel);
    		((PText)_yAxisTickMarkLabels.get(1)).setText(upperLabel);
		}
	}
    
    /**
     * Map the given decay time to one of the buckets of the histogram.
     * 
     * @param decayTime - Decay time in milliseconds.
     * @return
     */
    private int mapDecayTimeToHistogramBucket(double decayTime){
    	
    	if (decayTime > TIME_SPAN){
    		// This decay is off the chart and doesn't go in a bucket.  Return
    		// the largest integer in order to signal this to the caller.
    		return Integer.MAX_VALUE;
    	}
    	
    	return (int)Math.floor(decayTime / (TIME_SPAN / NUM_HISTOGRAM_BUCKETS));
    }
    
    /**
     * Calculate the "fall target", which is the vertical location on the
     * chart where a nucleus should be positioned once it has decayed and it
     * has completed the process of falling from the non-decayed position.
     * 
     * @param positionInHistogramBucket - The position within a histogram
     * bucket that should be calculated.  For instance, if the caller wants
     * the vertical position for the first node in this bucket, it should
     * pass in a value of 0.
     * @return - A normalized value between 0 and 1 indicating an amount
     * below the pre-decay line.  A value of 1 would signify that the
     * position is on the post-decay line, 0 would be on the pre-decay line.
     */
    private double calculateFallTarget( int positionInHistogramBucket ){
    	double maxFallDistance = _usableHeight * (POST_DECAY_TIME_LINE_POS_FRACTION - PRE_DECAY_TIME_LINE_POS_FRACTION);
    	double offsetFromPostDecayLine = 
    		(_nucleusNodeRadius * 2) * ((double)positionInHistogramBucket * HISTOGRAM_OVERLAP_PROPORTION);
    	return Math.max((maxFallDistance - offsetFromPostDecayLine) / maxFallDistance, 0);
    }
    
    /**
     * This class contains the data and functionality that is used to
     * represent a nucleus on the chart.
     */
    private class NucleusData{
    	
    	private static final int STATE_INACTIVE = 1;
    	private static final int STATE_PRE_DECAY = 2;
    	private static final int STATE_POST_DECAY = 3;
    	
    	private AbstractDecayNucleus _nucleus;
		private LabeledNucleusNode _nucleusNode;
    	private int _fallCount;
    	private double _fallTarget;
    	private int _internalState;
    	private int _decayBucket;
    	private Point2D _bunchingOffset;
    	
    	public NucleusData(AbstractDecayNucleus nucleus){
    		_nucleus = nucleus;
    		_fallCount = 0;
    		_fallTarget = 0;
    		_internalState = STATE_INACTIVE;
    		_decayBucket = Integer.MAX_VALUE;
    		_bunchingOffset = BUNCHING_OFFSETS[0];
    	}
    	
    	/**
    	 * Update the state of this nucleus data based on any changes detected
    	 * in the state of the nucleus in the model.
    	 */
    	public void updateNucleusDataState(){
    		
    		switch ( _internalState ){
    		
    		case STATE_INACTIVE:

    			if (_nucleus.isDecayActive()){
    				
    				// This nucleus has become active.
        	    	_internalState = STATE_PRE_DECAY;
        	    	_preDecayCount++;
        	    	updateNucleiNumberText();
        	    	updatePieChartProportions();
        			
        	    	if (_nucleusNode == null){
            			// Create a node for this nucleus.
            			_nucleusNode = createNucleusNode();
            	    	_nucleusNode.setScale((_nucleusNodeRadius * 2) / _nucleusNode.getFullBoundsReference().height);
        	    	}
        			
        	    	// Add the node to the chart.
        	    	_nonPickableChartNode.addChild(_nucleusNode);
        	    
        	    	// Reset internal counter in preparation for decay.
        	    	_fallCount = INITIAL_FALL_COUNT;
        	    	
        	    	// Set the offset for this node so that the nodes don't
        	    	// all just stack directly on top of each other.
        	    	_bunchingOffset = BUNCHING_OFFSETS[_bunchingCounter];
        	    	_bunchingCounter = (_bunchingCounter + 1) % BUNCHING_OFFSETS.length;

        	    	// Position the newly added node.
        	    	updateNucleusNodePosition();
        		}
    			break;
    			
    		case STATE_PRE_DECAY:
    			
    			if (_nucleus.hasDecayed()){
    				
    				// The nucleus has decayed since the last update.
    				_internalState = STATE_POST_DECAY;
    				_preDecayCount--;
    				_postDecayCount++;
        	    	updateNucleiNumberText();
        	    	updatePieChartProportions();

    				// Calculate the final position where this nucleus should end
        			// up based how many other nuclei have already decayed at
        			// approximately this time.
        			_decayBucket = mapDecayTimeToHistogramBucket(_nucleus.getActivatedTime());
        			if (_decayBucket < _decaysPerHistogramBucket.length){
        				_fallTarget = calculateFallTarget(_decaysPerHistogramBucket[_decayBucket]);
        				_decaysPerHistogramBucket[_decayBucket]++;
        			}
        			else{
        				_fallTarget = calculateFallTarget(0);
        			}
        			
        			// Start counting down on the fall counter.
        			_fallCount--;
    			}
    			else if (!_nucleus.isDecayActive()){
    				// The nucleus has been deactivated.
        	    	_internalState = STATE_INACTIVE;
        	    	_preDecayCount--;
        	    	updateNucleiNumberText();
        	    	updatePieChartProportions();
        	    	removeNodeFromChart();
    			}
    			
    			// Update the position of this node.
    			updateNucleusNodePosition();

    			break;
    			
    		case STATE_POST_DECAY:

    			if (_nucleus.isDecayActive() && !_nucleus.hasDecayed()){
    				// The nucleus has been reset.
    				_internalState = STATE_PRE_DECAY;
    				_fallCount = INITIAL_FALL_COUNT;
    				if (_decayBucket < NUM_HISTOGRAM_BUCKETS){
    					_decaysPerHistogramBucket[_decayBucket]--;
    				}
    				_decayBucket = Integer.MAX_VALUE;
    				_postDecayCount--;
    				_preDecayCount++;
        	    	updateNucleiNumberText();
        	    	updatePieChartProportions();
    			}
    			else if (!_nucleus.isDecayActive() && !_nucleus.hasDecayed()){
    				// The nucleus has been deactivated.
        	    	_internalState = STATE_INACTIVE;
        	    	_postDecayCount--;
        	    	updateNucleiNumberText();
        	    	updatePieChartProportions();
        	    	removeNodeFromChart();
    			}
    			else if (_fallCount > 0){
    				// Nucleus is still falling.
    				_fallCount--;
    				updateNucleusNodePosition();
    			}
    			break;
    			
    		default:
    			System.err.println("Unexpected nucleus data state.");
    		    assert false;
    			break;
    		}
    	}
    	
    	/**
    	 * Remove this nucleus's node from the chart.
    	 */
    	public void removeNodeFromChart(){
    		if ((_nucleusNode != null) && (_nonPickableChartNode.isAncestorOf(_nucleusNode))){
        		_nonPickableChartNode.removeChild(_nucleusNode);
        		if (_internalState == STATE_PRE_DECAY){
        			_preDecayCount--;
        		}
        		else if (_internalState == STATE_POST_DECAY){
        			_postDecayCount--;
        		}
    		}
    		if (_decayBucket != Integer.MAX_VALUE){
    			_decaysPerHistogramBucket[_decayBucket]--;
    		}
    	}
    	
    	/**
    	 * Update the scale setting for the node.  This is generally done if
    	 * and when the chart is resized.
    	 */
    	public void updateNucleusNodeScale(){

    		if (_nucleusNode == null){
        		// This nucleus does not have a node, probably because it has not
        		// been activated.  That's okay - just ignore the positioning
        		// request.
        		return;
        	}
        	
      		_nucleusNode.setScale(1);
   	    	_nucleusNode.setScale((_nucleusNodeRadius * 2) / _nucleusNode.getFullBoundsReference().height);
    	}
    	
    	/**
    	 * Position this nucleus on the chart.
    	 */
    	public void updateNucleusNodePosition(){
    		
        	double xPos, yPos;
        	
        	if (_nucleusNode == null){
        		// This nucleus does not have a node, probably because it has not
        		// been activated.  That's okay - just ignore the positioning
        		// request.
        		return;
        	}
        	
        	if (!_nucleus.hasDecayed()){
        		// The nucleus has not yet decayed, so position it on the upper line.
            	yPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) - _nucleusNodeRadius
            	        + _bunchingOffset.getY() * _usableHeight;
        	}
        	else{
        		// The nucleus has decayed.  See if it is still falling.
        		if (_fallCount != 0){
        			// The nucleus is falling.  Position it in the space between
        			// the upper and lower lines based on its fall target.
        			double fallDistance = (_usableHeight * 
        			        (POST_DECAY_TIME_LINE_POS_FRACTION - PRE_DECAY_TIME_LINE_POS_FRACTION)) * _fallTarget;
                	yPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) 
                	        + (fallDistance * (1 - (double)_fallCount / (double)INITIAL_FALL_COUNT))
                	        - _nucleusNodeRadius;
        		}
        		else{
        			// The nucleus has completely fallen, so put it at the fall target.
                	yPos = _usableAreaOriginY + _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION +
                	        _usableHeight * (POST_DECAY_TIME_LINE_POS_FRACTION - PRE_DECAY_TIME_LINE_POS_FRACTION)
                	        * _fallTarget - _nucleusNodeRadius;
        		}
        	}
        	
        	xPos = _graphOriginX + (_nucleus.getActivatedTime() + TIME_ZERO_OFFSET) * _msToPixelsFactor 
        	        - _nucleusNodeRadius + (_bunchingOffset.getX() * _usableHeight);
        	_nucleusNode.setOffset(xPos, yPos);
    	}
    	
    	/**
    	 * Create the appropriate visual representation for this nucleus.
    	 * 
    	 * @return - A PNode that depicts this nucleus.
    	 */
    	private LabeledNucleusNode createNucleusNode(){
        	
        	LabeledNucleusNode nucleusNode;

        	switch (_nucleus.getNumProtons()){
        	case 84:
        		// Create a labeled nucleus representing Polonium.
        		nucleusNode = new LabeledNucleusNode("Polonium Nucleus Small.png",
                        NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                        NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                        NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
        		break;
        		
        	case 83:
        		// This nucleus is bismuth, which we use as the pre-decay custom
        		// nucleus.
        		nucleusNode = new LabeledNucleusNode("Polonium Nucleus Small.png", 
        				"", // No isotope number.
                        NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                        NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR );
        		break;
        		
        	case 82:
        		// Create a labeled nucleus representing Lead.
        		nucleusNode = new LabeledNucleusNode("Lead Nucleus Small.png",
                        NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER, 
                        NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL, 
                        NuclearPhysicsConstants.LEAD_LABEL_COLOR );
        		break;
        		
        	case 81:
        		// This is thallium, which we use as the post-decay custom nucleus.
        		nucleusNode = new LabeledNucleusNode("Lead Nucleus Small.png",
        				"", // No isotope number.
                        NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                        NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR );
        		break;
        		
        	default:
        		assert false;  // This is not a nucleus type that we know how to handle.
        		throw new InvalidParameterException("Unrecognized nucleus type.");
        	}
        	
        	return nucleusNode;
    	}
    }
}
