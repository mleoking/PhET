/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:07:02 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class Clear3 extends DefaultStateDiagram {

    public Clear3( EnergySection energySection, ExciteForConduction leftExcite, ExciteForConduction midExcite, ExciteForConduction rightExcite ) {
        super( energySection );
        move( leftExcite.getRightCell(), leftExcite.getLeftCell(), getSpeed() );
        move( lower( leftExcite.getLeftCell() ), lower( leftExcite.getRightCell() ), getSpeed() );
        exitLeft( leftExcite.getLeftCell() );
        unexcite( energySection.getLowerNeighbor( leftExcite.getLeftCell() ) );
        unexcite( energySection.getLowerNeighbor( leftExcite.getRightCell() ) );
        fall( leftExcite.getLeftCell() );
        fall( leftExcite.getRightCell() );

        unexcite( lower( midExcite.getRightCell() ) );
        unexcite( lower( midExcite.getLeftCell() ) );
        fall( midExcite.getLeftCell() );
        fall( midExcite.getRightCell() );
        move( midExcite.getLeftCell(), leftExcite.getRightCell(), getSpeed() );
        move( midExcite.getRightCell(), rightExcite.getLeftCell(), getSpeed() );
        move( lower( rightExcite.getLeftCell() ), lower( midExcite.getRightCell() ), getSpeed() );
        move( lower( leftExcite.getRightCell() ), lower( midExcite.getLeftCell() ), getSpeed() );

        move( rightExcite.getLeftCell(), rightExcite.getLeftCell(), getSpeed() );
        move( lower( rightExcite.getRightCell() ), lower( rightExcite.getLeftCell() ), getSpeed() );
        exitRight( rightExcite.getRightCell() );
        fall( rightExcite.getLeftCell() );
        fall( rightExcite.getRightCell() );
        unexcite( energySection.getLowerNeighbor( rightExcite.getLeftCell() ) );
        unexcite( energySection.getLowerNeighbor( rightExcite.getRightCell() ) );

        enter( energySection.getLowerNeighbor( rightExcite.getRightCell() ) );
        enter( energySection.getLowerNeighbor( leftExcite.getLeftCell() ) );
    }

    public EnergyCell lower( EnergyCell cell ) {
        return getEnergySection().getLowerNeighbor( cell );
    }
}
