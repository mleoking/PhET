package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.lasers.view.AbstractLegend;

/**
 * Created by: Sam
 * May 25, 2008 at 11:23:40 PM
 */
public class DischargeLampsLegend extends AbstractLegend {
    public DischargeLampsLegend() {
        add( getAtomImage(), "Legend.atom" );
        add( getElectronImage(), "Legend.electron" );
        add( getPhotonImage( 400 ), "Legend.photon" );
    }
}
