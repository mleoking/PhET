/*  */
package edu.colorado.phet.qm.tests.thirdparty.richardson;

import javax.swing.*;
import java.applet.Applet;

/**
 * User: Sam Reid
 * Date: Jun 13, 2005
 * Time: 10:57:59 PM
 *
 */

public class InvokeWave {
    public static void main( String[] args ) {
        Applet a = new WaveSim();

        a.setSize( 600, 600 );
        JFrame f = new JFrame();
        f.setContentPane( a );
        f.pack();
        f.show();
        a.init();
    }
}
