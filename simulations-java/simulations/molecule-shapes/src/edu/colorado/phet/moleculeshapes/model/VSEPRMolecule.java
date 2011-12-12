// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.filter;

/**
 * A molecule that behaves with a behavior that doesn't discriminate between bond or atom types (only lone pairs vs bonds)
 */
public class VSEPRMolecule extends Molecule {

    private final double bondLengthOverride;
    private final boolean useBondLengthOverride;

    public VSEPRMolecule() {
        bondLengthOverride = 0;
        useBondLengthOverride = false;
    }

    public VSEPRMolecule( double bondLengthOverride ) {
        this.bondLengthOverride = bondLengthOverride;
        useBondLengthOverride = true;
    }

    @Override public LocalShape getLocalShape( PairGroup atom ) {
        return getLocalVSEPRShape( atom );
    }

    @Override public void update( float tpf ) {
        super.update( tpf );

        List<PairGroup> nonCentralGroups = filter( getGroups(), new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return group != getCentralAtom();
            }
        } );

        for ( PairGroup atom : getAtoms() ) {
            if ( getNeighbors( atom ).size() > 1 ) {
                // attractive force to the correct position
                double error = getLocalShape( atom ).applyAttraction( tpf );

                // factor that basically states "if we are close to an ideal state, force the coulomb force to ignore differences between bonds and lone pairs based on their distance"
                double trueLengthsRatioOverride = Math.max( 0, Math.min( 1, Math.log( error + 1 ) - 0.5 ) );

                for ( PairGroup group : nonCentralGroups ) {
                    for ( PairGroup otherGroup : nonCentralGroups ) {
                        if ( otherGroup != group && group != getCentralAtom() ) {
                            group.repulseFrom( otherGroup, tpf, trueLengthsRatioOverride );
                        }
                    }
                }
            }
        }

    }

    @Override public boolean isReal() {
        return false;
    }

    @Override public Option<Float> getMaximumBondLength() {
        if ( useBondLengthOverride ) {
            return new Some<Float>( (float) bondLengthOverride );
        }
        else {
            return new Some<Float>( (float) PairGroup.BONDED_PAIR_DISTANCE );
        }
    }
}
