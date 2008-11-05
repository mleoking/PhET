/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.model.Proton;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayTimeChart;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.GrabbableNucleusImageNode;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.ProtonModelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AlphaDecayTimeChart _alphaDecayTimeChart;
    private GradientButtonNode _resetButtonNode;
    private MultiNucleusAlphaDecayModel _model;
	private Rectangle2D _bucketRect;
	private BucketOfNucleiNode _bucketNode;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();
    private HashMap _mapNucleiToNodes = new HashMap();
    private GrabbableNucleusImageNode.Listener _grabbableNodeListener;

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
            	// TODO: JPB TBD
            };
        });
        
        // Add the button for resetting the nuclei to the canvas.
        _resetButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_ALL_NUCLEI, 22, new Color(0xff9900));
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            }
        });

        // Add the chart that shows the decay time.
        // TODO: JPB TBD - Temp workaround in place to keep chart visible while new model is developed.
        // Clean up when model is ready.
        SingleNucleusAlphaDecayModel singleNucleusAlphaDecayModel = new SingleNucleusAlphaDecayModel(new NuclearPhysicsClock(24, 10));
        _alphaDecayTimeChart = new AlphaDecayTimeChart(singleNucleusAlphaDecayModel.getClock(), 
        		singleNucleusAlphaDecayModel.getAtomNucleus());
        addScreenChild( _alphaDecayTimeChart );
        
        _bucketRect = _model.getBucketRectRef();
        _bucketNode = new BucketOfNucleiNode( _bucketRect.getWidth(), _bucketRect.getHeight() );
        addWorldChild(_bucketNode);
        _bucketNode.setOffset( _bucketRect.getX(), _bucketRect.getY() );
        
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
    		GrabbableNucleusImageNode poloniumNucleusNode = new GrabbableNucleusImageNode((Polonium211Nucleus)modelElement);
    		
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
    		// and subsequently added as a child of this node.
    		_bucketNode.removeNucleus(grabbedNode);
    		
    		// Adjust the node's position to account for the fact that it was
    		// in the bucket.
    		Point2D position = grabbedNode.getNucleusRef().getPositionReference();
    		grabbedNode.getNucleusRef().setPosition(position.getX() + _bucketRect.getX(), 
    				position.getY() + _bucketRect.getY());
    		
    		// Add this nucleus node as a child.
    		addWorldChild(grabbedNode);
    	}
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
}
