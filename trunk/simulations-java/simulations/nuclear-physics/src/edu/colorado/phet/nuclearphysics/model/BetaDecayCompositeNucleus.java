/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.Antineutrino;
import edu.colorado.phet.nuclearphysics.common.model.Electron;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon.NucleonType;

/**
 * Base class for composite nuclei (i.e. nuclei that are made up of multiple
 * individual protons and neutrons) that exhibit beta decay.
 * 
 * @author John Blanco
 */
public abstract class BetaDecayCompositeNucleus extends CompositeAtomicNucleus {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Random RAND = new Random();
	private static final double ANTINEUTRINO_EMISSION_SPEED = 1.2;  // Femtometers per clock tick.  Weird, I know.
	private static final double ELECTRON_EMISSION_SPEED = 0.7;  // Femtometers per clock tick.  Weird, I know.

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public BetaDecayCompositeNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons,
			double decayTimeScalingFactor) {
		super(clock, position, numProtons, numNeutrons, decayTimeScalingFactor);
	}

	/**
	 * Resets the nucleus to its original state, before any beta decay has
	 * occurred.
	 */
	public void reset() {
	    
		super.reset();
	    
		if (_numNeutrons != _origNumNeutrons || _numProtons != _origNumProtons){
			
			// Reset numerical configuration.
			_numNeutrons = _origNumNeutrons;
			_numProtons = _origNumProtons;
			
			// Change one of the protons into a neutron.  NOTE: We go backwards
			// through the list because that tends to yield a neutron which is less
			// likely to be obscured by other nucleons, just due to the way that
			// construction works.
			for (int i = _constituents.size() - 1; i >= 0; i--){
				SubatomicParticle nucleon = _constituents.get(i);
				if (nucleon instanceof Nucleon && ((Nucleon)nucleon).getNucleonType() == NucleonType.PROTON){
					((Nucleon)nucleon).setNucleonType(NucleonType.NEUTRON);
					break;
				}
			}
			
			// Update our agitation level.
			updateAgitationFactor();
			
			// Let the listeners know that the nucleus has changed.
			notifyNucleusChangeEvent(null);
		}
	}

	/**
	 * Take the actions that simulate beta decay.
	 */
	@Override
	protected void decay(ClockEvent clockEvent) {

		super.decay(clockEvent);

		// Update the numerical nucleus configuration.
		_numNeutrons -= 1;
		_numProtons += 1;
		
		// Change one of the neutrons into a proton.  NOTE: We go backwards
		// through the list because that tends to yield a proton which is less
		// likely to be obscured by other nucleons, just due to the way that
		// construction works.
		for (int i = _constituents.size() - 1; i >= 0; i--){
			SubatomicParticle nucleon = _constituents.get(i);
			if (nucleon instanceof Nucleon && ((Nucleon)nucleon).getNucleonType() == NucleonType.NEUTRON){
				((Nucleon)nucleon).setNucleonType(NucleonType.PROTON);
				break;
			}
		}

		// Create the emitted particles, which are an electron and an
		// antineutrino.
		double angle = RAND.nextDouble() * Math.PI * 2;
		double xVel = Math.cos(angle) * ELECTRON_EMISSION_SPEED; 
		double yVel = Math.sin(angle) * ELECTRON_EMISSION_SPEED; 
		ArrayList<SubatomicParticle> byProducts = new ArrayList<SubatomicParticle>();
		Electron electron = new Electron(getPositionReference().getX(), getPositionReference().getY());
		electron.setVelocity(xVel, yVel);
		byProducts.add(electron);
		angle = RAND.nextDouble() * Math.PI * 2;
		xVel = Math.cos(angle) * ANTINEUTRINO_EMISSION_SPEED; 
		yVel = Math.sin(angle) * ANTINEUTRINO_EMISSION_SPEED; 
		Antineutrino antineutrino = new Antineutrino(getPositionReference().getX(), getPositionReference().getY());
		antineutrino.setVelocity(xVel, yVel);
		byProducts.add(antineutrino);
		
		// Update our agitation factor.
		updateAgitationFactor();

		// Send out the decay event to all listeners.
		notifyNucleusChangeEvent(byProducts);
	}
}