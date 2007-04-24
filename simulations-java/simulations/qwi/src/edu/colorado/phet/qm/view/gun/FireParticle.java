package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.QWIModule;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Feb 6, 2006
 * Time: 10:35:53 PM
 *
 */
public interface FireParticle {
    QWIModule getSchrodingerModule();

    void updateGunLocation();

    void clearAndFire();

    PImage getGunImageGraphic();
}
