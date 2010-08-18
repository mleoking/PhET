package edu.colorado.phet.membranediffusion.model;

/**
 * A class for reporting the closest particle to the origin in a capture
 * zone and the total number of particles in the zone.
 */
public class CaptureZoneScanResult {
	final Particle closestFreeParticle;
	final int numParticlesInZone;
	public CaptureZoneScanResult(Particle closestParticle,
			int numParticlesInZone) {
		super();
		this.closestFreeParticle = closestParticle;
		this.numParticlesInZone = numParticlesInZone;
	}
	protected Particle getClosestFreeParticle() {
		return closestFreeParticle;
	}
	protected int getNumParticlesInZone() {
		return numParticlesInZone;
	}
}