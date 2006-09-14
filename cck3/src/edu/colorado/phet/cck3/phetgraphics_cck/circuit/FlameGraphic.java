/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit;

import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.components.CircuitComponentImageGraphic;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetImageGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 12:10:55 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class FlameGraphic extends PhetImageGraphic {
    CircuitComponent component;
    BufferedImage flameImage;
    private ModelViewTransform2D transform;
    private double fireAspectRatio = 1.3;
    private SimpleObserver componentObserver;
    private TransformListener transformListener;

    public FlameGraphic( Component parent, CircuitComponent branch, BufferedImage flameImage, ModelViewTransform2D transform ) {
        super( parent, flameImage );
        this.component = branch;
        this.flameImage = flameImage;
        this.transform = transform;
        componentObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        component.addObserver( componentObserver );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );
        changed();
        setVisible( true );
    }

    private void changed() {
        Point2D srcpt = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        ImmutableVector2D vec = new ImmutableVector2D.Double( srcpt, dstpt );
        AbstractVector2D normal = vec.getNormalVector();
        double fracDistToStart = .2;
        srcpt = vec.getScaledInstance( fracDistToStart ).getDestination( srcpt );
        dstpt = vec.getScaledInstance( 1 - fracDistToStart ).getDestination( srcpt );
        srcpt = normal.getInstanceOfMagnitude( flameImage.getHeight() * .25 ).getDestination( srcpt );
        dstpt = normal.getInstanceOfMagnitude( flameImage.getHeight() * .25 ).getDestination( dstpt );
        double dist = component.getStartJunction().getPosition().distance( component.getEndJunction().getPosition() );
        double length = component.getLength();
        double diff = Math.abs( length - dist );

        if( diff > Double.parseDouble( "1E-10" ) ) {
            throw new RuntimeException( "Components moved to a weird place, Dist between junctions=" + dist + ", Requested Length=" + length + ", diff=" + diff );
        }

        double newHeight = fireAspectRatio * srcpt.distance( dstpt );
        AffineTransform at = CircuitComponentImageGraphic.createTransform( flameImage.getWidth(), flameImage.getHeight(),
                                                                           srcpt, dstpt, newHeight );
        super.setTransform( at );
    }

    public Branch getBranch() {
        return component;
    }

    public void delete() {
        transform.removeTransformListener( transformListener );
        component.removeObserver( componentObserver );
    }

}
