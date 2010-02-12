package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.CaptureZone;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents a capture zone in the view, which is essentially the
 * area from which a channel can draw in a particle.  Note that this exists
 * primarily for debug, and will not be visible in the finished product.
 */
public class CaptureZoneNode extends PNode {
	
	private static final float STROKE_WIDTH = 3;
	private static final Color STROKE_COLOR = Color.PINK;
	private static final Stroke STROKE = new BasicStroke(STROKE_WIDTH);
	
    public CaptureZoneNode( CaptureZone captureZone, ModelViewTransform2D mvt ) {
        // Create the shape that represents this capture zone.
        PhetPPath representation = new PhetPPath(mvt.createTransformedShape(captureZone.getShape()), STROKE, STROKE_COLOR);
		addChild( representation );
	}
}
