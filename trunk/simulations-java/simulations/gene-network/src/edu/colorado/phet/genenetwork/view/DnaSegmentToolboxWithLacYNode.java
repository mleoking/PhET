package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Extension of the basic tool box class to include LacY.
 * 
 * @author John Blanco
 */
public class DnaSegmentToolboxWithLacYNode extends DnaSegmentToolBoxNode {

	private LacYGeneToolBoxNode lacYGene;

	public DnaSegmentToolboxWithLacYNode(PhetPCanvas canvas, IGeneNetworkModelControl model, ModelViewTransform2D mvt){
		super(canvas, model, mvt);
		
		lacYGene = new LacYGeneToolBoxNode(model, mvt, canvas);
		addChild(lacYGene);
	}

	@Override
	protected void updateLayout(JComponent parent){
		
		// Size the overall box.
    	double width = parent.getWidth() * WIDTH_PROPORTION;
    	boxNode.setPathTo(new RoundRectangle2D.Double(0, 0, width, width / ASPECT_RATIO, 15, 15));
    	setOffset(((double)parent.getWidth() - width) / 2,
    			parent.getHeight() - boxNode.getHeight() - OFFSET_FROM_BOTTOM);
    	
    	// Position the grabbable items.  Since the shapes of these vary quite
    	// a lot, making them line up well requires tweaking the multipliers
    	// below on an individual basis.
    	PBounds boxBounds = boxNode.getFullBounds();
    	lacIPromoter.setOffset(boxBounds.width * 0.07, boxBounds.height * 0.20);
    	lacIGene.setOffset(boxBounds.width * 0.25, boxBounds.height * 0.20);
    	lacZGene.setOffset(boxBounds.width * 0.45, boxBounds.height * 0.20);
    	lacYGene.setOffset(boxBounds.width * 0.65, boxBounds.height * 0.20);
    	lacPromoter.setOffset(boxBounds.width * 0.80, boxBounds.height * 0.20);
    	lacIBindingRegion.setOffset(boxBounds.width * 0.93, boxBounds.height * 0.20);
    	
    	// Position the check box button for turning the lactose meter on and off.
    	lactoseMeterCheckBoxPSwing.setOffset( 10, boxBounds.height - lactoseMeterCheckBoxPSwing.getFullBoundsReference().height - 5);
    	
    	// Position the check box button for turning the legend on and off.
    	legendControlCheckBoxPSwing.setOffset(
    			boxBounds.width - legendControlCheckBoxPSwing.getFullBoundsReference().width *  1.1,
    			boxBounds.height - legendControlCheckBoxPSwing.getFullBoundsReference().height - 5);
    	
    	// Let the model know our size, so that the model elements can figure
    	// out when they are being put back in the box.  Note that some of the
    	// odd-looking stuff related to the Y dimension is due to the
    	// inversion of the Y axis.
    	Point2D originInWorldCoords = localToGlobal(new Point2D.Double(boxBounds.x, boxBounds.y + boxBounds.height));
    	canvas.getPhetRootNode().screenToWorld(originInWorldCoords);
    	Point2D oppositeCornerInWorldCoords = localToGlobal(new Point2D.Double(boxBounds.getMaxX(), boxBounds.getMinY()));
    	canvas.getPhetRootNode().screenToWorld(oppositeCornerInWorldCoords);
    	Rectangle2D modelRect = new Rectangle2D.Double(mvt.viewToModelX(originInWorldCoords.getX()),
    			mvt.viewToModelY(originInWorldCoords.getY()),
    			mvt.viewToModelDifferentialX(oppositeCornerInWorldCoords.getX() - originInWorldCoords.getX()),
    			mvt.viewToModelDifferentialY(oppositeCornerInWorldCoords.getY() - originInWorldCoords.getY()));
    	model.setToolBoxRect( modelRect );
	}
}
