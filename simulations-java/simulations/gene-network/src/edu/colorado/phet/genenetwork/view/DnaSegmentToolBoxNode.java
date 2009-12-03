package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
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
	private LacZGeneToolBoxNode2 lacZGene;
	private LacIGeneToolBoxNode lacIGene;
	private LacIBindingRegionToolBoxNode lacIBindingRegion;
	private PolymeraseBindingRegionToolBoxNode polymeraseBindingRegion;
	private PolymeraseToolBoxNode polymerase;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public DnaSegmentToolBoxNode(final PhetPCanvas canvas, IObtainGeneModelElements model, ModelViewTransform2D mvt) {
		
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
		lacZGene = new LacZGeneToolBoxNode2(model, mvt, canvas);
		addChild(lacZGene);
		lacIGene = new LacIGeneToolBoxNode(model, mvt);
		addChild(lacIGene);
		lacIBindingRegion = new LacIBindingRegionToolBoxNode(model, mvt);
		addChild(lacIBindingRegion);
		polymeraseBindingRegion = new PolymeraseBindingRegionToolBoxNode(model, mvt);
		addChild(polymeraseBindingRegion);
		polymerase = new PolymeraseToolBoxNode(model, mvt);
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
    	polymeraseBindingRegion.setOffset(boxBounds.width * 0.3, boxBounds.height / 4);
    	lacIBindingRegion.setOffset(boxBounds.width * 0.5, boxBounds.height * 0.25);
    	lacZGene.setOffset(boxBounds.width * 0.7, boxBounds.height * 0.25);
    	lacIGene.setOffset(boxBounds.width * 0.88, boxBounds.height * 0.25);
	}
}
