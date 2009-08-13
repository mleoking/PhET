/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * This class creates a PNode that is a composite of an image (generally
 * a nucleus) and a shaded label.  To date, this type of node has been used
 * primarily on control panels and in graphs.
 * 
 * @author John Blanco
 */
public abstract class LabeledNucleusNode extends PComposite {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private final PNode _labelLayer;
	private final PNode _representationLayer;
	private final ShadowHTMLNode _label;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public LabeledNucleusNode( String isotopeNumber, String chemicalSymbol, Color labelColor ){

    	// Create the "layers" where the representation and the label will reside.
    	_representationLayer = new PNode();
    	addChild(_representationLayer);
    	_labelLayer = new PNode();
    	addChild(_labelLayer);
    	
        // Create and add the shadowed label.
        String labelText = "<html><sup><font size=-2>" + isotopeNumber + " </font></sup>" + chemicalSymbol + "</html>";
        _label = new ShadowHTMLNode( labelText );
        _label.setColor( labelColor );
        _label.setShadowColor( labelColor == Color.BLACK ? Color.WHITE : Color.BLACK );
        _label.setShadowOffset( 0.5, 0.5 );
        _labelLayer.addChild(_label);
        
        // Make sure we aren't pickable since we don't handle any mouse events.
        setPickable(false);
        setChildrenPickable(false);
    }
    
	public LabeledNucleusNode( NucleusDisplayInfo displayInfo ){

    	// Create the "layers" where the representation and the label will reside.
    	_representationLayer = new PNode();
    	addChild(_representationLayer);
    	_labelLayer = new PNode();
    	addChild(_labelLayer);
    	
    	// Get shorthand references to the needed display information.
    	String isotopeNumber = displayInfo.getIsotopeNumberString();
    	String chemicalSymbol = displayInfo.getChemicalSymbol();
    	Color labelColor = displayInfo.getLabelColor();
    	
        // Create and add the shadowed label.
        String labelText = "<html><sup><font size=-2>" + isotopeNumber + " </font></sup>" + chemicalSymbol + "</html>";
        _label = new ShadowHTMLNode( labelText );
        _label.setColor( labelColor );
        _label.setShadowColor( labelColor == Color.BLACK ? Color.WHITE : Color.BLACK );
        _label.setShadowOffset( 0.5, 0.5 );
        _labelLayer.addChild(_label);
        
        // Make sure we aren't pickable since we don't handle any mouse events.
        setPickable(false);
        setChildrenPickable(false);
    }

	//------------------------------------------------------------------------
	// Public and Protected Methods
	//------------------------------------------------------------------------
	
    public ShadowHTMLNode getLabel() {
		return _label;
	}

    protected PNode getRepresentationLayer() {
		return _representationLayer;
	}

    /**
     * Get a color for the highlight for use on a sphere or circle
     * representing a nucleus.
     *   
     * @param baseColor
     * @return
     */
    protected Color getHighlightColor( Color baseColor ){
    	
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
    
    /**
     * This main function is used to provide stand-alone testing of the class.
     * 
     * @param args - Unused.
     */
    // TODO
//    public static void main(String [] args){
//        LabeledNucleusNode imageTestNode = new LabeledNucleusNode("Uranium Nucleus Small.png",
//                NuclearPhysicsStrings.URANIUM_235_ISOTOPE_NUMBER, NuclearPhysicsStrings.URANIUM_235_CHEMICAL_SYMBOL, 
//                NuclearPhysicsConstants.URANIUM_235_LABEL_COLOR );
//        LabeledNucleusNode sphereTestNode = new LabeledNucleusNode(Color.RED,
//                NuclearPhysicsStrings.CARBON_14_ISOTOPE_NUMBER, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
//                NuclearPhysicsConstants.CARBON_14_LABEL_COLOR );
//        
//        JFrame frame = new JFrame();
//        PhetPCanvas canvas = new PhetPCanvas();
//        frame.setContentPane( canvas );
//        canvas.addScreenChild( imageTestNode );
//        imageTestNode.setOffset(200, 200);
//        canvas.addScreenChild(sphereTestNode);
//        frame.setSize( 800, 600 );
//        frame.setVisible( true );
//    }
}
