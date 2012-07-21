// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.*;

/**
 * The ideal local shape for a certain central atom and its (local) neighbors.
 * <p/>
 * Also contains the ability to push the local atoms into place, along with many helper functions
 */
public class LocalShape {
    // all of our pair groups should be connected to this atom
    private final PairGroup centralAtom;
    private final List<PairGroup> groups;

    // the ideal orientations (unit vectors) for the groups representing the ideal local shape
    private final List<Vector3D> idealOrientations;

    // denotes how we can map the groups into the orientation vectors. some combinations may not be possible
    private final List<Permutation> allowedPermutations;

    public LocalShape( List<Permutation> allowedPermutations, PairGroup centralAtom, List<PairGroup> groups, List<Vector3D> idealOrientations ) {
        this.allowedPermutations = allowedPermutations;
        this.centralAtom = centralAtom;
        this.groups = groups;
        this.idealOrientations = idealOrientations;
    }

    /**
     * Attracts the atoms to their ideal shape, and returns the current approximate "error" that they have at this state.
     * <p/>
     * Attraction done by adding in velocity.
     *
     * @param tpf Time elapsed.
     * @return Amount of error (least squares-style)
     */
    public double applyAttraction( float tpf ) {
        return AttractorModel.applyAttractorForces( groups, tpf, idealOrientations, allowedPermutations, centralAtom.position.get(), false );
    }

    /**
     * Given a list of permutations, return all permutations that exist with the specified indices permuted in all different ways.
     * <p/>
     * IE, if given the list of the single permutation (12), and specified indices {3,4,5}, the permutations returned will be
     * (12)(34),(12)(35),(12)(45),(12)(453),(12)(534),(12)
     */
    public static List<Permutation> permuteListWithIndices( List<Permutation> permutations, List<Integer> indices ) {
        if ( indices.size() < 2 ) {
            // no changes if we can't move more than 1 element (need somewhere to put it)
            return permutations;
        }
        List<Permutation> result = new ArrayList<Permutation>();
        for ( Permutation permutation : permutations ) {
            result.addAll( permutation.withIndicesPermuted( indices ) );
        }
        return result;
    }

    public static List<PairGroup> sortedLonePairsFirst( List<PairGroup> groups ) {
        List<PairGroup> result = new ArrayList<PairGroup>( groups );
        Collections.sort( result, new Comparator<PairGroup>() {
            public int compare( PairGroup a, PairGroup b ) {
                if ( a.isLonePair == b.isLonePair ) {
                    return 0;
                }
                else if ( a.isLonePair ) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        } );
        return result;
    }

    // allow switching of lone pairs with each other, and all other types of bonds with each other
    // NOTE: I recommended double or triple bonds being put in "higher repulsion" spots over single bonds, but this was specifically rejected. -JO
    public static List<Permutation> vseprPermutations( final List<PairGroup> neighbors ) {
        List<Permutation> permutations = new ArrayList<Permutation>();
        permutations.add( Permutation.identity( neighbors.size() ) );

        Function1<PairGroup, Integer> indexOf = new Function1<PairGroup, Integer>() {
            public Integer apply( PairGroup group ) {
                return neighbors.indexOf( group );
            }
        };

        // partition the neighbors into lone pairs and atoms.
        Pair<List<PairGroup>, List<PairGroup>> partitioned = partition( neighbors, new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return group.isLonePair;
            }
        } );
        // this separation looks better in languages where you say "(lonePairs, atoms) = partition(...)"
        List<PairGroup> lonePairs = partitioned._1, atoms = partitioned._2;

        // permute away the lone pairs
        permutations = permuteListWithIndices( permutations, map( lonePairs, indexOf ) );

        // permute away the bonded groups
        permutations = permuteListWithIndices( permutations, map( atoms, indexOf ) );
        return permutations;
    }

    // allow switching of lone pairs with each other, and all other types of bonds with the same type of element
    public static List<Permutation> realPermutations( final List<PairGroup> neighbors ) {
        List<Permutation> permutations = new ArrayList<Permutation>();
        permutations.add( Permutation.identity( neighbors.size() ) );

        Function1<PairGroup, Integer> indexOf = new Function1<PairGroup, Integer>() {
            public Integer apply( PairGroup group ) {
                return neighbors.indexOf( group );
            }
        };

        // allow interchanging of lone pairs
        List<PairGroup> lonePairs = filter( neighbors, new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return group.isLonePair;
            }
        } );
        permutations = permuteListWithIndices( permutations, map( lonePairs, indexOf ) );

        // allow interchanging of pair groups when they have the same chemical element
        List<RealPairGroup> atoms = map( filter( neighbors, new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return !group.isLonePair;
            }
        } ), new Function1<PairGroup, RealPairGroup>() {
            public RealPairGroup apply( PairGroup group ) {
                return (RealPairGroup) group;
            }
        } );
        List<Element> usedElements = unique( map( atoms, new Function1<RealPairGroup, Element>() {
            public Element apply( RealPairGroup realPairGroup ) {
                return realPairGroup.getElement();
            }
        } ) );
        for ( final Element element : usedElements ) {
            List<RealPairGroup> atomsWithElement = filter( atoms, new Function1<RealPairGroup, Boolean>() {
                public Boolean apply( RealPairGroup group ) {
                    return group.getElement() == element;
                }
            } );
            permutations = permuteListWithIndices( permutations, map( atomsWithElement, indexOf ) );
        }

        return permutations;
    }

    public void applyAngleAttractionRepulsion( float tpf ) {
        AttractorModel.applyAttractorForces( groups, tpf, idealOrientations, allowedPermutations, centralAtom.position.get(), true );
    }
}
