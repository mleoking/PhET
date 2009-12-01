/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.DnaStrand;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents a strand of DNA in the view.
 * 
 * @author John Blanco
 */
public class DnaStrandNode2 extends PNode {

	private static final boolean SHOW_BOUNDS = true;
	private static final Stroke STRAND_STROKE = new BasicStroke(2);
	
	private DnaStrand dnaStrand;
	private PPath boundsRectNode;
	private PPath strand1Node;
	private PPath strand2Node;
	private ModelViewTransform2D mvt;
	
	public DnaStrandNode2(DnaStrand dnaStrand, ModelViewTransform2D mvt){
	
		this.dnaStrand = dnaStrand;
		this.mvt = mvt;
		
		// Create the two main strands of the DNA.
		strand1Node = new PhetPPath(STRAND_STROKE, new Color(31, 163, 223));
		addChild(strand1Node);
		strand2Node = new PhetPPath(STRAND_STROKE, new Color(214, 87, 107));
		addChild(strand2Node);
		
		// Update our shape.
		updateShape();
		
		// Set up the visible bounds, if turned on.
		if (SHOW_BOUNDS){
			Shape viewBounds = mvt.createTransformedShape(getFullBoundsReference().getBounds2D());
			boundsRectNode = new PhetPPath(viewBounds, new BasicStroke(1), Color.RED);
			addChild(boundsRectNode);
		}
		
		// Set our initial position.
		updateOffset();
	}
	
    private void updateShape() {

    	// We only want the shape, and not any translation associated with the
    	// shape, so we create our own transform that only does the scaling
    	// that is indicated in the model-view transform.
    	
    	// Create transform that only scales, and does no translation.
    	AffineTransform scalingOnlyTransform = AffineTransform.getScaleInstance(mvt.getAffineTransform().getScaleX(),
    			mvt.getAffineTransform().getScaleY());
    	
    	// Create the transformed shape.
		Shape transformedShape = scalingOnlyTransform.createTransformedShape(dnaStrand.getStrand1Shape());
		
		// Set the shape and color.
		strand1Node.setPathTo(transformedShape);
    }
    
    private void updateOffset() {
        setOffset( mvt.modelToView( dnaStrand.getPositionRef() ));
    }
}
