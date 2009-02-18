package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.IUpdateTimer;
import edu.colorado.phet.common.phetcommon.updates.IVersionSkipper;

/**
 * The dialog that used to automatically notify the user that a sim update is available.
 */
public class AutomaticSimUpdateDialog extends AbstractSimUpdateDialog {

    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String SKIP_UPDATE_BUTTON = PhetCommonResources.getString( "Common.updates.skipThisUpdate" );
    private static final String PREFEENCES_MESSAGE = PhetCommonResources.getString( "Common.updates.seePreferences" );

    private final IUpdateTimer updateTimer;
    private final IVersionSkipper versionSkipper;
    
    public AutomaticSimUpdateDialog( Frame owner, ISimInfo simInfo, PhetVersion newVersion, IUpdateTimer updateTimer, IVersionSkipper versionSkipper ) {
        super( owner, TITLE, simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getName(), simInfo.getVersion(), newVersion, simInfo.getLocale() );
        
        this.updateTimer = updateTimer;
        this.versionSkipper = versionSkipper;
    }
    
    protected JPanel createButtonPanel( final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion, Locale locale ) {
        
        // does the update
        JButton updateButton = new SimUpdateButton( project, sim, locale, simName, newVersion );
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        // ignores this update until a later time
        JButton askMeLater = new JButton( ASK_ME_LATER_BUTTON );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTimer.setStartTime( System.currentTimeMillis() );
                dispose();
            }
        } );

        // ignores this version altogether
        JButton skipThisVersion = new JButton( SKIP_UPDATE_BUTTON );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                versionSkipper.setSkippedVersion( newVersion.getRevisionAsInt() );
                dispose();
            }
        } );
        
        // panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateButton );
        buttonPanel.add( Box.createHorizontalStrut( 30 ) );
        buttonPanel.add( askMeLater );
        buttonPanel.add( skipThisVersion );
        
        return buttonPanel;
    }
    
    protected JComponent createAdditionalMessageComponent() {
        // message about how to access the Preferences dialog
        String preferencesHTML = "<html><font size=\"2\">" + PREFEENCES_MESSAGE + "</font></html>";
        return new JLabel( preferencesHTML );
    }

}
