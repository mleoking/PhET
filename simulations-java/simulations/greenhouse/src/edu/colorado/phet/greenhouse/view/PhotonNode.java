/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that represents a photon in the view.
 * 
 * @author John Blanco
 */
public class PhotonNode extends PNode implements Observer {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Instance Data
	// ------------------------------------------------------------------------
	
	private PImage photonImage;
	private Photon photon;  // Model element represented by this node.
	private ModelViewTransform2D mvt;
	
	// ------------------------------------------------------------------------
	// Constructor(s)
	// ------------------------------------------------------------------------
	
	public PhotonNode(Photon photon, ModelViewTransform2D mvt) {
		
		this.photon = photon;
		this.photon.addObserver(this);
		this.mvt = mvt;
		
		if (photon.getWavelength() == GreenhouseConfig.irWavelength){
			photonImage = new PImage(GreenhouseResources.getImage("photon-660.png"));
		}
		else{
			photonImage = new PImage(GreenhouseResources.getImage("thin2.png"));
		}
		photonImage.setOffset( -photonImage.getFullBoundsReference().width / 2,
		        -photonImage.getFullBoundsReference().height / 2 );
		addChild(photonImage);
		updatePosition();
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
	public void update(Observable o, Object arg) {
		updatePosition();
	}
	
	private void updatePosition(){
		setOffset(mvt.modelToViewDouble(photon.getLocation()));
	}
	
	// ------------------------------------------------------------------------
	// Inner Classes and Interfaces
	//------------------------------------------------------------------------
}
