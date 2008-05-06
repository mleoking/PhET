/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.Uranium235Nucleus;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the nuclear reactor tab of this simulation.
 *
 * @author John Blanco
 */
public class NuclearReactorCanvas extends PhetPCanvas{
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Canvas dimensions.
    private final double CANVAS_WIDTH = 200;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * 0.87;

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 2.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private NuclearReactorModel _nuclearReactorModel;
    private HashMap _modelElementToNodeMap = new HashMap();
    private PNode _nucleusLayer;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public NuclearReactorCanvas(NuclearReactorModel nuclearReactorModel) {

        _nuclearReactorModel = nuclearReactorModel;
        
        // Register as a listener for notifications from the model about model
        // elements coming and going.
        _nuclearReactorModel.addListener( new NuclearReactorModel.Adapter(){
            public void modelElementAdded(Object modelElement){
                handleModelElementAdded(modelElement);
            }
            public void modelElementRemoved(Object modelElement){
                handleModelElementRemoved(modelElement);
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
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add a PNode that will act as a sort of 'layer' where the nuclei
        // will be added.
        _nucleusLayer = new PNode();
        addWorldChild( _nucleusLayer );
        
        // Add the initial nucleus or nuclei to the canvas.
        ArrayList nuclei = _nuclearReactorModel.getNuclei();
        for (int i = 0; i < nuclei.size(); i++){
            // IMPORTANT NOTE: This currently only handles adding U235 Nuclei,
            // since those are the only ones expected to be present when the
            // view is initially constructed.
            if (nuclei.get( i ) instanceof Uranium235Nucleus){
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
    public void handleModelElementAdded(Object modelElement){
        
        if ((modelElement instanceof AtomicNucleus)){
            // Add an atom node for this guy.
            PNode atomNode = new AtomicNucleusImageNode((AtomicNucleus)modelElement);
            _nucleusLayer.addChild( atomNode );
            _modelElementToNodeMap.put( modelElement, atomNode );
        }
        else if (modelElement instanceof Neutron){
            // Add a corresponding neutron node for this guy.
            PNode neutronNode = new NeutronNode((Neutron)modelElement);
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
    public void handleModelElementRemoved(Object modelElement){
        
        Object nucleusNode = _modelElementToNodeMap.get( modelElement );
        if ((nucleusNode != null) || (nucleusNode instanceof PNode)){
            
            if (modelElement instanceof AtomicNucleus){
                // Remove the nucleus node.
                _nucleusLayer.removeChild( (PNode )nucleusNode );
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
}
