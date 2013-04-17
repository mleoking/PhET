// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.tests;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.quantumwaveinterference.model.ParticleUnits;
import edu.colorado.phet.quantumwaveinterference.model.Propagator;
import edu.colorado.phet.quantumwaveinterference.model.WaveDebugger;
import edu.colorado.phet.quantumwaveinterference.model.WaveSetup;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.potentials.ConstantPotential;
import edu.colorado.phet.quantumwaveinterference.model.propagators.QWIFFT2D;
import edu.colorado.phet.quantumwaveinterference.model.propagators.SplitOperatorPropagator;
import edu.colorado.phet.quantumwaveinterference.model.waves.GaussianWave2D;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.VisualColorMap3;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.SimpleWavefunctionGraphic;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 */

public class SimpleWaveTest {
    private Propagator propagator;
    private Wavefunction wavefunction;
    private WaveDebugger waveDebugger;

    public SimpleWaveTest() {
        wavefunction = new Wavefunction( 256, 256 );
        ParticleUnits.NeutronUnits neutronUnits = new ParticleUnits.NeutronUnits();
        double momentumY = neutronUnits.getAverageVelocity() * neutronUnits.getMass().getValue();
        System.out.println( "momentumY = " + momentumY );
        GaussianWave2D gaussianWave2D = new GaussianWave2D( new Point2D.Double( wavefunction.getWidth() / 2, wavefunction.getHeight() / 2 ),
                                                            new MutableVector2D( 0, momentumY ), 3.5 * 2, neutronUnits.getHbar().getValue() );
        WaveSetup waveSetup = new WaveSetup( gaussianWave2D );
        waveSetup.initialize( wavefunction );

        waveDebugger = new WaveDebugger( "wave", wavefunction, 2, 2 );
        waveDebugger.setComplexColorMap( new VisualColorMap3() );
        waveDebugger.setVisible( true );
//        propagator = new ModifiedRichardsonPropagator( DiscreteModel.DEFAULT_DT, new ConstantPotential(), 1.0, 1.0 );
        double hbar = 1.0;
        propagator = new SplitOperatorPropagator( null, new ConstantPotential() );
    }

    private void updateGraphics() {
        waveDebugger.setSimpleWavefunctionGraphic( new SimpleWavefunctionGraphic( wavefunction, 2, 2, new ComplexColorMapAdapter( wavefunction, new VisualColorMap3() ) ) );
    }

    public static void main( String[] args ) throws InterruptedException {
        new SimpleWaveTest().start();
    }

    private void start() throws InterruptedException {
//        testTranslaton();
//        testFFT();

        for ( int i = 0; i < 3; i++ ) {
            propagator.propagate( wavefunction );
////            wavefunction.normalize();
            show( "[" + i + "]", wavefunction, 2, 2 );
//            updateGraphics();
//            Thread.sleep( 30 );
//        }
        }
    }

    private void testFFT() {
        Wavefunction orig = wavefunction.copy();
        System.out.println( "orig.getMagnitude() = " + orig.getMagnitude() );
        show( "orig", orig, 2, 2 );

        Wavefunction a = QWIFFT2D.forwardFFT( orig );
        System.out.println( "a.getMagnitude() = " + a.getMagnitude() );
        show( "a", a.getNormalizedInstance(), 2, 2 );

        Wavefunction b = QWIFFT2D.inverseFFT( a );
        System.out.println( "b.getMagnitude() = " + b.getMagnitude() );
        show( "b", b, 2, 2 );
    }

    private void show( String s, Wavefunction w, int dx, int dy ) {
        WaveDebugger waveDebugger = new WaveDebugger( s, w, dx, dy );
        waveDebugger.setComplexColorMap( new VisualColorMap3() );
        waveDebugger.setVisible( true );
    }

    private void testTranslaton() {
        double[] w = QWIFFT2D.toArray( wavefunction );
        Wavefunction wax = QWIFFT2D.parseData( w, wavefunction.getWidth(), wavefunction.getHeight() );
        new WaveDebugger( "wax", wax, 2, 2 ).setVisible( true );
    }

}
