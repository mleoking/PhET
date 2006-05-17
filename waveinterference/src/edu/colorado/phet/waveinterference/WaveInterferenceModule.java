/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;

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

}
