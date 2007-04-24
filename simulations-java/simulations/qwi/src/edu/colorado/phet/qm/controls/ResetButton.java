/*  */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 1, 2006
 * Time: 5:16:43 PM
 *
 */

public class ResetButton extends JButton {
    public ResetButton( final QWIModule module ) {
        super( QWIStrings.getString( "reset" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean confirm = module.confirmReset();
                if( confirm ) {
                    module.reset();
                }
            }
        } );
    }
}
