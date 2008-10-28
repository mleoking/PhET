/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.preferences.DefaultTrackingPreferences;
import edu.colorado.phet.common.phetcommon.preferences.DefaultUpdatePreferences;
import edu.colorado.phet.common.phetcommon.preferences.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.preferences.PreferencesDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.ApplicationConfigManualCheckForUpdates;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * PhetFileMenu
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFileMenu extends JMenu {

    public PhetFileMenu( final PhetFrame phetFrame, final ISimInfo simInfo, final ITrackingInfo trackingInfo ) {
        super( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Title" ) );
        setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.TitleMnemonic" ).charAt( 0 ) );

        if ( simInfo.isTrackingEnabled() || simInfo.isUpdatesEnabled() ) {
            JMenuItem preferencesMenuItem = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Preferences" ) );
            preferencesMenuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new PreferencesDialog( phetFrame, trackingInfo,
                                           new ApplicationConfigManualCheckForUpdates( phetFrame, simInfo ),
                                           new DefaultUpdatePreferences(),
                                           new DefaultTrackingPreferences() ).setVisible( true );
                }
            } );
            preferencesMenuItem.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.PreferencesMnemonic" ).charAt( 0 ) );
            add( preferencesMenuItem );
            addSeparator();
        }

        JMenuItem exitMI = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Exit" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.FILE_EXIT_SELECTED );
                TrackingManager.postSessionEndedMessage();
                System.exit( 0 );
            }
        } );
        exitMI.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.ExitMnemonic" ).charAt( 0 ) );
        this.add( exitMI );
    }
}
