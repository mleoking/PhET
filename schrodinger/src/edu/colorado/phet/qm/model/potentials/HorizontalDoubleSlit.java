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
    private int slitSize;
    private int slitSeparation;
    private double potential;
    private Potential potentialDelegate;
    private Rectangle leftSlit;
    private Rectangle rightSlit;
    private ArrayList listeners = new ArrayList();
    private boolean inverse = false;
    private int leftSlitStart;
    private int rightSlitStart;

    public HorizontalDoubleSlit( int gridWidth, int gridHeight, int y, int height, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.y = y;
        this.height = height;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        this.potential = potential;
        update();
    }

    public void setState( int gridWidth, int gridHeight, int y, int height, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.y = y;
        this.height = height;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        this.potential = potential;
        update();
    }

    public void addListener( Listener listener ) {
        this.listeners.add( listener );
    }

    private void update() {
        if( gridWidth % 2 == 0 ) {
            updateEven();
        }
        else {
            updateOdd();
        }
    }

    private void updateOdd() {
        int indexOfCenterSquare = ( gridWidth - 1 ) / 2;
        int numCellsToSlit = slitSeparation / 2;

        leftSlitStart = indexOfCenterSquare - numCellsToSlit - slitSize;
        rightSlitStart = indexOfCenterSquare + numCellsToSlit + 1;
        this.leftSlit = new Rectangle( leftSlitStart, y, slitSize, height );
        this.rightSlit = new Rectangle( rightSlitStart, y, slitSize, height );
        Rectangle[] bars = toBars();
        debugSymmetry2();

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
        notifyListeners();
    }

    private Rectangle[] toBars() {
        return new Rectangle[]{
                new Rectangle( 0, y, leftSlitStart, height ),
                new Rectangle( leftSlit.x + leftSlit.width, y, rightSlitStart - leftSlit.x - leftSlit.width, height ),
                new Rectangle( rightSlit.x + rightSlit.width, y, leftSlitStart, height )
        };
    }

    private void updateEven() {
        System.out.println( "update even not symmetric yet" );
        int leftSlitCenter = round( gridWidth / 2.0 - slitSeparation / 2.0 );
        int rightSlitCenter = round( gridWidth / 2.0 + slitSeparation / 2.0 );
        int midWidth = round( gridWidth / 2.0 - slitSeparation / 2.0 - slitSize / 2.0 );

        Rectangle leftBar = new Rectangle( 0, y, round( leftSlitCenter - slitSize / 2.0 ), height );
        Rectangle midBar = new Rectangle( round( leftSlitCenter + slitSize / 2.0 ), y, slitSeparation - slitSize, height );
        Rectangle rightBar = new Rectangle( round( rightSlitCenter + slitSize / 2.0 ), y, midWidth + 1, height );

        this.leftSlit = new Rectangle( leftBar.x + leftBar.width, y, slitSize, height );
        this.rightSlit = new Rectangle( midBar.x + midBar.width, y, slitSize, height );

//        debugSymmetry3( leftSlit, rightSlit );
//        debugSymmetry3( leftBar, rightBar );
//        debugSymmetry3( midBar );
        debugSymmetry2();

        CompositePotential compositePotential = new CompositePotential();
        if( inverse ) {
            compositePotential.addPotential( new BarrierPotential( leftSlit, potential ) );
            compositePotential.addPotential( new BarrierPotential( rightSlit, potential ) );
        }
        else {
            compositePotential.addPotential( new BarrierPotential( leftBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( midBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( rightBar, potential ) );
        }
        this.potentialDelegate = new PrecomputedPotential( compositePotential, gridWidth, gridHeight );
        notifyListeners();
    }

    private void debugSymmetry2() {
        double waveModelCenter = ( gridWidth ) / 2.0;
        double leftSlitCenter = leftSlit.getCenterX();
        double rightSlitCenter = rightSlit.getCenterX();
        double distToLeftCenter = Math.abs( waveModelCenter - leftSlitCenter );
        double distToRightCenter = Math.abs( waveModelCenter - rightSlitCenter );
        System.out.println( "distToRightCenter = " + distToRightCenter + ", distToLeftCenter=" + distToLeftCenter );
    }

    private int round( double v ) {
        return (int)v;
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

    public void setSlitSize( int slitSize ) {
        this.slitSize = slitSize;
        update();
    }

    public void setSlitSeparation( int slitSeparation ) {
        this.slitSeparation = slitSeparation;
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

    public int getSlitSize() {
        return slitSize;
    }

    public int getSlitSeparation() {
        return slitSeparation;
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
