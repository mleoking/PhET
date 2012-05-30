// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.sponsorship;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Menu item for displaying simulation sponsor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SponsorMenuItem extends JMenuItem {

    private JDialog dialog;

    public SponsorMenuItem( final PhetApplicationConfig config, final Frame frame ) {
        super( PhetCommonResources.getString( "Sponsor.sponsorMenuItem" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null ) {
                    dialog = SponsorDialog.show( config, frame, false /* startDisposeTimer */ );
                    dialog.addWindowListener( new WindowAdapter() {

                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            dialog.dispose();
                        }

                        // called by JDialog.dispose
                        public void windowClosed( WindowEvent e ) {
                            dialog = null;
                        }
                    } );
                }
            }
        } );
    }
}
