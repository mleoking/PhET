/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * This class creates a PNode that is a composite of an image (generally
 * a nucleus) and a shaded label.
 * 
 * @author John Blanco
 */
public class LabeledNucleusNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double IMAGE_SCALING_FACTOR = 0.20;
    private static final double FONT_SCALING_FACTOR = IMAGE_SCALING_FACTOR * 9;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param imageName - Name of the image resource that will provide the background.
     * @param isotopeNumber - Numerical isotope number, which will be displayed as a pre-script.
     * @param chemicalSymbol - Chemical symbol for the nucleus.
     * @param labelColor - Color that will be used to display the label.
     */
    public LabeledNucleusNode(String imageName, String isotopeNumber, String chemicalSymbol, Color labelColor){
        
        // Get the image for the nucleus.
        BufferedImage im = NuclearPhysics2Resources.getImage( imageName );
        
        // Create and add the image node.
        PImage nucleus = new PImage(im);
        nucleus.setScale( IMAGE_SCALING_FACTOR );
        addChild(nucleus);
        
        // Create and add the shadowed label.
        String labelText = "<html><sup><font size=-2>" + isotopeNumber +
            " </font></sup>" + chemicalSymbol + "</html>";
        ShadowHTMLNode label = new ShadowHTMLNode(labelText);
        label.setColor( labelColor );
        label.setShadowColor( labelColor == Color.BLACK ? Color.WHITE : Color.BLACK );
        label.setScale(FONT_SCALING_FACTOR);
        label.setShadowOffset( 0.5, 0.5 );
        if (nucleus.getWidth() < label.getWidth()){
            // Center the label over the nucleus.
            label.setOffset((nucleus.getWidth() - label.getWidth()) / 2, 0);
        }
        addChild(label);
    }
    
    /**
     * This main function is used to provide stand-alone testing of the class.
     * 
     * @param args - Unused.
     */
    public static void main(String [] args){
        LabeledNucleusNode testNode = new LabeledNucleusNode("Uranium Nucleus Small.png",
                NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER, NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.URANIUM_235_LABEL_COLOR );
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addScreenChild( testNode );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
