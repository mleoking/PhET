// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope;

import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.LGModule;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeCanvas;

/**
 * Module for the "Point Slope" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeModule extends LGModule {

    public PointSlopeModule() {
        super( UserComponents.pointSlopeTab, Strings.TAB_POINT_SLOPE );
        setSimulationPanel( new PointSlopeCanvas( new PointSlopeModel() ) );
    }
}
