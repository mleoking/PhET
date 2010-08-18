package edu.colorado.phet.membranediffusion.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the "tool box" for gated membrane channels that can
 * be grabbed by the user and placed on the membrane.
 * 
 * @author John Blanco
 */
public class GatedChannelToolBox extends ToolBox {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// The various grabbable things in the box.
	private PotassiumGatedChannelToolBoxNode potassiumGatedChannelToolBoxNode;
	private SodiumGatedChannelToolBoxNode sodiumGatedChannelToolBoxNode;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public GatedChannelToolBox(PDimension size, final MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
	    
	    super(size);
		
		// Create the grabbable items in the box.
        potassiumGatedChannelToolBoxNode = new PotassiumGatedChannelToolBoxNode(model, mvt, canvas);
		addToolBoxItem( potassiumGatedChannelToolBoxNode );
		
        sodiumGatedChannelToolBoxNode = new SodiumGatedChannelToolBoxNode(model, mvt, canvas);
		addToolBoxItem( sodiumGatedChannelToolBoxNode );
		
		// Set the caption.
		// TODO: i18n
		setCaption( "Gated Channels" );
		
		// Finalize the layout.
		updateLayout();
	}
}
