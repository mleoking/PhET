/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.chainreaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.Uranium235CompositeNucleus;
import edu.colorado.phet.nuclearphysics.view.AtomicBombGraphicNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics.view.ContainmentVesselNode;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.NeutronSourceNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the nuclear chain reaction tab of this simulation.
 *
 * @author John Blanco
 */
public class ChainReactionCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Canvas dimensions.
    private final double CANVAS_WIDTH = 200;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * 0.87;

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 2.0;
    
    // Values for the state machine that tracks reaction state.
    private final int REACTION_STATE_NO_REACTION_PRODUCTS_PRESENT = 0;
    private final int REACTION_STATE_REACTION_OR_ADJUSTMENT_IN_PROGRESS = 1;
    private final int REACTION_STATE_REACTION_COMPLETE = 2;
    
    // Timer used for hiding & showing the reset button.
	private static final int BUTTON_DELAY_TIME = 1500; // In milliseconds.
    private static final Timer BUTTON_DELAY_TIMER = new Timer( BUTTON_DELAY_TIME, null );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ChainReactionModel _chainReactionModel;
    private HashMap _modelElementToNodeMap = new HashMap();
    private PNode _nucleusLayer;
    private NeutronSourceNode _neutronSourceNode;
    AtomicBombGraphicNode _atomicBombGraphicNode;
    private GradientButtonNode _resetNucleiButtonNode;
    private int _reactionState = REACTION_STATE_NO_REACTION_PRODUCTS_PRESENT;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ChainReactionCanvas(ChainReactionModel chainReactionModel) {

        _chainReactionModel = chainReactionModel;
        
        // Register as a listener for notifications from the model about model
        // elements coming and going.
        _chainReactionModel.addListener( new ChainReactionModel.Adapter(){
            public void modelElementAdded(Object modelElement){
                handleModelElementAdded(modelElement);
            }
            public void modelElementRemoved(Object modelElement){
                handleModelElementRemoved(modelElement);
            }
            public void reactiveNucleiNumberChanged(){
            	handleReactiveNucleiNumberChanged();
            }
            public void resetOccurred(){
            	if (BUTTON_DELAY_TIMER.isRunning()){
            		BUTTON_DELAY_TIMER.stop();
            	}
            	_resetNucleiButtonNode.setVisible(false);
            	_reactionState = REACTION_STATE_NO_REACTION_PRODUCTS_PRESENT;
            }
        });
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add a PNode that will act as a sort of 'layer' where the nuclei
        // will be added.
        _nucleusLayer = new PNode();
        addWorldChild( _nucleusLayer );
        
        // Add a node that will depict the containment vessel.
        addWorldChild(new ContainmentVesselNode(_chainReactionModel.getContainmentVessel(), this, 
                _chainReactionModel.getClock()));
        
        // Add the button that will allow the user to clear the nuclei from
        // the canvas.
        _resetNucleiButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_NUCLEI, 16,
        		NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR);
        _resetNucleiButtonNode.setScale(0.40);
        _resetNucleiButtonNode.setOffset(-100, -70); 
        addWorldChild(_resetNucleiButtonNode);
        _resetNucleiButtonNode.setVisible(false);  // Initially invisible, becomes visible when nucleus decays.
        
        // Register to receive button pushes.
        _resetNucleiButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _chainReactionModel.resetNuclei();
            }
        });
        
        // Set up the button delay timer that will make the reset button
        // appear some time after the decay has occurred.
		BUTTON_DELAY_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            	if(_reactionState == REACTION_STATE_REACTION_OR_ADJUSTMENT_IN_PROGRESS){
                	// Show the button.
                	_resetNucleiButtonNode.setVisible(true);
                	BUTTON_DELAY_TIMER.stop();
                	_reactionState = REACTION_STATE_REACTION_COMPLETE; 
            	}
            	else{
            		// This is unexpected and should be debugged.
            		System.err.println("Error: Unexpected timer expiration.");
            		BUTTON_DELAY_TIMER.stop();
            	}
            }
        } );
        
        // Add the neutron source to the canvas.
        _neutronSourceNode = new NeutronSourceNode(_chainReactionModel.getNeutronSource(), 50);
        addWorldChild( _neutronSourceNode );
        
        // Add the node that will portray the atomic bomb explosion to the canvas.
        _atomicBombGraphicNode = 
            new AtomicBombGraphicNode(_chainReactionModel.getContainmentVessel(), _chainReactionModel.getClock());
        updateAtomicBombGraphicLocation();
        addScreenChild(_atomicBombGraphicNode);
        
        // Listen for resizing.
        addComponentListener( new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                updateAtomicBombGraphicLocation();
            }
        });
        
        // Add the initial nucleus or nuclei to the canvas.
        ArrayList nuclei = _chainReactionModel.getNuclei();
        for (int i = 0; i < nuclei.size(); i++){
            // IMPORTANT NOTE: This currently only handles adding U235 Nuclei,
            // since one such nucleus at the center is the generally expected
            // reset state.  If that ever changes, this code should be extended
            // to handle whatever is needed.
            if (nuclei.get( i ) instanceof Uranium235CompositeNucleus){
                handleModelElementAdded(nuclei.get( i ));
            }
        }
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

    /**
     * Handle the addition of a new model element by adding a corresponding
     * node to the canvas (i.e. the view).
     */
    private void handleModelElementAdded(Object modelElement){
        
        if ((modelElement instanceof AtomicNucleus)){
            // Add an atom node for this guy.
            PNode atomNode = new AtomicNucleusImageNode((AtomicNucleus)modelElement);
            _nucleusLayer.addChild( atomNode );
            _modelElementToNodeMap.put( modelElement, atomNode );
        }
        else if (modelElement instanceof Neutron){
            // Add a corresponding neutron node for this guy.
            PNode neutronNode = new NeutronModelNode((Neutron)modelElement);
            _nucleusLayer.addChild( neutronNode );
            _modelElementToNodeMap.put( modelElement, neutronNode );            
        }
        else{
            System.err.println("Error: Unable to find appropriate node for model element.");
        }
    }
    
    /**
     * Remove the node or nodes that corresponds with the given model element
     * from the canvas.
     */
    private void handleModelElementRemoved(Object modelElement){
        
        Object nucleusNode = _modelElementToNodeMap.get( modelElement );
        if ((nucleusNode != null) || (nucleusNode instanceof PNode)){
            
            if (modelElement instanceof AtomicNucleus){
                // Remove the nucleus node.
                _nucleusLayer.removeChild( (PNode)nucleusNode );
                _modelElementToNodeMap.remove( modelElement );
            }
            else {
                // This is not a composite model element, so just remove the
                // corresponding PNode.
                _nucleusLayer.removeChild( (PNode )nucleusNode );
                _modelElementToNodeMap.remove( modelElement );
            }
        }
        else{
            System.err.println("Error: Problem encountered removing node from canvas.");
        }
    }
    
    private void updateAtomicBombGraphicLocation(){
        _atomicBombGraphicNode.setContainerSize(getWidth(), getHeight());
    }

    /**
     * Handle a notification that indicates that the number of reactive
     * nuclei has changed.  This may cause changes in the visibility of the
     * reset button, depending on the internal state.
     */
	private void handleReactiveNucleiNumberChanged() {
		
		switch (_reactionState){
		case REACTION_STATE_NO_REACTION_PRODUCTS_PRESENT:
			if (_chainReactionModel.getChangedNucleiExist()){
				// A new reaction has started.  Move the the appropriate
				// state and start the timer that will cause the reset button
				// to be shown.
				_reactionState = REACTION_STATE_REACTION_OR_ADJUSTMENT_IN_PROGRESS;
				BUTTON_DELAY_TIMER.restart();
			}
			else{
				// This means that the user is adjusting the number of nuclei
				// before any reaction has been run, and can be ignored.
			}
			break;
			
		case REACTION_STATE_REACTION_OR_ADJUSTMENT_IN_PROGRESS:
			if (_chainReactionModel.getChangedNucleiExist() == false){
				// This indicates that a reset occurred.  Stop the timer, make
				// sure the button is hidden, and go back to the initial
				// state.
				BUTTON_DELAY_TIMER.stop();
				_resetNucleiButtonNode.setVisible(false);
				_reactionState = REACTION_STATE_NO_REACTION_PRODUCTS_PRESENT;
			}
			else{
				// This is an indication of either a decay event in the chain
				// reaction or the addition of more nuclei by the user (via
				// the control panel).  In either case, restart the timer so
				// that the button doesn't appear mid way through the
				// reaction.
				BUTTON_DELAY_TIMER.restart();
			}
			break;
			
		case REACTION_STATE_REACTION_COMPLETE:
			if (_chainReactionModel.getChangedNucleiExist() == false){
				// This indicates that a reset occurred or that the user is
				// adding new nuclei and thus causing some of the old nuclei
				// to be removed.  Make sure the timer is stopped and that the
				// button is hidden, and return to the original state.
				BUTTON_DELAY_TIMER.stop();
				_resetNucleiButtonNode.setVisible(false);
				_reactionState = REACTION_STATE_NO_REACTION_PRODUCTS_PRESENT;
			}
//			else{
//				// This is an indication of either a decay event in the chain
//				// reaction or the addition of more nuclei by the user (via
//				// the control panel) while decay products are still around.
//				// In either case, hide the button, restart the timer, and
//				// change to the appropriate state.
//				BUTTON_DELAY_TIMER.restart();
//				_resetNucleiButtonNode.setVisible(false);
//				_reactionState = REACTION_STATE_REACTION_OR_ADJUSTMENT_IN_PROGRESS;
//			}
			
			break;
		}
	}
}
