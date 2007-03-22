package edu.colorado.phet.energyskatepark.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:16:29 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */
public class Particle1D {
    private double alpha = 0.25;
    private double velocity = 0;

    private ParametricFunction2D cubicSpline;

    //    private UpdateStrategy updateStrategy = new Verlet();
    private UpdateStrategy updateStrategy = new Euler();
//    private UpdateStrategy updateStrategy = new RK4();

    private double g;// meters/s/s
    private double mass = 1.0;//kg
//    private double totalDE = 0;

    private ArrayList listeners = new ArrayList();
    private boolean splineTop = true;
    private boolean reflect = true;

    public Particle1D( ParametricFunction2D cubicSpline, boolean splineTop ) {
        this( cubicSpline, splineTop, 9.8 );
    }

    public Particle1D( ParametricFunction2D parametricFunction2D, boolean splineTop, double g ) {
        this.cubicSpline = parametricFunction2D;
        this.splineTop = splineTop;
        this.g = g;
    }

    public boolean isReflect() {
        return reflect;
    }

    public void setReflect( boolean reflect ) {
        this.reflect = reflect;
    }

    public double getX() {
        return cubicSpline.evaluate( alpha ).getX();
    }

    public double getY() {
        return cubicSpline.evaluate( alpha ).getY();
    }

    private double getY( double alpha ) {
        return cubicSpline.evaluate( alpha ).getY();
    }

    public void setAlpha( double alpha ) {
        this.alpha = alpha;
    }

    public AbstractVector2D getSideVector() {
        AbstractVector2D vector = getCubicSpline2D().getUnitNormalVector( alpha );
        double sign = isSplineTop() ? 1.0 : -1.0;
        return vector.getInstanceOfMagnitude( sign );
    }

    public void stepInTime( double dt ) {
        double initEnergy = getEnergy();
        double initAlpha = alpha;
        double initVelocity = velocity;
//        int N = 100;//todo determine this free parameter
        int N = 30;//todo determine this free parameter
        for( int i = 0; i < N; i++ ) {
            updateStrategy.stepInTime( dt / N );
        }

//        totalDE += getNormalizedEnergyDiff( initEnergy );
//        System.out.println( "Particle1D[0]: dE=" + ( getEnergy() - initEnergy ) );
        fixEnergy( initAlpha, initVelocity, initEnergy );
//        System.out.println( "Particle1D[1]: dE=" + ( getEnergy() - initEnergy ) );
//        double dEFix = getNormalizedEnergyDiff( initEnergy );
//            System.out.println( "dEUpdate = " + dEUpdate + "\tdEFix=" + dEFix + ", totalDE=" + totalDE + ", RC=" + getRadiusOfCurvature() );

//        System.out.println( "dEUpdate = " + dEUpdate + "\tdEFix=" + dEFix + ", totalDE=" + totalDE );// + ", RC=" + getRadiusOfCurvature() );
//            System.out.println( "dEAfter = " + ( getEnergy() - initEnergy ) / initEnergy );
        //look for an adjacent location that will give the correct energy

        for( int i = 0; i < listeners.size(); i++ ) {
            Particle1DNode particle1DNode = (Particle1DNode)listeners.get( i );
            particle1DNode.update();
        }
    }

    public void addListener( Particle1DNode particle1DNode ) {
        listeners.add( particle1DNode );
    }

    public Point2D getLocation() {
        return new Point2D.Double( getX(), getY() );
    }

    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
    }

    public UpdateStrategy createVerlet() {
        return new Verlet();
    }

    public UpdateStrategy createConstantVelocity() {
        return new ConstantVelocity();
    }

    public void setVelocity( double v ) {
        this.velocity = v;
    }

    public UpdateStrategy createEuler() {
        return new Euler();
    }

    public double getEnergy( double alpha, double velocity ) {
        return 0.5 * mass * velocity * velocity - mass * g * getY( alpha );
    }

    public double getEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }

    private double getPotentialEnergy() {
        return -mass * g * getY();
    }

    private double getKineticEnergy() {
        return 0.5 * mass * velocity * velocity;
    }

    public UpdateStrategy createVerletOffset( double L ) {
        return new VerletOffset( L );
    }

    public double getAlpha() {
        return alpha;
    }

    public AbstractVector2D getVelocity2D() {
        return cubicSpline.getUnitParallelVector( alpha ).getInstanceOfMagnitude( velocity );
    }

    public void setCubicSpline2D( ParametricFunction2D spline, boolean top ) {
        cubicSpline = spline;
        this.splineTop = top;
    }

    public double getSpeed() {
        return Math.abs( velocity );
    }

    public double getMass() {
        return mass;
    }

    public AbstractVector2D getCurvatureDirection() {
        return cubicSpline.getCurvatureDirection( alpha );
    }

    public ParametricFunction2D getCubicSpline2D() {
        return cubicSpline;
    }

    public boolean isSplineTop() {
        return splineTop;
    }

    public void setGravity( double g ) {
        this.g = g;
    }

    public interface UpdateStrategy {
        void stepInTime( double dt );
    }

    private void handleBoundary() {
        if( reflect ) {
            clampAndBounce();
        }
    }

    private void clampAndBounce() {
        alpha = MathUtil.clamp( 0, alpha, 1.0 );

        if( alpha <= 0 ) {
            velocity *= -1;
        }
        if( alpha >= 1 ) {
            velocity *= -1;
        }
    }

    private double getScore( double alpha0, double v0, double e0, double alpha1, double v1, double e1 ) {
//        double a = 0.01;
//        double b = 0.1;
//        double a = 0.0;
//        double b = 0.0;
        double a = 0.1;
        double b = 1;
        return a * Math.pow( alpha1 - alpha0, 2 ) + b * Math.pow( v1 - v0, 2 ) + Math.pow( e1 - e0, 2 );
    }

    private void fixEnergy( double alpha0, double v0, final double e0 ) {
//        gradientDescentSearch( alpha0, v0, e0 );

//        fixEnergyVelocity( e0 );
        testFixEnergy( alpha0, v0, e0 );
    }

    private void testFixEnergy( double alpha0, double v0, double e0 ) {
        double dE = getEnergy() - e0;
        if( Math.abs( dE ) < 1E-6 ) {
            //small enough
        }
        if( getEnergy() > e0 ) {
            System.out.println( "Energy too high" );
            //can we reduce the velocity enough?
            //amount we could reduce the energy if we deleted all the kinetic energy:
            double deKE = getKineticEnergy();
            if( Math.abs( deKE ) > Math.abs( dE ) ) {
                System.out.println( "Could fix all energy by changing velocity." );
                for( int i = 0; i < 100; i++ ) {
                    double dv = ( getEnergy() - e0 ) / ( mass * velocity );
                    velocity -= dv;
                }
                System.out.println( "changed velocity: dE=" + ( getEnergy() - e0 ) );
            }
            else {
                System.out.println( "Not enough KE to fix with velocity alone" );
            }
        }
        else {
            System.out.println( "Energy too low" );
            //increasing the kinetic energy
            //what should the velocity be..?
            double vSq = Math.abs( 2 / mass * ( e0 - getPotentialEnergy() ) );
            double v=Math.sqrt( vSq );
//            this.velocity = Math.sqrt( Math.abs( 2 * dE / mass ) ) * MathUtil.getSign( velocity );
            this.velocity = v * MathUtil.getSign( velocity );
            System.out.println( "Set velocity to match energy, when energy was low: " );
            System.out.println( "INC changed velocity: dE=" + ( getEnergy() - e0 ) );
        }
    }

    private void fixEnergyVelocity( double e0 ) {
        if( getEnergy() > e0 ) {//gained energy
//            System.out.println( "Gained energy..." );
            for( int i = 0; i < 100; i++ ) {
//                System.out.println( "dE="+(getEnergy( )-e0) );
                double dv = ( getEnergy() - e0 ) / ( mass * velocity );
                velocity -= dv;
            }
            if( ( getEnergy() - e0 ) > 1E-8 ) {
                //couldn't fix with velocity
                //try position
                System.out.println( "couldn't fix with velocity: dE=" + ( getEnergy() - e0 ) );
            }

        }
        else {
            System.out.println( "lost energy..., speeding up" );

            for( int i = 0; i < 10; i++ ) {
                System.out.println( "dE=" + ( getEnergy() - e0 ) );
                double dv = ( getEnergy() - e0 ) / ( mass * velocity );
                velocity += dv;
            }
            System.out.println( "dE=" + ( getEnergy() - e0 ) );
        }
    }

    private void gradientDescentSearch( double alpha0, double v0, double e0 ) {
        double initScore = getScore( alpha0, v0, e0, alpha, velocity, getEnergy() );
//        System.out.println( "initScore = " + initScore );//try to minimize score
        double dAlpha = 1E-6;
        double dV = 1E-6;
        double learningRate = 1E-2;
        double learningRateV = learningRate;
        System.out.println( "Start score: = " + getScore( alpha0, v0, e0, alpha, velocity, getEnergy() ) );
        double startScore = getScore( alpha0, v0, e0, alpha, velocity, getEnergy() );
        double prevScore = startScore;
        int climbcount = 0;
        for( int i = 0; i < 500; i++ ) {

//            System.out.println( "Start score: = " + getScore( alpha0, v0, e0, alpha, velocity, getEnergy() ) );
            double gradx = ( getScore( alpha0, v0, e0, alpha + dAlpha / 2, velocity, getEnergy( alpha + dAlpha / 2, velocity ) ) - getScore( alpha0, v0, e0, alpha - dAlpha / 2, velocity, getEnergy( alpha - dAlpha / 2, velocity ) ) ) / dAlpha;

            double da = learningRate * gradx;
            if( Math.abs( da ) > 1 ) {
                System.out.println( "da = " + da );
            }
            da = maxAbs( da, 0.01 );
            alpha -= da;
//            System.out.println( "After X Step: = " + getScore( alpha0, v0, e0, alpha, velocity, getEnergy() ) );

            double gradV = ( getScore( alpha0, v0, e0, alpha, velocity + dV / 2, getEnergy( alpha, velocity + dV / 2 ) ) - getScore( alpha0, v0, e0, alpha, velocity - dV / 2, getEnergy( alpha, velocity - dV / 2 ) ) ) / dV;
            double dv = learningRateV * gradV;
            dv = maxAbs( dv, 0.01 );
            velocity -= dv;

//            System.out.println( "After V Step: = " + getScore( alpha0, v0, e0, alpha, velocity, getEnergy() ) );
            double score = getScore( alpha0, v0, e0, alpha, velocity, getEnergy() );
            if( score > prevScore ) {
                System.out.println( "Score climbed: " + climbcount );
                climbcount++;
                if( climbcount > 50 ) {
                    System.out.println( "Climbcount>50" );
                    break;
                }
            }
//            if( Math.abs( getEnergy() - e0 ) < 1E-6 ) {
//                System.out.println( "Finished search, score=" + getScore( alpha0, v0, e0, alpha, velocity, getEnergy() ) );
//                break;
//            }
            if( i % 1 == 0 ) {
                System.out.println( "Searching i=" + i + ", score=" + getScore( alpha0, v0, e0, alpha, velocity, getEnergy() ) );
            }
            prevScore = score;
        }
    }

    double maxAbs( double variable, double magnitude ) {
        if( Math.abs( variable ) > Math.abs( magnitude ) ) {
            return Math.abs( magnitude ) * MathUtil.getSign( variable );
        }
        else {
            return variable;
        }
    }

    double getRadiusOfCurvature() {
//        double epsilon = 0.001;
        //        double curvatureAlpha = ( cubicSpline.getAngle( alpha - epsilon ) - cubicSpline.getAngle( alpha + epsilon ) ) / epsilon;
//        return 1.0 / curvatureAlpha;

        double epsilon = 0.001;
        double a0 = alpha + cubicSpline.getFractionalDistance( alpha, -epsilon / 2.0 );
        double a1 = alpha + cubicSpline.getFractionalDistance( alpha, epsilon / 2.0 );
        double d = cubicSpline.evaluate( a0 ).distance( cubicSpline.evaluate( a1 ) );
        double curvature = ( cubicSpline.getAngle( a0 ) - cubicSpline.getAngle( a1 ) ) / d;
        return 1.0 / curvature;
    }

    public AbstractVector2D getUnitParallelVector() {
        return cubicSpline.getUnitParallelVector( alpha );
    }

    public AbstractVector2D getUnitNormalVector() {
        return cubicSpline.getUnitNormalVector( alpha );
    }

    public class VerletOffset implements UpdateStrategy {
        private double L;

        public VerletOffset( double l ) {
            this.L = l;
        }

        public void stepInTime( double dt ) {
            double R = getRadiusOfCurvature();
            double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
//                double aOrig = g * Math.cos( origAngle );
            double aOrig = Math.pow( R / ( R + L ), 2 ) * g * Math.cos( origAngle ) * ( 1 + L / R );
            double ds = velocity * dt - 0.5 * aOrig * dt * dt;

            alpha += cubicSpline.getFractionalDistance( alpha, ds );
            double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
//                double accel = g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0;
            double accel = Math.pow( R / ( R + L ), 2 ) * g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2 * ( 1 + L / R );
            velocity = velocity + accel * dt;

            handleBoundary();
        }

        public void setL( double offsetDistance ) {
            this.L = offsetDistance;
        }
    }

    public class Verlet implements UpdateStrategy {

        public void stepInTime( double dt ) {
            double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
            double ds = velocity * dt - 0.5 * g * Math.cos( origAngle ) * dt * dt;

            alpha += cubicSpline.getFractionalDistance( alpha, ds );
            double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
            velocity = velocity + g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0 * dt;

            handleBoundary();
        }
    }

    public class ConstantVelocity implements UpdateStrategy {

        public void stepInTime( double dt ) {
            alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt );

            handleBoundary();
        }
    }

    /*
    Returns the net force (discluding normal forces).
     */
    public Vector2D getNetForce() {
        return new Vector2D.Double( 0, mass * g );
    }

    public class Euler implements UpdateStrategy {

        public void stepInTime( double dt ) {
            Vector2D netForce = getNetForce();
            double a = cubicSpline.getUnitParallelVector( alpha ).dot( netForce );
            alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt + 1 / 2 * a * dt * dt );
            velocity += a * dt;

            handleBoundary();
        }
    }

    public class RK4 implements UpdateStrategy {

        public void stepInTime( double dt ) {
            double state[] = new double[]{alpha, velocity};
            edu.colorado.phet.energyskatepark.model.RK4.Diff diffy = new edu.colorado.phet.energyskatepark.model.RK4.Diff() {
                public void f( double t, double state[], double F[] ) {
                    F[0] = state[1];
                    double parallelForce = cubicSpline.getUnitParallelVector( alpha ).dot( getNetForce() );
                    F[1] = parallelForce / mass;
                }
            };
            edu.colorado.phet.energyskatepark.model.RK4.rk4( 0, state, dt, diffy );
            alpha = state[0];
            velocity = state[1];

            handleBoundary();
        }
    }

}
