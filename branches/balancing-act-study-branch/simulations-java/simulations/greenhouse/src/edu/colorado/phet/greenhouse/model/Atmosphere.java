// Copyright 2002-2011, University of Colorado

/**
 * Class: Atmosphere
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.filter.BandpassFilter;

public class Atmosphere extends Annulus /*extends BasicPhotonAbsorber */ implements PhotonEmitter, PhotonAbsorber {

    public static final double troposphereThickness = 16;
    public static final double stratosphereThickness = 30;
    public static final double thickness = troposphereThickness + stratosphereThickness;
    private Annulus troposphere;
    private double greenhouseGasConcentration = GreenhouseConfig.defaultGreenhouseGasConcentration;
    ArrayList listeners = new ArrayList();

    private BandpassFilter greenhouseGasFilter = new BandpassFilter( WavelengthConstants.IR_WAVELENGTH,
                                                                     WavelengthConstants.IR_WAVELENGTH );

    public Atmosphere( Earth earth ) {
        super( earth.getLocation(), Earth.radius * 2, ( Earth.radius + troposphereThickness + stratosphereThickness ) * 2 );
        troposphere = new Annulus( earth.getLocation(), Earth.radius * 2,
                                   ( Earth.radius + troposphereThickness ) * 2 );
    }

    public Annulus getTroposphere() {
        return troposphere;
    }

    public double getGreenhouseGasConcentration() {
        return greenhouseGasConcentration;
    }

    public void setGreenhouseGasConcentration( double greenhouseGasConcentration ) {
        this.greenhouseGasConcentration = greenhouseGasConcentration;
    }

    public void interactWithPhoton( Photon photon ) {

        // Is the photon of a wavelength that is affected by greenhouse gasses?
        if ( greenhouseGasFilter.passes( photon.getWavelength() ) ) {

            // The likelihood of being scattered is dependent on the altitude
            double altitude = troposphere.distanceFromInnerDiameter( photon.getLocation() );
            double probability = ( troposphereThickness - altitude ) * greenhouseGasConcentration;
            boolean event = Math.random() <= probability;
            if ( event ) {
                // Scatter the photon in a random direction
                double dispersionAngle = Math.PI / 4;
                double theta = Math.random() * dispersionAngle + ( Math.PI * 3 / 2 ) - ( dispersionAngle / 2 );
                theta += Math.random() < 0.5 ? 0 : Math.PI;
//                double theta = Math.random() * Math.PI * 2;
                double vBar = photon.getVelocity().magnitude();

                photon.setVelocity( vBar * (float) Math.cos( theta ),
                                    vBar * (float) Math.sin( theta ) );

                for ( int i = 0; i < listeners.size(); i++ ) {
                    ScatterEventListener scatterEventListener = (ScatterEventListener) listeners.get( i );
                    scatterEventListener.photonScatered( photon );
                }

//                absorbPhoton( photon );
//                photonToEmit = new Photon( photon.getWavelength(), this );
//                photonToEmit.setLocation( photon.getLocation().getX(),
//                                          photon.getLocation().getY() );
//                photon.setVelocity( vBar * (float)Math.cos( theta ),
//                                    vBar * (float)Math.sin( theta ) );
//                emitPhoton();
            }
        }
    }

    public void addScatterEventListener( ScatterEventListener sel ) {
        listeners.add( sel );
    }

    private HashSet photonEmitterListeners = new HashSet();
    private HashSet photonAbsorberListeners = new HashSet();

    public void addListener( PhotonEmitter.Listener listener ) {
        photonEmitterListeners.add( listener );
    }

    public void removeListener( PhotonEmitter.Listener listener ) {
        photonEmitterListeners.remove( listener );
    }

    public double getProductionRate() {
        return 0;
    }

    public void setProductionRate( double productionRate ) {
    }

    private Photon photonToEmit;

    public Photon emitPhoton() {
        for ( Iterator iterator = photonEmitterListeners.iterator(); iterator.hasNext(); ) {
            PhotonEmitter.Listener listener = (PhotonEmitter.Listener) iterator.next();
            listener.photonEmitted( photonToEmit );
        }
        return photonToEmit;
    }

    public void addListener( PhotonAbsorber.Listener listener ) {
        photonAbsorberListeners.add( listener );
    }

    public void removeListener( PhotonAbsorber.Listener listener ) {
        photonAbsorberListeners.remove( listener );
    }

    public void absorbPhoton( Photon photon ) {
        for ( Iterator iterator = photonAbsorberListeners.iterator(); iterator.hasNext(); ) {
            PhotonAbsorber.Listener listener = (PhotonAbsorber.Listener) iterator.next();
            listener.photonAbsorbed( photon );
        }
    }

    //
    // inner classes
    //
    public interface ScatterEventListener {
        void photonScatered( Photon photon );
    }


}
