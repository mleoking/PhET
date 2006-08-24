/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public abstract class VerticalBarrier implements Potential {
    private int gridWidth;
    private int gridHeight;
    private int x;
    private int thickness;
    private int slitSize;
    private int slitSeparation;
    private double potential;
    private Potential potentialDelegate;
    private boolean inverse = false;
    private ArrayList listeners = new ArrayList();

    public VerticalBarrier( int gridWidth, int gridHeight, int x, int thickness, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.x = x;
        this.thickness = thickness;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        this.potential = potential;
        update();
    }

    public void reset( int gridWidth, int gridHeight, int x, int thickness, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
//        System.out.println( "reset, y=" + y );
        this.x = x;
        this.thickness = thickness;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        this.potential = potential;
        update();
    }

    public void addListener( VerticalBarrier.Listener listener ) {
        this.listeners.add( listener );
    }

    public abstract Rectangle[]getRectangleBarriers();

    public abstract Rectangle[] getSlitAreas();

    protected int round( double v ) {
        return (int)v;
    }

    protected void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            VerticalBarrier.Listener listener = (VerticalBarrier.Listener)listeners.get( i );
            listener.slitChanged();
        }
    }

    public void setGridWidth( int gridWidth ) {
        this.gridWidth = gridWidth;
        update();
    }

    protected void update() {
    }

    public void setGridHeight( int gridHeight ) {
        this.gridHeight = gridHeight;
        update();
    }

    public void setX( int x ) {
        this.x = x;
//        System.out.println( "changed: y=" + y );
        update();
    }

    public void setThickness( int thickness ) {
        this.thickness = thickness;
        update();
    }

    public void setSlitSize( int slitSize ) {
        this.slitSize = slitSize;
//        System.out.println( "slitSize = " + slitSize );
        update();
    }

    public void setSlitSeparation( int slitSeparation ) {
        this.slitSeparation = slitSeparation;
//        System.out.println( "slitSeparation = " + slitSeparation );
        update();
    }

    public void setPotential( double potential ) {
        this.potential = potential;
        update();
    }

    protected void setPotentialDelegate( Potential potentialDelegate ) {
        this.potentialDelegate = potentialDelegate;
//        update();
    }

    public double getPotential( int x, int y, int timestep ) {
        return potentialDelegate.getPotential( x, y, timestep );
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getX() {
        return x;
    }

    public int getThickness() {
        return thickness;
    }

    public int getSlitSize() {
        return slitSize;
    }

    public int getSlitSeparation() {
        return slitSeparation;
    }

    public double getPotential() {
        return potential;
    }

    public void setInverseSlits( boolean inverseSlits ) {
        this.inverse = inverseSlits;
        update();
    }

    public boolean getInverse() {
        return inverse;
    }

    public static interface Listener {
        public void slitChanged();
    }
}
