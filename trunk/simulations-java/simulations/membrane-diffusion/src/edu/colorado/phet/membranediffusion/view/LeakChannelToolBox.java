package edu.colorado.phet.membranediffusion.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the "tool box" for membrane leak channels that can be
 * grabbed by the user and placed on the membrane.
 * 
 * @author John Blanco
 */
public class LeakChannelToolBox extends ToolBox {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// The various grabbable things in the box.
	private MembraneChannelToolBoxNode potassiumLeakChannelToolBoxNode;
	private MembraneChannelToolBoxNode sodiumLeakageChannelToolBoxNode;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public LeakChannelToolBox(PDimension size, final MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
	    
	    super(size);
		
		// Create the grabbable items in the box.
		sodiumLeakageChannelToolBoxNode = new SodiumLeakageChannelToolBoxNode(model, mvt, canvas);
		addToolBoxItem( sodiumLeakageChannelToolBoxNode );
		
		potassiumLeakChannelToolBoxNode = new PotassiumLeakageChannelToolBoxNode(model, mvt, canvas);
		addToolBoxItem( potassiumLeakChannelToolBoxNode );
		
		// Set the caption.
		// TODO: i18n
		setCaption( "Leakage Channels" );
		
		// Finalize the layout.
		updateLayout();
	}
}
