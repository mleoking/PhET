// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.view.AbstractLegend;

/**
 * Created by: Sam
 * May 25, 2008 at 11:28:22 PM
 */
public class LasersLegend extends AbstractLegend {
    public LasersLegend() {
        super( LasersResources.getString( "Legend.title" ) );
        addLegendItem( getAtomImage(), LasersResources.getString( "Legend.atom" ) );
        addLegendItem( createPhotonLegendImage(), LasersResources.getString( "Legend.photon" ) );
    }
}
