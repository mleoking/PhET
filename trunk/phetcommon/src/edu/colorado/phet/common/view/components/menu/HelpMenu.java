/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.application.*;
import edu.colorado.phet.common.util.VersionUtils;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
                if( moduleManager.getActiveModule().hasMegaHelp() ) {
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
                megaHelpItem.setVisible( event.getModule().hasMegaHelp() );
            }

            public void moduleRemoved( ModuleEvent event ) {
            }
        } );
        megaHelpItem.setVisible( moduleManager.getActiveModule() != null && moduleManager.getActiveModule().hasMegaHelp() );
        add( megaHelpItem );

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

    /**
     * Reads the versioning information for this application.
     * When constructed with ant tasks using build.number and time.stamp, files are generated of the form:
     * phetcommon.build.number
     * and
     * phetcommon.build.time.stamp
     * <p/>
     * for example.
     * When a main file depends on these libraries, their version info can be read as well.
     * To refer to a library for the purpose of reading version info, add a file named
     * ${root}.resources that lists each of the names of the dependencies.
     * <p/>
     * For example, Force1D depends on chart and phetcommon.  So the final jar contains
     * build.number and build.time.stamp with prefixes force1d, chart, and phetcommon.
     * By adding force1d.resources, and adding the text:
     * chart
     * phetcommon
     * (on separate lines),
     * their version info can be read as well, and reported by this ApplicationModel.
     */
    private VersionUtils.VersionInfo[] readVersionInfo( String title ) throws IOException {
        if( title == null ) {
//            System.out.println( "ApplicationModel.readVersionInfo: null module name for module (with window title=" + windowTitle + ")" );
            return new VersionUtils.VersionInfo[0];
        }
        else {
            VersionUtils.VersionInfo[] versionInfos = VersionUtils.readVersionInfo( title );
            return versionInfos;
        }
    }

    //----------------------------------------------------------------
    // ModuleObserver implementation
    //----------------------------------------------------------------
    public void moduleAdded( ModuleEvent event ) {
        //noop
    }

    public void activeModuleChanged( ModuleEvent event ) {
        Module module = event.getModule();
        if( module != null ) {
            onscreenHelp.setEnabled( module.hasHelp() );
            onscreenHelp.setSelected( module.isHelpEnabled() );
        }
    }

    public void moduleRemoved( ModuleEvent event ) {
        //noop
    }
}
