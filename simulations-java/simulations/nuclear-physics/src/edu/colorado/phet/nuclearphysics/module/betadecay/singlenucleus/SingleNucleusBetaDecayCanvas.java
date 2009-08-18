/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay.singlenucleus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.Antineutrino;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Electron;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.model.Proton;
import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;
import edu.colorado.phet.nuclearphysics.common.view.AbstractAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.common.view.LabeledExplodingAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.AntineutrinoNode;
import edu.colorado.phet.nuclearphysics.view.AutoPressGradientButtonNode;
import edu.colorado.phet.nuclearphysics.view.ElectronNode;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.NucleonModelNode;
import edu.colorado.phet.nuclearphysics.view.NucleonNode;
import edu.colorado.phet.nuclearphysics.view.ProtonModelNode;
import edu.colorado.phet.nuclearphysics.view.SingleNucleusDecayTimeChart;
import edu.colorado.phet.nuclearphysics.view.SubatomicParticleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Beta Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class SingleNucleusBetaDecayCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 30;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.60; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double TIME_CHART_FRACTION = 0.2;   // Fraction of canvas for time chart.
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private SingleNucleusBetaDecayModel _singleNucleusBetaDecayModel;
    private AbstractAtomicNucleusNode _nucleusNode;
    private SingleNucleusDecayTimeChart _betaDecayTimeChart;
    private AutoPressGradientButtonNode _resetButtonNode;
	private PNode _nucleusLayer;
	private PNode _labelLayer;
    private HashMap<SubatomicParticle, SubatomicParticleNode> _mapParticlesToNodes = 
    	new HashMap<SubatomicParticle, SubatomicParticleNode>();
    private HashMap _mapNucleiToNodes = new HashMap();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public SingleNucleusBetaDecayCanvas(SingleNucleusBetaDecayModel singleNucleusBetaDecayModel) {
        
        _singleNucleusBetaDecayModel = singleNucleusBetaDecayModel;
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Register for decay events from the model.
        _singleNucleusBetaDecayModel.addListener(new NuclearDecayListenerAdapter(){
			@Override
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            }
			@Override
            public void modelElementRemoved(Object modelElement){
            	if (modelElement instanceof CompositeAtomicNucleus){
                	removeNucleusNodes();
            	}
            	// TODO: JPB TBD - Need to handle removal of particles.
            }
			@Override
			public void nucleusTypeChanged() {
				// Update the time span of the chart.
				updateTimeSpanOfChart();
			}
        });
        
        // Create the layer where nodes that comprise the nucleus will be placed.
        _nucleusLayer = new PNode();
        _nucleusLayer.setPickable( false );
        _nucleusLayer.setChildrenPickable( false );
        _nucleusLayer.setVisible( true );
        addWorldChild(_nucleusLayer);

        // Create the layer where the nucleus label will be placed.
        _labelLayer = new PNode();
        addWorldChild(_labelLayer);
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add to the canvas the button for resetting the nucleus.
        _resetButtonNode = new AutoPressGradientButtonNode(NuclearPhysicsStrings.RESET_NUCLEUS, 22, 
        		NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR);
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _singleNucleusBetaDecayModel.resetNucleus();
            }
        });

        // Add the chart that shows the decay time.
        _betaDecayTimeChart = new SingleNucleusDecayTimeChart(_singleNucleusBetaDecayModel);
        updateTimeSpanOfChart();
        addScreenChild( _betaDecayTimeChart );
        
        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                
                // Redraw the time chart.
                _betaDecayTimeChart.componentResized( new Rectangle2D.Double( 0, 0, getWidth(),
                        getHeight() * TIME_CHART_FRACTION));
                
                // Position the time chart.
                _betaDecayTimeChart.setOffset( 0, 0 );
                
                // Position the reset button.
                _resetButtonNode.setOffset( (0.82 * getWidth()) - (_resetButtonNode.getFullBoundsReference().width / 2),
                        0.30 * getHeight() );
            }
        } );
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

	/**
	 * Auto-press the reset button, i.e. make it look like someone or some
	 * THING pressed the button.
	 */
	public void autoPressResetButton(){
		_resetButtonNode.autoPress();
	}

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

	private void updateTimeSpanOfChart(){
		_betaDecayTimeChart.setTimeSpan(HalfLifeInfo.getHalfLifeForNucleusType(_singleNucleusBetaDecayModel.getNucleusType()) * 3.2);
	}
	
    /**
     * Create the nodes needed to represent the nucleus or subatomic particles
     * that were added to the model.
     * 
     * @param modelElement
     */
	private void handleModelElementAdded(Object modelElement) {
		if (modelElement instanceof AtomicNucleus){
			// Get the nucleus from the model and then get the constituents
			// and create a visible node for each.
			CompositeAtomicNucleus atomicNucleus = _singleNucleusBetaDecayModel.getAtomNucleus();
			ArrayList nucleusConstituents = atomicNucleus.getConstituents();

			// Add a node for each particle that comprises the nucleus.
			for (int i = 0; i < nucleusConstituents.size(); i++){

				Object constituent = nucleusConstituents.get( i );
				
	            if (constituent instanceof Nucleon){
	                // Add a visible representation of the nucleon to the canvas.
	            	NucleonNode nucleonNode = new NucleonNode((Nucleon)constituent);
	            	nucleonNode.setVisible(true);
	            	_nucleusLayer.addChild(nucleonNode);
	            }
				else {
					// There is some unexpected object in the list of constituents
					// of the nucleus.  This should never happen and should be
					// debugged if it does.
					assert false;
				}
			}

			_nucleusNode = new LabeledExplodingAtomicNucleusNode(atomicNucleus);
			_labelLayer.addChild( _nucleusNode );
		}
    	else if (modelElement instanceof Electron){
    		// Add a new electron node to track this electron.
    		ElectronNode electronNode = new ElectronNode((Electron)modelElement);
    		_mapParticlesToNodes.put((SubatomicParticle)modelElement, electronNode);
    		_nucleusLayer.addChild(electronNode);
    	}
    	else if (modelElement instanceof Antineutrino){
    		// Add a new antineutrino node to track this antineutrino.
    		AntineutrinoNode antineutrinoNode = new AntineutrinoNode((Antineutrino)modelElement);
    		_mapParticlesToNodes.put((SubatomicParticle)modelElement, antineutrinoNode);
    		_nucleusLayer.addChild(antineutrinoNode);
    	}
    	else{
    		System.err.println(getClass().getName() + " - Warning: Unrecognized model element added, unable to create node for canvas.");
    	}
	}
	
    /**
     * Remove and dispose of the nodes that are currently representing the nucleus.
     */
	private void removeNucleusNodes(){

		// Clean up the nodes that comprise the nucleus.
		Collection nucleusLayerNodes = _nucleusLayer.getAllNodes();
		Iterator itr = nucleusLayerNodes.iterator();
		while( itr.hasNext() ){
			Object node = itr.next();
			if (node instanceof NucleonModelNode){
				((NucleonModelNode)node).cleanup();
			}
			else{
				// Should never get here, debug it if we do.
				assert false;
			}
		}
		
		// Clean up the nucleus node itself, which is just the label in this case.
		Collection labelLayerNodes = _labelLayer.getAllNodes();
		itr = labelLayerNodes.iterator();
		while( itr.hasNext() ){
			Object node = itr.next();
			if (node instanceof LabeledExplodingAtomicNucleusNode){
				((AbstractAtomicNucleusNode)node).cleanup();
			}
		}
		
		// TODO: JPB TBD - Not sure if this is sufficient or if it will cause memory leaks.
		_nucleusLayer.removeAllChildren();
		_labelLayer.removeAllChildren();
	}
}
