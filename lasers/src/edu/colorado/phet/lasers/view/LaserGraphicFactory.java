/**
 * Class: LaserGraphicFactory
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.collision.Wall;

import java.util.Map;

public class LaserGraphicFactory extends GraphicFactory {
    private boolean highToMidEmissionsVisible;

    private LaserGraphicFactory() {
        Map classMap = getPhysicalToGraphicalClassMap();
        classMap.put( Atom.class, AtomGraphic.class );
        classMap.put( Photon.class, PhotonGraphic.class );
        classMap.put( PartialMirror.class, MirrorGraphic.class );
        classMap.put( Wall.class, WallGraphic.class );
        classMap.put( ResonatingCavity.class, ResonatingCavityGraphic.class );
    }

    public PhetGraphic createGraphic( Particle particle, ApparatusPanel apparatusPanel ) {

        // Here is where the switch is recognized that prevents photons emitted by
        // atoms moving from the high to mid energy states from being displayed
        if( particle instanceof Photon &&
            ((Photon)particle).getWavelength() == Photon.DEEP_RED  &&
            !this.highToMidEmissionsVisible) {
            return null;
        }
        return super.createGraphic( particle, apparatusPanel );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setHighToMidEmissionsVisible( boolean isVisible ){
        this.highToMidEmissionsVisible = isVisible;
    }

    //
    // Static fields and methods
    //
    private static LaserGraphicFactory instance = new LaserGraphicFactory();

    public static LaserGraphicFactory instance() {
        return instance;
    }

}
