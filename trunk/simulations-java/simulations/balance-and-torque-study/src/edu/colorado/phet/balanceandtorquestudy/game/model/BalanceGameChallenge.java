// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorquestudy.common.model.ColumnState;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;

/**
 * Base class for a single "challenge" (a.k.a. problem) that is presented to
 * the user during the balance game.
 *
 * @author John Blanco
 */
public abstract class BalanceGameChallenge {

    private static final int DEFAULT_NUMBER_OF_ATTEMPTS_ALLOWED = 2;

    // List of masses that will initially be sitting on the balance, and which
    // the user will not manipulate.
    public final List<MassDistancePair> fixedMassDistancePairs = new ArrayList<MassDistancePair>();

    // List of masses that the user will move into the appropriate positions
    // in order to balance out the other masses.
    public final List<Mass> movableMasses = new ArrayList<Mass>();

    // A configuration in which the movable masses balance the fixed masses.
    // For some challenges, this is what will be displayed to the user if they
    // ask to see a correct answer.
    public final List<MassDistancePair> balancedConfiguration = new ArrayList<MassDistancePair>();

    // State of the support column or columns when the challenge is initially
    // presented to the user.
    public final ColumnState initialColumnState;

    // Number of times that the user is allowed to attempt to solve this
    // challenge.
    public final int maxAttemptsAllowed;

    static int instanceCount = 0;
    public final int challengeID;

    /**
     * Constructor.
     *
     * @param initialColumnState
     */
    public BalanceGameChallenge( ColumnState initialColumnState ) {
        this( initialColumnState, DEFAULT_NUMBER_OF_ATTEMPTS_ALLOWED );
    }

    /**
     * Constructor.
     *
     * @param initialColumnState
     */
    public BalanceGameChallenge( ColumnState initialColumnState, int maxAttemptsAllowed ) {
        this.initialColumnState = initialColumnState;
        this.maxAttemptsAllowed = maxAttemptsAllowed;
        challengeID = instanceCount++;
    }

    /**
     * Returns true if the specified challenge uses the same masses as this
     * challenge.  Note that "same masses" means the same classes, not just
     * the same values.  For example, if both challenges have a movable mass
     * that weigh 60kg but one is a rock and the other is a person, this will
     * return false.
     *
     * @param that
     * @return true if same mass values are used in the given challenge.
     */
    public boolean usesSameMasses( BalanceGameChallenge that ) {
        return usesSameFixedMasses( that ) && usesSameMovableMasses( that );
    }

    /**
     * Returns true if the specified challenge uses the same fixed masses.
     * This is used for various equivalence comparisons.
     *
     * @param that
     * @return true if same fixed mass values are used in the given challenge.
     */
    public boolean usesSameFixedMasses( BalanceGameChallenge that ) {
        if ( this == that ) {
            return true;
        }

        List<Mass> thisFixedMasses = getFixedMassList();
        List<Mass> thatFixedMasses = that.getFixedMassList();

        if ( !containsEquivalentMasses( thisFixedMasses, thatFixedMasses ) ) {
            return false;
        }

        // If we made it to here, the masses are the same.
        return true;
    }

    /**
     * Compares the fixed masses and their distances to those of the given
     * challenge and, if all fixed masses and distances are the same, 'true'
     * is returned.
     *
     * @param that
     * @return
     */
    public boolean usesSameFixedMassesAndDistances( BalanceGameChallenge that ) {
        if ( this == that ) {
            return true;
        }

        if ( this.fixedMassDistancePairs.size() != that.fixedMassDistancePairs.size() ) {
            // If the lists are unequal in size, then the set of fixed masses
            // and distances can't be equal.
            return false;
        }

        int matchCount = 0;
        for ( MassDistancePair thisFixedMassDistancePair : fixedMassDistancePairs ) {
            for ( MassDistancePair thatFixedMassDistancePair : that.fixedMassDistancePairs ) {
                if ( thisFixedMassDistancePair.mass.getMass() == thatFixedMassDistancePair.mass.getMass() &&
                     thisFixedMassDistancePair.distance == thatFixedMassDistancePair.distance ) {
                    matchCount++;
                }
            }
        }

        // If a match was found for all fixed mass distance pairs, then the
        // lists are equivalent.
        return matchCount == fixedMassDistancePairs.size();
    }

    public boolean usesSameMovableMasses( BalanceGameChallenge that ) {
        return containsEquivalentMasses( movableMasses, that.movableMasses );
    }

    /**
     * Convenience function for determining whether an equivalent mass is
     * contained on the list.  The 'contains' function for the mass list can't
     * be used because it relies on the 'equals' function, which needs to be
     * more specific than just matching class and mass value.
     */
    private boolean containsEquivalentMass( Mass mass, List<Mass> massList ) {
        for ( Mass massFromList : massList ) {
            if ( mass.getMass() == massFromList.getMass() && mass.getClass() == massFromList.getClass() ) {
                // These masses are equivalent, so the list contains an equivalent mass.
                return true;
            }
        }
        return false;
    }

    /**
     * Test two mass lists to see if they contain equivalent masses.
     */
    private boolean containsEquivalentMasses( List<Mass> massList1, List<Mass> massList2 ) {
        if ( massList1.size() != massList2.size() ) {
            return false;
        }
        for ( Mass mass : massList1 ) {
            if ( !containsEquivalentMass( mass, massList2 ) ) {
                return false;
            }
        }
        return true;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        BalanceGameChallenge that = (BalanceGameChallenge) o;

        for ( MassDistancePair massDistancePair : fixedMassDistancePairs ) {
            if ( !that.fixedMassDistancePairs.contains( massDistancePair ) ) {
                return false;
            }
        }

        if ( movableMasses.size() != that.movableMasses.size() ) {
            return false;
        }

        List<Mass> copyOfThatMovableMasses = new ArrayList<Mass>( that.movableMasses );
        for ( Mass thisMass : movableMasses ) {
            for ( Mass thatMass : new ArrayList<Mass>( copyOfThatMovableMasses ) ) {
                if ( thisMass.getMass() == thatMass.getMass() ) {
                    if ( copyOfThatMovableMasses.contains( thatMass ) ) {
                        copyOfThatMovableMasses.remove( thatMass );
                        break;
                    }
                }
            }
        }

        if ( copyOfThatMovableMasses.size() != 0 ) {
            return false;
        }

        return balancedConfiguration.equals( that.balancedConfiguration );
    }

    /**
     * Get configuration information about how this challenge should be
     * displayed in the view.
     *
     * @return
     */
    public abstract ChallengeViewConfig getChallengeViewConfig();

    /**
     * Get a list of the fixed masses for this challenge (without their
     * distances).
     *
     * @return
     */
    public List<Mass> getFixedMassList() {
        List<Mass> fixedMassList = new ArrayList<Mass>();
        for ( MassDistancePair fixedMassDistancePair : fixedMassDistancePairs ) {
            fixedMassList.add( fixedMassDistancePair.mass );
        }
        return fixedMassList;
    }

    /**
     * Get the sum of the mass for all the fixed masses in this challenge.
     *
     * @return
     */
    public double getFixedMassValueTotal() {
        double totalMass = 0;
        for ( MassDistancePair massDistancePair : fixedMassDistancePairs ) {
            totalMass += massDistancePair.mass.getMass();
        }
        return totalMass;
    }

    /**
     * Get the model component for this challenge.  Subclasses must override.
     *
     * @return model component ID for this challenge.
     */
    abstract public IModelComponentType getModelComponentType();

    /**
     * Used for sim sharing support.
     *
     * @return A string suitable for sim sharing messages that indicates the
     *         correct answer.
     */
    abstract public String getCorrectAnswerString();
}
