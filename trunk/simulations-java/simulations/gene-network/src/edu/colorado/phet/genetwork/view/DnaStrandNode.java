/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents a strand of DNA in the view.
 * 
 * @author John Blanco
 */
public class DnaStrandNode extends PNode {

	private static final boolean SHOW_BOUNDS = true;
	private static final int NUM_SEGMENTS = 300;
	private static final Stroke STRAND_STROKE = new BasicStroke(2);
	
	private PPath boundsRectNode;
	
	public DnaStrandNode(Rectangle2D modelBounds, ModelViewTransform2D mvt){
	
		Rectangle2D viewBounds = mvt.createTransformedShape(modelBounds).getBounds2D();
		if (SHOW_BOUNDS){
			boundsRectNode = new PhetPPath(viewBounds, new BasicStroke(1), Color.RED);
			addChild(boundsRectNode);
		}

		GeneralPath strand1Path = new GeneralPath();
		GeneralPath strand2Path = new GeneralPath();
		float strandXOffset = 7; 
		strand1Path.moveTo((float)viewBounds.getMinX(),
				(float)(viewBounds.getMinY() + viewBounds.getHeight() / 2));
		strand2Path.moveTo((float)viewBounds.getMinX() + strandXOffset,
				(float)(viewBounds.getMinY() + viewBounds.getHeight() / 2));
		double angle = 0;
		for (double xPos = viewBounds.getMinX(); xPos < viewBounds.getMaxX(); xPos += viewBounds.getWidth() / NUM_SEGMENTS){
			// Draw the next segment of the DNA strand.  Note that this only
			// fills a portion of the bounds, since that is the way it appears
			// in the specification.
			strand1Path.lineTo( (float)xPos, 
				(float)(viewBounds.getCenterY() + Math.sin(angle) * viewBounds.getHeight() / 4));
			strand2Path.lineTo( (float)xPos + strandXOffset, 
					(float)(viewBounds.getCenterY() + Math.sin(angle) * viewBounds.getHeight() / 4));
			angle += Math.PI/4;
		}
		
		addChild(new PhetPPath(strand1Path, STRAND_STROKE, new Color(49, 170, 226)));
		addChild(new PhetPPath(strand2Path, STRAND_STROKE, new Color(212, 99, 119)));
	}
}
