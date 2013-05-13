// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources;
import edu.colorado.phet.balanceandtorquestudy.common.BalanceAndTorqueSharedConstants;
import edu.colorado.phet.balanceandtorquestudy.common.model.Plank;
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

        int numTickMarks = 17;
        String[] tickMarkLabels = new String[numTickMarks];
        if ( BalanceAndTorqueSharedConstants.USE_QUARTER_METER_INCREMENTS ) {
            for ( int i = 0; i < numTickMarks; i++ ) {
                int labelValue = Math.abs( i - ( numTickMarks / 2 ) );
                tickMarkLabels[i] = labelValue == 0 ? "" : Integer.toString( labelValue );
            }
        }
        else {
            DecimalFormat format = new DecimalFormat( "0.##" );
            for ( int i = 0; i < numTickMarks; i++ ) {
                double labelValue = Math.abs( ( i - ( numTickMarks / 2 ) ) * Plank.LENGTH / ( numTickMarks + 1 ) );
                tickMarkLabels[i] = labelValue == 0 ? "" : format.format( labelValue );
            }
        }

        final TopTickMarkRulerNode rulerNode = new TopTickMarkRulerNode( mvt.modelToViewDeltaX( Plank.LENGTH - 0.5 ),
                                                                         60,
                                                                         tickMarkLabels,
                                                                         "",
                                                                         0,
                                                                         12 );
        rulerNode.setBackgroundPaint( new Color( 236, 225, 113, 100 ) );
        addChild( rulerNode );

        // Create and add the units labels.
        final PText leftUnitsLabelNode = new LabelUnitsNode() {{
            setOffset( rulerNode.getFullBoundsReference().getWidth() * 0.25 - getFullBoundsReference().width / 2,
                       rulerNode.getFullBoundsReference().getHeight() - getFullBoundsReference().height );
        }};
        rulerNode.addChild( leftUnitsLabelNode );
        final PText rightUnitsLabelNode = new LabelUnitsNode() {{
            setOffset( rulerNode.getFullBoundsReference().getWidth() * 0.75 - getFullBoundsReference().width / 2,
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
            if ( !BalanceAndTorqueSharedConstants.USE_QUARTER_METER_INCREMENTS ) {
                setText( BalanceAndTorqueStudyResources.Strings.METERS );
            }
            setFont( new PhetFont( 16 ) );
        }
    }
}
