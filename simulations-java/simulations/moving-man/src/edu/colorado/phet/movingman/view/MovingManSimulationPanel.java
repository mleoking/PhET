package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.MutableBoolean;
import edu.colorado.phet.common.motion.charts.Range;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.movingman.MovingManColorScheme;
import edu.colorado.phet.movingman.MovingManResources;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanel extends PhetPCanvas {
    private MovingManModel model;
    private double earthOffset;
    private final Range viewRange;
    private final PlayAreaRulerNode playAreaRulerNode;
    private MutableBoolean positiveToTheRight;

    public MovingManSimulationPanel(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, int earthOffset, final MutableBoolean positiveToTheRight) {
        this.model = model;
        this.earthOffset = earthOffset;
        this.positiveToTheRight = positiveToTheRight;
        addScreenChild(new SkyNode());
        addScreenChild(new EarthNode());
        viewRange = new Range(0, 1000);
        playAreaRulerNode = new PlayAreaRulerNode(model.getModelRange(), viewRange);
        playAreaRulerNode.setOffset(0, earthOffset);
        addScreenChild(playAreaRulerNode);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateViewRange();
            }
        });
        updateViewRange();

        try {
            addScreenChild(new PlayAreaObjectNode(BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("tree.gif"), 100), model.getRange(), viewRange, -8, 0, positiveToTheRight));
            addScreenChild(new PlayAreaObjectNode(BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("cottage.gif"), 100), model.getRange(), viewRange, +8, 0, positiveToTheRight));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final MovingManNode manNode = new MovingManNode(model.getMovingMan(), model, viewRange, positiveToTheRight);
        manNode.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                recordAndPlaybackModel.startRecording();
            }
        });
        addScreenChild(manNode);

        BufferedImage wallImage = null;
        try {
            wallImage = BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("wall.jpg"), 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addScreenChild(new WallNode(wallImage, model.getRange(), viewRange, -10, model.getWalls(), -manNode.getImageStanding().getWidth() / 2 - wallImage.getWidth(), positiveToTheRight));
        addScreenChild(new WallNode(wallImage, model.getRange(), viewRange, +10, model.getWalls(), +manNode.getImageStanding().getWidth() / 2 + wallImage.getWidth(), positiveToTheRight));

        int arrowTailWidth = 28;
        //Add Velocity vector to play area
        final PlayAreaVector velocityVector = new PlayAreaVector(MovingManColorScheme.semitransparent(MovingManColorScheme.VELOCITY_COLOR, 128), arrowTailWidth);
        addScreenChild(velocityVector);
        final int arrowY = 100;
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double velocityScale = 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getVelocity() * velocityScale);
                velocityVector.setArrow(startX, arrowY, endX, arrowY);
            }
        });
        model.getVelocityVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateVelocityVectorVisibility(model, velocityVector);
            }
        });
        updateVelocityVectorVisibility(model, velocityVector);

        //Add Acceleration vector to play area
        final PlayAreaVector accelerationVector = new PlayAreaVector(MovingManColorScheme.semitransparent(MovingManColorScheme.ACCELERATION_COLOR, 128), arrowTailWidth);
        addScreenChild(accelerationVector);
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double accelerationScale = 0.8;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getAcceleration() * accelerationScale);
                accelerationVector.setArrow(startX, arrowY, endX, arrowY);
            }
        });
        model.getAccelerationVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateAccelerationVectorVisible(accelerationVector, model);
            }
        });
        updateAccelerationVectorVisible(accelerationVector, model);

        //Low quality rendering helps performance significantly
//        setInteractingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
//        setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
//        setAnimatingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);

        positiveToTheRight.addObserver(new SimpleObserver() {
            public void update() {
                updateViewRange();
            }
        });
    }

    private void updateViewRange() {
        final int inset = 100;
        double min = inset;
        double max = getWidth() - inset;
        if (positiveToTheRight.getValue()) {
            viewRange.setMin(min);
            viewRange.setMax(max);
        } else {
            viewRange.setMin(max);
            viewRange.setMax(min);
        }
    }

    public double getRulerHeight() {
        return playAreaRulerNode.getFullBounds().getHeight();
    }

    private void updateAccelerationVectorVisible(PlayAreaVector accelerationVector, MovingManModel model) {
        accelerationVector.setVisible(model.getAccelerationVectorVisible().getValue());
    }

    private void updateVelocityVectorVisibility(MovingManModel model, PlayAreaVector velocityVector) {
        velocityVector.setVisible(model.getVelocityVectorVisible().getValue());
    }

    private class EarthNode extends PNode {
        private EarthNode() {
            PhetPPath earthNode = new PhetPPath(new Rectangle2D.Double(-100, 100, 10000, 10000), new Color(200, 240, 200));
            addChild(earthNode);
        }
    }

    private class SkyNode extends PNode {
        private SkyNode() {
            PhetPPath skyNode = new PhetPPath(new Rectangle2D.Double(-100, -500, 10000, 10000), new Color(174, 217, 255));
            addChild(skyNode);
        }
    }

    protected PlayAreaRulerNode getPlayAreaRulerNode() {
        return playAreaRulerNode;
    }
}
