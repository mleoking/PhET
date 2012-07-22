// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jamaphet.JamaUtils;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;

/**
 * Contains the logic for applying an "attractor" force to a molecule that first:
 * (1) finds the closest VSEPR configuration (with rotation) to our current positions, and
 * (2) pushes the electron pairs towards those positions.
 */
public class AttractorModel {
    /**
     * Apply an attraction to the closest ideal position, with the given time elapsed
     *
     * @param groups                An ordered list of pair groups that should be considered, along with the relevant permutations
     * @param timeElapsed           Time elapsed
     * @param idealOrientations     An ideal position, that may be rotated.
     * @param allowablePermutations The un-rotated stable position that we are attracted towards
     * @param center                The point that the groups should be rotated around. Usually a central atom that all of the groups connect to
     * @return A measure of total error (least squares-style)
     */
    public static double applyAttractorForces( List<PairGroup> groups, final float timeElapsed, List<Vector3D> idealOrientations, List<Permutation> allowablePermutations, final Vector3D center, boolean angleRepulsion ) {
        List<Vector3D> currentOrientations = map( groups, new Function1<PairGroup, Vector3D>() {
            public Vector3D apply( PairGroup group ) {
                return group.position.get().minus( center ).normalized();
            }
        } );
        final ResultMapping mapping = findClosestMatchingConfiguration( currentOrientations, idealOrientations, allowablePermutations );

        boolean aroundCenterAtom = center.equals( new Vector3D() );

        double totalDeltaMagnitude = 0;

        // for each electron pair, push it towards its computed target
        for ( int i = 0; i < groups.size(); i++ ) {
            PairGroup pair = groups.get( i );

            Vector3D targetOrientation = JamaUtils.vectorFromMatrix3D( mapping.target, i );
            double currentMagnitude = ( pair.position.get().minus( center ) ).magnitude();
            Vector3D targetLocation = targetOrientation.times( currentMagnitude ).plus( center );

            Vector3D delta = targetLocation.minus( pair.position.get() );
            totalDeltaMagnitude += delta.magnitude() * delta.magnitude();

            /*
             * NOTE: adding delta here effectively is squaring the distance, thus more force when far from the target,
             * and less force when close to the target. This is important, since we want more force in a potentially
             * otherwise-stable position, and less force where our coulomb-like repulsion will settle it into a stable
             * position
             */
            double strength = timeElapsed * 3 * delta.magnitude();

            // change the velocity of all of the pairs, unless it is an atom at the origin!
            if ( pair.isLonePair || !pair.isCentralAtom() ) {
                if ( aroundCenterAtom ) {
                    pair.addVelocity( delta.times( strength ) );
                }
            }

            // position movement for faster convergence
            if ( !pair.isCentralAtom() && aroundCenterAtom ) { // TODO: better way of not moving the center atom?
                pair.position.set( pair.position.get().plus( delta.times( 2.0 * timeElapsed ) ) );
            }

            // if we are a terminal lone pair, move us just with this but much more quickly
            if ( !pair.isCentralAtom() && !aroundCenterAtom ) {
//                pair.position.set( targetLocation );
                pair.position.set( pair.position.get().plus( delta.times( Math.min( 20.0 * timeElapsed, 1 ) ) ) );
            }
        }

        double error = Math.sqrt( totalDeltaMagnitude );

        // angle-based repulsion
        if ( angleRepulsion && aroundCenterAtom ) {
            for ( Pair<Integer, Integer> pairIndices : FunctionalUtils.pairs( FunctionalUtils.rangeInclusive( 0, groups.size() - 1 ) ) ) {
                int aIndex = pairIndices._1;
                int bIndex = pairIndices._2;
                PairGroup a = groups.get( aIndex );
                PairGroup b = groups.get( bIndex );

                // current orientations w.r.t. the center
                Vector3D aOrientation = a.position.get().minus( center ).normalized();
                Vector3D bOrientation = b.position.get().minus( center ).normalized();

                // desired orientations
                Vector3D aTarget = JamaUtils.vectorFromMatrix3D( mapping.target, aIndex ).normalized();
                Vector3D bTarget = JamaUtils.vectorFromMatrix3D( mapping.target, bIndex ).normalized();
                double targetAngle = Math.acos( MathUtil.clamp( -1, aTarget.dot( bTarget ), 1 ) );
                double currentAngle = Math.acos( MathUtil.clamp( -1, aOrientation.dot( bOrientation ), 1 ) );
                double angleDifference = ( targetAngle - currentAngle );

                Vector3D dirTowardsA = a.position.get().minus( b.position.get() ).normalized();
                double timeFactor = PairGroup.getTimescaleImpulseFactor( timeElapsed );

                double extraClosePushFactor = MathUtil.clamp( 1, 3 * Math.pow( Math.PI - currentAngle, 2 ) / ( Math.PI * Math.PI ), 3 );

                Vector3D push = dirTowardsA.times( timeFactor
                                                            * angleDifference
                                                            * PairGroup.ANGLE_REPULSION_SCALE
                                                            * ( currentAngle < targetAngle ? 2.0 : 0.5 )
                                                            * extraClosePushFactor );
                a.addVelocity( push );
                b.addVelocity( push.negated() );
            }
        }

        return error;
    }

    /**
     * Find the closest VSEPR configuration for a particular molecule. Conceptually, we iterate through
     * each possible valid 1-to-1 mapping from electron pair to direction in our VSEPR geometry. For each
     * mapping, we calculate the rotation that makes the best match, and then calculate the error. We return
     * a result for the mapping (permutation) with the lowest error.
     * <p/>
     * This uses a slightly modified rotation computation from http://igl.ethz.ch/projects/ARAP/svd_rot.pdf
     * (Least-Squares Rigid Motion Using SVD). Basically, we ignore the centroid and translation computations,
     * since we want everything to be rotated around the origin. We also don't weight the individual electron
     * pairs.
     * <p/>
     * Of note, the lower-index slots in the VseprConfiguration (GeometryConfiguration) are for higher-repulsion
     * pair groups (the order is triple > double > lone pair > single). We need to iterate through all permutations,
     * but with the repulsion-ordering constraint (no single bond will be assigned a lower-index slot than a lone pair)
     * so we end up splitting the potential slots into bins for each repulsion type and iterating over all of the permutations.
     *
     * @param currentOrientations   An ordered list of orientations (normalized) that should be considered, along with the relevant permutations
     * @param idealOrientations     The un-rotated stable position that we are attracted towards
     * @param allowablePermutations A list of permutations that map stable positions to pair groups in order.
     * @return Result mapping (see docs there)
     */
    public static ResultMapping findClosestMatchingConfiguration( final List<Vector3D> currentOrientations, final List<Vector3D> idealOrientations, List<Permutation> allowablePermutations ) {
        final int n = currentOrientations.size(); // number of total pairs

        // y == electron pair positions
        final Matrix y = JamaUtils.matrixFromVectors3D( currentOrientations );
        final Matrix yTransposed = y.transpose();

        final Property<ResultMapping> bestResult = new Property<ResultMapping>( null );

        for ( Permutation permutation : allowablePermutations ) {
            // x == configuration positions
            Matrix x = JamaUtils.matrixFromVectors3D( permutation.apply( idealOrientations ) );

            // compute the rotation matrix
            Matrix rot = computeRotationMatrixWithTranspose( x, yTransposed );

            // target matrix, same shape as our y (current position) matrix
            Matrix target = rot.times( x );

            // calculate the error
            double error = 0;
            Matrix offsets = y.minus( target );
            Matrix squaredOffsets = offsets.arrayTimes( offsets );
            for ( int i = 0; i < n; i++ ) {
                error += squaredOffsets.get( 0, i ) + squaredOffsets.get( 1, i ) + squaredOffsets.get( 2, i );
            }

            // if this is the best one, record it
            if ( bestResult.get() == null || error < bestResult.get().error ) {
                bestResult.set( new ResultMapping( error, target, permutation, rot ) );
            }
        }
        return bestResult.get();
    }

    public static List<Vector3D> getOrientationsFromOrigin( List<PairGroup> groups ) {
        return map( groups, new Function1<PairGroup, Vector3D>() {
            public Vector3D apply( PairGroup group ) {
                return group.position.get().normalized();
            }
        } );
    }

    private static Matrix computeRotationMatrixWithTranspose( Matrix x, Matrix yTransposed ) {
        // S = X * Y^T
        Matrix s = x.times( yTransposed );

        // this code will loop infinitely on NaN, so we want to double-check
        assert !Double.isNaN( s.get( 0, 0 ) );
        SingularValueDecomposition svd = new SingularValueDecomposition( s );
        double det = svd.getV().times( svd.getU().transpose() ).det();

        return svd.getV().times( new Matrix( new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, det}} ).times( svd.getU().transpose() ) );
    }

    public static class ResultMapping {
        /**
         * Sum of the squared (2-norm) error between our target and current positions
         */
        final public double error;

        /**
         * Target matrix, where each column is a target vector corresponding to the columns of our electron pair matrix (not stored here)
         */
        final public Matrix target;

        /**
         * Permutation from electron pair index => VSEPR geometry vector index
         */
        final public Permutation permutation;

        /**
         * Rotation from the ideal to the approximate position in local space
         */
        final public Matrix rotation;

        private ResultMapping( double error, Matrix target, Permutation permutation, Matrix rotation ) {
            this.error = error;
            this.target = target;
            this.permutation = permutation;
            this.rotation = rotation;
        }

        public Vector3D rotateVector( Vector3D v ) {
            Matrix x = JamaUtils.matrixFromVectors3D( Arrays.asList( v ) );
            Matrix rotated = rotation.times( x );
            return new Vector3D( rotated.get( 0, 0 ), rotated.get( 1, 0 ), rotated.get( 2, 0 ) );
        }
    }

    /**
     * Call the function with each individual permutation of the list elements of "lists"
     *
     * @param lists    List of lists. Order of lists will not change, however each possible permutation involving sub-lists will be used
     * @param function Function to call
     * @param <T>      Type
     */
    public static <T> void forEachMultiplePermutations( final List<List<T>> lists, final VoidFunction1<List<List<T>>> function ) {
        if ( lists.isEmpty() ) {
            function.apply( lists );
        }
        else {
            final List<List<T>> remainder = new ArrayList<List<T>>( lists );
            final List<T> first = remainder.get( 0 );
            remainder.remove( 0 );
            forEachPermutation( first, new VoidFunction1<List<T>>() {
                public void apply( final List<T> permutedFirst ) {
                    forEachMultiplePermutations( remainder, new VoidFunction1<List<List<T>>>() {
                        public void apply( final List<List<T>> subLists ) {
                            function.apply( new ArrayList<List<T>>( lists.size() ) {{
                                add( permutedFirst );
                                addAll( subLists );
                            }} );
                        }
                    } );
                }
            } );
        }
    }

    /**
     * Call our function with each permutation of the provided list, in lexicographic order
     *
     * @param list     List to generate permutations of
     * @param function Function to call
     * @param <T>      Type of the list
     */
    public static <T> void forEachPermutation( List<T> list, VoidFunction1<List<T>> function ) {
        forEachPermutation( list, new ArrayList<T>(), function );
    }

    /**
     * Call our function with each permutation of the provided list PREFIXED by prefix, in lexicographic order
     *
     * @param list     List to generate permutations of
     * @param prefix   Elements that should be inserted at the front of each list before each call
     * @param function Function to call
     * @param <T>      Type of the list
     */
    private static <T> void forEachPermutation( List<T> list, List<T> prefix, VoidFunction1<List<T>> function ) {
        if ( list.isEmpty() ) {
            function.apply( prefix );
        }
        else {
            for ( final T element : list ) {
                forEachPermutation(
                        new ArrayList<T>( list ) {{remove( element );}},
                        new ArrayList<T>( prefix ) {{add( element );}},
                        function
                );
            }
        }
    }

    public static void main( String[] args ) {
        /*
         Testing of permuting each individual list. Output:
         AB C DEF
         AB C DFE
         AB C EDF
         AB C EFD
         AB C FDE
         AB C FED
         BA C DEF
         BA C DFE
         BA C EDF
         BA C EFD
         BA C FDE
         BA C FED
         */
        forEachMultiplePermutations( new ArrayList<List<String>>() {{
                                         add( new ArrayList<String>() {{
                                             add( "A" );
                                             add( "B" );
                                         }} );
                                         add( new ArrayList<String>() {{
                                             add( "C" );
                                         }} );
                                         add( new ArrayList<String>() {{
                                             add( "D" );
                                             add( "E" );
                                             add( "F" );
                                         }} );
                                     }}, new VoidFunction1<List<List<String>>>() {
                                         public void apply( List<List<String>> lists ) {
                                             String ret = listPrint( lists );
                                             System.out.println( ret );
                                         }
                                     }
        );
    }

    private static <T> String listPrint( List<List<T>> lists ) {
        String ret = "";
        for ( List<T> list : lists ) {
            ret += " ";
            for ( Object o : list ) {
                ret += o.toString();
            }
        }
        return ret;
    }
}
