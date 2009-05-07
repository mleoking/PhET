/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This class represents a chart that shows the proportion of nuclei that have
 * decayed versus time.  This is essentially the exponential display curve.
 *
 * @author John Blanco
 */
public class NuclearDecayProportionChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
    // Constants for controlling the appearance of the chart.
    private static final Color  BORDER_COLOR = Color.DARK_GRAY;
    private static final float  BORDER_STROKE_WIDTH = 6f;
    private static final Stroke BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final float  THICK_AXIS_LINE_WIDTH = 2.5f;
    private static final Stroke THICK_AXIS_STROKE = new BasicStroke( THICK_AXIS_LINE_WIDTH );
    private static final float  THIN_AXIS_LINE_WIDTH = 0.75f;
    private static final Stroke THIN_AXIS_STROKE = new BasicStroke( THIN_AXIS_LINE_WIDTH );
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
    private static final float  DATA_CURVE_LINE_WIDTH_PROPORTION = 0.015f;

    // Constants that control the location and size of the graph, around which
    // all the other components are positioned.
    private static final double X_ORIGIN_PROPORTION_WITHOUT_PIE_CHART = 0.1;
    private static final double X_ORIGIN_PROPORTION_WITH_PIE_CHART = 0.25;
    private static final double GRAPH_HEIGHT_PROPORTION = 0.6;
    private static final double Y_ORIGIN_PROPORTION = 0.80;
    private static final double GRAPH_WIDTH_PROPORTION_WITHOUT_CHECKBOXES = 0.85;
    private static final double GRAPH_WIDTH_PROPORTION_WITH_CHECKBOXES = 0.6;
    private static final double PIE_CHART_WIDTH_PROPORTION = 0.2;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Time span covered by this chart, in milliseconds.
    private double _timeSpan;
    
    // Half life of primary (i.e. decaying) element.
	private double _halfLife; // Half life of decaying element, in milliseconds.

	// Variables that control chart appearance.
	private String _preDecayElementLabel;
	private Color _preDecayLabelColor;
	private String _postDecayElementLabel;
	private Color _postDecayLabelColor;
	private boolean _pieChartEnabled;
	private boolean _showPostDecayCurve;
	private boolean _timeMarkerLabelEnabled;

	// References to the various components of the chart.
    private PPath _borderNode;
    private ShadowPText _numUndecayedNucleiLabel;
    private PText _numUndecayedNucleiText;
    private ShadowPText _numDecayedNucleiLabel;
    private PText _numDecayedNucleiText;
    private PText _dummyNumberText;
    private ArrayList _halfLifeLines = new ArrayList();
    private Stroke _dataCurveStroke = new BasicStroke();
    private ProportionsPieChartNode _pieChart;
    private GraphNode _graph;

    // Decay events that are represented on the graph.
    ArrayList _decayEvents = new ArrayList();
    
    // Parent node that will be non-pickable and will contain all of the
    // non-interactive portions of the chart.
    private PComposite _nonPickableChartNode;
    
    // Parent node that will have interactive portions of the chart.
    private PNode _pickableChartNode;
    
    // Variables that define the usable area for all nodes on the chart as
    // well as the location of the actual graph.
    Rectangle2D _usableAreaRect = new Rectangle2D.Double();
    Rectangle2D _graphRect = new Rectangle2D.Double();

    //------------------------------------------------------------------------
    // Builder + Constructor
    //------------------------------------------------------------------------

    /**
     * Builder pattern used to construct this chart.  This is from "Effective
     * Java", 2nd edition, pgs 11-16.
     */
    public static class Builder {
    	// Required parameters.
    	private double timeSpan;  // Span of time covered by this chart, in milliseconds.
    	private double halfLife; // Half life of decaying element, in milliseconds.
    	private String preDecayElementLabel;
    	private Color preDecayLabelColor;
    	
    	// Optional parameters - initialized to default values.
    	private boolean pieChartEnabled = false;
    	private boolean showPostDecayCurve = false;
    	private String postDecayElementLabel = NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL;
    	private Color postDecayLabelColor = NuclearPhysicsConstants.CUSTOM_NUCLEUS_POST_DECAY_COLOR;
    	private boolean timeMarkerLabelEnabled = false;
    	
    	public Builder( double timeSpan, double halfLife, String preDecayElementLabel, Color preDecayLabelColor) {
    		this.timeSpan = timeSpan;
    		this.halfLife = halfLife;
    		this.preDecayElementLabel = preDecayElementLabel;
    		this.preDecayLabelColor = preDecayLabelColor;
    	}
    	
    	public Builder pieChartEnabled( boolean val ) {
    		pieChartEnabled = val;
    		return this;
    	}
    	public Builder showPostDecayCurve( boolean val ) {
    		showPostDecayCurve = val;
    		return this;
    	}
    	public Builder postDecayElementLabel( String val ) {
    		postDecayElementLabel = val;
    		return this;
    	}
    	public Builder postDecayLabelColor( Color val ) {
    		postDecayLabelColor = val;
    		return this;
    	}
    	public Builder timeMarkerLabelEnabled( boolean val ) {
    		timeMarkerLabelEnabled = val;
    		return this;
    	}
    	public NuclearDecayProportionChart build(){
    		return new NuclearDecayProportionChart( this );
    	}
    }

    /**
     * Constructor - Instantiate using the Builder class defined in this file.
     */
    private NuclearDecayProportionChart( Builder builder ) {
    	
        _timeSpan = builder.timeSpan;
    	_halfLife = builder.halfLife;
    	_preDecayElementLabel = builder.preDecayElementLabel;
    	_preDecayLabelColor = builder.preDecayLabelColor;
    	_postDecayElementLabel = builder.postDecayElementLabel;
    	_postDecayLabelColor = builder.postDecayLabelColor;
    	_pieChartEnabled = builder.pieChartEnabled;
    	_showPostDecayCurve = builder.showPostDecayCurve;
    	_timeMarkerLabelEnabled = builder.timeMarkerLabelEnabled;

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

        // Create the graph.
        _graph = new GraphNode();
        addChild( _graph );
        
        // Add the pie chart (if enabled).
        if ( _pieChartEnabled ){
        	_pieChart = new ProportionsPieChartNode();
        	_nonPickableChartNode.addChild( _pieChart );
        }

        /*
        // Create the lower x axis of the graph.
        _lowerXAxisOfGraph = new PPath();
        _lowerXAxisOfGraph.setStroke( THICK_AXIS_STROKE );
        _lowerXAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _lowerXAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _lowerXAxisOfGraph );

        // Create the upper x axis of the graph.
        _upperXAxisOfGraph = new PPath();
        _upperXAxisOfGraph.setStroke( THIN_AXIS_STROKE );
        _upperXAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _upperXAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _upperXAxisOfGraph );

        // Create the x axis of the graph.
        _yAxisOfGraph = new PPath();
        _yAxisOfGraph.setStroke( THIN_AXIS_STROKE );
        _yAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _yAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _yAxisOfGraph );
        
        // Add the text for the Y axis.
        _yAxisLabel1 = new PText( NuclearPhysicsStrings.FIFTY_PER_CENT );
        _yAxisLabel1.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _yAxisLabel1 );
        _yAxisLabel2 = new PText( NuclearPhysicsStrings.ONE_HUNDRED_PER_CENT );
        _yAxisLabel2.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _yAxisLabel2 );
        
        // Add the label for the upper X axis.
        _upperXAxisLabel = new PText( NuclearPhysicsStrings.HALF_LIVES_LABEL );
        _upperXAxisLabel.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _upperXAxisLabel );
        
        // Add the pie chart (if enabled).
        if ( _pieChartEnabled ){
        	_pieChart = new ProportionsPieChartNode();
        	_nonPickableChartNode.addChild( _pieChart );
        }

        // Add the text for the X & Y axes.
//        _xAxisLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_X_AXIS_LABEL + " (" + NuclearPhysicsStrings.DECAY_TIME_UNITS + ")" );
//        _xAxisLabel.setFont( SMALL_LABEL_FONT );
//        _nonPickableChartNode.addChild( _xAxisLabel );
//        _yAxisLabel1 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL1 );
//        _yAxisLabel1.setFont( SMALL_LABEL_FONT );
//        _yAxisLabel1.rotate( 1.5 * Math.PI );
//        _nonPickableChartNode.addChild( _yAxisLabel1 );
//        _yAxisLabel2 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL2 );
//        _yAxisLabel2.setFont( SMALL_LABEL_FONT );
//        _yAxisLabel2.rotate( 1.5 * Math.PI );
//        _nonPickableChartNode.addChild( _yAxisLabel2 );
        
        // Add the pie chart.
//        _pieChartValues = new PieValue[]{
//                new PieChartNode.PieValue( _model.getNumNuclei(), NuclearPhysicsConstants.POLONIUM_LABEL_COLOR ),
//                new PieChartNode.PieValue( 0, NuclearPhysicsConstants.LEAD_LABEL_COLOR )};
//        _pieChart = new PieChartNode(_pieChartValues, new Rectangle(20, 20));  // Arbitrary initial size, resized later.
//        _nonPickableChartNode.addChild( _pieChart );
        
        // Add the text for labeling the pre- and post-decay quantities of the
        // nuclei.
//        _numUndecayedNucleiLabel = new ShadowPText();
//        _numUndecayedNucleiLabel.setFont(LARGE_LABEL_FONT);
//        _numUndecayedNucleiLabel.setTextPaint(Color.YELLOW);
//        _nonPickableChartNode.addChild(_numUndecayedNucleiLabel);
//        _numUndecayedNucleiText = new PText("0");
//        _numUndecayedNucleiText.setFont(LARGE_LABEL_FONT);
//        _nonPickableChartNode.addChild(_numUndecayedNucleiText);
//        _numDecayedNucleiLabel = new ShadowPText();
//        _numDecayedNucleiLabel.setFont(LARGE_LABEL_FONT);
//        _nonPickableChartNode.addChild(_numDecayedNucleiLabel);
//        _numDecayedNucleiText = new PText("0");
//        _numDecayedNucleiText.setFont(LARGE_LABEL_FONT);
//        _nonPickableChartNode.addChild(_numDecayedNucleiText);
        
        // Create a dummy text value for consistent positioning of the real
        // numerical values.
//        _dummyNumberText = new PText("00");
//        _dummyNumberText.setFont(LARGE_LABEL_FONT);

        // Create the line that will illustrate where the half life is.
//        _halfLifeMarkerLine = new PPath();
//        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
//        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
//        _halfLifeMarkerLine.setPaint( NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR );
//        _nonPickableChartNode.addChild( _halfLifeMarkerLine );
        
        // Create the label for the half life line.
//        _halfLifeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_HALF_LIFE );
//        _halfLifeLabel.setFont( HALF_LIFE_FONT );
//        _halfLifeLabel.setTextPaint( HALF_LIFE_TEXT_COLOR );
//        _nonPickableChartNode.addChild( _halfLifeLabel );
 * 
 */
    }

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public void setTimeSpan( double timeSpan ){
    	_timeSpan = timeSpan;
    	update();
    }
    
    

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation is changed.
     * 
     * @param 
     */
    private void updateBounds( Rectangle2D rect ) {
    	
    	if ( ( rect.getHeight() <= 0 ) || ( rect.getWidth() <= 0 ) ){
    		// This happens sometimes during initialization.  Don't know why,
    		// but we just ignore it if it does.
    		return;
    	}

        // Recalculate the usable area for the chart.
        _usableAreaRect.setRect( rect.getX() + BORDER_STROKE_WIDTH, rect.getY() + BORDER_STROKE_WIDTH,
        		rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 ), rect.getHeight() - ( BORDER_STROKE_WIDTH * 2 ) );

        // Decide where the data graph, around which all other components are
        // positioned, is located.
        double graphRectX, graphRectY, graphWidth, graphHeight;
        if ( _pieChartEnabled ){
            graphRectX = _usableAreaRect.getX() + ( X_ORIGIN_PROPORTION_WITH_PIE_CHART * _usableAreaRect.getWidth() );
        }
        else{
            graphRectX = _usableAreaRect.getX() + ( X_ORIGIN_PROPORTION_WITHOUT_PIE_CHART * _usableAreaRect.getWidth() );
        }
        graphWidth = _usableAreaRect.getWidth() * GRAPH_WIDTH_PROPORTION_WITHOUT_CHECKBOXES;
        graphRectY = _usableAreaRect.getY() + _usableAreaRect.getHeight() - 
            ( Y_ORIGIN_PROPORTION * _usableAreaRect.getHeight() );
        graphHeight = _usableAreaRect.getHeight() * GRAPH_HEIGHT_PROPORTION;
        _graphRect.setRect(graphRectX, graphRectY, graphWidth, graphHeight);

        // Update other variables that depend on the size of the chart.
        _dataCurveStroke = new BasicStroke( (float)(_graphRect.getHeight() * DATA_CURVE_LINE_WIDTH_PROPORTION ),
        		BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        
        // Redraw the chart based on these recalculated values.
        update();
    }

    /**
     * Redraw the chart based on the current state.  This is basically the
     * place where the chart gets laid out.
     */
    private void update() {
    	
        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( _usableAreaRect.getX(), _usableAreaRect.getY(), 
        		_usableAreaRect.getWidth(), _usableAreaRect.getHeight(), 20, 20 ) );
        
        // Position the pie chart if enabled.
        double graphLeftEdge = 0;
        if ( _pieChartEnabled ){
        	_pieChart.scale( 1 );
        	_pieChart.scale( _usableAreaRect.getWidth() * PIE_CHART_WIDTH_PROPORTION 
        			/ _pieChart.getFullBoundsReference().getWidth() );
        	
        	// Position the pie chart so that it is a little ways in from the
        	// left of the chart and vertically centered.
        	_pieChart.setOffset( BORDER_STROKE_WIDTH * 2, _usableAreaRect.getCenterY() 
        			- (_pieChart.getFullBoundsReference().getHeight() / 2));
        	
        	graphLeftEdge = _pieChart.getFullBoundsReference().getMaxX();
        }
        
        // Position the graph.
        _graph.update( (_usableAreaRect.getWidth() - graphLeftEdge) * 0.95, _usableAreaRect.getHeight() * 0.9 );
        _graph.setOffset( graphLeftEdge + 5, _usableAreaRect.getCenterY() - (_graph.getFullBoundsReference().height / 2 ) );
        
        /*

        // Position the x and y axes.
        _lowerXAxisOfGraph.setPathTo( new Line2D.Double(_graphRect.getX(), _graphRect.getMaxY(), 
        		_graphRect.getMaxX(), _graphRect.getMaxY() ) );
        
        _upperXAxisOfGraph.setPathTo( new Line2D.Double(_graphRect.getX(), _graphRect.getY(), 
        		_graphRect.getMaxX(), _graphRect.getY() ) );
        
        _yAxisOfGraph.setPathTo( new Line2D.Double(_graphRect.getX(), _graphRect.getY(), 
        		_graphRect.getX(), _graphRect.getMaxY() ) ); 

        // Position and size labels for the Y axis.
        double graphLabelHeight = _usableAreaRect.getHeight() * GRAPH_TEXT_HEIGHT_PROPORTION;
        _yAxisLabel1.setScale( 1 );
        _yAxisLabel2.setScale( 2 );
        double scale = graphLabelHeight / _yAxisLabel1.getFullBoundsReference().getHeight();
        _yAxisLabel1.setScale( scale );
        _yAxisLabel2.setScale( scale );
        _yAxisLabel1.setOffset( _graphRect.getX() - _yAxisLabel1.getFullBoundsReference().getWidth() - 5,
        		_graphRect.getCenterY() - ( graphLabelHeight / 2 ) );
        _yAxisLabel2.setOffset( _graphRect.getX() - _yAxisLabel2.getFullBoundsReference().getWidth() - 5,
        		_graphRect.getY() - ( graphLabelHeight / 2 ) );
        
        // Position and size labels for the upper X axis.
        _upperXAxisLabel.setScale( 1 );
        scale = graphLabelHeight / _upperXAxisLabel.getFullBoundsReference().getHeight();
        _upperXAxisLabel.setScale(scale);
        _upperXAxisLabel.setOffset(_graphRect.getX() + 5, _graphRect.getY() - 
        		_upperXAxisLabel.getFullBoundsReference().height - 5 );
        
        // Update the half life lines.
        updateHalfLifeLines();
        
        // Reposition the data curve(s)
        updateDecayCurves();
        
        // Position the tick marks and their labels on the X axis.
        // TODO: Position tick marks and labels.

        // Update the text for the Y axis labels.
        // Set text for Y axis.
        
        // Position the labels for the axes.
        // TODO: Lower X axis label
        // TODO: Y axis label
        // TODO: Upper axis label
         * 
         * 
         */
        
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
     * Clear the chart.
     */
    public void clear() {
        _decayEvents.clear();
        _graph.clearData();
    }
    
    /**
     * Add a decay event to the chart, which will be represented as a point
     * on the proportion curve.  IMPORTANT: It is assumed that these events
     * are added in order of increasing time.
     * 
     * @param time - time that event occurred
     * @param percentage - percentage of this nucleus remaining after decay
     * occurred.
     */
    public void addDecayEvent( double time, double percentage ){
    	
    	// Validate arguments.
    	if ((time < 0) || (percentage < 0) || (percentage > 100)){
    		throw ( new IllegalArgumentException(this.getClass().getName() + 
    				": Invalid argument for data point addition."));
    	}
    	
    	Point2D decayEvent = new Point2D.Double( time, percentage );
		_decayEvents.add( decayEvent );
		_graph.graphDecayEvent( decayEvent );
    }
    
    /**
     * This class defines a node that consists of a pie chart and the various
     * labels needed for this chart.  It assumes only two sections exist, one
     * for the amount of pre-decay element and one for the amount of post-
     * decay.
     */
    private class ProportionsPieChartNode extends PNode {

    	private final double INITIAL_WIDTH = 100;
    	private final double INITIAL_HEIGHT = INITIAL_WIDTH * 1.5;
    	private final double PIE_CHART_WIDTH_PROPORTION = 0.9;
    	private final int INITIAL_PIE_CHART_WIDTH = (int)(Math.round(INITIAL_WIDTH * PIE_CHART_WIDTH_PROPORTION));
    	
    	private PieChartNode _pieChartNode;
        private PieChartNode.PieValue[] _pieChartValues;
    	
		public ProportionsPieChartNode() {
	        _pieChartValues = new PieValue[]{
	                new PieChartNode.PieValue( 1, _preDecayLabelColor ),
	                new PieChartNode.PieValue( 0, _postDecayLabelColor )};
	        _pieChartNode = new PieChartNode(_pieChartValues, new Rectangle(INITIAL_PIE_CHART_WIDTH, INITIAL_PIE_CHART_WIDTH));
	        addChild( _pieChartNode );
		}
    }
    
    /**
     * This class represents the graph portion of this chart, meaning the
     * portion with the axes, data curves, etc.
     * 
     */
    private class GraphNode extends PNode {
    	
        // Constants that control other proportionate aspects of the graph.
        private static final double GRAPH_TEXT_HEIGHT_PROPORTION = 0.06;

        // Various components that make up the graph.
    	private PPath _sizingRect;
        private final PPath _lowerXAxisOfGraph;
        private final PPath _upperXAxisOfGraph;
        private final PPath _yAxisOfGraph;
        private ArrayList _xAxisTickMarks;
        private ArrayList _xAxisTickMarkLabels;
        private ArrayList _yAxisTickMarks;
        private ArrayList _yAxisTickMarkLabels;
        private final PText _yAxisLabel1;
        private final PText _yAxisLabel2;
        private final PText _upperXAxisLabel;
        private final PNode _pickableGraphLayer;
        private final PNode _nonPickableGraphLayer;
        private final PNode _dataPresentationLayer;
        private PPath _preDecayProportionCurve;
        private PPath _postDecayProportionCurve;
        
        // Factor for converting milliseconds to pixels.
        double _msToPixelsFactor = 1; // Arbitrary init val, updated later.
        
        // TODO: Fix this name when refactoring is complete.
        private final Rectangle2D _graphRectFixThis = new Rectangle2D.Double();

		public GraphNode() {

			// Set up the layers (so to speak) for the graph.
			
			_nonPickableGraphLayer = new PNode();
			_nonPickableGraphLayer.setPickable(false);
			_nonPickableGraphLayer.setChildrenPickable(false);
			addChild( _nonPickableGraphLayer );
			
	        // Set up the parent node that will contain the data points.
			_dataPresentationLayer = new PNode();
			_dataPresentationLayer.setPickable( false );
			_dataPresentationLayer.setChildrenPickable( false );
	        addChild( _dataPresentationLayer );
			
			_pickableGraphLayer = new PNode();
			_pickableGraphLayer.setPickable(true);
			_pickableGraphLayer.setChildrenPickable(true);
			addChild( _pickableGraphLayer );
			
			// Create the labels at the left of the graph, since they will
			// affect the position of the origin.
			
	        _lowerXAxisOfGraph = new PPath();
	        _lowerXAxisOfGraph.setStroke( THICK_AXIS_STROKE );
	        _lowerXAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
	        _lowerXAxisOfGraph.setPaint( AXES_LINE_COLOR );
	        _nonPickableGraphLayer.addChild( _lowerXAxisOfGraph );

	        // Create the upper x axis of the graph.
	        _upperXAxisOfGraph = new PPath();
	        _upperXAxisOfGraph.setStroke( THIN_AXIS_STROKE );
	        _upperXAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
	        _upperXAxisOfGraph.setPaint( AXES_LINE_COLOR );
	        _nonPickableGraphLayer.addChild( _upperXAxisOfGraph );

	        // Create the x axis of the graph.
	        _yAxisOfGraph = new PPath();
	        _yAxisOfGraph.setStroke( THIN_AXIS_STROKE );
	        _yAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
	        _yAxisOfGraph.setPaint( AXES_LINE_COLOR );
	        _nonPickableGraphLayer.addChild( _yAxisOfGraph );
	        
	        // Add the text for the Y axis.
	        _yAxisLabel1 = new PText( NuclearPhysicsStrings.FIFTY_PER_CENT );
	        _yAxisLabel1.setFont( SMALL_LABEL_FONT );
	        _nonPickableGraphLayer.addChild( _yAxisLabel1 );
	        _yAxisLabel2 = new PText( NuclearPhysicsStrings.ONE_HUNDRED_PER_CENT );
	        _yAxisLabel2.setFont( SMALL_LABEL_FONT );
	        _nonPickableGraphLayer.addChild( _yAxisLabel2 );
	        
	        // Add the label for the upper X axis.
	        _upperXAxisLabel = new PText( NuclearPhysicsStrings.HALF_LIVES_LABEL );
	        _upperXAxisLabel.setFont( SMALL_LABEL_FONT );
	        _nonPickableGraphLayer.addChild( _upperXAxisLabel );
	        
			_sizingRect = new PPath();
			_sizingRect.setStroke(THICK_AXIS_STROKE);
			_sizingRect.setStrokePaint(Color.red);
			addChild(_sizingRect);
		}
		
		public void update( double newWidth, double newHeight ){
			
			_sizingRect.setPathTo( new Rectangle2D.Double(0, 0, newWidth, newHeight ) );

	        // Position and size labels for the Y axis, since they will affect
			// the location of the graph's origin.
	        double graphLabelHeight = newHeight * GRAPH_TEXT_HEIGHT_PROPORTION;
	        _yAxisLabel1.setScale( 1 );
	        _yAxisLabel2.setScale( 1 );
	        double scale = graphLabelHeight / _yAxisLabel1.getFullBoundsReference().getHeight();
	        _yAxisLabel1.setScale( scale );
	        _yAxisLabel2.setScale( scale );
	        double maxYAxisLabelWidth = Math.max(_yAxisLabel1.getFullBoundsReference().width,
	        		_yAxisLabel2.getFullBoundsReference().width );
	        _yAxisLabel1.setOffset( maxYAxisLabelWidth - _yAxisLabel1.getFullBoundsReference().width,
	        		(newHeight / 2) - ( graphLabelHeight / 2 ) );
	        _yAxisLabel2.setOffset( maxYAxisLabelWidth - _yAxisLabel2.getFullBoundsReference().width,
	        		graphLabelHeight);
	        
	        // Create a rectangle that defines where the graph itself is,
	        // excluding all labels and such.

	        _graphRectFixThis.setRect(maxYAxisLabelWidth, graphLabelHeight, newWidth - maxYAxisLabelWidth,
	        		newHeight - 3 * graphLabelHeight);
	        
	        // Update the multiplier used for converting from pixels to
	        // milliseconds.  Use the multiplier to tweak the span of the x axis.
	        _msToPixelsFactor = _graphRectFixThis.getWidth() / _timeSpan;	        

	        // Create the graph axes.
	        
			_lowerXAxisOfGraph.setPathTo( new Line2D.Double(_graphRectFixThis.getX(), _graphRectFixThis.getMaxY(), 
					_graphRectFixThis.getMaxX(), _graphRectFixThis.getMaxY() ) );
	        
	        _upperXAxisOfGraph.setPathTo( new Line2D.Double(_graphRectFixThis.getX(), _graphRectFixThis.getY(), 
	        		_graphRectFixThis.getMaxX(), _graphRectFixThis.getY() ) );
	        
	        _yAxisOfGraph.setPathTo( new Line2D.Double(_graphRectFixThis.getX(), _graphRectFixThis.getY(), 
	        		_graphRectFixThis.getX(), _graphRectFixThis.getMaxY() ) ); 

	        // Position and size labels for the upper X axis.
	        _upperXAxisLabel.setScale( 1 );
	        scale = graphLabelHeight / _upperXAxisLabel.getFullBoundsReference().getHeight();
	        _upperXAxisLabel.setScale(scale);
	        _upperXAxisLabel.setOffset(_graphRectFixThis.getX() + 5, _graphRectFixThis.getY() - 
	        		_upperXAxisLabel.getFullBoundsReference().height - 5 );
	        
	        // Update the half life lines.
	        updateHalfLifeLines();
	        
	        // Reposition the data curve(s)
	        updateDecayCurves();
	        
	        // Position the tick marks and their labels on the X axis.
	        // TODO: Position tick marks and labels.

	        // Update the text for the Y axis labels.
	        // Set text for Y axis.
	        
	        // Position the labels for the axes.
	        // TODO: Lower X axis label
	        // TODO: Y axis label
	        // TODO: Upper axis label
		}
		
		public void clearData(){
	        _dataPresentationLayer.removeAllChildren();
	        _preDecayProportionCurve = null;
	        _postDecayProportionCurve = null;
		}
		
	    /**
	     * Add the vertical lines to the chart that represent a half life, one for
	     * each half life duration.  This does some sanity testing to make sure
	     * that there isn't a ridiculous number of half life lines on the graph.
	     */
	    private void updateHalfLifeLines(){
	    	
	    	int numHalfLifeLines = (int)Math.floor( _timeSpan / _halfLife );
	    	
	    	if ( numHalfLifeLines != _halfLifeLines.size() ){
	    		// Either this is the first time through, or something has changed
	    		// that requires the half life lines to be reallocated.  First,
	    		// remove the existing lines.
	    		for ( Iterator it = _halfLifeLines.iterator(); it.hasNext(); ){
	    			PNode halfLifeLine = (PNode)it.next();
	    			_nonPickableGraphLayer.removeChild( halfLifeLine );
	    			it.remove();
	    		}
	    		
	    		// Allocate the correct number of new lines.
	    		for ( int i = 0; i < numHalfLifeLines; i++ ){
	    			PPath halfLifeLine = new PPath();
	    			halfLifeLine.setStroke(HALF_LIFE_LINE_STROKE);
	    			halfLifeLine.setStrokePaint(HALF_LIFE_LINE_COLOR);
	    			_nonPickableGraphLayer.addChild( halfLifeLine );
	    			_halfLifeLines.add( halfLifeLine );
	    		}
	    	}
	    	
	    	// Set the size and location for each of the half life lines.
			for ( int i = 0; i < _halfLifeLines.size(); i++ ){
				PPath halfLifeLine = (PPath)_halfLifeLines.get(i);
				halfLifeLine.setPathTo( new Line2D.Double(0, 0, 0, _graphRectFixThis.getHeight() ) );
				halfLifeLine.setOffset( (i + 1) * _halfLife * _msToPixelsFactor + _graphRectFixThis.getX(), 
						_graphRectFixThis.getY() );
			}
	    }
	    
	    private void updateDecayCurves(){
	    	
	    	// Get rid of the existing curves.
	    	_dataPresentationLayer.removeAllChildren();
	    	_preDecayProportionCurve = null;
	    	_postDecayProportionCurve = null;
	    	
	    	// Add new nodes in the correct positions.
	    	for ( Iterator it = _decayEvents.iterator(); it.hasNext();){
	    		graphDecayEvent((Point2D)it.next());
	    	}
	    }
	    
	    /**
	     * Add the specified location to the graph of decay events.
	     *
	     * @param decayEventLocation - x represents time, y represents percentage
	     * of element remaining after this event.
	     */
	    private void graphDecayEvent( Point2D decayEventLocation ){
	    	
	    	float xPos = (float)(_msToPixelsFactor * decayEventLocation.getX() + _graphRectFixThis.getX());
	    	float yPosPreDecay = (float)( _graphRectFixThis.getMaxY() - ( ( ( 100 - decayEventLocation.getY() ) / 100 ) 
	    			* _graphRectFixThis.getHeight() ) );
	    	float yPosPostDecay = (float)( _graphRectFixThis.getMaxY() - ( ( decayEventLocation.getY() / 100 ) 
	    			* _graphRectFixThis.getHeight() ) );
	    	
	    	if ( _preDecayProportionCurve == null ){
	    		// Curve doesn't exist - create it.
	    		_preDecayProportionCurve = new PPath();
	    		_preDecayProportionCurve.moveTo( xPos, yPosPreDecay );
	    		_preDecayProportionCurve.setStroke( _dataCurveStroke );
	    		_preDecayProportionCurve.setStrokePaint( _preDecayLabelColor );
	        	_dataPresentationLayer.addChild( _preDecayProportionCurve );
	    	}
	    	else{
	    		// Add the next segment to the curve.
	    		_preDecayProportionCurve.lineTo(xPos, yPosPreDecay);
	    	}
	    	
	    	if ( _postDecayProportionCurve == null ){
	    		// Curve doesn't exist - create it.
	    		_postDecayProportionCurve = new PPath();
	    		_postDecayProportionCurve.moveTo( xPos, yPosPostDecay );
	    		_postDecayProportionCurve.setStroke( _dataCurveStroke );
	    		_postDecayProportionCurve.setStrokePaint( _postDecayLabelColor );
	        	_dataPresentationLayer.addChild( _postDecayProportionCurve );
	        	_postDecayProportionCurve.setVisible(_showPostDecayCurve);
	    	}
	    	else{
	    		// Add the next segment to the curve.
	    		_postDecayProportionCurve.lineTo(xPos, yPosPostDecay);
	    	}
	    }
    }

    /**
     * Main routine for stand alone testing.
     * 
     * @param args
     */
    public static void main(String [] args){
    	
        final NuclearDecayProportionChart proportionsChart = 
        	new NuclearDecayProportionChart.Builder(Carbon14Nucleus.HALF_LIFE * 3.2, 
        		Carbon14Nucleus.HALF_LIFE, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
        		NuclearPhysicsConstants.CARBON_COLOR).pieChartEnabled(true).
        		showPostDecayCurve(false).timeMarkerLabelEnabled(true).build();
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addScreenChild( proportionsChart );
        frame.setSize( 800, 400 );
        proportionsChart.componentResized(frame.getBounds());
        frame.setVisible( true );
        
        for (int i = 0; i < 5; i++){
        	// Put some data points on the chart to test its behavior.
        	proportionsChart.addDecayEvent(Carbon14Nucleus.HALF_LIFE / 5 * i, i * 10);
        }
        
        canvas.addComponentListener( new ComponentListener(){

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				proportionsChart.componentResized(e.getComponent().getBounds());
			}

			public void componentShown(ComponentEvent e) {
			}
        	
        });
    }
}
