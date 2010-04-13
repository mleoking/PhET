package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * This class represents the "toolbox" for membrane channels that can be
 * grabbed by the user and placed on the membrane.
 * 
 * @author John Blanco
 */
public class MembraneChannelToolBoxNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	// Defines the width of the tool box as a proportion of the parent window's
	// width.
	protected static final double WIDTH_PROPORTION = 0.8;
	
	// Aspect ratio, width divided by height, from which the height will be
	// calculated.  Smaller numbers means a taller box.
	protected static final double ASPECT_RATIO = 8; 
	
	// Offset from the bottom of the window.
	protected static final double OFFSET_FROM_BOTTOM = 10;
	
	// Background color used to fill the box.
	private static final Color BACKGROUND_COLOR = Color.WHITE; 
	
	// Outline stroke.
	protected static final float OUTLINE_STROKE_WIDTH = 2f;
	private static final Stroke OUTLINE_STROKE = new BasicStroke(OUTLINE_STROKE_WIDTH);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Main outline of the box.
	protected PhetPPath boxNode;

	// The various grabbable things in the box.
	// TODO
	
	// Reference to the model.
	protected MembraneDiffusionModel model;
	
	// Reference to the model-view transform.
	protected ModelViewTransform2D mvt;
	
	// Reference to the canvas upon which we are placed.
	protected PhetPCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public MembraneChannelToolBoxNode(final PhetPCanvas canvas, final MembraneDiffusionModel model, ModelViewTransform2D mvt) {
		
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
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, canvas.getWidth(), 100, 15, 15), BACKGROUND_COLOR,
				OUTLINE_STROKE, Color.BLACK);
		addChild(boxNode);
		
		// Create the grabbable items in the box.
		// TODO
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
	}
}
