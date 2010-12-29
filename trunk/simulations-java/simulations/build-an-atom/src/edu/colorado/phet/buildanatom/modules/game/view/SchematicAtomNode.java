package edu.colorado.phet.buildanatom.modules.game.view;


import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.buildanatom.view.ElectronCloudNode;
import edu.colorado.phet.buildanatom.view.ElectronNode;
import edu.colorado.phet.buildanatom.view.ElectronOrbitalNode;
import edu.colorado.phet.buildanatom.view.NeutronNode;
import edu.colorado.phet.buildanatom.view.ProtonNode;
import edu.colorado.phet.buildanatom.view.SubatomicParticleNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for schematic atom nodes, a.k.a. "Bohr model" representations of the atoms.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class SchematicAtomNode extends PNode {

    // The number of layers upon which particles can be placed.  The number
    // was determined empirically and can be adjusted if needed.
    private final static int NUM_NUCLEUS_LAYERS = 8;

    // Reference to the atom being represented.
    protected final Atom atom;

    // Model-view transform.
    private final ModelViewTransform2D mvt;

    // Property that controls whether the electrons are depicted as particles
    // or as a cloud when they are a part of the atom.
    private final BooleanProperty viewOrbitals;

    // Particle layers.  These exist so that we can give the nucleus a faux
    // 3D sort of look, with the particles toward the center of the nucleus
    // on top and those at the edges on the bottom.
    private final ArrayList<PNode> nucleusLayers = new ArrayList<PNode>( NUM_NUCLEUS_LAYERS );

    // Other layers that make up this node.
    protected final PNode backLayer;
    protected final PNode frontLayer;
    private final PNode electronLayer;

    /**
     * Constructor.
     *
     * @param atom - The atom that is being represented by this node.
     * @param mvt - Transform for moving back and forth between model and view coordinates.
     * @param viewOrbitals - A boolean property that controls whether the
     * orbitals (i.e. the electron shells) are shown or whether the electrons
     * in the atoms are depicted as a cloud.
     */
    public SchematicAtomNode( final Atom atom, ModelViewTransform2D mvt, final BooleanProperty viewOrbitals ) {
        this.atom = atom;
        this.mvt = mvt;
        this.viewOrbitals = viewOrbitals;

        // Create the layers and add them in the desired order.
        backLayer = new PNode( );
        addChild( backLayer );
        electronLayer = new PNode( );
        addChild( electronLayer );
        for (int i = 0; i < NUM_NUCLEUS_LAYERS; i++){
            PNode particleLayer = new PNode();
            addChild( particleLayer );
            nucleusLayers.add( particleLayer );
        }
        Collections.reverse( nucleusLayers ); // So that lower index values are higher layers.
        frontLayer = new PNode( );
        addChild( frontLayer );

        // Add the atom's electron shells to the canvas.  There are two representations that are mutually
        // exclusive - the orbital view and the cloud view.
        for ( ElectronShell electronShell : atom.getElectronShells() ) {
            backLayer.addChild( new ElectronOrbitalNode( mvt, viewOrbitals, atom, electronShell, true ) );
        }
        backLayer.addChild( new ElectronCloudNode( mvt, viewOrbitals, atom ) );

        // Add the subatomic particles.
        for ( final Electron electron : atom.getElectrons() ){
            addElectron( electron );
        }
        for ( final Proton proton : atom.getProtons()){
            addProton( proton );
        }
        for ( final Neutron neutron : atom.getNeutrons()){
            addNeutron( neutron );
        }
    }

    /**
     * Decide which of the layers this nucleon should be based on its
     * location.  Atoms close to the center of the nucleus are on the more
     * forward layers, further out are further back.
     *
     * @param nucleon
     * @return
     */
    private int mapNucleonToLayerNumber( SubatomicParticle nucleon ){
        // Note: This algorithm for layering was made up to look reasonable,
        // and can be modified as needed to produce better looking nuclei.
        double maxNucleusRadius = Neutron.RADIUS * 6;
        double distanceFromCenter = nucleon.getPosition().distance( atom.getPosition() );
        return Math.min( (int)Math.floor( distanceFromCenter / (maxNucleusRadius / NUM_NUCLEUS_LAYERS )), NUM_NUCLEUS_LAYERS - 1 );
    }

    /**
     * Update the layer upon which this nucleon's representation resides.  If
     * the nucleon is already on the correct layer, this will have no effect.
     *
     * @param nucleon
     * @param nucleonNode
     */
    private void updateNucleonLayer( SubatomicParticle nucleon, SubatomicParticleNode nucleonNode ){
        int currentLayerNumber = getNucleusLayerIndex( nucleonNode );
        int targetLayerNumber = mapNucleonToLayerNumber( nucleon );
        if (currentLayerNumber != targetLayerNumber){
            removeNucleonNodeFromLayers( nucleonNode );
            nucleusLayers.get( targetLayerNumber ).addChild( nucleonNode );
        }
    }

    /**
     * Get the index of the nucleus layer on which the given node resides.
     *
     * @param nucleonNode
     * @return
     */
    private int getNucleusLayerIndex( SubatomicParticleNode nucleonNode ){
        int index;
        for (index = 0; index < NUM_NUCLEUS_LAYERS; index++){
            if ( nucleusLayers.get( index ).isAncestorOf( nucleonNode )){
                break;
            }
        }
        return index;
    }

    private boolean removeNucleonNodeFromLayers( SubatomicParticleNode nucleonNode ){
        boolean found = false;
        for (PNode nucleusLayer : nucleusLayers){
            if ( nucleusLayer.removeChild( nucleonNode ) != null){
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Add a proton to this atom representation.  Note that this method is
     * sometimes used to add a particle that is actually external to the atom
     * but that may, over the course of its life, be moved into the atom.
     *
     * @param proton
     */
    protected void addProton( final Proton proton ){
        final ProtonNode protonNode = new ProtonNode( mvt, proton );
        nucleusLayers.get( mapNucleonToLayerNumber( proton ) ).addChild( protonNode );
        proton.addPositionListener( new SimpleObserver() {
            public void update() {
                if ( !proton.isUserControlled() ){
                    updateNucleonLayer( proton, protonNode );
                }
            }
        });
    }

    /**
     * Add a neutron to this atom representation.  Note that this method is
     * sometimes used to add a particle that is actually external to the atom
     * but that may, over the course of its life, be moved into the atom.
     *
     * @param neutron
     */
    protected void addNeutron( final Neutron neutron ){
        final NeutronNode neutronNode = new NeutronNode( mvt, neutron );
        nucleusLayers.get( mapNucleonToLayerNumber( neutron ) ).addChild( neutronNode );
        neutron.addPositionListener( new SimpleObserver() {
            public void update() {
                if ( !neutron.isUserControlled() ){
                    updateNucleonLayer( neutron, neutronNode );
                }
            }
        });
    }

    /**
     * Add an electron to this atom representation.  Note that this method is
     * sometimes used to add a particle that is actually external to the atom
     * but that may, over the course of its life, be moved into the atom.
     *
     * @param neutron
     */
    protected void addElectron( final Electron electron ){
        electronLayer.addChild( new ElectronNode( mvt, electron ){{
            final SimpleObserver updateVisibility = new SimpleObserver() {
                public void update() {
                    setVisible( viewOrbitals.getValue() || !atom.containsElectron( electron ) );
                }
            };
            viewOrbitals.addObserver( updateVisibility );
            atom.addObserver( updateVisibility );
            updateVisibility.update();
        }} );
    }
}
