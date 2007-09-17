package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetSimLauncher;
import edu.colorado.phet.common.piccolophet.PhetApplication;

public class StatesOfMatterApplication extends PhetApplication implements IProguardKeepClass {
    private static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize(StatesOfMatterConfig.WINDOW_WIDTH, StatesOfMatterConfig.WINDOW_HEIGHT);

    public StatesOfMatterApplication(String[] args) {
        super(new StatesOfMatterConfig(args, FRAME_SETUP));

        addModule(new SolidLiquidGasModule());
    }

    public static void main(final String[] args) {
        PhetSimLauncher.launchSim(args, StatesOfMatterApplication.class);
    }
}
