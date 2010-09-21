/*
  java edu.colorado.phet.batteryvoltage.BatteryApplet
*/

package edu.colorado.phet.batteryvoltage;

import java.awt.*;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

public class BatteryVoltageSimulationPanel extends JPanel {

    public BatteryVoltageSimulationPanel( IClock clock ) {
        int width = 500;
        int height = 300;
        int barrierX = 100;
        int barrierWidth = 300;
        int numElectrons = 30;

        int x = 20;
        int y = 20;
        int seed = 0;
        int numMen = 7;
        final Battery b = new Battery( x, y, width, height, barrierX, barrierWidth, numElectrons, new Random( seed ), numMen, 0.021 );

        setLayout( new BorderLayout() );
        add( b.getJPanel(), BorderLayout.CENTER );

        JPanel south = new JPanel();
        add( south, BorderLayout.SOUTH );
        south.setLayout( new BorderLayout() );

        south.add( b.getControlPanel(), BorderLayout.CENTER );
        validate();

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                b.getSystem().iterate( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

}
