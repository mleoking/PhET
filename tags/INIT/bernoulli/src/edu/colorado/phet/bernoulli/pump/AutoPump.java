package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.coreadditions.clock2.SimulationTimeListener;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 10:32:18 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class AutoPump implements SimulationTimeListener {
    Pump pump;
    SimulationTimeListener todo;
    private double speed = .004;
    public static boolean active = false;

    SimulationTimeListener startDraw = new SimulationTimeListener() {
        public void simulationTimeIncreased( double v ) {
            pump.getBottomValve().setOpen( true );
            pump.getTopValve().setOpen( false );
            todo = drawIn;
        }
    };
    SimulationTimeListener drawIn = new SimulationTimeListener() {
        public void simulationTimeIncreased( double dt ) {
            pump.getPiston().translate( -speed * dt );
            if( pump.getPiston().getExtension() <= pump.getPiston().getWidth() ) {
                todo = startExpel;
            }
        }
    };
    SimulationTimeListener startExpel = new SimulationTimeListener() {
        public void simulationTimeIncreased( double v ) {
            pump.getBottomValve().setOpen( false );
            pump.getTopValve().setOpen( true );
            todo = expel;
        }
    };
    SimulationTimeListener expel = new SimulationTimeListener() {
        public void simulationTimeIncreased( double dt ) {
            pump.getPiston().translate( speed * dt );
            if( pump.getPiston().getExtension() >= pump.getPiston().getMaxExtension() ) {
                todo = startDraw;
            }
        }
    };

    public AutoPump( Pump pump ) {
        this.pump = pump;
        this.todo = startDraw;
    }

    public void simulationTimeIncreased( double dt ) {
        if( active ) {
            todo.simulationTimeIncreased( dt );
        }
    }
}
