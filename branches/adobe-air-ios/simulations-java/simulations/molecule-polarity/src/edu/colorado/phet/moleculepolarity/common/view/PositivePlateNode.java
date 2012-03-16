// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.EField;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Positive plate for E-field creation device.
 * Origin at the upper-left corner of the plate, excluding the polarity indicator.
 * <p/>
 * Note that this is similar to NegativePlateNode, but just different enough that
 * some duplicate code is better than an awkward base class.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositivePlateNode extends PComposite {

    public PositivePlateNode( EField eField ) {

        // positive polarity indicator
        final PNode indicatorNode = new PolarityIndicatorNode( true ) {{
            setOffset( ( MPConstants.PLATE_WIDTH / 2 ) - getFullBoundsReference().getWidth() / 2,
                       ( MPConstants.PLATE_PERSPECTIVE_Y_OFFSET / 2 ) - getFullBoundsReference().getHeight() + 4 );
        }};

        // side edge to show thickness
        PPath sideEdgeNode = new PPath( new Rectangle2D.Double( MPConstants.PLATE_WIDTH, 0, MPConstants.PLATE_THICKNESS, MPConstants.PLATE_HEIGHT ) ) {{
            setPaint( MPColors.PLATE_DISABLED_COLOR );
        }};

        // the primary face of the plate
        DoubleGeneralPath facePath = new DoubleGeneralPath() {{
            moveTo( 0, MPConstants.PLATE_PERSPECTIVE_Y_OFFSET );
            lineTo( MPConstants.PLATE_WIDTH, 0 );
            lineTo( MPConstants.PLATE_WIDTH, MPConstants.PLATE_HEIGHT );
            lineTo( 0, MPConstants.PLATE_PERSPECTIVE_Y_OFFSET + ( MPConstants.PLATE_HEIGHT - 2 * MPConstants.PLATE_PERSPECTIVE_Y_OFFSET ) );
            closePath();
        }};
        final PPath faceNode = new PPath( facePath.getGeneralPath() ) {{
            setPaint( MPColors.PLATE_DISABLED_COLOR );
        }};

        // rendering order
        addChild( indicatorNode );
        addChild( sideEdgeNode );
        addChild( faceNode );

        // when the field is enabled/disabled...
        eField.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                faceNode.setPaint( enabled ? MPColors.PLATE_POSITIVE_COLOR : MPColors.PLATE_DISABLED_COLOR );
                indicatorNode.setVisible( enabled );
            }
        } );
    }
}
