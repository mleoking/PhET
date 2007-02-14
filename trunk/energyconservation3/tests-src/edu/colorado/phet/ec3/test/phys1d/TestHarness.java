package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.view.SplineGraphic;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 13, 2007
 * Time: 11:04:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestHarness extends PhetApplication {

    public TestHarness() throws HeadlessException {
        super(new String[0], "test", "", "", new FrameSetup.TopCenter(800, 600));
        EnergySkateParkModule module = new EnergySkateParkModule("module", new SwingClock(30, 1), this.getPhetFrame());
        CubicSpline cubicSpline = new CubicSpline();
        cubicSpline.addControlPoint(0, 0);
        cubicSpline.addControlPoint(10, 10);
        SplineSurface splineSurface = new SplineSurface(cubicSpline);
        SplineGraphic splineGraphic = new SplineGraphic(module.getEnergyConservationCanvas(), splineSurface);
        module.getEnergyConservationCanvas().addSplineGraphic(splineGraphic);
        getPhetFrame().setContentPane(module.getEnergyConservationCanvas());
    }

    public static void main(String[] args) {
        new TestHarness().startTest();
    }

    private void startTest() {
        getPhetFrame().setVisible(true);

    }

//    private void start() {
//        To change body of created methods use File | Settings | File Templates.
//        setVisible(true);
//    }
}
