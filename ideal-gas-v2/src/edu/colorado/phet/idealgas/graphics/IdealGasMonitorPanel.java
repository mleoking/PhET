/*
 * Class: IdealGasMonitorPanel
 * Package: edu.colorado.phet.graphicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 30, 2002
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.controller.TabbedMainPanel;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.model.IdealGasSystem;
import edu.colorado.phet.idealgas.model.Gravity;
//import edu.colorado.phet.idealgas.model.body.Balloon;
//import edu.colorado.phet.model.PhysicalSystem;
//import edu.colorado.phet.model.Gravity;
//import edu.colorado.phet.model.body.Particle;
//import edu.colorado.phet.graphics.MonitorPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.text.NumberFormat;

/**
 *
 */
public class IdealGasMonitorPanel extends MonitorPanel {

    private GasMonitorPanel globalGasMonitorPanel;
    private GasSpeciesMonitorPanel heavySpeciesPanel;
    private GasSpeciesMonitorPanel lightSpeciesPanel;
    private BalloonPressureMonitor balloonPressureMonitorPanel;

    /**
     * Constructor
     */
    public IdealGasMonitorPanel( TabbedMainPanel parent ) {

        globalGasMonitorPanel = new GasMonitorPanel( new Class[]{ HeavySpecies.class, LightSpecies.class });
        this.add( globalGasMonitorPanel );

        balloonPressureMonitorPanel = new BalloonPressureMonitor();
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
    public void clear() {

        super.clear();

        globalGasMonitorPanel.clear();
        heavySpeciesPanel.clear();
        lightSpeciesPanel.clear();
    }

    /**
     *
     */
    public void update( Observable observable, Object o ) {

        super.update( observable, o );

        long now = System.currentTimeMillis();
/*        if( now - getLastUpdateTime() >= getUpdateInterval() )*/ {
            setLastUpdateTime( now );
            globalGasMonitorPanel.update( observable, o );
            heavySpeciesPanel.update( observable, o );
            lightSpeciesPanel.update( observable, o );
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

    public BalloonPressureMonitor getBalloonPressureMonitorPanel() {
        return balloonPressureMonitorPanel;
    }

    public void setGravity( Gravity gravity ) {
        globalGasMonitorPanel.setGravity( gravity );
    }
}
