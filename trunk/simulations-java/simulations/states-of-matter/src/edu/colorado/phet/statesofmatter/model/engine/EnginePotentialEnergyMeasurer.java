package edu.colorado.phet.statesofmatter.model.engine;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.statesofmatter.model.engine.gravity.GravityPotentialMeasurer;
import edu.colorado.phet.statesofmatter.model.engine.lj.LJPotentialEnergyMeasurer;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesPotential;

public class EnginePotentialEnergyMeasurer extends CompositeMeasurable {
    public EnginePotentialEnergyMeasurer(List particles, double floor, double g, LennardJonesPotential potential) {
        super(new ArrayList());

        getMeasurables().add(new GravityPotentialMeasurer(particles, floor, g));
        getMeasurables().add(new LJPotentialEnergyMeasurer(particles, potential));
    }
}
