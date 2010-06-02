package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the "tool box" for membrane channels that can be
 * grabbed by the user and placed on the membrane.
 * 
 * @author John Blanco
 */
public class MembraneChannelToolBox extends PNode {
	
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
	private PotassiumLeakChannelToolBoxNode potassiumLeakChannelNode;
	private SodiumLeakageChannelToolBoxNode sodiumLeakageChannelToolBoxNode;
	private PotassiumGatedChannelToolBoxNode potassiumGatedChannelToolBoxNode;
	private SodiumDualGatedChannelToolBoxNode sodiumGatedChannelToolBoxNode;
	
	// Reference to the model.
	protected MembraneDiffusionModel model;
	
	// Reference to the model-view transform.
	protected ModelViewTransform2D mvt;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public MembraneChannelToolBox(PDimension size, final MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		
		this.model = model;
		this.mvt = mvt;
		
		// Create the surrounding box.
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getHeight(), 15, 15), BACKGROUND_COLOR,
				OUTLINE_STROKE, Color.BLACK);
		addChild(boxNode);
		
		// Create the grabbable items in the box.
		potassiumLeakChannelNode = new PotassiumLeakChannelToolBoxNode(model, mvt, canvas);
		potassiumLeakChannelNode.setOffset(
				boxNode.getFullBoundsReference().getMinX() + boxNode.getFullBoundsReference().width * 0.2, 
				boxNode.getFullBoundsReference().getMinY() + boxNode.getFullBoundsReference().height * 0.3);
		addChild(potassiumLeakChannelNode);
		
		sodiumLeakageChannelToolBoxNode = new SodiumLeakageChannelToolBoxNode(model, mvt, canvas);
		sodiumLeakageChannelToolBoxNode.setOffset(
				boxNode.getFullBoundsReference().getMinX() + boxNode.getFullBoundsReference().width * 0.4, 
				boxNode.getFullBoundsReference().getMinY() + boxNode.getFullBoundsReference().height * 0.3);
		addChild(sodiumLeakageChannelToolBoxNode);
		
		potassiumGatedChannelToolBoxNode = new PotassiumGatedChannelToolBoxNode(model, mvt, canvas);
		potassiumGatedChannelToolBoxNode.setOffset(
				boxNode.getFullBoundsReference().getMinX() + boxNode.getFullBoundsReference().width * 0.6, 
				boxNode.getFullBoundsReference().getMinY() + boxNode.getFullBoundsReference().height * 0.3);
		addChild(potassiumGatedChannelToolBoxNode);

		sodiumGatedChannelToolBoxNode = new SodiumDualGatedChannelToolBoxNode(model, mvt, canvas);
		sodiumGatedChannelToolBoxNode.setOffset(
				boxNode.getFullBoundsReference().getMinX() + boxNode.getFullBoundsReference().width * 0.8, 
				boxNode.getFullBoundsReference().getMinY() + boxNode.getFullBoundsReference().height * 0.2);
		addChild(sodiumGatedChannelToolBoxNode);
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
}
