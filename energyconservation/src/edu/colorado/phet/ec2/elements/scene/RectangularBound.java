/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.scene;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.elements.car.Car;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jul 13, 2003
 * Time: 10:04:22 AM
 * Copyright (c) Jul 13, 2003 by Sam Reid
 */
public class RectangularBound extends ModelElement {
    Rectangle2D.Double rect;
    EC2Module module;
    private PhetVector normal;
    double coefficientOfRestitution = .2;

    public double getHeight() {
        return rect.height;
    }

    public RectangularBound( Rectangle2D.Double rect, EC2Module module, PhetVector normal ) {
        this.rect = rect;
        this.module = module;
        this.normal = normal;
    }

    public Rectangle2D.Double getRectangle() {
        return rect;
    }

    public void stepInTime( double dt ) {
        if( intersects() ) {
            double carEnergy = module.getCar().getMechanicalEnergy();
            Car car = module.getCar();
//            PhetVector vel = car.getVelocityVector();
            PhetVector initialVelocity = car.getVelocityVector();
            double vperpInit = normal.dot( initialVelocity );

            double vPerpFinal = -vperpInit * coefficientOfRestitution;//car.getCoefficientOfRestitution();

            PhetVector perp = normal.getScaledInstance( vPerpFinal );
            PhetVector directionVector = normal.getNormalVector();
            double vparallel = normal.getCrossProductMagnitude( initialVelocity );
            PhetVector parallel = directionVector.getScaledInstance( vparallel );
            PhetVector finalVelocity = perp.getAddedInstance( parallel );

            PhetVector candidatePosition = car.getPosition();
            PhetVector dx = normal.getScaledInstance( .001 );
            while( intersects( candidatePosition ) ) {
                //move it out.
                candidatePosition.add( dx );
            }
            car.setPosition( candidatePosition.getX(), candidatePosition.getY() );
            car.setVelocity( finalVelocity.getX(), finalVelocity.getY() );
            double c2 = car.getMechanicalEnergy();
            double fric = carEnergy - c2;
            car.addFriction( fric );
            EC2Module.setMessage( "Crash!", 1000 );
        }
    }

    private boolean intersects( PhetVector position ) {
        Rectangle2D carrect = module.getCar().getRectangle( position );
        return rect.intersects( carrect );
    }

    private boolean intersects() {
//        Shape carshape=module.getCar().getShape();
        //This is much faster.
        Rectangle2D.Double carrect = module.getCar().getRectangle();
        return rect.intersects( carrect );
    }
}
