/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:12:40 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaveInterferenceCanvas extends DoubleBufferedPhetPCanvas {
    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        super.paintComponent( g );
    }
}
