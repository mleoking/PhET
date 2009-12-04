package edu.colorado.phet.genenetwork.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class LactoseSyringeNode extends PNode {

	PNode _displayImage;
	
	public LactoseSyringeNode() {
        // Load the graphic image for this device.
        BufferedImage bufferedImage = GeneNetworkResources.getImage( "lactose-syringe.png" );
        _displayImage = new PImage( bufferedImage );
        addChild(_displayImage);
	}
}
