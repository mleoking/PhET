/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.Man;

/**
 * User: Sam Reid
 * Date: May 4, 2005
 * Time: 6:34:17 PM
 * Copyright (c) May 4, 2005 by Sam Reid
 */
public abstract class ManValueChange {
    PlotSet plotSet;
    MovingManModule module;

    protected ManValueChange( MovingManModule module, PlotSet plotSet ) {
        this.module = module;
        this.plotSet = plotSet;
    }

    abstract void setValue( Man man, double value );

    public static class PositionChange extends ManValueChange {
        public PositionChange( MovingManModule module, PlotSet plotSet ) {
            super( module, plotSet );
        }

        public void setValue( Man man, double value ) {
            if( !module.isPaused() ) {
                man.setAcceleration( 0.0 );
                man.setVelocity( 0.0 );
            }
            man.setPosition( value );
            plotSet.getPositionPlotSuite().valueChanged( value );
            plotSet.notifyPositionControlMode();
        }
    }

    public static class VelocityChange extends ManValueChange {
        public VelocityChange( MovingManModule module, PlotSet plotSet ) {
            super( module, plotSet );
        }

        void setValue( Man man, double value ) {
            if( !module.isPaused() ) {
                man.setAcceleration( 0.0 );
            }
            man.setVelocity( value );
            plotSet.getVelocityPlotSuite().valueChanged( value );
            plotSet.notifyVelocityControlMode();
        }
    }

    public static class AccelerationChange extends ManValueChange {

        public AccelerationChange( MovingManModule module, PlotSet plotSet ) {
            super( module, plotSet );
        }

        void setValue( Man man, double value ) {
            man.setAcceleration( value );
            plotSet.getAccelerationPlotSuite().valueChanged( value );
            plotSet.notifyAccelerationControlMode();
        }
    }
}
