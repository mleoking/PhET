/**
 * Class: HardsphereCollision
 * Package: edu.colorado.phet.lasers.physics
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;

// TODO: Make the whole class static

public abstract class HardsphereCollision implements Collision {

    static private Vector2D loaHat = new Vector2D.Double();
    static private Vector2D tangentUnit = new Vector2D.Double();
    static private Vector2D dVA = new Vector2D.Double();

//    public void collide( CollidableBody bodyA, CollidableBody bodyB,
//                         Vector2D loa, double dt, IdealGasModel model ) {
//        double timeStep = dt;
//
//        // Compute the actual time of collision. This is, in general, prior to the end of the current time step.
//        // If t1 < 0, that means the objects were already overlapping at the beginning of the time step. If it is
//        // 0 < t1 <= dt, it means the bodies came into contact during the time step.
//        double t1 = computeT1( bodyA, bodyB, dt );
//
//        // If t1 doesn't make sense, then punt
//        if( Double.isNaN( t1 ) || t1 < 0 || t1 > timeStep ) {
//            return;
//        }
//
//        // Step the bodies back to the time of the actual collision
////                bodyA.stepInTimeNoNotify( t1 - timeStep );
////                bodyB.stepInTimeNoNotify( t1 - timeStep );
//
//
//        // Get the kinetic energy of the two bodies. We will have to tweak their velocities after the collision
//        // to conserve energy
//        double totalPreE = model.getBodyEnergy( bodyA )
//                           + model.getBodyEnergy( bodyB );
//
//        // Get the projection of each particleB's velocity on the line of action,
//        // and the rest of the parameters we need to compute the new velocities
//        // in that direction
//        loaHat.setX( loa.getX() );
//        loaHat.setY( loa.getY() );
//        loaHat.normalize();
//        double vA1Loa = bodyA.getVelocity().dot( loaHat );
//        double vB1Loa = bodyB.getVelocity().dot( loaHat );
//        double mA = bodyA.getMass();
//        double mB = bodyB.getMass();
//        double pA1 = mA == Double.POSITIVE_INFINITY ? Double.POSITIVE_INFINITY : vA1Loa * mA;
//        double pB1 = mB == Double.POSITIVE_INFINITY ? Double.POSITIVE_INFINITY : vB1Loa * mB;
//        double mTotal = ( mA == Double.POSITIVE_INFINITY || mB == Double.POSITIVE_INFINITY )
//                        ? Double.POSITIVE_INFINITY : mA + mB;
//
//        // Compute the velocities along the line of action. If one of the bodies has infinite mass,
//        // we assume that it is stationary
//        double vA2Loa;
//        double vB2Loa;
//        if( mB == Double.POSITIVE_INFINITY ) {
//            vA2Loa = -vA1Loa;
//            vB2Loa = 0;
//        }
//        else if( mB == Double.POSITIVE_INFINITY ) {
//            vB2Loa = -vB1Loa;
//            vA2Loa = 0;
//        }
//        else {
//            vA2Loa = ( ( pA1 + ( 2 * pB1 ) - ( mB * vA1Loa ) ) / mTotal );
//            vB2Loa = ( ( pB1 + ( 2 * pA1 ) - ( mA * vB1Loa ) ) / mTotal );
//        }
//
//        // HACK
//        // TODO: Fix so we don't need this hack
//        if( Double.isNaN( loaHat.getY() ) || Double.isNaN( loaHat.getX() ) ) {
//            System.out.println( "CollisionLaw.collide: loaUnit has a component that is NaN" );
//            return;
//        }
//
//        // Get the unit vector along the line tangential to the point of contact
//        tangentUnit.setX( -loaHat.getY() );
//        tangentUnit.setY( loaHat.getX() );
//
//        // Compute the velocities along the tangent
//        double vA2Tangent = bodyA.getVelocity().dot( tangentUnit );
//        double vB2Tangent = bodyB.getVelocity().dot( tangentUnit );
//
//        // Determine velocities in global coordinates
//        double xScaleTangent = tangentUnit.dot( s_xUnit );
//        double xScaleLoa = loaHat.dot( s_xUnit );
//        double yScaleTangent = tangentUnit.dot( s_yUnit );
//        double yScaleLoa = loaHat.dot( s_yUnit );
//
//        if( Double.isNaN( vA2Loa * xScaleLoa + vA2Tangent * xScaleTangent ) ) {
//            System.out.println( "xxx" );
//        }
//        if( Double.isNaN( vA2Loa * yScaleLoa + vA2Tangent * yScaleTangent ) ) {
//            System.out.println( "xxx" );
//        }
//        if( Double.isNaN( vB2Loa * xScaleLoa + vB2Tangent * xScaleTangent ) ) {
//            System.out.println( "xxx" );
//        }
//        if( Double.isNaN( vB2Loa * yScaleLoa + vB2Tangent * yScaleTangent ) ) {
//            System.out.println( "xxx" );
//        }
//
//        bodyA.getVelocity().setX( vA2Loa * xScaleLoa + vA2Tangent * xScaleTangent );
//        bodyA.getVelocity().setY( vA2Loa * yScaleLoa + vA2Tangent * yScaleTangent );
//        bodyB.getVelocity().setX( vB2Loa * xScaleLoa + vB2Tangent * xScaleTangent );
//        bodyB.getVelocity().setY( vB2Loa * yScaleLoa + vB2Tangent * yScaleTangent );
//
//
//        // Step the bodies forward from the actual time of the collision to the end of the system clock
//        // time step
//        bodyA.stepInTime( timeStep - t1 );
//        bodyB.stepInTime( timeStep - t1 );
//
//        // Correct the kinetic energy to account for any losses or gains in the collision due
//        // to quantized time
//        double totalPostE = model.getBodyEnergy( bodyA )
//                            + model.getBodyEnergy( bodyB );
//        double dE = totalPostE - totalPreE;
//
//        if( !Double.isNaN( dE ) && dE != 0 ) {
//            float dS = (float)Math.sqrt( 2 * Math.abs( dE ) / bodyA.getMass() );
//
//            dVA.setX( bodyA.getVelocity().getX() );
//            dVA.setY( bodyA.getVelocity().getY() );
//            dVA.normalize();
//            dVA.scale( dS );
//            if( dE < 0 ) {
//                bodyA.getVelocity().add( dVA );
//            }
//            else {
//                bodyA.getVelocity().subtract( dVA );
//            }
//
//            totalPostE = model.getBodyEnergy( bodyA )
//                         + model.getBodyEnergy( bodyB );
//
//            if( totalPostE != totalPreE ) {
//                //                System.out.println( "!!!" );
//            }
//            return;
//        }
//    }

    //
    // Abstract methods
    //
    public abstract void collide();

    //
    // Static fields and methods
    //

    private static Vector2D s_xUnit = new Vector2D.Double( 1, 0 );
    private static Vector2D s_yUnit = new Vector2D.Double( 0, 1 );
    private static float[] roots = new float[2];

    /**
     * Computes the time since the beginning of the last time step when the particle
     * was at a specified y coordinate.
     * <p/>
     * The difference between this and the original computeT1 is that this one does
     * not use the bodies' previous positions. Instead, it makes the determination
     * by reversing the bodies' velocities and applying them to their current
     * positions.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
//    public static double computeT1( CollidableBody bodyA, CollidableBody bodyB, double dt ) {
//
//        double t1 = 0;
//
//        double ra = bodyA.getContactOffset( bodyB );
//        double rb = bodyB.getContactOffset( bodyA );
//
//        double vax0 = bodyA.getVelocityPrev().getX();
//        double vay0 = bodyA.getVelocityPrev().getY();
//        double vbx0 = bodyB.getVelocityPrev().getX();
//        double vby0 = bodyB.getVelocityPrev().getY();
//        double sax0 = bodyA.getPosition().getX();
//        double say0 = bodyA.getPosition().getY();
//        double sbx0 = bodyB.getPosition().getX();
//        double sby0 = bodyB.getPosition().getY();
//
//        // Compute the average velocity for the time step
//        double vaxAve = -( vax0 + bodyA.getVelocity().getX() ) / 2;
//        double vayAve = -( vay0 + bodyA.getVelocity().getY() ) / 2;
//        double vbxAve = -( vbx0 + bodyB.getVelocity().getX() ) / 2;
//        double vbyAve = -( vby0 + bodyB.getVelocity().getY() ) / 2;
//
//        double vxDiff = vaxAve - vbxAve;
//        double vyDiff = vayAve - vbyAve;
//        double sxDiff = sax0 - sbx0;
//        double syDiff = say0 - sby0;
//
//        double a = vxDiff * vxDiff + vyDiff * vyDiff;
//        double b = 2 * ( vxDiff * sxDiff + vyDiff * syDiff );
//        double c = sxDiff * sxDiff + syDiff * syDiff - ( ( ra + rb ) * ( ra + rb ) );
//
//        if( a != 0 ) {
//
//            roots = MathUtil.quadraticRoots( roots, (float)a, (float)b, (float)c );
//
//            // Find the correct root to return.
//            if( roots[0] >= 0 && roots[0] <= dt
//                && roots[1] >= 0 && roots[1] <= dt ) {
//                t1 = Math.min( roots[0], roots[1] );
//            }
//            else if( roots[0] >= 0 && roots[0] <= dt ) {
//                t1 = roots[0];
//            }
//            else if( roots[1] >= 0 && roots[1] <= dt ) {
//                t1 = roots[1];
//            }
//
//            // If both roots are > dt, then send NaN back, indicating we don't
//            // know what to do
//            else if( roots[0] > dt && roots[1] > dt ) {
//                t1 = Double.NaN;
//            }
//
//            // If both roots are negative, then the bodies were in
//            // contact at the beginning of the time step. Therefore, we
//            // will back up to the start of the time step. [???]
//            else if( roots[0] < 0 && roots[1] < 0 ) {
//                t1 = Math.max( roots[0], roots[1] );
//            }
//
//            else if( roots[0] * roots[1] < 0 && Math.min( roots[0], roots[1] ) >= -2 * dt ) {
//                t1 = Math.min( roots[0], roots[1] );
//            }
//
//            // The collision must have happened more than one time step ago
//            // We're not sure what to do. Punt!
//            else {
//                t1 = Double.NaN;
//            }
//        }
//        else if( b != 0 ) {
//            t1 = -c / b;
//        }
//        else {
//            t1 = 0;
//        }
//        return t1;
//    }
}
