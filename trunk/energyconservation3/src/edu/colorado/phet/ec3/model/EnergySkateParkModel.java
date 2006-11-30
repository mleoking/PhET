/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.ec3.FloorSpline;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;

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
    private ArrayList floors = new ArrayList();
    private ArrayList splineSurfaces = new ArrayList();
    private double gravity = G_EARTH;
    private double zeroPointPotentialY;
    private ArrayList listeners = new ArrayList();
    private boolean recordPath = false;
    private double initZeroPointPotentialY;
    private PotentialEnergyMetric potentialEnergyMetric;

    public static final double G_SPACE = 0.0;
    public static final double G_EARTH = -9.81;
    public static final double G_MOON = -1.62;
    public static final double G_JUPITER = -25.95;

    public EnergySkateParkModel( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        this.initZeroPointPotentialY = zeroPointPotentialY;
        potentialEnergyMetric = new PotentialEnergyMetric() {
            public double getPotentialEnergy( Body body ) {
                double h = EnergySkateParkModel.this.zeroPointPotentialY - body.getCenterOfMass().getY();
                return body.getMass() * gravity * h;
            }

            public double getGravity() {
                return gravity;
            }

            public PotentialEnergyMetric copy() {
                return this;
            }
        };
        updateFloorState();
    }

    public int numSplineSurfaces() {
        return splineSurfaces.size();
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
            updateFloorState();
        }
    }

    public void removeEnergyModelListener( EnergyModelListener energyModelListener ) {
        listeners.remove( energyModelListener );
    }

    public boolean isSplineUserControlled() {
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            if( splineSurface.isUserControlled() ) {
                return true;
            }
        }
        return false;
    }

    public void splineTranslated( SplineSurface splineGraphic, double dx, double dy ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( splineGraphic ) ) {
                body.translate( dx, dy );
                body.notifyObservers();
            }
        }
    }

    public PotentialEnergyMetric getPotentialEnergyMetric() {
        return potentialEnergyMetric;
    }

    public void removeAllSplineSurfaces() {
        while( splineSurfaces.size() > 0 ) {
            removeSplineSurface( splineSurfaceAt( 0 ) );
        }
    }

    public void removeAllBodies() {
        while( bodies.size() > 0 ) {
            removeBody( 0 );
        }
    }

    private void removeBody( int i ) {
        bodies.remove( i );
    }

    public void updateFloorState() {
        int desiredNumFloors = Math.abs( getGravity() ) > 0 ? 1 : 0;
        while( getNumFloorSplines() > desiredNumFloors ) {
            removeFloorSpline();
        }
        while( getNumFloorSplines() < desiredNumFloors ) {
            addFloorSpline();
        }
        while( getFloorCount() < desiredNumFloors ) {
            addFloor( new Floor( this ) );
        }
        while( getFloorCount() > desiredNumFloors ) {
            removeFloor( 0 );
        }
        if( getFloorCount() == 2 || getNumFloorSplines() == 2 ) {
            System.out.println( "getFloorCount() = " + getFloorCount() );
            System.out.println( "getNumFloorSplines() = " + getNumFloorSplines() );
        }
    }

    private void removeFloors() {
        while( floors.size() > 0 ) {
            removeFloor( 0 );
        }
    }

    private void removeFloor( int i ) {
        floors.remove( i );
    }

    private void removeFloorSpline() {
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            if( splineSurface.getSpline() instanceof FloorSpline ) {
                removeSplineSurface( splineSurface );
                i--;
            }
        }
    }

    public boolean hasFloor() {
        return getFloorCount() > 0;
    }

    public int getFloorCount() {
        return floors.size();
    }

    private int getNumFloorSplines() {
        int sum = 0;
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            if( splineSurface.getSpline() instanceof FloorSpline ) {
                sum++;
            }
        }
        return sum;
    }

    private boolean hasFloorSpline() {
        return getNumFloorSplines() > 0;
    }

    private void addFloorSpline() {
        addSplineSurface( new SplineSurface( new FloorSpline(), false ) );
    }

    static interface Listener {
        public void numBodiesChanged();

        public void numFloorsChanged();

        public void numSplinesChanged();

        public void paramChanged();
    }

    public EnergySkateParkModel copyState() {
        EnergySkateParkModel copy = new EnergySkateParkModel( zeroPointPotentialY );
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            copy.bodies.add( body.copyState() );
        }
        for( int i = 0; i < floors.size(); i++ ) {
            Floor floor = (Floor)floors.get( i );
            copy.floors.add( floor.copyState() );
        }
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface surface = splineSurfaceAt( i );
            copy.splineSurfaces.add( surface.copy() );
        }
        copy.history = new ArrayList( history );
        copy.time = time;
        copy.gravity = gravity;
        return copy;
    }

    public void setState( EnergySkateParkModel model ) {
        bodies.clear();
        floors.clear();
        splineSurfaces.clear();
        for( int i = 0; i < model.bodies.size(); i++ ) {
            bodies.add( model.bodyAt( i ).copyState() );
        }
        for( int i = 0; i < model.splineSurfaces.size(); i++ ) {
            splineSurfaces.add( model.splineSurfaceAt( i ).copy() );
        }
        for( int i = 0; i < model.floors.size(); i++ ) {
            floors.add( model.floorAt( i ).copyState() );
        }
        this.history.clear();
        this.history.addAll( model.history );
        this.time = model.time;
        setGravity( model.gravity );
        //todo: some model objects are not getting copied over correctly, body's spline strategy could refer to different splines
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setPotentialEnergyMetric( getPotentialEnergyMetric() );
            if( body.isSplineMode() ) {
                AbstractSpline spline = body.getSpline();
                if( !containsSpline( spline ) ) {
//                    new RuntimeException( "Skater is on a track that the model doesn't currently know about" ).printStackTrace();
                    AbstractSpline bestMatch = getBestSplineMatch( spline );
                    if( bestMatch == null ) {
                        System.out.println( "\"Skater is on a track that the model doesn't currently know about\" = " + "Skater is on a track that the model doesn't currently know about" );
                    }
                    else {
                        body.stayInSplineModeNewSpline( bestMatch );
                    }
                }
            }
        }
        updateFloorState();
    }

    private AbstractSpline getBestSplineMatch( AbstractSpline spline ) {
        if( containsSpline( spline ) ) {
            return spline;
        }
        else {
            double bestScore = Double.POSITIVE_INFINITY;
            AbstractSpline best = null;
            for( int i = 0; i < splineSurfaces.size(); i++ ) {
                SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
                double score = spline.getDistance( splineSurface.getSpline() );
                if( score < bestScore ) {
                    bestScore = score;
                    best = splineSurface.getSpline();
                }
            }
            return best;
        }
    }

    private boolean containsSpline( AbstractSpline spline ) {
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            if( splineSurface.contains( spline ) ) {
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
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.preStep( dt );
        }
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( dt );
        }
        for( int i = 0; i < floors.size(); i++ ) {
            floorAt( i ).stepInTime( dt );
        }
    }

    public ArrayList getAllSplines() {
        ArrayList list = new ArrayList();
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            list.add( splineSurface.getSpline() );
//            list.add( splineSurface.getBottom() );
        }
        return list;
    }

    public SplineSurface splineSurfaceAt( int i ) {
        return (SplineSurface)splineSurfaces.get( i );
    }

    public Floor floorAt( int i ) {
        return (Floor)floors.get( i );
    }

    public void addSplineSurface( SplineSurface splineSurface ) {
        splineSurfaces.add( splineSurface );
    }

    public void addBody( Body body ) {
        bodies.add( body );
        if( bodies.size() == 1 ) {//The zero point potential now occurs at the center of mass of the skater.
            zeroPointPotentialY = body.getShape().getBounds2D().getHeight() / 2;
            initZeroPointPotentialY = zeroPointPotentialY;
        }
    }

    public int numBodies() {
        return bodies.size();
    }

    public Body bodyAt( int i ) {
        return (Body)bodies.get( i );
    }

    private void addFloor( Floor floor ) {
        floors.add( floor );
    }

    public double getGravity() {
        return gravity;
    }

    public void removeSplineSurface( SplineSurface splineSurface ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( splineSurface ) ) {
                body.setFreeFallMode();
            }
        }
        notifyBodiesSplineRemoved( splineSurface.getSpline() );
//        notifyBodiesSplineRemoved( splineSurface.getBottom() );
        splineSurfaces.remove( splineSurface );
    }

    private void notifyBodiesSplineRemoved( AbstractSpline spline ) {
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
    }

    public void translateZeroPointPotentialY( double dy ) {
        setZeroPointPotentialY( getZeroPointPotentialY() + dy );
    }

    public void reset() {
        bodies.clear();
        splineSurfaces.clear();
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
    }

    public static interface EnergyModelListener {
        void preStep( double dt );

        void gravityChanged();
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
