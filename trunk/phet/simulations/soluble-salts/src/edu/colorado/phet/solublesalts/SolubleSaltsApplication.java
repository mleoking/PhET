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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.solublesalts.control.OptionsMenu;
import edu.colorado.phet.solublesalts.module.ConfigurableSaltModule;
import edu.colorado.phet.solublesalts.module.RealSaltsModule;
import edu.colorado.phet.solublesalts.module.SodiumChlorideModule;
import edu.colorado.phet.solublesalts.view.IonGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * SolubleSaltsApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsApplication extends PiccoloPhetApplication {

    /**
     *
     * @param args
     */
    public SolubleSaltsApplication( String[] args ) {
        super( args,
               SolubleSaltsConfig.TITLE,
               SolubleSaltsConfig.DESCRIPTION,
               SolubleSaltsConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1000, 740 ) );

        IClock moduleAClock = new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT );
        Module moduleA = new RealSaltsModule( moduleAClock );

        IClock moduleBClock = new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT );
        Module moduleB = new ConfigurableSaltModule( moduleBClock );

        IClock moduleCClock = new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT );
        Module moduleC = new SodiumChlorideModule( moduleCClock );

        setModules( new Module[]{moduleC, moduleA, moduleB } );

//        setUpOptionsMenu();
    }

    private void setUpOptionsMenu() {
        this.getPhetFrame().addMenu( new OptionsMenu( getPhetFrame() ) );
    }


    /**
     * Main
     *
     * @param args
     */
    public static void main( String[] args ) {

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( "-b" ) ) {
                IonGraphic.showBondIndicators( true );
            }
            if( arg.startsWith( "-w" ) ) {
                int d = Integer.parseInt( arg.substring( 3 ) );
                SolubleSaltsConfig.DEFAULT_WATER_LEVEL = d ;
            }
            if( arg.equals( "-o" ) ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = true;
            }
            if( arg.startsWith( "-s=" ) ) {
                double d = Double.parseDouble( arg.substring( 3 ) );
                SolubleSaltsConfig.DEFAULT_LATTICE_STICK_LIKELIHOOD = d;
            }
            if( arg.startsWith( "-d=" ) ) {
                double d = Double.parseDouble( arg.substring( 3 ) );
                SolubleSaltsConfig.DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD = d;
            }
            if( arg.startsWith( "-c=" ) ) {
                double c = Double.parseDouble( arg.substring( 3 ) );
                SolubleSaltsConfig.CONCENTRATION_CALIBRATION_FACTOR = c;
            }
            if( arg.startsWith( "debug=" ) ) {
                SolubleSaltsConfig.DEBUG = true;
            }
        }

        SimStrings.init( args, SolubleSaltsConfig.STRINGS_BUNDLE_NAME );


        Color blueBackground = new Color( 230, 250, 255 );
        Color grayBackground = new Color( 220, 220, 220 );
        UIManager.put( "Panel.background", blueBackground );
        UIManager.put( "MenuBar.background", grayBackground );
        UIManager.put( "Menu.background", grayBackground );
        UIManager.put( "TabbedPane.background", blueBackground );
        UIManager.put( "TabbedPane.selected", blueBackground );

        PhetApplication app = new SolubleSaltsApplication( args );

        app.startApplication();


        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( "-m" ) ) {
                PhetPCanvas simPanel = (PhetPCanvas)app.getActiveModule().getSimulationPanel();
                if( simPanel != null ) {
//                    PMouseTracker mouseTracker = new PMouseTracker( simPanel );
//                    simPanel.addWorldChild( mouseTracker );
                }
            }
        }
    }
}
