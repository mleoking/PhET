/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import java.util.HashMap;
import java.awt.*;

/**
 * NucleusLabelColors
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NucleusLabelColors {
    private static HashMap classToColor = new HashMap( );
    static {
        classToColor.put ( Uranium235Graphic.class, new Color(144, 255, 0));
        classToColor.put ( Uranium238Graphic.class, Color.yellow);
        classToColor.put ( Uranium239Graphic.class, Color.white);
        classToColor.put ( Lead207Graphic.class, Color.black);
        classToColor.put ( Polonium211Graphic.class, Color.magenta);
    }

    public static Color getColor( Class nucleusGraphicClass ) {
        return (Color)classToColor.get( nucleusGraphicClass );
    }
}
