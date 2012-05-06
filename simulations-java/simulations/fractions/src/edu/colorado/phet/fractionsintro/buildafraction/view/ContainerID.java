package edu.colorado.phet.fractionsintro.buildafraction.view;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class ContainerID {
    public final int id;
    private static int count = 0;

    public static ContainerID nextID() { return new ContainerID( count++ ); }
}