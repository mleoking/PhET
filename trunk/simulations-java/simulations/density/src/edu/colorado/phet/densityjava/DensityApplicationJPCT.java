package edu.colorado.phet.densityjava;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.view.d2.DensityCanvas2D;
import edu.colorado.phet.densityjava.view.jpct.DensityCanvasJPCT;

import java.io.IOException;

public class DensityApplicationJPCT extends PiccoloPhetApplication {

    public DensityApplicationJPCT(PhetApplicationConfig config) {
        super(config);
        addModule(new DensityModule());
    }

    class DensityModule extends Module {
        private final DensityModel model = new DensityModel();
        private final DensityCanvasJPCT panel = new DensityCanvasJPCT(model);

        public DensityModule() {
            super("density", new ConstantDtClock(30, 30 / 1000.0));

            setSimulationPanel(panel);
            getClock().addClockListener(new ClockAdapter(){
                public void simulationTimeChanged(ClockEvent clockEvent) {
                    model.stepInTime(clockEvent.getSimulationTimeChange());
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        //add natives to path
        new PhetApplicationLauncher().launchSim(args, "density", "density-2d", DensityApplicationJPCT.class);
    }

}