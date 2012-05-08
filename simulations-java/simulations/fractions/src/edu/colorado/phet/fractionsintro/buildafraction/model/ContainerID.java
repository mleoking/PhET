package edu.colorado.phet.fractionsintro.buildafraction.model;

/**
 * @author Sam Reid
 */
public class ContainerID extends ObjectID {
    public ContainerID( final int id ) { super( id ); }

    public static int count = 0;

    public static ContainerID nextID() { return new ContainerID( count++ ); }
}