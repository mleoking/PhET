package edu.colorado.phet.qm.model;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.GrayscaleColorMap;
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
        simpleWavefunctionGraphic.setComplexColorMap( new GrayscaleColorMap.Real() );
        phetPCanvas.addScreenChild( simpleWavefunctionGraphic );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void setColorMap( ColorMap colorMap ) {
        simpleWavefunctionGraphic.setColorMap( colorMap );
        update();
    }

    public void setComplexColorMap( ComplexColorMap complexColorMap ) {
        simpleWavefunctionGraphic.setComplexColorMap( complexColorMap );
        update();
    }

    public void setVisible( boolean b ) {
        frame.setVisible( b );
    }

    public void update() {
        simpleWavefunctionGraphic.update();
    }
}
