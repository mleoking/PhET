/**
 * Class: LaserGraphicFactory
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.graphics.PhetGraphic;
import edu.colorado.phet.graphics.ApparatusPanel;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.lasers.physics.mirror.PartialMirror;
import edu.colorado.phet.physics.collision.Wall;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.photon.Photon;

import java.util.Map;

public class LaserGraphicFactory extends GraphicFactory {

    private LaserGraphicFactory() {
        Map classMap = getPhysicalToGraphicalClassMap();
        classMap.put( Atom.class, AtomGraphic.class );
        classMap.put( Photon.class, PhotonGraphic.class );
        classMap.put( PartialMirror.class, MirrorGraphic.class );
        classMap.put( Wall.class, WallGraphic.class );
        classMap.put( ResonatingCavity.class, ResonatingCavityGraphic.class );
    }

    //
    // Static fields and methods
    //
    private static LaserGraphicFactory instance = new LaserGraphicFactory();

    public static GraphicFactory instance() {
        return instance;
    }

}
