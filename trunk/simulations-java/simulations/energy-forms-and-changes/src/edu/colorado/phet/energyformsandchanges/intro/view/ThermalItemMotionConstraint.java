// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.colorado.phet.energyformsandchanges.intro.model.RectangularThermalMovableModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class defines the motion constraints for the model elements that can
 * be moved on and off the burners.  These elements are prevented from
 * overlapping one another.
 *
 * @author John Blanco
 */
public class ThermalItemMotionConstraint implements Function1<Point2D, Point2D> {

    private final Rectangle2D modelBounds;
    private final EFACIntroModel model;
    private final RectangularThermalMovableModelElement modelElement;

    public ThermalItemMotionConstraint( EFACIntroModel model, RectangularThermalMovableModelElement modelElement, PNode node,
                                        ModelViewTransform mvt, ImmutableVector2D offsetPosToNodeCenter ) {

        this.model = model;
        this.modelElement = modelElement;

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
        // Make sure the proposed position is on the stage.
        double constrainedXPos = MathUtil.clamp( modelBounds.getMinX(), proposedModelPos.getX(), modelBounds.getMaxX() );
        double constrainedYPos = MathUtil.clamp( modelBounds.getMinY(), proposedModelPos.getY(), modelBounds.getMaxY() );
        Point2D boundedToStagePos = new Point2D.Double( constrainedXPos, constrainedYPos );

        // Return a position that is also validated by the model.
        return model.validatePosition( modelElement, boundedToStagePos );
    }
}
