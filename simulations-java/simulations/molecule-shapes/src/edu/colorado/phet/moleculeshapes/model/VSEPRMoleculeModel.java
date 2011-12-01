// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.rangeInclusive;

/**
 * A molecule that behaves with a behavior that doesn't discriminate between bond or atom types (only lone pairs vs bonds)
 */
public class VSEPRMoleculeModel extends Molecule {
    @Override public List<ImmutableVector3D> getIdealPositionVectors() {
        return getCorrespondingIdealGeometryVectors();
    }

    @Override public List<Permutation> getAllowablePositionPermutations() {
        List<Permutation> permutations = new ArrayList<Permutation>();
        List<PairGroup> neighbors = getNeighbors( getCentralAtom() );
        permutations.add( Permutation.identity( neighbors.size() ) );

        // permute away the lone pairs
        List<PairGroup> lonePairs = getRadialLonePairs();
        if ( lonePairs.size() > 1 ) {
            List<Permutation> result = new ArrayList<Permutation>();
            for ( Permutation permutation : permutations ) {
                result.addAll( permutation.withIndicesPermuted( rangeInclusive( 0, lonePairs.size() - 1 ) ) );
            }
            permutations = result;
        }

        // permute away the bonded groups
        List<PairGroup> atoms = getRadialAtoms();
        if ( atoms.size() > 1 ) {
            List<Permutation> result = new ArrayList<Permutation>();
            for ( Permutation permutation : permutations ) {
                result.addAll( permutation.withIndicesPermuted( rangeInclusive( lonePairs.size(), neighbors.size() - 1 ) ) );
            }
            permutations = result;
        }
        return permutations;
    }

    @Override public boolean isReal() {
        return false;
    }
}
