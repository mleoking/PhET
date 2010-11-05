package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.BucketNode;
import edu.colorado.phet.buildanatom.view.ElectronNode;
import edu.colorado.phet.buildanatom.view.ElectronShellNode;
import edu.colorado.phet.buildanatom.view.NeutronNode;
import edu.colorado.phet.buildanatom.view.ProtonNode;
import edu.colorado.phet.buildanatom.view.SubatomicParticleNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * TODO: consolidate this with SchematicAtomNode, could be a subclass that adds interactivity, or a constructor arg interactive:Boolean
 * @author Sam Reid
 */
public class InteractiveSchematicAtomNode extends PNode {
    // The number of layers upon which particles can be placed.  The number
    // was determined empirically and can be adjusted if needed.
    private final static int NUM_NUCLEUS_LAYERS = 8;

    // Reference to the model.
    private final BuildAnAtomModel model;

    // Particle layers.  These exist so that we can give the nucleus a faux
    // 3D sort of look, with the particles toward the center of the nucleus
    // on top and those at the edges on the bottom.
    private final ArrayList<PNode> nucleusLayers = new ArrayList<PNode>( NUM_NUCLEUS_LAYERS );

    /**
     * Constructor.
     */
    public InteractiveSchematicAtomNode( final BuildAnAtomModel model, ModelViewTransform2D mvt, final BooleanProperty viewOrbitals ) {
        this.model=model;

        PNode backLayer = new PNode( );
        addChild( backLayer );
        PNode electronLayer = new PNode( );
        addChild( electronLayer );
        for (int i = 0; i < NUM_NUCLEUS_LAYERS; i++){
            PNode particleLayer = new PNode();
            addChild( particleLayer );
            nucleusLayers.add( particleLayer );
        }
        Collections.reverse( nucleusLayers ); // So that lower index values are higher layers.
        PNode frontLayer= new PNode( );
        addChild( frontLayer );

        // Add the atom's nucleus location to the canvas.
        DoubleGeneralPath nucleusXMarkerModelCoords = new DoubleGeneralPath();
        double xMarkerSize = Proton.RADIUS; // Arbitrary, adjust as desired.
        nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                model.getAtom().getPosition().getY() - xMarkerSize / 2 );
        nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                model.getAtom().getPosition().getY() + xMarkerSize / 2 );
        nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                model.getAtom().getPosition().getY() + xMarkerSize / 2 );
        nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                model.getAtom().getPosition().getY() - xMarkerSize / 2 );
        Shape nucleusXMarkerShape = mvt.createTransformedShape( nucleusXMarkerModelCoords.getGeneralPath() );
        PNode nucleusXMarkerNode = new PhetPPath( nucleusXMarkerShape, new BasicStroke( 4f ), new Color( 255, 0, 0, 75 ) );
        backLayer.addChild( nucleusXMarkerNode );

        // Add the atom's electron shells to the canvas.
        for ( ElectronShell electronShell : model.getAtom().getElectronShells() ) {
            backLayer.addChild( new ElectronShellNode( mvt, viewOrbitals, model.getAtom(), electronShell,true ) );
        }

        // Add the buckets that hold the sub-atomic particles.
        BucketNode electronBucketNode = new BucketNode( model.getElectronBucket(), mvt );
        electronBucketNode.setOffset( mvt.modelToViewDouble( model.getElectronBucket().getPosition() ) );
        backLayer.addChild( electronBucketNode.getHoleLayer() );
        frontLayer.addChild( electronBucketNode.getContainerLayer() );
        BucketNode protonBucketNode = new BucketNode( model.getProtonBucket(), mvt );
        protonBucketNode.setOffset( mvt.modelToViewDouble( model.getProtonBucket().getPosition() ) );
        backLayer.addChild( protonBucketNode.getHoleLayer() );
        frontLayer.addChild( protonBucketNode.getContainerLayer() );
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToViewDouble( model.getNeutronBucket().getPosition() ) );
        backLayer.addChild( neutronBucketNode.getHoleLayer() );
        frontLayer.addChild( neutronBucketNode.getContainerLayer() );

        // Add the electrons.
        for ( int i = 0; i < model.numElectrons(); i++ ) {
            final int finalI = i;
            electronLayer.addChild( new ElectronNode( mvt, model.getElectron( i ) ){{
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() || !model.getAtom().containsElectron( model.getElectron( finalI ) ) );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
                updateVisibility.update();

                model.getAtom().addObserver( updateVisibility );
            }} );
        }

        // Add the nucleons.
        for ( int i = 0; i < model.numProtons(); i++ ) {
            final Proton proton = model.getProton( i );
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
        for ( int i = 0; i < model.numNeutrons(); i++ ) {
            final Neutron neutron = model.getNeutron( i );
            nucleusLayers.get( mapNucleonToLayerNumber( neutron ) ).addChild( new NeutronNode( mvt, neutron ) );
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
    }

    public AtomValue getGuess() {
        return model.getAtom().toAtomValue();
    }

    public void displayAnswer( AtomValue answer ) {
        model.setState(answer);
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
        double distanceFromCenter = nucleon.getPosition().distance( model.getAtom().getPosition() );
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
            System.out.println("Moving node from layer " + currentLayerNumber + " to layer " + targetLayerNumber);
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
}
