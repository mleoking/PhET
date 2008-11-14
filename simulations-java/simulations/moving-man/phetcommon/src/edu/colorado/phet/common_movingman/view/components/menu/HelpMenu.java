/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_movingman.view.components.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.dialogs.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_movingman.application.ModuleEvent;
import edu.colorado.phet.common_movingman.application.ModuleManager;
import edu.colorado.phet.common_movingman.application.ModuleObserver;
import edu.colorado.phet.common_movingman.application.PhetApplication;
import edu.colorado.phet.common_movingman.view.PhetFrame;

/**
 * HelpMenu
 *
 * @author ?
 * @version $Revision$
 */
public class HelpMenu extends JMenu implements ModuleObserver {
    private ImageIcon icon;
    private JMenuItem onscreenHelp;
    private PhetFrame frame;

    public HelpMenu( PhetApplication application, PhetFrame frame ) {
        this( application.getModuleManager(), application.getApplicationModel().getName(),
              application.getApplicationModel().getDescription(), application.getApplicationModel().getVersion(), frame );
    }

    public HelpMenu( final ModuleManager moduleManager, final String title,
                     String description, String version, final PhetFrame frame ) {
        super( SimStrings.get( "Common.HelpMenu.Title" ) );
        this.frame = frame;
        this.setMnemonic( SimStrings.get( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        moduleManager.addModuleObserver( this );

        //----------------------------------------------------------------------
        // "Help" menu item
        onscreenHelp = new JCheckBoxMenuItem( SimStrings.get( "Common.HelpMenu.Help" ) );
        onscreenHelp.setMnemonic( SimStrings.get( "Common.HelpMenu.HelpMnemonic" ).charAt( 0 ) );
        onscreenHelp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moduleManager.getActiveModule().setHelpEnabled( onscreenHelp.isSelected() );
            }
        } );
        onscreenHelp.setEnabled( moduleManager.getActiveModule() != null && moduleManager.getActiveModule().hasHelp() );
        add( onscreenHelp );

        //----------------------------------------------------------------------
        // "MegaHelp" menu item
        final JMenuItem megaHelpItem = new JMenuItem( SimStrings.get( "Common.HelpMenu.MegaHelp" ) );
        megaHelpItem.setMnemonic( SimStrings.get( "Common.HelpMenu.MegaHelpMnemonic" ).charAt( 0 ) );
        megaHelpItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( moduleManager.getActiveModule().hasMegaHelp() ) {
                    moduleManager.getActiveModule().showMegaHelp();
                }
                else {
                    JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(),
                                                   "No MegaHelp available for this module." );
                }
            }
        } );
        moduleManager.addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
            }

            public void activeModuleChanged( ModuleEvent event ) {
                megaHelpItem.setEnabled( event.getModule().hasMegaHelp() );
            }

            public void moduleRemoved( ModuleEvent event ) {
            }
        } );
        megaHelpItem.setEnabled( moduleManager.getActiveModule() != null && moduleManager.getActiveModule().hasMegaHelp() );
        add( megaHelpItem );

        //----------------------------------------------------------------------
        // Separator
        addSeparator();

        //----------------------------------------------------------------------
        // "About" menu item
        final JMenuItem about = new JMenuItem( SimStrings.get( "Common.HelpMenu.About" ) );
        about.setMnemonic( SimStrings.get( "Common.HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PhetAboutDialogAdapter( frame, new PhetApplicationConfig( new String[0], "moving-man" ) ).show();
            }
        } );
        add( about );
    }

    static class PhetAboutDialogAdapter extends PhetAboutDialog {
        public PhetAboutDialogAdapter( Frame owner, ISimInfo config ) {
            super( owner, config );
        }
    }

    //----------------------------------------------------------------
    // ModuleObserver implementation
    //----------------------------------------------------------------
    public void moduleAdded( ModuleEvent event ) {
        //noop
    }

    public void activeModuleChanged( ModuleEvent event ) {
        onscreenHelp.setEnabled( event.getModule() != null && event.getModule().hasHelp() );
    }

    public void moduleRemoved( ModuleEvent event ) {
        //noop
    }
}
