// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.model.Thermometer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Thermometer node that the user can drag around and that updates its
 * temperature reading based on the reading from the supplied model element.
 *
 * @author John Blanco
 */
public class MovableThermometerNode extends SensingThermometerNode {

    public MovableThermometerNode( final Thermometer thermometer, final ModelViewTransform mvt ) {
        super( thermometer );

        // Update the offset if and when the model position changes.
        thermometer.position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D position ) {
                setOffset( mvt.modelToViewX( position.getX() ),
                           mvt.modelToViewY( position.getY() ) - ( getFullBoundsReference().height / 2 + triangleTipOffset.getHeight() ) );
            }
        } );

        // Add the drag handler.
        Vector2D offsetPosToCenter = new Vector2D( getFullBoundsReference().getCenterX() - mvt.modelToViewX( thermometer.position.get().getX() ),
                                                   getFullBoundsReference().getCenterY() - mvt.modelToViewY( thermometer.position.get().getY() ) );
        addInputEventListener( new ThermalElementDragHandler( thermometer, this, mvt, new ThermometerLocationConstraint( mvt, this, offsetPosToCenter ) ) );
    }

    // Class that constrains the valid locations for a thermometer.
    private static class ThermometerLocationConstraint implements Function1<Point2D, Point2D> {

        private final Rectangle2D modelBounds;

        private ThermometerLocationConstraint( ModelViewTransform mvt, PNode node, Vector2D offsetPosToNodeCenter ) {

            Dimension2D nodeSize = new PDimension( node.getFullBoundsReference().width, node.getFullBoundsReference().height );

            // Calculate the bounds based on the stage size of the canvas and
            // the nature of the provided node.
            double boundsMinX = mvt.viewToModelX( nodeSize.getWidth() / 2 - offsetPosToNodeCenter.getX() );
            double boundsMaxX = mvt.viewToModelX( EFACIntroCanvas.STAGE_SIZE.getWidth() - nodeSize.getWidth() / 2 - offsetPosToNodeCenter.getX() );
            double boundsMinY = mvt.viewToModelY( EFACIntroCanvas.STAGE_SIZE.getHeight() - offsetPosToNodeCenter.getY() - nodeSize.getHeight() / 2 );
            double boundsMaxY = mvt.viewToModelY( -offsetPosToNodeCenter.getY() + nodeSize.getHeight() / 2 );
            modelBounds = new Rectangle2D.Double( boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY );
        }

        public Point2D apply( Point2D proposedModelPos ) {
            double constrainedXPos = MathUtil.clamp( modelBounds.getMinX(), proposedModelPos.getX(), modelBounds.getMaxX() );
            double constrainedYPos = MathUtil.clamp( modelBounds.getMinY(), proposedModelPos.getY(), modelBounds.getMaxY() );
            return new Point2D.Double( constrainedXPos, constrainedYPos );
        }
    }
}
