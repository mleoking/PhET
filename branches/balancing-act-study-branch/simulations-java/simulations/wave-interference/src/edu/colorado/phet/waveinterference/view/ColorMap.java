// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 2:55:30 PM
 */
public interface ColorMap {
    Color getColor( int i, int k );

    Color getRootColor();
}
