package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.common.IdeaGraphic3;
import edu.colorado.phet.coreadditions.clock2.SimulationTimeListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 10:32:18 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class AutoPumpWithExplanation implements SimulationTimeListener {
    Pump pump;
    private BernoulliModule module;
    SimulationTimeListener todo;

    SimulationTimeListener startup = new SimulationTimeListener() {
        public void simulationTimeIncreased( double v ) {
            module.getApparatusPanel().addGraphic( startupExplanation, 1 );
        }
    };
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
            if( pump.getPiston().getExtension() <= 0 ) {
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
    private double speed = .0006;
    private boolean active = true;
    private boolean inited = false;
    private IdeaGraphic3 startupExplanation;

    public AutoPumpWithExplanation( Pump pump, BernoulliModule module ) {
        this.pump = pump;
        this.module = module;
        this.todo = startup;
    }

    public void simulationTimeIncreased( double dt ) {
        if( active && inited ) {
            todo.simulationTimeIncreased( dt );
        }
        else if( !inited ) {
            init();
        }
    }

    private void init() {
        Graphics2D g2 = (Graphics2D)module.getApparatusPanel().getGraphics();
        if( g2 == null ) {
            return;//can't init yet.
        }
        startupExplanation = new IdeaGraphic3( true, 100, 100, new String[]{"This is the pump.", "Let's try to make it work."}, g2.getFontRenderContext(), new Font( "Lucida Sans", 0, 24 ), Color.black, null, Color.yellow, module.getApparatusPanel() );
        inited = true;

    }
}
