/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 8:20:48 AM
 */

public class AutoFireCheckBox extends JCheckBox {
    public AutoFireCheckBox( final AutoFire autoFire ) {
        super( QWIResources.getString( "gun.auto-repeat" ) );
        setFont( new PhetFont( Font.BOLD, 12 ) );
        setForeground( Color.white );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                autoFire.setAutoFire( isSelected() );
            }
        } );
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );

    }
}
