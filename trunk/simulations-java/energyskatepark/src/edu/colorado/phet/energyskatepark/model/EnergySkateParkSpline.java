package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyskatepark.test.phys1d.ControlPointParametricFunction2D;
import edu.colorado.phet.energyskatepark.test.phys1d.ParametricFunction2D;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Mar 16, 2007
 * Time: 11:30:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class EnergySkateParkSpline {
    private ControlPointParametricFunction2D parametricFunction2D;
    private boolean rollerCoaster;
    private boolean userControlled;
    private boolean interactive = true;
    private GeneralPath generalPath;

    public EnergySkateParkSpline( ControlPointParametricFunction2D parametricFunction2D ) {
        this.parametricFunction2D = parametricFunction2D;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public EnergySkateParkSpline copy() {
        return null;
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
        if( generalPath == null ) {
            generalPath = createInterpolationPath();
        }
        return generalPath;
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
        return (Point2D[])pts.toArray( new Point2D.Double[0] );
    }

    public Point2D controlPointAt( int index ) {
        return getControlPoints()[index];
    }

    public void translate( double dx, double dy ) {
        parametricFunction2D.translateControlPoints(dx,dy);
    }

    public int numControlPoints() {
        return getControlPoints().length;
    }

    public void translateControlPoint( int index, double width, double height ) {
        parametricFunction2D.translateControlPoint(index,width,height);
    }

    public void removeControlPoint( int index ) {
        parametricFunction2D.removeControlPoint(index);
    }

    public void printControlPointCode() {
        System.out.println( "parametricFunction2D.toStringSerialization() = " + parametricFunction2D.toStringSerialization() );
    }
}
