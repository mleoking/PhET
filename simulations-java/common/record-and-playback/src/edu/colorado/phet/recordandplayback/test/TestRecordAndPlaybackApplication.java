package edu.colorado.phet.recordandplayback.test;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.DataPoint;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * This application shows the minimal setup required to configure and use the recordandplayback project in a PhetApplication.
 *
 * @author Sam Reid
 */
public class TestRecordAndPlaybackApplication extends PhetApplication {

    public TestRecordAndPlaybackApplication(PhetApplicationConfig config) {
        super(config);
        addModule(new TestRecordAndPlaybackModule());
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "record-and-playback", TestRecordAndPlaybackApplication.class);
    }

    private class TestRecordAndPlaybackModule extends Module {
        private TestRecordAndPlaybackModel model = new TestRecordAndPlaybackModel();

        public TestRecordAndPlaybackModule() {
            super("test record and playback", new SwingClock(30, 1.0));
            TestRecordAndPlaybackSimulationPanel simPanel = new TestRecordAndPlaybackSimulationPanel(model);
            setSimulationPanel(simPanel);
            getClock().addClockListener(new ClockAdapter() {
                public void simulationTimeChanged(ClockEvent clockEvent) {
                    model.stepInTime();
                }
            });

            setClockControlPanel(new RecordAndPlaybackControlPanel<TestState>(model, simPanel, 100));
        }
    }

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

    private static class ParticleNode extends PNode {
        private ParticleNode(final Particle particle, final TestRecordAndPlaybackModel model) {
            final PhetPPath path = new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.blue, new BasicStroke(10), Color.black);
            addChild(path);
            path.setOffset(particle.getX(), particle.getY());
            particle.addListener(new Particle.Listener() {
                public void moved() {
                    path.setOffset(particle.getX(), particle.getY());
                }
            });
            addInputEventListener(new CursorHandler());
            addInputEventListener(new PBasicInputEventHandler() {
                @Override
                public void mouseDragged(PInputEvent event) {
                    super.mouseDragged(event);    //To change body of overridden methods use File | Settings | File Templates.
                    particle.translate(event.getCanvasDelta().width, event.getCanvasDelta().getHeight());

                    //when the user starts dragging the object, start recording
                    model.setRecord(true);
                    model.setPaused(false);
                }
            });
        }
    }

    private class TestRecordAndPlaybackSimulationPanel extends PhetPCanvas {
        private TestRecordAndPlaybackSimulationPanel(TestRecordAndPlaybackModel model) {
            addScreenChild(new ParticleNode(model.getParticle(), model));
        }
    }

    private class TestRecordAndPlaybackModel extends RecordAndPlaybackModel<TestState> {
        private Particle particle = new Particle();

        private TestRecordAndPlaybackModel() {
        }

        @Override
        public void stepRecord() {
            //update state, apply physics, whatever
            //no-op in this sample

            setTime(getTime() + 1);
            addRecordedPoint(new DataPoint<TestState>(getTime(), new TestState(particle.getX(), particle.getY())));
            notifyObservers();
        }

        @Override
        public void setPlaybackState(TestState state) {
            particle.setPosition(state.getX(), state.getY());
        }

        @Override
        public int getMaxRecordPoints() {
            return 1000;
        }

        @Override
        public void handleRecordStartedDuringPlayback() {
        }

        public Particle getParticle() {
            return particle;
        }

        public void stepInTime() {
            if (!isPaused()) {
                if (isPlayback()) stepPlayback();
                else stepRecord();
            }
        }
    }

}
