/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayControl;
import edu.colorado.phet.nuclearphysics.model.AbstractAlphaDecayNucleus;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.AutoPressGradientButtonNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.GrabbableNucleusImageNode;
import edu.colorado.phet.nuclearphysics.view.MultiNucleusAlphaDecayTimeChart;
import edu.colorado.phet.nuclearphysics.view.NucleusImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayCanvas extends PhetPCanvas {
    
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
    
    private MultiNucleusAlphaDecayTimeChart _decayTimeChart;
    private AutoPressGradientButtonNode _resetButtonNode;
    private GradientButtonNode _addTenButtonNode;
    private MultiNucleusAlphaDecayModel _model;
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
    
    public MultiNucleusAlphaDecayCanvas(MultiNucleusAlphaDecayModel multiNucleusAlphaDecayModel) {

    	_model = multiNucleusAlphaDecayModel;
    	
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
        
        // Register with the model for notifications of nuclei and alpha
        // particles coming and going.
        _model.addListener( new AlphaDecayAdapter(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
            
            public void nucleusTypeChanged(){
            	handleNucleusTypeChanged();
            }
        });
        
        // Add the nodes that will be used to control what is drawn over whom.
        _nucleiLayer = new PNode();
        addWorldChild(_nucleiLayer);
        _chartLayer = new PNode();
        addScreenChild(_chartLayer);
        
        // Add the button for resetting the nuclei to the canvas.
        _resetButtonNode = new AutoPressGradientButtonNode(NuclearPhysicsStrings.RESET_ALL_NUCLEI, 22, 
        		CANVAS_BUTTON_COLOR);
        _chartLayer.addChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	resetAllNuclei();
            }
        });

        // Add the chart that shows the decay time.
        _decayTimeChart = new MultiNucleusAlphaDecayTimeChart(_model, this);
        _chartLayer.addChild( _decayTimeChart );
        
        // Create and add the node the represents the bucket from which nuclei
        // can be extracted and added to the play area.
        _bucketRect = _model.getBucketRectRef();
        _bucketNode = new BucketOfNucleiNode( _bucketRect.getWidth(), _bucketRect.getHeight() );
        _nucleiLayer.addChild(_bucketNode);
        _bucketNode.setOffset( _bucketRect.getX(), _bucketRect.getY() );
        
        // Add the button that allows the user to add multiple nuclei at once.
        // Position it just under the bucket and scale it so that its size is
        // proportionate to the bucket.
        _addTenButtonNode = new GradientButtonNode(NuclearPhysicsStrings.ADD_TEN, 12, CANVAS_BUTTON_COLOR);
        double addTenButtonScale = (_bucketRect.getWidth() / _addTenButtonNode.getFullBoundsReference().width) * 0.4;
        _addTenButtonNode.scale(addTenButtonScale);
        _addTenButtonNode.setOffset(_bucketRect.getCenterX() - _addTenButtonNode.getFullBoundsReference().width / 2, 
        		_bucketRect.getMaxY());
        _nucleiLayer.addChild(_addTenButtonNode);

        // Register to receive button pushes.
        _addTenButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	addMultipleNucleiFromBucket( 10 );
            }
        });

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
		
		// Redraw the time chart.
        _decayTimeChart.componentResized( new Rectangle2D.Double( 0, 0, getWidth(),
                getHeight() * TIME_CHART_FRACTION));
        
        // Position the time chart.
        _decayTimeChart.setOffset( 0, 0 );
        
        // Position the reset button.
        _resetButtonNode.setOffset( (0.82 * getWidth()) - (_resetButtonNode.getFullBoundsReference().width / 2),
                0.30 * getHeight() );
        
        // Update the rectangle that defines the outer boundary where
        // randomly placed nuclei can be put.
        Dimension2D chartSize = new PDimension(_decayTimeChart.getFullBoundsReference().width,
        		_decayTimeChart.getFullBoundsReference().height);
        getPhetRootNode().screenToWorld(chartSize);
        
        Dimension2D worldSize = getWorldSize();
        double x = -worldSize.getWidth() * WIDTH_TRANSLATION_FACTOR + MIN_NUCLEUS_TO_OBSTACLE_DISTANCE;
        double width = worldSize.getWidth() - (MIN_NUCLEUS_TO_OBSTACLE_DISTANCE * 2);
        double y = -worldSize.getHeight() * HEIGHT_TRANSLATION_FACTOR + chartSize.getHeight()
      		+ MIN_NUCLEUS_TO_OBSTACLE_DISTANCE;
        double height = worldSize.getHeight() - chartSize.getHeight() - (MIN_NUCLEUS_TO_OBSTACLE_DISTANCE * 2);
        _nucleusPlacementAreaRect.setRect(x, y, width, height);
        
        // Update the rectangle that is used to prevent nuclei from being
        // placed where the reset button resides.
        Dimension2D resetButtonSize = new PDimension(_resetButtonNode.getFullBoundsReference().width,
        		_resetButtonNode.getFullBoundsReference().height);
        getPhetRootNode().screenToWorld(resetButtonSize);
        Point2D resetButtonLocation = _resetButtonNode.getOffset();
        getPhetRootNode().screenToWorld(resetButtonLocation);
        
        x = resetButtonLocation.getX() - MIN_NUCLEUS_TO_OBSTACLE_DISTANCE;
        width = resetButtonSize.getWidth() + (MIN_NUCLEUS_TO_OBSTACLE_DISTANCE * 2);
        y = resetButtonLocation.getY() - MIN_NUCLEUS_TO_OBSTACLE_DISTANCE;
        height = resetButtonSize.getHeight() + (MIN_NUCLEUS_TO_OBSTACLE_DISTANCE * 2);
        _paddedResetButtonRect.setRect(x, y, width, height);
        
        // Update the rectangle that is used to prevent nuclei from being
        // placed where the bucket resides.  NOTE: Since the bucket is a
        // world child, this could actually be done in the constructor and
        // never updated, but it is done here for consistency.
        x = _bucketRect.getX() - MIN_NUCLEUS_TO_OBSTACLE_DISTANCE;
        width = _bucketRect.getWidth() + (MIN_NUCLEUS_TO_OBSTACLE_DISTANCE * 2);
        y = _bucketRect.getY() - MIN_NUCLEUS_TO_OBSTACLE_DISTANCE;
        height = _bucketRect.getHeight() + (MIN_NUCLEUS_TO_OBSTACLE_DISTANCE * 3); // Add a little extra to account for
                                                                                   // the button below the bucket.
        _paddedBucketRect.setRect(x, y, width, height);
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
    			new GrabbableNucleusImageNode((AtomicNucleus)modelElement);
    		
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
        _decayTimeChart.reset();
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
    			((AbstractAlphaDecayNucleus)grabbedNode.getNucleusRef()).setPaused(true);
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
	    		AbstractAlphaDecayNucleus alphaDecayNucleus = (AbstractAlphaDecayNucleus)nucleus;
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
