/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 19, 2005
 * Time: 7:43:34 AM
 * Copyright (c) Apr 19, 2005 by Sam Reid
 */

public class MMUtil {
    public static boolean isHighScreenResolution() {
        return Toolkit.getDefaultToolkit().getScreenSize().width >= 1240;
    }
}
