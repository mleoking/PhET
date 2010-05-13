/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that represents a photon in the view.
 * 
 * @author John Blanco
 */
public class PhotonNode extends PNode {

	private static final Random RAND = new Random(); 
	private PImage photonImage;
	

	public PhotonNode() {
		if (RAND.nextBoolean()){
			photonImage = new PImage(GreenhouseResources.getImage("photon-660.png"));
		}
		else{
			photonImage = new PImage(GreenhouseResources.getImage("thin2.png"));
		}
		addChild(photonImage);
	}
}
