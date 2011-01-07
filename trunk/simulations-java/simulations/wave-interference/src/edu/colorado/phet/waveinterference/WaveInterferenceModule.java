// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:12:28 PM
 */

public class WaveInterferenceModule extends PiccoloModule {

    public WaveInterferenceModule( String title ) {
        super( title, new WaveInterferenceClock() );
        PhetPCanvas phetPCanvas = new WaveInterferenceCanvas() {
            protected void updateWaveSize() {
            }
        };
        setSimulationPanel( phetPCanvas );
    }

    public void queryResetAll() {
        boolean paused = getClock().isPaused();
        getClock().pause();
        //see PhetFrameWorkaround; joptionpane doesn't paint when sim is running.
        int result = PhetOptionPane.showYesNoDialog( getSimulationPanel(), WIStrings.getString( "messages.confirm-reset" ) );
        if ( result == JOptionPane.OK_OPTION ) {
            resetAll();
        }
        if ( paused ) {
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
