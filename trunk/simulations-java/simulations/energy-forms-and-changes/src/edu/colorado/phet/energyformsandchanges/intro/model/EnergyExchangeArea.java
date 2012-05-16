// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Class that represents a 2D space that contains energy.  Contact between
 * two such areas that have different amounts of energy will result in a
 * transfer of energy proportional to the amount of contact.
 *
 * @author John Blanco
 */
public class EnergyExchangeArea implements EnergyContainer {

    private final Property<Double> energy;
    private final Rectangle2D bounds = new Rectangle2D.Double();

    public EnergyExchangeArea( Rectangle2D bounds, double initialEnergy ) {
        this.bounds.setFrame( bounds );
        energy = new Property<Double>( initialEnergy );
    }

    public void changeEnergy( double deltaEnergy ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getEnergy() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void exchangeEnergyWith( EnergyContainer energyContainer ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
