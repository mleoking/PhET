/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.ResizeArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayCanvas;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.nodes.PLine;


/**
 * AlphaDecayEnergyChart - This class displays a chart that depicts the
 * interaction between the alpha particles and the energy barrier for a single
 * atomic nucleus.
 *
 * @author John Blanco
 */
public class AlphaDecayEnergyChart extends PNode implements AlphaParticle.Listener {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Constants for controlling the nature of the Y-axis of the chart.  These
	// are quite important for understanding the overall nature of the chart's
	// behavior.  The chart does not use real units of energy for the Y-axis
	// because the shape of the graph is somewhat unrealistic.  So, since we
	// need some sort of units to work with, we define an arbitrary number of
	// them and an offset for where zero should be.
	private static final double NUM_Y_AXIS_UNITS = 100;
	private static final double Y_AXIS_ZERO_OFFSET = 55; // Sets origin of y-axis relative to bottom of chart.
	
	// Constants for setting the initial positions of the energy lines/curves.
	private static final double POLONIUM_INITIAL_TOTAL_ENERGY = 8;
	private static final double POLONIUM_INITIAL_MINIUMIM_POTENTIAL_ENERGY = 1; // Defines low point of the potential energy curve.
	private static final double POLONIUM_INITIAL_PEAK_POTENTIAL_ENERGY = 14; // Defines peak of the potential energy curve.
	private static final double CUSTOM_INITIAL_TOTAL_ENERGY = 16;
	private static final double CUSTOM_INITIAL_MINIUMIM_POTENTIAL_ENERGY = 1; // Defines low point of the potential energy curve.
	private static final double CUSTOM_INITIAL_PEAK_POTENTIAL_ENERGY = 22; // Defines peak of the potential energy curve.
	private static final double PRE_DECAY_ENERGY_WELL_BOTTOM = -37;
	
	// TODO: Decide which of these constants go here and which go elsewhere.
	private static final double MAX_TIME = 3.2e19;  // Trillion years
	private static final double HALF_LIFE_CALC_CONSTANT = Math.log(MAX_TIME)/(NUM_Y_AXIS_UNITS - Y_AXIS_ZERO_OFFSET);

    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 6f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = new Color( 246, 242, 175 );
    private static final double  AXES_LINE_WIDTH = 0.5f;
    private static final Color   AXES_LINE_COLOR = Color.BLACK;
    private static final double  ORIGIN_PROPORTION_X = 0.05d;
    private static final float   ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  ENERGY_LINE_STROKE = new BasicStroke( ENERGY_LINE_STROKE_WIDTH, BasicStroke.CAP_ROUND,
    		BasicStroke.JOIN_MITER, 1.0f );
    private static final Stroke  REFERENCE_LINE_STROKE = new BasicStroke( ENERGY_LINE_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
    		1.0f, new float[] {9}, 11);
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.RED;
    private static final Color   POTENTIAL_ENERGY_LINE_COLOR = Color.BLUE;
    private static final Color   LEGEND_BORDER_COLOR = Color.GRAY;
    private static final float   LEGEND_BORDER_STROKE_WIDTH = 2f;
    private static final Stroke  LEGEND_BORDER_STROKE = new BasicStroke( LEGEND_BORDER_STROKE_WIDTH );
    private static final Color   LEGEND_BACKGROUND_COLOR = BACKGROUND_COLOR;
    private static final double  LEGEND_WIDTH_PROPORTION = 0.27;
    private static final double  LEGEND_HEIGHT_PROPORTION = 0.5;
    private static final int     MAX_ALPHA_PARTICLES_DISPLAYED = 6;
    private static final double  ARROW_HEAD_HEIGHT = 10;
    private static final double  ARROW_HEAD_WIDTH = 8;
    private static final double  CONTROL_HANDLE_HEIGHT_PROPORTION = 0.13; // In proportion to overall graph height.
    private static final float   CONTROL_HANDLE_STROKE_WIDTH = 0.4f;
    private static final Stroke  CONTROL_HANDLE_STROKE = new BasicStroke( CONTROL_HANDLE_STROKE_WIDTH );
    private static final int     POTENTIAL_ENERGY_LINE_POINTINESS_FACTOR = 25;  // 0 = max pointiness, 100 least.
    
    // Constant that controls Y-axis position of the alpha particles after
    // the nucleus has decayed.  Value is arbitrary and chosen to look good.
    private static final double ALPHA_PARTICLE_POST_DECAY_ENERGY = -7.0;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the model that this chart monitors in order to present its
    // information.
    private SingleNucleusAlphaDecayModel _model;
    
    // Reference to the canvas on which everything is being displayed.
    private SingleNucleusAlphaDecayCanvas _canvas;
    
    // Reference to the alpha particles that are monitored & displayed.
    private AlphaParticle _tunneledAlpha;
    private ArrayList     _alphaParticles = new ArrayList();
    private ArrayList     _currentlyTrackedAlphas = new ArrayList(MAX_ALPHA_PARTICLES_DISPLAYED);
    
    // Width of the energy well in the chart in screen coordinates.
    private double _energyWellWidth;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PPath _potentialEnergyLine;
    private DoubleArrowNode _xAxisOfGraph;
    private ArrowNode _yAxisOfGraph;
    private PText _yAxisLabel;
    private PText _xAxisLabel;
    private GraphLegend _legend;
    private PImage _tunneledAlphaParticleImage;
    private PImage [] _alphaParticleImages = new PImage [MAX_ALPHA_PARTICLES_DISPLAYED];
    private ResizeArrowNode _totalEnergyHandle;
    private ResizeArrowNode _potentialEnergyPeakHandle;
    private PLine _potentialEnergyPeakRefLine;
    
    // Variables used for positioning nodes within the graph.
    private double _usableAreaOriginX;
    private double _usableAreaOriginY;
    private double _usableWidth;
    private double _usableHeight;
    private double _graphOriginX;
    private double _graphOriginY;
    
    // Tracks whether the alpha decay of the nucleus has occurred.
    private boolean _decayOccurred = false;
    
    // Values that define the positions of the energy lines/curves.
    private double _totalEnergy;
    private double _potentialEnergyPeak;
    private double _potentialEnergyMinimum;
    private double _energyWellBottom;
    
    // Variable the tracks whether interactivity is enabled.
    private boolean _interactivityEnabled = false;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructor for this chart, which creates all of the elements of the
     * chart.  Note that it does not lay them out - it counts on calls to
     * the updateBounds routine to do that.
     */
    public AlphaDecayEnergyChart(SingleNucleusAlphaDecayModel model, SingleNucleusAlphaDecayCanvas canvas) {
        
        _model = model;
        _canvas = canvas;
        
        // Do local initialization.
        _totalEnergy = POLONIUM_INITIAL_TOTAL_ENERGY;
        _potentialEnergyMinimum = POLONIUM_INITIAL_MINIUMIM_POTENTIAL_ENERGY;
        _potentialEnergyPeak = POLONIUM_INITIAL_PEAK_POTENTIAL_ENERGY;
        _energyWellBottom = PRE_DECAY_ENERGY_WELL_BOTTOM;
        
        // Register for significant events from the model.
        _model.addListener(new AlphaDecayAdapter(){
        	public void nucleusTypeChanged(){
        		if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
        			_interactivityEnabled = true;
        	        _totalEnergy = CUSTOM_INITIAL_TOTAL_ENERGY;
        	        _potentialEnergyMinimum = CUSTOM_INITIAL_MINIUMIM_POTENTIAL_ENERGY;
        	        _potentialEnergyPeak = CUSTOM_INITIAL_PEAK_POTENTIAL_ENERGY;
        	        updateCustomNucleusHalfLife();
        		}
        		else{
        			_interactivityEnabled = false;
        			_totalEnergy = POLONIUM_INITIAL_TOTAL_ENERGY;
        			_potentialEnergyMinimum = POLONIUM_INITIAL_MINIUMIM_POTENTIAL_ENERGY;
        			_potentialEnergyPeak = POLONIUM_INITIAL_PEAK_POTENTIAL_ENERGY;
        		}
        		update();
        	}
        	
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            }
            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            }
        });
        
        // If the model currently has the custom nucleus, we need to set the
        // half life to correspond to our initial energy configuration.
        if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
        	updateCustomNucleusHalfLife();
        }

        // Create the border for this chart.
        
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );
        
        // Initialize the arrow nodes that will comprise the axes of the
        // chart.  The initial sizes and positions are arbitrary, and the
        // real sizes and positions will be set when the bounds are updated.

        _xAxisOfGraph = new DoubleArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, AXES_LINE_WIDTH);
        _xAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _xAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        addChild( _xAxisOfGraph);
        
        _yAxisOfGraph = new ArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, AXES_LINE_WIDTH);
        _yAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _yAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        addChild( _yAxisOfGraph);
                
        // Initialize attributes of the line that shows the total energy level.
        
        _totalEnergyLine = new PLine();
        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLine.setStroke( ENERGY_LINE_STROKE );
        addChild( _totalEnergyLine);
        
        // Initialize attributes of the curve that shows the potential energy well.
        
        _potentialEnergyLine = new PPath(){
            // Override the rendering hints so that the segmented line can be
            // drawn smoothly.
            public void paint(PPaintContext paintContext){
                Graphics2D g2 = paintContext.getGraphics();
                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                super.paint( paintContext );
                g2.setRenderingHints( oldHints );
            }
        };
        _potentialEnergyLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyLine.setStroke( ENERGY_LINE_STROKE );
        addChild( _potentialEnergyLine);
        
        // Add the reference line that will allow the user to adjust the top
        // of the potential energy peak.
        _potentialEnergyPeakRefLine = new PLine();
        _potentialEnergyPeakRefLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyPeakRefLine.setStroke(REFERENCE_LINE_STROKE);
        addChild( _potentialEnergyPeakRefLine );
        
        // Add the handles that will allow the user to change the total
        // energy and potential energy peak.  These are initially arbitrarily
        // sized, then resized when the diagram is updated.
        _totalEnergyHandle = new ResizeArrowNode(10, Math.PI/2, Color.GREEN, Color.YELLOW);
        _totalEnergyHandle.setPickable(true);
        _totalEnergyHandle.setChildrenPickable(true);
        _totalEnergyHandle.setStroke(CONTROL_HANDLE_STROKE);
        addChild(_totalEnergyHandle);
        _totalEnergyHandle.addInputEventListener(new PBasicInputEventHandler(){
        	public void mousePressed(PInputEvent event) {
        		_model.setPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		_model.setPaused(false);
        		_model.resetNucleus();
        		_canvas.autoPressResetButton();
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double newEnergyValue = 
                	_totalEnergy + (d.height * _totalEnergyHandle.getScale() * NUM_Y_AXIS_UNITS / _usableHeight);
                if ((newEnergyValue >= _energyWellBottom * 0.67) && 
                	(convertEnergyToPixels(newEnergyValue) > (_usableAreaOriginY + BORDER_STROKE_WIDTH))){
                	_totalEnergy = newEnergyValue;
                	updateEnergyLines();
                	_model.setHalfLife(calculateHalfLife());
                }
            }
        });

        _potentialEnergyPeakHandle = new ResizeArrowNode(10, Math.PI/2, Color.GREEN, Color.YELLOW);
        _potentialEnergyPeakHandle.setPickable(true);
        _potentialEnergyPeakHandle.setChildrenPickable(true);
        _potentialEnergyPeakHandle.setStroke(CONTROL_HANDLE_STROKE);
        addChild(_potentialEnergyPeakHandle);
        _potentialEnergyPeakHandle.addInputEventListener(new PBasicInputEventHandler(){
        	public void mousePressed(PInputEvent event) {
        		_model.setPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		_model.setPaused(false);
        		_model.resetNucleus();
        		_canvas.autoPressResetButton();
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double newEnergyValue = _potentialEnergyPeak 
                	+ (d.height * _potentialEnergyPeakHandle.getScale() * NUM_Y_AXIS_UNITS / _usableHeight);
                if ((newEnergyValue >= POLONIUM_INITIAL_MINIUMIM_POTENTIAL_ENERGY) && 
                	(convertEnergyToPixels(newEnergyValue) > (_usableAreaOriginY + BORDER_STROKE_WIDTH))){
                	_potentialEnergyPeak = newEnergyValue;
                	updateEnergyLines();
                	calculateHalfLife();
                	_model.setHalfLife(calculateHalfLife());
                }
            }
        });
        
        // Add the text for the Y axis.
         _yAxisLabel = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_Y_AXIS_LABEL_2 );
         _yAxisLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
         _yAxisLabel.rotate( 1.5 * Math.PI );
         addChild( _yAxisLabel );
         
        // Add the text for the X axis.
        _xAxisLabel = new PText( NuclearPhysicsStrings.DECAY_ENERGY_PROFILE_X_AXIS_LABEL );
        _xAxisLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
        addChild( _xAxisLabel );

        // Create the legend (i.e. key) node for the chart.
        _legend = new GraphLegend();
        addChild(_legend);

        // Add the images that will depict alpha particles moving around
        // within the nucleus.
        for (int i = 0; i < MAX_ALPHA_PARTICLES_DISPLAYED; i++){
            PNode alphaParticleNode = new AlphaParticleNode();
            // Scale up the particle size by an arbitrary amount to make it look good.
            alphaParticleNode.scale( 7 );
            _alphaParticleImages[i] = new PImage(alphaParticleNode.toImage());
            _alphaParticleImages[i].setVisible( true );
            addChild( _alphaParticleImages[i] );
        }
        
        // Add the image that depicts the tunneling alpha particle.
        PNode alphaParticleNode = new AlphaParticleNode();
        // Scale up the particle size by an arbitrary amount to make it look good.
        alphaParticleNode.scale( 7 );
        _tunneledAlphaParticleImage = new PImage(alphaParticleNode.toImage());
        _tunneledAlphaParticleImage.setVisible( false );
        addChild( _tunneledAlphaParticleImage );
    }

    /**
     * Update the half life of the custom nucleus (i.e. a nucleus with an
     * adjustable half life) based on the current settings for the energy
     * levels.
     */
	private void updateCustomNucleusHalfLife() {
		if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
			_model.setHalfLife(calculateHalfLife());
        }
	}

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param rect - Position on the canvas where this chart should appear.
     */
    public void componentResized( Rectangle2D rect ) {
        updateBounds( rect );
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation canvas is changed.
     * 
     * @param rect - Rectangle where this chart should appear on the canvas.
     */
    private void updateBounds( Rectangle2D rect ) {

        // Recalculate the usable area and origin for the chart.
        
        _usableAreaOriginX = rect.getX() + BORDER_STROKE_WIDTH;
        _usableAreaOriginY = rect.getY() + BORDER_STROKE_WIDTH;
        _usableWidth       = rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = rect.getHeight() - ( BORDER_STROKE_WIDTH * 2);
        _graphOriginX      = _usableWidth * ORIGIN_PROPORTION_X + _usableAreaOriginX;
        _graphOriginY      = convertEnergyToPixels(0);
        
        // Recalculate energy well width.
        // Get the diameter of the atomic nucleus so that it can be
        // used to set the width of the energy well in the chart.
        double nucleusDiameter = _model.getAtomNucleus().getDiameter();
        PDimension nucleusDiameterDim = new PDimension(nucleusDiameter, nucleusDiameter);
        
        // Convert the diameter to screen coordinates so that we have
        // the right units for setting the width of the energy well in
        // the chart.
        _canvas.getPhetRootNode().worldToScreen( nucleusDiameterDim );
        _energyWellWidth = nucleusDiameterDim.getWidth();
        
        update();
    }
    
    /**
     * Redraw the graph.
     */
    private void update(){
        
        // Set up the border for the graph.
        
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                _usableAreaOriginX,
                _usableAreaOriginY,
                _usableWidth,
                _usableHeight,
                20,
                20 ) );
        
        // Position the axes for the graph.

        Point2D xAxisTailPt = new Point2D.Double( _usableAreaOriginX + BORDER_STROKE_WIDTH, _graphOriginY );
        Point2D xAxisTipPt = new Point2D.Double( _usableAreaOriginX + _usableWidth - BORDER_STROKE_WIDTH, _graphOriginY );
        _xAxisOfGraph.setTipAndTailLocations( xAxisTailPt, xAxisTipPt );
        Point2D yAxisTailPt = new Point2D.Double( _graphOriginX, _usableAreaOriginY + BORDER_STROKE_WIDTH );
        Point2D yAxisTipPt = new Point2D.Double( _graphOriginX, _usableAreaOriginY + _usableHeight - BORDER_STROKE_WIDTH );
        _yAxisOfGraph.setTipAndTailLocations( yAxisTailPt, yAxisTipPt );
        
        // Position the labels for the axes.
        
        _yAxisLabel.setOffset( _graphOriginX - (1.5 * _yAxisLabel.getFont().getSize()), 
                _graphOriginY + (0.5 * (yAxisTipPt.getY() - _graphOriginY + _yAxisLabel.getWidth())));

        _xAxisLabel.setOffset( _graphOriginX + 15, _graphOriginY + 3);

        
        // Draw the potential and total energy lines.
        updateEnergyLines();
        
        // Position the legend.
        double legendHeight = Math.min(LEGEND_HEIGHT_PROPORTION * _usableHeight,
        		_usableHeight - _graphOriginY - _xAxisLabel.getFullBounds().height - (4 * LEGEND_BORDER_STROKE_WIDTH));
        double legendWidth = LEGEND_WIDTH_PROPORTION * _usableWidth;
        _legend.updateSize(legendWidth, legendHeight);
        _legend.setOffset(_usableAreaOriginX + _usableWidth * 0.06, 
        		_usableAreaOriginY + _usableHeight - legendHeight - (4 * LEGEND_BORDER_STROKE_WIDTH));

        // Refresh the positions of the alpha particles.
        refreshAlphaImages();
    }

    /**
     * Convert an energy value to a position in pixels on the vertical axis of
     * the chart.  Note that the variables that define the boundaries and size
     * of the chart must be set for this to work.
     * 
     * @param energy
     * @return
     */
    private double convertEnergyToPixels(double energy){
    	double pixelsPerEnergyUnit = _usableHeight / NUM_Y_AXIS_UNITS; 
    	if (pixelsPerEnergyUnit > 0){
        	return _usableHeight - ((energy + Y_AXIS_ZERO_OFFSET) * pixelsPerEnergyUnit) + _usableAreaOriginY;
    	}
    	else{
    		return 0;
    	}
    }
    
    // TODO: I created this, then realized that I don't need it immediately,
    // but I hate to just dump it in case I end up needing it later.  So
    // keep it until I'm reasonably sure it isn't needed.
    /**
     * Convert a position on the chart (which might come from, say, a mouse
     * click) into a value that corresponds to the energy level for that
     * position.
     */
    private double convertPixelsToEnergy(double pixels){

    	if (_usableHeight == 0){
    		return 0;
    	}

    	double energyUnitsPerPixel = NUM_Y_AXIS_UNITS / _usableHeight;
    	
    	return (pixels - _usableHeight - _usableAreaOriginY) * energyUnitsPerPixel - Y_AXIS_ZERO_OFFSET; 
    }
    
    /**
     * Convert a number of pixels on the X-axis into the equivalent distance
     * value with respect to the parent canvas.
     * 
     * @param pixels - Number of pixels to be converted
     * @return A value indicating the equivalent distance in femtometers.
     */
    private double convertPixelsToDistance(double pixels){
    	PDimension dim = new PDimension(pixels, pixels);
        _canvas.getPhetRootNode().screenToWorld( dim );
        return dim.width;
    }
    
    /**
     * Draw or reposition the straight line that represents the total energy
     * and the curve that represents the potential energy.
     */
	private void updateEnergyLines() {
		
        double centerX = _usableAreaOriginX + (_usableWidth/2);

        // Draw/update the potential energy line.
		
		_potentialEnergyLine.reset();
        _potentialEnergyLine.moveTo( (float)_usableAreaOriginX + 3*BORDER_STROKE_WIDTH, 
                (float)(convertEnergyToPixels(_potentialEnergyMinimum)));
        _potentialEnergyLine.quadTo((float)(centerX - (_energyWellWidth / 2) - POTENTIAL_ENERGY_LINE_POINTINESS_FACTOR),
        		(float)convertEnergyToPixels(0),
        		(float)(centerX - (_energyWellWidth / 2)),
        		(float)(convertEnergyToPixels(_potentialEnergyPeak)));
        _potentialEnergyLine.lineTo( (float)(centerX - (_energyWellWidth / 2)), 
                (float)(convertEnergyToPixels(_energyWellBottom)));
        _potentialEnergyLine.lineTo( (float)(centerX + (_energyWellWidth / 2)), 
                (float)(convertEnergyToPixels(_energyWellBottom)));
        _potentialEnergyLine.lineTo( (float)(centerX + (_energyWellWidth / 2)), 
                (float)(convertEnergyToPixels(_potentialEnergyPeak)));
        _potentialEnergyLine.quadTo( (float)(centerX + (_energyWellWidth / 2)) + POTENTIAL_ENERGY_LINE_POINTINESS_FACTOR,
        		(float)convertEnergyToPixels(0),
        		(float)(_usableAreaOriginX + _usableWidth - 3*BORDER_STROKE_WIDTH), 
                (float)(convertEnergyToPixels(_potentialEnergyMinimum)));
        
        // Update the reference line that makes it clear to the user where the
        // peak of the energy well is.
        _potentialEnergyPeakRefLine.removeAllPoints();
        _potentialEnergyPeakRefLine.addPoint( 0, (float)(centerX + (_energyWellWidth / 2)), 
        		(float)convertEnergyToPixels(_potentialEnergyPeak) );
        _potentialEnergyPeakRefLine.addPoint( 1, (float)(_usableAreaOriginX + _usableWidth * 0.95), 
        		(float)convertEnergyToPixels(_potentialEnergyPeak) );
        
        // Position that handle that allows the user to control the potential
        // energy peak.
        double desiredHandleHeight = _usableHeight * CONTROL_HANDLE_HEIGHT_PROPORTION;
        _potentialEnergyPeakHandle.setScale(1);
        _potentialEnergyPeakHandle.setScale(desiredHandleHeight / _potentialEnergyPeakHandle.getFullBounds().height);
        
        _potentialEnergyPeakHandle.setOffset(_usableAreaOriginX + _usableWidth * 0.90, 
        		(float)convertEnergyToPixels(_potentialEnergyPeak));
        
        // Position the total energy line.
        
        _totalEnergyLine.removeAllPoints();
        double totalEnergyLineYPos = convertEnergyToPixels(_totalEnergy);
        _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3*BORDER_STROKE_WIDTH, totalEnergyLineYPos );
        _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3*BORDER_STROKE_WIDTH, totalEnergyLineYPos );
        
        // Scale and position the handle that the user can use to change
        // the total energy.
        _totalEnergyHandle.setScale(1);
        _totalEnergyHandle.setScale(desiredHandleHeight / _totalEnergyHandle.getFullBounds().height);
        _totalEnergyHandle.setOffset(_usableAreaOriginX + _usableWidth * 0.95, totalEnergyLineYPos);
        
        // Update the visibility state of the interactivity controls based on
        // whether or not interactivity is currently enabled.
        _totalEnergyHandle.setVisible(_interactivityEnabled);
        _potentialEnergyPeakHandle.setVisible(_interactivityEnabled);
        _potentialEnergyPeakRefLine.setVisible(_interactivityEnabled);
        
        // Update the tunneling region radius based on the energy line position.
        AtomicNucleus nucleus = _model.getAtomNucleus();
        if (nucleus != null){
            nucleus.setTunnelingRegionRadius(findEnergyLineIntersectionDistance());
        }
	}
    
    /**
     * Handle notification of a decay event from the nucleus.  If everything
     * is correct, register with the alpha particle that was generated as a
     * result of the decay event so we can portray it moving away from the
     * nucleus.
     * 
     * @param decayProducts
     */
    private void handleDecayEvent(ArrayList decayProducts){
        
        if (decayProducts != null){
            
            // First make sure that this decay event is what is expected.
            if ((decayProducts.size() == 1) && (decayProducts.get( 0 ) instanceof AlphaParticle)){
                
                // This is the expected event.  Track this particle and make
                // its representation visible.
                _tunneledAlpha = (AlphaParticle)decayProducts.get( 0 );
                _tunneledAlphaParticleImage.setVisible(true);
                setAlphaImageOffset(_tunneledAlphaParticleImage, _tunneledAlpha);
                _decayOccurred = true;
                
                if (_currentlyTrackedAlphas.contains(_tunneledAlpha)){
                	// The particle that tunneled was one that we were already
                	// watching, so find a new one to watch instead.
                    int index = _currentlyTrackedAlphas.indexOf( _tunneledAlpha );
                    AlphaParticle untrackedParticle = null;
                    for (int i = 0; i < _alphaParticles.size(); i++){
                    	untrackedParticle = (AlphaParticle) _alphaParticles.get(i);
                    	if (!_currentlyTrackedAlphas.contains(untrackedParticle)){
                    		// Track this particle instead of the tunneled one.
                    		_currentlyTrackedAlphas.set(index, untrackedParticle);
                    		break;
                    	}
                    }
                    setAlphaImageOffset(_alphaParticleImages[index], untrackedParticle);
                }
                
                // Update the bottom of the energy well.  The bottom of the
                // well must drop by the same amount as the alpha particles.
                _energyWellBottom = _energyWellBottom - (_totalEnergy - ALPHA_PARTICLE_POST_DECAY_ENERGY);
                
                // Redraw the graph to handle any changes.
                update();
            }
            else{
                System.err.println("Error: Unexpected decay event received.");
                assert false;
            }
        }
    }
    
    /**
     * Handle the addition of a new element to the model by determining the
     * type of element and adding the appropriate representation.
     * 
     * @param modelElement
     */
    private void handleModelElementAdded(Object modelElement){

    	if (modelElement instanceof CompositeAtomicNucleus){
    		// A nucleus has been added.
    		
        	// Register to listen for decay events.
            _model.getAtomNucleus().addListener( new AtomicNucleus.Adapter(){
                public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                        ArrayList byProducts ){
                    if (byProducts != null){
                        handleDecayEvent(byProducts);
                    }
                    else{
                        // Must have been a reset of the nucleus.
                        _decayOccurred = false;
                        _energyWellBottom = PRE_DECAY_ENERGY_WELL_BOTTOM;
                        _tunneledAlphaParticleImage.setVisible(false);
                        update();
                    }
                }
            });
            
            // Extract references to the alpha particles so that they can be
            // shown moving around on the graph.
            ArrayList nucleusConstituents = _model.getAtomNucleus().getConstituents();
            for (int i = 0; i < nucleusConstituents.size(); i++){
            	
                // Monitor the alpha particles of this nucleus.
                if (nucleusConstituents.get( i ) instanceof AlphaParticle){
                    // Add this to our overall list of watched particles.
                    _alphaParticles.add( nucleusConstituents.get( i ) );
                    
                    // If we don't have enough yet, add this to our list of
                    // particles that we are tracking and possibly displaying.
                    if (_currentlyTrackedAlphas.size() < MAX_ALPHA_PARTICLES_DISPLAYED){
                        _currentlyTrackedAlphas.add( nucleusConstituents.get( i ) );
                    }
                    
                    // Register as a listener to this particle.
                    ((AlphaParticle)nucleusConstituents.get( i )).addListener( this );
                }
            }
            
            // Make sure the tunneled alpha particle is not currently visible.
            _tunneledAlphaParticleImage.setVisible(false);
    	}
    	
    	// Reset the decay flag and anything else that needs is.
    	_decayOccurred = false;
        _totalEnergy = POLONIUM_INITIAL_TOTAL_ENERGY;
        _potentialEnergyMinimum = POLONIUM_INITIAL_MINIUMIM_POTENTIAL_ENERGY;
        _potentialEnergyPeak = POLONIUM_INITIAL_PEAK_POTENTIAL_ENERGY;
        _energyWellBottom = PRE_DECAY_ENERGY_WELL_BOTTOM;
        
        // If the graph is ready, update it with the new information.
        if (_usableHeight != 0 && _usableWidth != 0){
            update();
        }
    }

    private void handleModelElementRemoved(Object modelElement){

    	if (modelElement instanceof CompositeAtomicNucleus){
    		// The nucleus was removed.  Clear all references to any of its
    		// components.
    		_alphaParticles.clear();
    		_currentlyTrackedAlphas.clear();
    	}
    }

    /**
     * Handle a change in the position of an alpha particle, and update the
     * displayed graphics accordingly.
     */
    public void positionChanged(AlphaParticle alpha){
        
        if ((_tunneledAlpha != null) && (_tunneledAlpha == alpha)){
            
            // Convert the current position into a distance from the center of
            // the nucleus and then into screen coordinates.
            
            Point2D tunneledAlphaPosition = _tunneledAlpha.getPosition();
            double distanceFromNucleus = tunneledAlphaPosition.distance( 0, 0 );
            PDimension distanceDim = new PDimension(distanceFromNucleus, distanceFromNucleus);
            _canvas.getPhetRootNode().worldToScreen( distanceDim );
            double distance;
            if (tunneledAlphaPosition.getX() < 0){
                distance = -(distanceDim.getWidth());
            }
            else{
                distance = distanceDim.getWidth();                
            }
            
            // Figure out where to place the image of the particle.
            
            if (Math.abs( distance ) > _usableWidth/2){
                // This guy is off the chart, so forget about him.
                _tunneledAlphaParticleImage.setVisible( false );
                _tunneledAlpha = null;
            }
            else{
                setAlphaImageOffset( _tunneledAlphaParticleImage, _tunneledAlpha );
            }
        }
        else if ((alpha.getPosition().distance( 0, 0 ) > _model.getAtomNucleus().getDiameter()/2) &&
                 (alpha.getPosition().distance( 0, 0 ) < _model.getAtomNucleus().getTunnelingRegionRadius())){
            // This particle is in the region outside of the nucleus but not
            // fully tunneled away from it.  We want to display this, so we
            // swap it for one of the currently non-tunneled particles.
            if (!(_currentlyTrackedAlphas.contains( alpha ))){
                for (int i = 0; i < _currentlyTrackedAlphas.size(); i++){
                    AlphaParticle trackedAlpha = (AlphaParticle)_currentlyTrackedAlphas.get( i );
                    if (trackedAlpha.getPosition().distance( 0, 0 ) <= _model.getAtomNucleus().getDiameter()/2){
                        // We will drop this one from the tracked list and replace
                        // it with the new one.
                        _currentlyTrackedAlphas.set( i, alpha );
                        setAlphaImageOffset( _alphaParticleImages[i], alpha);
                        break;
                    }
                }
            }
            else{
                int index = _currentlyTrackedAlphas.indexOf( alpha );
                setAlphaImageOffset(_alphaParticleImages[index], alpha);
            }
        }
        else if (_currentlyTrackedAlphas.contains( alpha )){
            // This is a particle that we're tracking, so we should update its
            // position.
            int index = _currentlyTrackedAlphas.indexOf( alpha );
            setAlphaImageOffset(_alphaParticleImages[index], alpha);
        }
    }
    
    /**
     * Force the images representing alpha particles to be redrawn.  This is
     * generally done when something changes, such as when the screen has been
     * resized.
     */
    private void refreshAlphaImages(){
        for (int i = 0; i < _currentlyTrackedAlphas.size(); i++){
            positionChanged( (AlphaParticle)_currentlyTrackedAlphas.get( i ) );
        }
    }
    
    /**
     * Set the position on the chart of one of the alpha particle graphics
     * based on the setting of the given alpha particle.
     * 
     * @param index
     * @param alpha
     */
    private void setAlphaImageOffset(PImage image, AlphaParticle alpha){
        
        // Make sure the function is being used correctly.
        assert image != null;
        if (image == null){
            return;
        }
        
        // Calculate the Y axis position based on the particle's energy.
        double yPos;
        if ((!_decayOccurred) || (alpha == _tunneledAlpha)){
            yPos = convertEnergyToPixels( _totalEnergy );
        }
        else{
            yPos = convertEnergyToPixels( ALPHA_PARTICLE_POST_DECAY_ENERGY );
        }
        yPos -= image.getFullBounds().height / 2; // Center the image on the desired Y position.
        
        // Convert from model units
        Point2D alphaPosition = alpha.getPosition();
        double distanceFromNucleus = alphaPosition.distance( 0, 0 );
        PDimension distanceDim = new PDimension(distanceFromNucleus, distanceFromNucleus);
        _canvas.getPhetRootNode().worldToScreen( distanceDim );
        double xPos;
        if (alphaPosition.getX() < 0){
            xPos = _usableWidth / 2 - distanceDim.getWidth();
        }
        else{
            xPos = _usableWidth / 2 + distanceDim.getWidth();                
        }
        image.setOffset( xPos, yPos );
    }
    
    /**
     * Calculate the half life of the nucleus described by the current values
     * for total energy and peak potential energy.
     */
    private double calculateHalfLife(){
    	
    	double halfLife;
    	
    	if (_totalEnergy > _potentialEnergyPeak){
    		// This nucleus will decay instantly.
    		halfLife = 0;
    	}
    	else if (_totalEnergy < 0){
    		// This nucleus will never decay.
    		halfLife = Double.POSITIVE_INFINITY;
    	}
    	else {
    		/* The equation that was originally created for this, and the one
    		 * that arguably makes the most sense, is this one:
    		 * 
    		 * halfLife = Math.pow(10, (HALF_LIFE_CALC_CONSTANT * (_potentialEnergyPeak - _totalEnergy)));
    		 * 
    		 * But this was making the polonium graph too small, so a new one
    		 * was created as shown below.  I am keeping the original here as a
    		 * historical note and in case we ever need to go back to it.
    		 */
    		
    		// Tweaked calculation that doesn't have a real physical meaning,
    		// it just behaves the way we want it to.
    		double normalizedDelta = (_potentialEnergyPeak - _totalEnergy) / (NUM_Y_AXIS_UNITS - Y_AXIS_ZERO_OFFSET);
    		halfLife = Math.pow(10, 24 * normalizedDelta - 0.5);
    	}
    	
    	return halfLife;
    }
    
    //------------------------------------------------------------------------
    // Inner Classes
    //------------------------------------------------------------------------
    
    private class GraphLegend extends PNode{

    	private final double ENERGY_LINE_HORIZ_PROPORTION = 0.1;
    	private final double TOTAL_ENERGY_VERT_PROPORTION = 0.5;
    	private final double POTENTIAL_ENERGY_VERT_PROPORTION = 0.75;
    	private final double ENERGY_LINE_LENGTH_PROPORTION = 0.15;
    	private PPath _background;
    	private PText _title;
    	private PLine _potentialEnergyLine;
    	private PText _potentialEnergyLabel;
    	private PLine _totalEnergyLine;
    	private PText _totalEnergyLabel;
    	
		public GraphLegend() {
			
			// Create the background and main shape.
			_background = new PPath();
			_background.setStroke( LEGEND_BORDER_STROKE );
			_background.setStrokePaint( LEGEND_BORDER_COLOR );
			_background.setPaint( LEGEND_BACKGROUND_COLOR );
	        addChild( _background );
	        
	        // Add the title.
	        _title = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_LEGEND_TITLE );
	        _title.setFont( new PhetFont( Font.BOLD, 14 ) );
	        _background.addChild( _title );
	        
	        // Add other text and graphics to the legend.
	        _potentialEnergyLine = new PLine ();
	        _potentialEnergyLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
	        _potentialEnergyLine.setStroke( ENERGY_LINE_STROKE );
	        _background.addChild( _potentialEnergyLine );
	        
	        _potentialEnergyLabel = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_POTENTIAL_ENERGY );
	        _potentialEnergyLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
	        _background.addChild( _potentialEnergyLabel );
	        
	        _totalEnergyLine = new PLine ();
	        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
	        _totalEnergyLine.setStroke( ENERGY_LINE_STROKE );
	        _background.addChild( _totalEnergyLine );
	        
	        _totalEnergyLabel = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_TOTAL_ENERGY );
	        _totalEnergyLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
	        _background.addChild( _totalEnergyLabel );
		}
		
		/**
		 * Lay out the graph based on its current proportions.
		 */
		public void updateSize(double width, double height){
			
			_background.setPathTo(new Rectangle2D.Double(0, 0, width, height));
			
			_title.setOffset(width / 2 - _title.getFullBoundsReference().width / 2, 2 * LEGEND_BORDER_STROKE_WIDTH);
	        
	        double energyLineOriginX = ENERGY_LINE_HORIZ_PROPORTION * width;
	        double energyLineLength = ENERGY_LINE_LENGTH_PROPORTION * width;
			_totalEnergyLine.removeAllPoints();        
			_totalEnergyLine.addPoint( 0, energyLineOriginX, TOTAL_ENERGY_VERT_PROPORTION * height );
			_totalEnergyLine.addPoint( 1, energyLineOriginX + energyLineLength, TOTAL_ENERGY_VERT_PROPORTION * height );
	        
	        _totalEnergyLabel.setOffset(_totalEnergyLine.getFullBounds().getMaxX() + 10, 
	        		_totalEnergyLine.getFullBounds().getCenterY() - _totalEnergyLabel.getFullBounds().height/2);
	        
	        _potentialEnergyLine.removeAllPoints();        
	        _potentialEnergyLine.addPoint( 0, energyLineOriginX,
	        		POTENTIAL_ENERGY_VERT_PROPORTION * height );
	        _potentialEnergyLine.addPoint( 1, energyLineOriginX + energyLineLength, 
	        		POTENTIAL_ENERGY_VERT_PROPORTION * height );
	        
	        _potentialEnergyLabel.setOffset(_potentialEnergyLine.getFullBounds().getMaxX() + 10, 
	        		_potentialEnergyLine.getFullBounds().getCenterY() - _potentialEnergyLabel.getFullBounds().height/2);
		}
    }
    
    /**
     * Find the distance from the center of the chart where the total and
     * potential energy lines intersect.
     */
    private double findEnergyLineIntersectionDistance(){

    	double intersectionDistance = Double.POSITIVE_INFINITY;
    	
    	if (_totalEnergy <= 0){
    		intersectionDistance = Double.POSITIVE_INFINITY;
    	}
    	else if (_totalEnergy > _potentialEnergyPeak){
    		intersectionDistance = 0;
    	}
    	else{
    		double threshold = convertEnergyToPixels(_totalEnergy);
    		boolean intersectionFound = false;
        	PathIterator pathIterator = _potentialEnergyLine.getPathReference().getPathIterator(null, 0.001);
        	double[] coords = new double[2];
        	while(!pathIterator.isDone() && !intersectionFound) {
                int type = pathIterator.currentSegment(coords);
                switch(type) {
                    case PathIterator.SEG_LINETO:
                    	if (coords[1] <= threshold){
                    		// We have found the intersection point.
                    		intersectionDistance = convertPixelsToDistance(_usableAreaOriginX + 
                    				(_usableWidth/2) - coords[0]);
                    		intersectionFound = true;
                    	}
                        break;
                    case PathIterator.SEG_MOVETO:  // fall through
                    case PathIterator.SEG_CLOSE:
                        break;
                    default:
                        System.out.println("unexpected type: " + type);
                }
                pathIterator.next();
        	}
    	}
   	
    	return intersectionDistance;
    }
}