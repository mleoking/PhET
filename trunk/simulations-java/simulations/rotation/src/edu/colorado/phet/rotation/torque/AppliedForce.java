package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.rotation.util.RotationUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Sep 12, 2007
 * Time: 8:18:11 PM
 */
public class AppliedForce {
    private DefaultTemporalVariable radius = new DefaultTemporalVariable();

    private DefaultTemporalVariable appliedForceSrcX = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceSrcY = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceDstX = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceDstY = new DefaultTemporalVariable();
    private ArrayList listeners = new ArrayList();

    public void setRadius( double r ) {
        if ( radius.getValue() != r ) {
            this.radius.setValue( r );
            notifyChanged();
        }
    }

    public void stepInTime( double dt, double time ) {
        defaultUpdate( radius, time );
        defaultUpdate( appliedForceSrcX, time );
        defaultUpdate( appliedForceSrcY, time );
        defaultUpdate( appliedForceDstX, time );
        defaultUpdate( appliedForceDstY, time );
        notifyChanged();//todo: only signify change when data values change
    }

    private void defaultUpdate( ITemporalVariable variable, double time ) {
        variable.addValue( variable.getValue(), time );
    }

    public void setPlaybackTime( double time ) {
        radius.setPlaybackTime( time );
        appliedForceSrcX.setPlaybackTime( time );
        appliedForceSrcY.setPlaybackTime( time );
        appliedForceDstX.setPlaybackTime( time );
        appliedForceDstY.setPlaybackTime( time );
        notifyChanged();//todo: only signify change when data values change
    }

    public void clear() {
        radius.clear();
        appliedForceSrcX.clear();
        appliedForceSrcY.clear();
        appliedForceDstX.clear();
        appliedForceDstY.clear();
        notifyChanged();//todo: only signify change when data values change
    }

    public ITemporalVariable getRadiusSeries() {
        return radius;
    }

    public Point2D getP1() {
        return toLine2D().getP1();
    }

    public Point2D getP2() {
        return toLine2D().getP2();
    }

    public Line2D.Double toLine2D() {
        return new Line2D.Double( appliedForceSrcX.getValue(), appliedForceSrcY.getValue(),
                                  appliedForceDstX.getValue(), appliedForceDstY.getValue() );
    }

    private void setP2( Point2D destination ) {
        if ( !getP2().equals( destination ) ) {
            this.appliedForceDstX.setValue( destination.getX() );
            this.appliedForceDstY.setValue( destination.getY() );
            notifyChanged();
        }
    }

    public void setValue( Line2D.Double appliedForce ) {
        if ( !RotationUtil.lineEquals( toLine2D(), appliedForce ) ) {
            this.appliedForceSrcX.setValue( appliedForce.getX1() );
            this.appliedForceSrcY.setValue( appliedForce.getY1() );
            setP2( appliedForce.getP2() );

            this.radius.setValue( new Vector2D.Double( appliedForce.getP1() ).getMagnitude() );
            notifyChanged();
        }
    }

    private void notifyChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).changed();
        }
    }

    public double getForceMagnitude() {
        return getP1().distance( getP2() );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getSignedForce( Point2D center ) {
        return getForceMagnitude() * MathUtil.getSign( getTorque( center ) );
    }

    public double getTorque( Point2D center ) {
        Vector2D.Double r = new Vector2D.Double( center, getP1() );
        Vector2D.Double f = new Vector2D.Double( getP1(), getP2() );
        return -r.getCrossProductScalar( f );
    }

    public double getRadius() {
        return getRadiusSeries().getValue();
    }

    public interface Listener {
        void changed();
    }
}
