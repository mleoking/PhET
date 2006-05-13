package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Feb 6, 2006
 * Time: 10:35:53 PM
 * Copyright (c) Feb 6, 2006 by Sam Reid
 */
public interface FireParticle {
    SchrodingerModule getSchrodingerModule();

    void updateGunLocation();

    void clearAndFire();

    PImage getGunImageGraphic();
}
