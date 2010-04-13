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
import edu.umd.cs.piccolo.util.PDimension;

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

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public MembraneChannelToolBoxNode(PDimension size, final MembraneDiffusionModel model, ModelViewTransform2D mvt) {
		
		this.model = model;
		this.mvt = mvt;
		
		// Create the surrounding box.
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getHeight(), 15, 15), BACKGROUND_COLOR,
				OUTLINE_STROKE, Color.BLACK);
		addChild(boxNode);
		
		// Create the grabbable items in the box.
		// TODO
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
}
