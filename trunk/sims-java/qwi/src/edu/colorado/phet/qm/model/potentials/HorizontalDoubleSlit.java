/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.potentials;

import edu.colorado.phet.qm.model.Potential;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class HorizontalDoubleSlit implements Potential {
    private int gridWidth;
    private int gridHeight;
    private int y;
    private int height;
    private int slitWidth;
    private int numCellsBetweenSlits;
    private double potential;
    private Potential potentialDelegate;
    private Rectangle leftSlit;
    private Rectangle rightSlit;
    private ArrayList listeners = new ArrayList();
    private boolean inverse = false;

    public HorizontalDoubleSlit( int gridWidth, int gridHeight, int y, int height, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.y = y;
        this.height = height;
        this.slitWidth = slitSize;
        this.numCellsBetweenSlits = slitSeparation;
        this.potential = potential;
        update();
    }

    public void setState( int gridWidth, int gridHeight, int y, int height, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.y = y;
        this.height = height;
        this.slitWidth = slitSize;
        this.numCellsBetweenSlits = slitSeparation;
        this.potential = potential;
        update();
    }

    public String toString() {
        return "gridWidth=" + gridWidth + ", gridHeight=" + gridHeight + ", y=" + y + ", height=" + height + ", slitSize=" + slitWidth + ", slitsep=" + numCellsBetweenSlits;
    }

    public void addListener( Listener listener ) {
        this.listeners.add( listener );
    }

    private void update() {
//        System.out.println( "this = " + this );
        if( gridWidth % 2 == 0 ) {
            updateEven();
        }
        else {
            updateOdd();
        }
    }

    private void updateOdd() {
        int indexOfCenterSquare = ( gridWidth - 1 ) / 2;
        int numCellsToSlitEachSide = numCellsBetweenSlits / 2;

        int leftSlitStart = indexOfCenterSquare - numCellsToSlitEachSide - slitWidth;
        int rightSlitStart = indexOfCenterSquare + numCellsToSlitEachSide + 1;
        this.leftSlit = new Rectangle( leftSlitStart, y, slitWidth, height );
        this.rightSlit = new Rectangle( rightSlitStart, y, slitWidth, height );
//        debugSymmetry2();

        updatePotentialDelegate();
        notifyListeners();
    }

    private void updateEven() {
        int indexOfCenterPair = ( gridWidth - 1 ) / 2;
        int numCellsToSlitEachSide = numCellsBetweenSlits / 2 - 1;

        int leftSlitStart = indexOfCenterPair - numCellsToSlitEachSide - slitWidth;
        int rightSlitStart = indexOfCenterPair + numCellsToSlitEachSide + 2;
        this.leftSlit = new Rectangle( leftSlitStart, y, slitWidth, height );
        this.rightSlit = new Rectangle( rightSlitStart, y, slitWidth, height );
//        debugSymmetry2();

        updatePotentialDelegate();
        notifyListeners();
    }

    private void updatePotentialDelegate() {
        Rectangle[] bars = toBars();
        CompositePotential compositePotential = new CompositePotential();
        if( inverse ) {
            compositePotential.addPotential( new BarrierPotential( leftSlit, potential ) );
            compositePotential.addPotential( new BarrierPotential( rightSlit, potential ) );
        }
        else {
            compositePotential.addPotential( new BarrierPotential( bars[0], potential ) );
            compositePotential.addPotential( new BarrierPotential( bars[1], potential ) );
            compositePotential.addPotential( new BarrierPotential( bars[2], potential ) );
        }
        this.potentialDelegate = new PrecomputedPotential( compositePotential, gridWidth, gridHeight );
    }

    private Rectangle[] toBars() {
        return new Rectangle[]{
                new Rectangle( 0, y, leftSlit.x, height ),
                new Rectangle( leftSlit.x + leftSlit.width, y, rightSlit.x - leftSlit.x - leftSlit.width, height ),
                new Rectangle( rightSlit.x + rightSlit.width, y, leftSlit.x, height )
        };
    }

    private void debugSymmetry2() {
        double waveModelCenter = ( gridWidth ) / 2.0;
        double leftSlitCenter = leftSlit.getCenterX();
        double rightSlitCenter = rightSlit.getCenterX();
        double distToLeftCenter = Math.abs( waveModelCenter - leftSlitCenter );
        double distToRightCenter = Math.abs( waveModelCenter - rightSlitCenter );
        System.out.println( "distToRightCenter = " + distToRightCenter + ", distToLeftCenter=" + distToLeftCenter );
    }

    private void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.slitChanged();
        }
    }

    public void setGridWidth( int gridWidth ) {
        this.gridWidth = gridWidth;
        update();
    }

    public void setGridHeight( int gridHeight ) {
        this.gridHeight = gridHeight;
        update();
    }

    public void setY( int y ) {
        this.y = y;
        update();
    }

    public void setHeight( int height ) {
        this.height = height;
        update();
    }

    public void setSlitWidth( int slitWidth ) {
        this.slitWidth = slitWidth;
        update();
    }

    public void setNumCellsBetweenSlits( int slitSeparation ) {
        this.numCellsBetweenSlits = slitSeparation;
        update();
    }

    public void setPotential( double potential ) {
        this.potential = potential;
        update();
    }

    public void setPotentialDelegate( Potential potentialDelegate ) {
        this.potentialDelegate = potentialDelegate;
        update();
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

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getSlitWidth() {
        return slitWidth;
    }

    public int getNumCellsBetweenSlits() {
        return numCellsBetweenSlits;
    }

    public double getPotential() {
        return potential;
    }

    public Rectangle[] getSlitAreas() {
        return new Rectangle[]{new Rectangle( leftSlit ), new Rectangle( rightSlit )};
    }

    public Rectangle[] getBlockAreas() {
        return toBars();
    }

    public void setInverseSlits( boolean inverseSlits ) {
        this.inverse = inverseSlits;
        update();
    }

    public void debugSymmetry() {
        System.out.println( "Arrays.asList( getSlitAreas( )) = " + Arrays.asList( getSlitAreas() ) );
        System.out.println( "Arrays.asList( getBlockAreas( )) = " + Arrays.asList( getBlockAreas() ) );
    }

    public static interface Listener {
        public void slitChanged();
    }
}
