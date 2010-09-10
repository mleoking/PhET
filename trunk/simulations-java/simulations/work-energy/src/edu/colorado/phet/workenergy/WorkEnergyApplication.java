package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class WorkEnergyApplication extends PiccoloPhetApplication {
    public WorkEnergyApplication(PhetApplicationConfig config) {
        super(config);

        final IntroModule introModule = new IntroModule(getPhetFrame());
        addModule(introModule);
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "work-energy", WorkEnergyApplication.class);
    }
}
