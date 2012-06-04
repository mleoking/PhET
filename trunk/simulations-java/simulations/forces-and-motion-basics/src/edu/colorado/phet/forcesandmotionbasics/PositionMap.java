package edu.colorado.phet.forcesandmotionbasics;

/**
 * @author Sam Reid
 */
public abstract class PositionMap {
    public final String name;

    protected PositionMap( final String name ) {this.name = name;}

    public abstract double getTilePosition( final Double x );

    public abstract double getBlockPosition( final Double x );

    public abstract double getCloudPosition( final Double x );
}