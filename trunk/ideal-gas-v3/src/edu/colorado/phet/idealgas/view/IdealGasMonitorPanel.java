/*
 * Class: IdealGasMonitorPanel
 * Package: edu.colorado.phet.graphicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 30, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.view.monitors.GasMonitorPanel;
import edu.colorado.phet.idealgas.view.monitors.GasSpeciesMonitorPanel;
import edu.colorado.phet.idealgas.view.monitors.PhetMonitorPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 *
 */
public class IdealGasMonitorPanel extends PhetMonitorPanel implements SimpleObserver {

    private GasMonitorPanel globalGasMonitorPanel;
    private GasSpeciesMonitorPanel heavySpeciesPanel;
    private GasSpeciesMonitorPanel lightSpeciesPanel;
//    private BalloonPressureMonitor balloonPressureMonitorPanel;

    /**
     * Constructor
     */
    public IdealGasMonitorPanel( IdealGasModel model ) {

        globalGasMonitorPanel = new GasMonitorPanel( new Class[]{ HeavySpecies.class, LightSpecies.class },
                                                     model );
        this.add( globalGasMonitorPanel );

//        balloonPressureMonitorPanel = new BalloonPressureMonitor();
//        this.add( balloonPressureMonitorPanel );

        JPanel speciesPanel = new JPanel( new GridLayout( 2, 1 ));
        heavySpeciesPanel = new GasSpeciesMonitorPanel( HeavySpecies.class, "Heavy species" );
        speciesPanel.add( heavySpeciesPanel );
        lightSpeciesPanel = new GasSpeciesMonitorPanel( LightSpecies.class, "Light species" );
        speciesPanel.add( lightSpeciesPanel );

        this.add( speciesPanel );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );

        // Note: These two lines should be un-commented when and if we decide
        // to use a separate thread for this panel
//        MonitorClock monitorClock = new MonitorClock( this, parent.getPhysicalSystem() );
//        monitorClock.start();
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

        long now = System.currentTimeMillis();
/*        if( now - getLastUpdateTime() >= getUpdateInterval() )*/ {
            super.setLastUpdateTime( now );
            globalGasMonitorPanel.update();
            heavySpeciesPanel.update();
            lightSpeciesPanel.update();
//            globalGasMonitorPanel.update( observable, o );
//            heavySpeciesPanel.update( observable, o );
//            lightSpeciesPanel.update( observable, o );
        }
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
