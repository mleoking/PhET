/*  */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:25 PM
 */

public class EnergyBarGraphSet extends BarGraphSet {
    public EnergyBarGraphSet( RampPanel rampPanel, RampPhysicalModel rampPhysicalModel, ModelViewTransform1D transform1D ) {
        super( rampPanel, rampPhysicalModel, TheRampStrings.getString( "energy.energy" ), transform1D );
        ValueAccessor[] energyAccess = new ValueAccessor[]{
                new ValueAccessor.KineticEnergy( super.getLookAndFeel() ), new ValueAccessor.PotentialEnergy( getLookAndFeel() ),
                new ValueAccessor.ThermalEnergy( getLookAndFeel() ), new ValueAccessor.TotalEnergy( getLookAndFeel() )
        };
        finishInit( energyAccess );
    }
}
