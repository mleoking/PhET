package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import java.awt.geom.Rectangle2D;

public class StatesOfMatterConfig extends PhetApplicationConfig {
    private static final String PROJECT_NAME = "states-of-matter";

    public static final PhetResources RESOURCES = PhetResources.forProject(PROJECT_NAME);
    public static final int WINDOW_WIDTH  = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int INITIAL_PARTICLE_COUNT = 100;
    public static final Rectangle2D.Double CONTAINER_BOUNDS = new Rectangle2D.Double(-3, -4, 6, 8);

    public StatesOfMatterConfig(String[] commandLineArgs, FrameSetup frameSetup) {
        super(commandLineArgs, frameSetup, RESOURCES);
    }
}
