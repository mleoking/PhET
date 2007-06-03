/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;
import edu.colorado.phet.energyskatepark.SkaterCharacter;
import edu.colorado.phet.energyskatepark.SkaterCharacterSet;
import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.model.physics.ParticleStage;
import edu.colorado.phet.energyskatepark.util.OptionalItemSerializableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:16 AM
 */

public class EnergySkateParkModel implements Serializable {
    private double time = 0;
    private ArrayList history = new ArrayList();
    private ArrayList bodies = new ArrayList();
    private ArrayList splines = new ArrayList();

    private Floor floor;

    private double gravity = G_EARTH;
    private double zeroPointPotentialY;
    private List listeners = new OptionalItemSerializableList();
    private boolean recordPath = false;
    private double initZeroPointPotentialY;

    private int maxNumHistoryPoints = 100;

    private ParticleStage particleStage;
    public static final double G_SPACE = 0.0;
    public static final double G_EARTH = -9.81;
    public static final double G_MOON = -1.62;
    public static final double G_JUPITER = -25.95;
    public static final double SPLINE_THICKNESS = 0.25f;//meters
    private Body.Listener energyListener = new EnergyChangeNotifyingListener();
    private SkaterCharacter skaterCharacter = SkaterCharacterSet.getDefaultCharacter();

    public EnergySkateParkModel( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        this.initZeroPointPotentialY = zeroPointPotentialY;
        this.particleStage = new EnergySkateParkSplineListAdapter( this );//todo copy, clone this
        updateFloorState();
    }

    private void notifyBodyEnergyChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.bodyEnergyChanged();
        }
    }

    public int getNumSplines() {
        return splines.size();
    }

    public double getTime() {
        return time;
    }

    public void setRecordPath( boolean selected ) {
        this.recordPath = selected;
    }

    public boolean isRecordPath() {
        return recordPath;
    }

    public boolean containsBody( Body body ) {
        return bodies.contains( body );
    }

    public void clearHistory() {
        history.clear();
        notifyHistoryChanged();
    }

    public void clearHeat() {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.clearHeat();
        }
    }

    public void setGravity( double value ) {
        if( value == -0.0 ) {
            value = 0;//workaround since -0 and +0 are getting hashcoded differently
        }
        if( this.gravity != value ) {
            this.gravity = value;
            notifyGravityChanged();
            for( int i = 0; i < bodies.size(); i++ ) {
                Body body = (Body)bodies.get( i );
                body.setGravityState( getGravity(), getZeroPointPotentialY() );
            }
            updateFloorState();
        }
    }

    private void notifyGravityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.gravityChanged();
        }
    }

    public void removeEnergyModelListener( EnergyModelListener energyModelListener ) {
        listeners.remove( energyModelListener );
    }

    public void removeAllSplineSurfaces() {
        while( splines.size() > 0 ) {
            removeSplineSurface( getSpline( 0 ) );
        }
    }

    public void removeAllBodies() {
        while( bodies.size() > 0 ) {
            removeBody( 0 );
        }
    }

    public void removeBody( int i ) {
        getBody( i ).removeListener( energyListener );
        bodies.remove( i );
        notifyBodyCountChanged();
    }

    public void updateFloorState() {
        int desiredNumFloors = Math.abs( getGravity() ) > 0 ? 1 : 0;
        if( desiredNumFloors == 1 ) {
            floor = new Floor();
        }
        else {
            floor = null;
        }
        notifyFloorChanged();
    }

    private void notifyFloorChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.floorChanged();
        }
    }

    public Floor getFloor() {
        return floor;
    }

    public EnergySkateParkModel copyState() {
        try {
            return (EnergySkateParkModel)PersistenceUtil.copy( this );
        }
        catch( PersistenceUtil.CopyFailedException e ) {
            e.printStackTrace();
            return this;
        }
    }

    public void setState( EnergySkateParkModel model ) {
        model = model.copyState();

        this.time = model.time;
        this.maxNumHistoryPoints = model.maxNumHistoryPoints;
        setSkaterCharacter( model.getSkaterCharacter() );

        this.bodies = model.bodies;
        notifyBodiesSynced();

        this.splines = model.splines;
        notifySplinesSynced();

        if( this.floor.getY() != model.floor.getY() ) {
            this.floor.setY( model.floor.getY() );
            notifyFloorChanged();
        }

        this.history = model.history;
        notifyHistoryChanged();

        if( this.gravity != model.gravity ) {
            this.gravity = model.gravity;
            notifyGravityChanged();
        }

        if( this.zeroPointPotentialY != model.zeroPointPotentialY ) {
            this.zeroPointPotentialY = model.zeroPointPotentialY;
            notifyZeroPointPotentialYChanged();
        }

        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.stateSet();
        }
    }

    private void notifyBodiesSynced() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.bodiesSynced();
        }
    }

    private void notifySplinesSynced() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.splinesSynced();
        }
    }

    private void notifyHistoryChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (EnergyModelListener)listeners.get( i ) ).historyChanged();
        }
    }

    public EnergySkateParkSpline getEnergySkateParkSpline( ParametricFunction2D spline ) {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = (EnergySkateParkSpline)splines.get( i );
            if( energySkateParkSpline.getParametricFunction2D() == spline ) {
                return energySkateParkSpline;
            }
        }
        return null;
    }

    public double timeSinceLastHistory() {
        if( history.size() == 0 ) {
            return time;
        }
        return time - historyPointAt( history.size() - 1 ).getTime();
    }

    public void stepInTime( double dt ) {
        time += dt;
        updateHistory();
        notifyPreStep( dt );
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( dt );
        }
        notifyPostStep();
    }

    private void notifyPostStep() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.stepFinished();
        }
    }

    private void notifyPreStep( double dt ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.preStep( dt );
        }
    }

    private void updateHistory() {
        boolean historyChanged = false;
        if( recordPath && getNumBodies() > 0 && timeSinceLastHistory() > 0.1 ) {
            history.add( new HistoryPoint( getTime(), getBody( 0 ) ) );
            historyChanged = true;
        }
        if( history.size() > maxNumHistoryPoints ) {
            history.remove( 0 );
            historyChanged = true;
        }
        if( historyChanged ) {
            notifyHistoryChanged();
        }
    }

    public EnergySkateParkSpline getSpline( int i ) {
        return (EnergySkateParkSpline)splines.get( i );
    }

    public void addSplineSurface( EnergySkateParkSpline energySkateParkSpline ) {
        splines.add( energySkateParkSpline );
        notifySplineCountChanged();
    }

    private void notifySplineCountChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.splineCountChanged();
        }
    }

    public void addBody( Body body ) {

        body.addListener( energyListener );
        bodies.add( body );
//        System.out.println( "EnergySkateParkModel.addBody, bodies="+bodies.size() );
        if( bodies.size() == 1 ) {//The zero point potential now occurs at the center of mass of the skater.
            zeroPointPotentialY = 0;
            initZeroPointPotentialY = zeroPointPotentialY;
        }
        notifyBodyCountChanged();
    }

    private void notifyBodyCountChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.bodyCountChanged();
        }
    }

    public int getNumBodies() {
        return bodies.size();
    }

    public Body getBody( int i ) {
        return (Body)bodies.get( i );
    }

    public double getGravity() {
        return gravity;
    }

    public void removeSplineSurface( EnergySkateParkSpline splineSurface ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( splineSurface ) ) {
                body.setFreeFallMode();
            }
        }
        notifyBodiesSplineRemoved( splineSurface );//todo: this looks like it is duplicating the above work
        splines.remove( splineSurface );
        notifySplineCountChanged();
    }

    private void notifyBodiesSplineRemoved( EnergySkateParkSpline spline ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.splineRemoved( spline );
        }
    }

    public double getZeroPointPotentialY() {
        return zeroPointPotentialY;
    }

    public void setZeroPointPotentialY( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setGravityState( getGravity(), zeroPointPotentialY );
        }
        notifyZeroPointPotentialYChanged();
    }

    private void notifyZeroPointPotentialYChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.zeroPointPotentialYChanged();
        }
    }

    public void translateZeroPointPotentialY( double dy ) {
        setZeroPointPotentialY( getZeroPointPotentialY() + dy );
    }

    public void reset() {
        removeAllBodies();
        removeAllSplineSurfaces();
        clearHistory();
        setGravity( G_EARTH );
        setZeroPointPotentialY( initZeroPointPotentialY );
        updateFloorState();
        setSkaterCharacter( SkaterCharacterSet.getDefaultCharacter() );
    }

    public ParticleStage getParticleStage() {
        return particleStage;
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        if( !this.skaterCharacter.equals( skaterCharacter ) ) {
            this.skaterCharacter = skaterCharacter;
            for( int i = 0; i < bodies.size(); i++ ) {
                Body body = (Body)bodies.get( i );
                body.setSkaterCharacter( skaterCharacter );
            }
            notifySkaterCharacterChanged();
        }
    }

    private void notifySkaterCharacterChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.skaterCharacterChanged();
        }
    }

    public SkaterCharacter getSkaterCharacter() {
        return skaterCharacter;
    }

    public static class EnergyModelListenerAdapter implements EnergyModelListener {

        public void preStep( double dt ) {
        }

        public void gravityChanged() {
        }

        public void splineCountChanged() {
        }

        public void floorChanged() {
        }

        public void stepFinished() {
        }

        public void bodyEnergyChanged() {
        }

        public void bodyCountChanged() {
        }

        public void historyChanged() {
        }

        public void zeroPointPotentialYChanged() {
        }

        public void splinesSynced() {
        }

        public void bodiesSynced() {
        }

        public void stateSet() {
        }

        public void skaterCharacterChanged() {
        }
    }

    public static interface EnergyModelListener {
        void preStep( double dt );

        void gravityChanged();

        void splineCountChanged();

        void floorChanged();

        void stepFinished();

        void bodyEnergyChanged();

        void bodyCountChanged();

        void historyChanged();

        void zeroPointPotentialYChanged();

        void splinesSynced();

        void bodiesSynced();

        void stateSet();

        void skaterCharacterChanged();
    }

    public void addEnergyModelListener( EnergyModelListener listener ) {
//        System.out.println( "listeners.size() = " + listeners.size() );
        listeners.add( listener );
    }

    public int getNumHistoryPoints() {
        return history.size();
    }

    public HistoryPoint historyPointAt( int i ) {
        return (HistoryPoint)history.get( i );
    }

    private class EnergyChangeNotifyingListener extends Body.ListenerAdapter implements Serializable {
        public void energyChanged() {
            notifyBodyEnergyChanged();
        }
    }
}
