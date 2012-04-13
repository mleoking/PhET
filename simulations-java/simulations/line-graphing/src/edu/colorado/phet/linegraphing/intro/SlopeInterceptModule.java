// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro;

import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.LGModule;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptModel;
import edu.colorado.phet.linegraphing.intro.view.SlopeInterceptCanvas;

/**
 * Module for the "Intro" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModule extends LGModule {

    public SlopeInterceptModule() {
        super( UserComponents.slopeInterceptTab, Strings.TAB_SLOPE_INTERCEPT );
        setSimulationPanel( new SlopeInterceptCanvas( new SlopeInterceptModel() ) );
    }
}
