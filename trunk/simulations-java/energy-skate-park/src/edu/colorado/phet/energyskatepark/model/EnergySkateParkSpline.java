package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyskatepark.test.phys1d.ControlPointParametricFunction2D;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 11:30:06 AM
 */
public class EnergySkateParkSpline implements Cloneable {
    private ControlPointParametricFunction2D parametricFunction2D;
    private boolean rollerCoaster;
    private boolean userControlled;
    private boolean interactive = true;
//    private GeneralPath generalPath;

    public EnergySkateParkSpline( ControlPointParametricFunction2D parametricFunction2D ) {
        this.parametricFunction2D = parametricFunction2D;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public Object clone() {
        try {
            EnergySkateParkSpline clone = (EnergySkateParkSpline)super.clone();
            clone.parametricFunction2D = (ControlPointParametricFunction2D)this.parametricFunction2D.clone();
            clone.rollerCoaster = this.rollerCoaster;
            clone.userControlled = this.userControlled;
            clone.interactive = this.interactive;
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public EnergySkateParkSpline copy() {
        return (EnergySkateParkSpline)clone();
    }

    public boolean equals( Object obj ) {
        if( obj instanceof EnergySkateParkSpline ) {
            EnergySkateParkSpline energySkateParkSpline = (EnergySkateParkSpline)obj;
            return energySkateParkSpline.parametricFunction2D.equals( this.parametricFunction2D ) && energySkateParkSpline.rollerCoaster == rollerCoaster && energySkateParkSpline.userControlled == userControlled && energySkateParkSpline.interactive == interactive;
        }
        else {
            return false;
        }
    }

    public ControlPointParametricFunction2D getParametricFunction2D() {
        return parametricFunction2D;
    }

    public void setRollerCoasterMode( boolean selected ) {
        this.rollerCoaster = selected;
    }

    public boolean isRollerCoasterMode() {
        return rollerCoaster;
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled = userControlled;
    }

    public boolean isRollerCoaster() {
        return rollerCoaster;
    }

    public Point2D[] getControlPoints() {
        return parametricFunction2D.getControlPoints();
    }

    public boolean isInteractive() {
        return interactive;
    }

    public GeneralPath getInterpolationPath() {
        return createInterpolationPath();//todo: buffering for this call?
//        if( generalPath == null ) {
//            generalPath = createInterpolationPath();
//        }
//        return generalPath;
    }

    private GeneralPath createInterpolationPath() {
        Point2D[] pts = getInterpolationPoints();
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( pts[0].getX(), pts[0].getY() );
        for( int i = 1; i < pts.length; i++ ) {
            path.lineTo( pts[i] );
        }
        return path.getGeneralPath();
    }

    private Point2D[] getInterpolationPoints() {
        ArrayList pts = new ArrayList();
        for( double alpha = 0.0; alpha <= 1.0; alpha += 0.01 ) {
            pts.add( parametricFunction2D.evaluate( alpha ) );
        }
        pts.add( parametricFunction2D.evaluate( 1.0 ) );
        return (Point2D[])pts.toArray( new Point2D.Double[0] );
    }

    public Point2D controlPointAt( int index ) {
        return getControlPoints()[index];
    }

    public void translate( double dx, double dy ) {
        parametricFunction2D.translateControlPoints( dx, dy );
    }

    public int numControlPoints() {
        return getControlPoints().length;
    }

    public void translateControlPoint( int index, double width, double height ) {
        parametricFunction2D.translateControlPoint( index, width, height );
    }

    public void removeControlPoint( int index ) {
        parametricFunction2D.removeControlPoint( index );
    }

    public void printControlPointCode() {
        System.out.println( "parametricFunction2D.toStringSerialization() = " + parametricFunction2D.toStringSerialization() );
    }

    public void setControlPointLocation( int index, Point2D pt ) {
        parametricFunction2D.setControlPoint( index, pt );
    }
}
