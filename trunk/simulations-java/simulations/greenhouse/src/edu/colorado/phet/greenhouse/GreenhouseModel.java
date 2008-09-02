/**
 * Class: GreenhouseModel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.greenhouse.common_greenhouse.model.BaseModel;
import edu.colorado.phet.greenhouse.common_greenhouse.model.IClock;
import edu.colorado.phet.greenhouse.common_greenhouse.model.ModelElement;

public class GreenhouseModel extends BaseModel implements PhotonEmitter.Listener, PhotonAbsorber.Listener {
    private Star sun;
    private Earth earth;
    private Atmosphere atmosphere;
    private ArrayList photons = new ArrayList();
    private ArrayList clouds = new ArrayList();
    private ArrayList glassPanes = new ArrayList();
    private Rectangle2D.Double bounds;

    public GreenhouseModel( Rectangle2D.Double bounds ) {
        this.bounds = bounds;
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public ArrayList getPhotons() {
        return photons;
    }

    public Earth getEarth() {
        return earth;
    }


    public void clockTicked( IClock iClock, double dt ) {

        super.clockTicked( iClock, dt );

        // Check for interactions between photons and other elements
        // in the model
        for ( int i = photons.size() - 1; i >= 0; i-- ) {
            Photon photon = (Photon) photons.get( i );
            PhotonEarthCollisionModel.handle( photon, earth );

            for ( int j = 0; j < clouds.size(); j++ ) {
                Cloud cloud = (Cloud) clouds.get( j );
                PhotonCloudCollisionModel.handle( photon, cloud );
            }

            for ( int j = 0; j < glassPanes.size(); j++ ) {
                GlassPane glassPane = (GlassPane) glassPanes.get( j );
                PhotonGlassPaneCollisionModel.handle( photon, glassPane );
            }
            atmosphere.interactWithPhoton( photon );
        }
    }

    public void addModelElement( ModelElement aps ) {
        super.addModelElement( aps );
    }

//    ArrayList pl = new ArrayList( );

    public void removeModelElement( ModelElement m ) {
//        pl.add( m );

        super.removeModelElement( m );
    }

    public void setSun( Star sun ) {
        super.addModelElement( sun );
        sun.addListener( this );
        this.sun = sun;
    }

    public void setEarth( Earth earth ) {
        super.addModelElement( earth );
        earth.addPhotonAbsorberListener( this );
        earth.addPhotonEmitterListener( this );
        this.earth = earth;
    }

    public void setAtmosphere( Atmosphere atmosphere ) {
        super.addModelElement( atmosphere );
        this.atmosphere = atmosphere;
    }

    private void addPhoton( Photon photon ) {
        super.addModelElement( photon );
        this.photons.add( photon );
    }

    public void photonEmitted( Photon photon ) {
        addPhoton( photon );
    }

    public void photonAbsorbed( Photon photon ) {
        super.removeModelElement( photon );
        this.photons.remove( photon );
    }

    public void setSunPhotonProductionRate( double rate ) {
        this.sun.setProductionRate( rate );
    }

    public void setEarthEmissivity( double emissivity ) {
        this.earth.setEmissivity( emissivity );
    }

    public void addCloud( Cloud cloud ) {
        clouds.add( cloud );
    }

    public void removeCloud( Cloud cloud ) {
        clouds.remove( cloud );
    }

    public void addGlassPane( GlassPane glassPane ) {
        glassPanes.add( glassPane );
    }

    public void removeGlassPane( GlassPane glassPane ) {
        glassPanes.remove( glassPane );
    }

    public void setGreenhouseGasConcentration( double concentration ) {
        atmosphere.setGreenhouseGasConcentration( concentration );
    }

    public void removePhoton( Photon photon ) {
        photons.remove( photon );
    }
}
