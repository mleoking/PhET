/*
 * Class: IdealGasMonitorPanel
 * Package: edu.colorado.phet.graphicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 30, 2002
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.view.SimStrings;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 *
 */
public class IdealGasMonitorPanel extends JPanel {

    private GasMonitorPanel globalGasMonitorPanel;
    private GasSpeciesMonitorPanel heavySpeciesPanel;
    private GasSpeciesMonitorPanel lightSpeciesPanel;
    //    private BalloonPressureMonitor balloonPressureMonitorPanel;

    /**
     * Constructor
     */
    public IdealGasMonitorPanel( IdealGasModel model ) {

        globalGasMonitorPanel = new GasMonitorPanel( model );
        this.add( globalGasMonitorPanel );

        //        balloonPressureMonitorPanel = new BalloonPressureMonitor();
        //        this.add( balloonPressureMonitorPanel );

        JPanel speciesPanel = new JPanel( new GridLayout( 2, 1 ) );
        heavySpeciesPanel = new GasSpeciesMonitorPanel( HeavySpecies.class, SimStrings.get( "IdealGasMonitorPanel.Heavy_species" ), model );
        speciesPanel.add( heavySpeciesPanel );
        lightSpeciesPanel = new GasSpeciesMonitorPanel( LightSpecies.class, SimStrings.get( "IdealGasMonitorPanel.Light_species" ), model );
        speciesPanel.add( lightSpeciesPanel );
        this.add( speciesPanel );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );

        // Set up an event thread to update the monitor panel
        final long updateInterval = 500;
        Thread updater = new Thread( new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( updateInterval );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    update();
                }
            }
        } );
        updater.start();
    }

    /**
     *
     */
    //    public void clear() {
    //
    ////        super.clear();
    //
    //        globalGasMonitorPanel.clear();
    //        heavySpeciesPanel.clear();
    //        lightSpeciesPanel.clear();
    //    }

    /**
     *
     */
    public void update() {
        globalGasMonitorPanel.update();
        heavySpeciesPanel.update();
        lightSpeciesPanel.update();
        //            globalGasMonitorPanel.update( observable, o );
        //            heavySpeciesPanel.update( observable, o );
        //            lightSpeciesPanel.update( observable, o );
    }

    /**
     *
     */
    public void setOomSpinnersVisible( boolean isVisible ) {
        globalGasMonitorPanel.setOomSpinnersVisible( isVisible );
    }

    /**
     *
     */
    public boolean isOomSpinnersVisible() {
        return globalGasMonitorPanel.isOomSpinnersVisible();
    }

    //    public BalloonPressureMonitor getBalloonPressureMonitorPanel() {
    //        return balloonPressureMonitorPanel;
    //    }

    public void setGravity( Gravity gravity ) {
        globalGasMonitorPanel.setGravity( gravity );
    }
}
