package edu.colorado.phet.movingman.motion.ramps;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 5, 2007 at 3:32:30 AM
 */
public class Force1DMotionModel extends MotionModel implements UpdateableObject, Force1DNode.IForceNodeModel
//        extends MovingManMotionModel
{

    //handled in parent hierarchy: time, x,v,a
    private Force1DObjectConfig currentObject;
    private Force1DObjectConfig[] availableObjects;

    private Force1DModelObject object = new Force1DModelObject();

    private UpdateStrategy appliedForceStrategy = new UpdateStrategy() {
        public void update( IMotionBody motionBody, double dt, double time ) {
            Force1DMotionModel model = (Force1DMotionModel) motionBody;//Force1DMotionModel.this
//            model.getAccelDriven().update( motionBody, dt, time );
        }
    };

    public Force1DMotionModel( ConstantDtClock clock ) {
        super( clock, new TimeSeriesFactory.Default() );
//        super( clock );
//        addTemporalVariables( getForce1DVars() );
//        appliedForce.getXVariable().addListener( new IVariable.Listener() {
//            public void valueChanged() {
////                System.out.println( "appliedForceValue = " + appliedForce.getX() );
////                appliedForce.setValue( appliedForceValue );
//                netForce.setXValue( appliedForce.getXValue() + frictionForce.getXValue() + wallForce.getXValue() );
//                getAVariable().setValue( netForce.getXValue() / mass.getValue() );
//            }
//        } );
        availableObjects = new Force1DObjectConfig[]{
                new Force1DObjectConfig( "/simulations/moving-man/data/moving-man/images/cabinet.gif", SimStrings.get( "Force1DModule.fileCabinet" ), 0.8, 200, 0.3, 0.2 ),
                new Force1DObjectConfig( "/simulations/moving-man/data/moving-man/images/fridge.gif", SimStrings.get( "Force1DModule.refrigerator" ), 0.35, 400, 0.7, 0.5 ),
                new Force1DObjectConfig( "/simulations/moving-man/data/moving-man/images/phetbook.gif", SimStrings.get( "Force1DModule.textbook" ), 0.8, 10, 0.3, 0.25 ),
                new Force1DObjectConfig( "/simulations/moving-man/data/moving-man/images/crate.gif", SimStrings.get( "Force1DModule.crate" ), 0.8, 300, 0.2, 0.2 ),
                new Force1DObjectConfig( "/simulations/moving-man/data/moving-man/images/ollie.gif", SimStrings.get( "Force1DModule.sleepyDog" ), 0.5, 25, 0.1, 0.1 ),
        };
        currentObject = availableObjects[0];
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        object.stepInTime( dt, getTime() );
//        defaultUpdate( getForce1DVars() );
    }

    public UpdateStrategy getAppliedForceStrategy() {
        return appliedForceStrategy;
    }

    public void setCurrentObject( Force1DObjectConfig currentObject ) {
        if ( this.currentObject != currentObject ) {
            this.currentObject = currentObject;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener o = (Listener) listeners.get( i );
                o.objectChanged();
            }
        }
    }

    ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
//        super.addListener( listener );
        listeners.add( listener );
    }

    public void setAppliedForce( double v ) {
        object.setAppliedForce( v );
    }

    public ITemporalVariable getXVariable() {
        return object.getXVariable();
    }

    public ITemporalVariable getAppliedForce() {
        return object.getAppliedForce();
    }

    public Force1DModelObject getObject() {
        return object;
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
    }

    public void startRecording() {
        getTimeSeriesModel().startRecording();
    }

    public static interface Listener extends MovingManMotionModel.Listener {
        void objectChanged();
    }

    public static class Adapter extends MovingManMotionModel.Adapter implements Listener {
        public void objectChanged() {
        }
    }

    public Force1DObjectConfig getCurrentObject() {
        return currentObject;
    }

    public double getPosition() {
        return object.getXVariable().getValue();
    }

    public Force1DObjectConfig[] getAvailableObjects() {
        return availableObjects;
    }

}
