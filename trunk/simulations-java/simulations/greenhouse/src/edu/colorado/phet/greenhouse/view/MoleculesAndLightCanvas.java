// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas on which the interaction between molecules and light (photons) is
 * depicted.
 *
 * @author John Blanco
 */
public class MoleculesAndLightCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double PHOTON_EMITTER_WIDTH = 300;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model-view transform.
    private final ModelViewTransform2D mvt;

    // Model that is being viewed.
    private final PhotonAbsorptionModel photonAbsorptionModel;

    // Local root node for world children.
    PNode myWorldNode;

    // Layers for the canvas.
    private final PNode moleculeLayer;
    private final PNode photonLayer;
    private final PNode photonEmitterLayer;

    // Data structures that match model objects to their representations in
    // the view.
    private final HashMap<Photon, PhotonNode> photonMap = new HashMap<Photon, PhotonNode>();
    private final HashMap<Molecule, MoleculeNode> moleculeMap = new HashMap<Molecule, MoleculeNode>();

    // Button for restoring molecules that break apart.
    private final ButtonNode restoreMoleculeButtonNode;

    // Listener for watching molecules and updating the restore button
    // visibility.
    private final Molecule.Adapter moleculeMotionListener = new Molecule.Adapter() {
        @Override
        public void centerOfGravityPosChanged( Molecule molecule ) {
            updateRestoreMolecueButtonVisibility();
        }
    };

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param photonAbsorptionModel - Model that is being portrayed on this canvas.
     */
    public MoleculesAndLightCanvas( final PhotonAbsorptionModel photonAbsorptionModel ) {

        this.photonAbsorptionModel = photonAbsorptionModel;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteringBoxStrategy( this, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE ) );

        // Set up the model-canvas transform.  The multiplier values below can
        // be used to shift the center of the play area right or left, and the
        // scale factor can be used to essentially zoom in or out.
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.65 ),
                (int) Math.round( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.4 ) ),
                0.18, // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        true );

        setBackground( Color.BLACK );

        // Listen to the model for notifications that we care about.
        photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter() {

            @Override
            public void photonRemoved( Photon photon ) {
                if ( photonLayer.removeChild( photonMap.get( photon ) ) == null ) {
                    System.out.println( getClass().getName() + " - Error: PhotonNode not found for photon." );
                }
                photonMap.remove( photon );
            }

            @Override
            public void photonAdded( Photon photon ) {
                PhotonNode photonNode = new PhotonNode( photon, mvt );
                photonLayer.addChild( photonNode );
                photonMap.put( photon, photonNode );
            }

            @Override
            public void moleculeRemoved( Molecule molecule ) {
                removeMolecule( molecule );
            }

            @Override
            public void moleculeAdded( Molecule molecule ) {
                addMolecule( molecule );
            }
        } );

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild( myWorldNode );

        // Add the layers.
        moleculeLayer = new PNode();
        myWorldNode.addChild( moleculeLayer );
        photonLayer = new PNode();
        myWorldNode.addChild( photonLayer );
        photonEmitterLayer = new PNode();
        myWorldNode.addChild( photonEmitterLayer );

        // Create the control panel for photon emission frequency.
        PNode photonEmissionControlPanel = new QuadEmissionFrequencyControlPanel( photonAbsorptionModel );
        photonEmissionControlPanel.setOffset( 0, 500 );

        // Create the photon emitter.
        PNode photonEmitterNode = new PhotonEmitterNode( PHOTON_EMITTER_WIDTH, mvt, photonAbsorptionModel );
        photonEmitterNode.setOffset( mvt.modelToViewDouble( photonAbsorptionModel.getPhotonEmissionLocation() ) );

        // Create the rod that connects the emitter to the control panel.
        PNode connectingRod = new VerticalRodNode( 30,
                Math.abs( photonEmitterNode.getFullBoundsReference().getCenterY() - photonEmissionControlPanel.getFullBoundsReference().getCenterY() ),
                new Color( 205, 198, 115 ) );

        connectingRod.setOffset(
                photonEmitterNode.getFullBoundsReference().getCenterX() - connectingRod.getFullBoundsReference().width / 2,
                photonEmitterNode.getFullBoundsReference().getCenterY() );

        // Add the button for restoring molecules that break apart.
        restoreMoleculeButtonNode = new ButtonNode( GreenhouseResources.getString( "ButtonNode.ReturnMolecule" ), 24, new Color( 255, 144, 0 ) );
        restoreMoleculeButtonNode.setOffset( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width - restoreMoleculeButtonNode.getFullBounds().getWidth(), 50 );
        restoreMoleculeButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photonAbsorptionModel.restorePhotonTarget();
            }
        } );
        myWorldNode.addChild( restoreMoleculeButtonNode );
        updateRestoreMolecueButtonVisibility();

        // Add the nodes in the order necessary for correct layering.
        photonEmitterLayer.addChild( connectingRod );
        photonEmitterLayer.addChild( photonEmitterNode );
        photonEmitterLayer.addChild( photonEmissionControlPanel );

        // Add in the initial molecule(s).
        for ( Molecule molecule : photonAbsorptionModel.getMolecules() ) {
            addMolecule( molecule );
        }

        // Update the layout.
        updateLayout();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    private void addMolecule( Molecule molecule ) {
        molecule.addListener( moleculeMotionListener );
        MoleculeNode moleculeNode = new MoleculeNode( molecule, mvt );
        moleculeLayer.addChild( moleculeNode );
        moleculeMap.put( molecule, moleculeNode );
        updateRestoreMolecueButtonVisibility();
    }

    private void removeMolecule( Molecule molecule ) {
        if ( moleculeLayer.removeChild( moleculeMap.get( molecule ) ) == null ) {
            System.out.println( getClass().getName() + " - Error: MoleculeNode not found for molecule." );
        }
        moleculeMap.remove( molecule );
        updateRestoreMolecueButtonVisibility();
        molecule.removeListener( moleculeMotionListener );
    }

    /**
     * Update the visibility of the button that restores molecules that have
     * broken apart.  This button should be visible only when one or more
     * molecules are off the screen (more or less).  This routine uses the
     * intermediate rendering size to make the determination, which isn't
     * perfectly accurate, but works well enough for our purposes.
     */
    private void updateRestoreMolecueButtonVisibility() {
        boolean restoreButtonVisible = false;
        Rectangle2D screenRect = new Rectangle2D.Double( 0, 0, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height );
        for ( Molecule molecule : photonAbsorptionModel.getMolecules() ) {
            if ( !screenRect.contains( mvt.modelToView( molecule.getCenterOfGravityPos() ) ) ) {
                restoreButtonVisible = true;
                break;
            }
        }
        restoreMoleculeButtonNode.setVisible( restoreButtonVisible );
    }
}
