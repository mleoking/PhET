/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 8:50:15 AM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class ForwardBiasDiode extends BasicStateModel {

    protected ForwardBiasDiode( EnergySection energySection, EnergyCell entryPoint, EnergyCell exitCell, int sign ) {
        super( energySection, entryPoint, exitCell );

        EnergyCell right = energySection.getNeighbor( entryPoint, 0, 1 * sign );
        EnergyCell nextBand = energySection.getNeighbor( entryPoint, 0, 2 * sign );
        EnergyCell fallTo = energySection.getNeighbor( exitCell, 0, -1 * sign );

        PhetVector enterAt = entryPoint.getPosition().getAddedInstance( -.4 * sign, 0 );
        enterTo( enterAt.getX(), getEntryPoint() );
        moveTo( right );
        moveTo( nextBand );
        super.fall();
//        super.exit(exitCell.getX()+.4*sign);
//        fallTo( fallTo );
//        moveTo( exitCell );
//        exitTo( exitCell.getPosition().getAddedInstance( .4 * sign, 0 ) );
    }


}
