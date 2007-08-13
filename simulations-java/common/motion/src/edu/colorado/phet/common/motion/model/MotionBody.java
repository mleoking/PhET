package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.rotation.model.DefaultTemporalVariable;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 6:19:17 PM
 */
public class MotionBody implements IUpdateStrategy {

    private DefaultTemporalVariable x = new DefaultTemporalVariable( );
    private DefaultTemporalVariable v = new DefaultTemporalVariable( );
    private DefaultTemporalVariable a = new DefaultTemporalVariable( );
    private MotionBodySeries motionBodySeries = new MotionBodySeries(x, v, a);
    private MotionBodyState motionBodyState = new MotionBodyState(x, v, a);//current state


    public MotionBody() {
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void positionChanged( double dx ) {
                x.setValue( motionBodyState.getPosition() );
            }
        } );
        x.addListener( new IVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setPosition( x.getValue() );
            }
        } );

        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void velocityChanged() {
                v.setValue( motionBodyState.getVelocity() );
            }
        } );
        v.addListener( new IVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setVelocity( v.getValue() );
            }
        } );

        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void accelerationChanged() {
                a.setValue( motionBodyState.getAcceleration() );
            }
        } );
        a.addListener( new IVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setAcceleration( a.getValue() );
            }
        } );
    }

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
        updateStateFromSeries();
    }

    public void updateStateFromSeries() {
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValue() );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValue() );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValue() );
    }

    public void clear() {
        motionBodySeries.clear();
    }

    public ITemporalVariable getXVariable() {
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

    public void setPosition( double position ) {
        motionBodyState.setPosition( position );
    }

    public ITemporalVariable getVVariable() {
        return v;
    }

    public ITemporalVariable getAVariable() {
        return a;
    }

    public PositionDriven getPositionDriven() {
        return motionBodySeries.getPositionDriven();
    }

    public ITemporalVariable getPositionVariable() {
        return x;
    }

    public void setPositionDriven() {
        motionBodySeries.setPositionDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        motionBodySeries.setUpdateStrategy( updateStrategy );
    }

    public UpdateStrategy getUpdateStrategy() {
        return motionBodySeries.getUpdateStrategy();
    }

    public ITemporalVariable getVelocityVariable() {
        return v;
    }

    public ITemporalVariable getAccelerationVariable() {
        return a;
    }

    public VelocityDriven getVelocityDriven() {
        return motionBodySeries.getVelocityDriven();
    }

    public AccelerationDriven getAccelDriven() {
        return motionBodySeries.getAccelDriven();
    }

    public boolean isPositionDriven() {
        return motionBodySeries.isPositionDriven();
    }

    public boolean isVelocityDriven() {
        return motionBodySeries.isVelocityDriven();
    }

    public boolean isAccelerationDriven() {
        return motionBodySeries.isAccelerationDriven();
    }

    public void reset() {
        clear();
        getXVariable().setValue( 0.0 );
        getVVariable().setValue( 0.0 );
        getAVariable().setValue( 0.0 );
    }
}
