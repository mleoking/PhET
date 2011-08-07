// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

public class AttractorModel {
    public static void applyAttractorForces( final MoleculeModel molecule, final float timeElapsed ) {
        long a = System.currentTimeMillis();
        final ResultMapping mapping = findClosestMatchingConfiguration( molecule );
        long b = System.currentTimeMillis();

        System.out.println( "error: " + mapping.error );
        System.out.println( "ms: " + ( b - a ) );
    }

    private static ResultMapping findClosestMatchingConfiguration( final MoleculeModel molecule ) {
        final VseprConfiguration configuration = new VseprConfiguration( molecule.getBondedPairs().size(), molecule.getLonePairs().size() );

        final int n = molecule.getPairs().size(); // number of total pairs
        final int e = molecule.getLonePairs().size();

        // y == electron pair positions
        final Matrix y = new Matrix( 3, n ) {{
            for ( int i = 0; i < n; i++ ) {
                // fill the vector into the matrix as a column
                ImmutableVector3D normalizedPosition = molecule.getPairs().get( i ).position.get().normalized();
                set( 0, i, normalizedPosition.getX() );
                set( 1, i, normalizedPosition.getY() );
                set( 2, i, normalizedPosition.getZ() );
            }
        }};

        final Property<ResultMapping> bestResult = new Property<ResultMapping>( null );

        // permutation over lone pairs
        forEachPermutation( rangeInclusive( 0, e - 1 ), new VoidFunction1<List<Integer>>() {
            public void apply( final List<Integer> lonePairIndices ) {

                // permutation over bonded pairs
                forEachPermutation( rangeInclusive( e, n - 1 ), new VoidFunction1<List<Integer>>() {
                    public void apply( final List<Integer> bondedPairIndices ) {
                        final List<ImmutableVector3D> unitVectors = configuration.geometry.unitVectors;

                        // permutation. think of it as a function of index of electron pair => index of configuration vector
                        final List<Integer> permutation = new ArrayList<Integer>( n );

                        // x == configuration positions
                        Matrix x = new Matrix( 3, n ) {{
                            // fill up the matrix with the permuted configuration vectors
                            for ( int i = 0; i < n; i++ ) {
                                // compute the index and store it
                                Integer permutedIndex = i < e ? lonePairIndices.get( i ) : bondedPairIndices.get( i - e );
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
            }
        } );
        return bestResult.get();
    }

    private static class ResultMapping {
        final public double error;
        final public Matrix target;
        final public List<Integer> permutation;

        private ResultMapping( double error, Matrix target, List<Integer> permutation ) {
            this.error = error;
            this.target = target;
            this.permutation = permutation;
        }
    }

    public static <T> void forEachPermutation( List<T> list, VoidFunction1<List<T>> function ) {
        forEachPermutation( list, new ArrayList<T>(), function );
    }

    public static <T> void forEachPermutation( List<T> list, List<T> prefix, VoidFunction1<List<T>> function ) {
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

    public static List<Integer> rangeInclusive( int a, int b ) {
        List<Integer> result = new ArrayList<Integer>( b - a + 1 );
        for ( int i = a; i <= b; i++ ) {
            result.add( i );
        }
        return result;
    }
}
