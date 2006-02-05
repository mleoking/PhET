/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:50:45 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class DGModule extends IntensityModule {
    /**
     * @param schrodingerApplication
     */
    public DGModule( SchrodingerApplication schrodingerApplication, IClock clock ) {
        super( "Davisson-Germer Experiment", schrodingerApplication, clock );

        DGControlPanel intensityControlPanel = new DGControlPanel( this );
        setSchrodingerControlPanel( intensityControlPanel );
    }
}
