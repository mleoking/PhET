/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.solublesalts.control.OptionsMenu;
import edu.colorado.phet.solublesalts.module.ConfigurableSaltModule;
import edu.colorado.phet.solublesalts.module.RealSaltsModule;
import edu.colorado.phet.solublesalts.module.SodiumChlorideModule;
import edu.colorado.phet.solublesalts.view.IonGraphic;

/**
 * SolubleSaltsApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsApplication extends PhetApplication {
    private boolean showOptions = false;

    public SolubleSaltsApplication( String[] args ) {
        super( args,
               SolubleSaltsConfig.TITLE,
               SolubleSaltsConfig.DESCRIPTION,
               SolubleSaltsConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1000, 740 ) );

        Module moduleA = new RealSaltsModule( new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT ) );
        Module moduleB = new ConfigurableSaltModule( new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT ) );
        Module moduleC = new SodiumChlorideModule( new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT ) );

        setModules( new Module[]{moduleC, moduleA, moduleB} );
        if ( showOptions ) {
            setUpOptionsMenu();
        }
    }

    private void setUpOptionsMenu() {
        this.getPhetFrame().addMenu( new OptionsMenu( getPhetFrame() ) );
    }

    public static void main( String[] args ) {

        for ( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if ( arg.equals( "-b" ) ) {
                IonGraphic.showBondIndicators( true );
            }
            if ( arg.startsWith( "-w" ) ) {
                SolubleSaltsConfig.DEFAULT_WATER_LEVEL = Integer.parseInt( arg.substring( 3 ) );
            }
            if ( arg.equals( "-o" ) ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = true;
            }
            if ( arg.startsWith( "-s=" ) ) {
                SolubleSaltsConfig.DEFAULT_LATTICE_STICK_LIKELIHOOD = Double.parseDouble( arg.substring( 3 ) );
            }
            if ( arg.startsWith( "-d=" ) ) {
                SolubleSaltsConfig.DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD = Double.parseDouble( arg.substring( 3 ) );
            }
            if ( arg.startsWith( "-c=" ) ) {
                SolubleSaltsConfig.CONCENTRATION_CALIBRATION_FACTOR = Double.parseDouble( arg.substring( 3 ) );
            }
            if ( arg.startsWith( "debug=" ) ) {
                SolubleSaltsConfig.DEBUG = true;
            }
        }

        SimStrings.getInstance().init( args, SolubleSaltsConfig.STRINGS_BUNDLE_NAME );


        Color blueBackground = new Color( 230, 250, 255 );
        Color grayBackground = new Color( 220, 220, 220 );
        UIManager.put( "Panel.background", blueBackground );
        UIManager.put( "MenuBar.background", grayBackground );
        UIManager.put( "Menu.background", grayBackground );
        UIManager.put( "TabbedPane.background", blueBackground );
        UIManager.put( "TabbedPane.selected", blueBackground );

        PhetApplication app = new SolubleSaltsApplication( args );

        app.startApplication();
    }
}
