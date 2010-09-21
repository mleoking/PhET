package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.phetcommon.model.MutableBoolean;
import edu.colorado.phet.common.motion.charts.Range;
import edu.colorado.phet.common.motion.charts.TemporalDataSeries;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.movingman.MovingManResources;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sam Reid
 */
public class MovingManNode extends PNode {
    private Function.LinearFunction modelToView = new Function.LinearFunction(-10, 10, 0, 975);
    private MovingManModel model;
    private final Range viewRange;
    private final MutableBoolean positiveToTheRight;
    private BufferedImage imageStanding;
    private BufferedImage imageLeft;
    private BufferedImage imageRight;
    private MovingMan man;
    private final PImage imageNode;

    public MovingManNode(final MovingMan man, MovingManModel model, Range viewRange, MutableBoolean positiveToTheRight) {
        this.man = man;
        this.model = model;
        this.viewRange = viewRange;
        this.positiveToTheRight = positiveToTheRight;
        viewRange.addObserver(new SimpleObserver() {
            public void update() {
                updateTransform();
            }
        });
        try {
            imageStanding = BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("man-standing.gif"), 100);//todo: need our own resource loader
            imageLeft = BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("man-left.gif"), 100);
            imageRight = BufferedImageUtils.flipX(imageLeft);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageNode = new PImage(imageStanding);
        updateTransform();
        addChild(imageNode);
        man.addListener(new MovingMan.Listener() {
            public void changed() {
                updateMan();
            }
        });
        model.getVelocityGraphSeries().addListener(new TemporalDataSeries.Adapter() {
            public void entireSeriesChanged() {
                updateMan();
            }
        });
        updateMan();

        addInputEventListener(new CursorHandler());
        addInputEventListener(new MovingManDragger(this));
        addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                man.setPositionDriven();
            }
        });
    }

    public BufferedImage getImageStanding() {
        return imageStanding;
    }

    private void updateTransform() {
        modelToView.setOutput(viewRange.getMin(), viewRange.getMax());
        updateMan();
    }

    private void updateMan() {
        double velocity = man.getVelocity();
        if (velocity > 0.1) {
            imageNode.setImage(positiveToTheRight.getValue() ? imageRight : imageLeft);//make sure the man changes direction if the play area is flipped horizontally
        } else if (velocity < -0.1) {
            imageNode.setImage(positiveToTheRight.getValue() ? imageLeft : imageRight);
        } else {
            imageNode.setImage(imageStanding);
        }
        imageNode.setOffset(modelToView.evaluate(man.getPosition()) - imageNode.getFullBounds().getWidth() / 2, 0);
    }

    public double viewToModel(double x) {
        return modelToView.createInverse().evaluate(x);
    }

    public double modelToView(double x) {
        return modelToView.evaluate(x);
    }

    /**
     * This is the mouse handler for the man graphic, which allows the user to set the "mouse position" property on the
     * model, from which smoother position values are obtained.
     */
    private static class MovingManDragger extends PBasicInputEventHandler {
        private MovingManNode movingManNode;
        private double relativeGrabPoint = Double.NaN;//mouse location within the man graphic

        private MovingManDragger(MovingManNode movingManNode) {
            this.movingManNode = movingManNode;
        }

        public void mousePressed(PInputEvent event) {
            super.mousePressed(event);
            updateRelativeGrabPoint(event);
        }

        public void mouseDragged(PInputEvent event) {
            super.mouseDragged(event);
            if (Double.isNaN(relativeGrabPoint)) {
                updateRelativeGrabPoint(event);
            }
            movingManNode.model.setMousePosition(getModelPoint(event) + relativeGrabPoint);
        }

        public void mouseReleased(PInputEvent event) {
            relativeGrabPoint = Double.NaN;
        }

        private void updateRelativeGrabPoint(PInputEvent event) {
            relativeGrabPoint = getModelPoint(event) - movingManNode.model.getMousePosition();
        }

        private double getModelPoint(PInputEvent event) {
            return movingManNode.viewToModel(event.getCanvasPosition().getX());
        }
    }
}