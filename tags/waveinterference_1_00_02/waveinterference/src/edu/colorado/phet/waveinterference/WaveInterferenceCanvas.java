/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:12:40 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaveInterferenceCanvas extends DoubleBufferedPhetPCanvas {

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );//todo is this to avoid performance problems in 1.5?
        super.paintComponent( g );
    }

    public Point2D getWaveModelGraphicOffset() {
        return new Point2D.Double( 200, 30 );//has to keep the slits screen & buttons onscreen.
    }

    public int getLayoutHeight() {
        return super.getHeight();
    }
}
