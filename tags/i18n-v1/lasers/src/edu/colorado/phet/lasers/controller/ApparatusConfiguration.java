/**
 * Class: ApparatusConfiguration
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Apr 1, 2003
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;

public class ApparatusConfiguration {

    private double stimulatedPhotonRate;
    private double pumpingPhotonRate;
    private double highEnergySpontaneousEmissionTime;
    private double middleEnergySpontaneousEmissionTime;
    private double simulationRate;
    private double reflectivity;

    public double getStimulatedPhotonRate() {
        return stimulatedPhotonRate;
    }

    public void setStimulatedPhotonRate( double stimulatedPhotonRate ) {
        this.stimulatedPhotonRate = stimulatedPhotonRate;
    }

    public double getPumpingPhotonRate() {
        return pumpingPhotonRate;
    }

    public void setPumpingPhotonRate( double pumpingPhotonRate ) {
        this.pumpingPhotonRate = pumpingPhotonRate;
    }

    public double getHighEnergySpontaneousEmissionTime() {
        return highEnergySpontaneousEmissionTime;
    }

    public void setHighEnergySpontaneousEmissionTime( double highEnergySpontaneousEmissionTime ) {
        this.highEnergySpontaneousEmissionTime = highEnergySpontaneousEmissionTime;
    }

    public double getMiddleEnergySpontaneousEmissionTime() {
        return middleEnergySpontaneousEmissionTime;
    }

    public void setMiddleEnergySpontaneousEmissionTime( double middleEnergySpontaneousEmissionTime ) {
        this.middleEnergySpontaneousEmissionTime = middleEnergySpontaneousEmissionTime;
    }

    public double getSimulationRate() {
        return simulationRate;
    }

    public void setSimulationRate( double simulationRate ) {
        this.simulationRate = simulationRate;
    }

    public double getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity( double reflectivity ) {
        this.reflectivity = reflectivity;
    }

    public void configureSystem( LaserModel model ) {
        HighEnergyState.instance().setMeanLifetime( getHighEnergySpontaneousEmissionTime() );
        MiddleEnergyState.instance().setMeanLifetime( getMiddleEnergySpontaneousEmissionTime() );
        model.getPumpingBeam().setPhotonsPerSecond( getPumpingPhotonRate() );
        model.getStimulatingBeam().setPhotonsPerSecond( getStimulatedPhotonRate() );
        model.getResonatingCavity().setReflectivity( getReflectivity() );
    }
}
