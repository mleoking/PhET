// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.Laser;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipY;

/**
 * @author Sam Reid
 */
public class LaserNodeDragHandle extends PNode {
    int count = 0;

    public LaserNodeDragHandle( final ModelViewTransform transform, final Laser laser ) {
        final BufferedImage image = flipY( flipX( LightReflectionAndRefractionApplication.RESOURCES.getImage( "laser.png" ) ) );

        final SimpleObserver update = new SimpleObserver() {
            public void update() {
                removeAllChildren();
                final PNode counterClockwiseDragArrow = new PNode() {{
                    final double distance = transform.modelToViewDeltaX( laser.distanceFromOrigin.getValue() ) + image.getWidth() * 0.85;
                    final Point2D viewOrigin = transform.modelToView( 0, 0 );
                    final double v = laser.angle.getValue() * 180 / Math.PI;
                    Arc2D.Double circle = new Arc2D.Double( -distance + viewOrigin.getX(), -distance + viewOrigin.getY(), 2 * distance, 2 * distance, v, 20, Arc2D.OPEN );
                    final PhetPPath child = new PhetPPath( circle, new BasicStroke( 2 ), Color.green );
                    addChild( child );
                    setPickable( false );
                    setChildrenPickable( false );
                }};
                addChild( counterClockwiseDragArrow );
            }
        };
        laser.distanceFromOrigin.addObserver( update );
        laser.angle.addObserver( update );

        System.out.println( "transform.getTransform() = " + transform.getTransform() );

//        addChild( new PhetPPath() );
    }
}
