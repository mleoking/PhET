/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
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

    // Constants that control the location and size of the graph, around which
    // all the other components are positioned.
    private static final double X_ORIGIN_PROPORTION_WITHOUT_PIE_CHART = 0.1;
    private static final double X_ORIGIN_PROPORTION_WITH_PIE_CHART = 0.25;
    private static final double GRAPH_HEIGHT_PROPORTION = 0.6;
    private static final double Y_ORIGIN_PROPORTION = 0.80;
    private static final double GRAPH_WIDTH_PROPORTION_WITHOUT_CHECKBOXES = 0.85;
    private static final double GRAPH_WIDTH_PROPORTION_WITH_CHECKBOXES = 0.6;
    
    // Constants that control other proportionate aspects of the graph.
    private static final double GRAPH_TEXT_HEIGHT_PROPORTION = 0.06;

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
    private PPath _halfLifeMarkerLine;
    private PText _halfLifeLabel;
    private PPath _lowerXAxisOfGraph;
    private PPath _upperXAxisOfGraph;
    private PPath _yAxisOfGraph;
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
    
    // Parent node that will have interactive portions of the chart.
    private PNode _pickableChartNode;

    // Variables that define the usable area for all nodes on the chart as
    // well as the location of the actual graph.
    Rectangle2D _usableAreaRect = new Rectangle2D.Double();
    Rectangle2D _graphRect = new Rectangle2D.Double();

    // Factor for converting milliseconds to pixels.
    double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

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
     * Constructor - Instantiate using the Builder defined in this file.
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

        // Update the multiplier used for converting from pixels to
        // milliseconds.  Use the multiplier to tweak the span of the x axis.
//        _msToPixelsFactor = ((_usableWidth - _graphOriginX) * 0.98) / _timeSpan;
        
        // Redraw the chart based on these recalculated values.
        update();
    }

    /**
     * Redraw the chart based on the current state.
     */
    private void update() {
    	
        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( _usableAreaRect.getX(), _usableAreaRect.getY(), 
        		_usableAreaRect.getWidth(), _usableAreaRect.getHeight(), 20, 20 ) );

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
        
        // Position the tick marks and their labels on the X axis.
        // TODO: Position tick marks and labels.

        // Update the text for the Y axis labels.
        // Set text for Y axis.
        
        // Position the labels for the axes.
        // TODO: Lower X axis label
        // TODO: Y axis label
        // TODO: Upper axis label
        
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
     * Reset the chart.
     */
    public void reset() {
        // Redraw the chart.
        update();
    }
}
