/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleusConstituent;
import edu.colorado.phet.nuclearphysics2.model.FissionOneNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.util.GraphicButtonNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;
import edu.colorado.phet.nuclearphysics2.view.NeutronSourceNode;
import edu.colorado.phet.nuclearphysics2.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the nuclear chain reaction tab of this simulation.
 *
 * @author John Blanco
 */
public class ChainReactionCanvas extends PhetPCanvas implements ChainReactionModel.Listener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 200;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 2.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ChainReactionModel _chainReactionModel;
    private GraphicButtonNode _containmentVesselButtonNode;
    private HashMap _modelElementToNodeMap = new HashMap();
    private PNode _nucleusLayer;
    private NeutronSourceNode _neutronSourceNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ChainReactionCanvas(ChainReactionModel chainReactionModel) {

        _chainReactionModel = chainReactionModel;
        _chainReactionModel.addListener( this );
        
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
        
        // Add the button for enabling the containment vessel to the canvas.
        // TODO: JPB TBD - Need to make this a string and possibly a two-lined button.
        _containmentVesselButtonNode = new GraphicButtonNode("Mesh Gradient Button Unpushed.png", 
                "Mesh Gradient Button Pushed.png",
                "Containment Vessel", 0.8, 0.6);
        addScreenChild(_containmentVesselButtonNode);
        
        // Register to receive button pushes.
        _containmentVesselButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // TODO: JPB TBD.
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
                
                // Position the containment vessel button.
                _containmentVesselButtonNode.setOffset( 0.75 * getWidth(), 0.15 * getHeight() );
            }
        });
        
        // Add the neutron source to the canvas.
        _neutronSourceNode = new NeutronSourceNode(_chainReactionModel.getNeutronSource(), 50);
        addWorldChild( _neutronSourceNode );
        
        // Register as a listener with the neutron source so that we will know
        // when new neutrons have been produced.
        // TODO: JPB TBD - I think that this may be redundant, since
        // I added the ability to get notification for every new particle
        // directly from the model.  If so, remove this, and I should probably
        // refactor the fission tab to behave similarly.
        _chainReactionModel.getNeutronSource().addListener( new NeutronSource.Listener (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the canvas.
                NeutronNode neutronNode = new NeutronNode(neutron);
                addWorldChild( neutronNode );
                _modelElementToNodeMap.put( neutron, neutronNode );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

    /**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
        // TODO: JPB TBD.
    }
    
    /**
     * Handle the addition of a new model element by adding a corresponding
     * node to the canvas (i.e. the view).
     */
    public void modelElementAdded(Object modelElement){
        
        if (modelElement instanceof FissionOneNucleus){

            // Add a node for each of the constituents of this nucleus.
            ArrayList nucleusConstituents = ((FissionOneNucleus)modelElement).getConstituents();
            for (int i = 0; i < nucleusConstituents.size(); i++){
                
                Object constituent = nucleusConstituents.get( i );
                
                if (constituent instanceof AlphaParticle){
                    // Add a visible representation of the alpha particle to the canvas.
                    AlphaParticleNode alphaNode = new AlphaParticleNode((AlphaParticle)constituent);
                    _modelElementToNodeMap.put( constituent, alphaNode );
                    _nucleusLayer.addChild( alphaNode );
                }
                else if (constituent instanceof Neutron){
                    // Add a visible representation of the neutron to the canvas.
                    NeutronNode neutronNode = new NeutronNode((Neutron)constituent);
                    _modelElementToNodeMap.put( constituent, neutronNode );
                    _nucleusLayer.addChild( neutronNode );
                }
                else if (constituent instanceof Proton){
                    // Add a visible representation of the proton to the canvas.
                    ProtonNode protonNode = new ProtonNode((Proton)constituent);
                    _modelElementToNodeMap.put( constituent, protonNode );
                    _nucleusLayer.addChild( protonNode );
                }
                else {
                    // There is some unexpected object in the list of constituents
                    // of the nucleus.  This should never happen and should be
                    // debugged if it does.
                    assert false;
                }
            }
            
            // Add an atom node for this guy.
            PNode atomNode = new AtomicNucleusNode((FissionOneNucleus)modelElement);
            _nucleusLayer.addChild( atomNode );
            _modelElementToNodeMap.put( modelElement, atomNode );
        }
        else{
            System.err.println("Error: Unable to find appropriate node for model element.");
        }
    }
    
    /**
     * Remove the node or nodes that corresponds with the given model element
     * from the canvas.
     */
    public void modelElementRemoved(Object modelElement){
        
        Object nucleusNode = _modelElementToNodeMap.get( modelElement );
        if ((nucleusNode != null) || (nucleusNode instanceof PNode)){
            
            if (modelElement instanceof FissionOneNucleus){
                // First remove the nodes for all the constituent particles.
                ArrayList nucleusConstituents = ((FissionOneNucleus)modelElement).getConstituents();
                for (int i = 0; i < nucleusConstituents.size(); i++){
                    
                    Object constituent = nucleusConstituents.get( i );

                    PNode constituentNode = (PNode)_modelElementToNodeMap.get( constituent );

                    if (constituentNode != null){
                        _nucleusLayer.removeChild( constituentNode );
                    }
                    
                    _modelElementToNodeMap.remove( constituent );
                }

                // Remove the nucleus node itself.
                _nucleusLayer.removeChild( (PNode )nucleusNode );
                _modelElementToNodeMap.remove( modelElement );
            }
        }
        else{
            System.err.println("Error: Problem encountered removing node from canvas.");
        }
    }
}
