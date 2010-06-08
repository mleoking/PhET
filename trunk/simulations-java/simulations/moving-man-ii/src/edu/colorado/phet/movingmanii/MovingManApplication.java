package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
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
            setControlPanel(null);
            setSimulationPanel(new MovingManSimulationPanel(model, recordAndPlaybackModel));
            setClockControlPanel(new RecordAndPlaybackControlPanel<MovingManState>(recordAndPlaybackModel, getSimulationPanel(), 20.0));
            setLogoPanelVisible(false);
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
