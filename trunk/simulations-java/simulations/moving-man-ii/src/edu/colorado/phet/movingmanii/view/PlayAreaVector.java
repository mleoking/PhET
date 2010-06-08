package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Sam Reid
 */
public class PlayAreaVector extends PNode {
    private final PhetPPath path;
    private final double tailWidth;

    public PlayAreaVector(String name, Color color, double tailWidth) {
        this.tailWidth = tailWidth;
        path = new PhetPPath(color, new BasicStroke(1), Color.black);
        addChild(path);
    }

    public void setArrow(double x, double y, double x2, double y2) {
        path.setPathTo(new Arrow(new Point2D.Double(x, y), new Point2D.Double(x2, y2), 10, 10, tailWidth, 4.0, false).getShape());
    }
}
