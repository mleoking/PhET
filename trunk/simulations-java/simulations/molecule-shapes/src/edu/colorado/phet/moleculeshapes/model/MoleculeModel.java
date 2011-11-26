// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.common.phetcommon.model.event.CompositeNotifier;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;

/**
 * Model of a single-atom-centered molecule which has a certain number of pair groups
 * surrounding it.
 */
public abstract class MoleculeModel {

    public static final int MAX_PAIRS = 6;

    private boolean sortingEnabled = true;

    private List<PairGroup> groups = new ArrayList<PairGroup>();

    protected float repulsionMultiplier = 1;
    protected float attractionMultiplier = 1;

    public final Notifier<PairGroup> onGroupAdded = new Notifier<PairGroup>();
    public final Notifier<PairGroup> onGroupRemoved = new Notifier<PairGroup>();
    public final CompositeNotifier<PairGroup> onGroupChanged = new CompositeNotifier<PairGroup>( onGroupAdded, onGroupRemoved );

    public MoleculeModel() {
    }

    public abstract List<ImmutableVector3D> getIdealPositionVectors();

    public abstract List<Permutation> getAllowablePositionPermutations();

    public void update( final float tpf ) {
        // move based on velocity
        for ( PairGroup group : groups ) {
            double oldDistance = group.position.get().magnitude();
            group.stepForward( tpf );
            group.attractToIdealDistance( tpf, oldDistance );
        }

        // attractive force to the correct position
        double error = AttractorModel.applyAttractorForces( this, tpf * attractionMultiplier, getIdealPositionVectors(), getAllowablePositionPermutations() );

        // factor that basically states "if we are close to an ideal state, force the coulomb force to ignore differences between bonds and lone pairs based on their distance"
        double trueLengthsRatioOverride = Math.max( 0, Math.min( 1, Math.log( error + 1 ) - 0.5 ) );

//        System.out.println( error );

        // repulsion forces
            for ( PairGroup group : groups ) {
                for ( PairGroup otherGroup : groups ) {
                    if ( otherGroup != group ) {
                        group.repulseFrom( otherGroup, tpf * repulsionMultiplier, trueLengthsRatioOverride );
                    }
                }
            }
    }

    public int getStericNumber() {
        return groups.size();
    }

    public void setSortingEnabled( boolean sortingEnabled ) {
        this.sortingEnabled = sortingEnabled;
    }

    //TODO: Unused
    public int getNumberOfPairs() {
        int result = 0;
        for ( PairGroup group : groups ) {
            result += group.getNumberOfPairs();
        }
        return result;
    }

    public ArrayList<PairGroup> getBondedGroups() {
        return getGroups( false );
    }

    public ArrayList<PairGroup> getLonePairs() {
        return getGroups( true );
    }

    public VseprConfiguration getConfiguration() {
        // TODO: refactor to use this
        return new VseprConfiguration( getBondedGroups().size(), getLonePairs().size() );
    }

    public ArrayList<PairGroup> getGroups( final boolean lonePairs ) {
        return new ArrayList<PairGroup>() {{
            for ( PairGroup pair : groups ) {
                if ( pair.isLonePair == lonePairs ) {
                    add( pair );
                }
            }
        }};
    }

    public void addPair( PairGroup pair ) {
        groups.add( pair );

        if ( sortingEnabled ) {
            // sort so that the higher-repulsion groups come first
            Collections.sort( groups, new Comparator<PairGroup>() {
                public int compare( PairGroup a, PairGroup b ) {
                    return new Integer( getSortingKey( a ) ).compareTo( getSortingKey( b ) );
                }
            } );
        }

        // notify
        onGroupAdded.updateListeners( pair );
    }

    //SRR modified 10/24/2011
    //JO said: Also for consistency, MoleculeModel.getSortingKey should not differentiate between different bonds (return what happens for bond order == 1).
    //Original implementation was: return group.bondOrder > 1 ? -group.bondOrder : group.bondOrder;
    private int getSortingKey( PairGroup group ) {
        return group.bondOrder >= 1 ? 1 : 0;
    }

    public void removePair( PairGroup pair ) {
        groups.remove( pair );

        // notify
        onGroupRemoved.updateListeners( pair );
    }

    public void removeAllPairs() {
        while ( !getGroups().isEmpty() ) {
            removePair( getGroups().get( 0 ) );
        }
    }

    public List<PairGroup> getGroups() {
        return groups;
    }

    public List<ImmutableVector3D> getCorrespondingIdealGeometryVectors() {
        return new VseprConfiguration( getBondedGroups().size(), getLonePairs().size() ).geometry.unitVectors;
    }

    /**
     * @param bondOrder Bond order of potential pair group to add
     * @return Whether the pair group can be added, or whether this molecule would go over its pair limit
     */
    public boolean wouldAllowBondOrder( int bondOrder ) {
        return getStericNumber() < MAX_PAIRS;
//        return getNumberOfPairs() + ( bondOrder == 0 ? 1 : bondOrder ) <= MAX_PAIRS;
    }
}
