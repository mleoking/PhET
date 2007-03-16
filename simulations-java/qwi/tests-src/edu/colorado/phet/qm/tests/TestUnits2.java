/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.WaveDebugger;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.waves.GaussianWave2D;
import edu.colorado.phet.qm.view.complexcolormaps.VisualColorMap3;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 * Copyright (c) Dec 21, 2005 by Sam Reid
 */

public class TestUnits2 {

    public TestUnits2() {
        Wavefunction wavefunction = new Wavefunction( 50, 50 );
        ParticleUnits.NeutronUnits neutronUnits = new ParticleUnits.NeutronUnits();
        double momentumY = neutronUnits.getAverageVelocity() * neutronUnits.getMass().getValue();
        System.out.println( "momentumY = " + momentumY );
        GaussianWave2D gaussianWave2D = new GaussianWave2D( new Point2D.Double( 25, 25 ),
                                                            new Vector2D.Double( 0, momentumY ), 3.5 * 2, neutronUnits.getHbar().getValue() );
        WaveSetup waveSetup = new WaveSetup( gaussianWave2D );
        waveSetup.initialize( wavefunction );

        WaveDebugger waveDebugger = new WaveDebugger( "wave", wavefunction );
        waveDebugger.setComplexColorMap( new VisualColorMap3() );
        waveDebugger.setVisible( true );

        wavefunction.printWaveToScreen();
    }


    public static void main( String[] args ) {
        new TestUnits2().start();
    }

    private void start() {
    }

}
