package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingmanii.charts.ChartCursor;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.movingmanii.view.MovingManSimulationPanel;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

/**
 * This is the moving man application, redesigned + rewritten in 2010 to be part of the new motion series suite of sims.
 *
 * @author Sam Reid
 */
public class MovingManApplication extends PhetApplication {

    public static class MyModule extends Module {
        public MyModule() {
            super("test", new ConstantDtClock(30, 1.0 / 30.0));
            final MovingManModel model = new MovingManModel();

            //TODO: make MovingManModel extend RecordAndPlaybackModel<MovingManState>? 
            final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel = new RecordAndPlaybackModel<MovingManState>(1000) {
                public MovingManState stepRecording(double simulationTimeChange) {
                    model.simulationTimeChanged(simulationTimeChange);
                    return model.getRecordingState();
                }

                public void setPlaybackState(MovingManState state) {
                    model.setPlaybackState(state);
                }
            };
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

            setSimulationPanel(new MovingManSimulationPanel(model, recordAndPlaybackModel));
            setClockControlPanel(new RecordAndPlaybackControlPanel<MovingManState>(recordAndPlaybackModel, getSimulationPanel(), 20.0));
            setLogoPanelVisible(false);
        }

        private void updateCursorVisibility(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
            model.getChartCursor().setVisible(recordAndPlaybackModel.isPlayback() && recordAndPlaybackModel.getNumRecordedPoints() > 0);
        }
    }

    public MovingManApplication(PhetApplicationConfig config) {
        super(config);
        Module module = new MyModule();
        addModule(module);
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "moving-man-ii", MovingManApplication.class);
    }
}
