/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import edu.colorado.phet.common.application.SplashWindow;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.simlauncher.menus.SimLauncherMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * SimLauncher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncher {

    SplashWindow splashWindow;
    Frame splashWindowOwner;

    /**
     * Private constructor. The only instance created is created by main()
     */
    private SimLauncher() {

        showSplashWindow( "PhET Simulation Launcher" );

        JFrame frame = new JFrame( "PhET Simulation Launcher" );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                centerFrame( (JFrame)e.getComponent() );
            }
        } );
        frame.setContentPane( TopLevelPane.getInstance() );
        frame.setJMenuBar( new SimLauncherMenuBar() );
        frame.pack();
        new FrameSetup.CenteredWithSize( 800, 600 ).initialize( frame );
        frame.setVisible( true );

        disposeSplashWindow();

        // Display any startup notices the user should see
        startupNotices( frame );

        // Create a notifier that will tell the user when the status of the connection
        // to the PhET site changes
        new PhetSiteConnectionStatusNotifier( frame, PhetSiteConnection.instance() );
    }


    private void showSplashWindow( String title ) {
        if( splashWindow == null ) {
            // PhetFrame doesn't exist when this is called, so create and manage the window's owner.
            splashWindowOwner = new Frame();
            splashWindow = new SplashWindow( splashWindowOwner, title );
            splashWindow.show();
        }
    }

    public SplashWindow getSplashWindow() {
        return splashWindow;
    }

    private void disposeSplashWindow() {
        if( splashWindow != null ) {
            splashWindow.dispose();
            splashWindow = null;
            // Clean up the window's owner that we created in showSplashWindow.
            splashWindowOwner.dispose();
            splashWindowOwner = null;
        }
    }


    /**
     * Notifies the user if there is no connection to the PhET site. If there is a connectin and there are no
     * simulations installed, asks the user if he would like to go directly to the catalog. This is a
     * simple way of guiding a first-time user into understanding how the program works.
     *
     * @param parent
     */
    private void startupNotices( Component parent ) {

        // No internet connection and no installed simulations
        if( !PhetSiteConnection.instance().isConnected() && Catalog.instance().getInstalledSimulations() == null ) {
            JOptionPane.showMessageDialog( parent,
                                           "<html>You have no simulations installed and are do not have an" +
                                           "internet connection to the PhET web site." +
                                           "<br><br>You need to establish an internet connection before you can" +
                                           "<br>install any PhET simulations. " );
        }

        // Installed simulations, but no internet connection
        else
        if( !PhetSiteConnection.instance().isConnected() && Catalog.instance().getInstalledSimulations() != null ) {
            JOptionPane.showMessageDialog( parent,
                                           "<html>You are working offline." +
                                           "<br><br>You will be able to run simulations you've installed, but " +
                                           "<br>will not be able to browse the online catalog of simulations. " );
        }

        // If there aren't any simulations installed, ask the user if he'd like to go to the catalog
        else if( Catalog.instance().getInstalledSimulations() == null
                 || Catalog.instance().getInstalledSimulations().size() == 0 ) {
            int choice = JOptionPane.showConfirmDialog( parent, "<html><center>You have no simulations installed.<br>Would you like to visit the online simulation catalog?</html>", "Startup..", JOptionPane.YES_NO_OPTION );
            if( choice == JOptionPane.YES_OPTION ) {
                if( !PhetSiteConnection.instance().isConnected() ) {
                    JOptionPane.showMessageDialog( parent, "The online simulation catolg is not available.\nEither you do not have access to the internet, or the PhET web site is not available." );
                }
                else {
                    TopLevelPane.getInstance().setActivePane( TopLevelPane.ONLINE_SIMS_PANE );
                }
            }
        }
    }

    private static void parseArgs( String[] args ) {
        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-no_web")) {
                DebugFlags.NO_PHET_SITE_CONNECTION_AVAILABLE = true;
            }
        }
    }

    private static void centerFrame( JFrame frame ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int x = ( d.width - frame.getWidth() ) / 2;
        int y = ( d.height - frame.getHeight() ) / 2;
        frame.setLocation( x, y );
    }

    public static void main( String[] args ) {
        parseArgs( args );

        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        Font font = phetLookAndFeel.getTabFont();
        Font newFont = new Font( font.getName(), font.getStyle(),  font.getSize() - 4);
        phetLookAndFeel.setTabFont( newFont );
        phetLookAndFeel.initLookAndFeel();

        new SimLauncher();
    }
}