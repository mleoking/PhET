/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 10:38:02 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class InitEmergence00 extends Page {
    public InitEmergence00( BasicTutorialCanvas page ) {
        super( page );
        setText( "Welcome to Unit II: Emergent properties of the Self Driven Particle Model." );

    }

    public void init() {
        super.init();
        artificialAdvance();
        showNextButton();

    }

    public void relayoutChildren() {
        super.relayoutChildren();
    }
}
