package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 6:19:17 PM
 */
public class MotionBody implements IUpdateStrategy {

    private MotionBodySeries motionBodySeries = new MotionBodySeries();
    private MotionBodyState motionBodyState = new MotionBodyState();//current state

    public MotionBodySeries getMotionBodySeries() {
        return motionBodySeries;
    }

    public MotionBodyState getMotionBodyState() {
        return motionBodyState;
    }

    public void setTime( double time ) {
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValueForTime( time ) );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValueForTime( time ) );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValueForTime( time ) );
    }

    public void stepInTime( double time, double dt ) {
        motionBodySeries.stepInTime( time, motionBodyState, dt );
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValue() );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValue() );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValue() );
    }

    public void clear() {
        motionBodySeries.clear();
    }

    public ISimulationVariable getXVariable() {
        final DefaultSimulationVariable x = new DefaultSimulationVariable();
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void positionChanged( double dx ) {
                x.setValue( motionBodyState.getPosition() );
            }
        } );
        x.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setPosition( x.getValue() );
            }
        } );
        return x;
    }

    public double getAcceleration() {
        return motionBodyState.getAcceleration();
    }

    public double getVelocity() {
        return motionBodyState.getVelocity();
    }

    public double getPosition() {
        return motionBodyState.getPosition();
    }

    public ISimulationVariable getVVariable() {
        final DefaultSimulationVariable v = new DefaultSimulationVariable();
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void velocityChanged() {
                v.setValue( motionBodyState.getVelocity() );
            }
        } );
        v.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setVelocity( v.getValue() );
            }
        } );
        return v;
    }

    public ISimulationVariable getAVariable() {
        final DefaultSimulationVariable a = new DefaultSimulationVariable();
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void accelerationChanged() {
                a.setValue( motionBodyState.getAcceleration() );
            }
        } );
        a.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setAcceleration( a.getValue() );
            }
        } );
        return a;
    }

    public PositionDriven getPositionDriven() {
        return motionBodySeries.getPositionDriven();
    }

    public ITimeSeries getXTimeSeries() {
        return motionBodySeries.getXTimeSeries();
    }

    public void setPositionDriven() {
        motionBodySeries.setPositionDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        motionBodySeries.setUpdateStrategy( updateStrategy );
    }
}
