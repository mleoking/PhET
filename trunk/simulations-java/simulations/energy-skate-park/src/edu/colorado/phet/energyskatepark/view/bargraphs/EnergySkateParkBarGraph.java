/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:16:45 PM
 */

public class EnergySkateParkBarGraph extends BarGraph {
    private EnergySkateParkModel model;

    public EnergySkateParkBarGraph( EnergySkateParkSimulationPanel canvas, final EnergySkateParkModel energySkateParkModel, double scale ) {
        this( canvas, energySkateParkModel, EnergySkateParkStrings.getString( "properties.energy" ), scale );
        final ValueAccessor[] energyAccess = new ValueAccessor[]{
                new ValueAccessor.KineticEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.PotentialEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.ThermalEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.TotalEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() )
        };
        Variable[] v = toVariableArray( energyAccess, energySkateParkModel );
        setVariables( v );
    }

    public EnergySkateParkBarGraph( EnergySkateParkSimulationPanel energySkateParkSimulationPanel,
                                    EnergySkateParkModel energySkateParkModel, String title, double scale ) {
        super( title, scale );
        this.model = energySkateParkModel;

        energySkateParkSimulationPanel.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void stepFinished() {
                update();
            }

            public void bodyEnergyChanged() {//todo: this will be called too many times if there are many bodies changing energy during one time step
                update();
            }
        } );
    }

    public static Variable[] toVariableArray( final ValueAccessor[] energyAccess, final EnergySkateParkModel energySkateParkModel ) {
        Variable[] v = new Variable[energyAccess.length];
        for( int i = 0; i < v.length; i++ ) {
            final int i1 = i;
            v[i] = new Variable( energyAccess[i1].getName(), energyAccess[i1].getValue( energySkateParkModel ), energyAccess[i1].getColor() ) {
                public double getValue() {
                    return energyAccess[i1].getValue( energySkateParkModel );
                }
            };
        }
        return v;
    }

}
