package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.membranediffusion.model.CaptureZone;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that represents a capture zone in the view, which is essentially the
 * area from which a channel can draw in a particle.  Note that this exists
 * primarily for debug, and will not be visible in the finished product.
 */
public class CaptureZoneNode extends PNode {
	
	private static final float STROKE_WIDTH = 3;
	private static final Stroke STROKE = new BasicStroke(STROKE_WIDTH);
	
	private CaptureZone captureZone;
	private ModelViewTransform2D mvt;
	private PPath representation;
	
    public CaptureZoneNode( CaptureZone captureZone, ModelViewTransform2D mvt, Color color ) {
    	this.captureZone = captureZone;
    	this.mvt = mvt;
    	
        // Create the shape that represents this capture zone.
        representation = new PPath();
        representation.setStroke(STROKE);
        representation.setStrokePaint(color);
        updateRepresentation();
		addChild( representation );
	}
    
    public void updateRepresentation(){
    	representation.setPathTo(mvt.createTransformedShape(captureZone.getShape()));
    }
}
