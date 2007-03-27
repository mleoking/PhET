/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.test.phys1d.ParametricFunction2D;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:16 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EnergySkateParkModel {
    private double time = 0;
    private ArrayList history = new ArrayList();
    private ArrayList bodies = new ArrayList();
    private ArrayList splines = new ArrayList();

    private Floor floor;

    private double gravity = G_EARTH;
    private double zeroPointPotentialY;
    private ArrayList listeners = new ArrayList();
    private boolean recordPath = false;
    private double initZeroPointPotentialY;

    public static final double G_SPACE = 0.0;
    public static final double G_EARTH = -9.81;
    public static final double G_MOON = -1.62;
    public static final double G_JUPITER = -25.95;
    private int maxNumHistoryPoints = 100;
    private static boolean thermalLanding = true;

    public EnergySkateParkModel( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        this.initZeroPointPotentialY = zeroPointPotentialY;
        updateFloorState();
    }

    public int numSplineSurfaces() {
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
            removeSplineSurface( splineSurfaceAt( 0 ) );
        }
    }

    public void removeAllBodies() {
        while( bodies.size() > 0 ) {
            removeBody( 0 );
        }
    }

    public void removeBody( int i ) {
        bodies.remove( i );
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

    public static boolean isThermalLanding() {
        return thermalLanding;
    }

    public static void setThermalLanding( boolean selected ) {
        thermalLanding = selected;
    }

    public EnergySkateParkModel copyState() {
        EnergySkateParkModel copy = new EnergySkateParkModel( zeroPointPotentialY );
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            copy.bodies.add( body.copyState() );
        }
        copy.floor = this.floor == null ? null : this.floor.copyState();
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline surface = splineSurfaceAt( i );
            copy.splines.add( surface.copy() );
        }
        copy.history = new ArrayList( history );
        copy.time = time;
        copy.gravity = gravity;
        copy.maxNumHistoryPoints = maxNumHistoryPoints;
        return copy;
    }

    public void setState( EnergySkateParkModel model ) {
        bodies.clear();
        splines.clear();
        for( int i = 0; i < model.bodies.size(); i++ ) {
            bodies.add( model.bodyAt( i ).copyState() );
        }
        for( int i = 0; i < model.splines.size(); i++ ) {
            splines.add( model.splineSurfaceAt( i ).copy() );
        }
        this.floor=model.floor==null?null:model.floor.copyState();
        this.history.clear();
        this.history.addAll( model.history );
        this.time = model.time;
        this.maxNumHistoryPoints = model.maxNumHistoryPoints;
        setGravity( model.gravity );
        //todo: some model objects are not getting copied over correctly, body's spline strategy could refer to different splines
//        for( int i = 0; i < bodies.size(); i++ ) {
//            Body body = (Body)bodies.get( i );
//            body.setGravityState(gravity,zeroPointPotentialY);
//            if( body.isSplineMode() ) {
//                ParametricFunction2D spline = body.getSpline();
//                EnergySkateParkSpline esps = getEnergySkateParkSpline( spline );
//                if( !containsSpline( spline ) ) {
////                    new RuntimeException( "Skater is on a track that the model doesn't currently know about" ).printStackTrace();
////                    EnergySkateParkSpline bestMatch = getBestSplineMatch( esps );
////                    if( bestMatch == null ) {
////                        System.out.println( "\"Skater is on a track that the model doesn't currently know about\" = " + "Skater is on a track that the model doesn't currently know about" );
////                    }
////                    else {
//                    body.setSplineMode( esps,body.isTop() );//todo: determine side of spline
////                    }
//                }
//            }
//        }
        updateFloorState();
    }

    private EnergySkateParkSpline getEnergySkateParkSpline( ParametricFunction2D spline ) {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = (EnergySkateParkSpline)splines.get( i );
            if( energySkateParkSpline.getParametricFunction2D() == spline ) {
                return energySkateParkSpline;
            }
        }
        return null;
    }

    private boolean containsSpline( ParametricFunction2D spline ) {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = (EnergySkateParkSpline)splines.get( i );
            if( energySkateParkSpline.getParametricFunction2D() == spline ) {
                return true;
            }
        }
        return false;
    }

    public double timeSinceLastHistory() {
        if( history.size() == 0 ) {
            return time;
        }
        return time - historyPointAt( history.size() - 1 ).getTime();
    }

    public void stepInTime( double dt ) {
        time += dt;
        if( recordPath && numBodies() > 0 && timeSinceLastHistory() > 0.1 ) {
            history.add( new HistoryPoint( this, bodyAt( 0 ) ) );
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

    public EnergySkateParkSpline splineSurfaceAt( int i ) {
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
        bodies.add( body );
        if( bodies.size() == 1 ) {//The zero point potential now occurs at the center of mass of the skater.
            zeroPointPotentialY = 0;
            initZeroPointPotentialY = zeroPointPotentialY;
        }
    }

    public int numBodies() {
        return bodies.size();
    }

    public Body bodyAt( int i ) {
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
    }

    public static interface EnergyModelListener {
        void preStep( double dt );

        void gravityChanged();

        void splinesChanged();

        void floorChanged();

        void stepFinished();
    }

    public void addEnergyModelListener( EnergyModelListener listener ) {
        listeners.add( listener );
    }

    public int numHistoryPoints() {
        return history.size();
    }

    public HistoryPoint historyPointAt( int i ) {
        return (HistoryPoint)history.get( i );
    }
}
