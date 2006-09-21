/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:12:28 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaveInterferenceModule extends PiccoloModule {

    public WaveInterferenceModule( String title ) {
        super( title, new WaveInterferenceClock() );
        PhetPCanvas phetPCanvas = new WaveInterferenceCanvas();
        setSimulationPanel( phetPCanvas );
    }

    public void queryResetAll() {
        boolean paused = getClock().isPaused();
        getClock().pause();
        //see PhetFrameWorkaround; joptionpane doesn't paint when sim is running.
        int result = JOptionPane.showConfirmDialog( getSimulationPanel(), WIStrings.getString( "sure.to.reset.all" ) );
        if( result == JOptionPane.OK_OPTION ) {
            resetAll();
        }
        if( paused ) {
            getClock().pause();
        }
        else {
            getClock().start();
        }
    }

    public void resetAll() {
        getClock().start();
    }
}
