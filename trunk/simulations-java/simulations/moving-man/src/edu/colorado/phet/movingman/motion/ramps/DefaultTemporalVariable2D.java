package edu.colorado.phet.movingman.motion.ramps;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Created by: Sam
 * Dec 7, 2007 at 6:23:43 PM
 */
public class DefaultTemporalVariable2D {
    private DefaultTemporalVariable x = new DefaultTemporalVariable();
    private DefaultTemporalVariable y = new DefaultTemporalVariable();

    public ITemporalVariable getX() {
        return x;
    }

    public double getXValue() {
        return x.getValue();
    }

    public void setXValue( double v ) {
        x.setValue( v );
    }

    public ITemporalVariable getY() {
        return y;
    }

    public void setYValue( double v ) {
        y.setValue( v );
    }

    public Vector2D.Double getScaledInstance( double scale ) {
        return new Vector2D.Double( getXValue() * scale, getYValue() * scale );
    }

    public double getYValue() {
        return y.getValue();
    }

    public void setValue( double x, double y ) {
        this.x.setValue( x );
        this.y.setValue( y );
    }

    public void setValue( AbstractVector2D value ) {
        setValue( value.getX(), value.getY() );
    }

    public AbstractVector2D getValue() {
        return new Vector2D.Double( getXValue(), getYValue() );
    }

    public double getMagnitude() {
        return getValue().getMagnitude();
    }

    public String toString() {
        return "value=" + getValue() + ", xseries=" + x + ", yseries=" + y;
    }

    public int getSampleCount() {
        return x.getSampleCount();
    }

    public void setMagnitudeAndAngle( double magnitude, double angle ) {
        setValue( Vector2D.Double.parseAngleAndMagnitude( magnitude, angle ) );
    }
}
