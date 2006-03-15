/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class FractionalDoubleSlit {
    private DiscreteModel discreteModel;
    private double y;
    private double height;
    private double slitSize;
    private double slitSeparation;
    private double potential;

    public FractionalDoubleSlit( DiscreteModel discreteModel, double y, double height, double slitSize, double slitSeparation ) {
        this.discreteModel = discreteModel;
        this.y = y;
        this.height = height;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        update();
        discreteModel.addListener( new DiscreteModel.Adapter() {
            public void sizeChanged() {
                update();
            }
        } );
    }

    public void reset( double y, double height, double slitSize, double slitSeparation, double potential ) {
        this.y = y;
        this.height = height;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        this.potential = potential;
        update();
    }

    private void update() {
        discreteModel.getDoubleSlitPotential().setGridWidth( discreteModel.getGridWidth() );
        discreteModel.getDoubleSlitPotential().setGridHeight( discreteModel.getGridHeight() );
        discreteModel.getDoubleSlitPotential().setHeight( round( height * discreteModel.getGridHeight() ) );
        discreteModel.getDoubleSlitPotential().setSlitSeparation( round( slitSeparation * discreteModel.getGridWidth() ) );
        int gridWidth = discreteModel.getGridWidth();
        int slitSize = round( this.slitSize * gridWidth );
        discreteModel.getDoubleSlitPotential().setSlitSize( slitSize );
        discreteModel.getDoubleSlitPotential().setY( round( y * discreteModel.getGridHeight() ) );
    }

    private int round( double v ) {
//        return (int)Math.round( v );
        return (int)v;
    }

    public void setY( double y ) {
        this.y = y;
//        System.out.println( "changed: y=" + y );
        update();
    }

    public void setHeight( double height ) {
        this.height = height;
        update();
    }

    public void setSlitSize( double slitSize ) {
        this.slitSize = slitSize;
//        System.out.println( "slitSize = " + slitSize );
        update();
    }

    public void setSlitSeparation( double slitSeparation ) {
        this.slitSeparation = slitSeparation;
//        System.out.println( "slitSeparation = " + slitSeparation );
        update();
    }

    public void setPotential( double potential ) {
        this.potential = potential;
        update();
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getSlitSize() {
        return slitSize;
    }

    public double getSlitSeparation() {
        return slitSeparation;
    }

    public double getPotential() {
        return potential;
    }

    public static interface Listener {
        public void slitChanged();
    }
}
