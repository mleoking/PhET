package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.updates.SimUpdater;

/**
 * Updates the simulations by running the PhET "updater", which downloads the new version
 * of the sim, replaces the running version, and restarts the new version.
 *
 * @author Sam Reid
 */
public class UpdateButton extends JButton {
    public UpdateButton( final String project, final String sim, final String locale ) {
        super( PhetCommonResources.getString( "Common.updates.updateNow" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new SimUpdater().updateSim( project, sim, locale );
            }
        } );
    }
}