/*
 * Class: PhetFrame
 * Package: edu.colorado.phet.controller
 *
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 12:09:19 PM
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.graphics.util.HelpItem;
import edu.colorado.phet.graphics.util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * This class provides the frame for PhetApplications. It provides default menu
 * and on-screen help facilities.
 * <p>
 * The on-screen help facility is implemented with an inner class that is used as the
 * PhetFrame's glassPane. Clients can add on-screen help items to the PhetFrame by
 * creating instances of OnScreenHelpItemOld and call addHelpItem() on the PhetFrame.
 * @see HelpItem
 */
public class PhetFrame extends JFrame {

    private PhetApplication phetApplication;
    private JMenuBar menuBar;
    private ArrayList helpItems = new ArrayList();
    private boolean helpVisible;

    /**
     * @see Config
     * @param phetApplication The PhetApplication with which this PhetFrame
     * is associated
     * @param config An instance of an application-specific subclass of Config
     * associated with the PhetApplication
     */
    public PhetFrame( PhetApplication phetApplication, Config config ) {

        this.phetApplication = phetApplication;
        setTitle( "PhET: " + config.getTitle() );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent event ) {
                PhetFrame.this.hide();
                System.exit( 0 );
            }
        } );

        // Add the Ctl-M key binding to toggle display of menus
        this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_M,java.awt.event.InputEvent.CTRL_MASK),
                                    "toggleMenus");
        this.getRootPane().getActionMap().put("toggleMenus",
                                     new MenuEasterEgg());

        // Add menus
        this.createMenus();

        // Set up the help pane
        setHelpVisible( true );

        pack();
    }

    /**
     * Adds an instance of an application-specific subclass of PhetMainPanel
     * to the frame. This method is called within the framework, and should
     * not be called by specific applications.
     * @see PhetMainPanel
     * @param mainPanel The application's PhetMainPanel
     */
    public void addMainPanel( PhetMainPanel mainPanel ) {
        this.getContentPane().add( mainPanel );
        pack();
    }

    /**
     * Adds an on-screen help item to the PhetFrame. This is typically called
     * in the activate() method of an application-specific subclass of
     * ApparatusPanel.
     * @see HelpItem
     * @see edu.colorado.phet.graphics.ApparatusPanel
     * @param helpText
     */
    public void addHelpItem( HelpItem helpText ) {
        this.getLayeredPane().add( helpText, JLayeredPane.POPUP_LAYER );
        helpItems.add( helpText );
    }

    public void setHelpVisible( boolean isVisible ) {
        helpVisible = isVisible;
        for( int i = 0; i < helpItems.size(); i++ ) {
            HelpItem helpItem = (HelpItem)helpItems.get( i );
            helpItem.setVisible( isVisible );
        }
    }

    /**
     * Removes all OnScreenHelpItems from the PhetFrame
     */
    public void clearHelp() {
        setHelpVisible( false );
        helpItems.clear();
        setHelpVisible( helpVisible );
    }

    /**
     * Displays all on-screen help
     */
    public void showHelp() {
        this.setHelpVisible( true );
    }

    /**
     * Hides all on-screen help
     */
    public void hideHelp() {
        this.setHelpVisible( false );
    }

    /**
     *
     * @return
     */
    public HelpPane getHelpPane() {
//        return helpPane;
        // TODO: fix
        return null;
    }

    /**
     * Creates default menus
     */
    private void createMenus() {

        // Create the menu bar
        menuBar = new JMenuBar();
//        this.setJMenuBar( menuBar );

        // Controls menu
        JMenu controlsMenu = phetApplication.createControlsMenu( this );
        if( controlsMenu != null ) {
            menuBar.add( controlsMenu );
        }

        // Test Menu
        JMenu testMenu = phetApplication.createTestMenu();
        if( testMenu != null ) {
            menuBar.add( testMenu );
        }

        // Help Menu
        menuBar.add( new PhetHelpMenu( this, phetApplication.getAboutDialog( this ) ) );
    }

    //
    // Inner classes
    //

    /**
     * A subclass of JPanel that is used as the PhetFrames glassPane to
     * display on-screen help
     * @see HelpItem
     */
    public static class HelpPane extends JPanel implements MouseListener {

        ArrayList helpItems = new ArrayList();

        HelpPane() {
            setOpaque( false );
        }

//        void addHelpItem( OnScreenHelpItemOld helpItem ) {
//            helpItems.add( helpItem );
//        }

        void clear() {
            helpItems.clear();
        }

        public void paint( Graphics g ) {
//            Graphics2D g2 = (Graphics2D)g;
//            g2.setFont( new Font( "sans serif", Font.PLAIN, 16 ) );
//            for( int i = 0; i < helpItems.size(); i++ ) {
//                OnScreenHelpItemOld onScreenHelpItem = (OnScreenHelpItemOld)helpItems.get( i );
//                onScreenHelpItem.paint( g2 );
//            }
        }

        public void mouseClicked( MouseEvent e ) {
        }

        public void mousePressed( MouseEvent e ) {
        }

        public void mouseReleased( MouseEvent e ) {
        }

        public void mouseEntered( MouseEvent e ) {
        }

        public void mouseExited( MouseEvent e ) {
        }
    }

    /**
     * An Action class to toggle the display of menus
     */
    private class MenuEasterEgg extends AbstractAction {
        private boolean menuShowing;
        public void actionPerformed( ActionEvent evt ) {
            if( !menuShowing ) {
                PhetFrame.this.setJMenuBar( menuBar );
                menuShowing = true;
            }
            else {
                PhetFrame.this.setJMenuBar( null );
                menuShowing = false;
            }
            PhetFrame.this.pack();
        }
    }


    //
    // Static fields and methods
    //
    private static  Image s_helpItemIcon;
    private static ResourceLoader s_loader;
    static {
        s_loader = new ResourceLoader();
        s_helpItemIcon= s_loader.loadImage( Config.HELP_ITEM_ICON_IMAGE_FILE ).getImage();
    }
}
