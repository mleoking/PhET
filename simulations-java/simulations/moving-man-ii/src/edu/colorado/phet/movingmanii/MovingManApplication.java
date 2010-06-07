package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

import javax.swing.*;

/**
 * This is the moving man application, rewritten in 2010 to be part of the new motion series suite of sims.
 *
 * @author Sam Reid
 */
public class MovingManApplication extends PhetApplication {

    public static class MyModule extends Module {
        public MyModule() {
            super("test", new ConstantDtClock(30, 1.0));
            setSimulationPanel(new JPanel());
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
