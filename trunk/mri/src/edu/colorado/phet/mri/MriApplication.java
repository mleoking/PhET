/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.controller.HeadModule;
import edu.colorado.phet.mri.controller.NmrModule;
import edu.colorado.phet.mri.controller.OptionMenu;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MriApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriApplication extends PiccoloPhetApplication {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static String title = "Simplified MRI";
    private static String description = "A simplified model of magnetic resonance imaging";
    private static String version = "0.01.13";
    private static FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Module[] singleModule = new Module[]{
            new NmrModule(),
    };

    private Module[] fullAppModules = new Module[]{
            new NmrModule(),
            new HeadModule(),
//            new ScanModule(),
//            new ScanModuleB(),
    };

    private Module[] modules = fullAppModules;

    public MriApplication( String[] args ) {
//        super( args, title, description, version, frameSetup, PiccoloPhetApplication.JTABBED_PANE );
        super( args, title, description, version, frameSetup );
//        super( args, title, description, version, frameSetup, PiccoloPhetApplication.PHET_TABBED_PANE );
        setModules( modules );
    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-d" ) ) {
                PhetUtilities.getPhetFrame().addMenu( new OptionMenu() );
            }
            if( arg.equals( "-singlemodule" ) ) {
                modules = singleModule;
            }
        }
    }


    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------
    private class OptionsMenu extends JMenu {

        public OptionsMenu() {
            super( "Options" );
            final PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
            JMenuItem backgroundColorMI = new JMenuItem( "Background color" );
            add( backgroundColorMI );
            backgroundColorMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(),
                                                               "Background Color",
                                                               phetLookAndFeel.getBackgroundColor() );
                    phetLookAndFeel.setBackgroundColor( newColor );
                    phetLookAndFeel.apply();
                }
            } );

            JMenuItem foregroundColorMI = new JMenuItem( "Foreground color" );
            add( foregroundColorMI );
            foregroundColorMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(),
                                                               "Foreground Color",
                                                               phetLookAndFeel.getForegroundColor() );
                    phetLookAndFeel.setForegroundColor( newColor );
                    phetLookAndFeel.apply();
                }
            } );
        }
    }


    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PhetLookAndFeel.setLookAndFeel();
                SimStrings.init( args, MriConfig.STRINGS_BUNDLE_NAME );
                PhetApplication app = new MriApplication( args );
                app.startApplication();
            }
        } );

    }
}
