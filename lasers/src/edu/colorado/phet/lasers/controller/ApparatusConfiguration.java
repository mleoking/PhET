/**
 * Class: ApparatusConfiguration
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.physics.LaserModel;
import edu.colorado.phet.lasers.physics.atom.HighEnergyState;
import edu.colorado.phet.lasers.physics.atom.MiddleEnergyState;

public class ApparatusConfiguration {

    private float stimulatedPhotonRate;
    private float pumpingPhotonRate;
    private float highEnergySpontaneousEmissionTime;
    private float middleEnergySpontaneousEmissionTime;
    private float simulationRate;
    private float reflectivity;

    public float getStimulatedPhotonRate() {
        return stimulatedPhotonRate;
    }

    public void setStimulatedPhotonRate( float stimulatedPhotonRate ) {
        this.stimulatedPhotonRate = stimulatedPhotonRate;
    }

    public float getPumpingPhotonRate() {
        return pumpingPhotonRate;
    }

    public void setPumpingPhotonRate( float pumpingPhotonRate ) {
        this.pumpingPhotonRate = pumpingPhotonRate;
    }

    public float getHighEnergySpontaneousEmissionTime() {
        return highEnergySpontaneousEmissionTime;
    }

    public void setHighEnergySpontaneousEmissionTime( float highEnergySpontaneousEmissionTime ) {
        this.highEnergySpontaneousEmissionTime = highEnergySpontaneousEmissionTime;
    }

    public float getMiddleEnergySpontaneousEmissionTime() {
        return middleEnergySpontaneousEmissionTime;
    }

    public void setMiddleEnergySpontaneousEmissionTime( float middleEnergySpontaneousEmissionTime ) {
        this.middleEnergySpontaneousEmissionTime = middleEnergySpontaneousEmissionTime;
    }

    public float getSimulationRate() {
        return simulationRate;
    }

    public void setSimulationRate( float simulationRate ) {
        this.simulationRate = simulationRate;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity( float reflectivity ) {
        this.reflectivity = reflectivity;
    }

    public void configureSystem( LaserModel model ) {
        HighEnergyState.setSpontaneousEmmisionHalfLife( getHighEnergySpontaneousEmissionTime() );
        MiddleEnergyState.setSpontaneousEmmisionHalfLife( getMiddleEnergySpontaneousEmissionTime() );
        model.getPumpingBeam().setPhotonsPerSecond( getPumpingPhotonRate() );
        model.getStimulatingBeam().setPhotonsPerSecond( getStimulatedPhotonRate() );
        model.getResonatingCavity().setReflectivity( getReflectivity() );
    }
}
