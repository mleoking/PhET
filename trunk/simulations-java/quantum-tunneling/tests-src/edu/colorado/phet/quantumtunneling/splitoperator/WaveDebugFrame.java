
package edu.colorado.phet.quantumtunneling.splitoperator;

import edu.colorado.phet.quantumtunneling.model.IWavePacketSolver;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * User: Sam Reid
 * Date: Feb 28, 2006
 * Time: 6:01:09 PM
 */

public class WaveDebugFrame extends JFrame {
    private IWavePacketSolver solver;

    public WaveDebugFrame( IWavePacketSolver solver ) throws HeadlessException {
        super( "Debug" );
        this.solver = solver;

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( 800, 600 );
        setContentPane( new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor( Color.blue );
                LightweightComplex[]w = getWaveFunctionValues();
                GeneralPath path = new GeneralPath();
                for( int i = 0; i < w.length; i++ ) {
                    LightweightComplex lightweightComplex = w[i];
                    if( i == 0 ) {
                        path.moveTo( i, (float)( 100 * lightweightComplex.getAbs() + 400 ) );
                    }
                    else {
                        path.lineTo( i, (float)( 100 * lightweightComplex.getAbs() + 400 ) );
                    }
                }
                g2.draw( path );
            }

        } );
    }

    private LightweightComplex[] getWaveFunctionValues() {
        return solver.getWaveFunctionValues();
    }

    public void paint() {
        ( (JPanel)getContentPane() ).paintImmediately( 0, 0, getContentPane().getWidth(), getContentPane().getHeight() );
    }
}
