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
public class VSEPRMoleculeModel extends MoleculeModel {
    @Override public List<ImmutableVector3D> getIdealPositionVectors() {
        return getCorrespondingIdealGeometryVectors();
    }

    @Override public List<Permutation> getAllowablePositionPermutations() {
        List<Permutation> permutations = new ArrayList<Permutation>();
        permutations.add( Permutation.identity( getGroups().size() ) );

        // permute away the lone pairs
        if ( getLonePairs().size() > 1 ) {
            List<Permutation> result = new ArrayList<Permutation>();
            for ( Permutation permutation : permutations ) {
                result.addAll( permutation.withIndicesPermuted( rangeInclusive( 0, getLonePairs().size() - 1 ) ) );
            }
            permutations = result;
        }

        // permute away the bonded groups
        if ( getBondedGroups().size() > 1 ) {
            List<Permutation> result = new ArrayList<Permutation>();
            for ( Permutation permutation : permutations ) {
                result.addAll( permutation.withIndicesPermuted( rangeInclusive( getLonePairs().size(), getGroups().size() - 1 ) ) );
            }
            permutations = result;
        }
        return permutations;
    }
}
