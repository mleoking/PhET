// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope;

import edu.colorado.phet.linegraphing.common.LGModule;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeCanvas;
import edu.colorado.phet.linegraphing.slope.view.SlopeCanvas;

/**
 * Module for the "Slope" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeModule extends LGModule {

    public SlopeModule() {
        super( UserComponents.slopeTab, Strings.TAB_SLOPE );
        setSimulationPanel( new SlopeCanvas( LineFormsModel.createPointSlopeModel(), new LineFormsViewProperties() ) ); //TODO LineFormsModel.createSlopeModel
    }
}
