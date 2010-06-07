package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * This is the moving man application, redesigned + rewritten in 2010 to be part of the new motion series suite of sims.
 *
 * @author Sam Reid
 */
public class MovingManApplication extends PhetApplication {

    public static class MyModule extends Module {
        public MyModule() {
            super("test", new ConstantDtClock(30, 1.0 / 30.0));
            MovingManModel man = new MovingManModel(getClock());
            setSimulationPanel(new MovingManSimulationPanel(man));
        }
    }

    public static class MovingManNode extends PNode {
        private Function.LinearFunction modelToView = new Function.LinearFunction(-10, 10, 0, 600);
        private MovingMan man;

        public MovingManNode(final MovingMan man) {
            this.man = man;
            final PhetPPath movingMan = new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.blue, new BasicStroke(1), Color.yellow);
            addChild(movingMan);
            man.addListener(new MovingMan.Listener() {
                public void changed() {
                    updateManPosition(movingMan, man);
                }
            });
            updateManPosition(movingMan, man);

            addInputEventListener(new CursorHandler());
            addInputEventListener(new MovingManDragger(this));
        }

        private void updateManPosition(PhetPPath movingMan, MovingMan man) {
            movingMan.setOffset(modelToView.evaluate(man.getPosition()), 0);
        }

        public MovingMan getMan() {
            return man;
        }
    }

    private static class MovingManSimulationPanel extends PhetPCanvas {
        private MovingManSimulationPanel(final MovingManModel model) {
            int chartHeight = 150;
            int xMax = 10;
            int chartWidth = 800;
            final MovingManChart positionChart = new MovingManChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2), chartWidth, chartHeight);
            positionChart.setOffset(0, 100);
            positionChart.addDataSeries(model.getPositionSeries());
            addScreenChild(positionChart);

            double vMax = 60;

            final MovingManChart velocityChart = new MovingManChart(new Rectangle2D.Double(0, -vMax, 20, vMax * 2), chartWidth, chartHeight);
            velocityChart.setOffset(0, 100 + chartHeight);
            velocityChart.addDataSeries(model.getVelocitySeries());
            addScreenChild(velocityChart);

            double aMax = 200;
            final MovingManChart accelerationChart = new MovingManChart(new Rectangle2D.Double(0, -aMax, 20, aMax * 2), chartWidth, chartHeight);
            accelerationChart.setOffset(0, 100 + chartHeight * 2);
            accelerationChart.addDataSeries(model.getAccelerationSeries());
            addScreenChild(accelerationChart);

            addScreenChild(new MovingManNode(model.getMovingMan()));
        }
    }

    private static class MovingManDragger extends PBasicInputEventHandler {
        private MovingManNode man;

        private MovingManDragger(MovingManNode man) {
            this.man = man;
        }

        @Override
        public void mouseDragged(PInputEvent event) {
            super.mouseDragged(event);
            double canvasDelta = event.getCanvasDelta().width;
            double mappedDelta1 = man.modelToView.createInverse().evaluate(canvasDelta);
            double mappedDelta0 = man.modelToView.createInverse().evaluate(0);
            double mappedDelta = mappedDelta1 - mappedDelta0;
//            System.out.println("canvas delta = " + canvasDelta + ", mapped delta = " + mappedDelta);
            man.getMan().setPosition(man.getMan().getPosition() + mappedDelta);
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
