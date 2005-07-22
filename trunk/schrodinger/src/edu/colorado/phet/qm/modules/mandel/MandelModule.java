/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.view.colormaps.VisualColorMap;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:00:58 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelModule extends IntensityModule {
    private MandelPanel schrodingerPanel;

    public MandelModule( AbstractClock clock ) {
        super( "Mandel Experiment", clock );
        schrodingerPanel = new MandelPanel( this );
        setSchrodingerPanel( schrodingerPanel );
        schrodingerPanel.getWavefunctionGraphic().setWavefunctionColorMap( new VisualColorMap( getSchrodingerPanel() ) );
    }
}
