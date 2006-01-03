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
//import edu.colorado.phet.common.model.clock.IClock;
//import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.control.OptionsMenu;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.util.PMouseTracker;
//import edu.colorado.phet.piccolo.PhetPCanvas;
//import edu.colorado.phet.piccolo.util.PMouseTracker;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * SolubleSaltsApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsApplication extends PhetApplication {


    private static IClock CLOCK = new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT);

    public SolubleSaltsApplication( String[] args ) {
        super( args,
               SolubleSaltsConfig.TITLE,
               SolubleSaltsConfig.DESCRIPTION,
               SolubleSaltsConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1000, 740 ) );

        Module module = new SolubleSaltsModule( CLOCK );
        setModules( new Module[]{module} );

        setUpOptionsMenu();
    }

    private void setUpOptionsMenu() {
//        JMenu optionsMenu = new JMenu( "Options" );
//        optionsMenu.setMnemonic( 'O' );
//        final JCheckBoxMenuItem showBondIndicatorMI = new JCheckBoxMenuItem( "Show bond indicators" );
//        optionsMenu.add( showBondIndicatorMI );
//        showBondIndicatorMI.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                IonGraphic.showBondIndicators( showBondIndicatorMI.isSelected() );
//            }
//        } );
//
//        final JCheckBoxMenuItem randomWalkMI = new JCheckBoxMenuItem( "Random walk" );
//        optionsMenu.add( randomWalkMI );
//        randomWalkMI.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                SolubleSaltsConfig.RANDOM_WALK = randomWalkMI.isSelected();
//            }
//        } );
//
//        final JCheckBoxMenuItem oneCrystalMI = new JCheckBoxMenuItem( "One crystal only" );
//        optionsMenu.add(  oneCrystalMI );
//        oneCrystalMI.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = oneCrystalMI.isSelected();
//            }
//        } );
//
//        final JMenuItem randomWaltkThetaMI = new JMenuItem( "Adjust random walk...");
//        randomWaltkThetaMI.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                final JDialog dlg = new JDialog( getPhetFrame(), "Random Walk  Adjusment", false );
//                dlg.getContentPane().setLayout( new BorderLayout() );
//                final JSlider sldr = new JSlider( 0, 360, (int)Ion.randomWalkTheta );
//                sldr.setMajorTickSpacing( 45 );
//                sldr.setMinorTickSpacing( 15 );
//                sldr.setPaintTicks( true );
//                sldr.setPaintLabels( true );
//                sldr.addChangeListener( new ChangeListener() {
//                    public void stateChanged( ChangeEvent e ) {
//                        Ion.randomWalkTheta = sldr.getValue();
//                    }
//                } );
//                dlg.getContentPane().add( sldr );
//                JButton btn = new JButton( "Close" );
//                btn.addActionListener( new ActionListener() {
//                    public void actionPerformed( ActionEvent e ) {
//                        dlg.setVisible( false );
//                    }
//                } );
//                JPanel btnPnl = new JPanel( );
//                btnPnl.add( btn );
//                dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
//                dlg.pack();
//                dlg.setVisible( true );
//            }
//        } );
//        optionsMenu.add( randomWaltkThetaMI );
//
//        this.getPhetFrame().addMenu( optionsMenu );
        this.getPhetFrame().addMenu( new OptionsMenu( getPhetFrame() ));
    }

    public static void main( String[] args ) {

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( "-b" ) ) {
                IonGraphic.showBondIndicators( true );
            }
            if( arg.startsWith( "-w" )) {
                int d = Integer.parseInt( arg.substring( 3 ) );
                SolubleSaltsConfig.DEFAULT_WATER_LEVEL = d;
            }
            if( arg.equals( "-o" ) ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = true;
            }
        }

        SimStrings.init( args, SolubleSaltsConfig.STRINGS_BUNDLE_NAME );
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
