/* Copyright 2003-2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.preferences.PreferencesDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.statistics.SessionMessage;
import edu.colorado.phet.common.phetcommon.view.PhetExit;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * PhetFileMenu
 *
 * @author ?
 */
public class PhetFileMenu extends JMenu {

    public PhetFileMenu( final PhetFrame phetFrame, final ISimInfo simInfo ) {
        super( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Title" ) );
        setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.TitleMnemonic" ).charAt( 0 ) );

        if ( simInfo.isPreferencesEnabled() ) {
            addPreferencesMenuItem( phetFrame, simInfo );
            addSeparator();
        }

        JMenuItem exitMI = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Exit" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetExit.exit();
            }
        } );
        exitMI.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.ExitMnemonic" ).charAt( 0 ) );
        this.add( exitMI );
    }

    private void addPreferencesMenuItem( final PhetFrame phetFrame, final ISimInfo simInfo ) {
        JMenuItem preferencesMenuItem = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Preferences" ) );
        preferencesMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PreferencesDialog( phetFrame,
                                       SessionMessage.getInstance(),
                                       PhetPreferences.getInstance(),
                                       simInfo.isStatisticsFeatureIncluded(),
                                       simInfo.isUpdatesFeatureIncluded(),
                                       simInfo.isDev() ).setVisible( true );
            }
        } );
        preferencesMenuItem.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.PreferencesMnemonic" ).charAt( 0 ) );
        add( preferencesMenuItem );
    }
}
