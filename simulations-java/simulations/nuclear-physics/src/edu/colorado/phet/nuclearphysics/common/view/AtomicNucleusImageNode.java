/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
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

    PNode _displayImage;
    
    public AtomicNucleusImageNode( AtomicNucleus atomicNucleus, AtomicNucleusImageType imageType ){
        
        super(atomicNucleus);
        
        createImage( atomicNucleus, imageType );
        
        addChild(0, _displayImage);
    }

	private void createImage( AtomicNucleus atomicNucleus, AtomicNucleusImageType imageType ) {
		// Create a graphical image that will represent this nucleus in the view.
		if ( imageType == AtomicNucleusImageType.NUCLEONS_VISIBLE ){
	        _displayImage = NucleusImageFactory.getInstance().getNucleusImage( atomicNucleus.getNumNeutrons(), 
	                atomicNucleus.getNumProtons(), 25 );
	        // Scale the image to the appropriate size.  Note that this is tweaked
	        // a little bit in order to make it look better.
	        _displayImage.scale( (atomicNucleus.getDiameter()/1.2)/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
		}
		else if ( imageType == AtomicNucleusImageType.GRADIENT_SPHERE ){
	        _displayImage = new SphericalNode( atomicNucleus.getDiameter(), Color.RED, false );
		}
		else if ( imageType == AtomicNucleusImageType.CIRCLE_WITH_HIGHLIGHT ){
	        _displayImage = new PPath( new Ellipse2D.Double( 0, 0, atomicNucleus.getDiameter(),
	        		-atomicNucleus.getDiameter() / 2 ) );
	        _displayImage.setPaint( Color.red );
		}
        _displayImage.setOffset( -atomicNucleus.getDiameter() / 2, -atomicNucleus.getDiameter() / 2 );
	}
    
    protected void handleAtomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
            ArrayList byProducts){
        
        super.handleAtomicWeightChanged( atomicNucleus, numProtons, numNeutrons, byProducts );
        
        // Generate a new image for this node, since the weight has changed.
        removeChild( _displayImage );

        _displayImage = NucleusImageFactory.getInstance().getNucleusImage( atomicNucleus.getNumProtons(), 
                atomicNucleus.getNumNeutrons(), 20 );
        _displayImage.scale( (atomicNucleus.getDiameter()/1.2)/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        _displayImage.setOffset( -atomicNucleus.getDiameter() / 2, -atomicNucleus.getDiameter() / 2 );
        addChild(0, _displayImage);
    }
}
