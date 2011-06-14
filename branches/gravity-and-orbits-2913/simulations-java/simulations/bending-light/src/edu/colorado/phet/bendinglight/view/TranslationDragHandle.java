// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.BendingLightApplication;
import edu.colorado.phet.bendinglight.model.Laser;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipY;

/**
 * Graphic that depicts how the laser may be moved.  It is only shown when the cursor is over the laser and is non-interactive.
 *
 * @author Sam Reid
 */
public class TranslationDragHandle extends PNode {

    public TranslationDragHandle( final ModelViewTransform transform, final Laser laser, final double dx, final double dy, final BooleanProperty showDragHandles ) {
        showDragHandles.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( showDragHandles.get() );
            }
        } );
        final BufferedImage image = flipY( flipX( BendingLightApplication.RESOURCES.getImage( "laser.png" ) ) );

        //Update the location when laser pivot or emission point change
        new RichSimpleObserver() {
            public void update() {
                removeAllChildren();
                final PNode counterClockwiseDragArrow = new PNode() {{
                    Point2D laserEmissionViewPoint = transform.modelToView( laser.emissionPoint.get().toPoint2D() );
                    ImmutableVector2D viewDelta = ImmutableVector2D.parseAngleAndMagnitude( image.getWidth() / 2, -laser.getAngle() );
                    addChild( new PhetPPath( new Arrow( new Point2D.Double( laserEmissionViewPoint.getX() + viewDelta.getX(), laserEmissionViewPoint.getY() + viewDelta.getY() ),
                                                        new ImmutableVector2D( dx, dy ), 20, 20, 10, 2, true ).getShape(), Color.green, new BasicStroke( 1 ), Color.black ) );
                    setPickable( false );
                    setChildrenPickable( false );
                }};
                addChild( counterClockwiseDragArrow );
            }
        }.observe( laser.pivot, laser.emissionPoint );
    }

}
