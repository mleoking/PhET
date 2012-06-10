package edu.colorado.phet.common.phetcommon.math;

import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * Solves 1-dimensional damped mass-spring systems (general harmonic oscillators)
 * TODO: full testing
 */
public class DampedMassSpringSystem {

    private final double mass;
    private final double k;
    private final double c;
    private final double x0;
    private final double v0;

    private final double omega0;
    private final double zeta;

    private final Complex smallRoot;
    private final Complex largeRoot;

    private final Complex smallConstant;
    private final Complex largeConstant;

    private final Pair<Complex, Complex> characteristicEquationRoots;

    /**
     * Initialize the system
     *
     * @param mass Mass of the oscillator
     * @param k    Spring constant
     * @param c    Damping constant
     * @param x0   Initial position
     * @param v0   Initial velocity
     */
    public DampedMassSpringSystem( double mass, double k, double c, double x0, double v0 ) {
        this.mass = mass;
        this.k = k;
        this.c = c;
        this.x0 = x0;
        this.v0 = v0;
        omega0 = Math.sqrt( k / mass );
        zeta = c / ( 2 * Math.sqrt( mass * k ) );
        characteristicEquationRoots = solveQuadratic( 1, c / mass, k / mass );
        smallRoot = characteristicEquationRoots._1;
        largeRoot = characteristicEquationRoots._2;

        Complex initialPosition = new Complex( x0, 0 );
        Complex initialVelocity = new Complex( v0, 0 );

        Complex frac = ( largeRoot.getMultiply( initialPosition ).getSubtract( initialVelocity ) )
                .getDivide( smallRoot.getSubtract( largeRoot ) );

        if ( isCriticallyDamped() ) {
            // we are critically damped
            largeConstant = initialPosition;
            smallConstant = initialVelocity.getAdd( initialPosition.getMultiply( omega0 ) );
        }
        else {
            largeConstant = initialPosition.getAdd( frac );
            smallConstant = frac.getOpposite();
        }
    }

    public double evaluatePosition( double t ) {
        Complex result;
        if ( isCriticallyDamped() ) {
            // real root, simplifies things
            double root = -omega0;
            result = new Complex( ( x0 + ( v0 + omega0 * x0 ) * t ) * Math.exp( root * t ), 0 );
        }
        else {
            // = A * exp( rootA * t ) + B * exp( rootB * t )
            Complex left = largeConstant.getMultiply( Complex.getExp( largeRoot.getMultiply( t ) ) );
            Complex right = smallConstant.getMultiply( Complex.getExp( smallRoot.getMultiply( t ) ) );
            result = left.getAdd( right );
        }
        System.out.println( result );
        return result._real;
    }

    public double evaluateVelocity( double t ) {
        Complex result;
        if ( isCriticallyDamped() ) {
            // real root, simplifies things
            double root = -omega0;
            double a = x0;
            double b = v0 + omega0 * x0;
            result = new Complex( a * root * t * Math.exp( root * t )
                                  + b * Math.exp( root * t )
                                  + b * root * t * t * Math.exp( root * t ), 0 );
        }
        else {
            Complex left = largeConstant.getMultiply( Complex.getExp( largeRoot.getMultiply( t ) ) ).getMultiply( largeRoot.getMultiply( t ) );
            Complex right = smallConstant.getMultiply( Complex.getExp( smallRoot.getMultiply( t ) ) ).getMultiply( smallRoot.getMultiply( t ) );
            result = left.getAdd( right );
        }
        System.out.println( result );
        return result._real;
    }

    public boolean isCriticallyDamped() {
        return zeta == 1;
    }

    public boolean isOverDamped() {
        return zeta > 1;
    }

    public boolean isUnderDamped() {
        return zeta < 1;
    }

    public static double getCriticallyDampedDamping( double mass, double k ) {
        return 2 * Math.sqrt( mass * k );
    }

    // solves for the two roots (smallest root given first)
    public static Pair<Complex, Complex> solveQuadratic( double a, double b, double c ) {
        return solveQuadratic( new Complex( a, 0 ), new Complex( b, 0 ), new Complex( c, 0 ) );
    }

    public static Pair<Complex, Complex> solveQuadratic( Complex a, Complex b, Complex c ) {
        Complex discriminant = b.getMultiply( b ).getSubtract( a.getMultiply( c ).getMultiply( 4 ) );
        Complex sqrted = discriminant.getCanonicalSquareRoot();
        return new Pair<Complex, Complex>( new Complex( b.getOpposite().getSubtract( sqrted ).getDivide( a.getMultiply( 2 ) ) ),
                                           new Complex( b.getOpposite().getAdd( sqrted ).getDivide( a.getMultiply( 2 ) ) ) );
    }
}
