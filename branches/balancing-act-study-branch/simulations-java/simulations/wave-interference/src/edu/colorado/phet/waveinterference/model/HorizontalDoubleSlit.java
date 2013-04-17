// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
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

    public void reset( int gridWidth, int gridHeight, int y, int height, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
//        System.out.println( "reset, y=" + y );
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
        int leftSlitCenter = round( gridWidth / 2.0 - slitSeparation / 2.0 );
        int rightSlitCenter = round( gridWidth / 2.0 + slitSeparation / 2.0 );
        int midWidth = round( gridWidth / 2.0 - slitSeparation / 2.0 - slitSize / 2.0 );

        Rectangle leftBar = new Rectangle( 0, y, round( leftSlitCenter - slitSize / 2.0 ), height );
        Rectangle midBar = new Rectangle( round( leftSlitCenter + slitSize / 2.0 ), y, slitSeparation - slitSize, height );
        Rectangle rightBar = new Rectangle( round( rightSlitCenter + slitSize / 2.0 ), y, midWidth + 1, height );

        this.leftSlit = new Rectangle( leftBar.x + leftBar.width, y, slitSize, height );
        this.rightSlit = new Rectangle( midBar.x + midBar.width, y, slitSize, height );
        CompositePotential compositePotential = new CompositePotential();
        if ( inverse ) {
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

    private int round( double v ) {
        return (int) v;
    }

    private void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
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
//        System.out.println( "changed: y=" + y );
        update();
    }

    public void setHeight( int height ) {
        this.height = height;
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
        return new Rectangle[] { new Rectangle( leftSlit ), new Rectangle( rightSlit ) };
    }

    public Rectangle[] getBlockAreas() {
        int y = leftSlit.y + 1;
        int height = leftSlit.height;
        Rectangle leftBlock = new Rectangle( 0, y, leftSlit.x, height );
        Rectangle centerBlock = new Rectangle( leftSlit.x + leftSlit.width, y, rightSlit.x - leftSlit.x - leftSlit.width, height );
        Rectangle rightBlock = new Rectangle( rightSlit.x + rightSlit.width, y, gridWidth - rightSlit.x - rightSlit.width, height );
        return new Rectangle[] { leftBlock, centerBlock, rightBlock };
    }

    public void setInverseSlits( boolean inverseSlits ) {
        this.inverse = inverseSlits;
        update();
    }

    public static interface Listener {
        public void slitChanged();
    }
}
