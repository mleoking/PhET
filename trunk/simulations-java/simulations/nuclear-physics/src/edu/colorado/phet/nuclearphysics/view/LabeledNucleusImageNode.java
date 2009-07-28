/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The class represents a node that represents a nucleus as an image and that
 * does not need a corresponding model component.  This is generally used in
 * control panels and graphs.
 * 
 * @author John Blanco
 */
public class LabeledNucleusImageNode extends LabeledNucleusNode {

	private static final double IMAGE_SCALING_FACTOR = 0.20;

    /**
     * Constructor that takes the name of an image resource and loads it.
     * 
     * @param imageName - Name of the image resource that will provide the background.
     * @param isotopeNumber - Numerical isotope number, which will be displayed as a pre-script.
     * @param chemicalSymbol - Chemical symbol for the nucleus.
     * @param labelColor - Color that will be used to display the label.
     */
    public LabeledNucleusImageNode( String imageName, String isotopeNumber, String chemicalSymbol, Color labelColor ){
    	
    	super( isotopeNumber, chemicalSymbol, labelColor );
        
        // Get the image for the nucleus.
        BufferedImage im = NuclearPhysicsResources.getImage( imageName );
        
        // Create and add the image node.
        PImage nucleusImage = new PImage(im);
        nucleusImage.setScale( IMAGE_SCALING_FACTOR );
        getRepresentationLayer().addChild(nucleusImage);

        // Scale and position the label.
        double imageWidth = nucleusImage.getFullBoundsReference().getWidth();
        double imageHeight = nucleusImage.getFullBoundsReference().getHeight();
        PNode label = getLabel();
        double scale = Math.min( ( imageWidth / label.getFullBoundsReference().getWidth() ) * 0.9,
        	( imageHeight / label.getFullBoundsReference().getHeight() ) );
        
        label.setScale( scale );

        // Center the label over the nucleus image.
        label.setOffset( ( imageWidth - label.getFullBoundsReference().getWidth() ) / 2, 
      	  	  ( imageHeight - label.getFullBoundsReference().getHeight() ) / 2);

    }
    
    public LabeledNucleusImageNode( NucleusType nucleusType ){
    	
    	super( NucleusDisplayInfo.getDisplayInfoForNucleusType(nucleusType) );
    	
    	NucleusDisplayInfo displayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusType(nucleusType);
        
        // Get the image for the nucleus.
        BufferedImage im = NuclearPhysicsResources.getImage( displayInfo.getImageName() );
        
        // Create and add the image node.
        PImage nucleusImage = new PImage(im);
        nucleusImage.setScale( IMAGE_SCALING_FACTOR );
        getRepresentationLayer().addChild(nucleusImage);

        // Scale and position the label.
        double imageWidth = nucleusImage.getFullBoundsReference().getWidth();
        double imageHeight = nucleusImage.getFullBoundsReference().getHeight();
        PNode label = getLabel();
        double scale = Math.min( ( imageWidth / label.getFullBoundsReference().getWidth() ) * 0.9,
        	( imageHeight / label.getFullBoundsReference().getHeight() ) );
        
        label.setScale( scale );

        // Center the label over the nucleus image.
        label.setOffset( ( imageWidth - label.getFullBoundsReference().getWidth() ) / 2, 
      	  	  ( imageHeight - label.getFullBoundsReference().getHeight() ) / 2);
    }
}
