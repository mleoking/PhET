package edu.colorado.phet.movingman.common.transforms;

import edu.colorado.phet.movingman.common.math.RangeToRange;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Transforms from a to b rectangles by translation and scale.
 */
public class BoxToBox implements InvertibleTransform, IBoxToBox {
    RangeToRange xtrf;
    RangeToRange ytrf;
    Rectangle2D in;
    Rectangle2D out;

    //A is the input rectangle, B is the output rectangle.
    public BoxToBox( Rectangle2D in, Rectangle2D out ) {
        this.in = in;
        this.out = out;
        setState();

    }

    private void setState() {
        xtrf = new RangeToRange( in.getX(), in.getX() + in.getWidth(), out.getX(), out.getX() + out.getWidth() );
        ytrf = new RangeToRange( in.getY(), in.getY() + in.getHeight(), out.getY(), out.getY() + out.getHeight() );
    }

    public Rectangle2D getInputBounds() {
        return in;//new DoubleRectangle(ax, ay, aw, ah);
    }

    public Rectangle2D getOutputBounds() {
        return out;//new DoubleRectangle(bx, by, bw, bh);
    }

    public void setInputBounds( Rectangle2D in ) {
        this.in = in;
        setState();
    }

    public void setOutputBounds( Rectangle2D out ) {
        this.out = out;
        setState();
    }

    public Point2D.Double transform( Point2D.Double in ) {
        double x = xtrf.evaluate( in.x );
        double y = ytrf.evaluate( in.y );
        //o.O.d("in="+in+", rel="+rel+", scaled="+scaled+", out="+out);
        //o.O.d("in="+in+", out="+out);
        return new Point2D.Double( x, y );
    }

    public InvertibleTransform invert() {
        return new BoxToBox( out, in );
    }
}
