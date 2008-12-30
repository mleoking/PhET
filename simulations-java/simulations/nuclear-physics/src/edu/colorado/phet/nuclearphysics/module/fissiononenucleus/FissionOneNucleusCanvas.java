/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.fissiononenucleus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.NeutronSource;
import edu.colorado.phet.nuclearphysics.model.Nucleon;
import edu.colorado.phet.nuclearphysics.model.Proton;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.FissionEnergyChart;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.NeutronSourceNode;
import edu.colorado.phet.nuclearphysics.view.ProtonModelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * This class is the canvas upon which the simulation of the fission of a
 * single atomic nucleus is presented to the user. 
 *
 * @author John Blanco
 */
public class FissionOneNucleusCanvas extends PhetPCanvas {
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Constant that sets the scale of this sim, which is in femtometers.
    private final double SCALE = 0.8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private PNode _nucleusParticlesLayerNode;
    private PNode _nucleusLabelsLayerNode;
    private FissionOneNucleusModel _fissionOneNucleusModel;
    private AtomicNucleusNode _atomicNucleusNode;
    private AtomicNucleusNode _daughterNucleusNode;
    private NeutronSourceNode _neutronSourceNode;
    private FissionEnergyChart _fissionEnergyChart;
    private Hashtable _particleToNodeMap;
    private GradientButtonNode _resetButtonNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public FissionOneNucleusCanvas(FissionOneNucleusModel fissionOneNucleusModel) {
        
        // Set up the transform strategy so that the scale is in femtometers
        // and so that the center of the screen above the chart is at
        // coordinate location (0,0).
        setWorldTransformStrategy( new RenderingSizeStrategy(this, new PDimension(150.0d * SCALE, 115.0d * SCALE) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/2, getHeight()/4 );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Register as a listener to the model.
        _fissionOneNucleusModel = fissionOneNucleusModel;
        _fissionOneNucleusModel.addListener( new FissionOneNucleusModel.Listener(){
            public void daughterNucleusCreated(AtomicNucleus daughterNucleus){
                // Create a new node for this nucleus.  Since it is a daughter,
                // it is assumed that the constituent particles are already on
                // the canvas, and are not added here.
                _daughterNucleusNode = new AtomicNucleusNode(daughterNucleus);
                _nucleusLabelsLayerNode.addChild(_daughterNucleusNode);
            }
            public void daughterNucleusRemoved(AtomicNucleus daughterNucleus){
                // Remove the daughter from the canvas and from our records.
                if (_nucleusLabelsLayerNode.removeChild( _daughterNucleusNode ) == null){
                    System.err.println("Warning: Unable to locate and remove daughter nucleus.");
                }
                _daughterNucleusNode = null;
            }
            public void nucleonRemoved(Nucleon nucleon){
                // Remove the nucleon from the canvas and from our records.
                PNode nucleonNode = (PNode)_particleToNodeMap.get( nucleon );
                if (nucleonNode != null){
                   if ( _nucleusParticlesLayerNode.removeChild( nucleonNode ) == null){
                       System.err.println("Error: Unable to locate node for given nucleon.");
                   }
                   _particleToNodeMap.remove( nucleon );
                }
                else{
                    System.err.println("Error: Unable to locate particle in particle-to-node map.");                    
                }
            }
        });
        
        // Create a parent node where we will display the nucleus particles.  
        // This is being done so that a label can be placed over the top of
        // and so that new particles can be added to the world and not end up
        // appearing over other important nodes.
        _nucleusParticlesLayerNode = new PNode();
        addWorldChild(_nucleusParticlesLayerNode);
        
        // Create a parent node where the nodes that create the labels will
        // appear.  Again, this is done to retain the desired "layering"
        // effect.
        _nucleusLabelsLayerNode = new PNode();
        addWorldChild(_nucleusLabelsLayerNode);
        
        // Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        CompositeAtomicNucleus atomicNucleus = _fissionOneNucleusModel.getAtomicNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();
        
        // Add a node for each particle that comprises the nucleus.
        _particleToNodeMap = new Hashtable(nucleusConstituents.size());
        for (int i = 0; i < nucleusConstituents.size(); i++){
            
            Object constituent = nucleusConstituents.get( i );
            
            if (constituent instanceof AlphaParticle){
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleModelNode alphaNode = new AlphaParticleModelNode((AlphaParticle)constituent);
                _nucleusParticlesLayerNode.addChild( alphaNode );
                _particleToNodeMap.put( constituent, alphaNode );
            }
            else if (constituent instanceof Proton){
                // Add a visible representation of the proton to the canvas.
                ProtonModelNode protonNode = new ProtonModelNode((Proton)constituent);
                _nucleusParticlesLayerNode.addChild( protonNode );
                _particleToNodeMap.put( constituent, protonNode );
            }
            else if (constituent instanceof Neutron){
                // Add a visible representation of the neutron to the canvas.
                NeutronModelNode neutronNode = new NeutronModelNode((Neutron)constituent);
                _nucleusParticlesLayerNode.addChild( neutronNode );
                _particleToNodeMap.put( constituent, neutronNode );
            }
            else {
                // There is some unexpected object in the list of constituents
                // of the nucleus.  This should never happen and should be
                // debugged if it does.
                assert false;
            }
        }
        
        // Add the nucleus node to the canvas.  Since the constituents are
        // handled individually, this just shows the label.
        _atomicNucleusNode = new AtomicNucleusNode(fissionOneNucleusModel.getAtomicNucleus());
        _nucleusLabelsLayerNode.addChild( _atomicNucleusNode );
        
        // Add the neutron source to the canvas.
        _neutronSourceNode = new NeutronSourceNode(fissionOneNucleusModel.getNeutronSource(), 26);
        addWorldChild( _neutronSourceNode );
        
        // Register as a listener with the neutron source so that we will know
        // when new neutrons have been produced.
        fissionOneNucleusModel.getNeutronSource().addListener( new NeutronSource.Adapter (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the canvas.
                NeutronModelNode neutronNode = new NeutronModelNode(neutron);
                _nucleusParticlesLayerNode.addChild( neutronNode );
                _particleToNodeMap.put( neutron, neutronNode );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
        
        // Add the button for resetting the nucleus to the canvas.
        _resetButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_NUCLEUS, 16, new Color(0xff9900));
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _fissionOneNucleusModel.getClock().resetSimulationTime();
            }
        });

        // Add to the canvas the chart that will depict the energy of the nucleus.
        _fissionEnergyChart = new FissionEnergyChart(_fissionOneNucleusModel, this);
        addScreenChild( _fissionEnergyChart );
        
        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                _fissionEnergyChart.componentResized( getWidth(), getHeight() );
                _resetButtonNode.setOffset( getWidth() * 0.05, getHeight() * 0.07);
            }
        } );
    }
}
