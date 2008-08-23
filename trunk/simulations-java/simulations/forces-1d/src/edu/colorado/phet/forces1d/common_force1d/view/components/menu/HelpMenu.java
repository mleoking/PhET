/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.components.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.forces1d.common_force1d.application.ModuleEvent;
import edu.colorado.phet.forces1d.common_force1d.application.ModuleManager;
import edu.colorado.phet.forces1d.common_force1d.application.ModuleObserver;
import edu.colorado.phet.forces1d.common_force1d.application.PhetApplication;
import edu.colorado.phet.forces1d.Force1DResources;

/**
 * HelpMenu
 *
 * @author ?
 * @version $Revision$
 */
public class HelpMenu extends JMenu implements ModuleObserver {
    private ImageIcon icon;
    private JMenuItem onscreenHelp;
    private Frame parent;

    public HelpMenu( Frame parent, PhetApplication application ) {
        this( parent, application.getModuleManager(), application.getApplicationModel().getName(),
              application.getApplicationModel().getDescription(), application.getApplicationModel().getVersion() );
    }

    public HelpMenu( final Frame parent, final ModuleManager moduleManager, final String title,
                     String description, String version ) {
        super( Force1DResources.getCommonString( "Common.HelpMenu.Title" ) );
        this.parent = parent;
        this.setMnemonic( Force1DResources.getCommonString( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        moduleManager.addModuleObserver( this );

        //----------------------------------------------------------------------
        // "Help" menu item
        onscreenHelp = new JCheckBoxMenuItem( Force1DResources.getCommonString( "Common.HelpMenu.Title" ) );
        onscreenHelp.setMnemonic( Force1DResources.getCommonString( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        onscreenHelp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moduleManager.getActiveModule().setHelpEnabled( onscreenHelp.isSelected() );
            }
        } );
        onscreenHelp.setEnabled( moduleManager.getActiveModule() != null && moduleManager.getActiveModule().hasHelp() );
        add( onscreenHelp );

        //----------------------------------------------------------------------
        // "MegaHelp" menu item
//        final JMenuItem megaHelpItem = new JMenuItem( Force1DResources.getCommonString( "Common.HelpMenu.MegaHelp" ) );
//        megaHelpItem.setMnemonic( Force1DResources.getCommonString( "Common.HelpMenu.MegaHelpMnemonic" ).charAt( 0 ) );
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
        final JMenuItem about = new JMenuItem( Force1DResources.getCommonString( "Common.HelpMenu.About" ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetAboutDialog phetAboutDialog = new PhetAboutDialog( parent, "forces-1d" );
                SwingUtils.centerWindowOnScreen( phetAboutDialog );//not sure why the default centering fails for this application
                phetAboutDialog.show();
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
