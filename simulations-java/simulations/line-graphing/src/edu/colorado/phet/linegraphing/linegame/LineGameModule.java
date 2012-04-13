// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame;

import edu.colorado.phet.linegraphing.common.LGModule;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.LineGameCanvas;

/**
 * Module for the "Line Game" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModule extends LGModule {

    public LineGameModule() {
        super( UserComponents.lineGameTab, Strings.TAB_LINE_GAME );
        setSimulationPanel( new LineGameCanvas( new LineGameModel() ) );
    }
}
