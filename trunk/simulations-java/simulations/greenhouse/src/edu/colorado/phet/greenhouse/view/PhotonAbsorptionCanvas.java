// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.photonabsorption.model.Photon;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.view.MoleculeNode;
import edu.colorado.phet.common.photonabsorption.view.PhotonEmitterNode;
import edu.colorado.phet.common.photonabsorption.view.PhotonNode;
import edu.colorado.phet.common.photonabsorption.view.VerticalRodNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.common.photonabsorption.model.Molecule;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas on which the photon absorption simulation is depicted.
 *
 * @author John Blanco
 */
public class PhotonAbsorptionCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double PHOTON_EMITTER_WIDTH = 300;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model-view transform.
    private final ModelViewTransform2D mvt;

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

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param photonAbsorptionModel - Model that is being portrayed on this canvas.
     * @param onTabbedSim - boolean that indicates whether the canvas should
     * be sized assuming that it is in a sim with tabs, which reduces the
     * initial size of the play area.
     */
    public PhotonAbsorptionCanvas( PhotonAbsorptionModel photonAbsorptionModel, boolean onTabbedSim ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteringBoxStrategy( this, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE ) );

        // Use a different zoom factor if this is on a non-tabbed sim, since
        // otherwise things will probably go off the left and right edges of
        // the play area.
        double mvtScaleFactor = onTabbedSim ? 0.23 : 0.18;
        // Set up the model-canvas transform.  The multiplier values below can
        // be used to shift the center of the play area right or left, and the
        // scale factor can be used to essentially zoom in or out.
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.65 ),
                (int) Math.round( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.5 ) ),
                mvtScaleFactor, // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        true );

        setBackground( Color.BLACK );

        // Listen to the model for notifications that the canvas cares about,
        // which are primarily the coming and going of model objects.
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
                if ( moleculeLayer.removeChild( moleculeMap.get( molecule ) ) == null ) {
                    System.out.println( getClass().getName() + " - Error: MoleculeNode not found for molecule." );
                }
                moleculeMap.remove( molecule );
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

        // Add the chamber.
        PhetPPath chamberNode = new PhetPPath(
                mvt.createTransformedShape( photonAbsorptionModel.getContainmentAreaRect() ),
                new BasicStroke( 6 ),
                Color.LIGHT_GRAY );
        moleculeLayer.addChild( chamberNode );

        // Create the photon emitter.
        PNode photonEmitterNode = new PhotonEmitterNode( PHOTON_EMITTER_WIDTH, mvt, photonAbsorptionModel );
        photonEmitterNode.setOffset( mvt.modelToViewDouble( photonAbsorptionModel.getPhotonEmissionLocation() ) );

        // Create the control panel for photon emission frequency.
        PNode photonEmissionControlPanel = new DualEmissionFrequencyControlPanel( photonAbsorptionModel );
        photonEmissionControlPanel.setOffset(
                photonEmitterNode.getFullBoundsReference().getCenterX() - ( photonEmissionControlPanel.getFullBoundsReference().width / 2 ),
                photonEmitterNode.getFullBoundsReference().getMaxY() + 50 );

        // Create the rod that connects the emitter to the control panel.
        PNode connectingRod = new VerticalRodNode( 30,
                Math.abs( photonEmitterNode.getFullBoundsReference().getCenterY() - photonEmissionControlPanel.getFullBoundsReference().getCenterY() ),
                Color.LIGHT_GRAY );
        connectingRod.setOffset(
                photonEmitterNode.getFullBoundsReference().getCenterX() - ( connectingRod.getFullBoundsReference().width / 2 ),
                photonEmitterNode.getFullBoundsReference().getCenterY() );

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
        MoleculeNode moleculeNode = new MoleculeNode( molecule, mvt );
        moleculeLayer.addChild( moleculeNode );
        moleculeMap.put( molecule, moleculeNode );
    }
}
