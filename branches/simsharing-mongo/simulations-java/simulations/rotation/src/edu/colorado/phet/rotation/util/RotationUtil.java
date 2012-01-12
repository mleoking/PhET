// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.util;

import java.awt.geom.Line2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Sep 12, 2007
 * Time: 8:48:18 PM
 */
public class RotationUtil {

    public static boolean lineEquals( Line2D.Double a, Line2D.Double b ) {
        return a.getP1().equals( b.getP1() ) && a.getP2().equals( b.getP2() );
    }
}
