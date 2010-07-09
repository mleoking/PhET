package edu.colorado.phet.movingman;

import edu.colorado.phet.common.motion.charts.ChartCursor;
import edu.colorado.phet.common.motion.charts.MutableBoolean;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.movingman.model.*;
import edu.colorado.phet.movingman.view.MovingManSimulationPanel;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public abstract class MovingManModule extends Module {
    protected RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel;

    /*
    If the user sets acceleration and velocity then presses go, should switch to acceleration mode so the acceleration is not overwritten (and same for velocity)
    Try this rule: on pause, clear list.  When user enters a value, add to list.  When unpausing, choose highest modified property to be "driving".  This seems safer.
     */
    private ArrayList<MovingMan.MotionStrategy> actionList = new ArrayList<MovingMan.MotionStrategy>();
    private boolean lastPauseState;
    protected final MovingManModel movingManModel = new MovingManModel(new MovingManModel.BooleanGetter() {
        public boolean isTrue() {
            return recordAndPlaybackModel.isPaused();
        }
    });
    private MutableBoolean positiveToTheRight = new MutableBoolean(true);//True if positive meters is to the right in the play area view
    final ExpressionDialog expressionDialog;//one expression dialog per module
    final MovingManSimulationPanel simulationPanel;
    
    public MovingManModule(PhetFrame frame, String name) {
        super(name, new ConstantDtClock(MovingManModel.CLOCK_DELAY_MS, MovingManModel.DT));
        CrashSound.init();

        movingManModel.addCollisionListener(new JListener() {
            public void eventOccurred() {
                CrashSound.play();
            }
        });

        //Need different behavior when paused, currently the main clock is always running, and the RecordAndPlaybackModel determines whether the sim is running.
        //TODO: make MovingManModel extend RecordAndPlaybackModel<MovingManState>?
        recordAndPlaybackModel = new RecordAndPlaybackModel<MovingManState>(1000) {
            public MovingManState step(double simulationTimeChange) {
                movingManModel.simulationTimeChanged(simulationTimeChange);
                return movingManModel.getRecordingState();
            }

            public void setPlaybackState(MovingManState state) {
                movingManModel.setPlaybackState(state);
            }
        };
        //When the user presses "record" in the middle of a playback sequence, we clear the remaining data and keep going from the playback point.
        recordAndPlaybackModel.addHistoryRemainderClearListener(new RecordAndPlaybackModel.HistoryRemainderClearListener() {
            public void historyRemainderCleared() {
                movingManModel.historyRemainderCleared(recordAndPlaybackModel.getTime());
            }
        });

        //When unpausing, if user set velocity or acceleration to be nonzero, need to switch to vel or acc-driven
        //so that values are not overwritten to zero in the first time step (by differentiating a constant)
        recordAndPlaybackModel.addObserver(new SimpleObserver() {
            public void update() {
                if (recordAndPlaybackModel.isPaused() != lastPauseState) {
                    if (recordAndPlaybackModel.isPaused()) {//Just paused
                        actionList.clear();
                    } else {
                        if (actionList.contains(MovingMan.POSITION_DRIVEN))
                            MovingMan.POSITION_DRIVEN.apply(movingManModel.getMovingMan());
                        if (actionList.contains(MovingMan.VELOCITY_DRIVEN))
                            MovingMan.VELOCITY_DRIVEN.apply(movingManModel.getMovingMan());
                        if (actionList.contains(MovingMan.ACCELERATION_DRIVEN))
                            MovingMan.ACCELERATION_DRIVEN.apply(movingManModel.getMovingMan());//highest precedence
                    }
                    lastPauseState = recordAndPlaybackModel.isPaused();
                }
            }
        });
        movingManModel.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                if (recordAndPlaybackModel.isPaused()) {
                    if (!actionList.contains(movingManModel.getMovingMan().getMotionStrategy())) {
                        actionList.add(movingManModel.getMovingMan().getMotionStrategy());
//                        System.out.println("updated actionList = " + actionList);
                    }
                }
            }
        });
        lastPauseState = recordAndPlaybackModel.isPaused();

        //Wire up the clock to the record and playback model.
        getClock().addClockListener(new ClockAdapter() {
            public void simulationTimeChanged(ClockEvent clockEvent) {
                recordAndPlaybackModel.stepInTime(clockEvent.getSimulationTimeChange());
            }
        });
        recordAndPlaybackModel.addHistoryClearListener(new RecordAndPlaybackModel.HistoryClearListener() {
            public void historyCleared() {
                movingManModel.clear();
            }
        });
        movingManModel.getChartCursor().addListener(new ChartCursor.Adapter() {
            public void positionChanged() {
                final double setTime = movingManModel.getChartCursor().getTime();
                recordAndPlaybackModel.setTime(setTime);
            }
        });
        recordAndPlaybackModel.addObserver(new SimpleObserver() {
            public void update() {
                updateCursorVisibility(movingManModel, recordAndPlaybackModel);
            }
        });
        updateCursorVisibility(movingManModel, recordAndPlaybackModel);

        simulationPanel = createSimulationPanel(movingManModel, recordAndPlaybackModel);
        setSimulationPanel(simulationPanel);
        setClockControlPanel(createRecordAndPlaybackPanel());
        setLogoPanelVisible(false);

        expressionDialog = new ExpressionDialog(frame, this);
    }

    protected RecordAndPlaybackControlPanel<MovingManState> createRecordAndPlaybackPanel() {
        RecordAndPlaybackControlPanel<MovingManState> recordAndPlaybackControlPanel = new RecordAndPlaybackControlPanel<MovingManState>(recordAndPlaybackModel, getSimulationPanel(), 20.0);
        recordAndPlaybackControlPanel.addControl(new PSwing(new ResetAllButton(new Resettable() {
            public void reset() {
                MovingManModule.this.resetAll();
            }
        }, getSimulationPanel())));
        return recordAndPlaybackControlPanel;
    }

    protected abstract MovingManSimulationPanel createSimulationPanel(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel);

    private void updateCursorVisibility(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        model.getChartCursor().setVisible(recordAndPlaybackModel.isPlayback() && recordAndPlaybackModel.getNumRecordedPoints() > 0);
    }

    public void resetAll() {
        movingManModel.resetAll();
        recordAndPlaybackModel.resetAll();
        positiveToTheRight.setValue(true);
        simulationPanel.resetAll();
    }

    public void setPositiveToTheRight(boolean value) {
        positiveToTheRight.setValue(value);
    }

    public MutableBoolean getPositiveToTheRight() {
        return positiveToTheRight;
    }

    public void setEvaluateExpressionDialogVisible(boolean visible) {
        expressionDialog.setVisible(visible);
    }

    boolean expressionDialogVisibleOnDeactivate = false;

    @Override
    public void deactivate() {
        super.deactivate();
        this.expressionDialogVisibleOnDeactivate = expressionDialog.isVisible();
        expressionDialog.setVisible(false);
    }

    @Override
    public void activate() {
        super.activate();
        setEvaluateExpressionDialogVisible(expressionDialogVisibleOnDeactivate);
    }

    public MovingManModel getMovingManModel() {
        return movingManModel;
    }

    public void setPaused(boolean b) {
        recordAndPlaybackModel.setPaused(b);
    }

    public boolean getEvaluateExpressionDialogVisible() {
        return expressionDialog.isVisible();
    }

    public void setExpression(ExpressionEvaluator expressionEvaluator) {
        movingManModel.setExpression(expressionEvaluator);
    }
}