/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayControl;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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
    private final double CANVAS_WIDTH = 900;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.29; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double PROPORTION_CHART_FRACTION = 0.45;   // Fraction of canvas for proportion chart.
    
    // Constants that control the appearance of the canvas.
    private static final Color BUCKET_AND_BUTTON_COLOR = new Color(90, 180, 225);
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DecayRatesModel _model;
    private HashMap _mapNucleiToNodes = new HashMap();
    private AtomicNucleus.Listener _decayEventListener;
    private NuclearDecayProportionChart _proportionsChart;
    private PNode _particleLayer;
    private PNode _graphLayer;
	private BucketOfNucleiNode _bucketNode;
	private GradientButtonNode _addMultipleNucleiButtonNode;
	
	private PPath _holdingAreaRect;
    
    //----------------------------------------------------------------------------
    // Builder + Constructor
    //----------------------------------------------------------------------------
    
    public DecayRatesCanvas( DecayRatesModel decayRatesModel ) {

    	_model = decayRatesModel;
    	
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
        
        // Add a rect that represents the holding area.
        // TODO: This is here for debugging placement issues and should be removed eventually.
        _holdingAreaRect = new PhetPPath(new Rectangle2D.Double(0,0,_model.getHoldingAreaRect().getWidth(),
        		_model.getHoldingAreaRect().getHeight()), Color.CYAN);
        _holdingAreaRect.setOffset(_model.getHoldingAreaRect().getX(), _model.getHoldingAreaRect().getY());
//        addWorldChild(_holdingAreaRect);

        // Add the PNodes that will act as layers for the particles and graphs.
        _particleLayer = new PNode();
        addWorldChild(_particleLayer);
        _graphLayer = new PNode();
        addScreenChild(_graphLayer);
        
        // Create and add the node the represents the bucket from which nuclei
        // can be extracted and added to the play area.
        Rectangle2D bucketRect = _model.getHoldingAreaRect();
        // Use part of the holding area for the bucket and part for the button.  TODO - JPB - if
        // we end up keeping the button instead of the slider, this should be cleaned up and
        // the button should probably become part of the bucket node itself.
        // Also, make the bucket a little smaller than the holding are so that we don't
        // end up with particles really close to it.
        bucketRect.setRect(bucketRect.getX() + 0.1 * bucketRect.getWidth(),
        		bucketRect.getY() + 0.1 * bucketRect.getHeight(), 
        		bucketRect.getWidth() * 0.8, bucketRect.getHeight() * 0.66);
        _bucketNode = new BucketOfNucleiNode( bucketRect.getWidth(), bucketRect.getHeight(), Math.PI/12, 
        		BUCKET_AND_BUTTON_COLOR );
        _particleLayer.addChild(_bucketNode);
        _bucketNode.setShowLabel(false);
        _bucketNode.setShowRadiationSymbol(false);
        _bucketNode.setSliderEnabled(true);
        _bucketNode.getSlider().addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				setProportionOfNucleiOutsideHoldingArea(_bucketNode.getSlider().getNormalizedReading());
			}
        });
        _bucketNode.setOffset( bucketRect.getX(), bucketRect.getY() );

        // Add the button that allows the user to add multiple nuclei at once.
        // Position it just under the bucket and scale it so that its size is
        // proportionate to the bucket.
        // TODO: Make the string a resource if this button is kept.
        _addMultipleNucleiButtonNode = new GradientButtonNode("Add 50", 12, BUCKET_AND_BUTTON_COLOR);
        double addButtonScale = (bucketRect.getWidth() / _addMultipleNucleiButtonNode.getFullBoundsReference().width) * 0.5;
        _addMultipleNucleiButtonNode.scale(addButtonScale);
        _addMultipleNucleiButtonNode.setOffset(bucketRect.getCenterX() - _addMultipleNucleiButtonNode.getFullBoundsReference().width / 2, 
        		bucketRect.getMaxY());
        _particleLayer.addChild(_addMultipleNucleiButtonNode);
        
        // Register to receive button pushes.
        _addMultipleNucleiButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	moveNucleiFromBucket( 50 );
            	_model.resetActiveAndDecayedNuclei();
            	_proportionsChart.clear();
            }
        });
        
        // Add the diagram that will depict the relative concentration of
        // pre- and post-decay nuclei.
        _proportionsChart = new NuclearDecayProportionChart(true);
        _proportionsChart.setShowPostDecayCurve(true);
        _proportionsChart.setTimeMarkerLabelEnabled(false);
        _proportionsChart.configureForNucleusType(_model.getNucleusType());
        _graphLayer.addChild(_proportionsChart);
        
        // Register with the model for notifications of nuclei coming and
        // going.
        _model.addListener( new NuclearDecayListenerAdapter(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
            
            public void nucleusTypeChanged(){
            	_proportionsChart.clear();
                _proportionsChart.configureForNucleusType(_model.getNucleusType());
            }
        });
        
        // Create a listener for decay events so the chart can be informed.
        _decayEventListener = new AtomicNucleus.Adapter(){
            public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){

            	if (atomicNucleus instanceof AbstractDecayNucleus){
            		AbstractDecayNucleus nucleus = (AbstractDecayNucleus)atomicNucleus;
            		if (nucleus.hasDecayed()){
            			// This was a decay event.  Inform the chart.
            			_proportionsChart.addDecayEvent(nucleus.getAdjustedActivatedTime(), 
            					_model.getPercentageDecayed());
            		}
            	}
            }
        };
        
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
        
        // Add a listener for when the clock gets reset.
        _model.getClock().addClockListener( new ClockAdapter() {
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // When the simulation time is reset, clear the chart.
            	_proportionsChart.clear();
            }
        });
    }

    /**
     * Highly specialized function that can be used to move nuclei into or out
     * of the holding area (a.k.a. the bucket) based on the ratio of nuclei
     * outside to total nuclei.
     */
    private void setProportionOfNucleiOutsideHoldingArea( double targetProportion ) {

    	// Set up some variables that will make calculations more straightforward.
    	int totalNumNuclei = _model.getNumNuclei();
    	int numNucleiInHoldingArea = _model.getNumNucleiInHoldingArea();
    	int numNucleiOutOfHoldingArea = totalNumNuclei - numNucleiInHoldingArea;

    	// Calculate the current proportion of nuclei outside the bucket.
    	double currentProportion = (double)numNucleiOutOfHoldingArea / (double)totalNumNuclei;
    	
    	if (currentProportion > targetProportion){
    		// We need to move some nuclei into the bucket.
    		int numNucleiToMove = numNucleiOutOfHoldingArea - (int)Math.round(targetProportion * totalNumNuclei);
    		moveNucleiToBucket(numNucleiToMove);
    		_model.resetActiveAndDecayedNuclei();
    	}
    	else if (currentProportion < targetProportion){
    		// We need to move some nuclei from the bucket into the main canvas area.
    		int numNucleiToMove = (int)Math.round(targetProportion * totalNumNuclei) - numNucleiOutOfHoldingArea;
    		moveNucleiFromBucket(numNucleiToMove);
    		_model.resetActiveAndDecayedNuclei();
    	}
	}

	/**
     * Update the layout on the canvas.
     */
	public void update() {
		
		super.update();
		
		_proportionsChart.componentResized(new Rectangle2D.Double( 0, 0, getWidth(), getHeight() * PROPORTION_CHART_FRACTION ) );
		_proportionsChart.setOffset(0, getHeight() - _proportionsChart.getFullBoundsReference().height * 1.02);
		
	}
	
    /**
     * Extract the specified number of nuclei from the bucket and place them
     * on the canvas.  If there aren't enough in the bucket, add as many as
     * possible.
     * 
     * @param numNucleiToMove - Number of nuclei to move.
     * @return - Number of nuclei actually moved.
     */
    private int moveNucleiFromBucket(int numNucleiToMove){
    	
    	int numberOfNucleiObtained;
    	for (numberOfNucleiObtained = 0; numberOfNucleiObtained < numNucleiToMove; numberOfNucleiObtained++){
    		AtomicNucleusNode nucleusNode = _bucketNode.extractAnyNucleusFromBucket();
    		if (nucleusNode == null){
    			// The bucket must be empty, so there is nothing more to do.
    			break;
    		}
    		else{
    			// Make the node a child of the appropriate layer on the canvas. 
    			_particleLayer.addChild(nucleusNode);
    			
    			// Move the nucleus to an open location outside of the holding
    			// area.
    			nucleusNode.getNucleusRef().setPosition(_model.findOpenNucleusLocation());
    			
    			// Activate the nucleus so that it will decay.
    			AtomicNucleus nucleus = nucleusNode.getNucleusRef();
    			if (nucleus instanceof NuclearDecayControl){
    				((NuclearDecayControl)nucleus).activateDecay();
    			}
    		}
    	}
    	
    	return numberOfNucleiObtained;
    }

    /**
     * Move the specified number of nuclei from the canvas to the bucket.  If
     * there aren't enough to meet the request, move as many as possible.
     * 
     * @param numNucleiToMove - Number of nuclei to move.
     * @return - Number of nuclei actually moved.
     */
    private int moveNucleiToBucket(int numNucleiToMove){
    	
    	int numberOfNucleiMoved;
    	for (numberOfNucleiMoved = 0; numberOfNucleiMoved < numNucleiToMove; numberOfNucleiMoved++){
    		
    		// Get an arbitrary nucleus that is not in the holding area.
    		AtomicNucleus nucleus = _model.getAnyNonHeldNucleus();
    		
    		if (nucleus == null){
    			// The play area must be empty, so there is nothing more to do.
    			break;
    		}
    		else{
    			// Move the nucleus to the holding area.
    			nucleus.reset();
    			_model.moveNucleusToHoldingArea(nucleus);
    			
    			// Find the node associated with this nucleus.
    			AtomicNucleusNode nucleusNode = (AtomicNucleusNode)_mapNucleiToNodes.get(nucleus);
    			
    			// Add this node to the bucket.
    			_bucketNode.addNucleus(nucleusNode);
    		}
    	}
    	
    	return numberOfNucleiMoved;
    }
    
	private void handleModelElementAdded(Object modelElement) {

    	if (modelElement instanceof AtomicNucleus){
    		// A new nucleus has been added to the model.  Create a
    		// node for it and add it to the nucleus-to-node map.
    		AtomicNucleus nucleus = (AtomicNucleus) modelElement;
    		AtomicNucleusImageNode atomicNucleusNode = 
    			new AtomicNucleusImageNode( nucleus, AtomicNucleusImageType.GRADIENT_SPHERE );
    		
    		// Map this node and nucleus together.
    		_mapNucleiToNodes.put(nucleus, atomicNucleusNode);
    		
    		if ( _model.isNucleusInHoldingArea(nucleus)){
    			// The nucleus is in the holding area in the model, so place
    			// it in the bucket in order to convey this to the user.
    			_bucketNode.addNucleus(atomicNucleusNode);
    		}
    		else{
    			// The nucleus is outside of the holding area, so just add it
    			// directly to the appropriate layer.
    			_particleLayer.addChild( atomicNucleusNode );
    		}
    		
    		// Listen to the nucleus for decay events.
    		((AtomicNucleus)modelElement).addListener(_decayEventListener);
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
    			System.err.println(this.getClass().getName() + ": Error - Could not find node for removed model element.");
    		}
    		else {
    			((AtomicNucleus)modelElement).removeListener(_decayEventListener);
    			
    			// Remove the node from the canvas or the bucket.
    			if  (_bucketNode.isNodeInBucket(nucleusNode)){
    				_bucketNode.removeNucleus(nucleusNode);
    			}
    			else {
    			    PNode child = _particleLayer.removeChild( nucleusNode );
    			    if (child == null){
    			    	System.err.println(this.getClass().getName() + ": Error - Could not remove nucleus from canvas.");
    			    }
    			}
    		}
    		_mapNucleiToNodes.remove( modelElement );
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
            AtomicNucleus nucleus = (AtomicNucleus)entry.getKey();
            nucleus.reset();
        }
    }
    
	/**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
    }
}
