// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.view.ElectronNode;
import edu.colorado.phet.buildanatom.view.ElectronOrbitalNode;
import edu.colorado.phet.buildanatom.view.FixedSizeElectronCloudNode;
import edu.colorado.phet.buildanatom.view.NeutronNode;
import edu.colorado.phet.buildanatom.view.OrbitalView;
import edu.colorado.phet.buildanatom.view.OrbitalViewProperty;
import edu.colorado.phet.buildanatom.view.ProtonNode;
import edu.colorado.phet.buildanatom.view.ResizingElectronCloudNode;
import edu.colorado.phet.buildanatom.view.SubatomicParticleNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
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
    private final ModelViewTransform mvt;

    // Property that controls whether the electrons are depicted as particles
    // or as a cloud when they are a part of the atom.
    private final OrbitalViewProperty orbitalViewProperty;

    // Particle layers.  These exist so that we can give the nucleus a faux
    // 3D sort of look, with the particles toward the center of the nucleus
    // on top and those at the edges on the bottom.
    private final ArrayList<PNode> nucleusLayers = new ArrayList<PNode>( NUM_NUCLEUS_LAYERS );

    // Other layers that make up this node.
    protected final PNode electronShellLayer;
    protected final PNode frontLayer;
    private final PNode electronLayer;

    // Flags that control whether or not the corresponding subatomic particles
    // are interactive.
    private final boolean electronsAreInteractive;
    private final boolean protonsAreInteractive;
    private final boolean neutronsAreInteractive;

    private final ResizingElectronCloudNode electronCloudNode;

    /**
     * Constructor that assumes that all particles are interactive, meaning
     * that the user can pick them up and move them.
     *
     * @param atom - The atom that is being represented by this node.
     * @param mvt - Transform for moving back and forth between model and view coordinates.
     * @param orbitalViewProperty - A property that controls how the electrons
     * are represented.
     */
    public SchematicAtomNode( final Atom atom, ModelViewTransform mvt, final OrbitalViewProperty orbitalViewProperty ) {
        this( atom, mvt, orbitalViewProperty, true, true, true );
    }

    /**
     * Constructor.
     *
     * @param atom - The atom that is being represented by this node.
     * @param mvt - Transform for moving back and forth between model and view coordinates.
     * @param orbitalViewProperty - A property that controls how the electrons
     * are represented.
     * @param protonsAreInteractive - Flag that controls whether protons can
     * be picked up and moved.
     * @param neutronsAreInteractive - Flag that controls whether neutrons can
     * be picked up and moved.
     * @param electronsAreInteractive - Flag that controls whether electrons can
     * be picked up and moved.
     */
    public SchematicAtomNode( final Atom atom, ModelViewTransform mvt, final OrbitalViewProperty orbitalViewProperty,
            boolean protonsAreInteractive, boolean neutronsAreInteractive, boolean electronsAreInteractive ) {
        this.protonsAreInteractive = protonsAreInteractive;
        this.electronsAreInteractive = electronsAreInteractive;
        this.neutronsAreInteractive = neutronsAreInteractive;
        this.atom = atom;
        this.mvt = mvt;
        this.orbitalViewProperty = orbitalViewProperty;

        // Create the layers and add them in the desired order.
        electronShellLayer = new PNode( );
        addChild( electronShellLayer );
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

        // Add the atom's electron shells to the canvas.  There are three representations that are mutually
        // exclusive - the orbital view, the resizing cloud view, and the fixed-size cloud view.
        for ( ElectronShell electronShell : atom.getElectronShells() ) {
            electronShellLayer.addChild( new ElectronOrbitalNode( mvt, orbitalViewProperty, atom, electronShell, true ) );
        }
        electronCloudNode = new ResizingElectronCloudNode( mvt, orbitalViewProperty, atom );
        electronShellLayer.addChild( electronCloudNode );

        // Add the subatomic particles.
        for ( final Electron electron : atom.getElectrons() ){
            addElectronNode( electron );
        }
        for ( final Proton proton : atom.getProtons()){
            addProtonNode( proton );
        }
        for ( final Neutron neutron : atom.getNeutrons()){
            addNeutronNode( neutron );
        }
    }

    protected ResizingElectronCloudNode getElectronCloudNode() {
        return electronCloudNode;
    }

    /**
     * Decide which of the layers this nucleon should be based on its
     * location.  Atoms close to the center of the nucleus are on the more
     * forward layers, further out are further back.
     *
     * @param nucleon
     * @return
     */
    private int mapNucleonToLayerNumber( SphericalParticle nucleon ){
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
    private void updateNucleonLayer( SphericalParticle nucleon, SubatomicParticleNode nucleonNode ){
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
    protected void addProtonNode( final Proton proton ){

        // Create the node to represent this particle.
        final ProtonNode protonNode = new ProtonNode( mvt, proton );
        proton.addPositionListener( new SimpleObserver() {
            public void update() {
                if ( !proton.isUserControlled() ){
                    updateNucleonLayer( proton, protonNode );
                }
            }
        });

        // Set up the removal of this particle's representation when the
        // particle itself is removed.
        proton.addListener( new SphericalParticle.Adapter(){
            @Override
            public void removedFromModel( SphericalParticle particle ) {
                removeNucleonNodeFromLayers( protonNode );
                proton.removeListener( this );
            }
        });

        // Set the pickability of the new node.
        if ( !protonsAreInteractive ){
            protonNode.setPickable( false );
            protonNode.setChildrenPickable( false );
        }

        // Add the new node to the appropriate layer.
        nucleusLayers.get( mapNucleonToLayerNumber( proton ) ).addChild( protonNode );
    }

    /**
     * Add a neutron to this atom representation.  Note that this method is
     * sometimes used to add a particle that is actually external to the atom
     * but that may, over the course of its life, be moved into the atom.
     *
     * @param neutron
     */
    protected void addNeutronNode( final Neutron neutron ){
        // Create the node to represent this particle.
        final NeutronNode neutronNode = new NeutronNode( mvt, neutron );
        neutron.addPositionListener( new SimpleObserver() {
            public void update() {
                if ( !neutron.isUserControlled() ){
                    updateNucleonLayer( neutron, neutronNode );
                }
            }
        });

        // Set up the removal of this particle's representation when the
        // particle itself is removed.
        neutron.addListener( new SphericalParticle.Adapter(){
            @Override
            public void removedFromModel( SphericalParticle particle ) {
                removeNucleonNodeFromLayers( neutronNode );
                neutron.removeListener( this );
            }
        });

        // Set the pickability of the new node.
        if ( !neutronsAreInteractive ){
            neutronNode.setPickable( false );
            neutronNode.setChildrenPickable( false );
        }

        // Add the new node to the appropriate layer.
        nucleusLayers.get( mapNucleonToLayerNumber( neutron ) ).addChild( neutronNode );
    }

    /**
     * Add an electron to this atom representation.  Note that this method is
     * sometimes used to add a particle that is actually external to the atom
     * but that may, over the course of its life, be moved into the atom.
     *
     * @param electron
     */
    protected void addElectronNode( final Electron electron ){

        // Create the node to represent this particle.
        final ElectronNode electronNode = new ElectronNode( mvt, electron ){{
            orbitalViewProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( orbitalViewProperty.getValue() == OrbitalView.PARTICLES || !atom.containsElectron( electron ) );
                }
            } );
            atom.addAtomListener( new AtomListener.Adapter(){
                @Override
                public void configurationChanged() {
                    setVisible( orbitalViewProperty.getValue() == OrbitalView.PARTICLES || !atom.containsElectron( electron ) );
                }
            } );
        }};

        // Add the particle to the representation.
        electronLayer.addChild( electronNode );

        // Set up automatic removal of the particle's representation when it
        // is removed from the model.
        electron.addListener( new SphericalParticle.Adapter() {
            @Override
            public void removedFromModel( SphericalParticle particle ) {
                electronLayer.removeChild( electronNode );
                electron.removeListener( this );
            }
        });

        // Set the pickability of the new node.
        if ( !electronsAreInteractive ){
            electronNode.setPickable( false );
            electronNode.setChildrenPickable( false );
        }
    }

    /**
     * Generic version of the add routine that determines the appropriate
     * representation to add.
     *
     * @param particle
     */
    protected void addParticleNode( SphericalParticle particle ) {
        if ( particle instanceof Neutron ) {
            addNeutronNode( (Neutron) particle );
        }
        else if ( particle instanceof Proton ) {
            addProtonNode( (Proton) particle );
        }
        else if ( particle instanceof Electron ) {
            addElectronNode( (Electron) particle );
        }
    }
}
