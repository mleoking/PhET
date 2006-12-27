/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:27:26 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class PopupBarrier implements Potential {


    public double getPotential( int x, int y, int timestep ) {
        return 0;
//        if( timestep < 500 ) {
//            return ( 0 );
//        }
//        if( ( i - XMESH / 2 ) * ( i - XMESH / 2 ) + ( j - YMESH / 2 ) * ( j - YMESH / 2 ) < XMESH * YMESH / 64 ) {
//            return ( 1E4 );
//        }
//        else {
//            return ( 0. );
//        }
    }
}
