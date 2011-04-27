// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.motion.model.IPositionDriven;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:26:37 PM
 */
public class RotationPlatformDragHandler extends PBasicInputEventHandler {
    private double initAngle;
    private Point2D initLoc;
    private final IPositionDriven environment;
    private final RotationPlatform rotationPlatform;
    private PNode rotationPlatformNode;
    private static final double MIN_ANG_VEL_FLING_THRESHOLD = 0.05;

    public RotationPlatformDragHandler( PNode rotationPlatformNode, IPositionDriven environment, RotationPlatform rotationPlatform ) {
        this.rotationPlatformNode = rotationPlatformNode;
        this.environment = environment;
        this.rotationPlatform = rotationPlatform;
    }

    public void mousePressed( PInputEvent event ) {
//        resetDrag( rotationPlatformNode.getAngle(), event );
        resetDrag( rotationPlatform.getAngle().getValue(), event );//todo: used to be angle of graphic, maybe model value is incorrect
        environment.setPositionDriven();
    }

    public void mouseReleased( PInputEvent event ) {
        double angularVelocity = rotationPlatform.getAngularVelocity().getValue();

        if ( Math.abs( angularVelocity ) > MIN_ANG_VEL_FLING_THRESHOLD ) {
            rotationPlatform.setVelocityDriven();
            rotationPlatform.setVelocity( angularVelocity );
        }
    }

    public void mouseDragged( PInputEvent event ) {
        Point2D loc = event.getPositionRelativeTo( rotationPlatformNode );
        Point2D center = rotationPlatform.getCenter();
        Vector2D a = new Vector2D( center, initLoc );
        Vector2D b = new Vector2D( center, loc );
        double angleDiff = b.getAngle() - a.getAngle();
//                System.out.println( "a=" + a + ", b=" + b + ", center=" + center + ", angleDiff = " + angleDiff );

        angleDiff = clampAngle( angleDiff, -Math.PI, Math.PI );

        double angle = initAngle + angleDiff;
//                System.out.println( "angleDiff=" + angleDiff + ", angle=" + angle );
        rotationPlatform.setAngle( angle );
        resetDrag( angle, event );//have to reset drag in order to keep track of the winding number
    }

    private void resetDrag( double angle, PInputEvent event ) {
        initAngle = angle;
        initLoc = event.getPositionRelativeTo( rotationPlatformNode );
    }

    public static double clampAngle( double angle, double min, double max ) {
        if ( max <= min ) {
            throw new IllegalArgumentException( "max<=min" );
        }
        while ( angle < min ) {
            angle += Math.PI * 2;
        }
        while ( angle > max ) {
            angle -= Math.PI * 2;
        }
        return angle;
    }
}
