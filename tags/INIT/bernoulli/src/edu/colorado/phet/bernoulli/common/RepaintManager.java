package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.coreadditions.clock2.AbstractClock;
import edu.colorado.phet.coreadditions.clock2.TickListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 1:19:13 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class RepaintManager implements TickListener, SimpleObserver {
    boolean timeToRepaint = false;
    Component target;

    public RepaintManager( Component target ) {
        this.target = target;
    }

    public void update() {
        timeToRepaint = true;
    }

    public void clockTicked( AbstractClock source ) {
        if( timeToRepaint ) {
            target.repaint();
        }
        timeToRepaint = false;
    }

}
