// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.moleculeshapes.model.AttractorModel.ResultMapping;

/**
 * Represents a physically malleable version of a real molecule, with lone pairs if necessary. Different from the
 * RealMolecule class, which is a static representation that cannot be worked with.
 */
public class RealMoleculeModel extends MoleculeModel {

    private final RealMolecule realMolecule;

    // stores the ideal position vectors for both the bonds and lone pairs
    private List<ImmutableVector3D> idealPositionVectors;

    public RealMoleculeModel( final RealMolecule realMolecule ) {
        this.realMolecule = realMolecule;

        final int numLonePairs = realMolecule.getLonePairCount();
        final int numBonds = realMolecule.getBonds().size();

        idealPositionVectors = new ArrayList<ImmutableVector3D>();

        // add in bonds
        for ( Bond<Atom3D> bond : realMolecule.getBonds() ) {
            Atom3D atom = bond.getOtherAtom( realMolecule.getCentralAtom() );
            ImmutableVector3D normalizedPosition = atom.position.get().normalized();
            idealPositionVectors.add( normalizedPosition );
            addPair( new RealPairGroup( normalizedPosition.times( PairGroup.BONDED_PAIR_DISTANCE ), bond.order, atom.getElement() ) );
        }

        // all of the ideal vectors (including for lone pairs)
        final List<ImmutableVector3D> idealModelVectors = new VseprConfiguration( numBonds, numLonePairs ).geometry.unitVectors;

        // ideal vectors excluding lone pairs (just for the bonds)
        List<ImmutableVector3D> idealModelBondVectors = new ArrayList<ImmutableVector3D>() {{
            for ( int i = numLonePairs; i < idealModelVectors.size(); i++ ) {
                add( idealModelVectors.get( i ) );
            }
        }};

        ResultMapping mapping = AttractorModel.findClosestMatchingConfiguration( this, idealModelBondVectors, Permutation.permutations( idealModelBondVectors.size() ) );

        Matrix rotation = mapping.rotation;

        // add in lone pairs in their correct "initial" positions
        for ( int i = 0; i < numLonePairs; i++ ) {
            addPair( new PairGroup( mapping.rotateVector( idealModelVectors.get( i ) ).times( PairGroup.LONE_PAIR_DISTANCE ), 0, false ) );
        }
    }

    @Override public List<ImmutableVector3D> getIdealPositionVectors() {
        return idealPositionVectors;
    }

    @Override public List<Permutation> getAllowablePositionPermutations() {
        return new ArrayList<Permutation>() {{
            add( Permutation.identity( idealPositionVectors.size() ) );
        }};
    }
}
