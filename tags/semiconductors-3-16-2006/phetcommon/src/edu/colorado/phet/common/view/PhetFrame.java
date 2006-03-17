/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleEvent;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.common.view.menu.PhetFileMenu;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The PhetFrame is the JFrame for the PhetApplication.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFrame extends JFrame {
    private HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    private PhetApplication application;
    private Container contentPanel;
    private Module lastAdded;

    /**
     * Constructs a PhetFrame for the specified PhetApplication.
     */
    public PhetFrame( final PhetApplication application ) throws HeadlessException {
        super( application.getTitle() + " (" + application.getVersion() + ")" );
        this.application = application;

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener( new WindowAdapter() {

            // Pause the clock if the simulation window is iconified.
            public void windowIconified( WindowEvent e ) {
                application.pause();
            }

            // Restore the clock state if the simulation window is deiconified.
            public void windowDeiconified( WindowEvent e ) {
                application.resume();
            }
        } );

        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( application );
        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );
        setJMenuBar( menuBar );

        application.addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
                addModule( event.getModule() );
            }

            public void activeModuleChanged( ModuleEvent event ) {
            }

            public void moduleRemoved( ModuleEvent event ) {
                removeModule( event.getModule() );
            }

        } );
    }

    /**
     * Sets the <code>contentPane</code> property.
     *
     * @param contentPane the <code>contentPane</code> object for this frame
     * @throws java.awt.IllegalComponentStateException
     *          (a runtime exception) if the content pane parameter is <code>null</code>
     */
    public void setContentPane( Container contentPane ) {
        super.setContentPane( contentPane );
        this.contentPanel = contentPane;
    }

    private void addModule( Module module ) {
        setContentPane( addToContentPane( module ) );
        this.lastAdded = module;
    }

    private Container addToContentPane( Module module ) {
        if( contentPanel == null ) {
            return module.getModulePanel();
        }
        else if( contentPanel instanceof ModulePanel ) {
            return new TabbedModulePane( application, new Module[]{lastAdded, module} );
        }
        else if( contentPanel instanceof TabbedModulePane ) {
            TabbedModulePane tabbedModulePane = (TabbedModulePane)contentPanel;
            tabbedModulePane.addTab( module );
            return tabbedModulePane;
        }
        else {
            throw new RuntimeException( "Illegal type for content pane: " + contentPanel );
        }
    }

    private void removeModule( Module module ) {
        setContentPane( removeFromContentPane( module ) );
    }

    private Container removeFromContentPane( Module module ) {
        if( contentPanel == null ) {
            throw new RuntimeException( "Cannot remove module: " + module + ", from contentPane=" + contentPanel );
        }
        else if( contentPanel == module.getModulePanel() ) {
            return new JLabel( "No modules" );
        }
        else if( contentPanel instanceof TabbedModulePane ) {
            TabbedModulePane tabbedModulePane = (TabbedModulePane)contentPanel;
            tabbedModulePane.removeTab( module );
            if( tabbedModulePane.getTabCount() > 1 ) {
                return tabbedModulePane;
            }
            else if( tabbedModulePane.getTabCount() == 1 ) {
                return tabbedModulePane.getModulePanel( 0 );
            }
        }
        throw new RuntimeException( "Illegal module/tab state" );
    }

    /**
     * Gets the PhetApplication associated with this PhetFrame.
     *
     * @return the PhetApplication associated with this PhetFrame.
     */
    public PhetApplication getApplication() {
        return application;
    }

    //----------------------------------------------------------------
    // Menu setup methods
    //----------------------------------------------------------------

    /**
     * Adds a JMenu before the Help Menu.
     *
     * @param menu
     */
    public void addMenu( JMenu menu ) {
        SwingUtils.addMenuAt( menu, getJMenuBar(), getJMenuBar().getMenuCount() - 1 );
    }

    /**
     * Adds a menu separator to the File menu, just before the Exit menu item.
     */
    public void addFileMenuSeparator() {
        defaultFileMenu.insertSeparator( defaultFileMenu.getMenuComponentCount() - 1 );
    }

    /**
     * Adds a menu separator to the File menu after a specified menu item
     *
     * @param menuItem
     */
    public void addFileMenuSeparatorAfter( JMenuItem menuItem ) {
        JMenu fileMenu = getFileMenu();
        if( fileMenu != null ) {
            for( int i = 0; i < fileMenu.getItemCount(); i++ ) {
                if( fileMenu.getItem( i ) == menuItem ) {
                    fileMenu.insertSeparator( i + 1 );
                    return;
                }
            }
        }
    }

    /**
     * Adds a menu separator to the File menu before a specified menu item
     *
     * @param menuItem
     */
    public void addFileMenuSeparatorBefore( JMenuItem menuItem ) {
        JMenu fileMenu = getFileMenu();
        if( fileMenu != null ) {
            for( int i = 0; i < fileMenu.getItemCount(); i++ ) {
                if( fileMenu.getItem( i ) == menuItem ) {
                    fileMenu.insertSeparator( i );
                    return;
                }
            }
        }
    }

    /**
     * Adds a menu item to the File menu, just before the Exit menu item.
     *
     * @param menuItem
     */
    public void addFileMenuItem( JMenuItem menuItem ) {
        defaultFileMenu.insert( menuItem, defaultFileMenu.getMenuComponentCount() - 1 );
    }

    /**
     * Removes a menu item from the File menu
     *
     * @param menuItem
     */
    public void removeFileMenuItem( JMenuItem menuItem ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    /**
     * Sets a specified menu in the leftmost postition of the menu bar
     *
     * @param defaultFileMenu
     */
    public void setFileMenu( PhetFileMenu defaultFileMenu ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    /**
     * Returns the leftmost menu on the menu bar
     *
     * @return the leftmost menu on the menu bar
     */
    private PhetFileMenu getFileMenu() {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            return (PhetFileMenu)testMenu;
        }
        return null;
    }

    /**
     * Gets the HelpMenu for this PhetFrame.
     *
     * @return the HelpMenu for this PhetFrame.
     */
    public HelpMenu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Removes the specified JMenu from the JMenuBar.
     *
     * @param menu
     */
    public void removeMenu( JMenu menu ) {
        getJMenuBar().remove( menu );
    }

}
