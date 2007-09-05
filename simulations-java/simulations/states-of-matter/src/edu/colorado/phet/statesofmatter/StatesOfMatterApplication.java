package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class StatesOfMatterApplication extends PiccoloPhetApplication {
    private static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize(StatesOfMatterConfig.WINDOW_WIDTH, StatesOfMatterConfig.WINDOW_HEIGHT);

    public StatesOfMatterApplication(String[] args) {
        super(null);//new StatesOfMatterConfig(args, FRAME_SETUP));

        addModule(new SolidLiquidGasModule());
    }

    public static void main(final String[] args) {
        new PhetApplicationConfig( new String[0], new FrameSetup.NoOp(), PhetResources.forProject( "states-of-matter" ) ).getVersion();
//
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new StatesOfMatterApplication(args).startApplication();
//            }
//        });
    }
}
