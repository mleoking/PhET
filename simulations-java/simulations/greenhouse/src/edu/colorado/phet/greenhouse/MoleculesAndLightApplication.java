// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application class for the "Molecules and Light" simulation.
 *
 * @author John Blanco
 */
public class MoleculesAndLightApplication extends PiccoloPhetApplication {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final MoleculesAndLightModule moleculesAndLightModule;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public MoleculesAndLightApplication( PhetApplicationConfig config ) {
        super( config );

        // module(s)
        PhetFrame parentFrame = getPhetFrame();
        moleculesAndLightModule = new MoleculesAndLightModule(parentFrame);
        addModule( moleculesAndLightModule );

        // Options
        getPhetFrame().addMenu( new JMenu( GreenhouseResources.getCommonString( "Common.OptionsMenu" ) ) {{
            // i18n
            add( new PropertyCheckBoxMenuItem( "White Background", moleculesAndLightModule.getWhiteBackgroundProperty() ) );
        }} );


        // Developer controls.
        JMenu developerMenu = parentFrame.getDeveloperMenu();
        final JCheckBoxMenuItem photonAbsorptionParamCheckBox = new JCheckBoxMenuItem( "Photon Absorption Controls" );
        developerMenu.add( photonAbsorptionParamCheckBox );
        photonAbsorptionParamCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moleculesAndLightModule.setPhotonAbsorptionParamsDlgVisible( photonAbsorptionParamCheckBox.isSelected() );
            }
        } );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Main entry point for this simulation.
     */
    public static void main( String[] args ) {
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MoleculesAndLightApplication( config );
            }
        };
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, GreenhouseConfig.PROJECT_NAME,
                GreenhouseConfig.FLAVOR_NAME_MOLECULES_AND_LIGHT );
        appConfig.setLookAndFeel( new MoleculesAndLightLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    private static class MoleculesAndLightLookAndFeel extends PhetLookAndFeel {
        public MoleculesAndLightLookAndFeel() {
            setBackgroundColor( GreenhouseConfig.PANEL_BACKGROUND_COLOR );
            setTitledBorderFont( new PhetFont( Font.PLAIN, 12 ) );
        }
    }

}
