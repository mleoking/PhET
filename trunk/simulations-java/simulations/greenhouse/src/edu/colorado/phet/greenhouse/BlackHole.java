/**
 * Class: BlackHole
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 13, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class BlackHole extends BasicPhotonAbsorber {

    private GreenhouseModel model;
    private Rectangle2D.Double eventHorizon = new Rectangle2D.Double();

    public BlackHole( GreenhouseModel model ) {
        this.model = model;
    }

    public void stepInTime( double v ) {

        eventHorizon.setRect( model.getBounds().x - 10, model.getBounds().y - 10,
                              model.getBounds().getWidth()+ 20,
                              model.getBounds().getWidth()+ 20 );
        // If a photon is way outside the view, delete it
        ArrayList photonsToRemove = new ArrayList();
        for( int i = 0; i < model.getPhotons().size(); i++ ) {
            Photon photon = (Photon)model.getPhotons().get( i );
            if( !eventHorizon.contains( photon.getLocation() ) ) {
                photonsToRemove.add( photon );
            }
        }
        for( int i = 0; i < photonsToRemove.size(); i++ ) {
            Photon photon = (Photon)photonsToRemove.get( i );
            notifyListeners( photon );
            model.removePhoton( photon );
//            model.getPhotons().remove( photon );
        }
    }
}
