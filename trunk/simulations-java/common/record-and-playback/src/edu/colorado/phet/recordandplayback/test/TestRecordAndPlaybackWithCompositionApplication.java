package edu.colorado.phet.recordandplayback.test;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.util.Observable;

/**
 * This application shows how to use the record-and-playback feature by containing instead of extending the RecordAndPlaybackModel.
 *
 * @author Sam Reid
 */
public class TestRecordAndPlaybackWithCompositionApplication extends PhetApplication {

    public TestRecordAndPlaybackWithCompositionApplication(PhetApplicationConfig config) {
        super(config);
        addModule(new TestRecordAndPlaybackModule());
    }

    private class TestRecordAndPlaybackModule extends Module {
        private TestRecordAndPlaybackModel model = new TestRecordAndPlaybackModel();

        public TestRecordAndPlaybackModule() {
            super("test record and playback", new SwingClock(30, 1.0));
            TestRecordAndPlaybackSimulationPanel simPanel = new TestRecordAndPlaybackSimulationPanel(model);
            setSimulationPanel(simPanel);

            //it doesn't matter how you wire up the model to update with each clock tick, here is one way
            getClock().addClockListener(new ClockAdapter() {
                public void simulationTimeChanged(ClockEvent clockEvent) {
                    model.stepInTime(clockEvent.getSimulationTimeChange());
                }
            });

            //use the record and playback control panel
            setClockControlPanel(new RecordAndPlaybackControlPanel<TestState>(model.getRecordAndPlaybackModel(), simPanel, 1000));
        }
    }

    /**
     * The state that gets recorded, which should be immutable.  In this sample application, we just record the location of the particle.
     */
    public static class TestState {
        private double x;
        private double y;

        public TestState(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    private class TestRecordAndPlaybackSimulationPanel extends PhetPCanvas {
        private TestRecordAndPlaybackSimulationPanel(final TestRecordAndPlaybackModel model) {
            ParticleNode particleNode = new ParticleNode(model.getParticle());

            //when the user starts dragging the object, start recording
            //note that this cannot be an attachment to the model's normal movement listener, since that is updated during playback
            //alternatively, you could add a new listener interface to the model object such as Particle.userDragged
            //to ensure communication happens through model notifications
            particleNode.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseDragged(PInputEvent event) {
                    model.startRecording();
                }
            });
            addScreenChild(particleNode);
        }
    }

    /**
     * Model class which contains a RecordAndPlaybackModel as a component
     */
    private class TestRecordAndPlaybackModel extends Observable {
        private Particle particle = new Particle();
        private RecordAndPlaybackModel<TestState> recordAndPlaybackModel = new RecordAndPlaybackModel<TestState>(1000) {
            public TestState stepRecording(double simulationTimeChange) {
                return new TestState(particle.getX(), particle.getY());
            }

            public void setPlaybackState(TestState state) {
                particle.setPosition(state.getX(), state.getY());
            }
        };

        protected TestRecordAndPlaybackModel() {
        }

        public Particle getParticle() {
            return particle;
        }

        public TestState stepRecording(double simulationTimeChange) {
            return recordAndPlaybackModel.stepRecording(simulationTimeChange);
        }

        public void stepInTime(double simulationTimeChange) {
            recordAndPlaybackModel.stepInTime(simulationTimeChange);
        }

        public RecordAndPlaybackModel<TestState> getRecordAndPlaybackModel() {
            return recordAndPlaybackModel;
        }

        public void startRecording() {
            recordAndPlaybackModel.startRecording();
        }
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "record-and-playback", TestRecordAndPlaybackWithCompositionApplication.class);
    }
}