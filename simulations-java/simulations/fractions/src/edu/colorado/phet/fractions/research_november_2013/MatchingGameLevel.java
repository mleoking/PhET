// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.util.ArrayList;

/**
 * Created by Sam on 10/22/13.
 */
public class MatchingGameLevel {

    public final ArrayList<MatchingGameGuess> guesses = new ArrayList<MatchingGameGuess>();

    public MatchingGameLevel( int id, int level, String targets ) {
        this.id = id;
        this.level = level;
        this.targets = targets;
    }

    public final int id;
    public final int level;
    public final String targets;

    @Override public String toString() {
        return "BAFLevel{" +
               "id=" + id +
               ", level=" + level +
               ", targets='" + targets + '\'' +
               '}';
    }

    public void addGuess( MatchingGameGuess matchingGameGuess ) {
        guesses.add( matchingGameGuess );
    }
}
