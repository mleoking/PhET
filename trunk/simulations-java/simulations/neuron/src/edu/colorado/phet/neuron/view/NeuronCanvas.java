/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.event.EventListenerList;

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

    // List of registered listeners for canvas events.
    private EventListenerList listeners = new EventListenerList();
    
    // Amount of zooming applied to the root world node.
    private double zoomFactor = 1;
    
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
    
    // Chart for showing membrane potential.
    private MembranePotentialChart membranePotentialChart;
    
    // Button for stimulating the neuron.
    DisableableGradientButtonNode stimulateNeuronButton2;
    
    // For debug: Shows center of zoom.
    private CrossHairNode crossHairNode;
    private PNode myWorldNode=new PNode();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NeuronCanvas( final AxonModel model ) {

    	this.model = model;

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

        myWorldNode.addChild(axonBodyLayer);
        myWorldNode.addChild(axonCrossSectionLayer);
        myWorldNode.addChild(channelLayer);
        myWorldNode.addChild(particleLayer);
        myWorldNode.addChild(channelEdgeLayer);

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
        
        // Update the layout.
        updateLayout();
        
        // Update other initial state.
        updateStimButtonState();
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
    
    /**
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        Dimension2D screenSize = getScreenSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else {
            double centerX = getScreenSize().getWidth() / 2;
            
            // Set the membrane potential chart such that it is centered in
            // the play area and just a bit up from the bottom.
            membranePotentialChart.setOffset(
            		centerX - membranePotentialChart.getFullBoundsReference().width / 2,
            		screenSize.getHeight() - membranePotentialChart.getFullBoundsReference().height - 5);
        }
    }
    
    private void updateStimButtonState(){
    	stimulateNeuronButton2.setEnabled(!model.isStimulusInitiationLockedOut());
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
