package edu.colorado.phet.fractionsintro.buildafraction.model;

import lombok.Data;

/**
 * For keeping track of objects throughout the application.  Cannot use reference identity because object instances change.
 * Cannot use equality test since two objects might have the same state.  See implementation-notes.txt
 *
 * @author Sam Reid
 */
public @Data class ObjectID {
    public final int id;
}