// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents a ruler that sits on the bottom of the plank and
 * rotates as the plank rotates.
 *
 * @author John Blanco
 */
public class RotatingRulerNode extends PNode {

    public RotatingRulerNode( final Plank plank, final ModelViewTransform mvt, BooleanProperty visibleProperty ) {
        final TopTickMarkRulerNode rulerNode = new TopTickMarkRulerNode( mvt.modelToViewDeltaX( Plank.LENGTH - 0.5 ),
                                                                         60,
                                                                         new String[] { "2", "1.75", "1.5", "1.25", "1", "0.75", "0.5", "0.25", "", "0.25", "0.5", "0.75", "1", "1.25", "1.5", "1.75", "2" },
                                                                         "",
                                                                         0,
                                                                         12 );
        rulerNode.setBackgroundPaint( new Color( 236, 225, 113, 100 ) );
        addChild( rulerNode );
        // Create and add the units labels.
        final PText leftUnitsLabelNode = new LabelUnitsNode() {{
            setOffset( rulerNode.getFullBoundsReference().getWidth() / 3 - getFullBoundsReference().width / 2,
                       rulerNode.getFullBoundsReference().getHeight() - getFullBoundsReference().height );
        }};
        rulerNode.addChild( leftUnitsLabelNode );
        final PText rightUnitsLabelNode = new LabelUnitsNode() {{
            setOffset( 2 * rulerNode.getFullBoundsReference().getWidth() / 3 - getFullBoundsReference().width / 2,
                       rulerNode.getFullBoundsReference().getHeight() - getFullBoundsReference().height );
        }};
        rulerNode.addChild( rightUnitsLabelNode );

        // Add a dividing line at the center of the ruler to make it look like
        // it is actually two rulers connected in the middle.
        DoubleGeneralPath dividingLineShape = new DoubleGeneralPath( rulerNode.getFullBoundsReference().width / 2, 0 ) {{
            // Note: There is a tweak factor in here that is dependent on the
            // stroke width used on the ruler.
            lineTo( rulerNode.getFullBoundsReference().width / 2, rulerNode.getFullBoundsReference().height - 2 );
        }};
        rulerNode.addChild( new PhetPPath( dividingLineShape.getGeneralPath(), new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), Color.BLACK ) );

        // Observe visibility property.
        visibleProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
        // Position the ruler so that it appears to be stuck to the bottom of
        // the plank, and rotates as the plank rotates.
        plank.bottomCenterPoint.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D centerBottomOfPlank ) {
                rulerNode.setRotation( 0 );
                centerFullBoundsOnPoint( mvt.modelToViewX( centerBottomOfPlank.getX() ),
                                         mvt.modelToViewY( centerBottomOfPlank.getY() ) + getFullBoundsReference().height / 2 );
                Point2D rotationPoint = new Point2D.Double( rulerNode.getFullBoundsReference().getWidth() / 2, 0 );
                rulerNode.rotateAboutPoint( -plank.getTiltAngle(), rotationPoint );
            }
        } );
    }

    private static class LabelUnitsNode extends PText {
        private LabelUnitsNode() {
            setText( BalanceAndTorqueResources.Strings.METERS );
            setFont( new PhetFont( 16 ) );
        }
    }
}
