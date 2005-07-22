/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.CylinderWave;
import edu.colorado.phet.qm.DoubleWaveEnvelope;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.PlaneWave;
import edu.colorado.phet.qm.model.Wave;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:07:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class DoubleWave extends CylinderWave {
    public DoubleWave( SchrodingerModule schrodingerModule, DiscreteModel discreteModel ) {
        super( schrodingerModule, discreteModel );
    }

    protected Wave dampWave( PlaneWave planeWave, double totalIntensity ) {
        return new DoubleWaveEnvelope( planeWave, totalIntensity );
    }

    protected Wave createPlaneWave( double phase ) {
        return new MandelWave( phase );
////        AbstractVector2D planeWaveDir = AbstractVector2D.Double.parseAngleAndMagnitude( super.getMomentum(), Math.PI / 2 );
//        AbstractVector2D planeWaveDir = AbstractVector2D.Double.parseAngleAndMagnitude( super.getMomentum(), Math.PI / 4 );
//        PlaneWave2D planeWave2D = new PlaneWave2D( planeWaveDir, getDiscreteModel().getGridWidth() );
//        planeWave2D.setPhase( phase );
//        planeWave2D.setMagnitude( super.getMagnitude() );
////        final PlaneWave planeWave = new PlaneWave( super.getMomentum(), getDiscreteModel().getGridWidth() );
////        planeWave.setPhase( phase );
////        planeWave.setMagnitude( super.getMagnitude() );
//        return planeWave2D;
//        return dampWave( planeWave, super.getTotalIntensity() );
    }
}
