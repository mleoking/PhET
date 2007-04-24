package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.energyskatepark.model.TrackWithFriction;
import edu.colorado.phet.energyskatepark.model.TraversalState;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.timeseries.OptionalItemSerializableList;

import edu.colorado.phet.energyskatepark.model.SPoint2D;
import java.io.Serializable;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:16:29 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */
public class Particle1D implements Serializable {
    private double alpha = 0.25;
    private double velocity = 0;

    private ParametricFunction2D track;

    private UpdateStrategy updateStrategy = new Euler();

    private double g;// meters/s/s
    private double mass = 1.0;//kg

    private List listeners = new OptionalItemSerializableList();
    private boolean splineTop = true;
    private boolean reflect = true;
    private double zeroPointPotentialY = 0.0;
    private double xThrust = 0;
    private double yThrust = 0;
    private double frictionCoefficient = 0;
    private double thermalEnergy = 0;

    public Particle1D( ParametricFunction2D cubicSpline, boolean splineTop ) {
        this( cubicSpline, splineTop, 9.8 );
    }

    public Particle1D( ParametricFunction2D parametricFunction2D, boolean splineTop, double g ) {
        this.track = parametricFunction2D;
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
        return track.evaluate( alpha ).getX();
    }

    public double getY() {
        return track.evaluate( alpha ).getY();
    }

    private double getY( double alpha ) {
        return track.evaluate( alpha ).getY();
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
        int N = 10;//todo determine this free parameter
        for( int i = 0; i < N; i++ ) {
            updateStrategy.stepInTime( dt / N );
        }

//        totalDE += getNormalizedEnergyDiff( initEnergy );
//        System.out.println( "Particle1D[0]: dE=" + ( getEnergy() - initEnergy ) );
        assert ( !Double.isNaN( getEnergy() ) );
        if( getThrust().getMagnitude() == 0 ) {
            fixEnergy( initAlpha, initEnergy );
        }

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

    private Vector2D.Double getThrust() {
        return new Vector2D.Double( xThrust, yThrust );
    }

    public void addListener( Particle1DNode particle1DNode ) {
        listeners.add( particle1DNode );
    }

    public SPoint2D getLocation() {
        return new SPoint2D( getX(), getY() );
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

    private double getEnergy( double alpha, double velocity ) {
        return 0.5 * mass * velocity * velocity - mass * g * getY( alpha ) + thermalEnergy;
    }

    public double getEnergy() {
        return getKineticEnergy() + getPotentialEnergy() + thermalEnergy;
    }

    private double getPotentialEnergy() {
        return -mass * g * ( getY() - zeroPointPotentialY );
    }

    public double getKineticEnergy() {
        return 0.5 * mass * velocity * velocity;
    }

    public UpdateStrategy createVerletOffset( double L ) {
        return new VerletOffset( L );
    }

    public double getAlpha() {
        return alpha;
    }

    public AbstractVector2D getVelocity2D() {
        return track.getUnitParallelVector( alpha ).getInstanceOfMagnitude( velocity );
    }

    public void detach() {
        track = null;
    }

    public void setCubicSpline2D( ParametricFunction2D spline, boolean top, double alpha ) {
        this.track = spline;
        this.splineTop = top;
        this.alpha = alpha;
    }

    public double getSpeed() {
        return Math.abs( velocity );
    }

    public double getMass() {
        return mass;
    }

    public AbstractVector2D getCurvatureDirection() {
        return track.getCurvatureDirection( alpha );
    }

    public ParametricFunction2D getCubicSpline2D() {
        return track;
    }

    public boolean isSplineTop() {
        return splineTop;
    }

    public void setGravity( double g ) {
        this.g = g;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public void setZeroPointPotentialY( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
    }

    public void setThrust( double xThrust, double yThrust ) {
        this.xThrust = xThrust;
        this.yThrust = yThrust;
    }

    public TraversalState getTraversalState() {
        return new TraversalState( track, splineTop, alpha );
    }

    public void setFrictionCoefficient( double frictionCoefficient ) {
        this.frictionCoefficient = frictionCoefficient;
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public void setThermalEnergy( double thermalEnergy ) {
        this.thermalEnergy = thermalEnergy;
    }

    public void addThermalEnergy( double dT ) {
        setThermalEnergy( thermalEnergy + dT );
    }

    public boolean isRollerCoasterMode() {
        if( track instanceof EnergySkateParkSpline.DefaultTrackSpline ) {
            EnergySkateParkSpline.DefaultTrackSpline defaultTrackSpline = (EnergySkateParkSpline.DefaultTrackSpline)track;
            return defaultTrackSpline.isRollerCoasterMode();
        }
        return false;
    }

    public interface UpdateStrategy extends Serializable {
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

    void fixEnergy( double alpha0, final double e0 ) {
        fixEnergyHeuristic( alpha0, e0 );
    }

    private void fixEnergyHeuristic( double alpha0, double e0 ) {
        if( Double.isNaN( getEnergy() ) ) { throw new IllegalArgumentException();}
        double dE = getEnergy() - e0;
        if( Math.abs( dE ) < 1E-6 ) {
            //small enough
        }
        if( getEnergy() > e0 ) {
            verboseDebug( "Energy too high" );
            //can we reduce the velocity enough?
            if( Math.abs( getKineticEnergy() ) > Math.abs( dE ) ) {//amount we could reduce the energy if we deleted all the kinetic energy:
                verboseDebug( "Could fix all energy by changing velocity." );//todo: maybe should only do this if all velocity is not converted
                correctEnergyReduceVelocity( e0 );
                verboseDebug( "changed velocity: dE=" + ( getEnergy() - e0 ) );
                if( !MathUtil.isApproxEqual( e0, getEnergy(), 1E-8 ) ) {
                    new RuntimeException( "Energy error[0]" ).printStackTrace();
                }
            }
            else {
                verboseDebug( "Not enough KE to fix with velocity alone: normal:" + getCubicSpline2D().getUnitNormalVector( alpha ) );
                verboseDebug( "changed position alpha: dE=" + ( getEnergy() - e0 ) );
                //search for a place between alpha and alpha0 with a better energy

                int numRecursiveSearches = 10;
                double bestAlpha = ( alpha + alpha0 ) / 2.0;
                double da = ( alpha - alpha0 ) / 2;
                for( int i = 0; i < numRecursiveSearches; i++ ) {
                    int numSteps = 10;
                    bestAlpha = searchAlpha( bestAlpha - da, bestAlpha + da, e0, numSteps );
                    da = ( ( bestAlpha - da ) - ( bestAlpha + da ) ) / numSteps;
                }

                this.alpha = bestAlpha;
                verboseDebug( "changed position alpha: dE=" + ( getEnergy() - e0 ) );
                if( !MathUtil.isApproxEqual( e0, getEnergy(), 1E-8 ) ) {
                    if( Math.abs( getKineticEnergy() ) > Math.abs( dE ) ) {//amount we could reduce the energy if we deleted all the kinetic energy:
                        verboseDebug( "Fixed position some, still need to fix velocity as well." );//todo: maybe should only do this if all velocity is not converted
                        correctEnergyReduceVelocity( e0 );
                        if( !MathUtil.isApproxEqual( e0, getEnergy(), 1E-8 ) ) {
                            System.out.println( "Changed position & Velocity and still had energy error" );
                            new RuntimeException( "Energy error[123]" ).printStackTrace();
                        }
                    }
                    else {
                        System.out.println( "Changed position, wanted to change velocity, but didn't have enough to fix it..., dE=" + ( getEnergy() - e0 ) );
                        new RuntimeException( "Energy error[456]" ).printStackTrace();
                    }
                }
            }
        }
        else {
            if( Double.isNaN( getEnergy() ) ) { throw new IllegalArgumentException();}
            verboseDebug( "Energy too low" );
            //increasing the kinetic energy
            //Choose the exact velocity in the same direction as current velocity to ensure total energy conserved.
            double vSq = Math.abs( 2 / mass * ( e0 - getPotentialEnergy() - thermalEnergy ) );
            double v = Math.sqrt( vSq );
//            this.velocity = Math.sqrt( Math.abs( 2 * dE / mass ) ) * MathUtil.getSign( velocity );
            this.velocity = v * MathUtil.getSign( velocity );
            verboseDebug( "Set velocity to match energy, when energy was low: " );
            verboseDebug( "INC changed velocity: dE=" + ( getEnergy() - e0 ) );
            if( !MathUtil.isApproxEqual( e0, getEnergy(), 1E-8 ) ) {
                new RuntimeException( "Energy error[2]" ).printStackTrace();
            }
        }
    }

    boolean verbose = false;

    private void verboseDebug( String text ) {
        if( verbose ) {
            System.out.println( text );
        }
    }

    private void correctEnergyReduceVelocity( double e0 ) {
        for( int i = 0; i < 100; i++ ) {
            double dv = ( getEnergy() - e0 ) / ( mass * velocity );
            velocity -= dv;
            if( MathUtil.isApproxEqual( e0, getEnergy(), 1E-8 ) ) {
                break;
            }
        }
    }

    private double searchAlpha( double alpha0, double alpha1, double e0, int numSteps ) {
        double da = ( alpha1 - alpha0 ) / numSteps;
        double bestAlpha = ( alpha1 - alpha0 ) / 2;
        double bestDE = getEnergy( bestAlpha, velocity );
        for( int i = 0; i < numSteps; i++ ) {
            double proposedAlpha = alpha0 + da * i;
            double e = getEnergy( proposedAlpha, velocity );
            if( Math.abs( e - e0 ) <= Math.abs( bestDE ) ) {
                bestDE = e - e0;
                bestAlpha = proposedAlpha;
            }//continue to find best value closest to proposed alpha, even if several values give dE=0.0
        }
        verboseDebug( "After " + numSteps + " steps, origAlpha=" + alpha0 + ", stepAlpha=" + alpha + ", bestAlpha=" + bestAlpha + ", dE=" + bestDE );
        return bestAlpha;
    }

    double getRadiusOfCurvature() {
        double epsilon = 0.001;
        double a0 = alpha + track.getFractionalDistance( alpha, -epsilon / 2.0 );
        double a1 = alpha + track.getFractionalDistance( alpha, epsilon / 2.0 );
        double d = track.evaluate( a0 ).distance( track.evaluate( a1 ) );
        double curvature = ( track.getAngle( a0 ) - track.getAngle( a1 ) ) / d;
        return 1.0 / curvature;
    }

    public AbstractVector2D getUnitParallelVector() {
        return track.getUnitParallelVector( alpha );
    }

    public AbstractVector2D getUnitNormalVector() {
        return track.getUnitNormalVector( alpha );
    }

    public class VerletOffset implements UpdateStrategy {
        private double L;

        public VerletOffset( double l ) {
            this.L = l;
        }

        public void stepInTime( double dt ) {
            double R = getRadiusOfCurvature();
            double origAngle = Math.PI / 2 - track.getAngle( alpha );
//                double aOrig = g * Math.cos( origAngle );
            double aOrig = Math.pow( R / ( R + L ), 2 ) * g * Math.cos( origAngle ) * ( 1 + L / R );
            double ds = velocity * dt - 0.5 * aOrig * dt * dt;

            alpha += track.getFractionalDistance( alpha, ds );
            double newAngle = Math.PI / 2 - track.getAngle( alpha );
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
            double origAngle = Math.PI / 2 - track.getAngle( alpha );
            double ds = velocity * dt - 0.5 * g * Math.cos( origAngle ) * dt * dt;

            alpha += track.getFractionalDistance( alpha, ds );
            double newAngle = Math.PI / 2 - track.getAngle( alpha );
            velocity = velocity + g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0 * dt;

            handleBoundary();
        }
    }

    public class ConstantVelocity implements UpdateStrategy {

        public void stepInTime( double dt ) {
            alpha += track.getFractionalDistance( alpha, velocity * dt );

            handleBoundary();
        }
    }

    public AbstractVector2D getNormalForce() {//todo some code duplication in Particle.Particle1DUpdate
//        System.out.println( "getRadiusOfCurvature() = " + getRadiusOfCurvature() );
        if( Double.isInfinite( getRadiusOfCurvature() ) ) {
//            System.out.println( "infinite" );

            double radiusOfCurvature = 100000;
//            System.out.println( "radiusOfCurvature = " + radiusOfCurvature );
//            System.out.println( " getCurvatureDirection()  = " + getCurvatureDirection() );
            Vector2D.Double netForceRadial = new Vector2D.Double();
            netForceRadial.add( new Vector2D.Double( 0, mass * g ) );//gravity
            netForceRadial.add( new Vector2D.Double( xThrust * mass, yThrust * mass ) );//thrust
            double normalForce = mass * velocity * velocity / Math.abs( radiusOfCurvature ) - netForceRadial.dot( getCurvatureDirection() );

            return Vector2D.Double.parseAngleAndMagnitude( normalForce, getCurvatureDirection().getAngle() );
        }
        else {
            Vector2D.Double netForceRadial = new Vector2D.Double();
            netForceRadial.add( new Vector2D.Double( 0, mass * g ) );//gravity
            netForceRadial.add( new Vector2D.Double( xThrust * mass, yThrust * mass ) );//thrust
            double normalForce = mass * velocity * velocity / Math.abs( getRadiusOfCurvature() ) - netForceRadial.dot( getCurvatureDirection() );
            return Vector2D.Double.parseAngleAndMagnitude( normalForce, getCurvatureDirection().getAngle() );
        }
    }

    /*
   Returns the net force (discluding normal forces).
    */
    public AbstractVector2D getNetForce() {
        Vector2D.Double netForce = new Vector2D.Double();
        netForce.add( new Vector2D.Double( 0, mass * g ) );//gravity
        netForce.add( new Vector2D.Double( xThrust * mass, yThrust * mass ) );//thrust
        netForce.add( getFrictionForce() );
        return netForce;
    }

    public AbstractVector2D getFrictionForce() {
//        if( getTotalFriction() == 0 || getVelocity2D().getMagnitude() < 1E-6 ) {
        if( getTotalFriction() == 0 || getVelocity2D().getMagnitude() < 1E-2 ) {
            return new Vector2D.Double();
        }
        else {
//        return getVelocity2D().getScaledInstance( -frictionCoefficient * 10000 );//todo factor out heuristic
//        return getVelocity2D().getScaledInstance( -frictionCoefficient * getNormalForce().getMagnitude() );//todo factor out heuristic
//            System.out.println( "friction = " + getTotalFriction() );
            AbstractVector2D f = getVelocity2D().getInstanceOfMagnitude( -getTotalFriction() * getNormalForce().getMagnitude() * 25 );
            if( ( Double.isNaN( f.getMagnitude() ) ) ) { throw new IllegalArgumentException();}
            return f;//todo factor out heuristic
        }
    }

    private double getTotalFriction() {
        return frictionCoefficient + ( ( track instanceof TrackWithFriction ) ? ( (TrackWithFriction)track ).getFriction() : 0.0 );
    }

    public class Euler implements UpdateStrategy {
        public void stepInTime( double dt ) {
//            System.out.println( "nor = " + getNormalForce().getMagnitude() );
            double origEnergy = getEnergy();
            SPoint2D origLoc = getLocation();
            AbstractVector2D netForce = getNetForce();
            double a = track.getUnitParallelVector( alpha ).dot( netForce ) / mass;
            velocity += a * dt;
            alpha += track.getFractionalDistance( alpha, velocity * dt + 1 / 2 * a * dt * dt );
            if( getTotalFriction() > 0 ) {
                if( ( Double.isNaN( getFrictionForce().getMagnitude() ) ) ) { throw new IllegalArgumentException();}
                double therm = getFrictionForce().getMagnitude() * getLocation().distance( origLoc );
                thermalEnergy += therm;
                if( getThrust().getMagnitude() == 0 ) {//only conserve energy if the user is not adding energy
                    if( getEnergy() < origEnergy ) {
                        thermalEnergy += Math.abs( getEnergy() - origEnergy );//add some thermal to exactly match
                        if( Math.abs( getEnergy() - origEnergy ) > 1E-6 ) {
                            System.out.println( "Added thermal, dE=" + ( getEnergy() - origEnergy ) );
                        }
                    }
                    if( getEnergy() > origEnergy ) {
                        if( Math.abs( getEnergy() - origEnergy ) < therm ) {
                            System.out.println( "gained energy, removing thermal (Would have to remove more than we gained)" );
                        }
                        else {
                            double editThermal = Math.abs( getEnergy() - origEnergy );
                            thermalEnergy -= editThermal;
                            if( Math.abs( getEnergy() - origEnergy ) > 1E-6 ) {
                                System.out.println( "Removed thermal, dE=" + ( getEnergy() - origEnergy ) );
                            }
                        }
                    }
                }
            }
            if( ( Double.isNaN( getKineticEnergy() ) ) ) { throw new IllegalArgumentException();}
            if( ( Double.isInfinite( getKineticEnergy() ) ) ) { throw new IllegalArgumentException();}
            if( ( Double.isNaN( getVelocity2D().getMagnitude() ) ) ) { throw new IllegalArgumentException();}
            handleBoundary();
        }
    }

    public class RK4 implements UpdateStrategy {

        public void stepInTime( double dt ) {
            double state[] = new double[]{alpha, velocity};
            edu.colorado.phet.energyskatepark.model.RK4.Diff diffy = new edu.colorado.phet.energyskatepark.model.RK4.Diff() {
                public void f( double t, double state[], double F[] ) {
                    F[0] = state[1];
                    double parallelForce = track.getUnitParallelVector( alpha ).dot( getNetForce() );
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
