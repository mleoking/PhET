// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.teetertotter.model.AttachmentBar;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Graphic for the bar that connects from the pivot point of the fulcrum to
 * the plank.
 *
 * @author John Blanco
 */
public class AttachmentBarNode extends ModelObjectNode {
    public AttachmentBarNode( final ModelViewTransform mvt, final AttachmentBar attachmentBar ) {
        super( mvt, attachmentBar, Color.GRAY );
    }
}
