/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.CircuitComponentImageGraphic;
import edu.colorado.phet.cck3.model.components.Switch;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;
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
 * Date: Jun 10, 2004
 * Time: 12:54:18 PM
 * Copyright (c) Jun 10, 2004 by Sam Reid
 */
public class LeverGraphic extends PhetImageGraphic {
    private CircuitComponentImageGraphic baseGraphic;
    private double length;
    private double height;
    private ModelViewTransform2D mvtransform;
    public static final double OPEN_ANGLE = 4.19;//open
    public static final double CLOSED_ANGLE = Math.PI * 2;//closed

    double relativeAngle = OPEN_ANGLE;
    private Point2D modelPivot;
    private Point2D viewPivot;
    private Switch aSwitch;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;

    public LeverGraphic( CircuitComponentImageGraphic baseGraphic, BufferedImage image, Component parent, ModelViewTransform2D transform, double length, double height ) {
        super( parent, image );
        this.mvtransform = transform;
        this.baseGraphic = baseGraphic;
        this.length = length;
        this.height = height;
        this.aSwitch = (Switch)baseGraphic.getCircuitComponent();
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        baseGraphic.getCircuitComponent().addObserver( simpleObserver );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );

        Switch swit = aSwitch;
        if( swit.isClosed() ) {
            relativeAngle = CLOSED_ANGLE;
        }
        else {
            relativeAngle = OPEN_ANGLE;
        }
        changed();
        setVisible( true );
    }

    private void changed() {
        Point2D baseSrc = baseGraphic.getCircuitComponent().getStartJunction().getPosition();
        Point2D baseDst = baseGraphic.getCircuitComponent().getEndJunction().getPosition();

        AbstractVector2D baseVector = new Vector2D.Double( baseSrc, baseDst );
        double angle = baseVector.getAngle();
        AbstractVector2D leverDir = Vector2D.Double.parseAngleAndMagnitude( length, angle + relativeAngle );

        AbstractVector2D to = baseVector.getScaledInstance( .3 );
        Point2D modelDst = to.getDestination( baseSrc );
        Point2D modelSrc = leverDir.getDestination( modelDst );

        int w0 = getImage().getWidth();
        int h0 = getImage().getHeight();
        this.modelPivot = modelSrc;
        Point2D viewSrc = mvtransform.modelToView( modelSrc );
        Point2D viewDst = mvtransform.modelToView( modelDst );
        this.viewPivot = viewDst;
        double newHeight = mvtransform.modelToViewDifferentialY( height );
        AffineTransform tx = CircuitComponentImageGraphic.createTransform( w0, h0, viewSrc, viewDst, newHeight );
        setTransform( tx );
    }

    public CircuitComponentImageGraphic getBaseGraphic() {
        return baseGraphic;
    }

    public Point2D getPivotViewLocation() {
        return viewPivot;
    }

    public void setRelativeAngle( double angle ) {
        while( angle < 0 ) {
            angle += Math.PI * 2;
        }
        while( angle > Math.PI * 2 ) {
            angle -= Math.PI * 2;
        }
        if( angle < 2.53 ) {
            angle = CLOSED_ANGLE;
        }
        else if( angle < 4.1 ) {
            angle = OPEN_ANGLE;
        }
//        System.out.println( "angle = " + angle );
        if( angle >= OPEN_ANGLE && angle <= CLOSED_ANGLE ) {
            double oldAngle = relativeAngle;
            if( oldAngle != angle ) {
                this.relativeAngle = angle;
                changed();
                if( angle == CLOSED_ANGLE && !aSwitch.isClosed() ) {
                    aSwitch.setClosed( true );
                }
                else if( angle != CLOSED_ANGLE && aSwitch.isClosed() ) {
                    aSwitch.setClosed( false );
                }
            }
        }
    }

    public void delete() {
        this.mvtransform.removeTransformListener( transformListener );
        baseGraphic.getCircuitComponent().removeObserver( simpleObserver );
    }
}
