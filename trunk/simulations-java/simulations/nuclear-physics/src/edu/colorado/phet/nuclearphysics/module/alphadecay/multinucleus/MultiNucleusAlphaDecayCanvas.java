/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
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
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayTimeChart;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.GrabbableNucleusImageNode;
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
    private final double TIME_CHART_FRACTION = 0.2;   // Fraction of canvas for time chart.
    
    // Base color for the buttons on the canvas.
    private final static Color CANVAS_BUTTON_COLOR = new Color(255, 100, 0);
    
    // Number of tries for finding open nucleus location.
    private final static int MAX_PLACEMENT_ATTEMPTS = 100;
    
    // Preferred distance between nuclei when placing them on the canvas.
    private static final double PREFERRED_INTER_NUCLEUS_DISTANCE = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AlphaDecayTimeChart _alphaDecayTimeChart;
    private GradientButtonNode _resetButtonNode;
    private GradientButtonNode _addTenButtonNode;
    private MultiNucleusAlphaDecayModel _model;
	private Rectangle2D _bucketRect;
	private BucketOfNucleiNode _bucketNode;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();
    private HashMap _mapNucleiToNodes = new HashMap();
    private GrabbableNucleusImageNode.Listener _grabbableNodeListener;
    private Random _rand = new Random();

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
        
        // Register with the model for notifications of nuclei coming and going.
        _model.addListener( new MultiNucleusAlphaDecayModel.Listener(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
        });
        
        // Add the button for resetting the nuclei to the canvas.
        _resetButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_ALL_NUCLEI, 22, CANVAS_BUTTON_COLOR);
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	resetAllNuclei();
            }
        });

        // Add the chart that shows the decay time.
        // TODO: JPB TBD - Temp workaround in place to keep chart visible while new model is developed.
        // Clean up when model is ready.
        SingleNucleusAlphaDecayModel singleNucleusAlphaDecayModel = new SingleNucleusAlphaDecayModel(new NuclearPhysicsClock(24, 10));
        _alphaDecayTimeChart = new AlphaDecayTimeChart(singleNucleusAlphaDecayModel.getClock(), 
        		singleNucleusAlphaDecayModel.getAtomNucleus());
        addScreenChild( _alphaDecayTimeChart );

        // Create and add the node the represents the bucket from which nuclei
        // can be extracted and added to the play area.
        _bucketRect = _model.getBucketRectRef();
        _bucketNode = new BucketOfNucleiNode( _bucketRect.getWidth(), _bucketRect.getHeight() );
        addWorldChild(_bucketNode);
        _bucketNode.setOffset( _bucketRect.getX(), _bucketRect.getY() );
        
        // Add the button that allows the user to add multiple nuclei at once.
        // Position it just under the bucket and scale it so that its size is
        // proportionate to the bucket.
        _addTenButtonNode = new GradientButtonNode(NuclearPhysicsStrings.ADD_TEN, 12, CANVAS_BUTTON_COLOR);
        double addTenButtonScale = (_bucketRect.getWidth() / _addTenButtonNode.getFullBoundsReference().width) * 0.4;
        _addTenButtonNode.scale(addTenButtonScale);
        _addTenButtonNode.setOffset(_bucketRect.getCenterX() - _addTenButtonNode.getFullBoundsReference().width / 2, 
        		_bucketRect.getMaxY());
        addWorldChild(_addTenButtonNode);

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
                
                // Redraw the time chart.
                _alphaDecayTimeChart.componentResized( new Rectangle2D.Double( 0, 0, getWidth(),
                        getHeight() * TIME_CHART_FRACTION));
                
                // Position the time chart.
                _alphaDecayTimeChart.setOffset( 0, 0 );
                
                // Position the reset button.
                _resetButtonNode.setOffset( (0.82 * getWidth()) - (_resetButtonNode.getFullBoundsReference().width / 2),
                        0.30 * getHeight() );
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
    }
    
	private void handleModelElementAdded(Object modelElement) {

    	if (modelElement instanceof Polonium211Nucleus){
    		// A new polonium nucleus has been added to the model.  Create a
    		// node for it and add it to the nucleus-to-node map.
    		GrabbableNucleusImageNode poloniumNucleusNode = 
    			new GrabbableNucleusImageNode((Polonium211Nucleus)modelElement);
    		
    		// Map this node and nucleus together.
    		_mapNucleiToNodes.put(modelElement, poloniumNucleusNode);
    		
    		// If the node's position indicates that it is in the bucket then
    		// add it to the bucket node.
    		if (isNucleusPosInBucketRectangle((AtomicNucleus)modelElement)){
    			_bucketNode.addNucleus(poloniumNucleusNode);
    		}
    		
            // Map the nucleus to the node so that we can find it it later.
            _mapAlphaParticlesToNodes.put( modelElement, poloniumNucleusNode );
            
            // Register for notifications from this node.
            poloniumNucleusNode.addListener(_grabbableNodeListener);
    	}
    	else if ( modelElement instanceof AlphaParticle ){
    		// An alpha particle has been added to the model, probably as a
    		// result of a decay event.  Add a node for it.
    		AlphaParticleModelNode alphaParticleNode = new AlphaParticleModelNode((AlphaParticle)modelElement);
    		addWorldChild(alphaParticleNode);
    		
    		// Map this alpha particle to its node.
    		_mapAlphaParticlesToNodes.put(modelElement, alphaParticleNode);
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
    	
    	if (modelElement instanceof Polonium211Nucleus){
    		AtomicNucleusNode nucleusNode = (AtomicNucleusNode)_mapNucleiToNodes.get(modelElement);
    		if (nucleusNode == null){
    			System.err.println("Error: Could not find node for removed model element.");
    		}
    		else if (_bucketNode.isNodeInBucket( nucleusNode )){
    			_bucketNode.removeNucleus( nucleusNode );
    		}
    		else{
    			removeWorldChild( nucleusNode );
    		}
    		_mapNucleiToNodes.remove( modelElement );
    	}
    	else if (modelElement instanceof AlphaParticleModelNode){
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
    
    /**
     * Reset all the nuclei back to their pre-decay state.
     */
    private void resetAllNuclei(){
        Set entries = _mapNucleiToNodes.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Polonium211Nucleus nucleus = (Polonium211Nucleus)entry.getKey();
            nucleus.reset();
            if (!_bucketNode.isNodeInBucket((AtomicNucleusNode)_mapNucleiToNodes.get(nucleus))){
                nucleus.activate();
            }
        }
    }

	/**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
        _alphaDecayTimeChart.reset();
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
    }

	private void transferNodeFromBucketToCanvas( AtomicNucleusNode node ) {
		// Adjust the node's position to account for the fact that it was
		// in the bucket.
		Point2D position = node.getNucleusRef().getPositionReference();
		node.getNucleusRef().setPosition(position.getX() + _bucketRect.getX(), 
				position.getY() + _bucketRect.getY());
		
		// Add this nucleus node as a child.
		addWorldChild(node);
	}

    /**
     * Handle a notification that indicates that one of the nucleus nodes was
     * released by the user.
     * 
     * @param releasedNode
     */
    private void handleNodeReleased(GrabbableNucleusImageNode releasedNode){

    	// JPB TBD - Need to handle case where nucleus is added back to bucket.
    	AtomicNucleus nucleus = releasedNode.getNucleusRef();
    	if (nucleus instanceof Polonium211Nucleus){
    		// Cause this node to start moving towards fissioning.
    		((Polonium211Nucleus)nucleus).activate();
    	}
    }
    
    /**
     * Find a location on the canvas where a nucleus can be placed that isn't
     * to close to an existing one.
     * @return
     */
    private Point2D findOpenSpotForNucleus(){

    	// TODO: JPB TBD - Need to see if this is actually open instead of just generating a random spot.
    	double height = CANVAS_HEIGHT - (TIME_CHART_FRACTION * CANVAS_HEIGHT);
    	double width = CANVAS_WIDTH * 0.8;
    	double xPos = (_rand.nextDouble() * width) - (width * WIDTH_TRANSLATION_FACTOR);
    	double yPos = (_rand.nextDouble() * height) - (CANVAS_HEIGHT / 2 - (TIME_CHART_FRACTION * CANVAS_HEIGHT));
    	return new Point2D.Double(xPos, yPos);
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
    			nucleusNode.getNucleusRef().setPosition(findOpenSpotForNucleus());
    			
    			// Activate the nucleus so that it will decay.
    			AtomicNucleus nucleus = nucleusNode.getNucleusRef();
    			if (nucleus instanceof Polonium211Nucleus){
    				((Polonium211Nucleus)nucleus).activate();
    			}
    		}
    	}
    	
    	return numberOfNucleiObtained;
    }
}
