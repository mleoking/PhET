// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Model of a light beam as a collection of photons.
 * Photons propagate through space over time.
 * Changes to the lights wavelength do not instantaneously affect all photons in the beam.
 * So if the wavelength changes over time, this beam will potentially consist of photons
 * with different wavelengths.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonBeam {

    private static final int PHOTONS_EMITTED_PER_CLOCK_TICK = 10;
    private static final double PHOTON_VELOCITY = 0.75; // cm/sec
    private static final double PHOTON_DIAMETER = 0.075; // cm

    private final Light light;
    private final ArrayList<Photon> photons;
    private final ArrayList<PhotonBeamChangeListener> listeners;

    public interface PhotonBeamChangeListener {

        // A photon has been added to the beam.
        public void photonAdded( Photon photon );

        // A photon has been removed from the beam.
        public void photonRemoved( Photon photon );
    }

    public PhotonBeam( final Light light ) {
        this.light = light;
        this.photons = new ArrayList<Photon>();
        this.listeners = new ArrayList<PhotonBeamChangeListener>();
    }

    private void addPhoton( Photon photon ) {
        photons.add( photon );
        firePhotonAdded( photon );
    }

    private void removePhoton( Photon photon ) {
        photons.remove( photon );
        firePhotonRemoved( photon );
    }

    public void removeAllPhotons() {
        for ( Photon photon : new ArrayList<Photon>( photons ) ) {
            removePhoton( photon );
        }
    }

    // Photon creation and animation, called when the simulation clock ticks.
    public void stepInTime( double deltaSeconds ) {

        // propagate existing photons
        final double dx = PHOTON_VELOCITY * deltaSeconds;
        for ( Photon photon : new ArrayList<Photon>( photons ) ) {
            final ImmutableVector2D oldLocation = photon.location.get();
            final ImmutableVector2D newLocation = new ImmutableVector2D( oldLocation.getX() + dx, oldLocation.getY() );
            if ( newLocation.getX() > 10 ) { //TODO temporary, just to get basic functionality working
                removePhoton( photon );
            }
            else {
                photon.location.set( newLocation );
            }
        }

        // create new photons
        if ( light.on.get() ) {
            final int numberOfNewPhotons = PHOTONS_EMITTED_PER_CLOCK_TICK;
            for ( int i = 0; i < numberOfNewPhotons; i++ ) {
                addPhoton( new Photon( getRandomLocation(), light.wavelength.get(), PHOTON_DIAMETER ) );
            }
        }
    }

    public void addPhotonBeamChangeListener( PhotonBeamChangeListener listener ) {
        listeners.add( listener );
    }

    // Notifies listeners when a photon is added.
    private void firePhotonAdded( Photon photon ) {
        for ( PhotonBeamChangeListener listener : new ArrayList<PhotonBeamChangeListener>( listeners ) ) {
            listener.photonAdded( photon );
        }
    }

    // Notifies listeners when a photon is removed.
    private void firePhotonRemoved( Photon photon ) {
        for ( PhotonBeamChangeListener listener : new ArrayList<PhotonBeamChangeListener>( listeners ) ) {
            listener.photonRemoved( photon );
        }
    }

    // Gets a random starting location for the photon, in cm.
    private ImmutableVector2D getRandomLocation() {
        final double x = light.location.getX();
        final double y = ( light.location.getY() - ( light.lensDiameter / 2 ) + ( PHOTON_DIAMETER / 2 ) ) + ( Math.random() * ( light.lensDiameter - PHOTON_DIAMETER ) );
        return new ImmutableVector2D( x, y );
    }
}
