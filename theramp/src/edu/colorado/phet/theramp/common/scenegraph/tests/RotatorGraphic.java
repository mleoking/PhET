package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.common.scenegraph.AbstractGraphic;
import edu.colorado.phet.theramp.common.scenegraph.GraphicNode;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 12:08:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class RotatorGraphic extends GraphicNode {
    AbstractGraphic child;
    private AbstractClock clock;
    private double dTheta;

    public RotatorGraphic(final AbstractGraphic child, AbstractClock clock, final double dTheta) {
        this.child = child;
        this.clock = clock;
        this.dTheta = dTheta;
        clock.addClockTickListener(new ClockTickListener() {
            public void clockTicked(ClockTickEvent event) {
                doRotate();
            }
        });
        child.setParent(this);
    }

    private void doRotate() {
        Point2D center = RectangleUtils.getCenter2D(getLocalBounds());
        rotate(dTheta, center.getX(), center.getY());
    }

    public AbstractGraphic[] getChildren() {
        return new AbstractGraphic[]{child};
    }
}
