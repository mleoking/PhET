/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.umd.cs.piccolo.PNode;

/**
 * This class creates a PNode that is a composite of a sphere and a shaded
 * label.  To date, this type of node has been used primarily on control
 * panels and in graphs to represent nuclei.
 * 
 * @author John Blanco
 */
public class LabeledNucleusSphereNode extends LabeledNucleusNode {

	private static final double SPHERE_DIAMETER = 50;

    public LabeledNucleusSphereNode( NucleusDisplayInfo displayInfo ){
    	
    	super( displayInfo );
        
    	// Create the gradient paint for the sphere in order to give it a 3D look.
		Paint spherePaint = new RoundGradientPaint( SPHERE_DIAMETER / 8, -SPHERE_DIAMETER / 8, 
				getHighlightColor( displayInfo.getDisplayColor() ), 
				new Point2D.Double( SPHERE_DIAMETER / 4, SPHERE_DIAMETER / 4 ),
				displayInfo.getDisplayColor() );

    	// Create and add the sphere node.
    	SphericalNode sphere = new SphericalNode(SPHERE_DIAMETER, spherePaint, false);
    	sphere.setOffset(SPHERE_DIAMETER / 2, SPHERE_DIAMETER / 2);
        getRepresentationLayer().addChild( sphere );
        
        // Scale and position the label.
        double sphereWidth = sphere.getFullBoundsReference().getWidth();
        PNode label = getLabel();
        double scale = (sphereWidth / label.getFullBoundsReference().getWidth()) * 0.9;
        label.setScale( scale );
        
        // Center the label over the nucleus image.
        label.setOffset( ( sphereWidth - label.getFullBoundsReference().getWidth() ) / 2, 
      	  	  ( sphere.getFullBoundsReference().getHeight() - label.getFullBoundsReference().getHeight() ) / 2);
    }
}
