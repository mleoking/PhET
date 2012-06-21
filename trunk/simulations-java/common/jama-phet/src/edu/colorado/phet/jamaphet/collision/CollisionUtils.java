package edu.colorado.phet.jamaphet.collision;

import Jama.Matrix;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Optimization1D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.jamaphet.LinearLeastSquares;
import edu.colorado.phet.jamaphet.RigidMotionLeastSquares;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.filter;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.rangeInclusive;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.reduceLeft;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.zip;

/**
 * Collision-related matrix methods, mainly for collision prediction and fitting of multiple objects.
 *
 * @author Jonathan Olson
 */
public class CollisionUtils {
    public static CollisionFit2D bestFitIntersection2D( final List<? extends Collidable2D> objects ) {
        final int n = objects.size();

        // we need to intersect at least two rays, otherwise it is ill-defined
        assert n > 1;

        /*
          Least-squares matrix is based on our over-constrained system.
          Basically, for each ray, we constrain the result (x,y,t) pair with
          p_x + t * v_x = x
          p_y + t * v_y = y
          Which can then be transformed into a matrix system with
          [ 1 0 -d_x ] * [ x y t ]' = [ p_x ]
          and
          [ 0 1 -d_y ] * [ x y t ]' = [ p_y ]
        */

        Matrix a = new Matrix( 2 * n, 3 ) {{
            for ( int i = 0; i < n; i++ ) {
                ImmutableVector2D velocity = objects.get( i ).getVelocity();

                set( 2 * i, 0, 1 );
                set( 2 * i, 1, 0 );
                set( 2 * i, 2, -velocity.getX() );

                set( 2 * i + 1, 0, 0 );
                set( 2 * i + 1, 1, 1 );
                set( 2 * i + 1, 2, -velocity.getY() );
            }
        }};
        Matrix b = new Matrix( 2 * n, 1 ) {{
            for ( int i = 0; i < n; i++ ) {
                ImmutableVector2D position = objects.get( i ).getPosition();
                set( 2 * i, 0, position.getX() );
                set( 2 * i + 1, 0, position.getY() );
            }
        }};

        Matrix x = LinearLeastSquares.solveLinearLeastSquares( a, b );

        return new CollisionFit2D(
                new ImmutableVector2D(
                        x.get( 0, 0 ),
                        x.get( 1, 0 ) ),
                x.get( 2, 0 ) );
    }

    // result class for best fitting
    public static class CollisionFit2D {
        // best fit position
        public final ImmutableVector2D position;

        // time elapsed
        public final double t;

        public CollisionFit2D( ImmutableVector2D position, double t ) {
            this.position = position;
            this.t = t;
        }
    }

    /*---------------------------------------------------------------------------*
    * testing
    *----------------------------------------------------------------------------*/

    private static class ImmutableCollidable2D implements Collidable2D {
        public final ImmutableVector2D position;
        public final ImmutableVector2D velocity;

        private ImmutableCollidable2D( ImmutableVector2D position, ImmutableVector2D velocity ) {
            this.position = position;
            this.velocity = velocity;
        }

        public ImmutableVector2D getPosition() {
            return position;
        }

        public ImmutableVector2D getVelocity() {
            return velocity;
        }

        @Override public String toString() {
            return "ImmutableCollidable2D{" +
                   "position=" + position +
                   ", velocity=" + velocity +
                   '}';
        }
    }

    public static void main( String[] args ) {
        List<List<Double>> listy = filter( map( rangeInclusive( 0, 0 ), new Function1<Integer, List<Double>>() {
            public List<Double> apply( Integer integer ) {
                return getErrors();
            }
        } ), new Function1<List<Double>, Boolean>() {
            public Boolean apply( List<Double> doubles ) {
                return doubles != null;
            }
        } );
        StringBuilder builder = new StringBuilder();
        List<Double> xs = getXs();
        for ( int i = 0; i < listy.get( 0 ).size(); i++ ) {
            builder.append( xs.get( i ) );
            for ( List<Double> list : listy ) {
                builder.append( "\t" + list.get( i ) );
            }
            builder.append( "\n" );
        }
        setClipboard( builder.toString() );
    }

    private static double HIGH_BOUND = 2;
    private static double STEP_SIZE = 0.005;

    private static List<Double> getXs() {
        List<Double> result = new ArrayList<Double>();
        for ( double t = 0; t < HIGH_BOUND; t += STEP_SIZE ) {
            result.add( t );
        }
        return result;
    }

    private static List<Double> getErrors() {
        final ArrayList<Collidable2D> objects = new ArrayList<Collidable2D>() {{
            add( new ImmutableCollidable2D(
                    new ImmutableVector2D( Math.random() - 0.5, Math.random() - 0.5 ),
                    new ImmutableVector2D( Math.random() - 0.5, Math.random() - 0.5 )
            ) );
            add( new ImmutableCollidable2D(
                    new ImmutableVector2D( Math.random() - 0.5, Math.random() - 0.5 ),
                    new ImmutableVector2D( Math.random() - 0.5, Math.random() - 0.5 )
            ) );
            add( new ImmutableCollidable2D(
                    new ImmutableVector2D( Math.random() - 0.5, Math.random() - 0.5 ),
                    new ImmutableVector2D( Math.random() - 0.5, Math.random() - 0.5 )
            ) );
        }};
//        System.out.println( mkString( objects, ", " ) );
        final ArrayList<ImmutableVector2D> targets = new ArrayList<ImmutableVector2D>() {{
            add( new ImmutableVector2D( 0, 0 ) );
            add( new ImmutableVector2D( 0.2, 0 ) );
            add( new ImmutableVector2D( 0, 0.2 ) );
        }};

        CollisionFit2D result = bestFitIntersection2D( objects );

        double t0error = getError( objects, targets, 0 );
        double tminerror = getError( objects, targets, 0.001 );
        double tprederror = getError( objects, targets, result.t );

        if ( tminerror > t0error && ( result.t < 0 || tprederror > t0error ) ) {
            // bailing, not predicted to return something good
            return null;
        }

        List<Double> errors = new ArrayList<Double>();
        for ( double t = 0; t < HIGH_BOUND; t += STEP_SIZE ) {
            double err = getError( objects, targets, t );
            if ( t > result.t * 1.5 ) {
                errors.add( tprederror );
            }
            else {
                errors.add( err );
            }
        }

        System.out.println( "minimization: " + Optimization1D.goldenSectionSearch( new Function1<Double, Double>() {
            public Double apply( Double t ) {
                System.out.println( "eval" );
                return getError( objects, targets, t );
            }
        }, 0, result.t * 1.5, 0.01 ) );

        return errors;
    }

    private static double getError( ArrayList<Collidable2D> objects, ArrayList<ImmutableVector2D> targets, final double t ) {
        // positions after time T
        List<ImmutableVector2D> positions = map( objects, new Function1<Collidable2D, ImmutableVector2D>() {
            public ImmutableVector2D apply( Collidable2D object ) {
                return object.getPosition().plus( object.getVelocity().times( t ) );
            }
        } );

        // rigid motion transformation
        final RigidMotionLeastSquares.RigidMotionTransformation transformation = RigidMotionLeastSquares.bestFitMotion2D( targets, positions, true );

        // and closest targets at time T, based on the computed positions
        List<ImmutableVector2D> transformedTargets = map( targets, new Function1<ImmutableVector2D, ImmutableVector2D>() {
            public ImmutableVector2D apply( ImmutableVector2D v ) {
                return transformation.transformVector2D( v );
            }
        } );

        List<ImmutableVector2D> differences = zip( positions, transformedTargets, new Function2<ImmutableVector2D, ImmutableVector2D, ImmutableVector2D>() {
            public ImmutableVector2D apply( ImmutableVector2D position, ImmutableVector2D target ) {
                return position.minus( target );
            }
        } );

        return reduceLeft( map( differences, new Function1<ImmutableVector2D, Double>() {
            public Double apply( ImmutableVector2D v ) {
                return v.getMagnitude();
            }
        } ), new Function2<Double, Double, Double>() {
            public Double apply( Double a, Double b ) {
                return a + b;
            }
        } );
    }

    public static void setClipboard( String str ) {
        StringSelection ss = new StringSelection( str );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( ss, null );
    }
}
