/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.potentials;

import edu.colorado.phet.qm.model.Potential;

import java.awt.*;
import java.util.ArrayList;

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

    public void reset( int gridWidth, int gridHeight, int y, int height, int slitSize, int slitSeparation, double potential ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.y = y;
        this.height = height;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        this.potential = potential;
        update();
    }


    public static interface Listener {
        public void slitChanged();
    }

    private ArrayList listeners = new ArrayList();

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

    public void addListener( Listener listener ) {
        this.listeners.add( listener );
    }

    private void update() {
        createDoubleSlit( gridWidth, gridHeight, y, height, slitSize, slitSeparation, potential );
        notifyListeners();
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

    private void createDoubleSlit( int gridWidth, int gridHeight,
                                   int y, int height, int slitSize, int slitSeparation, double potential ) {
        CompositePotential compositePotential = new CompositePotential();
        int barWidth = ( gridWidth - 2 * slitSize - slitSeparation ) / 2;

        Rectangle leftBar = new Rectangle( 0, y, barWidth, height );
        Rectangle midBar = new Rectangle( barWidth + slitSize, y, slitSeparation, height );
        Rectangle rightBar = new Rectangle( barWidth + slitSize * 2 + slitSeparation, y, barWidth + 1, height );

        compositePotential.addPotential( new BarrierPotential( leftBar, potential ) );
        compositePotential.addPotential( new BarrierPotential( midBar, potential ) );
        compositePotential.addPotential( new BarrierPotential( rightBar, potential ) );
        this.potentialDelegate = new PrecomputedPotential( compositePotential, gridWidth, gridHeight );

        this.leftSlit = new Rectangle( (int)leftBar.getMaxX(), y, slitSize, height );
        this.rightSlit = new Rectangle( (int)midBar.getMaxX(), y, slitSize, height );
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
        int y = leftSlit.y + 1;
        int height = leftSlit.height;
        Rectangle leftBlock = new Rectangle( 0, y, leftSlit.x, height );
        Rectangle centerBlock = new Rectangle( leftSlit.x + leftSlit.width, y, rightSlit.x - leftSlit.x - leftSlit.width, height );
        Rectangle rightBlock = new Rectangle( rightSlit.x + rightSlit.width, y, gridWidth - rightSlit.x - rightSlit.width, height );
        return new Rectangle[]{leftBlock, centerBlock, rightBlock};
    }
}
