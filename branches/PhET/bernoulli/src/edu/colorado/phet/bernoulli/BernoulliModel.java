package edu.colorado.phet.bernoulli;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 11:03:16 AM
 * To change this template use Options | File Templates.
 */
public class BernoulliModel {
    ArrayList vessels = new ArrayList();

    public void addVessel( Vessel v ) {
        vessels.add( v );
//        v.addVesselListener(this);
    }

    public Vessel vesselAt( double x, double y ) {
        for( int i = 0; i < vessels.size(); i++ ) {
            Vessel vessel = (Vessel)vessels.get( i );
            if( vessel.waterContainsPoint( x, y ) ) {
                return vessel;
            }
        }
        return null;
    }
}
