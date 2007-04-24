/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;

import java.awt.geom.Point2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SPoint2D extends Point2D.Double implements Externalizable {
    public SPoint2D() {
        super();
    }

    public SPoint2D( double x, double y ) {
        super( x, y );
    }

    public SPoint2D( Point2D pt ) {
        this( pt.getX(), pt.getY() );
    }

    public String toString() {
        return getClass().getName() + " [" + getX() + ", " + getY() + "]";
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        x = in.readDouble();
        y = in.readDouble();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeDouble( x );
        out.writeDouble( y );
    }

    public static void main( String[] args ) throws PersistenceUtil.CopyFailedException {
        System.out.println( "SPoint2D.main" );

        SPoint2D pt = (SPoint2D)PersistenceUtil.copy( new SPoint2D( 3, 4 ) );
        System.out.println( "pt = " + pt );

        EnergySkateParkSpline spline = new EnergySkateParkSpline( new SPoint2D[]{new SPoint2D( 5, 5 ), new SPoint2D( 10, 10 )} );
        EnergySkateParkSpline copy = (EnergySkateParkSpline)PersistenceUtil.copy( spline );
        System.out.println( "copy = " + copy );

        EnergySkateParkModel model = new EnergySkateParkModel( 5 );
        model.addSplineSurface( spline );
        EnergySkateParkModel modelCopy = (EnergySkateParkModel)PersistenceUtil.copy( model );
        System.out.println( "modelCopy = " + modelCopy.getNumSplines() );
        System.out.println( "modelCopy.getSpline( 0) = " + modelCopy.getSpline( 0 ) );
    }
}
