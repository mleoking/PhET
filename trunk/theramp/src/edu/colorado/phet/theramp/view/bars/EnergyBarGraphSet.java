/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:25 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class EnergyBarGraphSet extends BarGraphSet {
    public EnergyBarGraphSet( RampPanel rampPanel, RampModel rampModel ) {
        super( rampPanel, rampModel, "Energy" );
        ValueAccessor[] energyAccess = new ValueAccessor[]{
            new ValueAccessor.KineticEnergy( super.getLookAndFeel() ), new ValueAccessor.PotentialEnergy( getLookAndFeel() ),
            new ValueAccessor.ThermalEnergy( getLookAndFeel() ), new ValueAccessor.TotalEnergy( getLookAndFeel() )
        };
        setAccessors( energyAccess );
//        for( int i = 0; i < energyAccess.length; i++ ) {
//            final ValueAccessor accessor = energyAccess[i];
//            final BarGraphic2D barGraphic = new BarGraphic2D( getComponent(), accessor.getName(), transform1D,
////                                                              accessor.getValue( rampModel ), sep * ( i + energyAccess.length + 1 ) + dw, width, y, dx, dy, toEnergyPaint( accessor.getColor() ) );
//                                                              accessor.getValue( rampModel ), sep * ( i + energyAccess.length + 1 ) + dw, width, y, dx, dy, accessor.getColor() );
//            addClockTickListener( new ClockTickListener() {
//                public void clockTicked( ClockTickEvent event ) {
//                    barGraphic.setValue( accessor.getValue( rampModel ) );
//                }
//            } );
//            addGraphic( barGraphic );
//        }


    }
}
