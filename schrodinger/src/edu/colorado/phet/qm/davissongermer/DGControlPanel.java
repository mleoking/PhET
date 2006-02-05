/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.modules.intensity.IntensityControlPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:54:25 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class DGControlPanel extends IntensityControlPanel {

    public DGControlPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        setupDG();
    }

    private void setupDG() {
        DGModel dgModel = new DGModel( getDiscreteModel() );
        addControlFullWidth( new AtomLatticeControlPanel( dgModel ) );
    }
}
