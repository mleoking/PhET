// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.motion.charts.Range;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.movingman.LinearTransform;
import edu.colorado.phet.movingman.MovingManColorScheme;
import edu.colorado.phet.movingman.MovingManResources;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private BooleanProperty positiveToTheRight;
    private LinearTransform transform;
    protected TimeReadout timeReadout;

    public MovingManSimulationPanel(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, int earthOffset, final BooleanProperty positiveToTheRight) {
        this.model = model;
        this.earthOffset = earthOffset;
        this.positiveToTheRight = positiveToTheRight;
        addScreenChild(new SkyNode());
        addScreenChild(new EarthNode());
        viewRange = new Range(0, 1000);
        playAreaRulerNode = new PlayAreaRulerNode(model.getModelRange(), viewRange);
        playAreaRulerNode.setOffset(0, earthOffset);
        addScreenChild(playAreaRulerNode);
        final SimpleObserver updateViewRangeInstant = new SimpleObserver() {
            public void update() {
                final int inset = 100;
                double min = positiveToTheRight.get() ? inset : getWidth() - inset;
                double max = positiveToTheRight.get() ? getWidth() - inset : inset;
                viewRange.setMin(min);
                viewRange.setMax(max);
            }
        };
        final SimpleObserver updateViewRangeAnimate = new SimpleObserver() {
            public void update() {
                final int inset = 100;
                double min = positiveToTheRight.get() ? inset : getWidth() - inset;
                double max = positiveToTheRight.get() ? getWidth() - inset : inset;
                //step towards the viewrange value to animate
                viewRange.stepTowardsRange(min, max,getWidth()/600.0*20);//speed independent of screen size
            }
        };
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateViewRangeInstant.update();
            }
        });
        updateViewRangeInstant.update();
        Timer timer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateViewRangeAnimate.update();
            }
        });
        timer.start();//want this to run even if the sim is paused, so use a separate timer

        this.transform = new LinearTransform(model.getRange(), viewRange);
        try {
            addScreenChild(new PlayAreaObjectNode(BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("tree.gif"), 100), transform, -8, 0, positiveToTheRight));
            addScreenChild(new PlayAreaObjectNode(BufferedImageUtils.multiScaleToHeight(MovingManResources.loadBufferedImage("cottage.gif"), 100), transform, +8, 0, positiveToTheRight));
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

        try {
            BufferedImage wallImage = BufferedImageUtils.getScaledInstance(MovingManResources.loadBufferedImage("wall.jpg"), 60, 100, RenderingHints.VALUE_INTERPOLATION_BILINEAR, false);
            addScreenChild(new WallNode(wallImage, transform, -10, model.getWalls(), -manNode.getImageStanding().getWidth() / 2 - wallImage.getWidth() / 2, positiveToTheRight));
            addScreenChild(new WallNode(wallImage, transform, +10, model.getWalls(), +manNode.getImageStanding().getWidth() / 2 + wallImage.getWidth() / 2, positiveToTheRight));
        } catch (IOException e) {
            e.printStackTrace();
        }

        timeReadout = new TimeReadout(model.getTimeProperty());
        transform.addObserver(new SimpleObserver() {
            public void update() {
                timeReadout.setOffset(Math.min(transform.evaluate(-6), transform.evaluate(6)), getTimeReadoutOffsetY());//Initialize at -6 but do not update when axes flip
            }
        });
        addScreenChild(timeReadout);

        int arrowTailWidth = 28;
        //Add Velocity vector to play area
        final PlayAreaVector velocityVector = new PlayAreaVector(MovingManColorScheme.semitransparent(MovingManColorScheme.VELOCITY_COLOR, 128), arrowTailWidth);
        addScreenChild(velocityVector);
        final int arrowY = 100;
        final SimpleObserver updateVelocityVector = new SimpleObserver() {
            public void update() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double velocityScale = 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getVelocity() * velocityScale);
                velocityVector.setArrow(startX, arrowY, endX, arrowY);
            }
        };
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                updateVelocityVector.update();
            }
        });
        viewRange.addObserver(updateVelocityVector);
        model.getVelocityVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateVelocityVectorVisibility(model, velocityVector);
            }
        });
        updateVelocityVectorVisibility(model, velocityVector);

        //Add Acceleration vector to play area
        final PlayAreaVector accelerationVector = new PlayAreaVector(MovingManColorScheme.semitransparent(MovingManColorScheme.ACCELERATION_COLOR, 128), arrowTailWidth);
        addScreenChild(accelerationVector);
        final SimpleObserver updateAccelerationVector = new SimpleObserver() {
            public void update() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double accelerationScale = 0.8;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getAcceleration() * accelerationScale);
                accelerationVector.setArrow(startX, arrowY, endX, arrowY);
            }
        };
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                updateAccelerationVector.update();
            }
        });
        viewRange.addObserver(updateAccelerationVector);
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

        positiveToTheRight.addObserver(updateViewRangeAnimate);
    }

    public void resetAll() {
        // no op in base class
    }

    protected double getTimeReadoutOffsetY() {
        return 0;
    }

    public double getRulerHeight() {
        return playAreaRulerNode.getFullBounds().getHeight();
    }

    private void updateAccelerationVectorVisible(PlayAreaVector accelerationVector, MovingManModel model) {
        accelerationVector.setVisible(model.getAccelerationVectorVisible().get());
    }

    private void updateVelocityVectorVisibility(MovingManModel model, PlayAreaVector velocityVector) {
        velocityVector.setVisible(model.getVelocityVectorVisible().get());
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
