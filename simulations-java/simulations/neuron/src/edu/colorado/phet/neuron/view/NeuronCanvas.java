/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannel;
import edu.colorado.phet.neuron.model.Particle;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class NeuronCanvas extends PhetPCanvas implements IZoomable {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Size of the chart the depicts the membrane potential.
    private static final Dimension2D POTENTIAL_CHART_SIZE = new PDimension(
    		NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.95,
    		NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.3);
    
    // Color of button for stimulating the neuron.
    private static final Color CANVAS_BUTTON_COLOR = new Color(255, 144, 0);
    
    // For debug: Enable and disable nodes that can help with debug of layout.
    private static final boolean SHOW_PARTICLE_BOUNDS = false;
    private static final boolean SHOW_CENTER_CROSS_HAIR = false;
    private static final boolean SHOW_CHANNEL_LOCATIONS = false;
    private static final boolean SHOW_CAPTURE_ZONES = false;
    private static final boolean SHOW_VIEWPORT_BOUNDS = true;

    // Max size of the charge symbols, tweak as needed.
    private static final double MAX_CHARGE_SYMBOL_SIZE = 11; 
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private AxonModel model;
    
    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Layers for the canvas.
    private PNode particleLayer;
    private PNode axonBodyLayer;
    private PNode axonCrossSectionLayer;
    private PNode channelLayer;
    private PNode channelEdgeLayer;
    private PNode chartLayer;
    private PNode chargeSymbolLayer;
    
    // Chart for showing membrane potential.
    private MembranePotentialChart membranePotentialChart;
    
    // Button for stimulating the neuron.
    DisableableGradientButtonNode stimulateNeuronButton2;
    
    // List of registered listeners for canvas events.
    private EventListenerList listeners = new EventListenerList();
    
    // Amount of zooming applied to the root world node.
    private double zoomFactor = 1;
    
    // For debug: Shows center of zoom.
    private CrossHairNode crossHairNode;
    private PNode myWorldNode=new PNode();
    
    //  For optimization of painting.  The counters are here for testing, and
    // can be printed out when needed.
    private int paintCallCounter = 0;
    private int repaintCallCounter = 0;
    private PBounds repaintBounds = new PBounds();

    private Rectangle2D viewportInIntermediateCoords = new Rectangle2D.Double();
	private PhetPPath viewportOutline;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NeuronCanvas( final AxonModel model ) {

    	this.model = model;

    	model.getClock().addClockListener(new ClockAdapter(){
    	    public void clockTicked( ClockEvent clockEvent ) {
    	    	NeuronCanvas.super.repaint(repaintBounds);
    	    	repaintBounds.reset();
    	    	repaintCallCounter = 0;
    	    	paintCallCounter = 0;
    	    }
    	});
    	
    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(new PhetPCanvas.CenteringBoxStrategy(this, NeuronDefaults.INTERMEDIATE_RENDERING_SIZE));
    	
    	// Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point((int)Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.5), 
        				(int)Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.5 )),
        		4,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        		true);

        // Register for events from the model.
        this.model.addListener(new AxonModel.Adapter() {
			public void channelAdded(MembraneChannel channel) {
				addChannelNode(channel);
			}
			public void particleAdded(Particle particle) {
				addParticle(particle);
			}
			public void potentialChartVisibilityChanged(){
				membranePotentialChart.setVisible(model.isPotentialChartVisible());
			}
			public void chargesShownChanged() {
				updateChargeSymbolsShown();
			}
			public void stimulationLockoutStateChanged() {
				updateStimButtonState();
			}
		});
        
        setBackground( NeuronConstants.CANVAS_BACKGROUND );

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);

        // Create the layers in the desired order.
        axonBodyLayer = new PNode();
        axonCrossSectionLayer = new PNode();
        particleLayer = new PNode();
        channelLayer = new PNode();
        channelEdgeLayer = new PNode();
        chargeSymbolLayer = new PNode();

        myWorldNode.addChild(axonBodyLayer);
        myWorldNode.addChild(axonCrossSectionLayer);
        myWorldNode.addChild(channelLayer);
        myWorldNode.addChild(particleLayer);
        myWorldNode.addChild(channelEdgeLayer);
        myWorldNode.addChild(chargeSymbolLayer);

        chartLayer = new PNode();
        addScreenChild(chartLayer);
        
        // Add the button for stimulating the neuron.
        stimulateNeuronButton2 = new DisableableGradientButtonNode(NeuronStrings.STIMULATE_BUTTON_CAPTION, 12,
        		CANVAS_BUTTON_COLOR);
        stimulateNeuronButton2.scale(2);
        stimulateNeuronButton2.setOffset(10, 10);
        addScreenChild(stimulateNeuronButton2);

        // Register to receive button pushes.
        stimulateNeuronButton2.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	model.initiateStimulusPulse();
            	updateStimButtonState();
            }
        });
        
        // Add the axon cross section.
        AxonBodyNode axonBodyNode = new AxonBodyNode(model.getAxonMembrane(), mvt);
        axonBodyLayer.addChild(axonBodyNode);
        AxonCrossSectionNode axonCrossSectionNode = new AxonCrossSectionNode(model.getAxonMembrane(), mvt);
        axonCrossSectionLayer.addChild(axonCrossSectionNode);
        
        // Add the particles.
        for (Particle particle : model.getParticles()){
        	addParticle(particle);
        }
        
        // Add the channels.
        for (MembraneChannel channel : model.getMembraneChannels()){
        	addChannelNode(channel);
        }
        
        // Add the charge symbols.
        addChargeSymbols();
        
        // Add the membrane potential chart.
        membranePotentialChart = new MembranePotentialChart(POTENTIAL_CHART_SIZE, 
        		NeuronStrings.MEMBRANE_POTENTIAL_CHART_TITLE, model);
        membranePotentialChart.setVisible(false);
        chartLayer.addChild(membranePotentialChart);
        
        // Add the zoom slider.
        ZoomControl zoomSlider = new ZoomControl(new PDimension(25, 130), this, 0.6, 7, 10);
        zoomSlider.setOffset(stimulateNeuronButton2.getXOffset(),
        		stimulateNeuronButton2.getFullBoundsReference().getMaxY() + 10);
        chartLayer.addChild(zoomSlider);
        
        // Add the depiction of the particle motion bounds, if enabled.
        if (SHOW_PARTICLE_BOUNDS){
        	PhetPPath particleMotionBounds = new PhetPPath(mvt.createTransformedShape(model.getParticleMotionBounds()),
        			new BasicStroke(3), Color.red);
        	particleLayer.addChild(particleMotionBounds);
        }
        
        if (SHOW_CENTER_CROSS_HAIR){
        	// Add the crosshair, used for debugging zoom.
        	crossHairNode = new CrossHairNode();
        	crossHairNode.setOffset(mvt.modelToViewDouble(new Point2D.Double(0, 0)));
        	chartLayer.addChild(crossHairNode);
        }

        // Add the viewport bounds, used for debugging.
        if (SHOW_VIEWPORT_BOUNDS){
        	viewportOutline = new PhetPPath(new BasicStroke(10f), Color.PINK);
        	addWorldChild(viewportOutline);
        }
        
        // Update the layout.
        updateLayout();
        
        // Update other initial state.
        updateStimButtonState();
        updateChargeSymbolsShown();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    
    
    /**
     * Reset any stateful behavior of the canvas, such as the zoom factor.
     * Note that this does NOT cause the removal of nodes that were added to
     * the canvas due to model events - we rely on the model being reset and
     * sending out the appropriate notifications for that part.
     */
    public void reset(){
    	setZoomFactor(1);
    }
    
    @Override
    public void repaint(PBounds bounds) {
    	repaintCallCounter++;
		// Intercept the repaint request and expand the bounds to handle it.
    	// The repaint will be performed at the next clock tick.  This is an
    	// optimization to try to make the sim consume less CPU time.
    	repaintBounds.add(bounds);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintCallCounter++;
	}

	/**
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        Dimension2D screenSize = getScreenSize();
        if ( worldSize.getWidth() > 0 || worldSize.getHeight() > 0 ) {
            double centerX = getScreenSize().getWidth() / 2;
            
            // Set the membrane potential chart such that it is centered in
            // the play area and just a bit up from the bottom.
            membranePotentialChart.setOffset(
            		centerX - membranePotentialChart.getFullBoundsReference().width / 2,
            		screenSize.getHeight() - membranePotentialChart.getFullBoundsReference().height - 5);
            
        	// Set up some bounds that represent the size of the screen in
        	// intermediate coordinates.
    		
    		AffineTransform transform = getWorldTransformStrategy().getTransform();
    		AffineTransform inverseTransform;
    		try {
    			inverseTransform = transform.createInverse();
    		} catch (NoninvertibleTransformException e) {
    			System.err.println(getClass().getName() + " - Error: Unable to invert transform.");
    			e.printStackTrace();
    			inverseTransform = new AffineTransform(); // Unity transform by default.
    		}
    		Rectangle2D tranformedBounds = inverseTransform.createTransformedShape(getBounds()).getBounds2D();
    		
    		if (SHOW_VIEWPORT_BOUNDS){
    			double margin = 10;
    			viewportOutline.setPathTo(
    					new Rectangle2D.Double(
    							tranformedBounds.getX() + margin,
    							tranformedBounds.getY() + margin, 
    							tranformedBounds.getWidth() - 2 * margin, 
    							tranformedBounds.getHeight() - 2 * margin));
    			viewportOutline.setOffset(0, 0);
    		}
    		
    		viewportInIntermediateCoords.setFrame(tranformedBounds);
        }
    }
    
    private void updateStimButtonState(){
    	stimulateNeuronButton2.setEnabled(!model.isStimulusInitiationLockedOut());
    }
    
    private void updateChargeSymbolsShown(){
    	chargeSymbolLayer.setVisible(model.isChargesShown());
    }
    
    /**
     * Add the change symbols to the canvas.  These are added by going through
     * the list of channels and placing two symbols - one intended to be out
     * of the membrane one one inside of it - between each pair of gates.
     */
    private void addChargeSymbols(){
    	
    	// Create a sorted list of the membrane channels in the model.
        ArrayList<MembraneChannel> membraneChannels = model.getMembraneChannels();
        sortMembraneChannelList(membraneChannels);
        
        // Go through the list and put charge symbols between each pair of channels.
        for (int i = 0; i < membraneChannels.size(); i++){
        	addChargeSymbolPair(membraneChannels.get(i), membraneChannels.get((i + 1) % membraneChannels.size()));
        }
    }
    
    private void addChargeSymbolPair(MembraneChannel channel1, MembraneChannel channel2){

    	PNode outerChargeSymbol;
    	PNode innerChargeSymbol;
        Point2D innerSymbolLocation = new Point2D.Double();
        Point2D outerSymbolLocation = new Point2D.Double();
        Point2D neuronCenterPoint = new Point2D.Double(0, 0);  // Assumes center of neuron at (0, 0).
        
        calcChargeSymbolLocations(
        		channel1.getCenterLocation(),
        		channel2.getCenterLocation(), 
        		neuronCenterPoint, 
        		outerSymbolLocation,
        		innerSymbolLocation);
        outerChargeSymbol = new ChargeSymbolNode(model, MAX_CHARGE_SYMBOL_SIZE, 0.1, false);
        outerChargeSymbol.setOffset(mvt.modelToViewDouble(innerSymbolLocation));
        chargeSymbolLayer.addChild(outerChargeSymbol);
        innerChargeSymbol = new ChargeSymbolNode(model, MAX_CHARGE_SYMBOL_SIZE, 0.1, true);
        innerChargeSymbol.setOffset(mvt.modelToViewDouble(outerSymbolLocation));
        chargeSymbolLayer.addChild(innerChargeSymbol);
    }
    
    /**
     * Calculate the locations of the charge symbols and set the two provided
     * points accordingly.
     * 
     * @param p1
     * @param p2
     * @param center
     * @param outerPoint
     * @param innerPoint
     */
    private void calcChargeSymbolLocations(Point2D p1, Point2D p2, Point2D neuronCenter, Point2D outerPoint, Point2D innerPoint){
    	// Find the center point between the given points.
    	Point2D center = new Point2D.Double((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
    	
    	// Convert to polar coordinates.
    	double radius = Math.sqrt(Math.pow(center.getX() - neuronCenter.getX(), 2) + Math.pow(center.getY() - neuronCenter.getY(), 2));
    	double angle = Math.atan2(center.getY() - neuronCenter.getY(), center.getX() - neuronCenter.getX());
    	
    	// Add some distance to the radius to make the charge outside the cell.
    	double outsideRadius = radius + 4; // Tweak as needed to position outer charge symbol.
    	
    	// Subtract some distance from the radius to make the charge inside the cell.
    	double insideRadius = radius - 3; // Tweak as needed to position outer charge symbol.
    	
    	// Convert to cartesian coordinates
    	outerPoint.setLocation(outsideRadius * Math.cos(angle), outsideRadius * Math.sin(angle));
    	innerPoint.setLocation(insideRadius * Math.cos(angle), insideRadius * Math.sin(angle));
    }
    
	public void addZoomListener(ZoomListener neuronCanvasZoomListener){
		listeners.add(ZoomListener.class, neuronCanvasZoomListener);
	}
	
	public void removeZoomListener(ZoomListener neuronCanvasZoomListener){
		listeners.remove(ZoomListener.class, neuronCanvasZoomListener);
	}
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.view.IZoomable#setZoomFactor(double)
	 */
    public void setZoomFactor(double zoomFactor){
    	if (this.zoomFactor != zoomFactor){
    		myWorldNode.setTransform(new AffineTransform());
    		if (zoomFactor > 2){
    			myWorldNode.scaleAboutPoint(zoomFactor, 
    					(int)(Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width / 2)),
    					(zoomFactor - 2) * model.getAxonMembrane().getCrossSectionDiameter() * 0.1);
    		}
    		else{
    			myWorldNode.scaleAboutPoint(zoomFactor, 
    					(int)(Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width / 2)), 0);
    		}
    		this.zoomFactor = zoomFactor;
    		notifyZoomChanged();
    	}
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.view.IZoomable#getZoomFactor()
	 */
    public double getZoomFactor(){
    	return zoomFactor;
    }
    
    private void addParticle(Particle particleToBeAdded){
    	final ParticleNode particleNode = new ParticleNode(particleToBeAdded, mvt); 
    	particleLayer.addChild(particleNode);
    	
    	// Set up a listener to remove the particle node when and if the
    	// particle is removed from the model.
    	particleToBeAdded.addListener(new Particle.Adapter(){
    		public void removedFromModel() {
    			particleLayer.removeChild(particleNode);
    		}
    	});
    }
    
    private void addChannelNode(MembraneChannel channelToBeAdded){
    	final MembraneChannelNode channelNode = new MembraneChannelNode(channelToBeAdded, mvt);
    	channelNode.addToCanvas(channelLayer, channelEdgeLayer);
    	channelToBeAdded.addListener(new MembraneChannel.Adapter() {
			public void removed() {
				channelNode.removeFromCanvas(channelLayer, channelEdgeLayer);
			}
		});
    	
    	/* The code below adds nodes that make it clear exactly where the
    	 * channel is for the various membrane channels.  This is useful for
    	 * debugging.
    	 */
    	if (SHOW_CHANNEL_LOCATIONS){
    		PhetPPath channelTestShape = new PhetPPath(mvt.createTransformedShape(channelToBeAdded.getChannelTestShape()), Color.ORANGE);
    		channelEdgeLayer.addChild(channelTestShape);
    	}
    	
    	// If enabled, show the capture zones.
    	if (SHOW_CAPTURE_ZONES){
    		channelLayer.addChild(new CaptureZoneNode(channelToBeAdded.getInteriorCaptureZone(), mvt, Color.RED));
    		channelLayer.addChild(new CaptureZoneNode(channelToBeAdded.getExteriorCaptureZone(), mvt, Color.GREEN));
    	}
    }
    
    /**
     * Sort the provided list of membrane channels such that they proceed in
     * clockwise order around the membrane.
     * 
     * @param membraneChannels
     */
    private void sortMembraneChannelList(ArrayList<MembraneChannel> membraneChannels){
    	boolean orderChanged = true;
    	while (orderChanged){
    		orderChanged = false;
    		for (int i = 0; i < membraneChannels.size() - 1; i++){
    			Point2D p1 = membraneChannels.get(i).getCenterLocation();
    			Point2D p2 = membraneChannels.get(i + 1).getCenterLocation();
    			double a1 = Math.atan2(p1.getY(), p1.getX()); 
    			double a2 = Math.atan2(p2.getY(), p2.getX());
    			if (a1 > a2){
    				// These two need to be swapped.
    				MembraneChannel tempChannel = membraneChannels.get(i);
    				membraneChannels.set(i, membraneChannels.get(i + 1));
    				membraneChannels.set(i + 1, tempChannel);
    				orderChanged = true;
    			}
    		}
    	}
    }
    
    private static class CrossHairNode extends PNode {

    	private static final double LINE_LENGTH = 10;
    	private static final Stroke CROSS_HAIR_STROKE = new BasicStroke(3);
    	private static final Color CROSS_HAIR_COLOR = Color.RED;
    	
		public CrossHairNode() {
			PhetPPath verticalLine = new PhetPPath(new Line2D.Double(0, -LINE_LENGTH, 0, LINE_LENGTH),
					CROSS_HAIR_STROKE, CROSS_HAIR_COLOR);
			addChild(verticalLine);
			PhetPPath horizontalLine = new PhetPPath(new Line2D.Double(-LINE_LENGTH, 0, LINE_LENGTH, 0),
					CROSS_HAIR_STROKE, CROSS_HAIR_COLOR);
			addChild(horizontalLine);
		}
    }
    
	private void notifyZoomChanged(){
		for (ZoomListener listener : listeners.getListeners(ZoomListener.class)){
			listener.zoomFactorChanged();
		}
	}
}
