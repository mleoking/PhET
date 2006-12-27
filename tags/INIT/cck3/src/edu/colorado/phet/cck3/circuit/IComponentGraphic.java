/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 12:47:28 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public interface IComponentGraphic extends BoundedGraphic {
    ModelViewTransform2D getModelViewTransform2D();

    CircuitComponent getComponent();

    static class Impl {

        public static AffineTransform createTransform( ModelViewTransform2D transform, Point2D srcpt, Point2D dstpt, double getTargetWidth, double getTargetHeight, double componentHeight ) {
            srcpt = transform.toAffineTransform().transform( srcpt, null );
            dstpt = transform.toAffineTransform().transform( dstpt, null );
            double dist = srcpt.distance( dstpt );
//        System.out.println( "dist = " + dist );
            double newHeight = transform.modelToViewDifferentialY( componentHeight );

//        double newLength = transform.modelToViewDifferentialX( component.getLength() );
            double newLength = dist;
            double angle = new ImmutableVector2D.Double( srcpt, dstpt ).getAngle();
            AffineTransform trf = new AffineTransform();
            trf.rotate( angle, srcpt.getX(), srcpt.getY() );
            trf.translate( 0, -newHeight / 2 );
            trf.translate( srcpt.getX(), srcpt.getY() );
            trf.scale( newLength / getTargetWidth, newHeight / getTargetHeight );

            return trf;
        }
    }
}
