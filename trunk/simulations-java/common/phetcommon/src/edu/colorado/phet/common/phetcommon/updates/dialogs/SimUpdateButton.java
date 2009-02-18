package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.updates.SimUpdater;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;

/**
 * The update button that appears in dialogs.
 * Pressing this button requests an update.
 * What happens depends on the runtime situation of the simulation.
 */
public class SimUpdateButton extends JButton {
    public SimUpdateButton( final String project, final String sim, final Locale locale, final String simName, final PhetVersion newVersion ) {
        super( PhetCommonResources.getString( "Common.updates.updateNow" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( DeploymentScenario.getInstance().isOnline() ) {
                    PhetServiceManager.showSimPage( project, sim );
                }
                else {
                    new SimUpdater().updateSim( project, sim, locale, simName, newVersion );
                }
            }
        } );
    }
}