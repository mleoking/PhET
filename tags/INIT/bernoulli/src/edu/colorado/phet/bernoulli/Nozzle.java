/**
 * Class: Nozzle
 * Package: edu.colorado.phet.bernoulli
 * Author: Another Guy
 * Date: Sep 29, 2003
 */
package edu.colorado.phet.bernoulli;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

import java.awt.geom.Point2D;

public class Nozzle extends SimpleObservable {

    private Point2D.Double pivot;
    private double thickness;
    private double length;
    private double angle;   // In radian, with 0 at the positive X axis.
    private DropAdder dropAdder;
    private int dropAddModulo;
    private NozzleModelElement modelElement;
    private double inputPressure;

    public Nozzle( Point2D.Double pivot, double length, double angle, DropAdder dropAdder, int dropAddModulo ) {
        this.pivot = pivot;
        this.length = length;
        this.angle = angle;
        this.dropAdder = dropAdder;
        this.dropAddModulo = dropAddModulo;
        this.modelElement = new NozzleModelElement();
    }

    public Point2D.Double getPivot() {
        return pivot;
    }

    public Point2D.Double getOutlet() {
        return new Point2D.Double( pivot.getX() + length * Math.cos( angle ),
                                   pivot.getY() + length * Math.sin( angle ) );
    }

    public void setAngle( double angle ) {
        this.angle = angle;
        updateObservers();
    }

    public ModelElement getModelElement() {
        return modelElement;
    }

    public void setInputPressure( double p ) {
        this.inputPressure = p;
//        if (p == 0) {
//            System.out.println("ZeroPressure!");
//        }
    }


    //
    //
    private class NozzleModelElement extends ModelElement {
        private int numDrops;

        public void stepInTime( double dt ) {
            if( inputPressure != 0 ) {
                if( numDrops % dropAddModulo == 0 ) {
                    Point2D.Double out = getOutlet();
                    PhetVector vel = getDropVelocity();
                    Drop drop = new Drop( out.getX(), out.getY(),
                                          getDropRadius(), vel.getX(), vel.getY() );
                    dropAdder.addDrop( drop );
                    numDrops++;
                }
                else {
                    numDrops++;
                }
            }
        }

        private PhetVector getDropVelocity() {
            double v = Math.sqrt( 2 * inputPressure / Water.rho );
            return PhetVector.parseAngleAndMagnitude( angle, v );
        }

        private double getDropRadius() {
            return .1;
        }
    }
}
