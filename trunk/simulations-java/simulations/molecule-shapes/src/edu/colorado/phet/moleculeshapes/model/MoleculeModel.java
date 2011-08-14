// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoleculeModel {

    public static final int MAX_PAIRS = 6;

    private List<PairGroup> groups = new ArrayList<PairGroup>();
    private List<Listener> listeners = new ArrayList<Listener>();

    public MoleculeModel() {
    }

    public void update( final float tpf ) {
        for ( PairGroup group : groups ) {
            // run our fake physics
            group.stepForward( tpf );
            for ( PairGroup otherGroup : groups ) {
                if ( otherGroup != group ) {
                    group.repulseFrom( otherGroup, tpf );
                }
            }
            group.attractToDistance( tpf );
        }
        AttractorModel.applyAttractorForces( this, tpf );
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

        // sort so that the lone pairs come first
        Collections.sort( groups, new Comparator<PairGroup>() {
            public int compare( PairGroup a, PairGroup b ) {
                if ( a.isLonePair == b.isLonePair ) {
                    return 0;
                }
                if ( a.isLonePair ) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        } );

        // notify
        for ( Listener listener : listeners ) {
            listener.onGroupAdded( pair );
        }
    }

    public void removePair( PairGroup pair ) {
        groups.remove( pair );

        // notify
        for ( Listener listener : listeners ) {
            listener.onGroupRemoved( pair );
        }
    }

    public List<PairGroup> getGroups() {
        return groups;
    }

    /**
     * @param bondOrder Bond order of potential pair group to add
     * @return Whether the pair group can be added, or whether this molecule would go over its pair limit
     */
    public boolean wouldAllowBondOrder( int bondOrder ) {
        return getNumberOfPairs() + ( bondOrder == 0 ? 1 : bondOrder ) <= MAX_PAIRS;
    }

    /*---------------------------------------------------------------------------*
    * listeners
    *----------------------------------------------------------------------------*/

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onGroupAdded( PairGroup group );

        public void onGroupRemoved( PairGroup group );
    }

    public static class Adapter implements Listener {
        public void onGroupAdded( PairGroup group ) {
        }

        public void onGroupRemoved( PairGroup group ) {
        }
    }
}
