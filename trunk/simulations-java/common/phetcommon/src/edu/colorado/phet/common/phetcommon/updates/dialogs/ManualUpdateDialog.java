package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;

/**
 * 
 * The dialog that used to notify the user that an update is available,
 * when the user has manually requested an update check.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ManualUpdateDialog extends AbstractUpdateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.choice.cancel" );
    
    public ManualUpdateDialog( Frame owner, final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion,String locale ) {
        super( owner, TITLE, project, sim, simName,currentVersion, newVersion,locale );
    }
    
    protected JPanel createButtonPanel( final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion,String locale ) {
        
        // update button
        JButton updateButton = new UpdateButton( project, sim, locale, simName, newVersion );
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        // cancel button
        JButton cancelButton = new JButton( CANCEL_BUTTON );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATES_CANCEL_PRESSED );
                dispose();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateButton );
        buttonPanel.add( cancelButton );
        
        return buttonPanel;
    }

}
