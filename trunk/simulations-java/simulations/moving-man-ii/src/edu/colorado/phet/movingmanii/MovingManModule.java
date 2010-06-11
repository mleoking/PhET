package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingmanii.charts.ChartCursor;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

import javax.swing.*;
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

    public MovingManModule(String name) {
        super(name, new ConstantDtClock(MovingManModel.CLOCK_DELAY_MS, MovingManModel.DT));

        //Need different behavior when paused, currently the main clock is always running, and the RecordAndPlaybackModel determines whether the sim is running.
        final MovingManModel model = new MovingManModel(new MovingManModel.BooleanGetter() {
            public boolean isTrue() {
                return recordAndPlaybackModel.isPaused();
            }
        });
        //TODO: make MovingManModel extend RecordAndPlaybackModel<MovingManState>?
        recordAndPlaybackModel = new RecordAndPlaybackModel<MovingManState>(1000) {
            public MovingManState stepRecording(double simulationTimeChange) {
                model.simulationTimeChanged(simulationTimeChange);
                return model.getRecordingState();
            }

            public void setPlaybackState(MovingManState state) {
                model.setPlaybackState(state);
            }
        };

        //When unpausing, if user set velocity or acceleration to be nonzero, need to switch to vel or acc-driven
        recordAndPlaybackModel.addObserver(new SimpleObserver() {
            public void update() {
                if (recordAndPlaybackModel.isPaused() != lastPauseState) {
                    if (recordAndPlaybackModel.isPaused()) {//Just paused
                        actionList.clear();
                    } else {
//                        System.out.println("using actionList = " + actionList);
                        if (actionList.contains(MovingMan.POSITION_DRIVEN))
                            MovingMan.POSITION_DRIVEN.apply(model.getMovingMan());
                        if (actionList.contains(MovingMan.VELOCITY_DRIVEN))
                            MovingMan.VELOCITY_DRIVEN.apply(model.getMovingMan());
                        if (actionList.contains(MovingMan.ACCELERATION_DRIVEN))
                            MovingMan.ACCELERATION_DRIVEN.apply(model.getMovingMan());//highest precedence
                    }
                    lastPauseState = recordAndPlaybackModel.isPaused();
                }
            }
        });
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                if (recordAndPlaybackModel.isPaused()) {
                    if (!actionList.contains(model.getMovingMan().getMotionStrategy())) {
                        actionList.add(model.getMovingMan().getMotionStrategy());
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
                model.clear();
            }
        });
        model.getChartCursor().addListener(new ChartCursor.Adapter() {
            public void positionChanged() {
                final double setTime = model.getChartCursor().getTime();
                recordAndPlaybackModel.setTime(setTime);
            }
        });
        recordAndPlaybackModel.addObserver(new SimpleObserver() {
            public void update() {
                updateCursorVisibility(model, recordAndPlaybackModel);
            }
        });
        updateCursorVisibility(model, recordAndPlaybackModel);

        setSimulationPanel(createSimulationPanel(model, recordAndPlaybackModel));
        setClockControlPanel(new RecordAndPlaybackControlPanel<MovingManState>(recordAndPlaybackModel, getSimulationPanel(), 20.0));
        setLogoPanelVisible(false);
    }

    protected abstract JComponent createSimulationPanel(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel);

    private void updateCursorVisibility(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        model.getChartCursor().setVisible(recordAndPlaybackModel.isPlayback() && recordAndPlaybackModel.getNumRecordedPoints() > 0);
    }
}
