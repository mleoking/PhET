package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This class represents the "toolbox" for DNA segements that can be grabbed
 * by the user and placed into a DNA strand.
 * 
 * @author John Blanco
 */
public class DnaSegmentToolBoxNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	// Defines the width of the tool box as a proportion of the parent window's
	// width.
	static final double WIDTH_PROPORTION = 0.7;
	
	// Aspect ratio, width divided by height, from which the height will be
	// calculated.
	static final double ASPECT_RATIO = 6; 
	
	// Offset from the bottom of the window.
	static final double OFFSET_FROM_BOTTOM = 10;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Main outline of the box.
	private PhetPPath boxNode;

	// The various grabbable things in the box.
	private LacZGeneToolBoxNode lacZGene;
	private LacIGeneToolBoxNode lacIGene;
	private LacOperatorToolBoxNode lacIBindingRegion;
	private LacPromoterToolBoxNode lacPromoter;
	private RnaPolymeraseToolBoxNode polymerase;
	
	// Reference to the model.
	private IGeneNetworkModelControl model;
	
	// Reference to the model-view transform.
	private ModelViewTransform2D mvt;
	
	// Reference to the canvas upon which we are placed.
	private PhetPCanvas canvas;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public DnaSegmentToolBoxNode(final PhetPCanvas canvas, IGeneNetworkModelControl model, ModelViewTransform2D mvt) {
		
		this.canvas = canvas;
		this.model = model;
		this.mvt = mvt;
		
		// Register for events indicating that the parent window was resized.
		canvas.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	updateLayout(canvas);
		    }
		});
		
		// Create the surrounding box.
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, canvas.getWidth(), 100, 15, 15), Color.WHITE, new BasicStroke(2f), Color.BLACK);
		addChild(boxNode);
		
		// Create the grabbable items in the box.
		lacZGene = new LacZGeneToolBoxNode(model, mvt, canvas);
		addChild(lacZGene);
		lacIGene = new LacIGeneToolBoxNode(model, mvt, canvas);
		addChild(lacIGene);
		lacIBindingRegion = new LacOperatorToolBoxNode(model, mvt, canvas);
		addChild(lacIBindingRegion);
		lacPromoter = new LacPromoterToolBoxNode(model, mvt, canvas);
		addChild(lacPromoter);
		polymerase = new RnaPolymeraseToolBoxNode(model, mvt, canvas);
		addChild(polymerase);
		
		// Do the initial layout.
		updateLayout(canvas);
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	private void updateLayout(JComponent parent){
		
		// Size the overall box.
    	double width = parent.getWidth() * WIDTH_PROPORTION;
    	boxNode.setPathTo(new RoundRectangle2D.Double(0, 0, width, width / ASPECT_RATIO, 15, 15));
    	setOffset(((double)parent.getWidth() - width) / 2,
    			parent.getHeight() - boxNode.getHeight() - OFFSET_FROM_BOTTOM);
    	
    	// Position the grabbable items.  Since the shapes of these vary quite
    	// a lot, making them line up well requires tweaking the multipliers
    	// below on an individual basis.
    	PBounds boxBounds = boxNode.getFullBounds();
    	polymerase.setOffset(boxBounds.width * 0.1, boxBounds.height * 0.25);
    	lacPromoter.setOffset(boxBounds.width * 0.3, boxBounds.height / 4);
    	lacIBindingRegion.setOffset(boxBounds.width * 0.5, boxBounds.height * 0.25);
    	lacZGene.setOffset(boxBounds.width * 0.7, boxBounds.height * 0.25);
    	lacIGene.setOffset(boxBounds.width * 0.88, boxBounds.height * 0.25);
    	
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
