// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept;

import edu.colorado.phet.linegraphing.common.LGModule;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptCanvas;

/**
 * Module for the "Slope-Intercept" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModule extends LGModule {

    public SlopeInterceptModule() {
        super( UserComponents.slopeInterceptTab, Strings.TAB_SLOPE_INTERCEPT );
        setSimulationPanel( new SlopeInterceptCanvas( new SlopeInterceptModel(), new LineFormsViewProperties() ) );
    }
}
