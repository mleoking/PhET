/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.MultiNucleusDecayLinearTimeChart;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
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
    private AtomicNucleus.Listener _decayEventListener;
    private NuclearDecayProportionChart _proportionsChart;
    private PNode _particleLayer;
    private PNode _graphLayer;
    
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
        
        // Add the PNodes that will act as layers for the particles and graphs.
        _particleLayer = new PNode();
        addWorldChild(_particleLayer);
        _graphLayer = new PNode();
        addScreenChild(_graphLayer);
        
        // Add the diagram that will depict the relative concentration of
        // pre- and post-decay nuclei.
        _proportionsChart = new NuclearDecayProportionChart.Builder(Carbon14Nucleus.HALF_LIFE * 3.2, 
        		Carbon14Nucleus.HALF_LIFE, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
        		NuclearPhysicsConstants.CARBON_COLOR).
        		postDecayElementLabel(NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL).
        		postDecayLabelColor(NuclearPhysicsConstants.NITROGEN_COLOR).
        		pieChartEnabled(true).
        		showPostDecayCurve(true).
        		timeMarkerLabelEnabled(true).
        		build();
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
     * Update the layout on the canvas.
     */
	public void update() {
		
		super.update();
		
		_proportionsChart.componentResized(new Rectangle2D.Double( 0, 0, getWidth(), getHeight() * PROPORTION_CHART_FRACTION ) );
		_proportionsChart.setOffset(0, getHeight() - _proportionsChart.getFullBoundsReference().height * 1.02);
		
	}
	
	private void handleModelElementAdded(Object modelElement) {

    	if (modelElement instanceof AtomicNucleus){
    		// A new nucleus has been added to the model.  Create a
    		// node for it and add it to the nucleus-to-node map.
    		AtomicNucleusImageNode atomicNucleusNode = 
    			new AtomicNucleusImageNode( (AtomicNucleus)modelElement, AtomicNucleusImageType.GRADIENT_SPHERE );
    		
    		// Map this node and nucleus together.
    		_mapNucleiToNodes.put(modelElement, atomicNucleusNode);
    		
    		// Set the position and add the node to the canvas.
    		atomicNucleusNode.setOffset( ((AtomicNucleus)modelElement).getPositionReference() );
    		_particleLayer.addChild( atomicNucleusNode );
    		
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
    			
    			// Remove the node from the canvas.
    			PNode child = _particleLayer.removeChild( nucleusNode );
    			if (child == null){
        			System.err.println(this.getClass().getName() + ": Error - Could not remove nucleus from canvas.");
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
