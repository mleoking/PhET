// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.background;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * This node is intended for use as a background on a tab, and shows the ground on the
 * bottom and the sky on the top.
 * <p/>
 * This assumes that the horizon is at Y=0.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class OutsideBackgroundNode extends PNode {

    /**
     * Convenience constructor that assumes some typical values for the overall size of the earth and sky.
     *
     * @param mvt
     * @param skyGradientTopY       top of the gradient in model coordinates.
     * @param groundGradientBottomY bottom of the gradient in model coordinates.
     */
    public OutsideBackgroundNode( ModelViewTransform mvt, double skyGradientTopY, double groundGradientBottomY ) {
        this( mvt, skyGradientTopY, groundGradientBottomY, new Rectangle2D.Double( -1000, -2000, 2000, 4000 ) );
    }

    /**
     * Primary constructor.
     *
     * @param mvt
     * @param skyGradientTopY
     * @param skyGradientTopY       top of the gradient in model coordinates.
     * @param groundGradientBottomY bottom of the gradient in model coordinates.
     */
    public OutsideBackgroundNode( ModelViewTransform mvt, double skyGradientTopY, double groundGradientBottomY, Rectangle2D modelBounds ) {
        if ( modelBounds.getMinY() < 0 ) {
            // Add the ground first, because we're earthy people.
            addChild( new GroundNode( mvt, new Rectangle2D.Double( modelBounds.getX(), modelBounds.getMinY(), modelBounds.getWidth(), -modelBounds.getMinY() ), groundGradientBottomY ) );
        }
        if ( modelBounds.getMaxY() > 0 ) {
            // Add the sky.
            addChild( new SkyNode( mvt, new Rectangle2D.Double( modelBounds.getX(), 0, modelBounds.getWidth(), modelBounds.getMaxY() ), skyGradientTopY ) );
        }
    }
}
