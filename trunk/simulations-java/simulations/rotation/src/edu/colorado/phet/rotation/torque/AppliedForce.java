// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.model.RotationTemporalVariable;
import edu.colorado.phet.rotation.util.RotationUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Sep 12, 2007
 * Time: 8:18:11 PM
 */
public class AppliedForce {
    //independent values
    private DefaultTemporalVariable appliedForceSrcX = new RotationTemporalVariable();
    private DefaultTemporalVariable appliedForceSrcY = new RotationTemporalVariable();
    private DefaultTemporalVariable appliedForceDstX = new RotationTemporalVariable();
    private DefaultTemporalVariable appliedForceDstY = new RotationTemporalVariable();

    //dependent values
    private DefaultTemporalVariable radius = new RotationTemporalVariable();
    private DefaultTemporalVariable signedForce = new RotationTemporalVariable();
    private DefaultTemporalVariable torque = new RotationTemporalVariable();

    private ArrayList listeners = new ArrayList();
    private RotationPlatform platform;

    public AppliedForce( RotationPlatform platform ) {
        this.platform = platform;
        this.radius.setValue( platform.getRadius() );
    }

    public double getTorque() {
        return getTorque( platform.getCenter() );
    }

    public void stepInTime( double dt, double time ) {
        torque.setValue( getTorque() );
        defaultUpdate( radius, time );
        defaultUpdate( torque, time );
        defaultUpdate( signedForce, time );
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
        torque.setPlaybackTime( time );
        signedForce.setPlaybackTime( time );
        appliedForceSrcX.setPlaybackTime( time );
        appliedForceSrcY.setPlaybackTime( time );
        appliedForceDstX.setPlaybackTime( time );
        appliedForceDstY.setPlaybackTime( time );
        notifyChanged();//todo: only signify change when data values change
    }

    public void clear() {
        radius.clear();
        torque.clear();
        signedForce.clear();
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

    public void setValue( Line2D.Double appliedForce ) {
        if ( !RotationUtil.lineEquals( toLine2D(), appliedForce ) ) {
            this.appliedForceSrcX.setValue( appliedForce.getX1() );
            this.appliedForceSrcY.setValue( appliedForce.getY1() );
            this.appliedForceDstX.setValue( appliedForce.getX2() );
            this.appliedForceDstY.setValue( appliedForce.getY2() );

            updateDependentValues( appliedForce );
            notifyChanged();
        }
    }

    private void updateDependentValues( Line2D.Double appliedForce ) {
        radius.setValue( new MutableVector2D( appliedForce.getP1() ).getMagnitude() );
        signedForce.setValue( getForceMagnitude() * MathUtil.getSign( getTorque() ) );//todo: assumes platform center is (0,0)
        torque.setValue( getTorque() );
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
        MutableVector2D r = new MutableVector2D( center, getP1() );
        MutableVector2D f = new MutableVector2D( getP1(), getP2() );
        return -r.getCrossProductScalar( f );
    }

    public double getRadius() {
        return getRadiusSeries().getValue();
    }

    public ITemporalVariable getSignedForceSeries() {
        return signedForce;
    }

    public interface Listener {
        void changed();
    }

    public DefaultTemporalVariable getTorqueSeries() {
        return torque;
    }
}
