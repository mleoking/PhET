package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;

/**
 * This node represents an object that can exist in the play area, such as a wall or tree.
 *
 * @author Sam Reid
 */
public class PlayAreaObjectNode extends PNode {
    protected final Function.LinearFunction linearFunction;
    protected final double x;
    protected final double offsetX;
    protected PImage wallNode;

    public PlayAreaObjectNode(BufferedImage image, Range modelRange, final Range viewRange, double x, double offsetX) {
        this.offsetX = offsetX;
        linearFunction = new Function.LinearFunction(modelRange.getMin(), modelRange.getMax(), viewRange.getMin(), viewRange.getMax());
        wallNode = new PImage(image);
        this.x = x;

        addChild(wallNode);
        viewRange.addObserver(new SimpleObserver() {
            public void update() {
                linearFunction.setOutput(viewRange.getMin(), viewRange.getMax());
                updateLocation();
            }
        });
        updateLocation();
    }

    protected void updateLocation() {
        setOffset(linearFunction.evaluate(x) - getFullBounds().getWidth() / 2 + offsetX, 0);
    }
}
