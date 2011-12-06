// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.common.phetcommon.model.event.CompositeNotifier;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.*;

/**
 * Model of a single-atom-centered molecule which has a certain number of pair groups
 * surrounding it.
 */
public abstract class Molecule {

    public static final int MAX_PAIRS = 6;

    // all of the pair groups
    private List<PairGroup> groups = new ArrayList<PairGroup>();

    // bonds between pair groups. for lone pairs, this doesn't mean an actual molecular bond, so we just have order 0
    private List<Bond<PairGroup>> bonds = new ArrayList<Bond<PairGroup>>();

    private PairGroup centralAtom; // act like this is final.

    // TODO: remove the multipliers from this part of the model
    protected float repulsionMultiplier = 1;
    protected float attractionMultiplier = 1;

    public final Notifier<Bond<PairGroup>> onBondAdded = new Notifier<Bond<PairGroup>>();
    public final Notifier<Bond<PairGroup>> onBondRemoved = new Notifier<Bond<PairGroup>>();
    public final CompositeNotifier<Bond<PairGroup>> onBondChanged = new CompositeNotifier<Bond<PairGroup>>( onBondAdded, onBondRemoved );

    public final Notifier<PairGroup> onGroupAdded = new Notifier<PairGroup>();
    public final Notifier<PairGroup> onGroupRemoved = new Notifier<PairGroup>();
    public final CompositeNotifier<PairGroup> onGroupChanged = new CompositeNotifier<PairGroup>( onGroupAdded, onGroupRemoved );

    public Molecule() {
    }

    public abstract List<ImmutableVector3D> getIdealPositionVectors();

    public abstract List<Permutation> getAllowablePositionPermutations();

    public void update( final float tpf ) {
        List<PairGroup> nonCentralGroups = filter( groups, new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return group != getCentralAtom();
            }
        } );

        // move based on velocity
        for ( PairGroup group : nonCentralGroups ) {
            Bond<PairGroup> parentBond = getParentBond( group );
            ImmutableVector3D origin = parentBond.getOtherAtom( group ).position.get();

            double oldDistance = ( group.position.get().minus( origin ) ).magnitude();
            group.stepForward( tpf );
            group.attractToIdealDistance( tpf, oldDistance, parentBond );
        }

        // attractive force to the correct position
        double error = AttractorModel.applyAttractorForces( concat( getRadialLonePairs(), getRadialAtoms() ), tpf * attractionMultiplier, getIdealPositionVectors(), getAllowablePositionPermutations() );

        // factor that basically states "if we are close to an ideal state, force the coulomb force to ignore differences between bonds and lone pairs based on their distance"
        double trueLengthsRatioOverride = Math.max( 0, Math.min( 1, Math.log( error + 1 ) - 0.5 ) );

        // repulsion forces
        for ( PairGroup group : nonCentralGroups ) {
            for ( PairGroup otherGroup : nonCentralGroups ) {
                if ( otherGroup != group ) {
                    group.repulseFrom( otherGroup, tpf * repulsionMultiplier, trueLengthsRatioOverride );
                }
            }
        }
    }

    // the number of surrounding pair groups
    public int getStericNumber( PairGroup group ) {
        return getBonds( group ).size();
    }

    public List<Bond<PairGroup>> getBonds() {
        return bonds;
    }

    // all bonds to the pair group
    public List<Bond<PairGroup>> getBonds( final PairGroup group ) {
        return filter( bonds, new Function1<Bond<PairGroup>, Boolean>() {
            public Boolean apply( Bond<PairGroup> bond ) {
                return bond.contains( group );
            }
        } );
    }

    // all neighboring pair groups
    public List<PairGroup> getNeighbors( final PairGroup group ) {
        return map( getBonds( group ), new Function1<Bond<PairGroup>, PairGroup>() {
            public PairGroup apply( Bond<PairGroup> bond ) {
                return bond.getOtherAtom( group );
            }
        } );
    }

    public List<PairGroup> getAllNonCentralAtoms() {
        return filter( getGroups(), new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return !group.isLonePair && group != getCentralAtom();
            }
        } );
    }

    public List<PairGroup> getAllLonePairs() {
        return filter( getGroups(), new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup group ) {
                return group.isLonePair;
            }
        } );
    }

    // atoms surrounding the center atom
    public List<PairGroup> getRadialAtoms() {
        return getNeighboringAtoms( getCentralAtom() );
    }

    public List<PairGroup> getNeighboringAtoms( PairGroup group ) {
        return filter( getNeighbors( getCentralAtom() ), new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup pairGroup ) {
                return !pairGroup.isLonePair;
            }
        } );
    }

    public List<PairGroup> getLonePairNeighbors( PairGroup group ) {
        return filter( getNeighbors( getCentralAtom() ), new Function1<PairGroup, Boolean>() {
            public Boolean apply( PairGroup pairGroup ) {
                return pairGroup.isLonePair;
            }
        } );
    }

    public List<PairGroup> getRadialLonePairs() {
        return getLonePairNeighbors( getCentralAtom() );
    }

    public GeometryConfiguration getGeometryConfiguration( PairGroup group ) {
        return GeometryConfiguration.getConfiguration( getStericNumber( group ) );
    }

    public VseprConfiguration getCentralVseprConfiguration() {
        return getVseprConfiguration( getCentralAtom() );
    }

    public VseprConfiguration getVseprConfiguration( PairGroup group ) {
        return new VseprConfiguration( getNeighboringAtoms( group ).size(), getLonePairNeighbors( group ).size() );
    }

    // get the bond to the more central "parent"
    public Bond<PairGroup> getParentBond( final PairGroup group ) {
        // assumes we have simple atoms (star-shaped) with terminal lone pairs
        if ( group.isLonePair ) {
            return getBonds( group ).get( 0 );
        }
        else {
            return firstOrNull( getBonds( group ), new Function1<Bond<PairGroup>, Boolean>() {
                public Boolean apply( Bond<PairGroup> bond ) {
                    return bond.getOtherAtom( group ) == getCentralAtom();
                }
            } );
        }
    }

    // get the more central "parent" group
    public PairGroup getParent( final PairGroup group ) {
        return getParentBond( group ).getOtherAtom( group );
    }

    // add in the central atom
    public void addCentralAtom( PairGroup group ) {
        centralAtom = group;
        addGroup( group );
    }

    // add a group with a bond to another group at the same time
    public void addGroup( PairGroup group, PairGroup parent, int bondOrder ) {
        addGroup( group );
        addBond( group, parent, bondOrder );
    }

    // add a group with a bond to another group at the same time, with a specific distance in angstroms
    public void addGroup( PairGroup group, PairGroup parent, int bondOrder, double bondLength ) {
        addGroup( group );
        addBond( new Bond<PairGroup>( group, parent, bondOrder, bondLength ) );
    }

    public void addGroup( PairGroup group ) {
        // always add the central group first
        assert getCentralAtom() != null;

        groups.add( group );

        // notify
        onGroupAdded.updateListeners( group );
    }

    public void addBond( Bond<PairGroup> bond ) {
        bonds.add( bond );

        onBondAdded.updateListeners( bond );
    }

    public void addBond( PairGroup a, PairGroup b, int order ) {
        addBond( new Bond<PairGroup>( a, b, order ) );
    }

    public void removeBond( Bond<PairGroup> bond ) {
        bonds.remove( bond );

        onBondRemoved.updateListeners( bond );
    }

    public PairGroup getCentralAtom() {
        return centralAtom;
    }

    public void removeGroup( PairGroup group ) {
        assert getCentralAtom() != group;

        // remove all of its bonds first
        for ( Bond<PairGroup> bond : getBonds( group ) ) {
            removeBond( bond );
        }

        groups.remove( group );

        // notify
        onGroupRemoved.updateListeners( group );
    }

    public void removeAllGroups() {
        for ( PairGroup group : new ArrayList<PairGroup>( getGroups() ) ) {
            if ( group != getCentralAtom() ) {
                removeGroup( group );
            }
        }
    }

    public List<PairGroup> getGroups() {
        return groups;
    }

    public List<ImmutableVector3D> getCorrespondingIdealGeometryVectors() {
        return new VseprConfiguration( getRadialAtoms().size(), getRadialLonePairs().size() ).geometry.unitVectors;
    }

    /**
     * @param bondOrder Bond order of potential pair group to add
     * @return Whether the pair group can be added, or whether this molecule would go over its pair limit
     */
    public boolean wouldAllowBondOrder( int bondOrder ) {
        return getStericNumber( getCentralAtom() ) < MAX_PAIRS;
    }

    public List<PairGroup> getDistantLonePairs() {
        return subtract( getAllLonePairs(), getLonePairNeighbors( getCentralAtom() ) );
    }

    // should this molecule be displayed in a "real" style, or not? If "true", all atoms are expected to be represented with RealPairGroups
    public abstract boolean isReal();
}
