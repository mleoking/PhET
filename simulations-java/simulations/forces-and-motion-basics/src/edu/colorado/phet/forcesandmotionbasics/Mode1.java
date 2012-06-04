package edu.colorado.phet.forcesandmotionbasics;

/**
 * @author Sam Reid
 */
public class Mode1 extends PositionMap {
    public Mode1() {super( "Mode 1" );}

    @Override public double getTilePosition( final Double x ) {
        return -x;
    }

    @Override public double getBlockPosition( final Double x ) {
        return 0;
    }

    @Override public double getCloudPosition( final Double x ) {
        return -x / 4;
    }
}