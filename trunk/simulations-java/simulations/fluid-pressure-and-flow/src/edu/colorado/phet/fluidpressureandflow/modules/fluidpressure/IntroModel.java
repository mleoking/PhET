package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;

/**
 * @author Sam Reid
 */
public class IntroModel {
    private static final int STANDARD_AIR_PRESSURE = 101325;//Pascals is MKS, see http://en.wikipedia.org/wiki/Atmospheric_pressure
    public static final double g = 9.8;
    private Function.LinearFunction pressure = new Function.LinearFunction( 0, 500, STANDARD_AIR_PRESSURE, 99490 );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
    private ConstantDtClock clock = new ConstantDtClock( 30 );
    private Pool pool = new Pool();
    private PressureSensor pressureSensor0 = new PressureSensor( this, 0, 0 );
    private PressureSensor pressureSensor1 = new PressureSensor( this, -4, 1 );

    public ConstantDtClock getClock() {
        return clock;
    }

    public PressureSensor getPressureSensor0() {
        return pressureSensor0;
    }

    public PressureSensor getPressureSensor1() {
        return pressureSensor1;
    }

    public double getPressureSensor( Point2D position ) {
        if ( position.getY() > 0 ) {
            return pressure.evaluate( position.getY() );
        }
        else {
            return STANDARD_AIR_PRESSURE + pool.getLiquidDensity() * g * Math.abs( 0 - position.getY() );
        }
    }

    public Pool getPool() {
        return pool;
    }

    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        pool.addDensityListener( updatePressure );
    }
}
