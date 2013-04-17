// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.solublesalts.module;

import edu.colorado.phet.solublesalts.SolubleSaltsConfig.Calibration;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule.ResetListener;

/**
 * @author Sam Reid
 */
public interface ISolubleSaltsModelContainer {
    Calibration getCalibration();

    void addResetListener( ResetListener resetListener );

    double getMinimumFluidVolume();
}
