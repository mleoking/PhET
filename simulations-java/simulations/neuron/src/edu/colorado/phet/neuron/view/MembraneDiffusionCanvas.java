/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.MembraneChannel;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.colorado.phet.neuron.model.Particle;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class MembraneDiffusionCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Initial size of the reference coordinates that are used when setting up
	// the canvas transform strategy.  These were empirically determined to
	// roughly match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private MembraneDiffusionModel model;
    
    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Layers for the canvas.
    private PNode myWorldNode;
    private PNode particleLayer;
    private PNode chamberLayer;
    private PNode membraneLayer;
    private PNode channelLayer;
    private PNode channelEdgeLayer;
    private PNode chartLayer;
    private PNode toolBoxLayer;
    private PNode injectorLayer;
    
    // Items on the canvas.
    private MembraneChannelToolBox membraneChannelToolBoxNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MembraneDiffusionCanvas(MembraneDiffusionModel model  ) {
    	
    	this.model = model;

    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(new PhetPCanvas.CenteringBoxStrategy(this, INITIAL_INTERMEDIATE_DIMENSION));
    	
    	// Set up the model-canvas transform.  The center of the chamber is at
    	// (0,0) in model space, so this can be adjusted to move the chamber
    	// to wherever it works best.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point((int)Math.round(INITIAL_INTERMEDIATE_COORD_WIDTH * 0.55 ), // Mult by 0.5 is center.
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.35 )), // Mult by 0.5 is center.
        		8,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        		true);

        setBackground( Color.BLACK );
        
        // Listen to the model for events that the canvas needs to know about.
        model.addListener(new MembraneDiffusionModel.Adapter(){
    		public void particleAdded(Particle particle) {
    			addParticleNode(particle);
    		}
    		public void channelAdded(MembraneChannel channel) {
    			addChannelNode(channel);
    		}
        });

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);

        // Create the layers in the desired order.
        chamberLayer = new PNode();
        membraneLayer = new PNode();
        particleLayer = new PNode();
        toolBoxLayer = new PNode();
        injectorLayer = new PNode();
        channelLayer = new PNode();
        channelEdgeLayer = new PNode();

        myWorldNode.addChild(chamberLayer);
        myWorldNode.addChild(membraneLayer);
        myWorldNode.addChild(toolBoxLayer);
        myWorldNode.addChild(channelLayer);
        myWorldNode.addChild(particleLayer);
        myWorldNode.addChild(channelEdgeLayer);
        myWorldNode.addChild(injectorLayer);

        chartLayer = new PNode();
        addWorldChild(chartLayer);
        
        // Add the node the will represent the chamber where the particles can
        // move around.
        Rectangle2D transformedParticleChamberRect = 
        	mvt.createTransformedShape(model.getParticleChamberRect()).getBounds2D();
        PNode particleChamberNode = new PhetPPath(transformedParticleChamberRect, new Color(199, 234, 252));
        chamberLayer.addChild(particleChamberNode);
        
        // Add the node that will represent the membrane that separates the
        // upper and lower portions of the chamber.
        Rectangle2D membraneRect = model.getMembraneRect();
    	Rectangle2D transformedMembraneRect = mvt.createTransformedShape(membraneRect).getBounds2D();
    	GradientPaint paint = new GradientPaint(0f, (float)transformedMembraneRect.getCenterY(), Color.YELLOW,
    			0f, (float)transformedMembraneRect.getBounds2D().getMaxY(), new Color(255, 100, 100), true);
    	PNode membraneNode = new PhetPPath(transformedMembraneRect, paint, new BasicStroke(1f), Color.BLACK);
    	// TODO: i18n
        PText membraneLabel = new PText("Membrane");
        membraneLabel.setFont(new PhetFont());
        membraneLabel.setScale(transformedMembraneRect.getHeight() * 0.7 / membraneLabel.getFullBoundsReference().height);
        membraneLabel.setOffset(transformedMembraneRect.getMinX() + 10,
        		transformedMembraneRect.getCenterY() - membraneLabel.getFullBoundsReference().height / 2);
        membraneNode.addChild(membraneLabel);
        membraneLayer.addChild(membraneNode);
        
        // Add the nodes that will be used to inject ions into the chamber.
        // There is an assumption here that the injection point is on the
        // right side of the node and in the vertical center of it.
        ParticleInjectorNode sodiumInjector = new ParticleInjectorNode(ParticleType.SODIUM_ION, model, mvt, 0);
        sodiumInjector.setOffset(
        		transformedParticleChamberRect.getMinX() - sodiumInjector.getFullBoundsReference().getMaxX() + 20, 
        		transformedParticleChamberRect.getMinY() + transformedParticleChamberRect.getHeight() * 0.2);
        injectorLayer.addChild(sodiumInjector);
        
        ParticleInjectorNode potassiumInjector = new ParticleInjectorNode(ParticleType.POTASSIUM_ION, model, mvt, 0);
        potassiumInjector.setOffset(
        		transformedParticleChamberRect.getMinX() - potassiumInjector.getFullBoundsReference().getMaxX() + 20, 
        		transformedParticleChamberRect.getMaxY() - transformedParticleChamberRect.getHeight() * 0.2);
        injectorLayer.addChild(potassiumInjector);
        
        // Add the tool box that will allow users to drag membrane channels on
        // to the membrane.  The multipliers in the following statement set
        // the size of the tool box and can be adjusted as needed.
        membraneChannelToolBoxNode = new MembraneChannelToolBox(
        		new PDimension(transformedParticleChamberRect.getWidth() * 1.4, transformedParticleChamberRect.getHeight() * 0.4), model, mvt, this);
        membraneChannelToolBoxNode.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH / 2 - membraneChannelToolBoxNode.getFullBoundsReference().width / 2,
        		transformedParticleChamberRect.getMaxY() + 40);
        toolBoxLayer.addChild(membraneChannelToolBoxNode);
        
        // Add the concentration graphs.
        ConcentrationGraph upperGraph = new ConcentrationGraph(model, true);
        upperGraph.setOffset(transformedParticleChamberRect.getMaxX() + 20,
        		transformedParticleChamberRect.getMinY() + 10);
        chartLayer.addChild(upperGraph);
        
        ConcentrationGraph lowerGraph = new ConcentrationGraph(model, false);
        lowerGraph.setOffset(transformedParticleChamberRect.getMaxX() + 20,
        		transformedMembraneRect.getMaxY() + 10);
        chartLayer.addChild(lowerGraph);

        // Update the layout.
        updateLayout();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        Dimension2D screenSize = getScreenSize();
        Rectangle bounds = getBounds();
        Dimension size = getSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else {
            // TODO
        }
    }
    
    private void addParticleNode(Particle particleToBeAdded){
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
    }
}
