// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import edu.colorado.phet.balanceandtorque.teetertotter.model.LabeledImageMass;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 */
public class LabeledImageModelElementNode extends ImageModelElementNode {
    private static final double INSET_PROPORTION = 0.02;

    public LabeledImageModelElementNode( final ModelViewTransform mvt, final LabeledImageMass mass, PhetPCanvas canvas ) {
        super( mvt, mass, canvas );
        double inset = getFullBoundsReference().width * INSET_PROPORTION;
        // Create the label.
        PText label = new PText( mass.getLabelText() ) {{
            setFont( new PhetFont( 14, true ) );
        }};
        // Scale the label to fit.
        double widthScale = ( getFullBoundsReference().width - ( 2 * inset ) ) / label.getFullBoundsReference().width;
        double heightScale = ( getFullBoundsReference().height - ( 2 * inset ) ) / label.getFullBoundsReference().height;
        label.setScale( Math.min( widthScale, heightScale ) );
        label.centerFullBoundsOnPoint( getFullBoundsReference().getWidth() / 2, getFullBoundsReference().getHeight() / 2 );
        // Add the label as a child.
        addChild( label );
    }
}
