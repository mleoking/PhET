package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.SimUpdater;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * The update button that appears in dialogs.
 * Pressing this button requests an update.
 * What happens depends on the runtime situation of the simulation.
 */
public class UpdateButton extends JButton {
    public UpdateButton( final String project, final String sim, final String locale ) {
        super( PhetCommonResources.getString( "Common.updates.updateNow" ) );
        TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATE_NOW_PRESSED );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( PhetUtilities.isRunningFromWebsite() ) {
                    PhetServiceManager.showSimPage( project, sim );
                }
                else {
                    new SimUpdater().updateSim( project, sim, locale );
                }
            }
        } );
    }
}