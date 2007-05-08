/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:25 PM
 */

public class EnergyBarGraphSet extends BarGraphSet {
    public EnergyBarGraphSet( EnergySkateParkSimulationPanel canvas, EnergySkateParkModel energySkateParkModel, ModelViewTransform1D transform1D ) {
        super( canvas, energySkateParkModel, EnergySkateParkStrings.getString( "energy" ), transform1D );
        ValueAccessor[] energyAccess = new ValueAccessor[]{
                new ValueAccessor.KineticEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.PotentialEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.ThermalEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.TotalEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() )
        };
        finishInit( energyAccess );
    }
}
