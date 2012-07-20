// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.tests;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.quantumwaveinterference.model.ParticleUnits;
import edu.colorado.phet.quantumwaveinterference.model.WaveDebugger;
import edu.colorado.phet.quantumwaveinterference.model.WaveSetup;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.waves.GaussianWave2D;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.VisualColorMap3;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 */

public class TestUnits2 {

    public TestUnits2() {
        Wavefunction wavefunction = new Wavefunction( 50, 50 );
        ParticleUnits.NeutronUnits neutronUnits = new ParticleUnits.NeutronUnits();
        double momentumY = neutronUnits.getAverageVelocity() * neutronUnits.getMass().getValue();
        System.out.println( "momentumY = " + momentumY );
        GaussianWave2D gaussianWave2D = new GaussianWave2D( new Point2D.Double( 25, 25 ),
                                                            new MutableVector2D( 0, momentumY ), 3.5 * 2, neutronUnits.getHbar().getValue() );
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
