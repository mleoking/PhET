package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyskatepark.model.physics.ControlPointParametricFunction2D;
import edu.colorado.phet.energyskatepark.model.physics.CubicSpline2D;

import java.awt.geom.GeneralPath;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 11:30:06 AM
 */
public class EnergySkateParkSpline implements Cloneable, Serializable {
    private DefaultTrackSpline parametricFunction2D;
    private boolean rollerCoaster;
    private boolean userControlled;
    private boolean interactive = true;
    private transient ArrayList listeners = new ArrayList();

    public EnergySkateParkSpline( SerializablePoint2D[] controlPoints ) {
//        this( new CubicSpline2D( controlPoints ) );
        this( new DefaultTrackSpline( controlPoints ) );
    }

    private EnergySkateParkSpline( DefaultTrackSpline parametricFunction2D ) {
        this.parametricFunction2D = parametricFunction2D;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public String toString() {
        return "fn=" + parametricFunction2D;
    }

    public Object clone() {
        try {
            EnergySkateParkSpline clone = (EnergySkateParkSpline)super.clone();
            clone.parametricFunction2D = (DefaultTrackSpline)this.parametricFunction2D.clone();
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
        if( selected != rollerCoaster ) {
            this.rollerCoaster = selected;
            parametricFunction2D.rollerCoasterMode = rollerCoaster;
            notifyRollerCoasterModeChanged();
        }
    }

    public boolean isRollerCoasterMode() {
        return rollerCoaster;
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled = userControlled;
    }

    public SerializablePoint2D[] getControlPoints() {
        return parametricFunction2D.getControlPoints();
    }

    public boolean isInteractive() {
        return interactive;
    }

    public GeneralPath getInterpolationPath() {
        return createInterpolationPath();//todo: buffering for this call?
    }

    private GeneralPath createInterpolationPath() {
        SerializablePoint2D[] pts = getInterpolationPoints();
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( pts[0].getX(), pts[0].getY() );
        for( int i = 1; i < pts.length; i++ ) {
            path.lineTo( pts[i] );
        }
        return path.getGeneralPath();
    }

    private SerializablePoint2D[] getInterpolationPoints() {
        ArrayList pts = new ArrayList();
        for( double alpha = 0.0; alpha <= 1.0; alpha += 0.01 ) {
            pts.add( parametricFunction2D.evaluate( alpha ) );
        }
        pts.add( parametricFunction2D.evaluate( 1.0 ) );
        return (SerializablePoint2D[])pts.toArray( new SerializablePoint2D[0] );
    }

    public SerializablePoint2D controlPointAt( int index ) {
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

    public void setControlPointLocation( int index, SerializablePoint2D pt ) {
        parametricFunction2D.setControlPoint( index, pt );
    }

    public double getMinY() {
        double minY = Double.POSITIVE_INFINITY;
        for( int i = 0; i < 100; i++ ) {
            SerializablePoint2D pt = parametricFunction2D.evaluate( ( (double)i ) / 100.0 );
            if( pt.getY() < minY ) {
                minY = pt.getY();
            }
        }
        for( int i = 0; i < numControlPoints(); i++ ) {
            if( controlPointAt( i ).getY() < minY ) {
                minY = controlPointAt( i ).getY();
            }
        }
        return minY;
    }

    public static interface Listener {
        void rollerCoasterModeChanged();
    }

    public void addListener( Listener listener ) {
        createListenerArray();
        listeners.add( listener );
    }

    private void createListenerArray() {
        if( listeners == null ) {
            listeners = new ArrayList();
        }
    }

    public void removeListener( Listener listener ) {
        createListenerArray();
        listeners.remove( listener );
    }

    public void notifyRollerCoasterModeChanged() {
        createListenerArray();
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).rollerCoasterModeChanged();
        }
    }

    public static class DefaultTrackSpline extends CubicSpline2D {
        private boolean rollerCoasterMode = false;

        public DefaultTrackSpline( SerializablePoint2D[] pts ) {
            super( pts );
        }

        public boolean isRollerCoasterMode() {
            return rollerCoasterMode;
        }
    }
}
