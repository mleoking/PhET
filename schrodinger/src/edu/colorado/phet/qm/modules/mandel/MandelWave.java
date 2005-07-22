/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.qm.ParametricFlatDampedWave;
import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.PlaneWave2D;
import edu.colorado.phet.qm.model.Wave;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:33:30 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelWave implements Wave {
    private PlaneWave2D leftWave;
    private ParametricFlatDampedWave dampedLeft;
    private PlaneWave2D rightWave;
    private ParametricFlatDampedWave dampedRight;

    public MandelWave( double phase ) {
        leftWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( 1, Math.PI / 4 ), 100 );
        dampedLeft = new ParametricFlatDampedWave( 30, leftWave, 0.02 );
        leftWave.setPhase( 0 );

        rightWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( 1, Math.PI / 8 ), 100 );
        rightWave.setPhase( Math.PI / 8 );
        dampedRight = new ParametricFlatDampedWave( 70, leftWave, 0.02 );
//        leftWave.setPhase( phase );
    }

    public Complex getValue( int i, int j, double simulationTime ) {
//        return leftWave.getValue( i, j, simulationTime ).times( 0.08 );
        return dampedLeft.getValue( i, j, simulationTime ).plus( dampedRight.getValue( i, j, simulationTime ) );
    }
}
