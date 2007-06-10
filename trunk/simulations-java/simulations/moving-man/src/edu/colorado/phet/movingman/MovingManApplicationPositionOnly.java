package edu.colorado.phet.movingman;

import java.io.IOException;

/**
 * Author: Sam Reid
 * Jun 10, 2007, 4:39:05 PM
 */
public class MovingManApplicationPositionOnly extends MovingManApplication{
    public MovingManApplicationPositionOnly( String[] args ) throws IOException {
        super( args );
        getModule().minimizeGraphsExceptPosition();
    }
}
