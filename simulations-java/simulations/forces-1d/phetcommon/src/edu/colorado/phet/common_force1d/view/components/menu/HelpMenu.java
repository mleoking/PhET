/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_force1d.view.components.menu;

import edu.colorado.phet.common_force1d.application.ModuleEvent;
import edu.colorado.phet.common_force1d.application.ModuleManager;
import edu.colorado.phet.common_force1d.application.ModuleObserver;
import edu.colorado.phet.common_force1d.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HelpMenu
 *
 * @author ?
 * @version $Revision$
 */
public class HelpMenu extends JMenu implements ModuleObserver {
    private ImageIcon icon;
    private JMenuItem onscreenHelp;

    public HelpMenu( PhetApplication application ) {
        this( application.getModuleManager(), application.getApplicationModel().getName(),
              application.getApplicationModel().getDescription(), application.getApplicationModel().getVersion() );
    }

    public HelpMenu( final ModuleManager moduleManager, final String title,
                     String description, String version ) {
        super( SimStrings.get( "Common.HelpMenu.Title" ) );
        this.setMnemonic( SimStrings.get( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        moduleManager.addModuleObserver( this );

        //----------------------------------------------------------------------
        // "Help" menu item
        onscreenHelp = new JCheckBoxMenuItem( SimStrings.get( "Common.HelpMenu.Title" ) );
        onscreenHelp.setMnemonic( SimStrings.get( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        onscreenHelp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moduleManager.getActiveModule().setHelpEnabled( onscreenHelp.isSelected() );
            }
        } );
        onscreenHelp.setEnabled( moduleManager.getActiveModule() != null && moduleManager.getActiveModule().hasHelp() );
        add( onscreenHelp );
        
        //----------------------------------------------------------------------
        // "MegaHelp" menu item
//        final JMenuItem megaHelpItem = new JMenuItem( SimStrings.get( "Common.HelpMenu.MegaHelp" ) );
//        megaHelpItem.setMnemonic( SimStrings.get( "Common.HelpMenu.MegaHelpMnemonic" ).charAt( 0 ) );
//        megaHelpItem.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                if( moduleManager.getActiveModule().hasMegaHelp() ) {
//                    moduleManager.getActiveModule().showMegaHelp();
//                }
//                else {
//                    JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(),
//                                                   "No MegaHelp available for this module." );
//                }
//            }
//        } );
//        moduleManager.addModuleObserver( new ModuleObserver() {
//            public void moduleAdded( ModuleEvent event ) {
//            }
//
//            public void activeModuleChanged( ModuleEvent event ) {
//                megaHelpItem.setEnabled( event.getModule().hasMegaHelp() );
//            }
//
//            public void moduleRemoved( ModuleEvent event ) {
//            }
//        } );
//        megaHelpItem.setEnabled( moduleManager.getActiveModule() != null && moduleManager.getActiveModule().hasMegaHelp() );
//        add( megaHelpItem );

        //----------------------------------------------------------------------
        // Separator
        addSeparator();
        
        //----------------------------------------------------------------------
        // "About" menu item
        final JMenuItem about = new JMenuItem( SimStrings.get( "Common.HelpMenu.About" ) );

        String javaVersion = SimStrings.get( "Common.HelpMenu.JavaVersion" ) + ": " + System.getProperty( "java.version" );
        about.setMnemonic( SimStrings.get( "Common.HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        String message = title + "\n" + description + "\n" + SimStrings.get( "Common.HelpMenu.VersionLabel" ) + ": " + version + "\n\n" + javaVersion + "\n";
        final String msg = message;
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( about, msg, SimStrings.get( "Common.HelpMenu.AboutTitle" ) + " " + title, JOptionPane.INFORMATION_MESSAGE, icon );
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
        onscreenHelp.setEnabled( event.getModule() != null && event.getModule().hasHelp() );
    }

    public void moduleRemoved( ModuleEvent event ) {
        //noop
    }
}
