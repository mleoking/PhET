/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.MultiNucleusDecayLinearTimeChart;
import edu.colorado.phet.nuclearphysics.view.NucleusProportionsChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private MultiNucleusDecayLinearTimeChart _decayTimeChart;
    private DecayRatesModel _model;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();
    private HashMap _mapNucleiToNodes = new HashMap();
    private AtomicNucleus.Listener _listenerAdapter;
    private NucleusProportionsChart _proportionsChart;
    private PNode _particleLayer;
    private PNode _graphLayer;
    
    //----------------------------------------------------------------------------
    // Constructor
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
        
        // Add the PNodes that will act as layers for the particles and graphs.
        _particleLayer = new PNode();
        addWorldChild(_particleLayer);
        _graphLayer = new PNode();
        addScreenChild(_graphLayer);
        
        // Add the diagram that will depict the relative concentration of
        // pre- and post-decay nuclei.
        // TODO: This is stubbed with a static picture for demo purposes.
        _proportionsChart = new NucleusProportionsChart( _model );
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
    }
    
    /**
     * Update the layout on the canvas.
     */
	public void update() {
		
		super.update();
		
		// Resize the world in the model so that it can spread the particles
		// out over the entire visible area.
		if (getWidth() > 0 && getHeight() > 0){
//			_model.setWorldSize( getWidth(), getHeight() * (1 - PROPORTION_CHART_FRACTION) );
			_model.setWorldSize( getWidth() * 0.8, (getHeight() * (1 - PROPORTION_CHART_FRACTION) ) * 0.8);
		}
	
		_proportionsChart.componentResized(new Rectangle2D.Double( 0, 0, getWidth(), getHeight() * PROPORTION_CHART_FRACTION ) );
		_proportionsChart.setOffset(0, getHeight() - _proportionsChart.getFullBoundsReference().height);
		
	}
	
	private void handleModelElementAdded(Object modelElement) {

    	if (modelElement instanceof AtomicNucleus){
    		// A new nucleus has been added to the model.  Create a
    		// node for it and add it to the nucleus-to-node map.
    		AtomicNucleusImageNode atomicNucleusNode = 
    			new AtomicNucleusImageNode( (AtomicNucleus)modelElement, AtomicNucleusImageType.GRADIENT_SPHERE );
    		
    		// Map this node and nucleus together.
    		// TODO: Do I need this map?
    		_mapNucleiToNodes.put(modelElement, atomicNucleusNode);
    		
    		// Set the position and add the node to the canvas.
    		atomicNucleusNode.setOffset( ((AtomicNucleus)modelElement).getPositionReference() );
    		_particleLayer.addChild( atomicNucleusNode );
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
    			
    			// Remove the node from the canvas.
    			removeWorldChild( nucleusNode );
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
