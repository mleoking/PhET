/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class FractionalDoubleSlit {
    private QWIModel qwiModel;
    private double y;
    private double height;
    private double slitSize;
    private double slitSeparation;
    private double potential;

    public FractionalDoubleSlit( QWIModel QWIModel, double y, double height, double slitSize, double slitSeparation ) {
        this.qwiModel = QWIModel;
        this.y = y;
        this.height = height;
        this.slitSize = slitSize;
        this.slitSeparation = slitSeparation;
        update();
        QWIModel.addListener( new QWIModel.Adapter() {
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
        qwiModel.getDoubleSlitPotential().setGridWidth( qwiModel.getGridWidth() );
        qwiModel.getDoubleSlitPotential().setGridHeight( qwiModel.getGridHeight() );
        qwiModel.getDoubleSlitPotential().setHeight( round( height * qwiModel.getGridHeight() ) );

        int integralSlitWidth = roundEven( this.slitSize * qwiModel.getGridWidth() );
        qwiModel.getDoubleSlitPotential().setSlitWidth( integralSlitWidth );
        qwiModel.getDoubleSlitPotential().setY( round( y * qwiModel.getGridHeight() ) );

        int a = (int)( qwiModel.getGridWidth() * slitSeparation - integralSlitWidth );
        qwiModel.getDoubleSlitPotential().setNumCellsBetweenSlits( a );
    }

    private int roundEven( double v ) {
        int value = round( v );
        if( value % 2 == 0 ) {
            value++;
        }
        return value;
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
