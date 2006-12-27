/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.ec2.elements.car.Car;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jul 26, 2003
 * Time: 2:15:31 PM
 * Copyright (c) Jul 26, 2003 by Sam Reid
 */
public class SplineCarCollisionDetector extends ModelElement {
//    private EC2Module module;
    private Car car;
    private Spline spline;
    private long delay;
    private long lastTime;

    public SplineCarCollisionDetector( Car car, Spline spline ) {
//        this.module = module;
        this.car = car;
        this.spline = spline;
    }

    public void stepInTime( double dt ) {
        GeneralPath path = spline.getPath();
        Rectangle2D.Double carshape = car.getRectangle();
        delay = System.currentTimeMillis() - lastTime;
        if( path.intersects( carshape ) && delay > 100 ) {
//            module.getCrashAudioClip().play();
            lastTime = System.currentTimeMillis();
        }
    }

    public static boolean isCollision( Spline spline, Car car ) {
        return spline.getPath().intersects( car.getRectangle() );
    }

    public boolean isCollided() {
        GeneralPath path = spline.getPath();
        Rectangle2D.Double carshape = car.getRectangle();
        if( path.intersects( carshape ) ) {
            return true;
        }
        return false;
    }
}
