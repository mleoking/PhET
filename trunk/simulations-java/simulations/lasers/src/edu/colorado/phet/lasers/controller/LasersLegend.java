package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.view.AbstractLegend;

/**
 * Created by: Sam
 * May 25, 2008 at 11:28:22 PM
 */
public class LasersLegend extends AbstractLegend {
    public LasersLegend() {
        add( getAtomImage(), "Legend.atom" );
//        add( getElectronImage(), "Legend.electron" );
        add( getPhotonImage( 400 ), "Legend.photon" );
    }
}
