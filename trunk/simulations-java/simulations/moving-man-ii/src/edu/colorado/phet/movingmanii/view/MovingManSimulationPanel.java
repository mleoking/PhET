package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.movingmanii.MovingManColorScheme;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanel extends PhetPCanvas {
    private MovingManModel model;
    private double earthOffset;
    private final Range viewRange;
    private final PlayAreaRulerNode playAreaRulerNode;

    public MovingManSimulationPanel(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, int earthOffset) {
        this.model = model;
        this.earthOffset = earthOffset;
        addScreenChild(new SkyNode());
        addScreenChild(new EarthNode());
        viewRange = new Range(0, 1000);
        playAreaRulerNode = new PlayAreaRulerNode(new Range(-10, 10), viewRange);
        playAreaRulerNode.setOffset(0, earthOffset);
        addScreenChild(playAreaRulerNode);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateViewRange();
            }
        });
        updateViewRange();

        final MovingManNode manNode = new MovingManNode(model.getMovingMan(), model, viewRange);
        manNode.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                recordAndPlaybackModel.startRecording();
            }
        });
        addScreenChild(manNode);

        int arrowTailWidth = 7;
        //Add Velocity vector to play area
        final PlayAreaVector velocityVector = new PlayAreaVector(MovingManColorScheme.VELOCITY_COLOR, arrowTailWidth);
        addScreenChild(velocityVector);
        final double arrowDY = arrowTailWidth / 2 + 2;
        final double arrowY = 100 - arrowDY * 2;//don't let the arrows overlap the ruler
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double velocityScale = 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getVelocity() * velocityScale);
                velocityVector.setArrow(startX, arrowY - arrowDY, endX, arrowY - arrowDY);
            }
        });
        model.getVelocityVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateVelocityVectorVisibility(model, velocityVector);
            }
        });
        updateVelocityVectorVisibility(model, velocityVector);

        //Add Acceleration vector to play area
        final PlayAreaVector accelerationVector = new PlayAreaVector(MovingManColorScheme.ACCELERATION_COLOR, arrowTailWidth);
        addScreenChild(accelerationVector);
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double accelerationScale = 0.2 * 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getAcceleration() * accelerationScale);
                accelerationVector.setArrow(startX, arrowY + arrowDY, endX, arrowY + arrowDY);
            }
        });
        model.getAccelerationVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateAccelerationVectorVisible(accelerationVector, model);
            }
        });
        updateAccelerationVectorVisible(accelerationVector, model);
    }

    private void updateViewRange() {
        final int inset = 100;
        viewRange.setMin(inset);
        viewRange.setMax(getWidth() - inset);
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
            PhetPPath skyNode = new PhetPPath(new Rectangle2D.Double(-100, 0, 10000, 10000), new Color(174, 217, 255));
            addChild(skyNode);
        }
    }

    protected PlayAreaRulerNode getPlayAreaRulerNode() {
        return playAreaRulerNode;
    }
}
