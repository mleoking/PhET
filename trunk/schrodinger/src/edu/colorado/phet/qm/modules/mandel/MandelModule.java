/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.intensity.IntensityPanel;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:00:58 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelModule extends IntensityModule {
    private MandelPanel schrodingerPanel;

    public MandelModule( SchrodingerApplication app ) {
        super( "Mandel Experiment", app );
//        schrodingerPanel.getWavefunctionGraphic().setWavefunctionColorMap( new VisualColorMap( getSchrodingerPanel() ) );
        setControlPanel( new MandelControlPanel( this ) );
    }

    protected IntensityPanel createIntensityPanel() {
        return new MandelPanel( this );
    }
}
