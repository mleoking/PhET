
package edu.colorado.phet.greenhouse;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * General comments, issues:
 * I wrote this using real model coordinates and units. The origin is at the center of the earth, and the positive
 * y direction is up. Unfortunately, this has turned out to cause a host of issues.
 * <p/>
 * The snow in the ice age reflects photons, but is not really in the model. Instead I do a rough estimate of where
 * it is in the background image (in the view) and use that.
 * 
 * @author John Blanco
 */
public class GreenhouseApplication2 extends PiccoloPhetApplication {
    
    private PhotonAbsorptionModule photonAbsorptionModule;
    
    public GreenhouseApplication2( PhetApplicationConfig config ) {
        super( config );

        // modules
        PhetFrame parentFrame = getPhetFrame();
        photonAbsorptionModule = new PhotonAbsorptionModule(parentFrame);
        
        // There is some weird order dependency with these modules, so they
        // are constructed in one order and added in another.  This is due to
        // a static var in BaseGreenhouseModule that I just can't take the
        // time to deal with right now.  jblanco, 8/30/2010.
        GreenhouseModule greenhouseModule = new GreenhouseModule();
        GlassPaneModule glassPaneModule = new GlassPaneModule();
        addModule( glassPaneModule );
        addModule( greenhouseModule );
        addModule( photonAbsorptionModule );
        
        // Developer controls.
        JMenu developerMenu = parentFrame.getDeveloperMenu();
        final JCheckBoxMenuItem photonAbsorptionParamCheckBox = new JCheckBoxMenuItem( "Photon Absorption Controls" );
        developerMenu.add( photonAbsorptionParamCheckBox );
        photonAbsorptionParamCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photonAbsorptionModule.setPhotonAbsorptionParamsDlgVisible( photonAbsorptionParamCheckBox.isSelected() );
            }
        } );

        paintContentImmediately();
        getPhetFrame().addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                paintContentImmediately();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
    }

    public static void paintContentImmediately() {
        Container contentPane = PhetApplication.getInstance().getPhetFrame().getContentPane();
        if ( contentPane instanceof JComponent ) {
            JComponent jComponent = (JComponent) contentPane;
            jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
        }
    }

    private static class GreenhouseLookAndFeel extends PhetLookAndFeel {
        public GreenhouseLookAndFeel() {
            setBackgroundColor( GreenhouseConfig.PANEL_BACKGROUND_COLOR );
            setTitledBorderFont( new PhetFont( Font.PLAIN, 12 ) );
        }
    }

    public static void main( String[] args ) {
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new GreenhouseApplication2( config );
            }
        };
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, GreenhouseConfig.PROJECT_NAME );
        appConfig.setLookAndFeel( new GreenhouseLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
