/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
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
	
	private static final double FLASHLIGHT_WIDTH = 300;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Model that is being viewed by this canvas.
	private PhotonAbsorptionModel photonAbsorptionModel;

    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Local root node for world children.
    PNode myWorldNode;
    
    // Layers for the canvas.
    private PNode moleculeLayer;
    private PNode photonEmitterLayer;
    
    // Data structures that match model objects to their representations in
    // the view.
    private HashMap<Photon, PhotonNode> photonMap = new HashMap<Photon, PhotonNode>();
    private HashMap<Molecule, MoleculeNode> moleculeMap = new HashMap<Molecule, MoleculeNode>();
    
    // Node that depicts the largest open rectangle, used for debugging.
    private PhetPPath largestOpenRect = new PhetPPath(new BasicStroke(3), Color.YELLOW);
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhotonAbsorptionCanvas(PhotonAbsorptionModel photonAbsorptionModel  ) {
        
        this.photonAbsorptionModel = photonAbsorptionModel;

    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(
    	        new PhetPCanvas.CenteringBoxStrategy(this, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE));
    	
    	// Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point((int)Math.round(GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.65), 
        				(int)Math.round(GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.5 )),
        				0.25,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        				true);

        setBackground( Color.BLACK );
        
        // Listen to the model for notifications that we care about.
        photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter() {
            
            public void photonRemoved( Photon photon ) {
                if (moleculeLayer.removeChild( photonMap.get( photon ) ) == null){
                    System.out.println( getClass().getName() + " - Error: PhotonNode not found for photon." );
                }
                photonMap.remove( photon );
            }
            
            public void photonAdded( Photon photon ) {
                PhotonNode photonNode = new PhotonNode(photon, mvt); 
                moleculeLayer.addChild( photonNode );
                photonMap.put( photon, photonNode );
            }
            
            public void moleculeRemoved( Molecule molecule ) {
                if (moleculeLayer.removeChild( moleculeMap.get( molecule ) ) == null){
                    System.out.println( getClass().getName() + " - Error: MoleculeNode not found for molecule." );
                }
                moleculeMap.remove( molecule );
            }
            
            public void moleculeAdded( Molecule molecule ) {
                addMolecule( molecule );
            }
        });

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);
        
        // Add the layers.
        moleculeLayer = new PNode();
        myWorldNode.addChild( moleculeLayer );
        photonEmitterLayer = new PNode();
        myWorldNode.addChild( photonEmitterLayer );
        
        // Add the chamber.
        PhetPPath chamberNode = new PhetPPath(
                mvt.createTransformedShape( photonAbsorptionModel.getContainmentAreaRect() ),
                new BasicStroke(6),
                Color.LIGHT_GRAY);
        moleculeLayer.addChild(chamberNode);
        
        // Add the flashlight.
        PNode flashlightNode = new PhotonEmitterNode(FLASHLIGHT_WIDTH, mvt, photonAbsorptionModel);
        flashlightNode.setOffset(mvt.modelToViewDouble(photonAbsorptionModel.getPhotonEmissionLocation()));
        photonEmitterLayer.addChild(flashlightNode);
        
        // Add the largest open rectangle.
        moleculeLayer.addChild( largestOpenRect );
        
        // Add in the initial molecule(s).
        for (Molecule molecule : photonAbsorptionModel.getMolecules()){
            addMolecule( molecule );
        }
        
        // Update the layout.
        updateLayout();
        
        // Update other initial state.
        // TODO: TBD
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        Dimension2D screenSize = getScreenSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else {
        	// TODO: TBD
        }
    }
    
    private void addMolecule(Molecule molecule){
        MoleculeNode moleculeNode = new MoleculeNode(molecule, mvt); 
        moleculeLayer.addChild( moleculeNode );
        moleculeMap.put( molecule, moleculeNode );
    }
}
