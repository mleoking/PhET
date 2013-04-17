// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Feb 6, 2006
 * Time: 10:35:53 PM
 */
public interface FireParticle {
    QWIModule getSchrodingerModule();

    void updateGunLocation();

    void clearAndFire();

    PImage getGunImageGraphic();
}
