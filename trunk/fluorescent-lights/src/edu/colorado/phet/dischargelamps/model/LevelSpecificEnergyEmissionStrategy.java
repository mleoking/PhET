/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.quantum.model.AtomicState;

import java.util.*;

/**
 * LevelSpecificEnergyEmissionStrategy
 * <p/>
 * An energy emission strategy in which the probability of the atom going from
 * one state to another is different for the transition from each level to
 * every lower level.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LevelSpecificEnergyEmissionStrategy {
    private static final Random random = new Random();

    private Map originStateToTargetStates = new HashMap();

    public LevelSpecificEnergyEmissionStrategy( AtomicState[] states,
                                                double[][] probabilities ) {
        // For each state, build a TreeMap of probabilities to target states
        // This will be sorted by the probability, in ascending order
        for( int i = 0; i < states.length; i++ ) {
            TreeMap probToTargetState = new TreeMap( new ProbabilityComparator() );
            for( int j = 0; j < i; j++ ) {
                probToTargetState.put( new Double(probabilities[i][j]),
                                       states[j] );
            }
            originStateToTargetStates.put( states[i], probToTargetState );
        }
    }

    public AtomicState getTargetState( AtomicState originState ) {
        Map targetMap = (Map)originStateToTargetStates.get( originState );
        AtomicState targetState = (AtomicState)targetMap.get( new Double( random.nextDouble() ));
        return targetState;
    }

    private static class ProbabilityComparator implements Comparator {
        public int compare( Object o1, Object o2 ) {
            return 0;
        }
    }
}
