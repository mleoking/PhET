// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.membranechannels.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranechannels.MembraneChannelsStrings;
import edu.colorado.phet.membranechannels.model.MembraneChannelsModel;
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
	
	public GatedChannelToolBox(PDimension size, final MembraneChannelsModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
	    
	    super(size);
		
		// Create the grabbable items in the box.
        sodiumGatedChannelToolBoxNode = new SodiumGatedChannelToolBoxNode(model, mvt, canvas);
		addToolBoxItem( sodiumGatedChannelToolBoxNode );
		
		potassiumGatedChannelToolBoxNode = new PotassiumGatedChannelToolBoxNode(model, mvt, canvas);
		addToolBoxItem( potassiumGatedChannelToolBoxNode );
		
		// Set the caption.
		setCaption( MembraneChannelsStrings.GATED_CHANNELS );
		
		// Finalize the layout.
		updateLayout();
	}
}
