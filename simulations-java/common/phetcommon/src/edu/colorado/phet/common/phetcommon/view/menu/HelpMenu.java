/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14677 $
 * Date modified : $Date:2007-04-17 03:40:29 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.phetcommon.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.preferences.IManualUpdateChecker;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * HelpMenu
 *
 * @author ?
 * @version $Revision:14677 $
 */
public class HelpMenu extends JMenu implements ModuleObserver {
    private JMenuItem onscreenHelp;
    private IManualUpdateChecker iManuallyCheckForUpdates;

    public HelpMenu( final PhetApplication phetApplication, IManualUpdateChecker iManuallyCheckForUpdates ) {
        super( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.Title" ) );
        this.iManuallyCheckForUpdates = iManuallyCheckForUpdates;
        this.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        phetApplication.addModuleObserver( this );

        //----------------------------------------------------------------------
        // "Help" menu item
        onscreenHelp = new JCheckBoxMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.Help" ) );
        onscreenHelp.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.HelpMnemonic" ).charAt( 0 ) );
        onscreenHelp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                phetApplication.getActiveModule().setHelpEnabled( onscreenHelp.isSelected() );
            }
        } );
        onscreenHelp.setEnabled( phetApplication.getActiveModule() != null && phetApplication.getActiveModule().hasHelp() );
        add( onscreenHelp );

        //----------------------------------------------------------------------
        // "MegaHelp" menu item
        final JMenuItem megaHelpItem = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.MegaHelp" ) );
        megaHelpItem.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.MegaHelpMnemonic" ).charAt( 0 ) );
        megaHelpItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( phetApplication.getActiveModule().hasMegaHelp() ) {
                    phetApplication.getActiveModule().showMegaHelp();
                }
                else {
                    JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(),
                                                   "No MegaHelp available for this module." );
                }
            }
        } );
        phetApplication.addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
            }

            public void activeModuleChanged( ModuleEvent event ) {
                megaHelpItem.setVisible( event.getModule().hasMegaHelp() );
            }

            public void moduleRemoved( ModuleEvent event ) {
            }
        } );
        megaHelpItem.setVisible( phetApplication.getActiveModule() != null && phetApplication.getActiveModule().hasMegaHelp() );
        add( megaHelpItem );

        //----------------------------------------------------------------------
        // Separator
        addSeparator();
        if ( phetApplication.getSimInfo().isUpdatesEnabled() ) {
            add( new CheckForUpdatesMenuItem() );
        }

        //----------------------------------------------------------------------
        // "About" menu item
        final JMenuItem about = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.About" ) );
        about.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                phetApplication.showAboutDialog();
            }
        } );
        add( about );
    }

    /**
     * Sets the state of the Help menu item.
     * This is used to keep the menubar's Help menu item
     * in sync with the control panel's Help button.
     *
     * @param selected
     */
    public void setHelpSelected( boolean selected ) {
        onscreenHelp.setSelected( selected );
    }

    //----------------------------------------------------------------
    // ModuleObserver implementation
    //----------------------------------------------------------------
    public void moduleAdded( ModuleEvent event ) {
        //noop
    }

    public void activeModuleChanged( ModuleEvent event ) {
        Module module = event.getModule();
        if ( module != null ) {
            onscreenHelp.setEnabled( module.hasHelp() );
            onscreenHelp.setSelected( module.isHelpEnabled() );
        }
    }

    public void moduleRemoved( ModuleEvent event ) {
        //noop
    }

    private class CheckForUpdatesMenuItem extends JMenuItem {
        private CheckForUpdatesMenuItem() {
            super( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.CheckForUpdates" ) );
            setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.HelpMenu.CheckForUpdates" ).charAt( 0 ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    iManuallyCheckForUpdates.checkForUpdates();
                }
            } );
        }
    }
}
