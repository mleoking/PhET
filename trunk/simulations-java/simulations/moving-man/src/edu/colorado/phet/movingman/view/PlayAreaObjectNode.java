package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.MutableBoolean;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingman.LinearTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;

/**
 * This node represents an object that can exist in the play area, such as a wall or tree.
 *
 * @author Sam Reid
 */
public class PlayAreaObjectNode extends PNode {
    protected final double x;
    protected final double offsetX;
    protected PImage wallNode;

    public PlayAreaObjectNode(BufferedImage image, final LinearTransform transform, final double x, final double offsetX, final MutableBoolean positiveToTheRight) {
        this.offsetX = offsetX;
        wallNode = new PImage(image);
        this.x = x;
        addChild(wallNode);
        SimpleObserver locationUpdate = new SimpleObserver() {
            public void update() {
                setOffset(transform.evaluate(PlayAreaObjectNode.this.x) - getFullBounds().getWidth() / 2 + PlayAreaObjectNode.this.offsetX * (positiveToTheRight.getValue() ? 1 : -1), 0);
            }
        };
        locationUpdate.update();
        transform.addObserver(locationUpdate);
        positiveToTheRight.addObserver(locationUpdate);
    }
}