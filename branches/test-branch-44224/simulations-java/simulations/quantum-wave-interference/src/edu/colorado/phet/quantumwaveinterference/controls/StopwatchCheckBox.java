/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 7:30:43 AM
 */

//public class StopwatchCheckBox extends JCheckBox {
public class StopwatchCheckBox extends HorizontalLayoutPanel {
    private QWIPanel QWIPanel;

    public StopwatchCheckBox( QWIPanel QWIPanel ) {
        super();
        final JCheckBox checkBox = new JCheckBox( QWIResources.getString( "controls.stopwatch" ) );
        this.QWIPanel = QWIPanel;

//        final JCheckBox stopwatchCheckBox = new JCheckBox( "Stopwatch" );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setStopwatchVisible( checkBox.isSelected() );
            }

        } );
        new Timer( 500, new ActionListener() {//todo why does this drag the application if time < 30 ms?

            public void actionPerformed( ActionEvent e ) {
                setEnabled( !getSchrodingerPanel().isPhotonMode() );
            }
        } ).start();
        add( checkBox );
        try {
            add( new JLabel( new ImageIcon( edu.colorado.phet.common.phetcommon.view.util.ImageLoader.loadBufferedImage( "quantum-wave-interference/images/stopwatch.png" ) ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private QWIPanel getSchrodingerPanel() {
        return QWIPanel;
    }
}
