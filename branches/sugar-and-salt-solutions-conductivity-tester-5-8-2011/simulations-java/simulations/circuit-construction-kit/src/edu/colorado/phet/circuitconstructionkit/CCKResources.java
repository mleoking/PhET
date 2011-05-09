// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.image.BufferedImage;

/**
 * Author: Sam Reid
 * May 18, 2007, 11:33:31 PM
 */
public class CCKResources {
    private static final PhetResources INSTANCE = new PhetResources("circuit-construction-kit");

    public static BufferedImage getImage(String s) {
        return INSTANCE.getImage(s);
    }

    public static String getString(String key) {
        return INSTANCE.getLocalizedString(key);
    }

}
