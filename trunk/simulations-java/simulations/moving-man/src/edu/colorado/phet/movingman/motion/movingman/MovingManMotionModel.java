package edu.colorado.phet.movingman.motion.movingman;

import bsh.EvalError;
import bsh.Interpreter;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.MovingManResources;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:37:57 PM
 */
public class MovingManMotionModel extends MotionModel implements UpdateableObject, IMovingManModel, IMotionBody, UpdateStrategy.DefaultUpdateStrategy.Listener {
    /*

     X.long-label=
     X.short-label=
     X.color=

     x.isEqualTo(TemporalVariable.clamp(v.integrate().plus(x.lastValue(), 0, 12345));

     v.isEqualTo(x.derivative()).and(a.integrate().plus(v.lastValue()));

     a.isEqualTo(v.derivative());

     Graph g = new Graph(new TemporalVariable[]{x, v, a});

     g.setAdjustableVariable(x);

     mu.isEqualTo(TemporalVariable.if(v.isZero()), MU_STATIC).and(TemporalVariable.if(v.isNonZero(), MU_DYNAMIC);




    */

    private ITemporalVariable x;// = new DefaultTemporalVariable();
    private ITemporalVariable v;// = new DefaultTemporalVariable();
    private ITemporalVariable a;// = new DefaultTemporalVariable();

    private ControlGraphSeries xSeries;
    private ControlGraphSeries vSeries;
    private ControlGraphSeries aSeries;

    public static final int MAX_T = 20;

    private double min = -10;
    private double max = 10;


    private UpdateStrategy.PositionDriven positionDriven = new UpdateStrategy.PositionDriven( min, max );
    private UpdateStrategy.VelocityDriven velocityDriven = new UpdateStrategy.VelocityDriven( min, max );
    private UpdateStrategy.AccelerationDriven accelDriven = new UpdateStrategy.AccelerationDriven( min, max );

//    private UpdateStrategy positionDriven = new UpdateStrategy() {
//        public void update( IMotionBody motionBody, double dt, double time ) {
//            x.stepInTime( dt );
//        }
//    };
//    private UpdateStrategy velocityDriven = new UpdateStrategy() {
//        public void update( IMotionBody motionBody, double dt, double time ) {
//            v.stepInTime( dt );
//        }
//    };
//    private UpdateStrategy accelDriven = new UpdateStrategy() {
//        public void update( IMotionBody motionBody, double dt, double time ) {
//            a.stepInTime( dt );
//        }
//    };

    private UpdateStrategy updateStrategy = positionDriven;
    private ArrayList listeners = new ArrayList();
    private boolean boundaryOpen = false;
    private ArrayList crashTimes = new ArrayList();
    private double lastPlaybackTime;

    public MovingManMotionModel( ConstantDtClock clock ) {
        super( clock, new HeuristicPrunedTimeSeries.Factory( MAX_T ) );
        setMaxAllowedRecordTime( MAX_T );

        positionDriven.addListener( this );
        velocityDriven.addListener( this );
        accelDriven.addListener( this );

//        x = new DefaultTemporalVariable();
//        v = x.getDerivative();
//        a = v.getDerivative();
        x = new DefaultTemporalVariable();
        v = new DefaultTemporalVariable();
        a = new DefaultTemporalVariable();

        addTemporalVariables( new ITemporalVariable[]{x, v, a} );

        xSeries = new ControlGraphSeries( MovingManResources.getString( "variables.position" ), Color.blue, MovingManResources.getString( "variables.position.abbreviation" ), MovingManResources.getString( "units.meters.abbreviation" ), new BasicStroke( 2 ), null, x, true );
        vSeries = new ControlGraphSeries( MovingManResources.getString( "variables.velocity" ), Color.red, MovingManResources.getString( "variables.velocity.abbreviation" ), MovingManResources.getString( "units.velocity.abbreviation" ), new BasicStroke( 2 ), null, v, true );
        aSeries = new ControlGraphSeries( MovingManResources.getString( "variables.acceleration" ), Color.green, MovingManResources.getString( "variables.acceleration.abbreviation" ), MovingManResources.getString( "units.acceleration.abbreviation" ), new BasicStroke( 2 ), null, a, true );
//        v=x.getDerivative();
    }

    public void stepInTime( double dt ) {
        lastPlaybackTime = Double.NaN;
        super.stepInTime( dt );
//        x.addValue( x.getValue(), getTime() );
//        v.addValue( v.getValue(), getTime() );
        updateStrategy.update( this, dt, super.getTime() );
    }

    public void clear() {
        super.clear();
        crashTimes.clear();
        getTimeSeriesModel().setRecordMode();
        getTimeSeriesModel().getTimeModelClock().pause();
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        if ( containsCrash( lastPlaybackTime, time ) ) {
            System.out.println( "t0=" + lastPlaybackTime + ", t1=" + time + ", crashTimes=" + crashTimes );
            notifyCrashedMin( getVelocity() );
        }
        lastPlaybackTime = time;
    }

    private boolean containsCrash( double t0, double t1 ) {
        if ( Double.isNaN( t0 ) || Double.isNaN( t1 ) ) {
            return false;

        }
        for ( int i = 0; i < crashTimes.size(); i++ ) {
            double t = ( (Double) crashTimes.get( i ) ).doubleValue();
            if ( t >= t0 && t < t1 ) {
                return true;
            }
        }
        return false;
    }

    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
    }

    public void setPosition( double x ) {
        double origX = getPosition();
        final double newX = MathUtil.clamp( min, x, max );
        this.x.setValue( newX );
        double dt = getTimeSeriesModel().getTimeModelClock().getDt();
        if ( origX > min && newX == min ) {
            crashedMin( ( newX - origX ) / dt );
        }
        else if ( origX < max && newX == max ) {
            crashedMax( ( newX - origX ) / dt );
        }
    }

    public ITemporalVariable getXVariable() {
        return x;
    }

    public ITemporalVariable getVVariable() {
        return v;
    }

    public double getVelocity() {
        return v.getValue();
    }

    public double getAcceleration() {
        return a.getValue();
    }

    public double getPosition() {
        return x.getValue();
    }

    public void addAccelerationData( double acceleration, double time ) {
        a.addValue( acceleration, time );
    }

    public void addVelocityData( double v, double time ) {
        this.v.addValue( v, time );
    }

    public void addPositionData( double v, double time ) {
        this.x.addValue( v, time );
    }

    public void addPositionData( TimeData data ) {
        addPositionData( data.getValue(), data.getTime() );
    }

    public void addVelocityData( TimeData data ) {
        addVelocityData( data.getValue(), data.getTime() );
    }

    public void addAccelerationData( TimeData data ) {
        addAccelerationData( data.getValue(), data.getTime() );
    }

    public int getAccelerationSampleCount() {
        return a.getSampleCount();
    }

    public TimeData[] getRecentVelocityTimeSeries( int i ) {
        return v.getRecentSeries( i );
    }

    public int getPositionSampleCount() {
        return x.getSampleCount();
    }

    public int getVelocitySampleCount() {
        return v.getSampleCount();
    }

    public TimeData[] getRecentPositionTimeSeries( int i ) {
        return x.getRecentSeries( i );
    }

    public void setAcceleration( double value ) {
        a.setValue( value );
    }

    public void setVelocityDriven() {
        setUpdateStrategy( velocityDriven );
    }

    public void setVelocity( double v ) {
        this.v.setValue( v );
    }

    public void startRecording() {
        getTimeSeriesModel().startRecording();
    }

    public ControlGraphSeries getXSeries() {
        return xSeries;
    }

    public ControlGraphSeries getVSeries() {
        return vSeries;
    }

    public ControlGraphSeries getASeries() {
        return aSeries;
    }

    public UpdateStrategy getPositionDriven() {
        return positionDriven;
    }

    public UpdateStrategy getVelocityDriven() {
        return velocityDriven;
    }

    public UpdateStrategy getAccelDriven() {
        return accelDriven;
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).updateStrategyChanged();
        }
    }

    public void crashedMin( double velocity ) {
        System.out.println( "MovingManMotionModel.crashedMin, v=" + velocity );
        crashTimes.add( new Double( getTime() ) );
        notifyCrashedMin( velocity );
    }

    private void notifyCrashedMin( double velocity ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).crashedMin( velocity );
        }
    }

    public void crashedMax( double velocity ) {
        System.out.println( "MovingManMotionModel.crashedMax, v=" + velocity );
        crashTimes.add( new Double( getTime() ) );
        notifyCrashedMax( velocity );

    }

    private void notifyCrashedMax( double velocity ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).crashedMax( velocity );
        }
    }

    public ITemporalVariable getAVariable() {
        return a;
    }

    public boolean isBoundaryOpen() {
        return boundaryOpen;
    }

    public void unpause() {
        getClock().start();
    }

    public void setBoundaryOpen( boolean boundaryOpen ) {
        this.boundaryOpen = boundaryOpen;
        min = boundaryOpen ? Double.NEGATIVE_INFINITY : -10;
        max = boundaryOpen ? Double.POSITIVE_INFINITY : +10;
//        positionDriven.setMin( min );
//        positionDriven.setMax( max );
//        velocityDriven.setMin( min );
//        velocityDriven.setMax( max );
//        accelDriven.setMin( min );
//        accelDriven.setMax( max );
        notifyBoundaryChanged();
    }

    private void notifyBoundaryChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).boundaryChanged();
        }
    }

    public void setExpressionUpdate( String text ) {
        setUpdateStrategy( new MovingManExpressionUpdate( text ) );
    }

    public boolean isPositionDriven() {
        return updateStrategy == positionDriven;
    }

    public boolean isVelocityDriven() {
        return updateStrategy == velocityDriven;
    }

    public boolean isAccelerationDriven() {
        return updateStrategy == accelDriven;
    }

    public static interface Listener {
        void crashedMin( double velocity );

        void crashedMax( double velocity );

        void boundaryChanged();

        void updateStrategyChanged();
    }

    public static class Adapter implements Listener {
        public void crashedMin( double velocity ) {
        }

        public void crashedMax( double velocity ) {
        }

        public void boundaryChanged() {
        }

        public void updateStrategyChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private class MovingManExpressionUpdate extends UpdateStrategy.PositionDriven {
        private String text;
        private Interpreter interpreter = new Interpreter();

        public MovingManExpressionUpdate( String text ) {
            this.text = text;
        }

        public TimeData getNewX( IMotionBody motionBody, double dt, double time ) {
            double x = evaluate( time, text, interpreter );
            return new TimeData( x, time );
        }
    }

    public static double evaluate( double time, String expression, Interpreter interpreter ) {
        String timeString = "(" + time + ")";

//            String equation = expression.replaceAll( "t", timeString );
        String equation = expression.replaceAll( "cos", "Math.cos" );
        equation = equation.replaceAll( "sin", "Math.sin" );
        equation = equation.replaceAll( "pi", "Math.PI" );
        equation = equation.replaceAll( "log", "Math.log" );
        equation = equation.replaceAll( "pow", "Math.pow" );

        double x = 0;
        try {
            Object value = interpreter.eval( "t=" + timeString + "; y=" + equation );
            x = ( (Number) value ).doubleValue();
        }
        catch( EvalError evalError ) {
            evalError.printStackTrace();
        }
        return x;
    }
}
