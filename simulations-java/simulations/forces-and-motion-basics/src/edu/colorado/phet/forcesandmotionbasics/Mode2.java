package edu.colorado.phet.forcesandmotionbasics;

/**
 * @author Sam Reid
 */
public class Mode2 extends PositionMap {
    public Mode2() {super( "Mode 2" );}

    @Override public double getTilePosition( final Double x ) {
        return getBlockPosition( x ) - x;
    }

    @Override public double getBlockPosition( final Double x ) {
        if ( x > 2 ) { return 2; }
        else if ( x < -2 ) { return -2; }
        else { return x; }
    }

    @Override public double getCloudPosition( final Double x ) {
        return getTilePosition( x ) / 4;
    }
}