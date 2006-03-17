/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:07:02 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class Clear2 extends DefaultStateDiagram {

    public Clear2( EnergySection energySection, ExciteForConduction leftExcite, ExciteForConduction rightExcite ) {
        super( energySection );
        move( leftExcite.getRightCell(), leftExcite.getLeftCell(), getSpeed() );
        exitLeft( leftExcite.getLeftCell() );
        unexcite( energySection.getLowerNeighbor( leftExcite.getLeftCell() ) );
        unexcite( energySection.getLowerNeighbor( leftExcite.getRightCell() ) );
        fall( leftExcite.getLeftCell() );
        fall( leftExcite.getRightCell() );

        move( rightExcite.getLeftCell(), rightExcite.getLeftCell(), getSpeed() );
        exitRight( rightExcite.getRightCell() );
        fall( rightExcite.getLeftCell() );
        fall( rightExcite.getRightCell() );
        unexcite( energySection.getLowerNeighbor( rightExcite.getLeftCell() ) );
        unexcite( energySection.getLowerNeighbor( rightExcite.getRightCell() ) );

        enter( energySection.getLowerNeighbor( rightExcite.getRightCell() ) );
        enter( energySection.getLowerNeighbor( leftExcite.getLeftCell() ) );
    }

}
