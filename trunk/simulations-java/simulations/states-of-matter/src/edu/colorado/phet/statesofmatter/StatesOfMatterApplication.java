package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import javax.swing.*;

public class StatesOfMatterApplication extends PiccoloPhetApplication {
    private static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize(StatesOfMatterConfig.WINDOW_WIDTH, StatesOfMatterConfig.WINDOW_HEIGHT);

    public StatesOfMatterApplication(String[] args) {
        super(new StatesOfMatterConfig(args, FRAME_SETUP));

        addModule(new SolidLiquidGasModule());
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StatesOfMatterApplication(args).startApplication();
            }
        });
    }
}
