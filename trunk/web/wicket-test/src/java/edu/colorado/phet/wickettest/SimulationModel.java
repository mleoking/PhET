package edu.colorado.phet.wickettest;

import org.apache.wicket.model.IModel;

import edu.colorado.phet.tomcattest.WebSimulation;

public class SimulationModel implements IModel {
    private WebSimulation simulation;

    public SimulationModel( WebSimulation simulation ) {
        this.simulation = simulation;
    }

    public Object getObject() {
        return simulation;
    }

    public void setObject( Object o ) {
        simulation = (WebSimulation) o;
    }

    public void detach() {

    }
}
