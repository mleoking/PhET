/*  */
package edu.colorado.phet.semiconductor.macro.energy;

import java.util.ArrayList;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.common.phetcommon.model.ModelElement;


/**
 * User: Sam Reid
 * Date: Mar 17, 2006
 * Time: 11:08:36 AM
 */

public class DiodeIn3Handler implements ModelCriteria, ModelElement {
    private ArrayList tempList;
    private EnergySection energySection;

    public DiodeIn3Handler( EnergySection energySection ) {
        this.energySection = energySection;
    }

    public boolean isApplicable( EnergySection energySection ) {
        if ( energySection.numBandSets() == 3 ) {
            DopantType left = energySection.bandSetAt( 0 ).getDopantType();
            DopantType mid = energySection.bandSetAt( 1 ).getDopantType();
            DopantType right = energySection.bandSetAt( 2 ).getDopantType();
            DopantType N = DopantType.N;
            DopantType P = DopantType.P;
            ArrayList list = new ArrayList();
            list.add( left );
            list.add( mid );
            list.add( right );
            this.tempList = list;
            return listEquals( N, P, P ) || listEquals( N, N, P ) || listEquals( P, N, N ) || listEquals( P, P, N );
        }
        else {
            return false;
        }
    }

    private boolean listEquals( DopantType a, DopantType b, DopantType c ) {
        if ( a == null || b == null || c == null ) {
            return false;
        }
        return tempList.get( 0 ).equals( a ) && tempList.get( 1 ).equals( b ) && tempList.get( 2 ).equals( c );
    }

    public void stepInTime( double dt ) {
        if ( Math.abs( energySection.getVoltage() ) <= 0.1 ) {
            //setup bias

        }
    }
}
