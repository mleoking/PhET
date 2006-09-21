package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 2:55:30 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */
public interface ColorMap {
    Color getColor( int i, int k );

    Color getRootColor();
}
