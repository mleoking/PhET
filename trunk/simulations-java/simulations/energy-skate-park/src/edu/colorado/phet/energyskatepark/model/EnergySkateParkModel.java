/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;
import edu.colorado.phet.energyskatepark.SkaterCharacter;
import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.model.physics.ParticleStage;
import edu.colorado.phet.energyskatepark.timeseries.OptionalItemSerializableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:16 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
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

    public void clearPaths() {
        history.clear();
    }

    public void clearHeat() {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.clearHeat();
        }
    }

    public void setGravity( double value ) {
        if( this.gravity != value ) {
            this.gravity = value;
            for( int i = 0; i < listeners.size(); i++ ) {
                EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
                energyModelListener.gravityChanged();
            }
            for( int i = 0; i < bodies.size(); i++ ) {
                Body body = (Body)bodies.get( i );
                body.setGravityState( getGravity(), getZeroPointPotentialY() );
            }
            updateFloorState();
        }
    }

    public void removeEnergyModelListener( EnergyModelListener energyModelListener ) {
        listeners.remove( energyModelListener );
    }

    public boolean isSplineUserControlled() {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline splineSurface = (EnergySkateParkSpline)splines.get( i );
            if( splineSurface.isUserControlled() ) {
                return true;
            }
        }
        return false;
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
        Body body = getBody( i );
        body.removeListener( energyListener );
        bodies.remove( i );
        notifyBodiesChanged();
    }

    public void updateFloorState() {
        int desiredNumFloors = Math.abs( getGravity() ) > 0 ? 1 : 0;
        if( desiredNumFloors == 1 ) {
            floor = new Floor( this );
        }
        else {
            floor = null;
        }
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

//            , new PersistenceUtil.CopyObjectReplacementStrategy() {
//                public Object replaceObject( Object obj ) {
//                    if( obj instanceof Point2D.Double && !( obj instanceof SPoint2D.Double ) ) {
//                        Point2D.Double pt = (Point2D.Double)obj;
//                        return new SPoint2D.Double( pt.x, pt.y );
//                    }
//                    else {
//                        return obj;
//                    }
//                }
//            } );
        }
        catch( PersistenceUtil.CopyFailedException e ) {
            e.printStackTrace();
            return this;
        }
    }

    public void setState( EnergySkateParkModel model ) {
        model = model.copyState();

        this.bodies = model.bodies;
        this.splines = model.splines;
        this.floor = model.floor;
        this.history = model.history;
        this.time = model.time;
        this.maxNumHistoryPoints = model.maxNumHistoryPoints;
        this.gravity = model.gravity;
        notifyBodyEnergyChanged();
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
        if( recordPath && getNumBodies() > 0 && timeSinceLastHistory() > 0.1 ) {
            history.add( new HistoryPoint( getTime(), getBody( 0 ) ) );
        }
        if( history.size() > maxNumHistoryPoints ) {
            history.remove( 0 );
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.preStep( dt );
        }
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( dt );
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.stepFinished();
        }
    }

    public ArrayList getAllSplines() {
        ArrayList list = new ArrayList();
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline splineSurface = (EnergySkateParkSpline)splines.get( i );
            list.add( splineSurface );
        }
        return list;
    }

    public EnergySkateParkSpline getSpline( int i ) {
        return (EnergySkateParkSpline)splines.get( i );
    }

    public void addSplineSurface( EnergySkateParkSpline energySkateParkSpline ) {
        splines.add( energySkateParkSpline );
        notifySplinesChanged();
    }

    private void notifySplinesChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.splinesChanged();
        }
    }

    public void addBody( Body body ) {
        body.addListener( energyListener );
        bodies.add( body );
        if( bodies.size() == 1 ) {//The zero point potential now occurs at the center of mass of the skater.
            zeroPointPotentialY = 0;
            initZeroPointPotentialY = zeroPointPotentialY;
        }
        notifyBodiesChanged();
    }

    private void notifyBodiesChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.bodiesChanged();
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
        notifyBodiesSplineRemoved( splineSurface );
        splines.remove( splineSurface );
        notifySplinesChanged();
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
    }

    public void translateZeroPointPotentialY( double dy ) {
        setZeroPointPotentialY( getZeroPointPotentialY() + dy );
    }

    public void reset() {
        bodies.clear();
        splines.clear();
        history.clear();
        setGravity( G_EARTH );
        zeroPointPotentialY = initZeroPointPotentialY;
        updateFloorState();
    }

    public ParticleStage getParticleStage() {
        return particleStage;
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setDimension( skaterCharacter.getModelWidth(), skaterCharacter.getModelHeight() );
            body.setMass( skaterCharacter.getMass() );
        }
    }

    public static class EnergyModelListenerAdapter implements EnergyModelListener {

        public void preStep( double dt ) {
        }

        public void gravityChanged() {
        }

        public void splinesChanged() {
        }

        public void floorChanged() {
        }

        public void stepFinished() {
        }

        public void bodyEnergyChanged() {
        }

        public void bodiesChanged() {
        }

    }

    public static interface EnergyModelListener {
        void preStep( double dt );

        void gravityChanged();

        void splinesChanged();

        void floorChanged();

        void stepFinished();

        void bodyEnergyChanged();

        void bodiesChanged();
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
