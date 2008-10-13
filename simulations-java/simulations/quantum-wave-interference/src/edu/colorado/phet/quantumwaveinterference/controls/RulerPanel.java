/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:15:10 PM
 */

public class RulerPanel extends HorizontalLayoutPanel {
    private QWIPanel QWIPanel;

    public RulerPanel( QWIPanel QWIPanel ) throws IOException {
        this.QWIPanel = QWIPanel;

        final HorizontalLayoutPanel rulerPanel = this;

        final JCheckBox ruler = new JCheckBox( QWIResources.getString( "controls.ruler" ) );
        ImageIcon icon = new ImageIcon( ImageLoader.loadBufferedImage( "quantum-wave-interference/images/ruler3.png" ) );
        rulerPanel.add( ruler );
        rulerPanel.add( new JLabel( icon ) );
        ruler.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setRulerVisible( ruler.isSelected() );
            }
        } );
        new Timer( 500, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ruler.setSelected( getSchrodingerPanel().isRulerVisible() );
//                rulerPanel.setEnabled( !getSchrodingerPanel().isPhotonMode() );
//                ruler.setEnabled( !getSchrodingerPanel().isPhotonMode() );
            }
        } ).start();
    }

    public QWIPanel getSchrodingerPanel() {
        return QWIPanel;
    }
}
