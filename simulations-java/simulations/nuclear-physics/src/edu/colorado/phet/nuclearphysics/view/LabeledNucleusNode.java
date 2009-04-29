/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * This class creates a PNode that is a composite of an image (generally
 * a nucleus) and a shaded label.  To date, this type of node has been used
 * primarily on control panels.
 * 
 * @author John Blanco
 */
public class LabeledNucleusNode extends PComposite {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double IMAGE_SCALING_FACTOR = 0.20;
    private static final double SMALLER_FONT_SCALING_FACTOR = IMAGE_SCALING_FACTOR * 9;
    private static final double LARGER_FONT_SCALING_FACTOR = IMAGE_SCALING_FACTOR * 12;
    private static final double SPHERE_DIAMETER = 50;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructor that takes the name of an image resource and loads it.
     * 
     * @param imageName - Name of the image resource that will provide the background.
     * @param isotopeNumber - Numerical isotope number, which will be displayed as a pre-script.
     * @param chemicalSymbol - Chemical symbol for the nucleus.
     * @param labelColor - Color that will be used to display the label.
     */
    public LabeledNucleusNode( String imageName, String isotopeNumber, String chemicalSymbol, Color labelColor ){
        
        // Get the image for the nucleus.
        BufferedImage im = NuclearPhysicsResources.getImage( imageName );
        
        // Create and add the image node.
        PImage nucleusImage = new PImage(im);
        nucleusImage.setScale( IMAGE_SCALING_FACTOR );
        addChild(nucleusImage);
        
        // Create and add the shadowed label.
        String labelText = "<html><sup><font size=-2>" + isotopeNumber +
            " </font></sup>" + chemicalSymbol + "</html>";
        ShadowHTMLNode label = new ShadowHTMLNode(labelText);
        label.setColor( labelColor );
        label.setShadowColor( labelColor == Color.BLACK ? Color.WHITE : Color.BLACK );
        if (isotopeNumber == ""){
        	// If there is no isotope number, a somewhat larger font can be
        	// used for the chemical symbol.
            label.setScale(LARGER_FONT_SCALING_FACTOR);
        }
        else{
            label.setScale(SMALLER_FONT_SCALING_FACTOR);
        }
        label.setShadowOffset( 0.5, 0.5 );
        if (nucleusImage.getWidth() < label.getWidth()){
            // Center the label over the nucleus.
            label.setOffset((nucleusImage.getWidth() - label.getWidth()) / 2, 0);
        }
        addChild(label);
        
        // Make sure we aren't pickable since we don't handle any mouse events.
        setPickable(false);
        setChildrenPickable(false);
    }
    
    /**
     * Constructor that takes a color instead of an image name and creates a
     * sphere to represent the nucleus.
     * 
     * @param sphereColor - Desired color of the sphere the will represent the nucleus.
     * @param isotopeNumber - Numerical isotope number, which will be displayed as a pre-script.
     * @param chemicalSymbol - Chemical symbol for the nucleus.
     * @param labelColor - Color that will be used to display the label.
     */
    public LabeledNucleusNode( Color sphereColor, String isotopeNumber, String chemicalSymbol, Color labelColor ){

    	// Create the gradient paint for the sphere in order to give it a 3D look.
		Paint spherePaint = new RoundGradientPaint( SPHERE_DIAMETER / 8, -SPHERE_DIAMETER / 8, 
				getHighlightColor( sphereColor ), new Point2D.Double( SPHERE_DIAMETER / 4, SPHERE_DIAMETER / 4 ),
				sphereColor );

    	// Create and add the sphere node.
    	SphericalNode sphere = new SphericalNode(SPHERE_DIAMETER, spherePaint, false);
    	sphere.setOffset(SPHERE_DIAMETER / 2, SPHERE_DIAMETER / 2);
        addChild( sphere );
        
        // Create and add the shadowed label.
        String labelText = "<html><sup><font size=-2>" + isotopeNumber +
            " </font></sup>" + chemicalSymbol + "</html>";
        ShadowHTMLNode label = new ShadowHTMLNode( labelText );
        label.setColor( labelColor );
        label.setShadowColor( labelColor == Color.BLACK ? Color.WHITE : Color.BLACK );
        double scale = (SPHERE_DIAMETER / label.getFullBoundsReference().getWidth()) * 0.9;
        label.setScale( scale );
        label.setShadowOffset( 0.5, 0.5 );
        // Center the label over the nucleus.
        label.setOffset( ( sphere.getFullBoundsReference().getWidth() - label.getFullBoundsReference().getWidth() ) / 2, 
        		( sphere.getFullBoundsReference().getHeight() - label.getFullBoundsReference().getHeight() ) / 2);
        addChild(label);
        
        // Make sure we aren't pickable since we don't handle any mouse events.
        setPickable(false);
        setChildrenPickable(false);
    }
    
    /**
     * Get a color for the highlight for use on a sphere.  
     * @param baseColor
     * @return
     */
    private Color getHighlightColor( Color baseColor ){
    	
    	Color highlightColor;
    	
    	int maxIntensity = Math.max( Math.max( baseColor.getRed(), baseColor.getGreen() ), baseColor.getBlue() );
    	if ( maxIntensity > 225 ){
    		// Just go with white if the color is already pretty bright.
    		highlightColor = Color.WHITE;
    	}
    	else {
    		int red = baseColor.getRed() + ((255 - baseColor.getRed()) * 3 / 4);
    		int green = baseColor.getGreen() + ((255 - baseColor.getGreen()) * 3 / 4);
    		int blue = baseColor.getBlue() + ((255 - baseColor.getBlue()) * 3 / 4);
    		highlightColor = new Color( red, green, blue );
    	}
    	
    	return highlightColor;
    	
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * This main function is used to provide stand-alone testing of the class.
     * 
     * @param args - Unused.
     */
    public static void main(String [] args){
        LabeledNucleusNode imageTestNode = new LabeledNucleusNode("Uranium Nucleus Small.png",
                NuclearPhysicsStrings.URANIUM_235_ISOTOPE_NUMBER, NuclearPhysicsStrings.URANIUM_235_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.URANIUM_235_LABEL_COLOR );
        LabeledNucleusNode sphereTestNode = new LabeledNucleusNode(Color.RED,
                NuclearPhysicsStrings.CARBON_14_ISOTOPE_NUMBER, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.CARBON_14_LABEL_COLOR );
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addScreenChild( imageTestNode );
        imageTestNode.setOffset(200, 200);
        canvas.addScreenChild(sphereTestNode);
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
