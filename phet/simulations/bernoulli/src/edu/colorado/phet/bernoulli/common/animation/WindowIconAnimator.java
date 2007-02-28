package edu.colorado.phet.bernoulli.common.animation;

import edu.colorado.phet.coreadditions.clock2.AbstractClock;
import edu.colorado.phet.coreadditions.clock2.TickListener;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 20, 2003
 * Time: 10:45:53 PM
 * Copyright (c) Aug 20, 2003 by Sam Reid
 */
public class WindowIconAnimator implements TickListener {
    JFrame frame;
    Image[] icons;
    private int numTicks;
    int tickCount = 0;
    int image = 0;

    public WindowIconAnimator( JFrame frame, Image[] icons, int numTicks ) {
        this.frame = frame;
        this.icons = icons;
        this.numTicks = numTicks;
        frame.setIconImage( icons[0] );
    }

    public void clockTicked( AbstractClock source ) {
        tickCount++;
        if( tickCount >= numTicks ) {
            tickCount = 0;
            nextImage();
        }
    }

    private void nextImage() {
        image = ( image + 1 ) % icons.length;
        frame.setIconImage( icons[image] );
    }

}
