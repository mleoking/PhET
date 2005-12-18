package edu.colorado.phet.qm.model;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.qm.view.piccolo.SimpleWavefunctionGraphic;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Dec 4, 2005
 * Time: 5:59:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class WaveDebugger {
    private Wavefunction wavefunction;
    private JFrame frame;
    private PhetPCanvas phetPCanvas;
    private SimpleWavefunctionGraphic simpleWavefunctionGraphic;

    public WaveDebugger( String name, Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
        frame = new JFrame( name );
        phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );
        simpleWavefunctionGraphic = new SimpleWavefunctionGraphic( wavefunction );
        phetPCanvas.addScreenChild( simpleWavefunctionGraphic );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        Wavefunction wavefunction = new Wavefunction( 100, 100 );
        wavefunction.setValue( 10, 10, 1.0, 0 );
//        Wave wave = new GaussianWave2D(new Point2D.Double(50, 50), new Vector2D.Double(0, 0), 1);
//        new WaveSetup(wave).initialize(wavefunction);
        WaveDebugger waveDebugger = new WaveDebugger( "Test", wavefunction );
        waveDebugger.setVisible( true );
    }

    public void setVisible( boolean b ) {
        frame.setVisible( b );
    }

    public void update() {
        simpleWavefunctionGraphic.update();
    }
}
