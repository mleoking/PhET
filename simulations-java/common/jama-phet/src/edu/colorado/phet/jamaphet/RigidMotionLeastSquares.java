package edu.colorado.phet.jamaphet;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;

import static edu.colorado.phet.jamaphet.JamaUtils.columnVector;
import static edu.colorado.phet.jamaphet.JamaUtils.matrixFromVectors2D;
import static edu.colorado.phet.jamaphet.JamaUtils.matrixFromVectors3D;
import static edu.colorado.phet.jamaphet.JamaUtils.vectorFromMatrix2D;
import static edu.colorado.phet.jamaphet.JamaUtils.vectorFromMatrix3D;

/**
 * Rigid motion best-fit computation using http://igl.ethz.ch/projects/ARAP/svd_rot.pdf
 */
public class RigidMotionLeastSquares {

    public static RigidMotionTransformation bestFitMotion2D( final List<ImmutableVector2D> points,
                                                             final List<ImmutableVector2D> targets,
                                                             final boolean allowReflection ) {
        assert points.size() == targets.size();

        return bestFitMotion( matrixFromVectors2D( points ),
                              matrixFromVectors2D( targets ),
                              allowReflection );
    }

    public static RigidMotionTransformation bestFitMotion2D( final List<ImmutableVector2D> points,
                                                             final List<ImmutableVector2D> targets,
                                                             final boolean allowReflection,
                                                             final List<Double> weights ) {
        assert points.size() == targets.size();
        assert points.size() == weights.size();

        final int n = points.size();

        return bestFitMotion( matrixFromVectors2D( points ),
                              matrixFromVectors2D( targets ),
                              allowReflection,
                              new Matrix( n, n ) {{
                                  for ( int i = 0; i < n; i++ ) {
                                      set( i, i, weights.get( i ) );
                                  }
                              }} );
    }

    public static RigidMotionTransformation bestFitMotion3D( final List<ImmutableVector3D> points,
                                                             final List<ImmutableVector3D> targets,
                                                             final boolean allowReflection ) {
        assert points.size() == targets.size();

        return bestFitMotion( matrixFromVectors3D( points ),
                              matrixFromVectors3D( targets ),
                              allowReflection );
    }

    public static RigidMotionTransformation bestFitMotion3D( final List<ImmutableVector3D> points,
                                                             final List<ImmutableVector3D> targets,
                                                             final boolean allowReflection,
                                                             final List<Double> weights ) {
        assert points.size() == targets.size();
        assert points.size() == weights.size();

        final int n = points.size();

        return bestFitMotion( matrixFromVectors3D( points ),
                              matrixFromVectors3D( targets ),
                              allowReflection,
                              new Matrix( n, n ) {{
                                  for ( int i = 0; i < n; i++ ) {
                                      set( i, i, weights.get( i ) );
                                  }
                              }} );
    }

    /**
     * Dimension-independent best-fit code WITH weights
     *
     * @param points  Columns are vectors
     * @param targets Columns are vectors
     * @param weights Diagonal matrix, weights are on diagonal
     * @return The transformation
     */
    public static RigidMotionTransformation bestFitMotion( Matrix points, Matrix targets, final boolean allowReflection, Matrix weights ) {
        int n = points.getColumnDimension();

        Matrix pointCentroid = computeWeightedCentroid( points, weights );
        Matrix targetCentroid = computeWeightedCentroid( targets, weights );

        Matrix s = points.times( weights ).times( targets.transpose() );
        Matrix rotation = getRotation( s, allowReflection, n );
        Matrix translation = targetCentroid.minus( rotation.times( pointCentroid ) );

        return new RigidMotionTransformation( rotation, translation );
    }

    /**
     * Dimension-independent best-fit code WITHOUT weights
     *
     * @param points  Columns are vectors
     * @param targets Columns are vectors
     * @return The transformation
     */
    public static RigidMotionTransformation bestFitMotion( Matrix points, Matrix targets, final boolean allowReflection ) {
        int n = points.getColumnDimension();

        Matrix pointCentroid = computeCentroid( points );
        Matrix targetCentroid = computeCentroid( targets );

        Matrix s = points.times( targets.transpose() );
        Matrix rotation = getRotation( s, allowReflection, n );
        Matrix translation = targetCentroid.minus( rotation.times( pointCentroid ) );

        return new RigidMotionTransformation( rotation, translation );
    }

    // basically an affine transformation, but of arbitrary dimension (unlike Java AffineTransform)
    public static class RigidMotionTransformation {
        public final Matrix rotation; // d by d

        public final Matrix translation; // d by 1

        public RigidMotionTransformation( Matrix rotation, Matrix translation ) {
            this.rotation = rotation;
            this.translation = translation;
        }

        // for 2d case only
        public ImmutableVector2D transformVector2D( ImmutableVector2D v ) {
            return vectorFromMatrix2D( rotation.times( columnVector( v ) ).plus( translation ), 0 );
        }

        // for 3d case only
        public ImmutableVector3D transformVector3D( ImmutableVector3D v ) {
            return vectorFromMatrix3D( rotation.times( columnVector( v ) ).plus( translation ), 0 );
        }
    }

    /*---------------------------------------------------------------------------*
    * implementation details
    *----------------------------------------------------------------------------*/

    private static Matrix getRotation( Matrix s, boolean allowReflection, int n ) {

        // this code will loop infinitely on NaN, so we want to double-check
        assert !Double.isNaN( s.get( 0, 0 ) );
        SingularValueDecomposition svd = new SingularValueDecomposition( s );

        // in the paper, they note that the extra multiplication with the diagonal matrix is used to remove reflected transforms
        // it is easy to add the option to allow reflections, and is actually computationally faster
        if ( allowReflection ) {
            return svd.getV().times( svd.getU().transpose() );
        }
        else {
            double det = svd.getV().times( svd.getU().transpose() ).det();

            // if det == -1, then we have a reflection!

            Matrix diagonalWithDet = Matrix.identity( n, n );
            diagonalWithDet.set( n - 1, n - 1, det );

            return svd.getV().times( diagonalWithDet.times( svd.getU().transpose() ) );
        }
    }

    private static Matrix computeCentroid( Matrix points ) {
        int dim = points.getRowDimension();
        int n = points.getColumnDimension();

        Matrix result = new Matrix( dim, 1 );
        for ( int i = 0; i < n; i++ ) {
            result = result.plus( points.getMatrix( 0, dim - 1, i, i ) );
        }
        return result.times( 1 / ( (double) n ) );
    }

    private static Matrix computeWeightedCentroid( Matrix points, Matrix weights ) {
        int dim = points.getRowDimension();
        int n = points.getColumnDimension();
        assert weights.getColumnDimension() == n;

        double weightSum = 0;

        Matrix result = new Matrix( dim, 1 );
        for ( int i = 0; i < n; i++ ) {
            double weight = weights.get( i, i );
            result = result.plus( points.getMatrix( 0, 2, i, i ).times( weight ) );
            weightSum += weight;
        }
        return result.times( 1 / weightSum );
    }

}
