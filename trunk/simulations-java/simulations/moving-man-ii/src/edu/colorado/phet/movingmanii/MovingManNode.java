package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
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
    private MovingMan man;
    private MovingManModel model;
    private BufferedImage imageStanding;
    private BufferedImage imageLeft;
    private BufferedImage imageRight;

    public MovingManNode(final MovingMan man, MovingManModel model) {
        this.man = man;
        this.model = model;
        try {
            imageStanding = BufferedImageUtils.multiScaleToHeight(MovingManIIResources.loadBufferedImage("stand-ii.gif"), 100);//todo: need our own resource loader
            imageLeft = BufferedImageUtils.multiScaleToHeight(MovingManIIResources.loadBufferedImage("left-ii.gif"), 100);
            imageRight = BufferedImageUtils.flipX(imageLeft);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final PImage movingMan = new PImage(imageStanding);
        addChild(movingMan);
        man.addListener(new MovingMan.Listener() {
            public void changed() {
                updateMan(movingMan, man);
            }
        });
        model.getVelocitySeries().addListener(new MovingManDataSeries.Listener() {
            public void changed() {
                updateMan(movingMan, man);
            }
        });
        updateMan(movingMan, man);

        addInputEventListener(new CursorHandler());
        addInputEventListener(new MovingManDragger(this));
        addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                man.setPositionDriven();
            }
        });
    }

    private void updateMan(PImage movingMan, MovingMan man) {
        movingMan.setOffset(modelToView.evaluate(man.getPosition()), 0);
        if (model.getVelocitySeries().getNumPoints() == 0) {
            movingMan.setImage(imageStanding);
        } else {
            TimeData lastPoint = model.getVelocitySeries().getDataPoint(model.getVelocitySeries().getNumPoints() - 1);
            if (lastPoint.getValue() > 0.1) {
                movingMan.setImage(imageRight);
            } else if (lastPoint.getValue() < -0.1) {
                movingMan.setImage(imageLeft);
            } else {
                movingMan.setImage(imageStanding);
            }
        }
    }

    public MovingMan getMan() {
        return man;
    }

    private static class MovingManDragger extends PBasicInputEventHandler {
        private MovingManNode man;

        private MovingManDragger(MovingManNode man) {
            this.man = man;
        }

        @Override
        public void mouseDragged(PInputEvent event) {
            super.mouseDragged(event);
            double canvasDelta = event.getCanvasDelta().width;
            double mappedDelta1 = man.modelToView.createInverse().evaluate(canvasDelta);
            double mappedDelta0 = man.modelToView.createInverse().evaluate(0);
            double mappedDelta = mappedDelta1 - mappedDelta0;
//            System.out.println("canvas delta = " + canvasDelta + ", mapped delta = " + mappedDelta);
            man.getMan().setPosition(man.getMan().getPosition() + mappedDelta);
        }
    }

}
