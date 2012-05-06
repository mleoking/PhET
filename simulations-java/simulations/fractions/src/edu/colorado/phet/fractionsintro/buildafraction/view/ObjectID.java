package edu.colorado.phet.fractionsintro.buildafraction.view;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class ObjectID {
    public final int id;
    private static int count = 0;

    public static ObjectID nextID() { return new ObjectID( count++ ); }
}