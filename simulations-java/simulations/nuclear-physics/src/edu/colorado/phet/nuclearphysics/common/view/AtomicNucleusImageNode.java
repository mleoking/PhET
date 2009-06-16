/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.view.NucleusImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents the view of an Atomic Nucleus from the model.  The
 * nucleus is represented as an image that looks like it contains protons and
 * neutrons, as represented by smaller spheres.
 *
 * @author John Blanco
 */
public class AtomicNucleusImageNode extends AtomicNucleusNode {

    private PNode _displayNode;
    private AtomicNucleusImageType _imageType;
    
    public AtomicNucleusImageNode( AtomicNucleus atomicNucleus, AtomicNucleusImageType imageType ){
        
        super(atomicNucleus);
        
        _imageType = imageType;
        
        _displayNode = createImage( atomicNucleus );
        
        addChild(0, _displayNode);
    }

    /**
     * Create the image that will be used to visually represent this nucleus.
     * 
     * @param atomicNucleus
     * @param imageType
     */
	private PNode createImage( AtomicNucleus atomicNucleus ) {
		
		PNode newImage = null;
		
		// Create a graphical image that will represent this nucleus in the view.
		if ( _imageType == AtomicNucleusImageType.NUCLEONS_VISIBLE ){
			newImage = NucleusImageFactory.getInstance().getNucleusImage( atomicNucleus.getNumNeutrons(), 
	                atomicNucleus.getNumProtons(), 25 );
	        // Scale the image to the appropriate size.  Note that this is tweaked
	        // a little bit in order to make it look better.
			newImage.scale( (atomicNucleus.getDiameter()/1.2)/((newImage.getWidth() + newImage.getHeight()) / 2));

			// Set the offset so that the center will be at point (0, 0).
			newImage.setOffset( -newImage.getFullBoundsReference().width / 2,
					-newImage.getFullBoundsReference().height / 2 );
		}
		else if ( _imageType == AtomicNucleusImageType.GRADIENT_SPHERE ){
			double radius = atomicNucleus.getDiameter() / 2;
    		Paint spherePaint = new RoundGradientPaint( radius, -radius, Color.WHITE,
                    new Point2D.Double( -radius, radius ), getColorForElement(atomicNucleus) );

    		newImage = new SphericalNode( atomicNucleus.getDiameter(), spherePaint, false );
		}
		else if ( _imageType == AtomicNucleusImageType.CIRCLE_WITH_HIGHLIGHT ){
			newImage = new PPath( new Ellipse2D.Double( 0, 0, atomicNucleus.getDiameter(),
	        		-atomicNucleus.getDiameter() / 2 ) );
			newImage.setPaint( getColorForElement(atomicNucleus) );
		}
		
		return newImage;
	}
    
    protected void handleNucleusChangedEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
            ArrayList byProducts){
        
        super.handleNucleusChangedEvent( atomicNucleus, numProtons, numNeutrons, byProducts );
        
        // Generate a new image for this node, since the weight has changed.
        removeChild( _displayNode );
        _displayNode = createImage( atomicNucleus );
        addChild(0, _displayNode);
    }
    
    /**
     * The elements that are represented as circles or spheres need to be some
     * color, and this function figures out what that color should be.
     * @param nucleus
     * @return
     */
    private Color getColorForElement( AtomicNucleus nucleus ){
    	
    	Color color;
    	
    	switch ( nucleus.getNumProtons() ){
    	case 6:
    		// Carbon
    		color = NuclearPhysicsConstants.CARBON_COLOR;
    		break;
    		
    	case 7:
    		// Nitrogen
    		color = NuclearPhysicsConstants.NITROGEN_COLOR;
    		break;
    		
    	case 92:
    		// Uranium
    		color = NuclearPhysicsConstants.URANIUM_COLOR;
    		break;
    		
    	case 83:
    		// Bismuth, which by internal convention is the pre-decay custom nucleus.
    		color = NuclearPhysicsConstants.CUSTOM_NUCLEUS_PRE_DECAY_COLOR;
    		break;
    		
    	case 82:
    		// Lead
    		color = NuclearPhysicsConstants.LEAD_COLOR;
    		break;
    		
    	case 81:
    		// Thallium, which by internal convention is the post-decay custom nucleus.
    		color = NuclearPhysicsConstants.CUSTOM_NUCLEUS_POST_DECAY_COLOR;
    		break;
    		
    	default:
    		// Unknown
    		System.out.println("Warning: Don't have a color assignment for this element.");
    		color = Color.BLACK;
    	    break;
    	}
    		
    	return color;
    }
}
