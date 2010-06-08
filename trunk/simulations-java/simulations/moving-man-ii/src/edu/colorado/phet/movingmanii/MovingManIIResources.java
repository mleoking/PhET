package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sam Reid
 */
public class MovingManIIResources {

    private static PhetResources INSTANCE = new PhetResources("moving-man");

    /* not intended for instantiation */

    private MovingManIIResources() {
    }

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    public static BufferedImage loadBufferedImage(String url) throws IOException {
        return INSTANCE.getImage(url);
    }

    public static String getString(String s) {
        return INSTANCE.getLocalizedString(s);
    }

}