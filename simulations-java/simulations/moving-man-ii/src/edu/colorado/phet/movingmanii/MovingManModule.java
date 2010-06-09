package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingmanii.charts.ChartCursor;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

import javax.swing.*;

/**
 * @author Sam Reid
 */
public abstract class MovingManModule extends Module {
    protected RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel;

    public MovingManModule(String name) {
        super(name, new ConstantDtClock(30, 1.0 / 30.0));

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

        //TODO: When unpausing, if user set velocity or acceleration to be nonzero, need to switch to vel or acc-driven
        recordAndPlaybackModel.addObserver(new SimpleObserver() {
            public void update() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
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
                //TODO: this is buggy
//                    recordAndPlaybackModel.setPlaybackTime(model.getChartCursor().getTime());
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
