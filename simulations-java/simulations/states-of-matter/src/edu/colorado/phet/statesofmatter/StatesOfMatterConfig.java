package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import java.awt.geom.Rectangle2D;

public class StatesOfMatterConfig extends PhetApplicationConfig {
    private static final String PROJECT_NAME = "states-of-matter";

    public static final PhetResources RESOURCES = new PhetResources(PROJECT_NAME);
    public static final int WINDOW_WIDTH  = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int INITIAL_MAX_PARTICLE_COUNT = 600;
    public static final Rectangle2D.Double CONTAINER_SIZE   = new Rectangle2D.Double(0, 0, 10, 8);
    public static final Rectangle2D.Double CONTAINER_BOUNDS = new Rectangle2D.Double(-5, -4, CONTAINER_SIZE.getWidth(), CONTAINER_SIZE.getHeight());
    public static final Rectangle2D.Double ICE_CUBE_BOUNDS  = new Rectangle2D.Double(-1.5, 1, 3, 3);
    public static final double INITIAL_TOTAL_ENERGY_PER_PARTICLE = 225;
    public static final double PARTICLE_RADIUS = 0.2;
    public static final double PARTICLE_MASS   = 1.0;
    public static final double PARTICLE_MAX_KE = 100000;
    public static final double PARTICLE_CREATION_CUSHION = 0.0;

    public static final int COMPUTATIONS_PER_RENDER = 10;

    public static final double GRAVITY = -10;
    public static final double DELTA_T = 0.0000001;

    public static final double EPSILON = 10000000.0;
    public static final double RMIN    = 2.0 * PARTICLE_RADIUS;
    public static final double ICE_CUBE_DIST_FROM_FLOOR = RMIN;
    public static final String COFFEE_CUP_IMAGE = "coffee-cup-image.svg";


    public StatesOfMatterConfig(String[] commandLineArgs, FrameSetup frameSetup) {
        super(commandLineArgs, frameSetup, RESOURCES);
    }
}
