// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A label that is switchable between 2 representations: qualitative and quantitative.
 * When property valueVisible is true, the quantitative label is displayed; otherwise the qualitative label is displayed.
 * X-coordinate of the origin is adjusted to be in the center, to simplify layout.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DualLabelNode extends PComposite {

    public DualLabelNode( String quantitativeValue, String qualitativeValue, Property<Boolean> valuesVisible, final PhetFont font ) {

        //REVIEW: Please use PhetPText for cases like this.
        final PText quantitativeNode = new PText( quantitativeValue ) {{
            setFont( font );
        }};
        final PText qualitativeNode = new PText( qualitativeValue ) {{
            setFont( font );
        }};

        // rendering order
        addChild( quantitativeNode );
        addChild( qualitativeNode );

        // layout - horizontally centered
        qualitativeNode.setOffset( -qualitativeNode.getFullBoundsReference().getWidth() / 2, qualitativeNode.getYOffset() );
        quantitativeNode.setOffset( -quantitativeNode.getFullBoundsReference().getWidth() / 2, quantitativeNode.getYOffset() );

        // switch between qualitative and quantitative
        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                quantitativeNode.setVisible( visible );
                qualitativeNode.setVisible( !visible );
            }
        } );
    }
}
