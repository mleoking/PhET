// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.moleculeshapes.util.CompositeNotifier;
import edu.colorado.phet.moleculeshapes.util.Notifier;

/**
 * Model of a single-atom-centered molecule which has a certain number of pair groups
 * surrounding it.
 */
public class MoleculeModel {

    public static final int MAX_PAIRS = 6;

    private List<PairGroup> groups = new ArrayList<PairGroup>();

    public final Notifier<PairGroup> onGroupAdded = new Notifier<PairGroup>();
    public final Notifier<PairGroup> onGroupRemoved = new Notifier<PairGroup>();
    public final Notifier<PairGroup> onGroupChanged = new CompositeNotifier<PairGroup>( onGroupAdded, onGroupRemoved );

    public MoleculeModel() {
    }

    public void update( final float tpf ) {
        // move based on velocity
        for ( PairGroup group : groups ) {
            double oldDistance = group.position.get().magnitude();
            group.stepForward( tpf );
            group.attractToIdealDistance( tpf, oldDistance );
        }

        // attractive force to the correct position
        double error = AttractorModel.applyAttractorForces( this, tpf );

        // factor that basically states "if we are close to an ideal state, force the coulomb force to ignore differences between bonds and lone pairs based on their distance"
        double trueLengthsRatioOverride = Math.max( 0, Math.min( 1, Math.sqrt( error ) ) );

        // repulsion forces
        for ( PairGroup group : groups ) {
            for ( PairGroup otherGroup : groups ) {
                if ( otherGroup != group ) {
                    group.repulseFrom( otherGroup, tpf, trueLengthsRatioOverride );
                }
            }
        }
    }

    public int getStericNumber() {
        return groups.size();
    }

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

        // sort so that the higher-repulsion groups come first
        Collections.sort( groups, new Comparator<PairGroup>() {
            public int compare( PairGroup a, PairGroup b ) {
                return new Integer( getSortingKey( a ) ).compareTo( getSortingKey( b ) );
            }
        } );

        // notify
        onGroupAdded.fire( pair );
    }

    private int getSortingKey( PairGroup group ) {
        // triple bonds lowest, then double bonds, then lone pairs, then single bonds
        return group.bondOrder > 1 ? -group.bondOrder : group.bondOrder;
    }

    public void removePair( PairGroup pair ) {
        groups.remove( pair );

        // notify
        onGroupRemoved.fire( pair );
    }

    public List<PairGroup> getGroups() {
        return groups;
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
