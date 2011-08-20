// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

/**
 * Contains the logic for applying an "attractor" force to a molecule that first:
 * (1) finds the closest VSEPR configuration (with rotation) to our current positions, and
 * (2) pushes the electron pairs towards those positions.
 */
public class AttractorModel {
    public static void applyAttractorForces( final MoleculeModel molecule, final float timeElapsed ) {
        final ResultMapping mapping = findClosestMatchingConfiguration( molecule );

        // for each electron pair, push it towards its computed target
        for ( int i = 0; i < molecule.getGroups().size(); i++ ) {
            PairGroup pair = molecule.getGroups().get( i );
            ImmutableVector3D targetUnitVector = new ImmutableVector3D( mapping.target.get( 0, i ), mapping.target.get( 1, i ), mapping.target.get( 2, i ) );
            ImmutableVector3D targetLocation = targetUnitVector.times( pair.position.get().magnitude() );

            ImmutableVector3D delta = targetLocation.minus( pair.position.get() );

            /*
             * NOTE: adding delta here effectively is squaring the distance, thus more force when far from the target,
             * and less force when close to the target. This is important, since we want more force in a potentially
             * otherwise-stable position, and less force where our coulomb-like repulsion will settle it into a stable
             * position
             */
            double strength = timeElapsed * 3 * delta.magnitude();

            pair.addVelocity( delta.times( strength ) );
        }
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
     * @param molecule Molecule
     * @return Result mapping (see docs there)
     */
    private static ResultMapping findClosestMatchingConfiguration( final MoleculeModel molecule ) {
        final VseprConfiguration configuration = new VseprConfiguration( molecule.getBondedGroups().size(), molecule.getLonePairs().size() );

        final int n = molecule.getGroups().size(); // number of total pairs

        // y == electron pair positions
        final Matrix y = new Matrix( 3, n ) {{
            for ( int i = 0; i < n; i++ ) {
                // fill the vector into the matrix as a column
                ImmutableVector3D normalizedPosition = molecule.getGroups().get( i ).position.get().normalized();
                set( 0, i, normalizedPosition.getX() );
                set( 1, i, normalizedPosition.getY() );
                set( 2, i, normalizedPosition.getZ() );
            }
        }};

        final Property<ResultMapping> bestResult = new Property<ResultMapping>( null );

        // iterate over different repulsion categories
        forEachMultiplePermutations( separateRepulsionCategories( molecule ), new VoidFunction1<List<List<Integer>>>() {
            public void apply( final List<List<Integer>> indexLists ) {
                final List<ImmutableVector3D> unitVectors = configuration.geometry.unitVectors;

                // permutation. think of it as a function of index of electron pair => index of configuration vector
                final List<Integer> permutation = new ArrayList<Integer>( n );
                // TODO: consider removal of the permutation list. we don't actually use it (yet), even though it may be useful in the future

                // x == configuration positions
                Matrix x = new Matrix( 3, n ) {{
                    // fill up the matrix with the permuted configuration vectors
                    List<Integer> indices = flatten( indexLists );
                    for ( int i = 0; i < n; i++ ) {
                        // compute the index and store it (we need to combine the permutations here!)
                        Integer permutedIndex = indices.get( i );
                        permutation.add( permutedIndex );

                        // fill the vector into the matrix as a column
                        ImmutableVector3D configurationVector = unitVectors.get( permutedIndex );
                        set( 0, i, configurationVector.getX() );
                        set( 1, i, configurationVector.getY() );
                        set( 2, i, configurationVector.getZ() );
                    }
                }};

                // S = X * Y^T
                Matrix s = x.times( y.transpose() );

                SingularValueDecomposition svd = new SingularValueDecomposition( s );
                double det = svd.getV().times( svd.getU().transpose() ).det();

                Matrix rot = svd.getV().times( new Matrix( new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, det } } ).times( svd.getU().transpose() ) );

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
                    bestResult.set( new ResultMapping( error, target, permutation ) );
                }
            }
        } );
        return bestResult.get();
    }

    private static class ResultMapping {
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
        final public List<Integer> permutation;

        private ResultMapping( double error, Matrix target, List<Integer> permutation ) {
            this.error = error;
            this.target = target;
            this.permutation = permutation;
        }
    }

    /**
     * @param lists Lists to flatten
     * @param <T>   Type
     * @return Standard 1-step flattened list
     */
    private static <T> List<T> flatten( List<List<T>> lists ) {
        List<T> ret = new ArrayList<T>();
        for ( List<T> list : lists ) {
            ret.addAll( list );
        }
        return ret;
    }

    /**
     * Separate out lists of indices for groups of differing bond orders. Relies on the molecule groups having been
     * sorted into repulsion categories
     *
     * @param molecule The molecule
     * @return List of index lists. each sub-list has the same bond order
     */
    private static List<List<Integer>> separateRepulsionCategories( final MoleculeModel molecule ) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        List<Integer> current = new ArrayList<Integer>();
        int lastBondOrder = 50;

        for ( int i = 0; i < molecule.getGroups().size(); i++ ) {
            PairGroup group = molecule.getGroups().get( i );
            if ( group.bondOrder != lastBondOrder ) {
                lastBondOrder = group.bondOrder;
                if ( !current.isEmpty() ) {
                    result.add( current );
                }
                current = new ArrayList<Integer>();
            }
            current.add( i );
        }
        if ( !current.isEmpty() ) {
            result.add( current );
        }
        return result;
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

    /**
     * Returns a list of integers from A to B (including both A to B)
     *
     * @param a A
     * @param b B
     * @return A to B
     */
    public static List<Integer> rangeInclusive( int a, int b ) {
        List<Integer> result = new ArrayList<Integer>( b - a + 1 );
        for ( int i = a; i <= b; i++ ) {
            result.add( i );
        }
        return result;
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
