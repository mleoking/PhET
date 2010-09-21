package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import java.util.ArrayList;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

/**
 * User: Sam Reid
 * Date: Apr 20, 2004
 * Time: 7:20:46 AM
 */
public class DefaultCriteria implements ModelCriteria {
    ArrayList dopantTypes;
    double minVolts;
    double maxVolts;

    public DefaultCriteria( DopantType type, double minVolts, double maxVolts ) {
        this( toArray( type ), minVolts, maxVolts );
    }

    public static ArrayList toArray( DopantType type ) {
        ArrayList list = new ArrayList();
        list.add( type );
        return list;
    }

    public static ArrayList toArray( DopantType type, DopantType t2 ) {
        ArrayList list = new ArrayList();
        list.add( type );
        list.add( t2 );
        return list;
    }

    public DefaultCriteria( ArrayList dopantTypes, double minVolts, double maxVolts ) {
        this.dopantTypes = dopantTypes;
        this.minVolts = minVolts;
        this.maxVolts = maxVolts;
    }

    public DefaultCriteria( DopantType p, DopantType p1, double minVolts, double maxVolts ) {
        this( toArray( p, p1 ), minVolts, maxVolts );
    }

    public DefaultCriteria( DopantType n, DopantType n1, DopantType n2, double minVolts, double maxVolts ) {
        this( toArray( n, n1, n2 ), minVolts, maxVolts );
    }

    private static ArrayList toArray( DopantType n, DopantType n1, DopantType n2 ) {
        ArrayList list = new ArrayList();
        list.add( n );
        list.add( n1 );
        list.add( n2 );
        return list;
    }

    public int numDopantTypes() {
        return dopantTypes.size();
    }

    public boolean isApplicable( EnergySection energySection ) {
        int num = energySection.numBandSets();
        if ( num != numDopantTypes() ) {
            return false;
        }
        else {
            for ( int i = 0; i < dopantTypes.size(); i++ ) {
                DopantType dopantType = (DopantType) dopantTypes.get( i );
                if ( dopantType != energySection.bandSetAt( i ).getDopantType() ) {
                    return false;
                }
            }
        }
        double v = energySection.getVoltage();
        if ( v < minVolts || v > maxVolts ) {
            return false;
        }
        return true;
    }
}
