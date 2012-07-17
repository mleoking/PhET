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

    private final LineGameCanvas canvas;
    private boolean rewardWasRunning = false; // Was the game reward animation running when this module was deactivated?

    public LineGameModule() {
        super( UserComponents.lineGameTab, Strings.TAB_LINE_GAME );
        canvas = new LineGameCanvas( new LineGameModel() );
        setSimulationPanel( canvas );
    }

    @Override public void activate() {
        super.activate();
        canvas.setRewardRunning( rewardWasRunning );
    }

    @Override public void deactivate() {
        super.deactivate();
        rewardWasRunning = canvas.isRewardRunning();
        canvas.setRewardRunning( false );
    }
}
