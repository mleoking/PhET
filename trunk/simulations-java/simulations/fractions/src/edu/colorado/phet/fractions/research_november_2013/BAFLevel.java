// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

/**
 * Created by Sam on 10/22/13.
 */
public class BAFLevel {

    public BAFLevel( int id, int level, String type, String targets ) {
        this.id = id;
        this.level = level;
        this.type = type;
        this.targets = targets;
    }

    public final int id;
    public final int level;
    public final String type;
    public final String targets;

    @Override public String toString() {
        return "BAFLevel{" +
               "id=" + id +
               ", level=" + level +
               ", type='" + type + '\'' +
               ", targets='" + targets + '\'' +
               '}';
    }
}
