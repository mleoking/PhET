package edu.colorado.phet.common.math.transforms;

import edu.colorado.phet.common.math.transforms.functions.RangeToRange;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**Transforms from a to b rectangles by translation and scale.*/
public class BoxToBox implements InvertibleTransform, IBoxToBox {
    RangeToRange xtrf;
    RangeToRange ytrf;
    Rectangle2D.Double in;
    Rectangle2D.Double out;

    //A is the input rectangle, B is the output rectangle.
    public BoxToBox(Rectangle2D.Double in, Rectangle2D.Double out) {
        this.in = in;
        this.out = out;
        setState();

    }

    private void setState() {
        xtrf = new RangeToRange(in.x, in.x + in.width, out.x, out.x + out.width);
        ytrf = new RangeToRange(in.y, in.y + in.height, out.y, out.y + out.height);
    }

    public Rectangle2D.Double getInputBounds() {
        return in;//new DoubleRectangle(ax, ay, aw, ah);
    }

    public Rectangle2D.Double getOutputBounds() {
        return out;//new DoubleRectangle(bx, by, bw, bh);
    }

    public void setInputBounds(Rectangle2D.Double in) {
        this.in = in;
        setState();
    }

    public void setOutputBounds(Rectangle2D.Double out) {
        this.out = out;
        setState();
    }

    public Point2D.Double transform(Point2D.Double in) {
        double x = xtrf.evaluate(in.x);
        double y = ytrf.evaluate(in.y);
        //o.O.d("in="+in+", rel="+rel+", scaled="+scaled+", out="+out);
        //o.O.d("in="+in+", out="+out);
        return new Point2D.Double(x, y);
    }

    public InvertibleTransform invert() {
        return new BoxToBox(out,in);
    }
}
