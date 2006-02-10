/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.WaveDebugger;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.propagators.QWIFFT2D;
import edu.colorado.phet.qm.model.waves.GaussianWave2D;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.VisualColorMap3;
import edu.colorado.phet.qm.view.piccolo.SimpleWavefunctionGraphic;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 * Copyright (c) Dec 21, 2005 by Sam Reid
 */

public class SimpleWaveTest {

    public SimpleWaveTest() {
        Wavefunction wavefunction = new Wavefunction( 100, 100 );
        ParticleUnits.NeutronUnits neutronUnits = new ParticleUnits.NeutronUnits();
        double momentumY = neutronUnits.getAverageVelocity() * neutronUnits.getMass().getValue();
        System.out.println( "momentumY = " + momentumY );
        GaussianWave2D gaussianWave2D = new GaussianWave2D( new Point2D.Double( wavefunction.getWidth() / 2, wavefunction.getHeight() / 2 ),
                                                            new Vector2D.Double( 0, momentumY ), 3.5 * 2, neutronUnits.getHbar().getValue() );
        WaveSetup waveSetup = new WaveSetup( gaussianWave2D );
        waveSetup.initialize( wavefunction );

        WaveDebugger waveDebugger = new WaveDebugger( "wave", wavefunction, 2, 2 );
        waveDebugger.setComplexColorMap( new VisualColorMap3() );
        waveDebugger.setVisible( true );
//        Thread.sleep( 1000 );
        wavefunction = QWIFFT2D.forwardFFT( wavefunction );
        wavefunction.normalize();
        wavefunction.scale( 2.0 );
        waveDebugger.setSimpleWavefunctionGraphic( new SimpleWavefunctionGraphic( wavefunction, 2, 2, new ComplexColorMapAdapter( wavefunction, new MagnitudeColorMap() ) ) );
    }

    public static void main( String[] args ) throws InterruptedException {
        new SimpleWaveTest().start();
    }

    private void start() {
    }

}
