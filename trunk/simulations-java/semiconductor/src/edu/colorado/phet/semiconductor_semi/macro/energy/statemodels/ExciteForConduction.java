/** Sam Reid*/
package edu.colorado.phet.semiconductor_semi.macro.energy.statemodels;

import edu.colorado.phet.common_semiconductor.model.ModelElement;
import edu.colorado.phet.semiconductor_semi.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor_semi.macro.energy.StateTransitionList;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandSet;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor_semi.macro.energy.transitions.Move;

/**
 * User: Sam Reid
 * Date: Apr 20, 2004
 * Time: 12:21:33 PM
 * Copyright (c) Apr 20, 2004 by Sam Reid
 */
public class ExciteForConduction implements ModelElement {
    private StateTransitionList list;
    private EnergySection energySection;
    private EnergyCell leftDst;
    private EnergyCell rightDst;

    public ExciteForConduction( EnergySection energySection, int band, int bandSet, int srcLevel ) {
        this.energySection = energySection;
        BandSet bs = energySection.bandSetAt( bandSet );
        Band b = bs.bandAt( band );

        Move upLeft = getExciteTransition( b, srcLevel, 0 );
        Move upRight = getExciteTransition( b, srcLevel, 1 );

        this.leftDst = upLeft.getDst();
        this.rightDst = upRight.getDst();

        list = new StateTransitionList( energySection );
        list.addTransition( upLeft );
        list.addTransition( upRight );
    }

    private Move getExciteTransition( Band b, int srcLevel, int cellIndex ) {
        EnergyCell srcCell = b.energyLevelAt( srcLevel ).cellAt( cellIndex );
        leftDst = energySection.getUpperNeighbor( srcCell );
        Move excite = new Move( srcCell, leftDst, energySection.getSpeed() );
        return excite;
    }

    public void stepInTime( double dt ) {
        list.stepInTime( dt );
    }

    public EnergyCell getLeftCell() {
        return leftDst;
    }

    public EnergyCell getRightCell() {
        return rightDst;
    }

}
