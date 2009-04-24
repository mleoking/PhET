/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayControl;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.common.view.GrabbableNucleusImageNode;
import edu.colorado.phet.nuclearphysics.model.AbstractAlphaDecayNucleus;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AutoPressGradientButtonNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.NucleusImageFactory;
import edu.colorado.phet.nuclearphysics.view.RadiometricElementDecayTimeChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the radiometric
 * element model is displayed.
 *
 * @author John Blanco
 */
public class DecayRatesCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 200;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.45; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double TIME_CHART_FRACTION = 0.21;   // Fraction of canvas for time chart.
    
    // Base color for the buttons on the canvas.
    private final static Color CANVAS_BUTTON_COLOR = new Color(255, 100, 0);
    
    // Number of tries for finding open nucleus location.
    private final static int MAX_PLACEMENT_ATTEMPTS = 100;
    
    // Preferred distance between nucleus centers when placing them on the canvas.
    private static final double PREFERRED_INTER_NUCLEUS_DISTANCE = 15;  // In femtometers.
    
    // Minimum distance between the center of a nucleus and a wall or other obstacle.
    private static final double MIN_NUCLEUS_TO_OBSTACLE_DISTANCE = 10;  // In femtometers.
    
    // Scaling factor for nucleus nodes that are in the bucket.
    public static final double SCALING_FACTOR_FOR_NUCLEUS_NODES_IN_BUCKET = 0.6;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private RadiometricElementDecayTimeChart _decayTimeChart;
    private AutoPressGradientButtonNode _resetButtonNode;
    private GradientButtonNode _addTenButtonNode;
    private DecayRatesModel _model;
	private Rectangle2D _bucketRect;
	private BucketOfNucleiNode _bucketNode;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();
    private HashMap _mapNucleiToNodes = new HashMap();
    private GrabbableNucleusImageNode.Listener _grabbableNodeListener;
    private Random _rand = new Random();
    private AtomicNucleus.Listener _listenerAdapter;
    private PNode _nucleiLayer;
    private PNode _chartLayer;
    
    // The following rectangles are used to define the locations where
    // randomly placed nuclei can and cannot be put.
    private Rectangle2D _nucleusPlacementAreaRect = new Rectangle2D.Double();
    private Rectangle2D _paddedBucketRect = new Rectangle2D.Double();
    private Rectangle2D _paddedResetButtonRect = new Rectangle2D.Double();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DecayRatesCanvas( DecayRatesModel radiometricNucleusDecayModel ) {

    	_model = radiometricNucleusDecayModel;
    	
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                update();
            }
        } );
        
        // Create the listener that will be used to listen for user
        // interaction with the nucleus nodes.
        _grabbableNodeListener = new GrabbableNucleusImageNode.Listener(){
            public void nodeGrabbed(GrabbableNucleusImageNode node){
            	handleNodeGrabbed(node);
            };
            public void nodeReleased(GrabbableNucleusImageNode node){
            	handleNodeReleased(node);
            };
        };
        
        // Pre-generate the images for the custom nuclei and lead nuclei,
        // because otherwise there are noticeable pauses at various times
        // when the nuclei need to be generated.
        NucleusImageFactory.getInstance().preGenerateNucleusImages(AdjustableHalfLifeNucleus.ORIGINAL_NUM_PROTONS,
        		AdjustableHalfLifeNucleus.ORIGINAL_NUM_NEUTRONS, 25 );
        NucleusImageFactory.getInstance().preGenerateNucleusImages(Polonium211Nucleus.ORIGINAL_NUM_PROTONS - 2,
        		Polonium211Nucleus.ORIGINAL_NUM_NEUTRONS - 2, 25 );
    }
    
    /**
     * Update the layout on the canvas.
     */
	public void update() {
		
		super.update();
		
	}
	
	/**
	 * Auto-press the reset button, i.e. make it look like someone or some
	 * THING pressed the button.
	 */
	public void autoPressResetNucleiButton(){
		_resetButtonNode.autoPress();
	}

	private void handleModelElementAdded(Object modelElement) {

    	if (modelElement instanceof AtomicNucleus){
    		// A new nucleus has been added to the model.  Create a
    		// node for it and add it to the nucleus-to-node map.
    		GrabbableNucleusImageNode atomicNucleusNode = 
    			new GrabbableNucleusImageNode( (AtomicNucleus)modelElement, AtomicNucleusImageType.CIRCLE_WITH_HIGHLIGHT );
    		
    		// Map this node and nucleus together.
    		_mapNucleiToNodes.put(modelElement, atomicNucleusNode);
    		
    		// If the node's position indicates that it is in the bucket then
    		// add it to the bucket node.
    		if (isNucleusPosInBucketRectangle((AtomicNucleus)modelElement)){
    			// Scale the nucleus node down to make it appear smaller.  
    			// This was requested by the educators in order to try to make
    			// it clear to the users that the nuclei are somehow different
    			// when the are in the bucket, which is why they don't decay.
    			// It will be scaled back up when removed from the bucket.
    			atomicNucleusNode.scale(SCALING_FACTOR_FOR_NUCLEUS_NODES_IN_BUCKET);
    			
    			// Put it in the bucket.
    			_bucketNode.addNucleus(atomicNucleusNode);
    		}
    		
            // Register for notifications from this node.
            atomicNucleusNode.addListener(_grabbableNodeListener);
    	}
    	else if ( modelElement instanceof AlphaParticle ){
    		// An alpha particle has been added to the model, probably as a
    		// result of a decay event.  Add a node for it.
    		AlphaParticleModelNode alphaParticleNode = new AlphaParticleModelNode((AlphaParticle)modelElement);
    		_nucleiLayer.addChild(alphaParticleNode);
    		
    		// Map this alpha particle to its node.
    		_mapAlphaParticlesToNodes.put(modelElement, alphaParticleNode);
    	}
    	else {
    		System.err.println("WARNING: Unrecognized model element added, unable to create node for canvas.");
    	}
	}

	/**
	 * Handle a notification from the model that indicates that an element
	 * (e.g. a nucleus) was removed.  This generally means that the
	 * corresponding view elements should also go away.
	 * 
	 * @param modelElement
	 */
    private void handleModelElementRemoved(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		AtomicNucleusNode nucleusNode = (AtomicNucleusNode)_mapNucleiToNodes.get(modelElement);
    		if (nucleusNode == null){
    			System.err.println("Error: Could not find node for removed model element.");
    		}
    		else {
    			((AtomicNucleus)modelElement).removeListener(_listenerAdapter);
    			
    			if (_bucketNode.isNodeInBucket( nucleusNode )){
    				// Remove the node from the bucket.
        			_bucketNode.removeNucleus( nucleusNode );
    			}
        		else{
        			// Remove the node from the canvas.
        			removeWorldChild( nucleusNode );
        		}
    		}
    		_mapNucleiToNodes.remove( modelElement );
    	}
    	else if (modelElement instanceof AlphaParticle){
    		AlphaParticleModelNode alphaNode = (AlphaParticleModelNode)_mapAlphaParticlesToNodes.get(modelElement);
    		if (alphaNode == null){
    			System.err.println("Error: Could not find node for removed alpha particle.");
    		}
    		else{
    			removeWorldChild( alphaNode );
    		}
    		_mapAlphaParticlesToNodes.remove( modelElement );
    	}
	}
    
    private void handleNucleusTypeChanged(){
    	_bucketNode.setNucleusType(_model.getNucleusType());
    }
    
    /**
     * Reset all the nuclei back to their pre-decay state.
     */
    private void resetAllNuclei(){
        Set entries = _mapNucleiToNodes.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            AtomicNucleus nucleus = (AtomicNucleus)entry.getKey();
            nucleus.reset();
            if (!_bucketNode.isNodeInBucket((AtomicNucleusNode)_mapNucleiToNodes.get(nucleus))){
            	if (nucleus instanceof NuclearDecayControl){
            		((NuclearDecayControl) nucleus).activateDecay();
            	}
            }
        }
    }

	/**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
    }
    
    /**
     * Returns true if the position of the nucleus is within the bucket
     * rectangle and false if not.  This does NOT indicate whether or not the
     * node is currently a child of the bucket node.
     */
    private boolean isNucleusPosInBucketRectangle(AtomicNucleus nucleus){
    	
    	return _bucketRect.contains(nucleus.getPositionReference());
    }
    
    /**
     * Transfer a node from the bucket to the canvas.
     * 
     * @param node
     */
	private void transferNodeFromBucketToCanvas( AtomicNucleusNode node ) {

		// Add this nucleus node as a child.
		_nucleiLayer.addChild(node);

		// Set the node back to full size.
		node.setScale(1);
		
		// Adjust the node's position to account for the fact that it was
		// in the bucket.
		Point2D position = node.getNucleusRef().getPositionReference();
		node.getNucleusRef().setPosition(_bucketRect.getX() + position.getX(), 
				_bucketRect.getY() + position.getY());
	}
	
	/**
	 * Transfer a node that was out on the canvas back into the bucket.
	 * @param node
	 */
	private void transferNodeFromCanvasToBucket( AtomicNucleusNode node){
		node.getNucleusRef().reset();
		_bucketNode.addNucleusAnimated(node);
	}

    /**
     * Handle a notification that indicates that one of the nucleus nodes was
     * grabbed by the user.
     * 
     * @param grabbedNode
     */
    private void handleNodeGrabbed(GrabbableNucleusImageNode grabbedNode){
    	
    	if ( _bucketNode.isNodeInBucket( grabbedNode ) ){
    		// This node is currently in the bucket, so it should be removed
    		// and subsequently transferred to the canvas.
    		_bucketNode.removeNucleus(grabbedNode);
    		
    		transferNodeFromBucketToCanvas( grabbedNode );
    	}
    	else{
    		// Pause this nucleus while it is being manipulated.
    		if (grabbedNode.getNucleusRef() instanceof AbstractAlphaDecayNucleus){
    			((AbstractDecayNucleus)grabbedNode.getNucleusRef()).setPaused(true);
    		}
    	}
    }

    /**
     * Handle a notification that indicates that one of the nucleus nodes was
     * released by the user.
     * 
     * @param releasedNode
     */
    private void handleNodeReleased(GrabbableNucleusImageNode releasedNode){

    	// Check if this node is being released over the bucket or out on the
    	// open canvas.
    	if (isNucleusPosInBucketRectangle(releasedNode.getNucleusRef())){
    		transferNodeFromCanvasToBucket(releasedNode);
    	}
    	else{
	    	AtomicNucleus nucleus = releasedNode.getNucleusRef();
	    	if (nucleus instanceof AbstractAlphaDecayNucleus){
	    		AbstractDecayNucleus alphaDecayNucleus = (AbstractDecayNucleus)nucleus;
	    		if (alphaDecayNucleus.isPaused()){
	    			// Unpause this nucleus so that it continues towards decay.
	    			alphaDecayNucleus.setPaused(false);
	    		}
	    		else{
		    		// Cause this node to start moving towards fissioning.
		    		((NuclearDecayControl)nucleus).activateDecay();
	    		}
	    	}
    	}
    }
    
    /**
     * Find a location on the canvas where a nucleus can be placed that isn't
     * to close to an existing one.
     * @return
     */
    private Point2D findOpenSpotForNucleus(AtomicNucleusNode nucleusNode){

    	double xPos, yPos;
    	boolean openSpotFound = false;
    	Point2D openLocation = new Point2D.Double();
    	
    	for (int i = 0; i < 3 && !openSpotFound; i++){
    		
    		double minInterNucleusDistance = PREFERRED_INTER_NUCLEUS_DISTANCE;
    		
    		if (i == 1){
    			// Lower our standards.
    			minInterNucleusDistance = PREFERRED_INTER_NUCLEUS_DISTANCE / 2;
    		}
    		else if (i == 3){
    			// Anything goes - nuclei may end up on top of each other.
    			minInterNucleusDistance = 0;
        		System.err.println("WARNING: Allowing nucleus to overlap with others.");
    		}
    		
        	for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS & !openSpotFound; j++){
        		// Generate a candidate location.
            	xPos = _nucleusPlacementAreaRect.getX() + (_rand.nextDouble() * _nucleusPlacementAreaRect.getWidth());
            	yPos = _nucleusPlacementAreaRect.getY() + (_rand.nextDouble() * _nucleusPlacementAreaRect.getHeight());
            	openLocation.setLocation(xPos, yPos);
            	
            	// Innocent until proven guilty.
            	openSpotFound = true;
            	
            	if (_paddedResetButtonRect.contains(openLocation)){
            		openSpotFound = false;
            		continue;
            	}
            	if (_paddedBucketRect.contains(openLocation)){
            		openSpotFound = false;
            		continue;
            	}

            	// Check the position against all other nuclei and make sure that
            	// it is not too close.
            	AtomicNucleus thisNucleus = nucleusNode.getNucleusRef();
                Set entries = _mapNucleiToNodes.entrySet();
                Iterator iterator = entries.iterator();
                while (iterator.hasNext() && openSpotFound == true) {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    AtomicNucleus nucleus = (AtomicNucleus)entry.getKey();
                    if (!_bucketNode.isNodeInBucket((AtomicNucleusNode)_mapNucleiToNodes.get(nucleus))){
                        if ((thisNucleus != nucleus) &&
                        	(openLocation.distance(nucleus.getPositionReference()) < minInterNucleusDistance)){
                        	openSpotFound = false;
                        	break;
                        }
                    }
                }
        	}
    	}
    	
    	return openLocation;
    }
    
    /**
     * Extract the specified number of nuclei from the bucket and place them
     * on the canvas.  If there aren't enough in the bucket, add as many as
     * possible.
     * 
     * @param numNucleiToAdd - Number of nuclei to add.
     * @return - Number of nuclei actually added.
     */
    private int addMultipleNucleiFromBucket(int numNucleiToAdd){
    	
    	int numberOfNucleiObtained;
    	for (numberOfNucleiObtained = 0; numberOfNucleiObtained < numNucleiToAdd; numberOfNucleiObtained++){
    		AtomicNucleusNode nucleusNode = _bucketNode.extractNucleusFromBucket();
    		if (nucleusNode == null){
    			// The bucket must be empty, so there is nothing more to do.
    			break;
    		}
    		else{
    			transferNodeFromBucketToCanvas(nucleusNode);
    			
    			// Move the nucleus to a safe location.
    			nucleusNode.getNucleusRef().setPosition(findOpenSpotForNucleus(nucleusNode));
    			
    			// Activate the nucleus so that it will decay.
    			AtomicNucleus nucleus = nucleusNode.getNucleusRef();
    			if (nucleus instanceof NuclearDecayControl){
    				((NuclearDecayControl)nucleus).activateDecay();
    			}
    		}
    	}
    	
    	return numberOfNucleiObtained;
    }
}
