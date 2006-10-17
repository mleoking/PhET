/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 28, 2005
 * Time: 10:47:11 PM
 * Copyright (c) Sep 28, 2005 by Sam Reid
 */

public abstract class ForceMode implements UpdateMode, Derivable {
    private Vector2D.Double netForce;

    public ForceMode() {
        this.netForce = new Vector2D.Double();
    }

    public void setNetForce( AbstractVector2D netForce ) {
        this.netForce = new Vector2D.Double( netForce );
    }

    protected Vector2D.Double getNetForce() {
        return netForce;
    }

    public void stepInTime( Body body, double dt ) {
        updateRK4( body, dt );
    }

    private void updateRK4( final Body body, double dt ) {
        double y[] = new double[]{body.getAttachPoint().getY(), body.getVelocity().getY()};
        RK4.Diff diffy = new RK4.Diff() {
            public void f( double t, double y[], double F[] ) {
                F[0] = y[1];
                F[1] = getNetForce().getY() / body.getMass();
            }
        };
        RK4.rk4( 0, y, dt, diffy );

        double x[] = new double[]{body.getAttachPoint().getX(), body.getVelocity().getX()};
        RK4.Diff diffx = new RK4.Diff() {
            public void f( double t, double x[], double F[] ) {
                F[0] = x[1];
                F[1] = getNetForce().getX() / body.getMass();
            }
        };
        RK4.rk4( 0, x, dt, diffx );

        body.setAttachmentPointPosition( x[0], y[0] );
        body.setVelocity( x[1], y[1] );
    }

//    double oldY;
//    double oldV;
//
//    private void updateJSCILeapfrog( EnergyConservationModel model, final Body body, double dt ) {
//
//        double[]v = new double[3];
//        v[0] = oldV;
//        v[1] = body.getVelocity().getY();
//        NumericalMath.leapFrog( v, new Mapping() {
//            public double map( double v ) {
//                return 9.8 * 8;
//            }
//        }, dt );
//
//        body.setVelocity( 0, v[2] );
//
//        double[]y = new double[3];
//        y[0] = oldY;
//        y[1] = body.getY();
//        NumericalMath.leapFrog( y, new Mapping() {
//            public double map( double x ) {
//                return body.getVelocity().getY();
//            }
//        }, dt );
//        body.setPosition( body.getX(), y[2] );
//        this.oldY = body.getY();
//        this.oldV = body.getVelocity().getY();
//    }

//    private void updateJSCiRK4( EnergyConservationModel model, final Body body, double dt ) {
//        double[]v = new double[2];
//        v[0] = body.getVelocity().getY();
//        NumericalMath.rungeKutta4( v, new Mapping() {
//            public double map( double v ) {
//                return getNetForce().getY();
//            }
//        }, dt );
//
//        body.setVelocity( 0, v[1] );
//
//        double[]y = new double[2];
//        y[0] = body.getY();
//        NumericalMath.rungeKutta4( y, new Mapping() {
//            public double map( double x ) {
//                return body.getVelocity().getY();
//            }
//        }, dt );
//        body.setPosition( body.getX(), y[1] );
//    }

    private void updateFlanRK( Body body, double dt ) {
        double y = body.getY();
        double t = 0;
        double tFinal = t + dt;
        DerivFunction derivFunction = new DerivFunction() {
            public double deriv( double x, double y ) {
                double dydt = 9.8;//
//                double dydx = a * x * Math.sqrt( 1.0 - y * y );
                return dydt;
            }
        };
        double yFinal = FlanaganRK4th.fourthOrder( derivFunction, t, y, tFinal, dt );
        body.setVelocity( body.getVelocity().getX(), yFinal );
    }

    static interface Diff {
        double diff( double val );
    }

    private void updateTechRK( Body body, double dt ) {
        double xNew = integrate( body.getX(), body.getVelocity().getX(), dt, new Diff() {
            public double diff( double val ) {
                return getNetForce().getX();
            }
        } );
        double[] pos = new double[]{body.getX(), body.getY()};
        double[] vel = new double[]{body.getVelocity().getX(), body.getVelocity().getY()};
        System.out.println( "ORIGbody.getVelocity() = " + body.getVelocity() );
        JavaTechRK4th.step( 0, dt, pos, vel, this );
        AbstractVector2D acceleration = getNetForce().getScaledInstance( 1.0 / body.getMass() );

//        double []velocity = new double[]{body.getVelocity().getX(), body.getVelocity().getY()};
//        double []acc = new double[]{body.getAcceleration().getX(), body.getAcceleration().getY()};
//        JavaTechRK4th.step( 0, dt, velocity, acc, new Derivable() {
//            public double deriv( int i, double pos, double v, double t ) {
//                return 0;
//            }
//        } );
        double vx = ( pos[0] - body.getX() ) / dt;
        double vy = ( pos[1] - body.getY() ) / dt;

        body.setState( acceleration, new Vector2D.Double( vx, vy ), new Point2D.Double( pos[0], pos[1] ) );
        System.out.println( "FINabody.getVelocity() = " + body.getVelocity() );
//        t += dt;
    }

    private double integrate( double x, double v, double dt, Diff diff ) {
        double[] rkX = new double[]{x};
        double[] rkV = new double[]{v};
        JavaTechRK4th.step( 0, dt, rkX, rkV, new Derivable() {
            public double deriv( int i, double pos, double v, double t ) {
                return 0;
            }
        } );
        return 0;
    }

    public void updateEuler( Body body, double dt ) {
        AbstractVector2D acceleration = getNetForce().getScaledInstance( 1.0 / body.getMass() );
        AbstractVector2D velocity = body.getVelocity().getAddedInstance( acceleration.getScaledInstance( dt ) );
        Point2D newPosition = new Point2D.Double( body.getX() + velocity.getX() * dt, body.getY() + velocity.getY() * dt );
        body.setState( acceleration, velocity, newPosition );
//        t += dt;
    }

    public double deriv( int i, double var, double vel, double t ) {
        if( i == 0 ) { // x variable
            return netForce.getX();
        }
        else {// y variable
//            System.out.println( "netForce.getY() = " + netForce.getY() );
//            return netForce.getY()/100;
//            return 9.8;
            return netForce.getY();
        }
    }
}
