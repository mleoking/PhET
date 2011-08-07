// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoleculeModel {
    private List<ElectronPair> pairs = new ArrayList<ElectronPair>();
    private List<Listener> listeners = new ArrayList<Listener>();

    public MoleculeModel() {
    }

    private int counter = 0;

    public void update( final float tpf ) {
        for ( ElectronPair pair : pairs ) {
            // run our fake physics
            pair.stepForward( tpf );
            for ( ElectronPair otherPair : pairs ) {
                if ( otherPair != pair ) {
                    pair.repulseFrom( otherPair, tpf );
                }
            }
            pair.attractToDistance( tpf );
        }
        AttractorModel.applyAttractorForces( this, tpf );
        if ( counter++ % 50 == 0 ) {
            VseprConfiguration config = new VseprConfiguration( getBondedPairs().size(), getLonePairs().size() );
            System.out.println( "Testing " + config.name + "(" + config.geometry.name + "): " + config.matchesElectronPairs( pairs, 0.15 ) );
        }
    }

    public ArrayList<ElectronPair> getBondedPairs() {
        return getPairs( false );
    }

    public ArrayList<ElectronPair> getLonePairs() {
        return getPairs( true );
    }

    public ArrayList<ElectronPair> getPairs( final boolean lonePairs ) {
        return new ArrayList<ElectronPair>() {{
            for ( ElectronPair pair : pairs ) {
                if ( pair.isLonePair == lonePairs ) {
                    add( pair );
                }
            }
        }};
    }

    public void addPair( ElectronPair pair ) {
        pairs.add( pair );

        // sort so that the lone pairs come first
        Collections.sort( pairs, new Comparator<ElectronPair>() {
            public int compare( ElectronPair a, ElectronPair b ) {
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
            listener.onPairAdded( pair );
        }
    }

    public void removePair( ElectronPair pair ) {
        pairs.remove( pair );

        // notify
        for ( Listener listener : listeners ) {
            listener.onPairRemoved( pair );
        }
    }

    public List<ElectronPair> getPairs() {
        return pairs;
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
        public void onPairAdded( ElectronPair pair );

        public void onPairRemoved( ElectronPair pair );
    }

    public static class Adapter implements Listener {
        public void onPairAdded( ElectronPair pair ) {
        }

        public void onPairRemoved( ElectronPair pair ) {
        }
    }
}
