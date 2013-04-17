// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

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
public class DnaStrandNode extends PNode {

	public static Color STRAND_1_COLOR = new Color(31, 163, 223);
	public static Color STRAND_2_COLOR = new Color(214, 87, 107);
	private static final boolean SHOW_BOUNDS = true;
	private static final Stroke STRAND_STROKE = new BasicStroke(2);
	
	private DnaStrand dnaStrand;
	private PPath boundsRectNode;
	private PPath strand1Node;
	private PPath strand2Node;
	private ModelViewTransform2D mvt;
	
	public DnaStrandNode(DnaStrand dnaStrand, ModelViewTransform2D mvt, Color emptyGeneSegmentColor){
	
		this.dnaStrand = dnaStrand;
		this.mvt = mvt;
		
		// Create the two main strands of the DNA.
		strand1Node = new PhetPPath(STRAND_STROKE, STRAND_1_COLOR);
		addChild(strand1Node);
		strand2Node = new PhetPPath(STRAND_STROKE, STRAND_2_COLOR);
		addChild(strand2Node);
		
		// Update the shape of the DNA strands.
		updateStrandShapes();
		
		// Set up the visible bounds, if turned on.
		if (SHOW_BOUNDS){
			Shape viewBounds = mvt.createTransformedShape(getFullBoundsReference().getBounds2D());
			boundsRectNode = new PhetPPath(viewBounds, new BasicStroke(1), Color.RED);
			addChild(boundsRectNode);
		}
		
		// Add the gene segment shapes.
		for (DnaStrand.DnaSegmentSpace dnaSegmentSpace : dnaStrand.getDnaSegmentSpaces()){
			addChild(new DnaSegementSpaceNode(dnaSegmentSpace, mvt, emptyGeneSegmentColor));
		}
		
		// Set our initial position.
		updateOffset();
	}
	
    private void updateStrandShapes() {

    	// We only want the shape, and not any translation associated with the
    	// shape, so we create our own transform that only does the scaling
    	// that is indicated in the model-view transform.
    	
    	// Create transform that only scales, and does no translation.
    	AffineTransform scalingOnlyTransform = AffineTransform.getScaleInstance(mvt.getAffineTransform().getScaleX(),
    			mvt.getAffineTransform().getScaleY());
    	
    	// Create the transformed shape.
		Shape transformedShape = scalingOnlyTransform.createTransformedShape(dnaStrand.getStrand1Shape());
		
		// Set the shape.
		strand1Node.setPathTo(transformedShape);
		
    	// Create the transformed shape.
		transformedShape = scalingOnlyTransform.createTransformedShape(dnaStrand.getStrand2Shape());
		
		// Set the shape.
		strand2Node.setPathTo(transformedShape);
    }
    
    private void updateOffset() {
        setOffset( mvt.modelToView( dnaStrand.getPositionRef() ));
    }
    
    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    private static class DnaSegementSpaceNode extends PPath {

    	private static final Stroke OUTLINE_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
    			BasicStroke.JOIN_BEVEL, 0, new float[] {8, 5}, 0);
    	private static final Color EYE_CATCHING_COLOR = Color.YELLOW;
    	
    	private DnaStrand.DnaSegmentSpace dnaSegmentSpace;
    	private Color nonEyeCatchingColor;
    	
		public DnaSegementSpaceNode(final DnaStrand.DnaSegmentSpace dnaSegmentSpace, ModelViewTransform2D mvt, Color colorWhenEmpty) {
			
			super();
			this.nonEyeCatchingColor = colorWhenEmpty;
			this.dnaSegmentSpace = dnaSegmentSpace;
			setStroke(OUTLINE_STROKE);
			setStrokePaint(Color.BLACK);
			
	    	// We only want the shape, and not any translation associated with the
	    	// shape, so we create our own transform that only does the scaling
	    	// that is indicated in the model-view transform.
	    	
	    	// Create transform that only scales, and does no translation.
	    	AffineTransform scalingOnlyTransform = AffineTransform.getScaleInstance(mvt.getAffineTransform().getScaleX(),
	    			mvt.getAffineTransform().getScaleY());
	    	
	    	// Create the transformed shape.
			Shape transformedShape = scalingOnlyTransform.createTransformedShape(dnaSegmentSpace);
			
			// Set the shape.
			setPathTo(transformedShape);
			
			// Set our offset, which is generally expected to be relative to
			// the DNA strand node of which this is a part.
			setOffset(scalingOnlyTransform.transform(dnaSegmentSpace.getOffsetFromDnaStrandPosRef(), null));
			
			// Register for changes that might concern us.
			dnaSegmentSpace.addListener(new DnaStrand.DnaSegmentSpace.Listener() {
				
				public void eyeCatchingStateChange() {
					updateEyeCatching();
				}

				public void occupiedStateChange() {
					updateOccupied();
				}
			});
			
			// Set initial fill.
			updateEyeCatching();
			
			// Set initial visibility.
			updateOccupied();
		}
		
		private void updateEyeCatching(){
			if (dnaSegmentSpace.isEyeCatching()){
				setPaint(EYE_CATCHING_COLOR);
			}
			else{
				setPaint(nonEyeCatchingColor);
			}
		}
		
		private void updateOccupied(){
			// If this space is occupied, the space itself should be invisible.
			setVisible(!dnaSegmentSpace.isOccupied());
		}
    }
}
