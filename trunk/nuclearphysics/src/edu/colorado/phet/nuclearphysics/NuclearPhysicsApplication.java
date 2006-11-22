/**
 * Class: NuclearPhysicsApplication
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics;

import edu.colorado.phet.common.application.AWTSplashWindow;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayModule;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;
import edu.colorado.phet.nuclearphysics.controller.MultipleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.SingleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.util.ClockFactory;
import edu.colorado.phet.piccolo.PhetTabbedPane;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NuclearPhysicsApplication extends PiccoloPhetApplication {
//public class NuclearPhysicsApplication extends PhetApplication {

    // Localization
    public static final String localizedStringsPath = "localization/NuclearPhysicsStrings";
    private static PhetLookAndFeel phetLookAndFeel;
    private PhetTabbedPane phetTabbedPane;
    private static Color backgroundColor;

    /**
     *
     * @param args
     */
    public NuclearPhysicsApplication( String[] args ) {
        super( args, SimStrings.get( "NuclearPhysicsApplication.title" ),
               SimStrings.get( "NuclearPhysicsApplication.description" ),
               Config.version,
               new FrameSetup.CenteredWithSize( 1024, 768 ) );

        Module alphaModule = new AlphaDecayModule( ClockFactory.create( 40, Config.ALPHA_DECAY_SIM_TIME_STEP  ) );
        alphaModule.setLogoPanelVisible( false );
        Module singleNucleusFissionModule = new SingleNucleusFissionModule( ClockFactory.create( 40, 1.5 ) );
        singleNucleusFissionModule.setLogoPanelVisible( false );
        Module multipleNucleusFissionModule = new MultipleNucleusFissionModule( ClockFactory.create( 40, 6 ) );
        multipleNucleusFissionModule.setLogoPanelVisible( false );
        Module controlledReactionModule = new ControlledFissionModule( ClockFactory.create( 40, 20 ) );
        controlledReactionModule.setLogoPanelVisible( false );
        Module[] modules = new Module[]{
                alphaModule,
                singleNucleusFissionModule,
                multipleNucleusFissionModule,
                controlledReactionModule
        };
        setModules( modules );

//        getPhetFrame().addMenu( new OptionsMenu() );

        // Make the frame non-resizable for now
        getPhetFrame().setResizable( false );
    }

    /**
     * An override so we can set the color of the selected tab
     * @param modules
     * @return a JComponent that's the PhetTabbedPane for the app
     */
    public JComponent createTabbedPane( Module[] modules ) {
        phetTabbedPane = (PhetTabbedPane)super.createTabbedPane(  );
//        phetTabbedPane = (PhetTabbedPane)super.createTabbedPane( modules );
        phetTabbedPane.setSelectedTabColor( backgroundColor );
        return phetTabbedPane;
    }

    /**
     *
     * @param args
     */
    public static void main( final String[] args ) {
        SimStrings.init( args, localizedStringsPath );

        // Initialize the look and feel
        phetLookAndFeel = new PhetLookAndFeel();
//        phetLookAndFeel.setBackgroundColor( new Color( 236, 239, 254) );
        backgroundColor = new Color( 237, 232, 159);
//        backgroundColor = new Color( 227, 211, 175 );
        phetLookAndFeel.setBackgroundColor( backgroundColor );
//        phetLookAndFeel.setBackgroundColor( new Color( 203, 224, 249) );
        phetLookAndFeel.initLookAndFeel();

        AWTSplashWindow.setDefaultBackground( backgroundColor );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new NuclearPhysicsApplication( args ).startApplication();
            }
        } );
    }


    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------
    private class OptionsMenu extends JMenu {

        public OptionsMenu() {
            super( "Options");
            JMenuItem backgroundColorMI = new JMenuItem( "Background color" );
            add( backgroundColorMI );
            backgroundColorMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(),
                                                               "Background Color",
                                                               phetLookAndFeel.getBackgroundColor() );
                    phetLookAndFeel.setBackgroundColor( newColor );
                    phetLookAndFeel.initLookAndFeel();
                }
            } );

            JMenuItem foregroundColorMI = new JMenuItem( "Foreground color" );
            add( foregroundColorMI );
            foregroundColorMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(),
                                                               "Foreground Color",
                                                               phetLookAndFeel.getForegroundColor());
                    phetLookAndFeel.setForegroundColor( newColor );
                    phetLookAndFeel.initLookAndFeel();
                }
            } );
        }
    }

}