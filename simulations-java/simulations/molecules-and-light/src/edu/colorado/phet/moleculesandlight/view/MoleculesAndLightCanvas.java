// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculesandlight.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.photonabsorption.model.Molecule;
import edu.colorado.phet.common.photonabsorption.model.Photon;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.view.MoleculeNode;
import edu.colorado.phet.common.photonabsorption.view.PhotonEmitterNode;
import edu.colorado.phet.common.photonabsorption.view.PhotonNode;
import edu.colorado.phet.common.photonabsorption.view.VerticalRodNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.moleculesandlight.MoleculesAndLightModule;
import edu.colorado.phet.moleculesandlight.MoleculesAndLightResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

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

    // Model-view transform for intermediate coordinates.
    private static final PDimension INTERMEDIATE_RENDERING_SIZE = new PDimension( 786, 786 );

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

    // Button for displaying EM specturm.
    private final ButtonNode showSpectrumButton = new ButtonNode( MoleculesAndLightResources.getString( "SpectrumWindow.buttonCaption" ), new PhetFont( Font.BOLD, 24 ), new Color( 185, 178, 95 ) );

    // Window that displays the EM spectrum upon request.
    private final SpectrumWindow spectrumWindow = new SpectrumWindow() {{ setVisible( false ); }};

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param parentFrame           TODO
     * @param photonAbsorptionModel - Model that is being portrayed on this canvas.
     */
    public MoleculesAndLightCanvas( final Frame parentFrame, final MoleculesAndLightModule module, final PhotonAbsorptionModel photonAbsorptionModel ) {

        this.photonAbsorptionModel = photonAbsorptionModel;

        // Monitor the property that the user can use (through the options
        // menu) to force the background to be white.
        module.getWhiteBackgroundProperty().addObserver( new SimpleObserver() {
            public void update() {
                setBackground( module.getWhiteBackgroundProperty().get() ? Color.white : Color.black );
            }
        } );

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteringBoxStrategy( this, INTERMEDIATE_RENDERING_SIZE ) );

        // Set up the model-canvas transform.  The multiplier values below can
        // be used to shift the center of the play area right or left, and the
        // scale factor can be used to essentially zoom in or out.
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( INTERMEDIATE_RENDERING_SIZE.width * 0.65 ),
                           (int) Math.round( INTERMEDIATE_RENDERING_SIZE.height * 0.35 ) ),
                0.18, // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
                true );

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

            @Override
            public void modelReset() {
                // Hide the spectrum window (if it is showing) and set it
                // back to its default size.
                spectrumWindow.setVisible( false );
                spectrumWindow.setToDefaultSizeAndPosition();
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
        photonEmissionControlPanel.setOffset( -30, 500 );

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
        restoreMoleculeButtonNode = new ButtonNode( MoleculesAndLightResources.getString( "ButtonNode.ReturnMolecule" ), new PhetFont( Font.BOLD, 24 ), new Color( 255, 144, 0 ) );
        restoreMoleculeButtonNode.setOffset( INTERMEDIATE_RENDERING_SIZE.width - restoreMoleculeButtonNode.getFullBounds().getWidth(), 50 );
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

        // Add the button for displaying the EM spectrum.
        myWorldNode.addChild( showSpectrumButton );
        showSpectrumButton.setOffset( 0, 700 );
        showSpectrumButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                // If the spectrum window is currently invisible
                // maximized, un-maximize it.
                if ( !spectrumWindow.isVisible() && ( spectrumWindow.getExtendedState() & Frame.MAXIMIZED_BOTH ) == Frame.MAXIMIZED_BOTH ) {
                    spectrumWindow.setExtendedState( spectrumWindow.getExtendedState() & ~Frame.MAXIMIZED_BOTH );
                }
                // If the spectrum window is currently minimized, restore it.
                if ( spectrumWindow.getState() == Frame.ICONIFIED ) {
                    spectrumWindow.setState( Frame.NORMAL );
                }

                // Always set it visible.  This has the effect of bringing it
                // to the front if it is behind the main sim window.
                spectrumWindow.setVisible( true );
            }
        } );

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
        Rectangle2D screenRect = new Rectangle2D.Double( 0, 0, INTERMEDIATE_RENDERING_SIZE.width, INTERMEDIATE_RENDERING_SIZE.height );
        for ( Molecule molecule : photonAbsorptionModel.getMolecules() ) {
            if ( !screenRect.contains( mvt.modelToView( molecule.getCenterOfGravityPos() ) ) ) {
                restoreButtonVisible = true;
                break;
            }
        }
        restoreMoleculeButtonNode.setVisible( restoreButtonVisible );
    }
}
